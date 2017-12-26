package daniel.cn.dhimagekitandroid.DHFilters.base.filters;

import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;

/**
 * Created by huanghongsen on 2017/12/26.
 */

public class DHImageContrastFilter extends DHImageFilter {
    public static final String DH_BRIGHTNESS_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float contrast;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);\n" +
            " }";

    private int contrastUniform;
    private float contrast;

    public DHImageContrastFilter() {
        this(-1.f, 1.f, 0.f);
    }

    public DHImageContrastFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageContrastFilter(float minValue, float maxValue, float initialValue) {

        super(DH_VERTEX_SHADER_STRING, DH_BRIGHTNESS_FRAGMENT_SHADER, minValue, maxValue, initialValue);

        contrastUniform = filterProgram.getUniformIndex("contrast");
        setContrast(1.f);
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
        setFloatUniform(contrast, contrastUniform, filterProgram);
    }

    @Override
    public void updateWithInput(float input) {
        setContrast(input);
    }

    @Override
    public float getCurrentValue() {
        return contrast;
    }
}
