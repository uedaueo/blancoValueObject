/*
 * このソースコードは blanco Frameworkにより自動生成されました。
 */
package blanco.sample.valueobject;

import java.util.ArrayList;

/**
 * バリューオブジェクトのサンプル。このクラスは単にサンプルです。実際の動作には利用されません。
 */
public class ArraySample {
    /**
     * デフォルト値あり
     *
     * フィールド: [stringField1]。
     */
    private String fstringField1;

    /**
     * array&lt;SimpleObjectSample&gt;
     *
     * フィールド: [arrayObject]。
     */
    private ArrayList<blanco.sample.valueobject.SimpleSample> farrayObject;

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
     * フィールド [arrayObject] の値を設定します。
     *
     * フィールドの説明: [array<SimpleObjectSample>]。
     *
     * @param argarrayObject フィールド[arrayObject]に設定する値。
     */
    public void setarrayObject(final ArrayList<blanco.sample.valueobject.SimpleSample> argarrayObject) {
        farrayObject = argarrayObject;
    }

    /**
     * フィールド [arrayObject] の値を取得します。
     *
     * フィールドの説明: [array<SimpleObjectSample>]。
     *
     * @return フィールド[arrayObject]から取得した値。
     */
    public ArrayList<blanco.sample.valueobject.SimpleSample> getarrayObject() {
        return farrayObject;
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
    public void copyTo(final ArraySample target) {
        if (target == null) {
            throw new IllegalArgumentException("Bug: ArraySample#copyTo(target): argument 'target' is null");
        }

        // No needs to copy parent class.

        // Name: fstringField1
        // Type: java.lang.String
        target.fstringField1 = this.fstringField1;
        // Name: farrayObject
        // Type: java.util.ArrayList
        // フィールド[farrayObject]はサポート外の型[java.util.ArrayList]です。
    }
}
