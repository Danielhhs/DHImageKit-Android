package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;

/**
 * Created by huanghongsen on 2018/1/5.
 */

public class DHImageContrastBrightnessFilter extends DHImageFilter {
    public static String DH_CONTRAST_BRIGHTNESS_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float contrast;\n" +
            " uniform lowp float brightness;\n" +
            " uniform lowp float strength;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     highp vec4 contrastColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(textureColor.rgb, (contrastColor.rgb + vec3(brightness)), strength), contrastColor.w);\n" +
            " }";

    protected int brightnessUniform, contrastUniform;
    private float brightness, contrast;

    public DHImageContrastBrightnessFilter() {
        this(0.f, 1.f, 1.f);
    }

    public DHImageContrastBrightnessFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageContrastBrightnessFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_CONTRAST_BRIGHTNESS_FRAGMENT_SHADER);
        brightnessUniform = filterProgram.getUniformIndex("brightness");
        contrastUniform = filterProgram.getUniformIndex("contrast");

        setBrightness(0.f);
        setContrast(1.f);

        updateWithStrength(initialValue);
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
        setFloatUniform(brightness, brightnessUniform, filterProgram);
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
        setFloatUniform(contrast, contrastUniform, filterProgram);
    }
}
