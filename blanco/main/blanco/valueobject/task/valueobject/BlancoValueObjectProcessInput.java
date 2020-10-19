package blanco.valueobject.task.valueobject;

/**
 * 処理クラス [BlancoValueObjectProcess]の入力バリューオブジェクトクラスです。
 */
public class BlancoValueObjectProcessInput {
    /**
     * verboseモードで動作させるかどうか。
     *
     * フィールド: [verbose]。
     * デフォルト: [false]。
     */
    private boolean fVerbose = false;

    /**
     * メタディレクトリ。xlsファイルの格納先または xmlファイルの格納先を指定します。
     *
     * フィールド: [metadir]。
     */
    private String fMetadir;

    /**
     * 出力先フォルダを指定します。無指定の場合にはカレント直下のblancoを用います。
     *
     * フィールド: [targetdir]。
     * デフォルト: [blanco]。
     */
    private String fTargetdir = "blanco";

    /**
     * テンポラリディレクトリを指定します。無指定の場合にはカレント直下のtmpを用います。
     *
     * フィールド: [tmpdir]。
     * デフォルト: [tmp]。
     */
    private String fTmpdir = "tmp";

    /**
     * 自動生成するソースファイルの文字エンコーディングを指定します。
     *
     * フィールド: [encoding]。
     */
    private String fEncoding;

    /**
     * XML ルート要素のアノテーションを出力するかどうか。JDK 1.6 以降が必要。
     *
     * フィールド: [xmlrootelement]。
     * デフォルト: [false]。
     */
    private boolean fXmlrootelement = false;

    /**
     * meta定義書が期待しているプログラミング言語を指定します
     *
     * フィールド: [sheetType]。
     * デフォルト: [java]。
     */
    private String fSheetType = "java";

    /**
     * 出力先フォルダの書式を指定します。&amp;lt;br&amp;gt;\nblanco: [targetdir]/main&amp;lt;br&amp;gt;\nmaven: [targetdir]/main/java&amp;lt;br&amp;gt;\nfree: [targetdir](targetdirが無指定の場合はblanco/main)
     *
     * フィールド: [targetStyle]。
     * デフォルト: [blanco]。
     */
    private String fTargetStyle = "blanco";

    /**
     * 行末記号をしていします。LF=0x0a, CR=0x0d, CFLF=0x0d0x0a とします。LFがデフォルトです。
     *
     * フィールド: [lineSeparator]。
     * デフォルト: [LF]。
     */
    private String fLineSeparator = "LF";

    /**
     * 定義書で指定されたパッケージ名の後ろに追加するパッケージ文字列を指定します。
     *
     * フィールド: [packageSuffix]。
     */
    private String fPackageSuffix;

    /**
     * 定義書で指定されたパッケージ名を上書きします。
     *
     * フィールド: [overridePackage]。
     */
    private String fOverridePackage;

    /**
     * import文作成のために検索するtmpディレクトリをカンマ区切りで指定します。指定ディレクトリ直下のvalueobjectディレクトリの下にxmlを探しにいきます。
     *
     * フィールド: [searchTmpdir]。
     */
    private String fSearchTmpdir;

    /**
     * packageを探しにいくValueObject定義書を処理する際に指定されていたはずの packageSuffix を指定します。
     *
     * フィールド: [voPackageSuffix]。
     */
    private String fVoPackageSuffix;

    /**
     * packageを探しにいくValueObject定義書を処理する際に指定されていたはずの overridePackage を指定します。
     *
     * フィールド: [voOverridePackage]。
     */
    private String fVoOverridePackage;

    /**
     * Java向け以外のデフォルト値を無視します。
     *
     * フィールド: [ignoreDefault]。
     * デフォルト: [false]。
     */
    private boolean fIgnoreDefault = false;

    /**
     * Java向け以外のアノテーションを無視します。
     *
     * フィールド: [ignoreAnnotation]。
     * デフォルト: [false]。
     */
    private boolean fIgnoreAnnotation = false;

    /**
     * Java向け以外のインポートを無視します。
     *
     * フィールド: [ignoreImport]。
     * デフォルト: [false]。
     */
    private boolean fIgnoreImport = false;

    /**
     * フィールド [verbose] の値を設定します。
     *
     * フィールドの説明: [verboseモードで動作させるかどうか。]。
     *
     * @param argVerbose フィールド[verbose]に設定する値。
     */
    public void setVerbose(final boolean argVerbose) {
        fVerbose = argVerbose;
    }

    /**
     * フィールド [verbose] の値を取得します。
     *
     * フィールドの説明: [verboseモードで動作させるかどうか。]。
     * デフォルト: [false]。
     *
     * @return フィールド[verbose]から取得した値。
     */
    public boolean getVerbose() {
        return fVerbose;
    }

    /**
     * フィールド [metadir] の値を設定します。
     *
     * フィールドの説明: [メタディレクトリ。xlsファイルの格納先または xmlファイルの格納先を指定します。]。
     *
     * @param argMetadir フィールド[metadir]に設定する値。
     */
    public void setMetadir(final String argMetadir) {
        fMetadir = argMetadir;
    }

    /**
     * フィールド [metadir] の値を取得します。
     *
     * フィールドの説明: [メタディレクトリ。xlsファイルの格納先または xmlファイルの格納先を指定します。]。
     *
     * @return フィールド[metadir]から取得した値。
     */
    public String getMetadir() {
        return fMetadir;
    }

    /**
     * フィールド [targetdir] の値を設定します。
     *
     * フィールドの説明: [出力先フォルダを指定します。無指定の場合にはカレント直下のblancoを用います。]。
     *
     * @param argTargetdir フィールド[targetdir]に設定する値。
     */
    public void setTargetdir(final String argTargetdir) {
        fTargetdir = argTargetdir;
    }

    /**
     * フィールド [targetdir] の値を取得します。
     *
     * フィールドの説明: [出力先フォルダを指定します。無指定の場合にはカレント直下のblancoを用います。]。
     * デフォルト: [blanco]。
     *
     * @return フィールド[targetdir]から取得した値。
     */
    public String getTargetdir() {
        return fTargetdir;
    }

    /**
     * フィールド [tmpdir] の値を設定します。
     *
     * フィールドの説明: [テンポラリディレクトリを指定します。無指定の場合にはカレント直下のtmpを用います。]。
     *
     * @param argTmpdir フィールド[tmpdir]に設定する値。
     */
    public void setTmpdir(final String argTmpdir) {
        fTmpdir = argTmpdir;
    }

    /**
     * フィールド [tmpdir] の値を取得します。
     *
     * フィールドの説明: [テンポラリディレクトリを指定します。無指定の場合にはカレント直下のtmpを用います。]。
     * デフォルト: [tmp]。
     *
     * @return フィールド[tmpdir]から取得した値。
     */
    public String getTmpdir() {
        return fTmpdir;
    }

    /**
     * フィールド [encoding] の値を設定します。
     *
     * フィールドの説明: [自動生成するソースファイルの文字エンコーディングを指定します。]。
     *
     * @param argEncoding フィールド[encoding]に設定する値。
     */
    public void setEncoding(final String argEncoding) {
        fEncoding = argEncoding;
    }

    /**
     * フィールド [encoding] の値を取得します。
     *
     * フィールドの説明: [自動生成するソースファイルの文字エンコーディングを指定します。]。
     *
     * @return フィールド[encoding]から取得した値。
     */
    public String getEncoding() {
        return fEncoding;
    }

    /**
     * フィールド [xmlrootelement] の値を設定します。
     *
     * フィールドの説明: [XML ルート要素のアノテーションを出力するかどうか。JDK 1.6 以降が必要。]。
     *
     * @param argXmlrootelement フィールド[xmlrootelement]に設定する値。
     */
    public void setXmlrootelement(final boolean argXmlrootelement) {
        fXmlrootelement = argXmlrootelement;
    }

    /**
     * フィールド [xmlrootelement] の値を取得します。
     *
     * フィールドの説明: [XML ルート要素のアノテーションを出力するかどうか。JDK 1.6 以降が必要。]。
     * デフォルト: [false]。
     *
     * @return フィールド[xmlrootelement]から取得した値。
     */
    public boolean getXmlrootelement() {
        return fXmlrootelement;
    }

    /**
     * フィールド [sheetType] の値を設定します。
     *
     * フィールドの説明: [meta定義書が期待しているプログラミング言語を指定します]。
     *
     * @param argSheetType フィールド[sheetType]に設定する値。
     */
    public void setSheetType(final String argSheetType) {
        fSheetType = argSheetType;
    }

    /**
     * フィールド [sheetType] の値を取得します。
     *
     * フィールドの説明: [meta定義書が期待しているプログラミング言語を指定します]。
     * デフォルト: [java]。
     *
     * @return フィールド[sheetType]から取得した値。
     */
    public String getSheetType() {
        return fSheetType;
    }

    /**
     * フィールド [targetStyle] の値を設定します。
     *
     * フィールドの説明: [出力先フォルダの書式を指定します。&lt;br&gt;\nblanco: [targetdir]/main&lt;br&gt;\nmaven: [targetdir]/main/java&lt;br&gt;\nfree: [targetdir](targetdirが無指定の場合はblanco/main)]。
     *
     * @param argTargetStyle フィールド[targetStyle]に設定する値。
     */
    public void setTargetStyle(final String argTargetStyle) {
        fTargetStyle = argTargetStyle;
    }

    /**
     * フィールド [targetStyle] の値を取得します。
     *
     * フィールドの説明: [出力先フォルダの書式を指定します。&lt;br&gt;\nblanco: [targetdir]/main&lt;br&gt;\nmaven: [targetdir]/main/java&lt;br&gt;\nfree: [targetdir](targetdirが無指定の場合はblanco/main)]。
     * デフォルト: [blanco]。
     *
     * @return フィールド[targetStyle]から取得した値。
     */
    public String getTargetStyle() {
        return fTargetStyle;
    }

    /**
     * フィールド [lineSeparator] の値を設定します。
     *
     * フィールドの説明: [行末記号をしていします。LF=0x0a, CR=0x0d, CFLF=0x0d0x0a とします。LFがデフォルトです。]。
     *
     * @param argLineSeparator フィールド[lineSeparator]に設定する値。
     */
    public void setLineSeparator(final String argLineSeparator) {
        fLineSeparator = argLineSeparator;
    }

    /**
     * フィールド [lineSeparator] の値を取得します。
     *
     * フィールドの説明: [行末記号をしていします。LF=0x0a, CR=0x0d, CFLF=0x0d0x0a とします。LFがデフォルトです。]。
     * デフォルト: [LF]。
     *
     * @return フィールド[lineSeparator]から取得した値。
     */
    public String getLineSeparator() {
        return fLineSeparator;
    }

    /**
     * フィールド [packageSuffix] の値を設定します。
     *
     * フィールドの説明: [定義書で指定されたパッケージ名の後ろに追加するパッケージ文字列を指定します。]。
     *
     * @param argPackageSuffix フィールド[packageSuffix]に設定する値。
     */
    public void setPackageSuffix(final String argPackageSuffix) {
        fPackageSuffix = argPackageSuffix;
    }

    /**
     * フィールド [packageSuffix] の値を取得します。
     *
     * フィールドの説明: [定義書で指定されたパッケージ名の後ろに追加するパッケージ文字列を指定します。]。
     *
     * @return フィールド[packageSuffix]から取得した値。
     */
    public String getPackageSuffix() {
        return fPackageSuffix;
    }

    /**
     * フィールド [overridePackage] の値を設定します。
     *
     * フィールドの説明: [定義書で指定されたパッケージ名を上書きします。]。
     *
     * @param argOverridePackage フィールド[overridePackage]に設定する値。
     */
    public void setOverridePackage(final String argOverridePackage) {
        fOverridePackage = argOverridePackage;
    }

    /**
     * フィールド [overridePackage] の値を取得します。
     *
     * フィールドの説明: [定義書で指定されたパッケージ名を上書きします。]。
     *
     * @return フィールド[overridePackage]から取得した値。
     */
    public String getOverridePackage() {
        return fOverridePackage;
    }

    /**
     * フィールド [searchTmpdir] の値を設定します。
     *
     * フィールドの説明: [import文作成のために検索するtmpディレクトリをカンマ区切りで指定します。指定ディレクトリ直下のvalueobjectディレクトリの下にxmlを探しにいきます。]。
     *
     * @param argSearchTmpdir フィールド[searchTmpdir]に設定する値。
     */
    public void setSearchTmpdir(final String argSearchTmpdir) {
        fSearchTmpdir = argSearchTmpdir;
    }

    /**
     * フィールド [searchTmpdir] の値を取得します。
     *
     * フィールドの説明: [import文作成のために検索するtmpディレクトリをカンマ区切りで指定します。指定ディレクトリ直下のvalueobjectディレクトリの下にxmlを探しにいきます。]。
     *
     * @return フィールド[searchTmpdir]から取得した値。
     */
    public String getSearchTmpdir() {
        return fSearchTmpdir;
    }

    /**
     * フィールド [voPackageSuffix] の値を設定します。
     *
     * フィールドの説明: [packageを探しにいくValueObject定義書を処理する際に指定されていたはずの packageSuffix を指定します。]。
     *
     * @param argVoPackageSuffix フィールド[voPackageSuffix]に設定する値。
     */
    public void setVoPackageSuffix(final String argVoPackageSuffix) {
        fVoPackageSuffix = argVoPackageSuffix;
    }

    /**
     * フィールド [voPackageSuffix] の値を取得します。
     *
     * フィールドの説明: [packageを探しにいくValueObject定義書を処理する際に指定されていたはずの packageSuffix を指定します。]。
     *
     * @return フィールド[voPackageSuffix]から取得した値。
     */
    public String getVoPackageSuffix() {
        return fVoPackageSuffix;
    }

    /**
     * フィールド [voOverridePackage] の値を設定します。
     *
     * フィールドの説明: [packageを探しにいくValueObject定義書を処理する際に指定されていたはずの overridePackage を指定します。]。
     *
     * @param argVoOverridePackage フィールド[voOverridePackage]に設定する値。
     */
    public void setVoOverridePackage(final String argVoOverridePackage) {
        fVoOverridePackage = argVoOverridePackage;
    }

    /**
     * フィールド [voOverridePackage] の値を取得します。
     *
     * フィールドの説明: [packageを探しにいくValueObject定義書を処理する際に指定されていたはずの overridePackage を指定します。]。
     *
     * @return フィールド[voOverridePackage]から取得した値。
     */
    public String getVoOverridePackage() {
        return fVoOverridePackage;
    }

    /**
     * フィールド [ignoreDefault] の値を設定します。
     *
     * フィールドの説明: [Java向け以外のデフォルト値を無視します。]。
     *
     * @param argIgnoreDefault フィールド[ignoreDefault]に設定する値。
     */
    public void setIgnoreDefault(final boolean argIgnoreDefault) {
        fIgnoreDefault = argIgnoreDefault;
    }

    /**
     * フィールド [ignoreDefault] の値を取得します。
     *
     * フィールドの説明: [Java向け以外のデフォルト値を無視します。]。
     * デフォルト: [false]。
     *
     * @return フィールド[ignoreDefault]から取得した値。
     */
    public boolean getIgnoreDefault() {
        return fIgnoreDefault;
    }

    /**
     * フィールド [ignoreAnnotation] の値を設定します。
     *
     * フィールドの説明: [Java向け以外のアノテーションを無視します。]。
     *
     * @param argIgnoreAnnotation フィールド[ignoreAnnotation]に設定する値。
     */
    public void setIgnoreAnnotation(final boolean argIgnoreAnnotation) {
        fIgnoreAnnotation = argIgnoreAnnotation;
    }

    /**
     * フィールド [ignoreAnnotation] の値を取得します。
     *
     * フィールドの説明: [Java向け以外のアノテーションを無視します。]。
     * デフォルト: [false]。
     *
     * @return フィールド[ignoreAnnotation]から取得した値。
     */
    public boolean getIgnoreAnnotation() {
        return fIgnoreAnnotation;
    }

    /**
     * フィールド [ignoreImport] の値を設定します。
     *
     * フィールドの説明: [Java向け以外のインポートを無視します。]。
     *
     * @param argIgnoreImport フィールド[ignoreImport]に設定する値。
     */
    public void setIgnoreImport(final boolean argIgnoreImport) {
        fIgnoreImport = argIgnoreImport;
    }

    /**
     * フィールド [ignoreImport] の値を取得します。
     *
     * フィールドの説明: [Java向け以外のインポートを無視します。]。
     * デフォルト: [false]。
     *
     * @return フィールド[ignoreImport]から取得した値。
     */
    public boolean getIgnoreImport() {
        return fIgnoreImport;
    }

    /**
     * このバリューオブジェクトの文字列表現を取得します。
     *
     * <P>使用上の注意</P>
     * <UL>
     * <LI>オブジェクトのシャロー範囲のみ文字列化の処理対象となります。
     * <LI>オブジェクトが循環参照している場合には、このメソッドは使わないでください。
     * </UL>
     *
     * @return バリューオブジェクトの文字列表現。
     */
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("blanco.valueobject.task.valueobject.BlancoValueObjectProcessInput[");
        buf.append("verbose=" + fVerbose);
        buf.append(",metadir=" + fMetadir);
        buf.append(",targetdir=" + fTargetdir);
        buf.append(",tmpdir=" + fTmpdir);
        buf.append(",encoding=" + fEncoding);
        buf.append(",xmlrootelement=" + fXmlrootelement);
        buf.append(",sheetType=" + fSheetType);
        buf.append(",targetStyle=" + fTargetStyle);
        buf.append(",lineSeparator=" + fLineSeparator);
        buf.append(",packageSuffix=" + fPackageSuffix);
        buf.append(",overridePackage=" + fOverridePackage);
        buf.append(",searchTmpdir=" + fSearchTmpdir);
        buf.append(",voPackageSuffix=" + fVoPackageSuffix);
        buf.append(",voOverridePackage=" + fVoOverridePackage);
        buf.append(",ignoreDefault=" + fIgnoreDefault);
        buf.append(",ignoreAnnotation=" + fIgnoreAnnotation);
        buf.append(",ignoreImport=" + fIgnoreImport);
        buf.append("]");
        return buf.toString();
    }

    /**
     * このバリューオブジェクトを指定のターゲットに複写します。
     *
     * <P>使用上の注意</P>
     * <UL>
     * <LI>オブジェクトのシャロー範囲のみ複写処理対象となります。
     * <LI>オブジェクトが循環参照している場合には、このメソッドは使わないでください。
     * </UL>
     *
     * @param target target value object.
     */
    public void copyTo(final BlancoValueObjectProcessInput target) {
        if (target == null) {
            throw new IllegalArgumentException("Bug: BlancoValueObjectProcessInput#copyTo(target): argument 'target' is null");
        }

        // No needs to copy parent class.

        // Name: fVerbose
        // Type: boolean
        target.fVerbose = this.fVerbose;
        // Name: fMetadir
        // Type: java.lang.String
        target.fMetadir = this.fMetadir;
        // Name: fTargetdir
        // Type: java.lang.String
        target.fTargetdir = this.fTargetdir;
        // Name: fTmpdir
        // Type: java.lang.String
        target.fTmpdir = this.fTmpdir;
        // Name: fEncoding
        // Type: java.lang.String
        target.fEncoding = this.fEncoding;
        // Name: fXmlrootelement
        // Type: boolean
        target.fXmlrootelement = this.fXmlrootelement;
        // Name: fSheetType
        // Type: java.lang.String
        target.fSheetType = this.fSheetType;
        // Name: fTargetStyle
        // Type: java.lang.String
        target.fTargetStyle = this.fTargetStyle;
        // Name: fLineSeparator
        // Type: java.lang.String
        target.fLineSeparator = this.fLineSeparator;
        // Name: fPackageSuffix
        // Type: java.lang.String
        target.fPackageSuffix = this.fPackageSuffix;
        // Name: fOverridePackage
        // Type: java.lang.String
        target.fOverridePackage = this.fOverridePackage;
        // Name: fSearchTmpdir
        // Type: java.lang.String
        target.fSearchTmpdir = this.fSearchTmpdir;
        // Name: fVoPackageSuffix
        // Type: java.lang.String
        target.fVoPackageSuffix = this.fVoPackageSuffix;
        // Name: fVoOverridePackage
        // Type: java.lang.String
        target.fVoOverridePackage = this.fVoOverridePackage;
        // Name: fIgnoreDefault
        // Type: boolean
        target.fIgnoreDefault = this.fIgnoreDefault;
        // Name: fIgnoreAnnotation
        // Type: boolean
        target.fIgnoreAnnotation = this.fIgnoreAnnotation;
        // Name: fIgnoreImport
        // Type: boolean
        target.fIgnoreImport = this.fIgnoreImport;
    }
}
