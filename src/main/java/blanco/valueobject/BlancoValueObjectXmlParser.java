/*
 * blanco Framework
 * Copyright (C) 2004-2008 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.valueobject;

import blanco.cg.BlancoCgSupportedLang;
import blanco.commons.util.BlancoNameUtil;
import blanco.commons.util.BlancoStringUtil;
import blanco.valueobject.message.BlancoValueObjectMessage;
import blanco.valueobject.valueobject.BlancoValueObjectClassStructure;
import blanco.valueobject.valueobject.BlancoValueObjectFieldStructure;
import blanco.xml.bind.BlancoXmlBindingUtil;
import blanco.xml.bind.BlancoXmlUnmarshaller;
import blanco.xml.bind.valueobject.BlancoXmlAttribute;
import blanco.xml.bind.valueobject.BlancoXmlDocument;
import blanco.xml.bind.valueobject.BlancoXmlElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * blancoValueObjectの 中間XMLファイル形式をパース(読み書き)するクラス。
 *
 * @author IGA Tosiki
 */
public class BlancoValueObjectXmlParser {
    /**
     * メッセージ。
     */
    private final BlancoValueObjectMessage fMsg = new BlancoValueObjectMessage();

    private boolean fVerbose = false;
    public void setVerbose(boolean argVerbose) {
        this.fVerbose = argVerbose;
    }
    public boolean isVerbose() {
        return fVerbose;
    }

    /**
     * 中間XMLファイルのXMLドキュメントをパースして、バリューオブジェクト情報の配列を取得します。
     *
     * @param argMetaXmlSourceFile
     *            中間XMLファイル。
     * @return パースの結果得られたバリューオブジェクト情報の配列。
     */
    public BlancoValueObjectClassStructure[] parse(
            final File argMetaXmlSourceFile) {
        final BlancoXmlDocument documentMeta = new BlancoXmlUnmarshaller()
                .unmarshal(argMetaXmlSourceFile);
        if (documentMeta == null) {
            return null;
        }

        return parse(documentMeta);

    }

    /**
     * 中間XMLファイル形式のXMLドキュメントをパースして、バリューオブジェクト情報の配列を取得します。
     *
     * @param argXmlDocument
     *            中間XMLファイルのXMLドキュメント。
     * @return パースの結果得られたバリューオブジェクト情報の配列。
     */
    public BlancoValueObjectClassStructure[] parse(
            final BlancoXmlDocument argXmlDocument) {
        final List<BlancoValueObjectClassStructure> listStructure = new ArrayList<BlancoValueObjectClassStructure>();

        // ルートエレメントを取得します。
        final BlancoXmlElement elementRoot = BlancoXmlBindingUtil
                .getDocumentElement(argXmlDocument);
        if (elementRoot == null) {
            // ルートエレメントが無い場合には処理中断します。
            return null;
        }

        // sheet(Excelシート)のリストを取得します。
        final List<BlancoXmlElement> listSheet = BlancoXmlBindingUtil
                .getElementsByTagName(elementRoot, "sheet");

        final int sizeListSheet = listSheet.size();
        for (int index = 0; index < sizeListSheet; index++) {
            final BlancoXmlElement elementSheet = listSheet.get(index);

            /*
             * Java以外の言語用に記述されたシートにも対応．
             */
            List<BlancoXmlElement> listCommon = null;
            int sheetLang = BlancoCgSupportedLang.JAVA;
            for (String common : BlancoValueObjectUtil.mapCommons.keySet()) {
                listCommon = BlancoXmlBindingUtil
                        .getElementsByTagName(elementSheet,
                                common);
                if (listCommon.size() != 0) {
                    BlancoXmlAttribute attr = new BlancoXmlAttribute();
                    attr.setType("CDATA");
                    attr.setQName("style");
                    attr.setLocalName("style");

                    sheetLang = BlancoValueObjectUtil.mapCommons.get(common);
                    attr.setValue(new BlancoCgSupportedLang().convertToString(sheetLang));

                    elementSheet.getAtts().add(attr);

                    /* tueda DEBUG */
                    if (this.isVerbose()) {
                        System.out.println("/* tueda */ style = " + BlancoXmlBindingUtil.getAttribute(elementSheet, "style"));
                    }

                    break;
                }
            }

            if (listCommon == null || listCommon.size() == 0) {
                // commonが無い場合にはスキップします。
                continue;
            }

            // 最初のアイテムのみ処理しています。
            final BlancoXmlElement elementCommon = listCommon.get(0);
            final String name = BlancoXmlBindingUtil.getTextContent(
                    elementCommon, "name");
            if (BlancoStringUtil.null2Blank(name).trim().length() == 0) {
                continue;
            }

            BlancoValueObjectClassStructure objClassStructure = null;
            switch (sheetLang) {
                case BlancoCgSupportedLang.JAVA:
                    objClassStructure = parseElementSheet(elementSheet);
                    break;
                case BlancoCgSupportedLang.PHP:
                    objClassStructure = parseElementSheetPhp(elementSheet);
                    /* NOT YET SUPPORT ANOTHER LANGUAGES */
            }

            if (objClassStructure != null) {
                // 得られた情報を記憶します。
                listStructure.add(objClassStructure);
            }
        }

        final BlancoValueObjectClassStructure[] result = new BlancoValueObjectClassStructure[listStructure
                .size()];
        listStructure.toArray(result);
        return result;
    }

    /**
     * 中間XMLファイル形式の「sheet」XMLエレメントをパースして、バリューオブジェクト情報を取得します。
     *
     * @param argElementSheet
     *            中間XMLファイルの「sheet」XMLエレメント。
     * @return パースの結果得られたバリューオブジェクト情報。「name」が見つからなかった場合には nullを戻します。
     */
    public BlancoValueObjectClassStructure parseElementSheet(
            final BlancoXmlElement argElementSheet) {
        final BlancoValueObjectClassStructure objClassStructure = new BlancoValueObjectClassStructure();
        final List<BlancoXmlElement> listCommon = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobject-common");
        if (listCommon == null || listCommon.size() == 0) {
            // commonが無い場合にはスキップします。
            return null;
        }
        final BlancoXmlElement elementCommon = listCommon.get(0);
        objClassStructure.setName(BlancoXmlBindingUtil.getTextContent(
                elementCommon, "name"));
        objClassStructure.setPackage(BlancoXmlBindingUtil.getTextContent(
                elementCommon, "package"));

        objClassStructure.setDescription(BlancoXmlBindingUtil.getTextContent(
                elementCommon, "description"));
        if (BlancoStringUtil.null2Blank(objClassStructure.getDescription())
                .length() > 0) {
            final String[] lines = BlancoNameUtil.splitString(objClassStructure
                    .getDescription(), '\n');
            for (int index = 0; index < lines.length; index++) {
                if (index == 0) {
                    objClassStructure.setDescription(lines[index]);
                } else {
                    // 複数行の description については、これを分割して格納します。
                    // ２行目からは、適切に文字参照エンコーディングが実施されているものと仮定します。
                    objClassStructure.getDescriptionList().add(lines[index]);
                }
            }
        }

        objClassStructure.setAccess(BlancoXmlBindingUtil.getTextContent(
                elementCommon, "access"));
        objClassStructure.setAbstract("true".equals(BlancoXmlBindingUtil
                .getTextContent(elementCommon, "abstract")));
        objClassStructure.setGenerateToString("true"
                .equals(BlancoXmlBindingUtil.getTextContent(elementCommon,
                        "generateToString")));
        objClassStructure.setAdjustFieldName("true".equals(BlancoXmlBindingUtil
                .getTextContent(elementCommon, "adjustFieldName")));
        objClassStructure.setAdjustDefaultValue("true"
                .equals(BlancoXmlBindingUtil.getTextContent(elementCommon,
                        "adjustDefaultValue")));
        objClassStructure
                .setFieldList(new ArrayList<blanco.valueobject.valueobject.BlancoValueObjectFieldStructure>());

        if (BlancoStringUtil.null2Blank(objClassStructure.getName()).trim()
                .length() == 0) {
            // 名前が無いものはスキップします。
            return null;
        }

        if (objClassStructure.getPackage() == null) {
            throw new IllegalArgumentException(fMsg
                    .getMbvoji01(objClassStructure.getName()));
        }

        final List<BlancoXmlElement> extendsList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobject-extends");
        if (extendsList != null && extendsList.size() != 0) {
            final BlancoXmlElement elementExtendsRoot = extendsList.get(0);
            objClassStructure.setExtends(BlancoXmlBindingUtil.getTextContent(
                    elementExtendsRoot, "name"));
        }

        final List<BlancoXmlElement> interfaceList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobject-implements");
        if (interfaceList != null && interfaceList.size() != 0) {
            final BlancoXmlElement elementInterfaceRoot = interfaceList.get(0);
            final List<BlancoXmlElement> listInterfaceChildNodes = BlancoXmlBindingUtil
                    .getElementsByTagName(elementInterfaceRoot, "interface");
            for (int index = 0; index < listInterfaceChildNodes.size(); index++) {
                final BlancoXmlElement elementList = listInterfaceChildNodes
                        .get(index);

                final String interfaceName = BlancoXmlBindingUtil
                        .getTextContent(elementList, "name");
                if (interfaceName == null || interfaceName.trim().length() == 0) {
                    continue;
                }
                objClassStructure.getImplementsList().add(
                        BlancoXmlBindingUtil
                                .getTextContent(elementList, "name"));
            }
        }

        final List<BlancoXmlElement> listList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, "blancovalueobject-list");
        if (listList != null && listList.size() != 0) {
            final BlancoXmlElement elementListRoot = listList.get(0);
            final List<BlancoXmlElement> listChildNodes = BlancoXmlBindingUtil
                    .getElementsByTagName(elementListRoot, "field");
            for (int index = 0; index < listChildNodes.size(); index++) {
                final BlancoXmlElement elementList = listChildNodes.get(index);
                final BlancoValueObjectFieldStructure fieldStructure = new BlancoValueObjectFieldStructure();

                fieldStructure.setNo(BlancoXmlBindingUtil.getTextContent(
                        elementList, "no"));
                fieldStructure.setName(BlancoXmlBindingUtil.getTextContent(
                        elementList, "name"));
                if (fieldStructure.getName() == null
                        || fieldStructure.getName().trim().length() == 0) {
                    continue;
                }

                fieldStructure.setType(BlancoXmlBindingUtil.getTextContent(
                        elementList, "type"));

                fieldStructure.setDescription(BlancoXmlBindingUtil
                        .getTextContent(elementList, "description"));
                final String[] lines = BlancoNameUtil.splitString(
                        fieldStructure.getDescription(), '\n');
                for (int indexLine = 0; indexLine < lines.length; indexLine++) {
                    if (indexLine == 0) {
                        fieldStructure.setDescription(lines[indexLine]);
                    } else {
                        // 複数行の description については、これを分割して格納します。
                        // ２行目からは、適切に文字参照エンコーディングが実施されているものと仮定します。
                        fieldStructure.getDescriptionList().add(
                                lines[indexLine]);
                    }
                }

                fieldStructure.setDefault(BlancoXmlBindingUtil.getTextContent(
                        elementList, "default"));
                fieldStructure.setMinLength(BlancoXmlBindingUtil
                        .getTextContent(elementList, "minLength"));
                fieldStructure.setMaxLength(BlancoXmlBindingUtil
                        .getTextContent(elementList, "maxLength"));
                fieldStructure.setLength(BlancoXmlBindingUtil.getTextContent(
                        elementList, "length"));
                fieldStructure.setMinInclusive(BlancoXmlBindingUtil
                        .getTextContent(elementList, "minInclusive"));
                fieldStructure.setMaxInclusive(BlancoXmlBindingUtil
                        .getTextContent(elementList, "maxInclusive"));
                fieldStructure.setPattern(BlancoXmlBindingUtil.getTextContent(
                        elementList, "pattern"));

                if (fieldStructure.getType() == null
                        || fieldStructure.getType().trim().length() == 0) {
                    throw new IllegalArgumentException(fMsg.getMbvoji02(
                            objClassStructure.getName(), fieldStructure
                                    .getName()));
                }

                objClassStructure.getFieldList().add(fieldStructure);
            }
        }

        return objClassStructure;
    }

    /**
     * 中間XMLファイル形式の「sheet」XMLエレメント(PHP書式)をパースして、バリューオブジェクト情報を取得します。
     *
     * @param argElementSheet
     *            中間XMLファイルの「sheet」XMLエレメント。
     * @return パースの結果得られたバリューオブジェクト情報。「name」が見つからなかった場合には nullを戻します。
     */
    public BlancoValueObjectClassStructure parseElementSheetPhp(
            final BlancoXmlElement argElementSheet
    ) {
        final BlancoValueObjectClassStructure objClassStructure = new BlancoValueObjectClassStructure();
        final List<BlancoXmlElement> listCommon = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobjectphp-common");
        if (listCommon == null || listCommon.size() == 0) {
            // commonが無い場合にはスキップします。
            return null;
        }

        // パッケージ名の置き換えオプションがあれば設定しておく
        objClassStructure.setPackageSuffix(BlancoValueObjectUtil.packageSuffix);
        objClassStructure.setOverridePackage(BlancoValueObjectUtil.overridePackage);

        // バリューオブジェクト定義(php)・共通
        final BlancoXmlElement elementCommon = listCommon.get(0);
        parseCommonPhp(elementCommon, objClassStructure);
        if (BlancoStringUtil.null2Blank(objClassStructure.getName()).trim()
                .length() == 0) {
            // 名前が無いものはスキップします。
            return null;
        }

        // バリューオブジェクト定義(php)・継承
        final List<BlancoXmlElement> extendsList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobjectphp-extends");
        if (extendsList != null && extendsList.size() != 0) {
            final BlancoXmlElement elementExtendsRoot = extendsList.get(0);
            parseExtendsPhp(elementExtendsRoot, objClassStructure);
        }

        // バリューオブジェクト定義(php)・実装
        final List<BlancoXmlElement> interfaceList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobjectphp-implements");
        if (interfaceList != null && interfaceList.size() != 0) {
            final BlancoXmlElement elementInterfaceRoot = interfaceList.get(0);

            parseInterfacePhp(elementInterfaceRoot, objClassStructure);
        }

        /* import の一覧作成, java用があればそちらを優先 */

        List<BlancoXmlElement> importList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, "blancovalueobject-import");
        if (!BlancoValueObjectUtil.ignoreImport && (importList == null || importList.size() == 0)) {
            importList = BlancoXmlBindingUtil
                    .getElementsByTagName(argElementSheet, "blancovalueobjectphp-import");
        }
        if (importList != null && importList.size() != 0) {
            final BlancoXmlElement elementImportRoot = importList.get(0);
            parseImportListPhp(elementImportRoot, objClassStructure);
        }

        // バリューオブジェクト定義(php)・一覧
        final List<BlancoXmlElement> listList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, "blancovalueobjectphp-list");
        if (listList != null && listList.size() != 0) {
            final BlancoXmlElement elementListRoot = listList.get(0);
            parseFieldList(elementListRoot, objClassStructure);
        }

        return objClassStructure;
    }

    /**
     * 共通定義を処理します。
     * @param argElementCommon
     * @param argObjClassStructure
     */
    private void parseCommonPhp(
            final BlancoXmlElement argElementCommon,
            final BlancoValueObjectClassStructure argObjClassStructure
    ) {
        argObjClassStructure.setName(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "name"));

        /*
         * このクラスのパッケージ名は suffix や override 前のものを
         * 覚えておき、ソースコード生成時に調整する。
         */
        argObjClassStructure.setPackage(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "package"));

        argObjClassStructure.setDescription(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "description"));
        if (BlancoStringUtil.null2Blank(argObjClassStructure.getDescription())
                .length() > 0) {
            final String[] lines = BlancoNameUtil.splitString(argObjClassStructure
                    .getDescription(), '\n');
            for (int index = 0; index < lines.length; index++) {
                if (index == 0) {
                    argObjClassStructure.setDescription(lines[index]);
                } else {
                    // 複数行の description については、これを分割して格納します。
                    // ２行目からは、適切に文字参照エンコーディングが実施されているものと仮定します。
                    argObjClassStructure.getDescriptionList().add(lines[index]);
                }
            }
        }

        /* クラスの annotation に対応, (Java) があればそちらを優先 */
        String classAnnotation = BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "annotationJava");
        if (BlancoStringUtil.null2Blank(classAnnotation).length() == 0 && !BlancoValueObjectUtil.ignoreAnnotation) {
            classAnnotation = BlancoXmlBindingUtil.getTextContent(
                    argElementCommon, "annotation");
        }
        if (BlancoStringUtil.null2Blank(classAnnotation).length() > 0) {
            argObjClassStructure.setAnnotationList(createAnnotaionList(classAnnotation));
        }

//        objClassStructure.setAccess(BlancoXmlBindingUtil.getTextContent(
//                elementCommon, "access"));
        /*
         * PHP 形式の定義書には access 修飾子の設定がないので全てpublicとする
         */
        argObjClassStructure.setAccess("public");
        argObjClassStructure.setAbstract("true".equals(BlancoXmlBindingUtil
                .getTextContent(argElementCommon, "abstract")));
        argObjClassStructure.setGenerateToString("true"
                .equals(BlancoXmlBindingUtil.getTextContent(argElementCommon,
                        "generateToString")));
        argObjClassStructure.setAdjustFieldName("true".equals(BlancoXmlBindingUtil
                .getTextContent(argElementCommon, "adjustFieldName")));
        argObjClassStructure.setAdjustDefaultValue("true"
                .equals(BlancoXmlBindingUtil.getTextContent(argElementCommon,
                        "adjustDefaultValue")));
        argObjClassStructure
                .setFieldList(new ArrayList<blanco.valueobject.valueobject.BlancoValueObjectFieldStructure>());

        if (argObjClassStructure.getPackage() == null) {
            throw new IllegalArgumentException(fMsg
                    .getMbvoji01(argObjClassStructure.getName()));
        }
    }

    /**
     * 中間XMLファイル形式の「sheet」XMLエレメント(PHP書式)をパースして、バリューオブジェクト情報を取得します。
     *
     * @param argElementExtendsRoot
     * @param argClassStructure
     */
    private void parseExtendsPhp(
            final BlancoXmlElement argElementExtendsRoot,
            final BlancoValueObjectClassStructure argClassStructure
    ) {
        String className = BlancoXmlBindingUtil.getTextContent(argElementExtendsRoot, "name");
        String packageName = BlancoXmlBindingUtil.getTextContent(argElementExtendsRoot, "package");
        if (packageName == null ||
                (BlancoValueObjectUtil.packageSuffix != null && BlancoValueObjectUtil.packageSuffix.length() > 0) ||
                (BlancoValueObjectUtil.overridePackage != null && BlancoValueObjectUtil.overridePackage.length() > 0)) {
            /*
             * このクラスのパッケージ名を探す
             */
            packageName = BlancoValueObjectUtil.searchPackageBySimpleName(className);
        }
        if (packageName != null) {
            className = packageName + "." + className;
            if (isVerbose()) {
                System.out.println("Extends = " + className);
            }
        }
        argClassStructure.setExtends(className);
    }

    private List<String> createAnnotaionList(String annotations) {
        List<String> annotationList = new ArrayList<>();
        final String[] lines = BlancoNameUtil.splitString(annotations, '\n');
        StringBuffer sb = new StringBuffer();
        for (String line : lines) {
            if (line.startsWith("@")) {
                if (sb.length() > 0) {
                    annotationList.add(sb.toString());
                    sb = new StringBuffer();
                }
                line = line.substring(1);
            }
            sb.append(line + System.getProperty("line.separator", "\n"));
        }
        if (sb.length() > 0) {
            annotationList.add(sb.toString());
        }
//        if (this.isVerbose()) {
//            for (String ann : annotationList) {
//                System.out.println("Ann: " + ann);
//            }
//        }
        return annotationList;
    }

    /**
     * バリューオブジェクト定義(php)・実装<br>
     * <br>
     * packageSuffix, overridePackage が指定されている場合、
     * tmp を検索して見つかればそれを優先する。
     * @param argElementInterfaceRoot
     * @param argClassStructure
     */
    private void parseInterfacePhp(
            final BlancoXmlElement argElementInterfaceRoot,
            final BlancoValueObjectClassStructure argClassStructure
    ) {
        final List<BlancoXmlElement> listInterfaceChildNodes = BlancoXmlBindingUtil
                .getElementsByTagName(argElementInterfaceRoot, "interface");
        for (int index = 0; index < listInterfaceChildNodes.size(); index++) {
            final BlancoXmlElement elementList = listInterfaceChildNodes
                    .get(index);

            String interfaceName = BlancoXmlBindingUtil
                    .getTextContent(elementList, "name");
            if (interfaceName == null || interfaceName.trim().length() == 0) {
                continue;
            }
            String interfacePackage = BlancoValueObjectUtil.getPackageName(interfaceName);
            String interfaceSimple = BlancoValueObjectUtil.getSimpleClassName(interfaceName);
            if (interfacePackage.length() == 0 ||
                    (BlancoValueObjectUtil.packageSuffix != null && BlancoValueObjectUtil.packageSuffix.length() > 0) ||
                    (BlancoValueObjectUtil.overridePackage != null && BlancoValueObjectUtil.overridePackage.length() > 0)) {
                // このインタフェイスが自動生成されていればそちらを優先
                interfacePackage = BlancoValueObjectUtil.searchPackageBySimpleName(interfaceSimple);
                if (interfacePackage != null && interfacePackage.length() >0) {
                    interfaceName = interfacePackage + "." + interfaceSimple;
                }
            }
            if (isVerbose()) {
                System.out.println("Implements = " + interfaceName);
            }
            argClassStructure.getImplementsList().add(interfaceName);
        }
    }


    /**
     * import の一覧作成
     * @param argElementImportRoot
     * @param argClassStructure
     */
    private void parseImportListPhp(
            final BlancoXmlElement argElementImportRoot,
            final BlancoValueObjectClassStructure argClassStructure
    ) {
        final List<BlancoXmlElement> listImportChildNodes = BlancoXmlBindingUtil
                .getElementsByTagName(argElementImportRoot, "import");
        for (int index = 0; index < listImportChildNodes.size(); index++) {
            final BlancoXmlElement elementList = listImportChildNodes
                    .get(index);

            final String importName = BlancoXmlBindingUtil
                    .getTextContent(elementList, "name");
//            System.out.println("/* tueda */ import = " + importName);
            if (importName == null || importName.trim().length() == 0) {
                continue;
            }
            argClassStructure.getImportList().add(
                    BlancoXmlBindingUtil
                            .getTextContent(elementList, "name"));
        }
    }


    /**
     * バリューオブジェクト定義(php)・一覧
     * @param argElementListRoot
     * @param argClassStructure
     */
    private void parseFieldList(
            final BlancoXmlElement argElementListRoot,
            final BlancoValueObjectClassStructure argClassStructure
    ) {

        final List<BlancoXmlElement> listChildNodes = BlancoXmlBindingUtil
                .getElementsByTagName(argElementListRoot, "field");
        for (int index = 0; index < listChildNodes.size(); index++) {
            final BlancoXmlElement elementList = listChildNodes.get(index);
            final BlancoValueObjectFieldStructure fieldStructure = new BlancoValueObjectFieldStructure();

            fieldStructure.setNo(BlancoXmlBindingUtil.getTextContent(
                    elementList, "no"));
            fieldStructure.setName(BlancoXmlBindingUtil.getTextContent(
                    elementList, "name"));
            if (fieldStructure.getName() == null
                    || fieldStructure.getName().trim().length() == 0) {
                continue;
            }

            /*
             * 型の取得．Java 型が定義されている場合はそちらが優先。
             * Java型の取得．型名は Java 風に定義されている前提。
             * php 型の場合は、ここで Java 風の型名に変えておく
             */
            String javaType = BlancoXmlBindingUtil.getTextContent(elementList, "typeJava");
            if (BlancoStringUtil.null2Blank(javaType).length() == 0) {
                String phpType = BlancoXmlBindingUtil.getTextContent(elementList, "type");
                if (BlancoStringUtil.null2Blank(phpType).length() == 0) {
                    // 型は必須
                    throw new IllegalArgumentException(fMsg.getMbvoji04(
                            argClassStructure.getName(),
                            fieldStructure.getName()
                    ));

                }
                javaType = BlancoValueObjectUtil.parsePhpTypes(phpType, false);
            }
            fieldStructure.setType(javaType);

            /* Java Generic がある場合はそちらが優先 */
            String javaGeneric = BlancoXmlBindingUtil.getTextContent(elementList, "genericJava");
            if (BlancoStringUtil.null2Blank(javaGeneric).length() == 0) {
                String phpGeneric = BlancoXmlBindingUtil.getTextContent(elementList, "generic");
                if (BlancoStringUtil.null2Blank(phpGeneric).length() != 0) {
                    javaGeneric = BlancoValueObjectUtil.parsePhpTypes(phpGeneric, true);
                }
            }
            if (BlancoStringUtil.null2Blank(javaGeneric).length() != 0) {
                fieldStructure.setGeneric(javaGeneric);
            }

            /* Java の annnotation があればそちらを優先 */
            String javaAnnotation = BlancoXmlBindingUtil.getTextContent(elementList, "annotationJava");
            if (BlancoStringUtil.null2Blank(javaAnnotation).length() == 0 && !BlancoValueObjectUtil.ignoreAnnotation) {
                String phpAnnotation = BlancoXmlBindingUtil.getTextContent(elementList, "annotation");
                if (BlancoStringUtil.null2Blank(phpAnnotation).length() != 0) {
                    javaAnnotation = phpAnnotation;
                }
            }
            if (BlancoStringUtil.null2Blank(javaAnnotation).length() != 0) {
                fieldStructure.setAnnotationList(createAnnotaionList(javaAnnotation));
            }

            // abstract に対応
            fieldStructure.setAbstract("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementList, "abstract")));

            // required に対応 (NotNullアノテーションの付与）
            fieldStructure.setRequired("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementList, "required")));
            if (fieldStructure.getRequired()) {
                fieldStructure.getAnnotationList().add("NotNull");
                argClassStructure.getImportList().add("javax.validation.constraints.NotNull");
            }

            // value に対応
            fieldStructure.setFixedValue("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementList, "fixedValue")));

            fieldStructure.setDescription(BlancoXmlBindingUtil
                    .getTextContent(elementList, "description"));
            final String[] lines = BlancoNameUtil.splitString(
                    fieldStructure.getDescription(), '\n');
            for (int indexLine = 0; indexLine < lines.length; indexLine++) {
                if (indexLine == 0) {
                    fieldStructure.setDescription(lines[indexLine]);
                } else {
                    // 複数行の description については、これを分割して格納します。
                    // ２行目からは、適切に文字参照エンコーディングが実施されているものと仮定します。
                    fieldStructure.getDescriptionList().add(
                            lines[indexLine]);
                }
            }

            /*
             * Java 向け default 値があればそちらを優先。
             * ignoreDefault が指定されている場合も java 向け default は有効
             */
            String javaDefault = BlancoXmlBindingUtil.getTextContent(
                    elementList, "defaultJava");
            if (BlancoStringUtil.null2Blank(javaDefault).length() == 0 && !BlancoValueObjectUtil.ignoreDefault) {
                String phpDefault = BlancoXmlBindingUtil.getTextContent(
                        elementList, "default");
                if (BlancoStringUtil.null2Blank(phpDefault).length() != 0) {
                    javaDefault = phpDefault;
                }
            }
            fieldStructure.setDefault(javaDefault);

            fieldStructure.setMinLength(BlancoXmlBindingUtil
                    .getTextContent(elementList, "minLength"));
            fieldStructure.setMaxLength(BlancoXmlBindingUtil
                    .getTextContent(elementList, "maxLength"));
            fieldStructure.setLength(BlancoXmlBindingUtil.getTextContent(
                    elementList, "length"));
            fieldStructure.setMinInclusive(BlancoXmlBindingUtil
                    .getTextContent(elementList, "minInclusive"));
            fieldStructure.setMaxInclusive(BlancoXmlBindingUtil
                    .getTextContent(elementList, "maxInclusive"));
            fieldStructure.setPattern(BlancoXmlBindingUtil.getTextContent(
                    elementList, "pattern"));

            if (fieldStructure.getType() == null
                    || fieldStructure.getType().trim().length() == 0) {
                throw new IllegalArgumentException(fMsg.getMbvoji02(
                        argClassStructure.getName(), fieldStructure
                                .getName()));
            }

            argClassStructure.getFieldList().add(fieldStructure);
        }
    }
}
