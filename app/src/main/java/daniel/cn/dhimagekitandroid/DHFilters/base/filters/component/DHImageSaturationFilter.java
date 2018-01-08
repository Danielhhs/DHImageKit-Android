package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import android.opengl.GLES20;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;

/**
 * Created by huanghongsen on 2018/1/2.
 */

public class DHImageSaturationFilter extends DHImageFilter {
    public static final String DH_SATURATION_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float saturation;\n" +
            " uniform lowp float strength;\n" +
            " \n" +
            " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    lowp float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "    lowp vec3 greyScaleColor = vec3(luminance);\n" +
            "    \n" +
            "\tlowp vec3 processedColor = mix(greyScaleColor, textureColor.rgb, saturation);\n" +
            "\tgl_FragColor = vec4(mix(textureColor.rgb, processedColor, strength), textureColor.w);\n" +
            "\t \n" +
            " }";

    private float saturation;
    protected int saturationUniform;

    public DHImageSaturationFilter() {
        this(0.f, 2.f, 1.f);
    }

    public DHImageSaturationFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageSaturationFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_SATURATION_FRAGMENT_SHADER, minValue, maxValue, initialValue);
        saturationUniform = filterProgram.getUniformIndex("saturation");
        setSaturation(initialValue);
        updateWithStrength(1.f);
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.Saturation;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
        setFloatUniform(saturation, saturationUniform, filterProgram);
    }

    @Override
    public void updateWithInput(float input) {
        setSaturation(input);
    }

    @Override
    public float getCurrentValue() {
        return saturation;
    }
}
