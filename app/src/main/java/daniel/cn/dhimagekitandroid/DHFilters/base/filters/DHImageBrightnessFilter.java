package daniel.cn.dhimagekitandroid.DHFilters.base.filters;

/**
 * Created by huanghongsen on 2017/12/25.
 */

public class DHImageBrightnessFilter extends DHImageFilter {
    public static final String DH_BRIGHTNESS_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform lowp float brightness;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);\n" +
            " }";

    private int brightnessUniform;
    private float brightness;   //min: -1, max: 1, initial 0

    public DHImageBrightnessFilter() {
        super(DH_VERTEX_SHADER_STRING, DH_BRIGHTNESS_FRAGMENT_SHADER);
        brightnessUniform = filterProgram.getUniformIndex("brightness");
        setBrightness(0.f);
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
        setFloatUniform(brightness, brightnessUniform, filterProgram);
    }

    @Override
    public void updateWithInput(float input) {
        setBrightness(input);
    }

    @Override
    public float getMinValue() {
        return -1.f;
    }

    @Override
    public float getMaxValue() {
        return 1.f;
    }

    @Override
    public float getInitialValue() {
        return 0.f;
    }

    @Override
    public float getCurrentValue() {
        return brightness;
    }
}
