/*
 * blanco Framework
 * Copyright (C) 2004-2010 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.valueobject;

import blanco.beanutils.BlancoBeanUtils;
import blanco.cg.BlancoCgObjectFactory;
import blanco.cg.BlancoCgSupportedLang;
import blanco.cg.transformer.BlancoCgTransformerFactory;
import blanco.cg.util.BlancoCgSourceUtil;
import blanco.cg.valueobject.*;
import blanco.commons.util.BlancoJavaSourceUtil;
import blanco.commons.util.BlancoNameAdjuster;
import blanco.commons.util.BlancoNameUtil;
import blanco.commons.util.BlancoStringUtil;
import blanco.valueobject.message.BlancoValueObjectMessage;
import blanco.valueobject.resourcebundle.BlancoValueObjectResourceBundle;
import blanco.valueobject.valueobject.BlancoValueObjectClassStructure;
import blanco.valueobject.valueobject.BlancoValueObjectFieldStructure;
import blanco.xml.bind.BlancoXmlBindingUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A class that auto-generates Java source code from intermediate XML files for value objects.
 *
 * This is one of the main classes of blancoValueObject.
 *
 * @author IGA Tosiki
 */
public class BlancoValueObjectXml2JavaClass {
    /**
     * A message.
     */
    private final BlancoValueObjectMessage fMsg = new BlancoValueObjectMessage();

    /**
     * Resource bundle object for blancoValueObject.
     */
    private final BlancoValueObjectResourceBundle fBundle = new BlancoValueObjectResourceBundle();

    /**
     * A programming language expected for the input sheet.
     */
    private int fSheetLang = BlancoCgSupportedLang.JAVA;

    public void setSheetLang(final int argSheetLang) {
        fSheetLang = argSheetLang;
    }

    /**
     * Style of the source code generation destination directory
     */
    private boolean fTargetStyleAdvanced = false;
    public void setTargetStyleAdvanced(boolean argTargetStyleAdvanced) {
        this.fTargetStyleAdvanced = argTargetStyleAdvanced;
    }
    public boolean isTargetStyleAdvanced() {
        return this.fTargetStyleAdvanced;
    }

    private boolean fVerbose = false;
    public void setVerbose(boolean argVerbose) {
        this.fVerbose = argVerbose;
    }
    public boolean isVerbose() {
        return this.fVerbose;
    }

    /**
     * A factory for blancoCg to be used internally.
     */
    private BlancoCgObjectFactory fCgFactory = null;

    /**
     * Source file information for blancoCg to be used internally.
     */
    private BlancoCgSourceFile fCgSourceFile = null;

    /**
     * Class information for blancoCg to be used internally.
     */
    private BlancoCgClass fCgClass = null;

    /**
     * Character encoding of auto-generated source files.
     */
    private String fEncoding = null;

    public void setEncoding(final String argEncoding) {
        fEncoding = argEncoding;
    }

    private boolean fIsXmlRootElement = false;

    public void setXmlRootElement(final boolean isXmlRootElement) {
        fIsXmlRootElement = isXmlRootElement;
    }

    /**
     * Auto-generates Java source code from an intermediate XML file representing a value object.
     *
     * @param argMetaXmlSourceFile
     *            An XML file containing meta-information about the ValueObject.
     * @param argDirectoryTarget
     *            Source code generation destination directory.
     * @throws IOException
     *             If an I/O exception occurs.
     */
    public void process(final File argMetaXmlSourceFile,
            final File argDirectoryTarget) throws IOException {
        BlancoValueObjectXmlParser parser = new BlancoValueObjectXmlParser();
        parser.setVerbose(this.isVerbose());
        final BlancoValueObjectClassStructure[] structures = parser.parse(argMetaXmlSourceFile);
        for (int index = 0; index < structures.length; index++) {
            // Generates Java source code from the obtained information.
            structure2Source(structures[index], argDirectoryTarget);
        }
    }

    /**
     * Auto-generates source code from a given class information value object.
     *
     * @param argClassStructure
     *            Class information.
     * @param argDirectoryTarget
     *            Output directory for TypeScript source code.
     * @throws IOException
     *             If an I/O exception occurs.
     */
    public void structure2Source(
            final BlancoValueObjectClassStructure argClassStructure,
            final File argDirectoryTarget) throws IOException {
        /*
         * The output directory will be in the format specified by the targetStyle argument of the ant task.
         * For compatibility, the output directory will be blanco/main if it is not specified.
         * by tueda, 2019/08/30
         */
        String strTarget = argDirectoryTarget
                .getAbsolutePath(); // advanced
        if (!this.isTargetStyleAdvanced()) {
            strTarget += "/main"; // legacy
        }
        final File fileBlancoMain = new File(strTarget);

        /* tueda DEBUG */
        if (this.isVerbose()) {
            System.out.println("/* tueda */ structure2Source argDirectoryTarget : " + argDirectoryTarget.getAbsolutePath());
        }

        // Gets an instance of the BlancoCgObjectFactory class.
        fCgFactory = BlancoCgObjectFactory.getInstance();

        // Replace the package name if the Replace option is specified.
        // If Suffix is present, it takes precedence.
        String packageName = argClassStructure.getPackage();
        if (BlancoValueObjectUtil.packageSuffix != null && BlancoValueObjectUtil.packageSuffix.length() > 0) {
            if (BlancoStringUtil.null2Blank(packageName).length() > 0) {
                packageName += ".";
            } else {
                packageName = "";
            }
            packageName += BlancoValueObjectUtil.packageSuffix;
        } else if (BlancoValueObjectUtil.overridePackage != null && BlancoValueObjectUtil.overridePackage.length() > 0) {
            packageName = BlancoValueObjectUtil.overridePackage;
        }

        fCgSourceFile = fCgFactory.createSourceFile(packageName, null);
        fCgSourceFile.setEncoding(fEncoding);

        // Creates a class.
        fCgClass = fCgFactory.createClass(argClassStructure.getName(), "");
        fCgSourceFile.getClassList().add(fCgClass);

        // Sets access to the class.
        fCgClass.setAccess(argClassStructure.getAccess());
        // Abstract class or not.
        fCgClass.setAbstract(argClassStructure.getAbstract());

        // Inheritance
        if (BlancoStringUtil.null2Blank(argClassStructure.getExtends())
                .length() > 0) {
            fCgClass.getExtendClassList().add(
                    fCgFactory.createType(argClassStructure.getExtends()));
        }
        // Implementation
        for (int index = 0; index < argClassStructure.getImplementsList()
                .size(); index++) {
            final String impl = (String) argClassStructure.getImplementsList()
                    .get(index);
            fCgClass.getImplementInterfaceList().add(
                    fCgFactory.createType(impl));
        }

        if (fIsXmlRootElement) {
            fCgClass.getAnnotationList().add("XmlRootElement");
            fCgSourceFile.getImportList().add(
                    "javax.xml.bind.annotation.XmlRootElement");
        }

        // Sets the JavaDoc for the class.
        fCgClass.setDescription(argClassStructure.getDescription());
        for (String line : argClassStructure.getDescriptionList()) {
            fCgClass.getLangDoc().getDescriptionList().add(line);
        }

        /* Sets the annotation for the class. */
        List annotationList = argClassStructure.getAnnotationList();
        if (annotationList != null && annotationList.size() > 0) {
            fCgClass.getAnnotationList().addAll(argClassStructure.getAnnotationList());
            /* tueda DEBUG */
            System.out.println("/* tueda */ structure2Source : class annotation = " + argClassStructure.getAnnotationList().get(0));
        }

        /* Sets the import for the class. */
        for (int index = 0; index < argClassStructure.getImportList()
                .size(); index++) {
            final String imported = (String) argClassStructure.getImportList()
                    .get(index);
            fCgSourceFile.getImportList().add(imported);
        }

        for (int indexField = 0; indexField < argClassStructure.getFieldList()
                .size(); indexField++) {
            // Processes each field.
            final BlancoValueObjectFieldStructure fieldStructure = (BlancoValueObjectFieldStructure) argClassStructure
                    .getFieldList().get(indexField);

            // If a required field is not set, exception processing will be performed.
            if (fieldStructure.getName() == null) {
                throw new IllegalArgumentException(fMsg
                        .getMbvoji03(argClassStructure.getName()));
            }
            if (fieldStructure.getType() == null) {
                throw new IllegalArgumentException(fMsg.getMbvoji04(
                        argClassStructure.getName(), fieldStructure.getName()));
            }

            // Generates a field.
            buildField(argClassStructure, fieldStructure);

            // Generates a setter method.
            buildMethodSet(argClassStructure, fieldStructure);

            // Generates a getter method.
            buildMethodGet(argClassStructure, fieldStructure);
        }

        if (argClassStructure.getGenerateToString()) {
            // Generates toString method.
            buildMethodToString(argClassStructure);
        }

        // TODO: Considers whether to externally flag whether to generate copyTo method.
        BlancoBeanUtils.generateCopyToMethod(fCgSourceFile, fCgClass);

        // Auto-generates the actual source code based on the collected information.
        BlancoCgTransformerFactory.getJavaSourceTransformer().transform(
                fCgSourceFile, fileBlancoMain);
    }

    /**
     * Generates a field in the class.
     *
     * @param argClassStructure
     *            Class information.
     * @param argFieldStructure
     *            Field information.
     */
    private void buildField(
            final BlancoValueObjectClassStructure argClassStructure,
            final BlancoValueObjectFieldStructure argFieldStructure) {

        switch (fSheetLang) {
            case BlancoCgSupportedLang.PHP:
                if (argFieldStructure.getType() == "java.lang.Integer") argFieldStructure.setType("java.lang.Long");
                break;
            /* If you want to add more languages, add the case here. */
        }

        final BlancoCgField field = fCgFactory.createField("f"
                + getFieldNameAdjustered(argClassStructure, argFieldStructure),
                argFieldStructure.getType(), null);
        fCgClass.getFieldList().add(field);
        if (BlancoStringUtil.null2Blank(argFieldStructure.getGeneric()).length() > 0) {
            field.getType().setGenerics(argFieldStructure.getGeneric());
        }

        // Sets the JavaDoc for the field.
        field.setDescription(argFieldStructure.getDescription());
        for (String line : argFieldStructure.getDescriptionList()) {
            field.getLangDoc().getDescriptionList().add(line);
        }
        field.getLangDoc().getDescriptionList().add(
                fBundle.getXml2javaclassFieldName(argFieldStructure.getName()));

        if (argFieldStructure.getDefault() != null) {
            final String type = field.getType().getName();

            if (type.equals("java.util.Date")) {
                /*
                 * java.util.Date type does not allow default values.
                 */
                throw new IllegalArgumentException(fMsg.getMbvoji05(
                        argClassStructure.getName(), argFieldStructure
                                .getName(), argFieldStructure.getDefault(),
                        type));
            }

            // Sets the default value for the field.
            field.getLangDoc().getDescriptionList().add(
                    BlancoCgSourceUtil.escapeStringAsLangDoc(BlancoCgSupportedLang.JAVA, fBundle.getXml2javaclassFieldDefault(argFieldStructure
                            .getDefault())));
            if (argClassStructure.getAdjustDefaultValue() == false) {
                // If the variant of the default value is off, the value on the definition sheet is adopted as it is.
                field.setDefault(argFieldStructure.getDefault());
            } else {

                if (type.equals("java.lang.String")) {
                    // Adds double-quotes.
                    field.setDefault("\""
                            + BlancoJavaSourceUtil
                                    .escapeStringAsJavaSource(argFieldStructure
                                            .getDefault()) + "\"");
                } else if (type.equals("boolean") || type.equals("short")
                        || type.equals("int") || type.equals("long")) {
                    field.setDefault(argFieldStructure.getDefault());
                } else if (type.equals("java.lang.Boolean")
                        || type.equals("java.lang.Integer")
                        || type.equals("java.lang.Long")) {
                    field.setDefault("new "
                            + BlancoNameUtil.trimJavaPackage(type) + "("
                            + argFieldStructure.getDefault() + ")");
                } else if (type.equals("java.lang.Short")) {
                    field.setDefault("new "
                            + BlancoNameUtil.trimJavaPackage(type)
                            + "((short) " + argFieldStructure.getDefault()
                            + ")");
                } else if (type.equals("java.math.BigDecimal")) {
                    fCgSourceFile.getImportList().add("java.math.BigDecimal");
                    // Converts a string to BigDecimal.
                    field.setDefault("new BigDecimal(\""
                            + argFieldStructure.getDefault() + "\")");
                } else if (type.equals("java.util.List")
                        || type.equals("java.util.ArrayList")) {
                    // In the case of ArrayList, it will adopt the given character as is.
                    // TODO: In the case of second generation blancoValueObject adoption, all class imports are appropriate.
                    fCgSourceFile.getImportList().add(type);
                    field.setDefault(argFieldStructure.getDefault());
                } else {
                    throw new IllegalArgumentException(fMsg.getMbvoji05(
                            argClassStructure.getName(), argFieldStructure
                                    .getName(), argFieldStructure.getDefault(),
                            type));
                }
            }
        }

        /* Sets the annotation for the method. */
        List annotationList = argFieldStructure.getAnnotationList();
        if (annotationList != null && annotationList.size() > 0) {
            field.getAnnotationList().addAll(annotationList);
            System.out.println("/* tueda */ method annotation = " + field.getAnnotationList().get(0));
        }
    }

    /**
     * Generates a set method.
     *
     * @param argFieldStructure
     *            Field information.
     */
    private void buildMethodSet(
            final BlancoValueObjectClassStructure argClassStructure,
            final BlancoValueObjectFieldStructure argFieldStructure) {
        // Generates a setter method for each field.
        final BlancoCgMethod method = fCgFactory.createMethod("set"
                + getFieldNameAdjustered(argClassStructure, argFieldStructure),
                fBundle.getXml2javaclassSetJavadoc01(argFieldStructure
                        .getName()));
        fCgClass.getMethodList().add(method);

        // JavaDoc configuration of the method.
        if (argFieldStructure.getDescription() != null) {
            method.getLangDoc().getDescriptionList().add(
                    fBundle.getXml2javaclassSetJavadoc02(argFieldStructure
                            .getDescription()));
        }
        for (String line : argFieldStructure.getDescriptionList()) {
            method.getLangDoc().getDescriptionList().add(line);
        }

        BlancoCgParameter cgParameter = fCgFactory.createParameter("arg"
                        + getFieldNameAdjustered(argClassStructure,
                argFieldStructure),
                argFieldStructure.getType(),
                fBundle.getXml2javaclassSetArgJavadoc(argFieldStructure
                        .getName()));
        method.getParameterList().add(cgParameter);
        if (BlancoStringUtil.null2Blank(argFieldStructure.getGeneric()).length() > 0) {
            cgParameter.getType().setGenerics(argFieldStructure.getGeneric());
        }

        // Method implementation.
        method.getLineList().add(
                "f"
                        + getFieldNameAdjustered(argClassStructure,
                                argFieldStructure)
                        + " = "
                        + "arg"
                        + getFieldNameAdjustered(argClassStructure,
                                argFieldStructure) + ";");
    }

    /**
     * Generates a get method.
     *
     * @param argFieldStructure
     *            Field information.
     */
    private void buildMethodGet(
            final BlancoValueObjectClassStructure argClassStructure,
            final BlancoValueObjectFieldStructure argFieldStructure) {
        // Generates a getter method for each field.
        final BlancoCgMethod method = fCgFactory.createMethod("get"
                + getFieldNameAdjustered(argClassStructure, argFieldStructure),
                fBundle.getXml2javaclassGetJavadoc01(argFieldStructure
                        .getName()));
        fCgClass.getMethodList().add(method);

        // JavaDoc configuration of the method.
        if (argFieldStructure.getDescription() != null) {
            method.getLangDoc().getDescriptionList().add(
                    fBundle.getXml2javaclassGetJavadoc02(argFieldStructure
                            .getDescription()));
        }
        for (String line : argFieldStructure.getDescriptionList()) {
            method.getLangDoc().getDescriptionList().add(line);
        }
        if (argFieldStructure.getDefault() != null) {
            method.getLangDoc().getDescriptionList().add(
                    BlancoCgSourceUtil.escapeStringAsLangDoc(BlancoCgSupportedLang.JAVA, fBundle.getXml2javaclassGetDefaultJavadoc(argFieldStructure
                            .getDefault())));
        }

        BlancoCgReturn cgReturn = fCgFactory.createReturn(argFieldStructure.getType(), fBundle.getXml2javaclassGetReturnJavadoc(argFieldStructure.getName()));
        method.setReturn(cgReturn);
        if (BlancoStringUtil.null2Blank(argFieldStructure.getGeneric()).length() > 0) {
            cgReturn.getType().setGenerics(argFieldStructure.getGeneric());
        }

        // Method implementation.
        method.getLineList().add(
                "return f"
                        + getFieldNameAdjustered(argClassStructure,
                                argFieldStructure) + ";");
    }

    /**
     * Generates toString method.
     *
     * @param argClassStructure
     *            Class information.
     */
    private void buildMethodToString(
            final BlancoValueObjectClassStructure argClassStructure) {
        final BlancoCgMethod method = fCgFactory.createMethod("toString",
                "Gets the string representation of this value object.");
        fCgClass.getMethodList().add(method);

        method.getLangDoc().getDescriptionList().add("<P>Precautions for use</P>");
        method.getLangDoc().getDescriptionList().add("<UL>");
        method.getLangDoc().getDescriptionList().add(
                "<LI>Only the shallow range of the object will be subject to the stringification process.");
        method.getLangDoc().getDescriptionList().add(
                "<LI>Do not use this method if the object has a circular reference.");
        method.getLangDoc().getDescriptionList().add("</UL>");
        method.setReturn(fCgFactory.createReturn("java.lang.String",
                "String representation of a value object."));
        method.getAnnotationList().add("Override");

        final List<java.lang.String> listLine = method.getLineList();

        listLine.add("final StringBuffer buf = new StringBuffer();");
        listLine.add("buf.append(\"" + argClassStructure.getPackage() + "."
                + argClassStructure.getName() + "[\");");
        for (int indexField = 0; indexField < argClassStructure.getFieldList()
                .size(); indexField++) {
            final BlancoValueObjectFieldStructure field = (BlancoValueObjectFieldStructure) argClassStructure
                    .getFieldList().get(indexField);

            final String fieldNameAdjustered = (argClassStructure
                    .getAdjustFieldName() == false ? field.getName()
                    : BlancoNameAdjuster.toClassName(field.getName()));

            if (field.getType().endsWith("[]") == false) {
                listLine.add("buf.append(\"" + (indexField == 0 ? "" : ",")
                        + field.getName() + "=\" + f" + fieldNameAdjustered
                        + ");");
            } else {
                // 2006.05.31 t.iga In the case of arrays, it is necessary to first check whether the array itself is null or not.
                listLine.add("if (f" + fieldNameAdjustered + " == null) {");
                // If it is the 0th item, it will be given special treatment without a comma.
                listLine.add("buf.append(" + (indexField == 0 ? "\"" :
                // If it is not the 0th, a comma is always given.
                        "\",") + field.getName() + "=null\");");
                listLine.add("} else {");

                // In the case of arrays, uses deep toString.
                listLine.add("buf.append("
                // If it is the 0th item, it will be given special treatment without a comma.
                        + (indexField == 0 ? "\"" :
                        // If it is not the 0th, a comma is always given.
                                "\",") + field.getName() + "=[\");");
                listLine.add("for (int index = 0; index < f"
                        + fieldNameAdjustered + ".length; index++) {");
                // 2006.05.31 t.iga
                // To make it similar to toString in ArrayList, etc., adds a half-width space after the comma.
                listLine.add("buf.append((index == 0 ? \"\" : \", \") + f"
                        + fieldNameAdjustered + "[index]);");
                listLine.add("}");
                listLine.add("buf.append(\"]\");");
                listLine.add("}");
            }
        }
        listLine.add("buf.append(\"]\");");
        listLine.add("return buf.toString();");
    }

    /**
     * Gets the adjusted field name.
     *
     * @param argFieldStructure
     *            Field information.
     * @return Adjusted field name.
     */
    private String getFieldNameAdjustered(
            final BlancoValueObjectClassStructure argClassStructure,
            final BlancoValueObjectFieldStructure argFieldStructure) {
        return (argClassStructure.getAdjustFieldName() == false ? argFieldStructure
                .getName()
                : BlancoNameAdjuster.toClassName(argFieldStructure.getName()));
    }
}
