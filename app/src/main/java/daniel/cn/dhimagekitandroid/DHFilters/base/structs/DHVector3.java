package daniel.cn.dhimagekitandroid.DHFilters.base.structs;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHVector3 {
    public float one;
    public float two;
    public float three;

    public DHVector3() {
        this(0.f, 0.f, 0.f);
    }

    public DHVector3(float one, float two, float three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }
}
