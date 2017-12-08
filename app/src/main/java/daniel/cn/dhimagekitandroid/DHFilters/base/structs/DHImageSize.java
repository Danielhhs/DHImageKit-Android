package daniel.cn.dhimagekitandroid.DHFilters.base.structs;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHImageSize {
    public int width;
    public int height;

    public DHImageSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static DHImageSize zeroSize() {
        return new DHImageSize(0, 0);
    }
}
