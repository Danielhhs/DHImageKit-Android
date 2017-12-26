package daniel.cn.dhimagekitandroid.DHFilters;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by huanghongsen on 2017/12/6.
 */

public class DHImageBaseFilter extends GPUImageFilter implements IDHImageUpdatable {
    protected int strengthUniform;
    private float minValue;
    private float maxValue;
    private float initialValue;

    public DHImageBaseFilter() {}

    public DHImageBaseFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
    }

    public void onInit(){
        super.onInit();
        strengthUniform = GLES20.glGetUniformLocation(mGLProgId, "strength");
    }

    public void onInitialized() {
        super.onInitialized();
        updateWithStrength(1.f);
    }

    public void updateWithInput(float input) {

    }

    @Override
    public void updateWithStrength(float strength) {
        setFloat(strengthUniform, strength);
    }

    @Override
    public void updateWithPercent(float percent) {

    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getInitialValue() {
        return initialValue;
    }

    @Override
    public float getCurrentValue() {
        return 0;
    }

    public void setInitialValue(float initialValue) {
        this.initialValue = initialValue;
    }
}
