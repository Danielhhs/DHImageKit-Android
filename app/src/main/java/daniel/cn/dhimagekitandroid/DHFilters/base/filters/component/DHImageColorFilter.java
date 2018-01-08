package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector3;

/**
 * Created by huanghongsen on 2018/1/3.
 */

public class DHImageColorFilter extends DHImageFilter {
    public static String DH_COLOR_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform highp float redAdjustment;\n" +
            " uniform highp float greenAdjustment;\n" +
            " uniform highp float blueAdjustment;\n" +
            " uniform highp float strength;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4(mix(textureColor.rgb, vec3(textureColor.r * redAdjustment, textureColor.g * greenAdjustment, textureColor.b * blueAdjustment), strength), textureColor.a);\n" +
            " }";

    private DHVector3 color;
    private float adjustment;
    protected int redUniform, greenUniform, blueUniform;

    public DHImageColorFilter() {
        this(0.f, 1.f, 0.5f);
    }

    public DHImageColorFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageColorFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_COLOR_FRAGMENT_SHADER, minValue, maxValue, initialValue);

        redUniform = filterProgram.getUniformIndex("redAdjustment");
        greenUniform = filterProgram.getUniformIndex("greenAdjustment");
        blueUniform = filterProgram.getUniformIndex("blueAdjustment");

        setColor(new DHVector3(1.f, 0.f, 0.f));
        setAdjustment(initialValue);
        updateWithStrength(1.f);
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.Colors;
    }

    @Override
    public float getCurrentValue() {
        return adjustment;
    }

    @Override
    public void updateWithInput(float input) {
        setAdjustment(input);
    }

    public float getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(float adjustment) {
        this.adjustment = adjustment;
        updateColors();
    }

    public DHVector3 getColor() {
        return color;
    }

    public void setColor(DHVector3 color) {
        this.color = color;
        updateColors();
    }

    private void updateColors() {
        if (color == null) return;
        setFloatUniform((color.one -1.f) * adjustment + 1.f, redUniform, filterProgram);
        setFloatUniform((color.two -1.f) * adjustment + 1.f, greenUniform, filterProgram);
        setFloatUniform((color.three -1.f) * adjustment + 1.f, blueUniform, filterProgram);
    }
}
