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
     * Resource bundle object for blancoValueObject.
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

        /* tmpdir is unique. */
        String baseTmpdir = input.getTmpdir();
        /* searchTmpdir is comma separated. */
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

        // Reads information from XML-ized intermediate files.
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
             * The first step is to search all the sheets and make a list of class and package names.
             * This is because the package name is not specified when specifying a class in the PHP format definition.
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
                // Converts integer type to 64bit.
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
            if ("ArrayList".equals(phpType)) { // Replaces CanonicalName only if there is an exact match.
                javaType = "java.util.ArrayList";
            } else
            if ("List".equals(phpType)) { // Replaces CanonicalName only if there is an exact match.
                javaType = "java.util.List";
            } else
            if ("Map".equals(phpType)) { // Replaces CanonicalName only if there is an exact match.
                javaType = "java.util.Map";
            } else
            if ("HashMap".equals(phpType)) { // Replaces CanonicalName only if there is an exact match.
                javaType = "java.util.HashMap";
            } else {
                /* Searches for a package with this name. */
                String packageName = BlancoValueObjectUtil.searchPackageBySimpleName(phpType);
                if (packageName != null) {
                    javaType = packageName + "." + phpType;
                }

                /* Others are written as is. */
                if (isVerbose) {
                    System.out.println("/* tueda */ Unknown php type: " + javaType);
                }
            }
        }
        return javaType;
    }


    static public String searchPackageBySimpleName(String simpleName) {
        // Replaces the package name if the replace option is specified.
        // If Suffix is present, it takes precedence.
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
