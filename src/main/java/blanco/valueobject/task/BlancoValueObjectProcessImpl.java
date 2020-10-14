/*
 * blanco Framework
 * Copyright (C) 2004-2008 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.valueobject.task;

import blanco.cg.BlancoCgSupportedLang;
import blanco.valueobject.BlancoValueObjectConstants;
import blanco.valueobject.BlancoValueObjectMeta2Xml;
import blanco.valueobject.BlancoValueObjectUtil;
import blanco.valueobject.BlancoValueObjectXml2JavaClass;
import blanco.valueobject.message.BlancoValueObjectMessage;
import blanco.valueobject.task.valueobject.BlancoValueObjectProcessInput;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public class BlancoValueObjectProcessImpl implements BlancoValueObjectProcess {

    /**
     * メッセージ。
     */
    private final BlancoValueObjectMessage fMsg = new BlancoValueObjectMessage();

    /**
     * {@inheritDoc}
     */
    public int execute(final BlancoValueObjectProcessInput input)
            throws IOException, IllegalArgumentException {
        System.out.println("- " + BlancoValueObjectConstants.PRODUCT_NAME
                + " (" + BlancoValueObjectConstants.VERSION + ")");
        try {
            final File fileMetadir = new File(input.getMetadir());
            if (fileMetadir.exists() == false) {
                throw new IllegalArgumentException(fMsg.getMbvoja01(input
                        .getMetadir()));
            }


            /*
             * 改行コードを決定します。
             */
            String LF = "\n";
            String CR = "\r";
            String CRLF = CR + LF;
            String lineSeparatorMark = input.getLineSeparator();
            String lineSeparator = "";
            if ("LF".equals(lineSeparatorMark)) {
                lineSeparator = LF;
            } else if ("CR".equals(lineSeparatorMark)) {
                lineSeparator = CR;
            } else if ("CRLF".equals(lineSeparatorMark)) {
                lineSeparator = CRLF;
            }
            if (lineSeparator.length() != 0) {
                System.setProperty("line.separator", lineSeparator);
                if (input.getVerbose()) {
                    System.out.println("lineSeparator try to change to " + lineSeparatorMark);
                    String newProp = System.getProperty("line.separator");
                    String newMark = "other";
                    if (LF.equals(newProp)) {
                        newMark = "LF";
                    } else if (CR.equals(newProp)) {
                        newMark = "CR";
                    } else if (CRLF.equals(newProp)) {
                        newMark = "CRLF";
                    }
                    System.out.println("New System Props = " + newMark);
                }
            }

            /*
             * targetdir, targetStyleの処理。
             * 生成されたコードの保管場所を設定する。
             * targetstyle = blanco:
             *  targetdirの下に main ディレクトリを作成
             * targetstyle = maven:
             *  targetdirの下に main/java ディレクトリを作成
             * targetstyle = free:
             *  targetdirをそのまま使用してディレクトリを作成。
             *  ただしtargetdirがからの場合はデフォルト文字列(blanco)使用する。
             * by tueda, 2019/08/30
             */
            String strTarget = input.getTargetdir();
            String style = input.getTargetStyle();
            // ここを通ったら常にtrue
            boolean isTargetStyleAdvanced = true;
            if (style != null && BlancoValueObjectConstants.TARGET_STYLE_MAVEN.equals(style)) {
                strTarget = strTarget + "/" + BlancoValueObjectConstants.TARGET_DIR_SUFFIX_MAVEN;
            } else if (style == null ||
                    !BlancoValueObjectConstants.TARGET_STYLE_FREE.equals(style)) {
                strTarget = strTarget + "/" + BlancoValueObjectConstants.TARGET_DIR_SUFFIX_BLANCO;
            }
            /* style が free だったらtargetdirをそのまま使う */
            if (input.getVerbose()) {
                System.out.println("/* tueda */ TARGETDIR = " + strTarget);
            }

            // テンポラリディレクトリを作成。
            new File(input.getTmpdir()
                    + BlancoValueObjectConstants.TARGET_SUBDIRECTORY).mkdirs();

            new BlancoValueObjectMeta2Xml().processDirectory(fileMetadir, input
                    .getTmpdir()
                    + BlancoValueObjectConstants.TARGET_SUBDIRECTORY);

            // XML化されたメタファイルからValueObjectを生成
            // 最初にテンポラリフォルダを走査
            final File[] fileMeta2 = new File(input.getTmpdir()
                    + BlancoValueObjectConstants.TARGET_SUBDIRECTORY)
                    .listFiles();
            /*
             * まず始めにすべてのシートを検索して，クラス名とpackage名のリストを作ります．
             * php形式の定義書では，クラスを指定する際にpackage名が指定されていないからです．
             * ここでは生のxml情報を取得する必要があるので、共通で使用するオプションを
             * セットする前に呼び出さなければならない。
             */
            BlancoValueObjectUtil.processValueObjects(input);

            /*
             * 共通で使用するオプションを記憶する
             */
            BlancoValueObjectUtil.packageSuffix = input.getPackageSuffix();
            BlancoValueObjectUtil.overridePackage = input.getOverridePackage();
            BlancoValueObjectUtil.voPackageSuffix = input.getVoPackageSuffix();
            BlancoValueObjectUtil.voOverridePackage = input.getVoOverridePackage();
            BlancoValueObjectUtil.ignoreDefault = input.getIgnoreDefault();
            BlancoValueObjectUtil.ignoreAnnotation = input.getIgnoreAnnotation();

            for (int index = 0; index < fileMeta2.length; index++) {
                if (fileMeta2[index].getName().endsWith(".xml") == false) {
                    continue;
                }

                final BlancoValueObjectXml2JavaClass xml2JavaClass = new BlancoValueObjectXml2JavaClass();
                xml2JavaClass.setEncoding(input.getEncoding());
                xml2JavaClass.setVerbose(input.getVerbose());
                xml2JavaClass.setTargetStyleAdvanced(isTargetStyleAdvanced);
                xml2JavaClass.setXmlRootElement(input.getXmlrootelement());
                xml2JavaClass.setSheetLang(new BlancoCgSupportedLang().convertToInt(input.getSheetType()));
                xml2JavaClass.process(fileMeta2[index], new File(strTarget));

                // 単体試験コードの自動生成機能は 0.9.1以降では削除されました。
            }

            // 次にメタディレクトリとして指定されているディレクトリを走査
            final File[] fileMeta3 = fileMetadir.listFiles();
            for (int index = 0; index < fileMeta3.length; index++) {
                if (fileMeta3[index].getName().endsWith(".xml") == false) {
                    continue;
                }

                final BlancoValueObjectXml2JavaClass xml2JavaClass = new BlancoValueObjectXml2JavaClass();
                xml2JavaClass.setEncoding(input.getEncoding());
                xml2JavaClass.setVerbose(input.getVerbose());
                xml2JavaClass.setTargetStyleAdvanced(isTargetStyleAdvanced);
                xml2JavaClass.setXmlRootElement(input.getXmlrootelement());
                xml2JavaClass.setSheetLang(new BlancoCgSupportedLang().convertToInt(input.getSheetType()));
                xml2JavaClass.process(fileMeta3[index], new File(strTarget));

                // 単体試験コードの自動生成機能は 0.9.1以降では削除されました。
            }

            return BlancoValueObjectBatchProcess.END_SUCCESS;
        } catch (TransformerException e) {
            throw new IOException("XML変換の過程で例外が発生しました: " + e.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean progress(final String argProgressMessage) {
        System.out.println(argProgressMessage);
        return false;
    }
}
