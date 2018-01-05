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
 * Created by huanghongsen on 2018/1/5.
 */

public class DHImageSixInputFilter extends DHImageFiveInputFilter {

    public static String DH_SIX_INPUT_TEXTURE_FRAGMENT_SHADER = "attribute vec4 position;\n" +
            " attribute vec4 inputTextureCoordinate;\n" +
            " attribute vec4 inputTextureCoordinate2;\n" +
            " attribute vec4 inputTextureCoordinate3;\n" +
            " attribute vec4 inputTextureCoordinate4;\n" +
            " attribute vec4 inputTextureCoordinate5;\n" +
            " attribute vec4 inputTextureCoordinate6;\n" +
            " \n" +
            " varying vec2 textureCoordinate;\n" +
            " varying vec2 textureCoordinate2;\n" +
            " varying vec2 textureCoordinate3;\n" +
            " varying vec2 textureCoordinate4;\n" +
            " varying vec2 textureCoordinate5;\n" +
            " varying vec2 textureCoordinate6;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_Position = position;\n" +
            "     textureCoordinate = inputTextureCoordinate.xy;\n" +
            "     textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            "     textureCoordinate3 = inputTextureCoordinate3.xy;\n" +
            "     textureCoordinate4 = inputTextureCoordinate4.xy;\n" +
            "     textureCoordinate5 = inputTextureCoordinate5.xy;\n" +
            "     textureCoordinate6 = inputTextureCoordinate6.xy;\n" +
            " }";

    protected DHImageFrameBuffer sixthInputFrameBuffer;
    protected int filterSixthTextureCoordinateAttribute;
    protected int filterInputTextureUniform6;
    protected DHImageRotationMode inputRotation6;
    protected int filterSourceTexture6;
    protected float sixthFrameTime;
    protected boolean hasSetFifthTexture, hasReceivedSixthFrame, sixthFrameWasVideo;
    protected boolean sixthFrameCheckDisabled;

    public DHImageSixInputFilter(String fragmentShader) {
        this(DH_SIX_INPUT_TEXTURE_FRAGMENT_SHADER, fragmentShader);
    }

    public DHImageSixInputFilter(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);

        inputRotation6 = DHImageRotationMode.NoRotation;
        hasSetFifthTexture = false;
        hasReceivedSixthFrame = false;
        sixthFrameWasVideo = false;
        sixthFrameCheckDisabled = false;

        sixthFrameTime = 0.f;

        filterSixthTextureCoordinateAttribute = filterProgram.getAttributeIndex("inputTextureCoordinate6");
        filterInputTextureUniform6 = filterProgram.getUniformIndex("inputImageTexture6");

        GLES20.glEnableVertexAttribArray(filterSixthTextureCoordinateAttribute);
    }

    @Override
    public void initializeAttributes() {
        super.initializeAttributes();
        filterProgram.addAttribute("inputTextureCoordinate6");
    }

    public void disableSixthFrameCheck() {
        sixthFrameCheckDisabled = true;
    }

    @Override
    public void renderToTexture(float[] vertices, float[] texCoords) {
        if (preventRendering) {
            firstInputFrameBuffer.unlock();
            secondInputFrameBuffer.unlock();
            thirdInputFrameBuffer.unlock();
            fourthInputFrameBuffer.unlock();
            fifthInputFrameBuffer.unlock();
            sixthInputFrameBuffer.unlock();
            return;
        }

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

        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fourthInputFrameBuffer.getTexture());
        GLES20.glUniform1i(filterInputTextureUniform4, 5);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fifthInputFrameBuffer.getTexture());
        GLES20.glUniform1i(filterInputTextureUniform5, 6);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE7);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sixthInputFrameBuffer.getTexture());
        GLES20.glUniform1i(filterInputTextureUniform6, 7);

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

        float texCoords4[] = textureCoordinatesForRotation(inputRotation4);
        FloatBuffer texCoords4Buffer = ByteBuffer.allocateDirect(texCoords4.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoords4Buffer.put(texCoords4).position(0);

        float texCoords5[] = textureCoordinatesForRotation(inputRotation5);
        FloatBuffer texCoords5Buffer = ByteBuffer.allocateDirect(texCoords5.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoords5Buffer.put(texCoords5).position(0);

        float texCoords6[] = textureCoordinatesForRotation(inputRotation6);
        FloatBuffer texCoords6Buffer = ByteBuffer.allocateDirect(texCoords6.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoords6Buffer.put(texCoords6).position(0);

        GLES20.glVertexAttribPointer(filterPositionAttribute, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(filterTexCoordAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoordsBuffer);
        GLES20.glVertexAttribPointer(filterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoords2Buffer);
        GLES20.glVertexAttribPointer(filterThirdTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoords3Buffer);
        GLES20.glVertexAttribPointer(filterFourthTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoords4Buffer);
        GLES20.glVertexAttribPointer(filterFifthTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoords5Buffer);
        GLES20.glVertexAttribPointer(filterSixthTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoords6Buffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        firstInputFrameBuffer.unlock();
        secondInputFrameBuffer.unlock();
        thirdInputFrameBuffer.unlock();
        fourthInputFrameBuffer.unlock();
        fifthInputFrameBuffer.unlock();
        sixthInputFrameBuffer.unlock();

        outputFrameBuffer.deactivate();
    }

    @Override
    public int nextAvailableTextureIndex() {
        if (hasSetFifthTexture) {
            return 5;
        } else if (hasSetFourthTexture) {
            return 4;
        } else if (hasSetThirdTexture) {
            return 3;
        } else if (hasSetSecondTexture) {
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
        } else if (index == 2) {
            thirdInputFrameBuffer = inputFrameBuffer;
            hasSetThirdTexture = true;
            thirdInputFrameBuffer.lock();
        } else if (index == 3) {
            fourthInputFrameBuffer = inputFrameBuffer;
            hasSetFourthTexture = true;
            fourthInputFrameBuffer.lock();
        } else if (index == 4) {
            fifthInputFrameBuffer = inputFrameBuffer;
            hasSetFifthTexture = true;
            fifthInputFrameBuffer.lock();
        } else {
            sixthInputFrameBuffer = inputFrameBuffer;
            sixthInputFrameBuffer.lock();
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
        } else if (index == 2) {
            if (size.isZeroSize()) {
                hasSetThirdTexture = false;
            }
        } else if (index == 3) {
            if (size.isZeroSize()) {
                hasSetFourthTexture = false;
            }
        } else if (index == 4) {
            if (size.isZeroSize()) {
                hasSetFifthTexture = false;
            }
        }
    }

    @Override
    public void setInputRotation(DHImageRotationMode rotationMode, int index) {
        if (index == 0) {
            inputRotationMode = rotationMode;
        } else if (index == 1) {
            inputRotation2 = rotationMode;
        } else if (index == 2) {
            inputRotation3 = rotationMode;
        } else if (index == 3) {
            inputRotation4 = rotationMode;
        } else if (index == 4) {
            inputRotation5 = rotationMode;
        } else {
            inputRotation6 = rotationMode;
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
        } else if (textureIndex == 2) {
            rotationToCheck = inputRotation3;
        } else if (textureIndex == 3) {
            rotationToCheck = inputRotation4;
        } else if (textureIndex == 4) {
            rotationToCheck = inputRotation5;
        } else {
            rotationToCheck = inputRotation6;
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
            if (fourthFrameCheckDisabled == true) {
                hasReceivedFourthFrame = true;
            }
            if (fifthFrameCheckDisabled == true) {
                hasReceivedFifthFrame = true;
            }
            if (sixthFrameCheckDisabled == true) {
                hasReceivedSixthFrame = true;
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
            if (fourthFrameCheckDisabled == true) {
                hasReceivedFourthFrame = true;
            }
            if (fifthFrameCheckDisabled == true) {
                hasReceivedFifthFrame = true;
            }
            if (sixthFrameCheckDisabled == true) {
                hasReceivedSixthFrame = true;
            }
        } else if (index == 2) {
            hasReceivedThirdFrame = true;
            thirdFrameTime = time;
            if (firstFrameCheckDisabled == true) {
                hasReceivedFirstFrame = true;
            }
            if (secondFrameCheckDisabled == true) {
                hasReceivedSecondFrame = true;
            }
            if (fourthFrameCheckDisabled == true) {
                hasReceivedFourthFrame = true;
            }
            if (fifthFrameCheckDisabled == true) {
                hasReceivedFifthFrame = true;
            }
            if (sixthFrameCheckDisabled == true) {
                hasReceivedSixthFrame = true;
            }
        } else if (index == 3) {
            hasReceivedFourthFrame = true;
            fourthFrameTime = time;
            if (firstFrameCheckDisabled == true) {
                hasReceivedFirstFrame = true;
            }
            if (secondFrameCheckDisabled == true) {
                hasReceivedSecondFrame = true;
            }
            if (thirdFrameCheckDisabled == true) {
                hasReceivedThirdFrame = true;
            }
            if (fifthFrameCheckDisabled == true) {
                hasReceivedFifthFrame = true;
            }
            if (sixthFrameCheckDisabled == true) {
                hasReceivedSixthFrame = true;
            }
        } else if (index == 4) {
            hasReceivedFifthFrame = true;
            fifthFrameTime = time;
            if (firstFrameCheckDisabled == true) {
                hasReceivedFirstFrame = true;
            }
            if (secondFrameCheckDisabled == true) {
                hasReceivedSecondFrame = true;
            }
            if (thirdFrameCheckDisabled == true) {
                hasReceivedThirdFrame = true;
            }
            if (fourthFrameCheckDisabled == true) {
                hasReceivedFourthFrame = true;
            }
            if (sixthFrameCheckDisabled == true) {
                hasReceivedSixthFrame = true;
            }
        } else {
            hasReceivedSixthFrame = true;
            sixthFrameTime = time;
            if (firstFrameCheckDisabled == true) {
                hasReceivedFirstFrame = true;
            }
            if (secondFrameCheckDisabled == true) {
                hasReceivedSecondFrame = true;
            }
            if (thirdFrameCheckDisabled == true) {
                hasReceivedThirdFrame = true;
            }
            if (fourthFrameCheckDisabled == true) {
                hasReceivedFourthFrame = true;
            }
            if (fifthFrameCheckDisabled == true) {
                hasReceivedFifthFrame = true;
            }
        }

        if (hasReceivedFirstFrame && hasReceivedSecondFrame && hasReceivedThirdFrame && hasReceivedFourthFrame) {
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
            hasReceivedFourthFrame = false;
            hasReceivedFifthFrame = false;
            hasReceivedSixthFrame = false;
        }
    }
}
