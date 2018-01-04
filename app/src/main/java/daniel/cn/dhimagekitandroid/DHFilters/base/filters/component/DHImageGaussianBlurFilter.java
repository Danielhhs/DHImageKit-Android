package daniel.cn.dhimagekitandroid.DHFilters.base.filters.component;

import android.opengl.GLES20;
import android.util.Log;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.GLProgram;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageFilterType;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageTwoPassTextureSamplingFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageFilterParameters;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2018/1/3.
 */

public class DHImageGaussianBlurFilter extends DHImageTwoPassTextureSamplingFilter {

    public static String LOG_TAG = "GaussianBlurFilter";

    private static String DEFAULT_VERTEX_SHADER = vertexShaderForOptimizedBlur(4, 2.f);
    private static String DEFAULT_FRAGMENT_SHADER = fragmentShaderForOptimizedBlur(4, 2.f);

    protected boolean shouldResizeBlurRadiusWithImageSize;
    private float blurRadiusInPixels, blurRadiusAsFractionOfImageWidth, blurRadiusAsFractionOfImageHeight;

    //From 0.f up, default to be 1.f;
    private float texelSpacingMultiplier;
    private int blurPasses;

    public DHImageGaussianBlurFilter() {
        this(DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER, DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER, 0.f, 30.f, 0.f);
    }

    public DHImageGaussianBlurFilter(DHImageFilterParameters parameters) {
        this(DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER, DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER, parameters.minValue, parameters.maxValue, parameters.initialValue);
    }

    public DHImageGaussianBlurFilter(String firstStageFragmentShader, String secondStageFragmentShader) {
        this(DH_VERTEX_SHADER_STRING, firstStageFragmentShader, DH_VERTEX_SHADER_STRING, secondStageFragmentShader, 0.f, 30.f, 0.f);
    }

    public DHImageGaussianBlurFilter(String firstStageVertexShader, String firstStageFragmentShader, String secondStageVertexShader, String secondStageFragmentShader, float minValue, float maxValue, float initialValue) {
        super(firstStageVertexShader, firstStageFragmentShader, secondStageVertexShader, secondStageFragmentShader, minValue, maxValue, initialValue);

        setTexelSpacingMultiplier(1.f);
        blurRadiusInPixels = 2.f;
        shouldResizeBlurRadiusWithImageSize = false;
    }

    @Override
    public DHImageFilterType getType() {
        return DHImageFilterType.GaussianBlur;
    }

    public static String vertexShaderForStandardBlur(int blurRadius, float sigma) {
        if (blurRadius < 1) {
            return DH_VERTEX_SHADER_STRING;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("attribute vec4 position;\n"
                + "attribute vec4 inputTextureCoordinate;\n"
                + "\n"
                + "uniform float texelWidthOffset;\n"
                + "uniform float texelHeightOffset;\n"
                + "\n"
                + "varying vec2 blurCoordinates[" + blurRadius * 2 + 1 +"];\n"
                + "\n"
                + "void main()\n"
                + "{\n"
                + "gl_Position = position;\n"
                + "\n"
                + "vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);");
        for (int currentBlurCoordinateIndex = 0; currentBlurCoordinateIndex < (blurRadius * 2 + 1); currentBlurCoordinateIndex++) {
            int offsetFromCenter = currentBlurCoordinateIndex - blurRadius;
            if (offsetFromCenter < 0) {
                sb.append("blurCoordinates[").append(currentBlurCoordinateIndex).append("] = inputTextureCoordinate.xy - singleStepOffset * ").append((float)-offsetFromCenter).append(";\n");
            } else if (offsetFromCenter > 0) {
                sb.append("blurCoordinates[").append(currentBlurCoordinateIndex).append("] = inputTextureCoordinate.xy + singleStepOffset * ").append((float)offsetFromCenter).append(";\n");
            } else {
                sb.append("blurCoordinates[").append(currentBlurCoordinateIndex).append("] = inputTextureCoordinate.xy;\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    public static String fragmentShaderForStandardBlur(int blurRadius, float sigma) {
        if (blurRadius < 1) {
            return DH_PASS_THROUGH_FRAGMENT_SHADER;
        }
        float standardGaussianWeight[] = new float[blurRadius + 1];
        float sumsOfWeight = 0.f;
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeight[currentGaussianWeightIndex] = (float)((1.0 / Math.sqrt(2.0 * Math.PI * Math.pow((double)sigma, 2.0))) * Math.exp(-Math.pow((double)currentGaussianWeightIndex, 2.0) / (2.0 * Math.pow((double)sigma, 2.0))));
            if (currentGaussianWeightIndex == 0) {
                sumsOfWeight += standardGaussianWeight[currentGaussianWeightIndex];
            } else {
                sumsOfWeight += 2.f * standardGaussianWeight[currentGaussianWeightIndex];
            }
        }

        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++)
        {
            standardGaussianWeight[currentGaussianWeightIndex] = standardGaussianWeight[currentGaussianWeightIndex] / sumsOfWeight;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("uniform sampler2D inputImageTexture;\n" +
                "     \n" +
                "     varying highp vec2 blurCoordinates[").append(blurRadius * 2 + 1).append("];\n" +
                "     \n" +
                "     void main()\n" +
                "     {\n" +
                "        lowp vec4 sum = vec4(0.0);\n");
        for (int currentBlurCoordinateIndex = 0; currentBlurCoordinateIndex < blurRadius * 2 + 1; currentBlurCoordinateIndex++) {
            int offsetFromCenter = currentBlurCoordinateIndex - blurRadius;
            if (offsetFromCenter < 0) {
                sb.append("sum += texture2D(inputImageTexture, blurCoordinates[").append(currentBlurCoordinateIndex).append("] * ").append(standardGaussianWeight[-offsetFromCenter]).append(";\n");
            } else {
                sb.append("sum += texture2D(inputImageTexture, blurCoordinates[").append(currentBlurCoordinateIndex).append("] * ").append(standardGaussianWeight[offsetFromCenter]).append(";\n");
            }
        }
        sb.append("gl_FragColor = sum;\n").append("}\n");
        return sb.toString();
    }

    public static String vertexShaderForOptimizedBlur(int blurRadius, float sigma) {
        if (blurRadius < 1) {
            return DH_VERTEX_SHADER_STRING;
        }
        float standardGaussianWeight[] = new float[blurRadius + 1];
        float sumsOfWeight = 0.f;
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeight[currentGaussianWeightIndex] = (float)((1.0 / Math.sqrt(2.0 * Math.PI * Math.pow((double)sigma, 2.0))) * Math.exp(-Math.pow((double)currentGaussianWeightIndex, 2.0) / (2.0 * Math.pow((double)sigma, 2.0))));
            if (currentGaussianWeightIndex == 0) {
                sumsOfWeight += standardGaussianWeight[currentGaussianWeightIndex];
            } else {
                sumsOfWeight += 2.f * standardGaussianWeight[currentGaussianWeightIndex];
            }
        }
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeight[currentGaussianWeightIndex] = standardGaussianWeight[currentGaussianWeightIndex] / sumsOfWeight;
        }
        int numberOfOptimizedOffsets = Math.min(blurRadius / 2 + (blurRadius % 2), 7);
        float optimizedGaussianOffsets[] = new float[numberOfOptimizedOffsets];
        for (int currentOptimizedOffset = 0; currentOptimizedOffset < numberOfOptimizedOffsets; currentOptimizedOffset++) {
            float firstWeight = standardGaussianWeight[currentOptimizedOffset*2 + 1];
            float secondWeight = standardGaussianWeight[currentOptimizedOffset*2 + 2];

            float optimizedWeight = firstWeight + secondWeight;

            optimizedGaussianOffsets[currentOptimizedOffset] = (firstWeight * (currentOptimizedOffset*2 + 1) + secondWeight * (currentOptimizedOffset*2 + 2)) / optimizedWeight;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("attribute vec4 position;\n");
        sb.append("attribute vec4 inputTextureCoordinate;\n\n");
        sb.append("uniform float texelWidthOffset;\n");
        sb.append("uniform float texelHeightOffset;\n");
        sb.append("varying vec2 blurCoordinates[").append(1 + (numberOfOptimizedOffsets * 2)).append("];\n\n");
        sb.append("void main()\n");
        sb.append("{\n");
        sb.append("gl_Position = position;\n\n");
        sb.append("vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);\n");
        sb.append("blurCoordinates[0] = inputTextureCoordinate.xy;\n");

        for (int currentOptimizedOffset = 0; currentOptimizedOffset < numberOfOptimizedOffsets; currentOptimizedOffset++) {
            sb.append("blurCoordinates[").append((currentOptimizedOffset * 2) + 1).append("] = inputTextureCoordinate.xy + singleStepOffset * ").append(optimizedGaussianOffsets[currentOptimizedOffset]).append(";\n");
            sb.append("blurCoordinates[").append((currentOptimizedOffset * 2) + 2).append("] = inputTextureCoordinate.xy - singleStepOffset * ").append(optimizedGaussianOffsets[currentOptimizedOffset]).append(";\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    public static String fragmentShaderForOptimizedBlur(int blurRadius, float sigma) {
        if (blurRadius < 1) {
            return DH_PASS_THROUGH_FRAGMENT_SHADER;
        }
        float standardGaussianWeight[] = new float[blurRadius + 1];
        float sumsOfWeight = 0.f;
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeight[currentGaussianWeightIndex] = (float)((1.0 / Math.sqrt(2.0 * Math.PI * Math.pow((double)sigma, 2.0))) * Math.exp(-Math.pow((double)currentGaussianWeightIndex, 2.0) / (2.0 * Math.pow((double)sigma, 2.0))));
            if (currentGaussianWeightIndex == 0) {
                sumsOfWeight += standardGaussianWeight[currentGaussianWeightIndex];
            } else {
                sumsOfWeight += 2.f * standardGaussianWeight[currentGaussianWeightIndex];
            }
        }
        for (int currentGaussianWeightIndex = 0; currentGaussianWeightIndex < blurRadius + 1; currentGaussianWeightIndex++) {
            standardGaussianWeight[currentGaussianWeightIndex] = standardGaussianWeight[currentGaussianWeightIndex] / sumsOfWeight;
        }
        int numberOfOptimizedOffsets = Math.min(blurRadius / 2 + (blurRadius % 2), 7);
        int trueNumberOfOptimizedOffsets = blurRadius / 2 + (blurRadius % 2);

        StringBuilder sb = new StringBuilder();
        sb.append("uniform sampler2D inputImageTexture;\n");
        sb.append("uniform highp float texelWidthOffset;\n");
        sb.append("uniform highp float texelHeightOffset;\n\n");
        sb.append("varying highp vec2 blurCoordinates[").append(1 + (numberOfOptimizedOffsets * 2)).append("];\n\n");
        sb.append("void main()\n");
        sb.append("{\n");
        sb.append("\tlowp vec4 sum = vec4(0.0);\n");
        sb.append("\tsum += texture2D(inputImageTexture, blurCoordinates[0]) * ").append(standardGaussianWeight[0]).append(";\n");

        for (int currentBlurCoordinateIndex = 0; currentBlurCoordinateIndex < numberOfOptimizedOffsets; currentBlurCoordinateIndex++) {
            float firstWeight = standardGaussianWeight[currentBlurCoordinateIndex * 2 + 1];
            float secondWeight = standardGaussianWeight[currentBlurCoordinateIndex * 2 + 2];
            float optimizedWeight = firstWeight + secondWeight;

            sb.append("sum += texture2D(inputImageTexture, blurCoordinates[").append((currentBlurCoordinateIndex * 2) + 1).append("]) * ").append(optimizedWeight).append(";\n");
            sb.append("sum += texture2D(inputImageTexture, blurCoordinates[").append((currentBlurCoordinateIndex * 2) + 2).append("]) * ").append(optimizedWeight).append(";\n");
        }

        if (trueNumberOfOptimizedOffsets > numberOfOptimizedOffsets) {
            sb.append("highp vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);\n");
            for (int currentOverlowTextureRead = numberOfOptimizedOffsets; currentOverlowTextureRead < trueNumberOfOptimizedOffsets; currentOverlowTextureRead++) {
                float firstWeight = standardGaussianWeight[currentOverlowTextureRead * 2 + 1];
                float secondWeight = standardGaussianWeight[currentOverlowTextureRead * 2 + 2];

                float optimizedWeight = firstWeight + secondWeight;
                float optimizedOffset = (firstWeight * (currentOverlowTextureRead * 2 + 1) + secondWeight * (currentOverlowTextureRead * 2 + 2)) / optimizedWeight;

                sb.append("sum += texture2D(inputImageTexture, blurCoordinates[0] + singleStepOffset * ").append(optimizedOffset).append(") * ").append(optimizedWeight).append(";\n");
                sb.append("sum += texture2D(inputImageTexture, blurCoordinates[0] - singleStepOffset * ").append(optimizedOffset).append(") * ").append(optimizedWeight).append(";\n");
            }
        }
        sb.append("gl_FragColor = sum;\n").append("}\n");
        return sb.toString();
    }

    @Override
    public void setupFilterForSize(DHImageSize size) {
        super.setupFilterForSize(size);
        if (shouldResizeBlurRadiusWithImageSize) {
            if (blurRadiusAsFractionOfImageWidth > 0) {
                setBlurRadiusInPixels(blurRadiusAsFractionOfImageWidth * size.width);
            } else {
                setBlurRadiusInPixels(blurRadiusAsFractionOfImageHeight * size.height);
            }
        }
    }

    @Override
    public void renderToTexture(float[] vertices, float[] texCoords) {
        super.renderToTexture(vertices, texCoords);
        for (int currentAdditionalBlurPass = 1; currentAdditionalBlurPass < blurPasses; currentAdditionalBlurPass++) {
            super.renderToTexture(vertices, textureCoordinatesForRotation(DHImageRotationMode.NoRotation));
        }
    }

    public void switchToNewShaders(String vertexShader, String fragmentShader) {
        //TO-DO: Use cached program;
        filterProgram = new GLProgram(vertexShader, fragmentShader);
        if (!filterProgram.isInitialized()) {
            initializeAttributes();
            if (!filterProgram.link()) {
                Log.e(LOG_TAG, "Fragment Shader Log: " + filterProgram.getFragmentShaderLog());
                Log.e(LOG_TAG, "Vertex Shader Log: " + filterProgram.getVertexShaderLog());
                Log.e(LOG_TAG, "Program Log: " + filterProgram.getProgramLog());
                throw new RuntimeException("Error while switching shaders for Gaussian Blur filter");
            }

        }
        filterPositionAttribute = filterProgram.getAttributeIndex("position");
        filterTexCoordAttribute = filterProgram.getAttributeIndex("inputTextureCoordinate");
        filterInputTextureUniform = filterProgram.getUniformIndex("inputImageTexture");
        verticalPassTexelWidthOffsetUniform = filterProgram.getUniformIndex("texelWidthOffset");
        verticalPassTexelHeightOffsetUniform = filterProgram.getUniformIndex("texelHeightOffset");
        DHImageContext.setActiveProgram(filterProgram);
        GLES20.glEnableVertexAttribArray(filterPositionAttribute);
        GLES20.glEnableVertexAttribArray(filterTexCoordAttribute);

        secondFilterProgram = new GLProgram(vertexShader, fragmentShader);
        if (!secondFilterProgram.isInitialized()) {
            initializeSecondaryAttributes();;
            if (!secondFilterProgram.link()) {
                Log.e(LOG_TAG, "Fragment Shader Log: " + secondFilterProgram.getFragmentShaderLog());
                Log.e(LOG_TAG, "Vertex Shader Log: " + secondFilterProgram.getVertexShaderLog());
                Log.e(LOG_TAG, "Program Log: " + secondFilterProgram.getProgramLog());
                throw new RuntimeException("Error while switching shaders for second program Gaussian Blur filter");
            }
        }
        secondFilterPositionAttribute = secondFilterProgram.getAttributeIndex("position");
        secondFilterTextureCoordinateAttribute = secondFilterProgram.getAttributeIndex("inputTextureCoordinate");
        secondFilterInputTextureUniform = secondFilterProgram.getUniformIndex("inputImageTexture");
        secondFilterInputTextureUniform2 = secondFilterProgram.getUniformIndex("inputImageTexture2");
        horizontalPassTexelWidthOffsetUniform = filterProgram.getUniformIndex("texelWidthOffset");
        horizontalPassTexelHeightOffsetUniform = filterProgram.getUniformIndex("texelHeightOffset");
        DHImageContext.setActiveProgram(secondFilterProgram);

        GLES20.glEnableVertexAttribArray(secondFilterPositionAttribute);
        GLES20.glEnableVertexAttribArray(secondFilterTextureCoordinateAttribute);

        setupFilterForSize(sizeOfFBO());
        GLES20.glFinish();
    }

    public float getBlurRadiusInPixels() {
        return blurRadiusInPixels;
    }

    public void setBlurRadiusInPixels(float newValue) {
        if (Math.round(newValue) != this.blurRadiusInPixels) {
            blurRadiusInPixels = Math.round(newValue);
            int calculatedSampleRadius = 0;
            if (blurRadiusInPixels >= 1) {
                float minimumWeightToFindEdgeOfSamplingArea = 1.f / 256.f;
                calculatedSampleRadius = (int)Math.floor(Math.sqrt(-2.0 * Math.pow(blurRadiusInPixels, 2.0) * Math.log(minimumWeightToFindEdgeOfSamplingArea * Math.sqrt(2.0 * Math.PI * Math.pow(blurRadiusInPixels, 2.0))) ));
                calculatedSampleRadius += calculatedSampleRadius % 2;
            }
            String newVertexShader = vertexShaderForOptimizedBlur(calculatedSampleRadius, blurRadiusInPixels);
            String newFragmentShader = fragmentShaderForOptimizedBlur(calculatedSampleRadius, blurRadiusInPixels);
            switchToNewShaders(newVertexShader, newFragmentShader);
        }
        shouldResizeBlurRadiusWithImageSize = false;
    }

    public float getBlurRadiusAsFractionOfImageWidth() {
        return blurRadiusAsFractionOfImageWidth;
    }

    public void setBlurRadiusAsFractionOfImageWidth(float blurRadiusAsFractionOfImageWidth) {
        if (blurRadiusAsFractionOfImageWidth < 0) return;
        if (this.blurRadiusAsFractionOfImageWidth != blurRadiusAsFractionOfImageWidth && blurRadiusAsFractionOfImageWidth > 0) {
            shouldResizeBlurRadiusWithImageSize = true;
        } else {
            shouldResizeBlurRadiusWithImageSize = false;
        }
        this.blurRadiusAsFractionOfImageWidth = blurRadiusAsFractionOfImageWidth;
        this.blurRadiusAsFractionOfImageHeight = 0;

    }

    public float getTexelSpacingMultiplier() {
        return texelSpacingMultiplier;
    }

    public void setTexelSpacingMultiplier(float texelSpacingMultiplier) {
        this.texelSpacingMultiplier = texelSpacingMultiplier;
        verticalTexelSpacing = texelSpacingMultiplier;
        horinzontalTexelSpacing = texelSpacingMultiplier;
        setupFilterForSize(sizeOfFBO());
    }

    public int getBlurPasses() {
        return blurPasses;
    }

    public void setBlurPasses(int blurPasses) {
        this.blurPasses = blurPasses;
    }

    public float getBlurRadiusAsFractionOfImageHeight() {
        return blurRadiusAsFractionOfImageHeight;
    }

    public void setBlurRadiusAsFractionOfImageHeight(float blurRadiusAsFractionOfImageHeight) {
        if (blurRadiusAsFractionOfImageHeight < 0) return ;

        if (this.blurRadiusAsFractionOfImageHeight != blurRadiusAsFractionOfImageHeight && blurRadiusAsFractionOfImageHeight > 0) {
            shouldResizeBlurRadiusWithImageSize = true;
        } else {
            shouldResizeBlurRadiusWithImageSize = false;
        }
        this.blurRadiusAsFractionOfImageHeight = blurRadiusAsFractionOfImageHeight;
        this.blurRadiusAsFractionOfImageWidth = 0;
    }

    @Override
    public void updateWithInput(float input) {
        setBlurRadiusInPixels(input);
    }

    @Override
    public float getCurrentValue() {
        return blurRadiusInPixels;
    }
}
