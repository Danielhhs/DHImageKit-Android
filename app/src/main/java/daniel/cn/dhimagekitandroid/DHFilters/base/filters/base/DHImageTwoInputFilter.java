package daniel.cn.dhimagekitandroid.DHFilters.base.filters.base;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLSurface;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageFrameBuffer;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageSurfaceTexture;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.executors.DHImageVideoProcessExecutor;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/29.
 */

public class DHImageTwoInputFilter extends DHImageFilter {
    public static String DH_TWO_INPUT_TEXTURE_VERTEX_SHADER_STRING = "attribute vec4 position;\n" +
            " attribute vec2 inputTextureCoordinate;\n" +
            " attribute vec2 inputTextureCoordinate2;\n" +
            " \n" +
            " varying vec2 textureCoordinate;\n" +
            " varying vec2 textureCoordinate2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_Position = position;\n" +
            "     textureCoordinate = inputTextureCoordinate;\n" +
            "     textureCoordinate2 = inputTextureCoordinate2;\n" +
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
        inputRotation2 = DHImageRotationMode.NoRotation;
        hasFirstTexture = false;
        hasReceivedFirstFrame = false;
        hasReceivedSecondFrame = false;
        firstFrameWasVideo = false;
        secondFrameWasVideo = false;

        firstFrameTime = -1.f;
        secondFrameTime = -1.f;

        filterSecondTextureCoordinateAttribute = filterProgram.getAttributeIndex("inputTextureCoordinate2");
        filterInputTextureUniform2 = filterProgram.getUniformIndex("inputImageTexture2");

        GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute);
    }

    public void disableFirstFrameCheck() {
        firstFrameCheckDisabled = true;
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
            firstInputFrameBuffer.unlock();
            secondInputFrameBuffer.unlock();
            return;
        }
//        if (mSurface == null) {
//            mSurface = DHImageContext.getCurrentContext().createOffScreenSurface((int) outputFrameSize().width, (int) outputFrameSize().height);
//        }
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
        texCoords2Buffer.put(texCoords2).position(0);

        GLES20.glEnableVertexAttribArray(filterPositionAttribute);
        GLES20.glVertexAttribPointer(filterPositionAttribute, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(filterTexCoordAttribute);
        GLES20.glVertexAttribPointer(filterTexCoordAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoordsBuffer);

        GLES20.glEnableVertexAttribArray(filterSecondTextureCoordinateAttribute);
        GLES20.glVertexAttribPointer(filterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoords2Buffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        outputFrameBuffer.deactivate();
        firstInputFrameBuffer.unlock();
        secondInputFrameBuffer.unlock();
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
    public void setInputFrame(DHImageFrameBuffer inputFrameBuffer, int index) {
        if (index == 0) {
            firstInputFrameBuffer = inputFrameBuffer;
            hasFirstTexture = true;
            if (firstInputFrameBuffer != null) {
                firstInputFrameBuffer.lock();
            }
        } else {
            secondInputFrameBuffer = inputFrameBuffer;
            if (secondInputFrameBuffer != null) {
                secondInputFrameBuffer.lock();
            }
        }
    }

    @Override
    public void setInputSize(DHImageSize size, int index) {
        if (index == 0) {
            super.setInputSize(size, index);
            if (size.isZeroSize()) {
                hasFirstTexture = false;
            }
        }
    }

    @Override
    public void setInputRotation(DHImageRotationMode rotationMode, int index) {
        if (index == 0) {
            inputRotationMode = rotationMode;
        } else {
            inputRotation2 = rotationMode;
        }
    }

    @Override
    public DHImageSize rotatedSize(DHImageSize sizeToRotate, int textureIndex) {
        DHImageSize rotatedSize = new DHImageSize(sizeToRotate);
        DHImageRotationMode rotationToCheck = null;
        if (textureIndex == 0) {
            rotationToCheck = inputRotationMode;
        } else {
            rotationToCheck = inputRotation2;
        }
        if (rotationToCheck.needToSwapWidthAndHeight()) {
            rotatedSize.width = sizeToRotate.height;
            rotatedSize.height = sizeToRotate.width;
        }
        return rotatedSize;
    }

    @Override
    public void newFrameReady(float time, int index) {
        if (hasReceivedFirstFrame && hasReceivedSecondFrame) {
            return;
        }

        if (index == 0) {
            hasReceivedFirstFrame = true;
            firstFrameTime = time;
            if (secondFrameCheckDisabled == true) {
                hasReceivedSecondFrame = true;
            }

        } else {
            hasReceivedSecondFrame = true;
            secondFrameTime = time;
        }

        if (hasReceivedSecondFrame && hasReceivedFirstFrame) {
            super.newFrameReady(time, 0);
            hasReceivedFirstFrame = false;
            hasReceivedSecondFrame = false;
        }
    }

}
