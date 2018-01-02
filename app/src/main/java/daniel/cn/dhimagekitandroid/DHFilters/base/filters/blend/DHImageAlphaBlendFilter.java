package daniel.cn.dhimagekitandroid.DHFilters.base.filters.blend;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageTwoInputFilter;

/**
 * Created by huanghongsen on 2017/12/29.
 */

public class DHImageAlphaBlendFilter extends DHImageTwoInputFilter {

    private static String DH_ALPHA_BLEND_FRAGMENT_SHADER = " varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " uniform lowp float mixturePercent;\n" +
//            " uniform lowp float strength;\n" +
            "\n" +
            " void main()\n" +
            " {\n" +
            "\t lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "\t lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "\t \n" +
//            "\t gl_FragColor = textureColor2;\n" +
            "\t gl_FragColor = vec4(mix(textureColor.rgb, textureColor2.rgb, textureColor2.a * mixturePercent), textureColor.a);\n" +
            " }";

    private float mixture;
    private int mixtureUniform;

    public DHImageAlphaBlendFilter() {
        super(DH_ALPHA_BLEND_FRAGMENT_SHADER);
        mixtureUniform = filterProgram.getUniformIndex("mixturePercent");
        setMixture(0.5f);
        updateWithStrength(1.f);
    }

    public float getMixture() {
        return mixture;
    }

    public void setMixture(float mixture) {
        this.mixture = mixture;
        setFloatUniform(mixture, mixtureUniform, filterProgram);
    }
}
