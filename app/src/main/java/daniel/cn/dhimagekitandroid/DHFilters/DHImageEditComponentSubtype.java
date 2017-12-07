package daniel.cn.dhimagekitandroid.DHFilters;

/**
 * Created by huanghongsen on 2017/12/7.
 */

public enum DHImageEditComponentSubtype {
    NoTiltShift("无", 0),
    LinearTiltShift("线性", 1),
    RadialTiltShift("原型", 2);

    private DHImageEditComponentSubtype(String name, int index) {
        this.name = name;
        this.index = index;
    }

    private String name;
    private int index;

    public  String getName() {
        return this.name;
    }

    public int getIndex() {
        return this.index;
    }
}
