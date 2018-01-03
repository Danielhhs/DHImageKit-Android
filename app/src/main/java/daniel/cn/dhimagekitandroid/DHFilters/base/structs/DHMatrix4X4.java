package daniel.cn.dhimagekitandroid.DHFilters.base.structs;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHMatrix4X4 {
    public DHVector4 one;
    public DHVector4 two;
    public DHVector4 three;
    public DHVector4 four;

    public DHMatrix4X4(DHVector4 one, DHVector4 two, DHVector4 three, DHVector4 four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    public static DHMatrix4X4 matrixFromArray(float[] matrix) {
        if (matrix.length != 16) {
            throw new RuntimeException("The length of the array must be 16");
        }
        DHVector4 one = new DHVector4(matrix[0], matrix[1], matrix[2], matrix[3]);
        DHVector4 two = new DHVector4(matrix[4], matrix[5], matrix[6], matrix[7]);
        DHVector4 three = new DHVector4(matrix[8], matrix[9], matrix[10], matrix[11]);
        DHVector4 four = new DHVector4(matrix[12], matrix[13], matrix[14], matrix[15]);

        return new DHMatrix4X4(one, two, three, four);
    }

    public static DHMatrix4X4 identityMatrix() {
        DHVector4 one = new DHVector4(1.f, 0.f, 0.f, 0.f);
        DHVector4 two = new DHVector4(0.f, 1.f, 0.f, 0.f);
        DHVector4 three = new DHVector4(0.f, 0.f, 1.f, 0.f);
        DHVector4 four = new DHVector4(0.f, 0.f, 0.f, 1.f);
        DHMatrix4X4 matrix = new DHMatrix4X4(one, two, three, four);
        return matrix;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof DHMatrix4X4) {
            DHMatrix4X4 anotherMatrix = (DHMatrix4X4)obj;
            return this.one.equals(anotherMatrix.one) && this.two.equals(anotherMatrix.two) && this.three.equals(anotherMatrix.three) && this.four.equals(anotherMatrix.four);
        }
        return false;
    }

    public float[] toArray() {
        float array[] = new float[16];
        one.putIntoArray(array, 0);
        two.putIntoArray(array, 4);
        three.putIntoArray(array, 8);
        four.putIntoArray(array, 12);
        return array;
    }
}
