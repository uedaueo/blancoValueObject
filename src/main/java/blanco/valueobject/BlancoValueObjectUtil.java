package blanco.valueobject;

import blanco.cg.BlancoCgSupportedLang;
import blanco.commons.util.BlancoStringUtil;
import blanco.valueobject.resourcebundle.BlancoValueObjectResourceBundle;
import blanco.valueobject.task.valueobject.BlancoValueObjectProcessInput;
import blanco.valueobject.valueobject.BlancoValueObjectClassStructure;

import java.io.File;
import java.util.*;

public class BlancoValueObjectUtil {

    public static boolean isVerbose = false;
    public static String packageSuffix = null;
    public static String overridePackage = null;
    public static String voPackageSuffix = null;
    public static String voOverridePackage = null;
    public static boolean ignoreDefault = false;
    public static boolean ignoreAnnotation = false;
    public static boolean ignoreImport = false;
    public static boolean preferJavaImport = true;

    public static HashMap<String, BlancoValueObjectClassStructure> objects = new HashMap<>();

    /**
     * blancoValueObjectのリソースバンドルオブジェクト。
     */
    public final static BlancoValueObjectResourceBundle fBundle = new BlancoValueObjectResourceBundle();

    public static Map<String, Integer> mapCommons = new HashMap<String, Integer>() {
        {put(fBundle.getMeta2xmlElementCommon(), BlancoCgSupportedLang.JAVA);}
        {put(fBundle.getMeta2xmlElementCommonCs(), BlancoCgSupportedLang.CS);}
        {put(fBundle.getMeta2xmlElementCommonJs(), BlancoCgSupportedLang.JS);}
        {put(fBundle.getMeta2xmlElementCommonVb(), BlancoCgSupportedLang.VB);}
        {put(fBundle.getMeta2xmlElementCommonPhp(), BlancoCgSupportedLang.PHP);}
        {put(fBundle.getMeta2xmlElementCommonRuby(), BlancoCgSupportedLang.RUBY);}
        {put(fBundle.getMeta2xmlElementCommonPython(), BlancoCgSupportedLang.PYTHON);}
    };


    public static void processValueObjects(final BlancoValueObjectProcessInput input) {
        if (isVerbose) {
            System.out.println("BlancoValueObjectUtil : processValueObjects start !");
        }

        /* tmpdir はユニーク */
        String baseTmpdir = input.getTmpdir();
        /* searchTmpdir はカンマ区切り */
        String tmpTmpdirs = input.getSearchTmpdir();
        List<String> searchTmpdirList = null;
        if (tmpTmpdirs != null && !tmpTmpdirs.equals(baseTmpdir)) {
            String[] searchTmpdirs = tmpTmpdirs.split(",");
            searchTmpdirList = new ArrayList<>(Arrays.asList(searchTmpdirs));
        }
        if (searchTmpdirList == null) {
            searchTmpdirList = new ArrayList<>();
        }
        searchTmpdirList.add(baseTmpdir);

        for (String tmpdir : searchTmpdirList) {
            searchTmpdir(tmpdir.trim());
        }
    }

    static private void searchTmpdir(String tmpdir) {

        // XML化された中間ファイルから情報を読み込む
        final File[] fileMeta3 = new File(tmpdir
                + BlancoValueObjectConstants.TARGET_SUBDIRECTORY)
                .listFiles();

        if (fileMeta3 == null) {
            System.out.println("!!! NO FILES in " + tmpdir
                    + BlancoValueObjectConstants.TARGET_SUBDIRECTORY);
            throw new IllegalArgumentException("!!! NO FILES in " + tmpdir
                    + BlancoValueObjectConstants.TARGET_SUBDIRECTORY);
        }

        for (int index = 0; index < fileMeta3.length; index++) {
            if (fileMeta3[index].getName().endsWith(".xml") == false) {
                continue;
            }

            BlancoValueObjectXmlParser parser = new BlancoValueObjectXmlParser();
//            parser.setVerbose(this.isVerbose());
            /*
             * まず始めにすべてのシートを検索して，クラス名とpackage名のリストを作ります．
             * php形式の定義書では，クラスを指定する際にpackage名が指定されていないからです．
             *
             */
            final BlancoValueObjectClassStructure[] structures = parser.parse(fileMeta3[index]);

            if (structures != null ) {
                for (int index2 = 0; index2 < structures.length; index2++) {
                    BlancoValueObjectClassStructure structure = structures[index2];
                    if (structure != null) {
                        if (isVerbose) {
                            System.out.println("searchTmpdir: " + structure.getName());
                        }
                        objects.put(structure.getName(), structure);
                    } else {
                        System.out.println("searchTmpdir: a structure is NULL!!!");
                    }
                }
            } else {
                System.out.println("searchTmpdir: structures are NULL!!!");
            }
        }
    }

    /**
     * Make canonical classname into Simple.
     *
     * @param argClassNameCanon
     * @return simpleName
     */
    static public String getSimpleClassName(final String argClassNameCanon) {
        if (argClassNameCanon == null) {
            return "";
        }

        String simpleName = "";
        final int findLastDot = argClassNameCanon.lastIndexOf('.');
        if (findLastDot == -1) {
            simpleName = argClassNameCanon;
        } else if (findLastDot != argClassNameCanon.length() - 1) {
            simpleName = argClassNameCanon.substring(findLastDot + 1);
        }
        return simpleName;
    }

    /**
     * Make canonical classname into packageName
     *
     * @param argClassNameCanon
     * @return
     */
    static public String getPackageName(final String argClassNameCanon) {
        if (argClassNameCanon == null) {
            return "";
        }

        String simpleName = "";
        final int findLastDot = argClassNameCanon.lastIndexOf('.');
        if (findLastDot > 0) {
            simpleName = argClassNameCanon.substring(0, findLastDot);
        }
        return simpleName;
    }

    public static String parsePhpTypes(String phpType, boolean isGeneric) {
        String javaType = phpType;
        if (BlancoStringUtil.null2Blank(phpType).length() != 0) {
            if ("boolean".equalsIgnoreCase(phpType)) {
                javaType = "java.lang.Boolean";
            } else
            if ("integer".equalsIgnoreCase(phpType)) {
                // integer 型は 64 bit に変換する
                javaType = "java.lang.Long";
            } else
            if ("double".equalsIgnoreCase(phpType)) {
                javaType = "java.lang.Double";
            } else
            if ("float".equalsIgnoreCase(phpType)) {
                javaType = "java.lang.Double";
            } else
            if ("string".equalsIgnoreCase(phpType)) {
                javaType = "java.lang.String";
            } else
            if ("datetime".equalsIgnoreCase(phpType)) {
                javaType = "java.util.Date";
            } else
            if ("array".equalsIgnoreCase(phpType)) {
                if (isGeneric) {
                    throw new IllegalArgumentException("Cannot use array for Generics.");
                } else {
                    javaType = "java.util.ArrayList";
                }
            } else
            if ("object".equalsIgnoreCase(phpType)) {
                javaType = "java.lang.Object";
            } else
            if ("ArrayList".equals(phpType)) { // 完全一致する場合のみ、CanonicalName に置き換える。
                javaType = "java.util.ArrayList";
            } else
            if ("List".equals(phpType)) { // 完全一致する場合のみ、CanonicalName に置き換える。
                javaType = "java.util.List";
            } else
            if ("Map".equals(phpType)) { // 完全一致する場合のみ、CanonicalName に置き換える。
                javaType = "java.util.Map";
            } else
            if ("HashMap".equals(phpType)) { // 完全一致する場合のみ、CanonicalName に置き換える。
                javaType = "java.util.HashMap";
            } else {
                /* この名前の package を探す */
                String packageName = BlancoValueObjectUtil.searchPackageBySimpleName(phpType);
                if (packageName != null) {
                    javaType = packageName + "." + phpType;
                }

                /* その他はそのまま記述する */
                if (isVerbose) {
                    System.out.println("/* tueda */ Unknown php type: " + javaType);
                }
            }
        }
        return javaType;
    }


    static public String searchPackageBySimpleName(String simpleName) {
        // パッケージ名の置き換えオプションが指定されていれば置き換え
        // Suffix があればそちらが優先です。
        String packageName = null;
        BlancoValueObjectClassStructure voStructure = objects.get(simpleName);
        if (voStructure != null) {
            packageName = voStructure.getPackage();
            if (BlancoValueObjectUtil.voPackageSuffix != null && BlancoValueObjectUtil.voPackageSuffix.length() > 0) {
                packageName = packageName + "." + BlancoValueObjectUtil.voPackageSuffix;
            } else if (BlancoValueObjectUtil.voOverridePackage != null && BlancoValueObjectUtil.voOverridePackage.length() > 0) {
                packageName = BlancoValueObjectUtil.voOverridePackage;
            }
        }
        return packageName;
    }
}
