package daniel.cn.dhimagekitandroid.DHFilters.base.enums;

/**
 * Created by huanghongsen on 2017/12/29.
 */

public enum  DHImageFilterType {

    None("None", "无"),
    Brightness("Brightness", "亮度"),
    Contrast("Contrast", "对比度"),
    Saturation("Saturation", "饱和度"),
    Warmth("Warmth", "暖色调"),
    Structure("Structure", "层级"),
    Vignette("Vignette", "黑边"),
    Fade("Fade", "渐隐"),
    Sharpen("Sharpen", "锐化"),
    Shadow("Shadow", "阴影"),
    Highlight("Highlight", "高亮"),
    GaussianBlur("Gaussian Blur", "高斯模糊"),
    LinearTiltShift("Linear Tilt Shift", "线性聚焦"),
    RadialTiltShift("Radial Tilt Shift", "径向聚焦"),
    Transform("Transform", "旋转"),
    Colors("Colors", "颜色"),
    ToneCurve("ToneCurve", "曲线");

    private String name;
    private String chineseName;

    private DHImageFilterType(String name, String chineseName) {
        this.name = name;
        this.chineseName = chineseName;
    }

    public String getName() {
        return name;
    }

    public String getChineseName() {
        return chineseName;
    }
}
