package daniel.cn.dhimagekitandroid.DHFilters.base.filters.blend;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageTwoInputFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.component.DHImageVignetteFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImagePoint;

/**
 * Created by huanghongsen on 2018/1/8.
 */

public class DHImageVignetteBlendFilter extends DHImageTwoInputFilter {

    private static String DH_VIGNETTE_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " uniform mediump vec2 vignetteCenter;\n" +
            " uniform mediump float vignetteStart;\n" +
            " uniform mediump float vignetteEnd;\n" +
            " \n" +
            " uniform highp float strength;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     mediump vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     mediump vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "     lowp float d = distance(textureCoordinate, vignetteCenter);\n" +
            "     lowp float percent = smoothstep(vignetteStart, vignetteEnd, d);\n" +
            "     gl_FragColor = mix(textureColor, mix(textureColor, textureColor2, percent), strength);\n" +
            " }";

    private int vignetteStartUniform, vignetteEndUniform, vignetteCenterUniform;

    private DHImagePoint vignetteCenter;
    private float vignetteStart, vignetteEnd;

    public DHImageVignetteBlendFilter() {
        super(DH_VIGNETTE_BLEND_FRAGMENT_SHADER);

        vignetteCenterUniform = filterProgram.getUniformIndex("vignetteCenter");
        vignetteEndUniform = filterProgram.getUniformIndex("vignetteEnd");
        vignetteStartUniform = filterProgram.getUniformIndex("vignetteStart");

        setVignetteCenter(new DHImagePoint(0.5f, 0.5f));
        setVignetteStart(0.3f);
        setVignetteEnd(0.75f);


    }

    public DHImagePoint getVignetteCenter() {
        return vignetteCenter;
    }

    public void setVignetteCenter(DHImagePoint vignetteCenter) {
        this.vignetteCenter = vignetteCenter;
        setPointUniform(vignetteCenter, vignetteCenterUniform, filterProgram);
    }

    public float getVignetteStart() {
        return vignetteStart;
    }

    public void setVignetteStart(float vignetteStart) {
        this.vignetteStart = vignetteStart;
        setFloatUniform(vignetteStart, vignetteStartUniform, filterProgram);
    }

    public float getVignetteEnd() {
        return vignetteEnd;
    }

    public void setVignetteEnd(float vignetteEnd) {
        this.vignetteEnd = vignetteEnd;
        setFloatUniform(vignetteEnd, vignetteEndUniform, filterProgram);
    }
}
