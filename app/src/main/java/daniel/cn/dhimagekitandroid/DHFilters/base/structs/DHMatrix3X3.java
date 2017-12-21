package daniel.cn.dhimagekitandroid.DHFilters.base.structs;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHMatrix3X3 {
    public DHVector3 one;
    public DHVector3 two;
    public DHVector3 three;

    public DHMatrix3X3(DHVector3 one, DHVector3 two, DHVector3 three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }
}
