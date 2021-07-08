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
 *  * A class that parses (reads and writes) the intermediate XML file format of blancoValueObject.
 *
 * @author IGA Tosiki
 */
public class BlancoValueObjectXmlParser {
    /**
     * A message.
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
     * Parses an XML document in an intermediate XML file to get an array of information.
     *
     * @param argMetaXmlSourceFile
     *            An intermediate XML file.
     * @return An array of information obtained as a result of parsing.
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
     * Parses an XML document in an intermediate XML file to get an array of value object information.
     * 
     * @param argXmlDocument
     *            XML document of an intermediate XML file.
     * @return An array of value object information obtained as a result of parsing.
     */
    public BlancoValueObjectClassStructure[] parse(
            final BlancoXmlDocument argXmlDocument) {
        final List<BlancoValueObjectClassStructure> listStructure = new ArrayList<BlancoValueObjectClassStructure>();

        // Gets the root element.
        final BlancoXmlElement elementRoot = BlancoXmlBindingUtil
                .getDocumentElement(argXmlDocument);
        if (elementRoot == null) {
            // The process is aborted if there is no root element.
            return null;
        }

        // Gets a list of sheets (Excel sheets).
        final List<BlancoXmlElement> listSheet = BlancoXmlBindingUtil
                .getElementsByTagName(elementRoot, "sheet");

        final int sizeListSheet = listSheet.size();
        for (int index = 0; index < sizeListSheet; index++) {
            final BlancoXmlElement elementSheet = listSheet.get(index);

            /*
             * Supports sheets written for languages other than Java.
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
                // Skips if there is no common.
                continue;
            }

            // Processes only the first item.
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
                // Memorizes the obtained information.
                listStructure.add(objClassStructure);
            }
        }

        final BlancoValueObjectClassStructure[] result = new BlancoValueObjectClassStructure[listStructure
                .size()];
        listStructure.toArray(result);
        return result;
    }

    /**
     * Parses the "sheet" XML element in the intermediate XML file to get the value object information.
     * 
     * @param argElementSheet
     *            "sheet" XML element in the intermediate XML file.
     * @return Value object information obtained as a result of parsing. Null is returned if "name" is not found.
     */
    public BlancoValueObjectClassStructure parseElementSheet(
            final BlancoXmlElement argElementSheet) {
        final BlancoValueObjectClassStructure objClassStructure = new BlancoValueObjectClassStructure();
        final List<BlancoXmlElement> listCommon = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobject-common");
        if (listCommon == null || listCommon.size() == 0) {
            // Skips if there is no common.
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
                    // For a multi-line description, it will be split and stored.
                    // From the second line, assumes that character reference encoding has been properly implemented. 
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
            // Skips if name is empty.
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
                        // For a multi-line description, it will be split and stored.
                        // From the second line, assumes that character reference encoding has been properly implemented.   
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
     * Parses the "sheet" XML element (PHP format) in the intermediate XML file to get the value object information.
     *
     * @param argElementSheet
     *            "sheet" XML element in the intermediate XML file.
     * @return Value object information obtained as a result of parsing. Null is returned if "name" is not found.
     */
    public BlancoValueObjectClassStructure parseElementSheetPhp(
            final BlancoXmlElement argElementSheet
    ) {
        final BlancoValueObjectClassStructure objClassStructure = new BlancoValueObjectClassStructure();
        final List<BlancoXmlElement> listCommon = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobjectphp-common");
        if (listCommon == null || listCommon.size() == 0) {
            // Skips if there is no common.
            return null;
        }

        // Sets the option to replace the package name if available.
        objClassStructure.setPackageSuffix(BlancoValueObjectUtil.packageSuffix);
        objClassStructure.setOverridePackage(BlancoValueObjectUtil.overridePackage);

        // Value object definition (PHP) common
        final BlancoXmlElement elementCommon = listCommon.get(0);
        parseCommonPhp(elementCommon, objClassStructure);
        if (BlancoStringUtil.null2Blank(objClassStructure.getName()).trim()
                .length() == 0) {
            // Skips if name is empty.
            return null;
        }

        // Value object definition (PHP) inheritance
        final List<BlancoXmlElement> extendsList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobjectphp-extends");
        if (extendsList != null && extendsList.size() != 0) {
            final BlancoXmlElement elementExtendsRoot = extendsList.get(0);
            parseExtendsPhp(elementExtendsRoot, objClassStructure);
        }

        // Value object definition (PHP) implementation
        final List<BlancoXmlElement> interfaceList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        "blancovalueobjectphp-implements");
        if (interfaceList != null && interfaceList.size() != 0) {
            final BlancoXmlElement elementInterfaceRoot = interfaceList.get(0);

            parseInterfacePhp(elementInterfaceRoot, objClassStructure);
        }

        /* Create import List, prefer for java, or for java only if ignoreImport is specified. */
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

        // Value object definition (PHP) list
        final List<BlancoXmlElement> listList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, "blancovalueobjectphp-list");
        if (listList != null && listList.size() != 0) {
            final BlancoXmlElement elementListRoot = listList.get(0);
            parseFieldList(elementListRoot, objClassStructure);
        }

        return objClassStructure;
    }

    /**
     * Processes the common definition.
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
         * Remembers the package name of this class before suffixing and overriding, and adjusts it when generating the source code.
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
                    // For a multi-line description, it will be split and stored.
                    // From the second line, assumes that character reference encoding has been properly implemented.   
                    argObjClassStructure.getDescriptionList().add(lines[index]);
                }
            }
        }

        /* Class annotation, prefer for java, or java only if ignoreAnnotation is specified. */
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
         * There is no access modifier set in the PHP-style definition document, so all are public.
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
     * Parses the "sheet" XML element (PHP format) in the intermediate XML file to get the value object information.
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
             * Searches for the package name for this class.
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
     * Value object definition (PHP) implementation<br>
     * <br>
     * If packageSuffix or overridePackage is specified, searches for tmp and give priority to it if found.
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
                // If this interface is auto-generated, gives priority to it.
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
     * Creates a list of import.
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
     * Value object definition (PHP) list
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
             * Gets the type. If a Java type is defined, it takes precedence.
             * Gets the Java type. The type name is assumed to be defined in Java-style.
             * In the case of the PHP type, changes the type name to Java-style here.
             */
            String javaType = BlancoXmlBindingUtil.getTextContent(elementList, "typeJava");
            if (BlancoStringUtil.null2Blank(javaType).length() == 0) {
                String phpType = BlancoXmlBindingUtil.getTextContent(elementList, "type");
                if (BlancoStringUtil.null2Blank(phpType).length() == 0) {
                    // Type is required.
                    throw new IllegalArgumentException(fMsg.getMbvoji04(
                            argClassStructure.getName(),
                            fieldStructure.getName()
                    ));

                }
                javaType = BlancoValueObjectUtil.parsePhpTypes(phpType, false);
            }
            fieldStructure.setType(javaType);

            /* If Java Generic is available, it takes precedence. */
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

            /* Create method annotation, prefer java or javaOnly if ignoreAnnotation is specified. */
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

            // Supports for abstract.
            fieldStructure.setAbstract("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementList, "abstract")));

            // Supports for required. (Giving NotNull annotation)
            fieldStructure.setRequired("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementList, "required")));
            if (fieldStructure.getRequired()) {
                fieldStructure.getAnnotationList().add("NotNull");
                argClassStructure.getImportList().add("javax.validation.constraints.NotNull");
            }

            // Supports for value.
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
                    // For a multi-line description, it will be split and stored.
                    // From the second line, assumes that character reference encoding has been properly implemented.   
                    fieldStructure.getDescriptionList().add(
                            lines[indexLine]);
                }
            }

            /*
             * If there is a default value for Java, it is preferred.
             * Even if ignoreDefault is specified, default for Java is valid.
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
