package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector3;

/**
 * Created by huanghongsen on 2018/1/2.
 * In Correspondance to GPUImageLevelsFilter
 */

public class DHImageStructureFilter extends DHImageFilter {
    public static String DH_STRUCTURE_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate; \n" +
            "\n" +
            "uniform sampler2D inputImageTexture; \n" +
            "uniform mediump vec3 levelMinimum; \n" +
            "uniform mediump vec3 levelMiddle; \n" +
            "uniform mediump vec3 levelMaximum; \n" +
            "uniform mediump vec3 minOutput; \n" +
            "uniform mediump vec3 maxOutput; \n" +
            "uniform mediump float strength; \n" +
            "\n" +
            "void main() { \n" +
            "mediump vec4 textureColor = texture2D(inputImageTexture, textureCoordinate); \n" +
            "\n" +
            "highp vec3 processedColor = mix(minOutput, maxOutput, pow(min(max(textureColor.rgb - levelMinimum, vec3(0.0)) / (levelMaximum - levelMinimum), vec3(1.0)), 1.0 / levelMiddle)); \n" +
            "gl_FragColor = vec4(mix(textureColor.rgb, processedColor, strength), 1.0); \n" +
            "}";

    protected int levelMinimumUniform, levelMiddleUniform, levelMaximumUniform, minOutputUniform, maxOutputUniform;
    private DHVector3 minVector, maxVector, midVector, minOutputVector, maxOutputVector;

    private float level;

    public DHImageStructureFilter() {
        this(0.f, 0.25f, 0.f);
    }

    public DHImageStructureFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageStructureFilter(float minValue, float maxValue, float initialValue) {
        super(DH_VERTEX_SHADER_STRING, DH_STRUCTURE_FRAGMENT_SHADER, minValue, maxValue, initialValue);

        minVector = new DHVector3();
        maxVector = new DHVector3();
        midVector = new DHVector3();
        minOutputVector = new DHVector3();
        maxOutputVector = new DHVector3();

        levelMinimumUniform = filterProgram.getUniformIndex("levelMinimum");
        levelMaximumUniform = filterProgram.getUniformIndex("levelMaximum");
        levelMiddleUniform = filterProgram.getUniformIndex("levelMiddle");
        minOutputUniform = filterProgram.getUniformIndex("minOutput");
        maxOutputUniform = filterProgram.getUniformIndex("maxOutput");

        setLevel(initialValue);
        updateWithStrength(1.f);
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.Structure;
    }

    public void setRed(float min, float gamma, float max, float minOutput, float maxOutput) {
        minVector.one = min;
        midVector.one = gamma;
        maxVector.one = max;
        minOutputVector.one = minOutput;
        maxOutputVector.one = maxOutput;
        updateUniforms();
    }

    public void setRed(float min, float gamma, float max) {
        setRed(min, gamma, max, 0.f, 1.f);
    }

    public void setGreen(float min, float gamma, float max, float minOutput, float maxOutput) {
        minVector.two = min;
        midVector.two = gamma;
        maxVector.two = max;
        minOutputVector.two = minOutput;
        maxOutputVector.two = maxOutput;
        updateUniforms();
    }

    public void setGreen(float min, float gamma, float max) {
        setGreen(min, gamma, max, 0.f, 1.f);
    }

    public void setBlue(float min, float gamma, float max, float minOutput, float maxOutput) {
        minVector.three = min;
        midVector.three = gamma;
        maxVector.three = max;
        minOutputVector.three = minOutput;
        maxOutputVector.three = maxOutput;
        updateUniforms();
    }

    public void setBlue(float min, float gamma, float max) {
        setBlue(min, gamma, max, 0.f, 1.f);
    }

    public void setValues(float min, float gamma, float max, float minOutput, float maxOutput) {
        setRed(min, gamma, max, minOutput, maxOutput);
        setGreen(min, gamma, max, minOutput, maxOutput);
        setBlue(min, gamma, max, minOutput, maxOutput);
    }

    public void setValues(float min, float gamma, float max) {
        setValues(min, gamma, max, 0.f, 1.f);
    }

    private void updateUniforms() {
        setFloatVec3Uniform(minVector, levelMinimumUniform, filterProgram);
        setFloatVec3Uniform(midVector, levelMiddleUniform, filterProgram);
        setFloatVec3Uniform(maxVector, levelMaximumUniform, filterProgram);
        setFloatVec3Uniform(minOutputVector, minOutputUniform, filterProgram);
        setFloatVec3Uniform(maxOutputVector, maxOutputUniform, filterProgram);
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
        setValues(level, 1.f, 1.f, 0.f, 1.f);
    }

    @Override
    public float getCurrentValue() {
        return this.level;
    }

    @Override
    public void updateWithInput(float input) {
        if (minVector == null) return ;
        setLevel(input);
    }
}
