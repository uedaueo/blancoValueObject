/*
 * このソースコードは blanco Frameworkにより自動生成されました。
 */
package blanco.sample.valueobject;

/**
 * バリューオブジェクトのサンプル。このクラスは単にサンプルです。実際の動作には利用されません。
 */
public class PhpSampleSub extends PhpSample {
    /**
     * override
     *
     * フィールド: [stringField1]。
     */
    private String fstringField1;

    /**
     * フィールド: [stringField3]。
     */
    private String fstringField3;

    /**
     * packageが付与されることを確認
     *
     * フィールド: [objectField1]。
     */
    private PhpSample fobjectField1;

    /**
     * フィールド [stringField1] の値を設定します。
     *
     * フィールドの説明: [override]。
     *
     * @param argstringField1 フィールド[stringField1]に設定する値。
     */
    public void setstringField1(final String argstringField1) {
        fstringField1 = argstringField1;
    }

    /**
     * フィールド [stringField1] の値を取得します。
     *
     * フィールドの説明: [override]。
     *
     * @return フィールド[stringField1]から取得した値。
     */
    public String getstringField1() {
        return fstringField1;
    }

    /**
     * フィールド [stringField3] の値を設定します。
     *
     * @param argstringField3 フィールド[stringField3]に設定する値。
     */
    public void setstringField3(final String argstringField3) {
        fstringField3 = argstringField3;
    }

    /**
     * フィールド [stringField3] の値を取得します。
     *
     * @return フィールド[stringField3]から取得した値。
     */
    public String getstringField3() {
        return fstringField3;
    }

    /**
     * フィールド [objectField1] の値を設定します。
     *
     * フィールドの説明: [packageが付与されることを確認]。
     *
     * @param argobjectField1 フィールド[objectField1]に設定する値。
     */
    public void setobjectField1(final PhpSample argobjectField1) {
        fobjectField1 = argobjectField1;
    }

    /**
     * フィールド [objectField1] の値を取得します。
     *
     * フィールドの説明: [packageが付与されることを確認]。
     *
     * @return フィールド[objectField1]から取得した値。
     */
    public PhpSample getobjectField1() {
        return fobjectField1;
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
    public void copyTo(final PhpSampleSub target) {
        if (target == null) {
            throw new IllegalArgumentException("Bug: PhpSampleSub#copyTo(target): argument 'target' is null");
        }

        // No needs to copy parent class.

        // Name: fstringField1
        // Type: java.lang.String
        target.fstringField1 = this.fstringField1;
        // Name: fstringField3
        // Type: java.lang.String
        target.fstringField3 = this.fstringField3;
        // Name: fobjectField1
        // Type: blanco.sample.valueobject.PhpSample
        // フィールド[fobjectField1]はサポート外の型[blanco.sample.valueobject.PhpSample]です。
    }
}
