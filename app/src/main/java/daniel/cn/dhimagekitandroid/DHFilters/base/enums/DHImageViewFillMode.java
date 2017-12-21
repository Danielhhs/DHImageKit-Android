package daniel.cn.dhimagekitandroid.DHFilters.base.enums;

/**
 * Created by huanghongsen on 2017/12/20.
 */

public enum DHImageViewFillMode {
    Stretch(0),
    PreserveAspectRatio(1),
    PreserveAspectRatioAndFill(2);

    private int index;
    private DHImageViewFillMode(int i) {index = i;}
}
