package daniel.cn.dhimagekitandroid.DHFilters.base.filters.base;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageFrameBuffer;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2018/1/4.
 */

public class DHImageThreeInputFilter extends DHImageTwoInputFilter {
    public static String DH_THREE_INPUT_TEXTURE_VERTEX_SHADER = "attribute vec4 position;\n" +
            " attribute vec4 inputTextureCoordinate;\n" +
            " attribute vec4 inputTextureCoordinate2;\n" +
            " attribute vec4 inputTextureCoordinate3;\n" +
            " \n" +
            " varying vec2 textureCoordinate;\n" +
            " varying vec2 textureCoordinate2;\n" +
            " varying vec2 textureCoordinate3;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_Position = position;\n" +
            "     textureCoordinate = inputTextureCoordinate.xy;\n" +
            "     textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            "     textureCoordinate3 = inputTextureCoordinate3.xy;\n" +
            " }";

    protected DHImageFrameBuffer thirdInputFrameBuffer;
    protected int filterThirdTextureCoordinateAttribute;
    protected int filterInputTextureUnfiorm3;
    protected DHImageRotationMode inputRotation3;
    protected int filterSourceTexture3;
    float thirdFrameTime;

    protected boolean hasSetSecondTexture, hasReceivedThirdFrame, thirdFrameWasVideo;
    protected boolean thirdFrameCheckDisabled;

    public DHImageThreeInputFilter(String fragmentShader) {
        this(DH_THREE_INPUT_TEXTURE_VERTEX_SHADER, fragmentShader);
    }

    public DHImageThreeInputFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        inputRotation3 = DHImageRotationMode.NoRotation;
        hasSetSecondTexture = false;
        hasReceivedThirdFrame = false;
        thirdFrameWasVideo = false;
        thirdFrameCheckDisabled = false;
        thirdFrameTime = 0.f;

        filterThirdTextureCoordinateAttribute = filterProgram.getAttributeIndex("inputTextureCoordinate3");
        filterInputTextureUnfiorm3 = filterProgram.getUniformIndex("inputImageTexture3");
        GLES20.glEnableVertexAttribArray(filterThirdTextureCoordinateAttribute);
    }

    @Override
    public void initializeAttributes() {
        super.initializeAttributes();
        filterProgram.addAttribute("inputTextureCoordinate3");
    }

    public void disableThirdFrameCheck() {
        thirdFrameCheckDisabled = true;
    }

    @Override
    public void renderToTexture(float[] vertices, float[] texCoords) {
        if (preventRendering) {
            firstInputFrameBuffer.unlock();
            secondInputFrameBuffer.unlock();
            thirdInputFrameBuffer.unlock();
            return;
        }
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

        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, thirdInputFrameBuffer.getTexture());
        GLES20.glUniform1i(filterInputTextureUnfiorm3, 4);

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

        float texCoords3[] = textureCoordinatesForRotation(inputRotation3);
        FloatBuffer texCoords3Buffer = ByteBuffer.allocateDirect(texCoords3.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoords3Buffer.put(texCoords3).position(0);

        GLES20.glVertexAttribPointer(filterPositionAttribute, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(filterTexCoordAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoordsBuffer);
        GLES20.glVertexAttribPointer(filterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoords2Buffer);
        GLES20.glVertexAttribPointer(filterThirdTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoords3Buffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        firstInputFrameBuffer.unlock();
        secondInputFrameBuffer.unlock();
        thirdInputFrameBuffer.unlock();

        outputFrameBuffer.deactivate();
    }

    @Override
    public int nextAvailableTextureIndex() {
        if (hasSetSecondTexture) {
            return 2;
        } else if (hasSetFirstTexture) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void setInputFrame(DHImageFrameBuffer inputFrameBuffer, int index) {
        if (index == 0) {
            firstInputFrameBuffer = inputFrameBuffer;
            hasSetFirstTexture = true;
            firstInputFrameBuffer.lock();
        } else if (index == 1) {
            secondInputFrameBuffer = inputFrameBuffer;
            hasSetSecondTexture = true;
            secondInputFrameBuffer.lock();
        } else {
            thirdInputFrameBuffer = inputFrameBuffer;
            thirdInputFrameBuffer.lock();
        }
    }

    @Override
    public void setInputSize(DHImageSize size, int index) {
        if (index == 0) {
            super.setInputSize(size, 0);
            if (size.isZeroSize()) {
                hasSetFirstTexture = false;
            }
        } else if (index == 1) {
            if (size.isZeroSize()) {
                hasSetSecondTexture = false;
            }
        }
    }

    @Override
    public void setInputRotation(DHImageRotationMode rotationMode, int index) {
        if (index == 0) {
            inputRotationMode = rotationMode;
        } else if (index == 1) {
            inputRotation2 = rotationMode;
        } else {
            inputRotation3 = rotationMode;
        }
    }

    @Override
    public DHImageSize rotatedSize(DHImageSize sizeToRotate, int textureIndex) {
        DHImageSize rotatedSize = new DHImageSize(sizeToRotate);
        DHImageRotationMode rotationToCheck;
        if (textureIndex == 0) {
            rotationToCheck = inputRotationMode;
        } else if (textureIndex == 1) {
            rotationToCheck = inputRotation2;
        } else {
            rotationToCheck = inputRotation3;
        }
        if (rotationToCheck.needToSwapWidthAndHeight()) {
            rotatedSize.width = sizeToRotate.height;
            rotatedSize.height = sizeToRotate.width;
        }
        return rotatedSize;
    }

    @Override
    public void newFrameReady(float time, int index) {
        if (hasReceivedFirstFrame && hasReceivedSecondFrame && hasReceivedThirdFrame) {
            return;
        }

        if (index == 0) {
            hasReceivedFirstFrame = true;
            firstFrameTime = time;
            if (secondFrameCheckDisabled == true) {
                hasReceivedSecondFrame = true;
            }
            if (thirdFrameCheckDisabled == true) {
                hasReceivedThirdFrame = true;
            }
        } else if (index == 1) {
            hasReceivedSecondFrame = true;
            secondFrameTime = time;
            if (firstFrameCheckDisabled == true) {
                hasReceivedFirstFrame = true;
            }
            if (thirdFrameCheckDisabled == true) {
                hasReceivedThirdFrame = true;
            }
        } else {
            hasReceivedThirdFrame = true;
            thirdFrameTime = time;
            if (firstFrameCheckDisabled == true) {
                hasReceivedFirstFrame = true;
            }
            if (secondFrameCheckDisabled == true) {
                hasReceivedSecondFrame = true;
            }
        }

        if (hasReceivedFirstFrame && hasReceivedSecondFrame && hasReceivedThirdFrame) {
            float imageVertices[] = {
                    -1.0f, -1.0f,
                    1.0f, -1.0f,
                    -1.0f,  1.0f,
                    1.0f,  1.0f,
            };
            renderToTexture(imageVertices, textureCoordinatesForRotation(inputRotationMode));
            informTargetsAboutNewFrameReadyAtTime(time);
            hasReceivedFirstFrame = false;
            hasReceivedSecondFrame = false;
            hasReceivedThirdFrame = false;
        }
    }
}
