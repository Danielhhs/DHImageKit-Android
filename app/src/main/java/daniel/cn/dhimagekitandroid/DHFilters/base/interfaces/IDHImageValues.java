package daniel.cn.dhimagekitandroid.DHFilters.base.interfaces;

/**
 * Created by huanghongsen on 2017/12/26.
 */

public interface IDHImageValues {

    public void updateWithPercent(float percent);
    public float getMinValue();
    public float getMaxValue();
    public float getInitialValue();
    public float getCurrentValue();
}
