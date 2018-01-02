package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;

/**
 * Created by huanghongsen on 2018/1/2.
 */

public class DHImageHighlightFilter extends DHImageFilter {
    public static String DH_HIGHLIGHT_FRAGMENT_SHADER = "precision highp float;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform float highlights;\n" +
            " uniform float strength;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " const vec3 luminanceWeight = vec3(0.3, 0.3, 0.3);\n" +
            " const float highlightLuminance = 0.66;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     float luminance = dot(textureColor.rgb, luminanceWeight);\n" +
            "     \n" +
            "     if (luminance > highlightLuminance) {\n" +
            "         gl_FragColor = vec4(textureColor.rgb * highlights, textureColor.a);\n" +
            "     } else {\n" +
            "         gl_FragColor = textureColor;\n" +
            "     }\n" +
            " }";

    protected int highlightUniform;
    private float highlight;

    public DHImageHighlightFilter() {
        this(0.9f, 1.1f, 1.f);
    }

    public DHImageHighlightFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageHighlightFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_HIGHLIGHT_FRAGMENT_SHADER, minValue, maxValue, initialValue);

        highlightUniform = filterProgram.getUniformIndex("highlights");
        setHighlight(initialValue);
        updateWithStrength(1.f);
    }

    public float getHighlight() {
        return highlight;
    }

    public void setHighlight(float highlight) {
        this.highlight = highlight;
        setFloatUniform(highlight, highlightUniform, filterProgram);
    }

    @Override
    public void updateWithInput(float input) {
        setHighlight(input);
    }

    @Override
    public float getCurrentValue() {
        return highlight;
    }
}
