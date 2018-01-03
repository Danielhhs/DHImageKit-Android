package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import java.util.ArrayList;
import java.util.List;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterBase;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilterGroup;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageTwoInputFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageValues;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImagePicture;

/**
 * Created by huanghongsen on 2018/1/3.
 */

public class DHImageTiltShiftFilter extends DHImageFilterGroup implements IDHImageValues {
    protected DHImageGaussianBlurFilter blurFilter;
    protected DHImageFilter tiltShiftFilter;

    private float blurRadiusInPixel;
    private float focusFallOffRate;
    private float maskAlpha;

    private float minValue, maxValue, initialValue;

    public DHImageTiltShiftFilter(float minValue, float maxValue, float initialValue) {
        super();
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initialValue = initialValue;

        blurFilter = new DHImageGaussianBlurFilter();
        addFilter(blurFilter);

        tiltShiftFilter = new DHImageTwoInputFilter(tiltShiftFilterFragmentShaderString());
        addFilter(tiltShiftFilter);

        blurFilter.addTarget(tiltShiftFilter, 1);

        List<DHImageFilterBase> initialFilters = new ArrayList<>();
        initialFilters.add(blurFilter);
        initialFilters.add(tiltShiftFilter);

        setInitialFilters(initialFilters);
        setTerminalFilter(tiltShiftFilter);

        setFocusFallOffRate(0.2f);
        setBlurRadiusInPixel(5.f);
    }

    public void showMask(DHImagePicture picture) {

    }

    public void hideMask(DHImagePicture picture, float duration) {

    }

    public String tiltShiftFilterFragmentShaderString() {
        return null;
    }

    public DHImageFilter getTiltShiftFilter() {
        return tiltShiftFilter;
    }

    public float getBlurRadiusInPixel() {
        return blurRadiusInPixel;
    }

    public float getFocusFallOffRate() {
        return focusFallOffRate;
    }

    public float getMaskAlpha() {
        return maskAlpha;
    }

    public void setBlurRadiusInPixel(float blurRadiusInPixel) {
        this.blurRadiusInPixel = blurRadiusInPixel;
        blurFilter.setBlurRadiusInPixels(blurRadiusInPixel);
    }

    public void setFocusFallOffRate(float focusFallOffRate) {
        this.focusFallOffRate = focusFallOffRate;
        tiltShiftFilter.setFloatUniform(focusFallOffRate, "focusFallOffRate");
    }

    public void setMaskAlpha(float maskAlpha) {
        this.maskAlpha = maskAlpha;
        tiltShiftFilter.setFloatUniform(maskAlpha, "maskAlpha");
    }

    @Override
    public void updateWithPercent(float percent) {
        float input = (getMaxValue() - getMinValue()) * percent + getMinValue();
        updateWithInput(input);
    }

    @Override
    public float getMinValue() {
        return minValue;
    }

    @Override
    public float getMaxValue() {
        return maxValue;
    }

    @Override
    public float getInitialValue() {
        return initialValue;
    }

    @Override
    public float getCurrentValue() {
        return 0;
    }
}
