package daniel.cn.dhimagekitandroid.DHFilters.base.structs;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHImageSize {
    public float width;
    public float height;

    public DHImageSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public static DHImageSize zeroSize() {
        return new DHImageSize(0.f, 0.f);
    }

    public boolean isZeroSize() {
        return width == 0.f && height == 0.f;
    }

    public boolean equals(DHImageSize size) {
        if (size == null) return false;
        return size.width == width && size.height == height;
    }
}
