package daniel.cn.dhimagekitandroid.DHFilters.base.filters.base;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageFrameBuffer;
import daniel.cn.dhimagekitandroid.DHFilters.base.GLProgram;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.base.DHImageTwoInputFilter;

/**
 * Created by huanghongsen on 2018/1/3.
 */

public class DHImageTwoPassFilter extends DHImageFilter {
    public static String LOG_TAG = "DHImageTwoPassFilter";

    protected DHImageFrameBuffer secondOutputFrameBuffer;
    protected GLProgram secondFilterProgram;
    protected int secondFilterPositionAttribute, secondFilterTextureCoordinateAttribute;
    protected int secondFilterInputTextureUniform, secondFilterInputTextureUniform2;

    public DHImageTwoPassFilter(String firstStageFragmentShader, String secondStageFragmentShader) {
        this(DH_VERTEX_SHADER_STRING, firstStageFragmentShader, DH_VERTEX_SHADER_STRING, secondStageFragmentShader, 0.f, 0.f, 0.f);
    }

    public DHImageTwoPassFilter(String firstStageVertexShader, String firstStageFragmentShader, String secondStatgeVertexShader, String secondStageFragmentShader, float minValue, float maxValue, float initialValue) {
        super(firstStageVertexShader, firstStageFragmentShader, minValue, maxValue, initialValue);
        //TO-DO: Use shared program
        secondFilterProgram = new GLProgram(secondStatgeVertexShader, secondStageFragmentShader);
        if (!secondFilterProgram.isInitialized()) {
            initializeSecondaryAttributes();
            if (!secondFilterProgram.link()) {
                Log.e(LOG_TAG, "Fragment Shader Log = " + secondFilterProgram.getFragmentShaderLog());
                Log.e(LOG_TAG, "Vertex Shader Log = " + secondFilterProgram.getVertexShaderLog());
                Log.e(LOG_TAG, "Program Link Log = " + secondFilterProgram.getProgramLog());
                throw new RuntimeException("Fail to create Two Pass Filter program");
            }
            secondFilterPositionAttribute = secondFilterProgram.getAttributeIndex("position");
            secondFilterTextureCoordinateAttribute = secondFilterProgram.getAttributeIndex("inputTextureCoordinate");
            secondFilterInputTextureUniform = secondFilterProgram.getUniformIndex("inputImageTexture");
            secondFilterInputTextureUniform2 = secondFilterProgram.getUniformIndex("inputImageTexture2");

            secondFilterProgram.use();
            GLES20.glEnableVertexAttribArray(secondFilterPositionAttribute);
            GLES20.glEnableVertexAttribArray(secondFilterTextureCoordinateAttribute);
        }
    }

    public void initializeSecondaryAttributes() {
        secondFilterProgram.addAttribute("position");
        secondFilterProgram.addAttribute("inputTextureCoordinate");
    }

    @Override
    public DHImageFrameBuffer frameBufferForOutput() {
        return secondOutputFrameBuffer;
    }

    @Override
    public void removeOutputFrameBuffer() {
        secondOutputFrameBuffer = null;
    }

    @Override
    public void renderToTexture(float[] vertices, float[] texCoords) {
        if (preventRendering) {
            firstInputFrameBuffer.unlock();
            return;
        }
        DHImageContext.setActiveProgram(filterProgram);
        if (mSurface == null) {
            mSurface = DHImageContext.getCurrentContext().createOffScreenSurface((int) outputFrameSize().width, (int) outputFrameSize().height);
        }
        outputFrameBuffer = DHImageContext.getCurrentContext().sharedFrameBufferCache().fetchFrameBuffer(sizeOfFBO(), getOutputTextureOptions(), false);
        outputFrameBuffer.activate();

        setUniformsForProgram(0);
        GLES20.glClearColor(backgroundColorRed, backgroundColorGreen, backgroundColorBlue, backgroundColorAlpha);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, firstInputFrameBuffer.getTexture());
        GLES20.glUniform1i(filterInputTextureUniform, 2);

        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices).position(0);

        FloatBuffer texCoordsBuffer = ByteBuffer.allocateDirect(texCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoordsBuffer.put(texCoords).position(0);
        GLES20.glVertexAttribPointer(filterPositionAttribute, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(filterTexCoordAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoordsBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        firstInputFrameBuffer.unlock();
        firstInputFrameBuffer = null;

        secondOutputFrameBuffer = DHImageContext.getCurrentContext().sharedFrameBufferCache().fetchFrameBuffer(sizeOfFBO(), getOutputTextureOptions(), false);
        secondOutputFrameBuffer.activate();
        DHImageContext.setActiveProgram(secondFilterProgram);
        if (usingNextFrameForImageCapture) {
            secondOutputFrameBuffer.lock();
        }

        setUniformsForProgram(1);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, outputFrameBuffer.getTexture());
        GLES20.glUniform1i(secondFilterInputTextureUniform, 3);

        texCoordsBuffer = ByteBuffer.allocateDirect(texCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoordsBuffer.put(textureCoordinatesForRotation(DHImageRotationMode.NoRotation)).position(0);
        GLES20.glVertexAttribPointer(secondFilterTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoordsBuffer);
        GLES20.glVertexAttribPointer(secondFilterPositionAttribute, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glClearColor(backgroundColorRed, backgroundColorGreen, backgroundColorBlue, backgroundColorAlpha);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        outputFrameBuffer.unlock();
        outputFrameBuffer = null;

        secondOutputFrameBuffer.deactivate();
    }
}
