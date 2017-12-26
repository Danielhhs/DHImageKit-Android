package daniel.cn.dhimagekitandroid.DHFilters.base.structs;

/**
 * Created by huanghongsen on 2017/12/26.
 */

public class DHImageFilterParameters {
    public float minValue;
    public float maxValue;
    public float initialValue;
    public float startValue;

    public DHImageFilterParameters(float minValue, float maxValue, float initialValue) {
        this(minValue, maxValue, initialValue, initialValue);
    }

    public DHImageFilterParameters(float minValue, float maxValue, float initialValue, float startValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initialValue = initialValue;
        this.startValue = startValue;
    }
}
