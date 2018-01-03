package daniel.cn.dhimagekitandroid.DHFilters.base.structs;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHVector4 {
    public float one;
    public float two;
    public float three;
    public float four;

    public DHVector4() {
        this(0.f, 0.f, 0.f, 0.f);
    }

    public DHVector4(float one, float two, float three, float four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    public float[] toArray() {
        float array[] = new float[4];
        array[0] = one;
        array[1] = two;
        array[2] = three;
        array[3] = four;
        return array;
    }

    public void putIntoArray(float[] array, int startIndex) {
        array[startIndex] = one;
        array[startIndex + 1] = two;
        array[startIndex + 2] = three;
        array[startIndex + 3] = four;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof DHVector4) {
            DHVector4 anotherVector = (DHVector4)obj;
            return this.one == anotherVector.one && this.two == anotherVector.two && this.three == anotherVector.three && this.four == anotherVector.four;
        }
        return false;
    }
}
