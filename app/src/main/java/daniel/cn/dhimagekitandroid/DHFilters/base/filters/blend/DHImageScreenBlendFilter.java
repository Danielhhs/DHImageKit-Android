package daniel.cn.dhimagekitandroid.DHFilters.base.filters.blend;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageTwoInputFilter;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageScreenBlendFilter extends DHImageTwoInputFilter {

    private static String DH_SCREEN_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " uniform highp float strength;\n" +
            " uniform mediump float opacity;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     mediump vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     mediump vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2) * opacity;\n" +
            "     mediump vec4 whiteColor = vec4(1.0);\n" +
            "     mediump vec4 processedColor = whiteColor - ((whiteColor - textureColor2) * (whiteColor - textureColor));\n" +
            "     gl_FragColor = mix(textureColor, processedColor, strength);\n" +
            " }";

    private int opacityUniform;
    private float opacity;

    public DHImageScreenBlendFilter() {
        super(DH_SCREEN_BLEND_FRAGMENT_SHADER);
        opacityUniform = filterProgram.getUniformIndex("opacity");

    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
        setFloatUniform(opacity, opacityUniform, filterProgram);
    }
}
