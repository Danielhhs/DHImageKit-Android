package daniel.cn.dhimagekitandroid.DHFilters.base.output;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.TextureView;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageFrameBuffer;
import daniel.cn.dhimagekitandroid.DHFilters.base.GLProgram;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageViewFillMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.executors.DHImageVideoProcessExecutor;
import daniel.cn.dhimagekitandroid.DHFilters.base.filters.DHImageFilter;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/20.
 */

public class DHImageViewRenderer implements GLSurfaceView.Renderer {

    public static final String LOG_TAG = "DHImageViewRenderer";

    static float noRotationTextureCoordinates[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    static float rotateRightTextureCoordinates[] = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };

    static float rotateLeftTextureCoordinates[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    static float verticalFlipTextureCoordinates[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };

    static float horizontalFlipTextureCoordinates[] = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    static float rotateRightVerticalFlipTextureCoordinates[] = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };

    static float rotateRightHorizontalFlipTextureCoordinates[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    static float rotate180TextureCoordinates[] = {
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
    };

    private DHImageViewFillMode fillMode;
    private DHImageSize sizeInPixels;
    private boolean enabled;

    protected DHImageRotationMode inputRotation;

    private DHImageFrameBuffer inputFrameBufferForDisplay;
    private GLProgram displayProgram;
    private int displayPositionAttribute, displayTexCoordsAttribute;
    private int displayTextureUniform;
    private DHImageSize inputImageSize;
    private float imageVertices[];
    private float backgroundColorRed, backgroudColorGreen, backgroundColorBlue, backgroundColorAlpha;
    private DHImageSize boundsSizeAtFrameBufferEpoch;
    private int aspectRatio;


    public void setBackgroundColor(float red, float green, float blue, float alpha) {
        backgroundColorRed = red;
        backgroudColorGreen = green;
        backgroundColorBlue = blue;
        backgroundColorAlpha = alpha;
    }

    public DHImageViewRenderer() {
    }

    private void commonInit() {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                //TO-DO: Use shared program instead;
                displayProgram = new GLProgram(DHImageFilter.DH_VERTEX_SHADER_STRING, DHImageFilter.DH_PASS_THROUGH_FRAGMENT_SHADER);
                if (displayProgram != null && displayProgram.isInitialized()) {
                    displayProgram.addAttribute("position");
                    displayProgram.addAttribute("inputTextureCoordinate");

                    if (displayProgram.link() != true) {
                        Log.e(LOG_TAG, "Program Log : " + displayProgram.getProgramLog());
                        Log.e(LOG_TAG, "Vertext Shader Log : " + displayProgram.getVertexShaderLog());
                        Log.e(LOG_TAG, "Fragment Shader Log : " + displayProgram.getFragmentShaderLog());
                        return;
                    }
                }

                displayPositionAttribute = displayProgram.getAttributeIndex("position");
                displayTexCoordsAttribute = displayProgram.getAttributeIndex("inputTextureCoordinate");
                displayTextureUniform = displayProgram.getUniformIndex("inputImageTexture");

                DHImageContext.setActiveProgram(displayProgram);
                GLES20.glEnableVertexAttribArray(displayPositionAttribute);
                GLES20.glEnableVertexAttribArray(displayTexCoordsAttribute);

                setBackgroundColor(0.f, 0.f, 0.f, 1.f);
                fillMode = DHImageViewFillMode.PreserveAspectRatio;
            }
        });
    }

    private void recalculateViewGeometry(final int width, final int height) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                float widthScaling = 1.f, heightScaling = 1.f;
                float scaledWidth, scaledHeight;
                if (inputImageSize.width > inputImageSize.height) {
                    scaledWidth = width;
                    scaledHeight = width * inputImageSize.height / inputImageSize.width;
                } else {
                    scaledHeight = height;
                    scaledWidth = height * inputImageSize.width / inputImageSize.height;
                }
                switch (fillMode) {
                    case Stretch: {
                        widthScaling = 1.f;
                        heightScaling = 1.f;
                    }break;
                    case PreserveAspectRatio: {
                        widthScaling = scaledWidth / width;
                        heightScaling = scaledHeight / height;
                    }break;
                    case PreserveAspectRatioAndFill: {
                        widthScaling = height / scaledHeight;
                        heightScaling = width / scaledWidth;
                    }break;
                }
                imageVertices[0] = -widthScaling;
                imageVertices[1] = -heightScaling;
                imageVertices[2] = widthScaling;
                imageVertices[3] = -heightScaling;
                imageVertices[4] = -widthScaling;
                imageVertices[5] = heightScaling;
                imageVertices[6] = widthScaling;
                imageVertices[7] = heightScaling;
            }
        });
    }

    private float[] textureCoordinateForRotation(DHImageRotationMode rotationMode) {
        switch (rotationMode) {
            case NoRotation: return noRotationTextureCoordinates;
            case Left: return rotateLeftTextureCoordinates;
            case Right: return rotateRightTextureCoordinates;
            case FlipVertical: return verticalFlipTextureCoordinates;
            case FlipHorizontal: return horizontalFlipTextureCoordinates;
            case RightFlipHorizontal:return rotateRightHorizontalFlipTextureCoordinates;
            case RightFlipVertical: return rotateRightVerticalFlipTextureCoordinates;
            case Rotate180: return rotate180TextureCoordinates;
        }
        return noRotationTextureCoordinates;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        commonInit();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        DHImageContext.setActiveProgram(displayProgram);
        GLES20.glClearColor(backgroundColorRed, backgroudColorGreen, backgroundColorBlue, backgroundColorAlpha);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputFrameBufferForDisplay.getTexture());
        GLES20.glUniform1i(displayTextureUniform, 4);

        GLES20.glVertexAttribPointer(displayPositionAttribute, 2, GLES20.GL_FLOAT, false, 0, FloatBuffer.wrap(imageVertices));
        GLES20.glVertexAttribPointer(displayTexCoordsAttribute, 2, GLES20.GL_FLOAT, false, 0, FloatBuffer.wrap(textureCoordinateForRotation(inputRotation)));

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        inputFrameBufferForDisplay.unlock();;
        inputFrameBufferForDisplay = null;

    }

    public DHImageFrameBuffer getInputFrameBufferForDisplay() {
        return inputFrameBufferForDisplay;
    }

    public void setInputFrameBufferForDisplay(DHImageFrameBuffer inputFrameBufferForDisplay) {
        this.inputFrameBufferForDisplay = inputFrameBufferForDisplay;
    }

    public DHImageRotationMode getInputRotation() {
        return inputRotation;
    }

    public void setInputRotation(DHImageRotationMode inputRotation) {
        this.inputRotation = inputRotation;
    }

    public DHImageSize getInputImageSize() {
        return inputImageSize;
    }

    public void setInputImageSize(final DHImageSize inputImageSize) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageSize rotatedSize = new DHImageSize(inputImageSize.width, inputImageSize.height);
                if (inputRotation == DHImageRotationMode.Left || inputRotation == DHImageRotationMode.Right || inputRotation == DHImageRotationMode.RightFlipHorizontal
                        || inputRotation == DHImageRotationMode.RightFlipVertical) {
                    rotatedSize.width = inputImageSize.height;
                    rotatedSize.height = inputImageSize.width;
                }
                if (!rotatedSize.equals(inputImageSize)) {
                    recalculateViewGeometry((int)rotatedSize.width, (int)rotatedSize.height);
                }
            }
        });
        this.inputImageSize = inputImageSize;
    }
}
