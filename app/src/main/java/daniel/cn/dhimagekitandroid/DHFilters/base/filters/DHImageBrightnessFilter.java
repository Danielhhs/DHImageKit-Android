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
    private float brightness;

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
}
