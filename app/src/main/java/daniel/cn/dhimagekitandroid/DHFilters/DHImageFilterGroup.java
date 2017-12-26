package daniel.cn.dhimagekitandroid.DHFilters;

import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;

/**
 * Created by huanghongsen on 2017/12/6.
 */

public class DHImageFilterGroup extends GPUImageFilterGroup implements IDHImageUpdatable {

    private double strength;

    public void updateWithStrength(float strength) {
        this.strength = strength;
        for (int i = 0; i < mFilters.size(); i++) {
            IDHImageUpdatable updatable = (IDHImageUpdatable) mFilters.get(i);
            updatable.updateWithStrength(strength);
        }
    }

    @Override
    public void updateWithPercent(float percent) {

    }

    public void updateWithInput(float input) {

    }

    @Override
    public float getMinValue() {
        return 0;
    }

    @Override
    public float getMaxValue() {
        return 0;
    }

    @Override
    public float getInitialValue() {
        return 0;
    }

    @Override
    public float getCurrentValue() {
        return 0;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }
}
