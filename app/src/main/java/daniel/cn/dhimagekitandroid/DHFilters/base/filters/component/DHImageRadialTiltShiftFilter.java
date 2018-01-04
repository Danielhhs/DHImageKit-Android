package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImagePoint;

/**
 * Created by huanghongsen on 2018/1/3.
 */

public class DHImageRadialTiltShiftFilter extends DHImageTiltShiftFilter {
    public static String DH_RADIAL_TILT_SHIFT_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " uniform highp vec2 center;\n" +
            " uniform highp float radius;\n" +
            " uniform highp float focusFallOffRate;\n" +
            " \n" +
            " uniform highp float maskAlpha;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 sharpImageColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     lowp vec4 blurredImageColor = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     \n" +
            "     highp float d = distance(center, textureCoordinate2);\n" +
            "     \n" +
            "     lowp float blurIntensity = smoothstep(radius - focusFallOffRate, radius, d);\n" +
            "     \n" +
            "     lowp vec4 whiteBlend = vec4(1.0);\n" +
            "     lowp vec4 mixedColor = mix(sharpImageColor, blurredImageColor, blurIntensity);\n" +
            "     \n" +
            "     blurIntensity = clamp (blurIntensity, 0.0, 0.7 * maskAlpha);\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(mixedColor.rgb, whiteBlend.rgb, blurIntensity), whiteBlend.a);\n" +
            " }";

    private float radius;
    private DHImagePoint center;

    public DHImageRadialTiltShiftFilter() {
        this(0.f, 0.5f, 0.2f);
    }

    public DHImageRadialTiltShiftFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageRadialTiltShiftFilter(float minValue, float maxValue, float initialValue) {
        super(minValue, maxValue, initialValue);

        setCenter(new DHImagePoint(0.5f, 0.5f));
        setRadius(initialValue);
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.RadialTiltShift;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        tiltShiftFilter.setFloatUniform(radius, "radius");
    }

    public DHImagePoint getCenter() {
        return center;
    }

    public void setCenter(DHImagePoint center) {
        this.center = center;
        tiltShiftFilter.setPointUniform(center, "center");
    }

    @Override
    public String tiltShiftFilterFragmentShaderString() {
        return DH_RADIAL_TILT_SHIFT_FRAGMENT_SHADER;
    }

    @Override
    public float getCurrentValue() {
        return radius;
    }

    @Override
    public void updateWithInput(float input) {
        setRadius(input);
    }
}
