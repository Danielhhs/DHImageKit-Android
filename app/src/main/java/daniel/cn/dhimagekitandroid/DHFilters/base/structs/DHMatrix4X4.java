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
}
