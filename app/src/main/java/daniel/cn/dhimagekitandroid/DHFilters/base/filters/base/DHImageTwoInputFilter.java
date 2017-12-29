package daniel.cn.dhimagekitandroid.DHFilters.base.filters.base;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLSurface;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageFrameBuffer;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageSurfaceTexture;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.executors.DHImageVideoProcessExecutor;

/**
 * Created by huanghongsen on 2017/12/29.
 */

public class DHImageTwoInputFilter extends DHImageFilter {
    public static String DH_TWO_INPUT_TEXTURE_VERTEX_SHADER_STRING = "attribute vec4 position;\n" +
            " attribute vec4 inputTextureCoordinate;\n" +
            " attribute vec4 inputTextureCoordinate2;\n" +
            " \n" +
            " varying vec2 textureCoordinate;\n" +
            " varying vec2 textureCoordinate2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_Position = position;\n" +
            "     textureCoordinate = inputTextureCoordinate.xy;\n" +
            "     textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            " }";

    protected DHImageFrameBuffer secondInputFrameBuffer;
    protected int filterSecondTextureCoordinateAttribute;
    protected int filterInputTextureUniform2;
    protected DHImageRotationMode inputRotation2;

    protected float firstFrameTime, secondFrameTime;

    protected boolean hasFirstTexture, hasReceivedFirstFrame, hasReceivedSecondFrame, firstFrameWasVideo, secondFrameWasVideo;
    protected boolean firstFrameCheckDisabled, secondFrameCheckDisabled;

    public DHImageTwoInputFilter(String fragmentShaderString) {
        this(DH_TWO_INPUT_TEXTURE_VERTEX_SHADER_STRING, fragmentShaderString);
    }

    public DHImageTwoInputFilter(String vertexShaderString, String fragmentShaderString) {
        super(vertexShaderString, fragmentShaderString);
    }

    public void disableFirstFrameCheck() {
        firstFrameCheckDisabled = true;
        inputRotation2 = DHImageRotationMode.NoRotation;
        hasFirstTexture = false;
        hasReceivedFirstFrame = false;
        hasReceivedSecondFrame = false;
        firstFrameWasVideo = false;
        secondFrameWasVideo = false;

        firstFrameTime = -1.f;
        secondFrameTime = -1.f;

        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                filterSecondTextureCoordinateAttribute = filterProgram.getAttributeIndex("inputTextureCoordinate2");
                filterInputTextureUniform2 = filterProgram.getUniformIndex("inputImageTexture2");

                GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute);
            }
        });
    }

    @Override
    public void initializeAttributes() {
        super.initializeAttributes();
        filterProgram.addAttribute("inputTextureCoordinate2");
    }

    public void disableSecondFrameCheck() {
        secondFrameCheckDisabled = true;
    }

    @Override
    public void renderToTexture(final float[] vertices, final float[] texCoords) {
        if (preventRendering) {
            //TO-DO: Unlock input frame buffers
            return;
        }
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(filterProgram);
                outputFrameBuffer = DHImageContext.sharedFrameBufferCache().fetchFrameBuffer(sizeOfFBO(), getOutputTextureOptions(), false);
                outputFrameBuffer.activate();
                if (usingNextFrameForImageCapture) {
                    outputFrameBuffer.lock();
                }
                setUniformsForProgram(0);

                GLES20.glClearColor(backgroundColorRed, backgroundColorGreen, backgroundColorBlue, backgroundColorAlpha);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, firstInputFrameBuffer.getTexture());
                GLES20.glUniform1i(filterInputTextureUniform, 2);

                GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, secondInputFrameBuffer.getTexture());
                GLES20.glUniform1i(filterInputTextureUniform2, 3);

                FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
                vertexBuffer.put(vertices).position(0);

                FloatBuffer texCoordsBuffer = ByteBuffer.allocateDirect(texCoords.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
                texCoordsBuffer.put(texCoords).position(0);

                float texCoords2[] = textureCoordinatesForRotation(inputRotation2);
                FloatBuffer texCoords2Buffer = ByteBuffer.allocateDirect(texCoords2.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
                texCoords2Buffer.put(texCoords).position(0);

                GLES20.glEnableVertexAttribArray(filterPositionAttribute);
                GLES20.glVertexAttribPointer(filterPositionAttribute, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

                GLES20.glEnableVertexAttribArray(filterTexCoordAttribute);
                GLES20.glVertexAttribPointer(filterTexCoordAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoordsBuffer);

                GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute);
                GLES20.glVertexAttribPointer(filterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoords2Buffer);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


            }
        });
    }

    @Override
    public int nextAvailableTextureIndex() {
        if (hasFirstTexture) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void setInputFrame(EGLSurface surface, DHImageFrameBuffer inputFrameBuffer, int index) {

    }
}
