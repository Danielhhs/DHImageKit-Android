package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImagePoint;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector3;

/**
 * Created by huanghongsen on 2018/1/2.
 */

public class DHImageVignetteFilter extends DHImageFilter {
    public static String DH_VIGNETTE_FRAGMENT_SHADER = "uniform sampler2D inputImageTexture;\n" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform lowp vec2 vignetteCenter;\n" +
            " uniform lowp vec3 vignetteColor;\n" +
            " uniform highp float vignetteStart;\n" +
            " uniform highp float vignetteEnd;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     lowp vec4 sourceImageColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     lowp float d = distance(textureCoordinate, vec2(vignetteCenter.x, vignetteCenter.y));\n" +
            "     lowp float percent = smoothstep(vignetteStart, vignetteEnd, d);\n" +
            "     gl_FragColor = vec4(mix(sourceImageColor.rgb, vignetteColor, percent), sourceImageColor.a);\n" +
            " }";

    protected int vignetteCenterUniform, vignetteColorUniform, vignetteStartUniform, vignetteEndUniform;
    private float vignetteStart, vignetteEnd;
    private DHImagePoint vignetteCenter;
    private DHVector3 vignetteColor;

    public DHImageVignetteFilter() {
        this(0.75f, 1.f, 0.75f);
    }

    public DHImageVignetteFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageVignetteFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_VIGNETTE_FRAGMENT_SHADER, minValue, maxValue, initialValue);

        vignetteCenterUniform = filterProgram.getUniformIndex("vignetteCenter");
        vignetteColorUniform = filterProgram.getUniformIndex("vignetteColor");
        vignetteStartUniform = filterProgram.getUniformIndex("vignetteStart");
        vignetteEndUniform = filterProgram.getUniformIndex("vignetteEnd");

        setVignetteStart(0.3f);
        setVignetteEnd(initialValue);
        setVignetteCenter(new DHImagePoint(0.5f, 0.5f));
        setVignetteColor(new DHVector3());
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.Vignette;
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

    public DHImagePoint getVignetteCenter() {
        return vignetteCenter;
    }

    public void setVignetteCenter(DHImagePoint vignetteCenter) {
        this.vignetteCenter = vignetteCenter;
        setPointUniform(vignetteCenter, vignetteCenterUniform, filterProgram);
    }

    public DHVector3 getVignetteColor() {
        return vignetteColor;
    }

    public void setVignetteColor(DHVector3 vignetteColor) {
        this.vignetteColor = vignetteColor;
        setFloatVec3Uniform(vignetteColor, vignetteColorUniform, filterProgram);
    }

    @Override
    public void updateWithInput(float input) {
        setVignetteEnd(input);
    }

    @Override
    public float getCurrentValue() {
        return vignetteEnd;
    }
}
