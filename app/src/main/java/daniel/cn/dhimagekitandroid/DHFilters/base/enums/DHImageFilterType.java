package daniel.cn.dhimagekitandroid.DHFilters.base.enums;

/**
 * Created by huanghongsen on 2017/12/26.
 */

public enum DHImageFilterType {

    Brightness("Brightness", "亮度"),
    Contrast("Contrast", "对比度");

    private String name;
    private String chineseName;

    private DHImageFilterType(String name, String chineseName) {
        this.name = name;
        this.chineseName = chineseName;
    }
}
