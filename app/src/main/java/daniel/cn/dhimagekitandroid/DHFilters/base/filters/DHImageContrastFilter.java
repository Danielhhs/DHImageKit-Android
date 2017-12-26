package daniel.cn.dhimagekitandroid.DHFilters.base.filters;

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
        super(DH_VERTEX_SHADER_STRING, DH_BRIGHTNESS_FRAGMENT_SHADER);

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
    public float getMinValue() {
        return 0.f;
    }

    @Override
    public float getMaxValue() {
        return 4.f;
    }

    @Override
    public float getInitialValue() {
        return 1.f;
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
