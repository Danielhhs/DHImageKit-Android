package daniel.cn.dhimagekitandroid.DHFilters.base.filters.base;

import android.opengl.GLES20;

import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2018/1/3.
 */

public class DHImageTwoPassTextureSamplingFilter extends DHImageTwoPassFilter {
    protected int verticalPassTexelWidthOffsetUniform, verticalPassTexelHeightOffsetUniform, horizontalPassTexelWidthOffsetUniform, horizontalPassTexelHeightOffsetUniform;
    protected float verticalPassTexelWidthOffset, verticalPassTexelHeightOffset, horizontalPassTexelWidthOffset, horizontalPassTexelHeightOffset;
    protected float verticalTexelSpacing, horinzontalTexelSpacing;

    public DHImageTwoPassTextureSamplingFilter(String firstStageFragmentShader, String secondStageFragmentShader) {
        this(DH_VERTEX_SHADER_STRING, firstStageFragmentShader, DH_VERTEX_SHADER_STRING, secondStageFragmentShader, 0.f, 0.f, 0.f);
    }

    public DHImageTwoPassTextureSamplingFilter(String firstStageVertexShader, String firstStageFragmentShader, String secondStageVertexShader, String secondStageFragmentShader, float minValue, float maxValue, float initialValue) {
        super(firstStageVertexShader, firstStageFragmentShader, secondStageVertexShader, secondStageFragmentShader, minValue, maxValue, initialValue);
        verticalPassTexelWidthOffsetUniform = filterProgram.getUniformIndex("texelWidthOffset");
        verticalPassTexelHeightOffsetUniform = filterProgram.getUniformIndex("texelHeightOffset");
        horizontalPassTexelWidthOffsetUniform = filterProgram.getUniformIndex("texelWidthOffset");
        horizontalPassTexelHeightOffsetUniform = filterProgram.getUniformIndex("texelHeightOffset");

        setVerticalTexelSpacing(1.f);
        setHorinzontalTexelSpacing(1.f);
    }

    @Override
    public void setupFilterForSize(DHImageSize size) {
        if (size == null || size.isZeroSize()) return ;
        if (inputRotationMode.needToSwapWidthAndHeight()) {
            verticalPassTexelWidthOffset = verticalTexelSpacing / size.height;
            verticalPassTexelHeightOffset = 0.f;
        } else {
            verticalPassTexelWidthOffset = 0.f;
            verticalPassTexelHeightOffset = verticalTexelSpacing / size.height;
        }
        horizontalPassTexelWidthOffset = horinzontalTexelSpacing / size.width;
        horizontalPassTexelHeightOffset = 0.f;
    }

    public float getVerticalTexelSpacing() {
        return verticalTexelSpacing;
    }

    public void setVerticalTexelSpacing(float verticalTexelSpacing) {
        this.verticalTexelSpacing = verticalTexelSpacing;
        setupFilterForSize(sizeOfFBO());
    }

    public float getHorinzontalTexelSpacing() {
        return horinzontalTexelSpacing;
    }

    public void setHorinzontalTexelSpacing(float horinzontalTexelSpacing) {
        this.horinzontalTexelSpacing = horinzontalTexelSpacing;
        setupFilterForSize(sizeOfFBO());
    }

    @Override
    public void setUniformsForProgram(int programIndex) {
        super.setUniformsForProgram(programIndex);
        if (programIndex == 0) {
            GLES20.glUniform1f(verticalPassTexelWidthOffsetUniform, verticalPassTexelWidthOffset);
            GLES20.glUniform1f(verticalPassTexelHeightOffsetUniform, verticalPassTexelHeightOffset);
        } else {
            GLES20.glUniform1f(horizontalPassTexelWidthOffsetUniform, horizontalPassTexelWidthOffset);
            GLES20.glUniform1f(horizontalPassTexelHeightOffsetUniform, horizontalPassTexelHeightOffset);
        }
    }
}
