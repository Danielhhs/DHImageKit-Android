package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImagePoint;

/**
 * Created by huanghongsen on 2018/1/3.
 */

public class DHImageLinearTiltShiftFilter extends DHImageTiltShiftFilter {
    public static String DH_LINEAR_TILT_SHIFT_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " uniform highp float topFocusLevel;\n" +
            " uniform highp float bottomFocusLevel;\n" +
            " uniform highp float focusFallOffRate;\n" +
            " \n" +
            " uniform highp float maskAlpha;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 sharpImageColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     lowp vec4 blurredImageColor = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     \n" +
            "     lowp float blurIntensity = 1.0 - smoothstep(topFocusLevel - focusFallOffRate, topFocusLevel, textureCoordinate2.y);\n" +
            "     blurIntensity += smoothstep(bottomFocusLevel, bottomFocusLevel + focusFallOffRate, textureCoordinate2.y);\n" +
            "     \n" +
            "         lowp vec4 whiteBlend = vec4(1.0);\n" +
            "         lowp vec4 mixedColor = mix(sharpImageColor, blurredImageColor, blurIntensity);\n" +
            "         \n" +
            "         blurIntensity = clamp (blurIntensity, 0.0, 0.7 * maskAlpha);\n" +
            "         \n" +
            "         gl_FragColor = vec4(mix(mixedColor.rgb, whiteBlend.rgb, blurIntensity), whiteBlend.a);\n" +
            "    }\n";

    private float center;
    private float range;

    public DHImageLinearTiltShiftFilter() {
        this(01.f, 0.9f, 0.5f);
    }

    public DHImageLinearTiltShiftFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageLinearTiltShiftFilter(float minValue, float maxValue, float initialValue) {
        super(minValue, maxValue, initialValue);
        setCenter(initialValue);
        setRange(0.1f);
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.LinearTiltShift;
    }

    @Override
    public String tiltShiftFilterFragmentShaderString() {
        return DH_LINEAR_TILT_SHIFT_FRAGMENT_SHADER;
    }

    public float getCenter() {
        return center;
    }

    public void setCenter(float center) {
        this.center = center;
        tiltShiftFilter.setFloatUniform(this.center - this.range, "topFocusLevel");
        tiltShiftFilter.setFloatUniform(this.center + this.range, "bottomFocusLevel");
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;

        tiltShiftFilter.setFloatUniform(this.center - this.range, "topFocusLevel");
        tiltShiftFilter.setFloatUniform(this.center + this.range, "bottomFocusLevel");
    }

    @Override
    public float getCurrentValue() {
        return center;
    }

    @Override
    public void updateWithInput(float input) {
        setCenter(input);
    }
}
