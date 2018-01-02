package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;

/**
 * Created by huanghongsen on 2018/1/2.
 */

public class DHImageShadowFilter extends DHImageFilter {
    public static String DH_SHADOW_FRAGMENT_SHADER = "precision highp float;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform float shadows;\n" +
            " uniform float strength;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " const vec3 luminanceWeight = vec3(0.3, 0.3, 0.3);\n" +
            " const float shadowsLuminance = 0.66;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     float luminance = dot(textureColor.rgb, luminanceWeight);\n" +
            "     \n" +
            "     if (luminance < shadowsLuminance) {\n" +
            "         gl_FragColor = vec4(textureColor.rgb * shadows, textureColor.a);\n" +
            "     } else {\n" +
            "         gl_FragColor = textureColor;\n" +
            "     }\n" +
            " }";

    protected int shadowUniform;
    private float shadow;

    public DHImageShadowFilter() {
        this(0.9f, 1.1f, 1.f);
    }

    public DHImageShadowFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageShadowFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_SHADOW_FRAGMENT_SHADER, minValue, maxValue, initialValue);
        shadowUniform = filterProgram.getUniformIndex("shadows");

        setShadow(initialValue);
        updateWithStrength(1.f);
    }

    public float getShadow() {
        return shadow;
    }

    public void setShadow(float shadow) {
        this.shadow = shadow;
        setFloatUniform(shadow, shadowUniform, filterProgram);
    }

    @Override
    public float getCurrentValue() {
        return shadow;
    }

    @Override
    public void updateWithInput(float input) {
        setShadow(input);
    }
}
