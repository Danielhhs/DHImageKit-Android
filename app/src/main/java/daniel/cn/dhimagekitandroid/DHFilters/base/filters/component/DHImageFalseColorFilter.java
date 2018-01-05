package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector3;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector4;

/**
 * Created by huanghongsen on 2018/1/5.
 */

public class DHImageFalseColorFilter extends DHImageFilter {
    public static String DH_FALSE_COLOR_FRAGMENT_SHADER = "precision lowp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform float intensity;\n" +
            " uniform vec3 firstColor;\n" +
            " uniform vec3 secondColor;\n" +
            " uniform float strength;\n" +
            " \n" +
            " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "     \n" +
            "     gl_FragColor = vec4( mix(textureColor.rgb, mix(firstColor.rgb, secondColor.rgb, luminance), strength), textureColor.a);\n" +
            " }\n";

    private DHVector3 firstColor, secondColor;
    protected int firstColorUniform, secondColorUniform;

    public DHImageFalseColorFilter() {
        this(0.f, 1.f, 0.f);
    }

    public DHImageFalseColorFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageFalseColorFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_FALSE_COLOR_FRAGMENT_SHADER);

        firstColorUniform = filterProgram.getUniformIndex("firstColor");
        secondColorUniform = filterProgram.getUniformIndex("secondColor");

        setFirstColor(new DHVector3(0.f, 0.f, 0.5f));
        setSecondColor(new DHVector3(1.f, 0.f, 0.f));
        updateWithInput(1.f);
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.FalseColor;
    }

    public DHVector3 getFirstColor() {
        return firstColor;
    }

    public void setFirstColor(DHVector3 firstColor) {
        this.firstColor = firstColor;
        setFloatVec3Uniform(firstColor, firstColorUniform, filterProgram);
    }

    public DHVector3 getSecondColor() {
        return secondColor;
    }

    public void setSecondColor(DHVector3 secondColor) {
        this.secondColor = secondColor;
        setFloatVec3Uniform(secondColor, secondColorUniform, filterProgram);
    }
}
