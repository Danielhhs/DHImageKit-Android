package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import android.opengl.GLES20;

import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2018/1/2.
 */

public class DHImageSharpenFilter extends DHImageFilter {
    public static String DH_SHARPEN_VERTEX_SHADER = "attribute vec4 position;\n" +
            " attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            " uniform float imageWidthFactor; \n" +
            " uniform float imageHeightFactor; \n" +
            " uniform float sharpness;\n" +
            " \n" +
            " varying vec2 textureCoordinate;\n" +
            " varying vec2 leftTextureCoordinate;\n" +
            " varying vec2 rightTextureCoordinate; \n" +
            " varying vec2 topTextureCoordinate;\n" +
            " varying vec2 bottomTextureCoordinate;\n" +
            " \n" +
            " varying float centerMultiplier;\n" +
            " varying float edgeMultiplier;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_Position = position;\n" +
            "     \n" +
            "     vec2 widthStep = vec2(imageWidthFactor, 0.0);\n" +
            "     vec2 heightStep = vec2(0.0, imageHeightFactor);\n" +
            "     \n" +
            "     textureCoordinate = inputTextureCoordinate.xy;\n" +
            "     leftTextureCoordinate = inputTextureCoordinate.xy - widthStep;\n" +
            "     rightTextureCoordinate = inputTextureCoordinate.xy + widthStep;\n" +
            "     topTextureCoordinate = inputTextureCoordinate.xy + heightStep;     \n" +
            "     bottomTextureCoordinate = inputTextureCoordinate.xy - heightStep;\n" +
            "     \n" +
            "     centerMultiplier = 1.0 + 4.0 * sharpness;\n" +
            "     edgeMultiplier = sharpness;\n" +
            " }";

    public static String DH_SHARPEN_FRAGMENT_SHADER = "precision highp float;\n" +
            " \n" +
            " varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 leftTextureCoordinate;\n" +
            " varying highp vec2 rightTextureCoordinate; \n" +
            " varying highp vec2 topTextureCoordinate;\n" +
            " varying highp vec2 bottomTextureCoordinate;\n" +
            " \n" +
            " varying highp float centerMultiplier;\n" +
            " varying highp float edgeMultiplier;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     mediump vec3 textureColor = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
            "     mediump vec3 leftTextureColor = texture2D(inputImageTexture, leftTextureCoordinate).rgb;\n" +
            "     mediump vec3 rightTextureColor = texture2D(inputImageTexture, rightTextureCoordinate).rgb;\n" +
            "     mediump vec3 topTextureColor = texture2D(inputImageTexture, topTextureCoordinate).rgb;\n" +
            "     mediump vec3 bottomTextureColor = texture2D(inputImageTexture, bottomTextureCoordinate).rgb;\n" +
            "\n" +
            "     gl_FragColor = vec4((textureColor * centerMultiplier - (leftTextureColor * edgeMultiplier + rightTextureColor * edgeMultiplier + topTextureColor * edgeMultiplier + bottomTextureColor * edgeMultiplier)), texture2D(inputImageTexture, bottomTextureCoordinate).w);\n" +
            " }\n";

    protected int sharpnessUnfiorm;
    protected int imageWidthFactorUniform, imageHeightFactorUniform;
    private  float sharpness;

    public DHImageSharpenFilter() {
        this(-4.f, 4.f, 0.f);
    }

    public DHImageSharpenFilter(DHImageFilterParameters parameters) {
        this(parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageSharpenFilter(float minValue, float maxValue, float initialValue) {
        super(DH_SHARPEN_VERTEX_SHADER, DH_SHARPEN_FRAGMENT_SHADER, minValue, maxValue, initialValue);

        sharpnessUnfiorm = filterProgram.getUniformIndex("sharpness");
        imageWidthFactorUniform = filterProgram.getUniformIndex("imageWidthFactor");
        imageHeightFactorUniform = filterProgram.getUniformIndex("imageHeightFactor");

        setSharpness(initialValue);
    }

    public float getSharpness() {
        return sharpness;
    }

    public void setSharpness(float sharpness) {
        this.sharpness = sharpness;
        setFloatUniform(sharpness, sharpnessUnfiorm, filterProgram);
    }

    @Override
    public void updateWithInput(float input) {
        setSharpness(input);
    }

    @Override
    public float getCurrentValue() {
        return sharpness;
    }

    @Override
    public void setupFilterForSize(DHImageSize size) {
        filterProgram.use();
        if (inputRotationMode.needToSwapWidthAndHeight()) {
            GLES20.glUniform1f(imageWidthFactorUniform, 1.0f / size.height);
            GLES20.glUniform1f(imageHeightFactorUniform, 1.0f / size.width);
        } else {
            GLES20.glUniform1f(imageWidthFactorUniform, 1.0f / size.width);
            GLES20.glUniform1f(imageHeightFactorUniform, 1.0f / size.height);
        }
    }
}
