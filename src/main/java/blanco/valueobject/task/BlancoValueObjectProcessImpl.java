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
     * A message.
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
             * Determines the newline code.
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
             * Processes targetdir and targetStyle.
             * Sets the storage location for the generated code.
             * targetstyle = blanco:
             *  Creates a main directory under targetdir.
             * targetstyle = maven:
             *  Creates a main/java directory under targetdir.
             * targetstyle = free:
             *  Creates a directory using targetdir as is.
             *  However, if targetdir is empty, the default string (blanco) is used.
             * by tueda, 2019/08/30
             */
            String strTarget = input.getTargetdir();
            String style = input.getTargetStyle();
            // Always true when passing through here.
            boolean isTargetStyleAdvanced = true;
            if (style != null && BlancoValueObjectConstants.TARGET_STYLE_MAVEN.equals(style)) {
                strTarget = strTarget + "/" + BlancoValueObjectConstants.TARGET_DIR_SUFFIX_MAVEN;
            } else if (style == null ||
                    !BlancoValueObjectConstants.TARGET_STYLE_FREE.equals(style)) {
                strTarget = strTarget + "/" + BlancoValueObjectConstants.TARGET_DIR_SUFFIX_BLANCO;
            }
            /* If style is free, uses targetdir as is. */
            if (input.getVerbose()) {
                System.out.println("/* tueda */ TARGETDIR = " + strTarget);
            }

            // Creates a temporary directory.
            new File(input.getTmpdir()
                    + BlancoValueObjectConstants.TARGET_SUBDIRECTORY).mkdirs();

            new BlancoValueObjectMeta2Xml().processDirectory(fileMetadir, input
                    .getTmpdir()
                    + BlancoValueObjectConstants.TARGET_SUBDIRECTORY);

            // Generates ValueObject from XML-ized meta file.
            // Scans the temporary folder first.
            final File[] fileMeta2 = new File(input.getTmpdir()
                    + BlancoValueObjectConstants.TARGET_SUBDIRECTORY)
                    .listFiles();
            /*
             * First, searches all the sheets and makes a list of structures from the class names.
             * The reason is that in the PHP-style definitions, the package name is not specified when specifying a class.
             * Since we need to get the raw XML information here, it has to be called before setting the commonly used options.
             */
            BlancoValueObjectUtil.processValueObjects(input);

            /*
             * Stores commonly used options.
             */
            BlancoValueObjectUtil.packageSuffix = input.getPackageSuffix();
            BlancoValueObjectUtil.overridePackage = input.getOverridePackage();
            BlancoValueObjectUtil.voPackageSuffix = input.getVoPackageSuffix();
            BlancoValueObjectUtil.voOverridePackage = input.getVoOverridePackage();
            BlancoValueObjectUtil.ignoreDefault = input.getIgnoreDefault();
            BlancoValueObjectUtil.ignoreAnnotation = input.getIgnoreAnnotation();
            BlancoValueObjectUtil.ignoreImport = input.getIgnoreImport();

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

                // The auto-generation of unit test codes has been removed in 0.9.1 and later.
            }

            // Next, scans the directory specified as the meta directory.
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

                // The auto-generation of unit test codes has been removed in 0.9.1 and later.
            }

            return BlancoValueObjectBatchProcess.END_SUCCESS;
        } catch (TransformerException e) {
            throw new IOException("An exception has occurred during the XML conversion process: " + e.toString());
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
