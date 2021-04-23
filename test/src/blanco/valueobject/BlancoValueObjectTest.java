/*
 * blanco Framework
 * Copyright (C) 2004-2020 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.valueobject;

import blanco.valueobject.task.BlancoValueObjectProcessImpl;
import blanco.valueobject.task.valueobject.BlancoValueObjectProcessInput;
import org.junit.Test;

import java.io.IOException;

/**
 * Java言語用の生成試験。
 *
 * @author IGA Tosiki
 * @author tueda
 */
public class BlancoValueObjectTest {

    @Test
    public void testBlancoValueObject() {
        BlancoValueObjectProcessInput input = new BlancoValueObjectProcessInput();
        input.setMetadir("meta/objects2");
        input.setEncoding("UTF-8");
        input.setSheetType("php");
        input.setTmpdir("tmpTest2");
        input.setTargetdir("sample/blanco");
        input.setTargetStyle("maven");
        input.setVerbose(true);
        input.setLineSeparator("LF");
        input.setPackageSuffix("blanco");
        input.setVoPackageSuffix("blanco");

        BlancoValueObjectProcessImpl imple = new BlancoValueObjectProcessImpl();
        try {
            imple.execute(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        input = new BlancoValueObjectProcessInput();
        input.setMetadir("meta/objects");
        input.setEncoding("UTF-8");
        input.setSheetType("php");
        input.setTmpdir("tmpTest");
        input.setTargetdir("sample/blanco");
        input.setTargetStyle("maven");
        input.setVerbose(true);
        input.setLineSeparator("LF");
        input.setPackageSuffix("blanco");
        input.setSearchTmpdir("tmpTest2");
        input.setVoPackageSuffix("blanco");

        imple = new BlancoValueObjectProcessImpl();
        try {
            imple.execute(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIgnoreDefaultTest() {
        // ignoreDefault のテスト
        BlancoValueObjectProcessInput input = new BlancoValueObjectProcessInput();
        input.setMetadir("meta/objects3");
        input.setEncoding("UTF-8");
        input.setSheetType("php");
        input.setTmpdir("tmpTest3");
        input.setTargetdir("sample/blanco");
        input.setTargetStyle("maven");
        input.setVerbose(true);
        input.setLineSeparator("LF");
        input.setPackageSuffix("blanco");
        input.setSearchTmpdir("tmpTest");
        input.setVoPackageSuffix("blanco");
        input.setIgnoreDefault(true);

        BlancoValueObjectProcessImpl imple = new BlancoValueObjectProcessImpl();
        try {
            imple.execute(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJavaInportAnnotationTest() {
        BlancoValueObjectProcessInput input = new BlancoValueObjectProcessInput();
        input.setMetadir("meta/objects");
        input.setEncoding("UTF-8");
        input.setSheetType("php");
        input.setTmpdir("tmpTest");
        input.setTargetdir("sample/blanco");
        input.setTargetStyle("maven");
        input.setVerbose(true);
        input.setLineSeparator("LF");
        input.setPackageSuffix("blanco");
//        input.setSearchTmpdir("tmpTest");
        input.setVoPackageSuffix("blanco");
        input.setIgnoreImport(true);
        input.setIgnoreAnnotation(true);

        BlancoValueObjectProcessImpl imple = new BlancoValueObjectProcessImpl();
        try {
            imple.execute(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
