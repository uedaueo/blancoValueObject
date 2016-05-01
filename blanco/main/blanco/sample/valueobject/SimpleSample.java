/*
 * このソースコードは blanco Frameworkにより自動生成されました。
 */
package blanco.sample.valueobject;

/**
 * バリューオブジェクトのサンプル。このクラスは単にサンプルです。実際の動作には利用されません。
 */
public class SimpleSample {
    /**
     * デフォルト値あり
     *
     * フィールド: [stringField1]。
     */
    private String fstringField1;

    /**
     * デフォルト値なし
     *
     * フィールド: [stringField2]。
     */
    private String fstringField2;

    /**
     * フィールド [stringField1] の値を設定します。
     *
     * フィールドの説明: [デフォルト値あり]。
     *
     * @param argstringField1 フィールド[stringField1]に設定する値。
     */
    public void setstringField1(final String argstringField1) {
        fstringField1 = argstringField1;
    }

    /**
     * フィールド [stringField1] の値を取得します。
     *
     * フィールドの説明: [デフォルト値あり]。
     *
     * @return フィールド[stringField1]から取得した値。
     */
    public String getstringField1() {
        return fstringField1;
    }

    /**
     * フィールド [stringField2] の値を設定します。
     *
     * フィールドの説明: [デフォルト値なし]。
     *
     * @param argstringField2 フィールド[stringField2]に設定する値。
     */
    public void setstringField2(final String argstringField2) {
        fstringField2 = argstringField2;
    }

    /**
     * フィールド [stringField2] の値を取得します。
     *
     * フィールドの説明: [デフォルト値なし]。
     *
     * @return フィールド[stringField2]から取得した値。
     */
    public String getstringField2() {
        return fstringField2;
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
    public void copyTo(final SimpleSample target) {
        if (target == null) {
            throw new IllegalArgumentException("Bug: SimpleSample#copyTo(target): argument 'target' is null");
        }

        // No needs to copy parent class.

        // Name: fstringField1
        // Type: java.lang.String
        target.fstringField1 = this.fstringField1;
        // Name: fstringField2
        // Type: java.lang.String
        target.fstringField2 = this.fstringField2;
    }
}
