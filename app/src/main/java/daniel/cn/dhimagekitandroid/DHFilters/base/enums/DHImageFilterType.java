package daniel.cn.dhimagekitandroid.DHFilters.base.enums;

/**
 * Created by huanghongsen on 2017/12/29.
 */

public enum  DHImageFilterType {

    None("None", "无"),
    Brightness("Brightness", "亮度"),
    Contrast("Contrast", "对比度");

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
