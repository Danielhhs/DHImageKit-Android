package daniel.cn.dhimagekitandroid.DHFilters;

/**
 * Created by huanghongsen on 2017/12/6.
 */

public interface IDHImageUpdatable {

    public void updateWithStrength(float strength);
    public void updateWithPercent(float percent);
    public void updateWithInput(float input);
    public float getMinValue();
    public float getMaxValue();
    public float getInitialValue();
    public float getCurrentValue();
}
