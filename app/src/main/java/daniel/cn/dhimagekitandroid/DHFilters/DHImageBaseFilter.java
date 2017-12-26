package daniel.cn.dhimagekitandroid.DHFilters;

import android.opengl.GLES20;

import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageUpdatable;
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


    public void setInitialValue(float initialValue) {
        this.initialValue = initialValue;
    }
}
