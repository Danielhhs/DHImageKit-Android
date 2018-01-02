package daniel.cn.dhimagekitandroid.DHFilters.base.enums;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public enum DHImageRotationMode {
    NoRotation(0),
    Left(1),
    Right(2),
    FlipVertical(3),
    FlipHorizontal(4),
    RightFlipVertical(5),
    RightFlipHorizontal(6),
    Rotate180(7);

    private int index;
    private DHImageRotationMode(int i) {
        index = i;
    }

    public boolean needToSwapWidthAndHeight() {
        if (index == 1 || index == 2 || index == 5 || index == 6) {
            return true;
        }
        return false;
    }
}
