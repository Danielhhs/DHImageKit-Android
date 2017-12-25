package daniel.cn.dhimagekitandroid.DHFilters.base.filters;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.GLProgram;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.executors.DHImageVideoProcessExecutor;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageInput;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImageFrameBuffer;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImageOutput;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImageSurfaceTexture;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImagePoint;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHMatrix3X3;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHMatrix4X4;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector3;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHVector4;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHImageFilter extends DHImageOutput implements IDHImageInput {

    public static final String DH_VERTEX_SHADER_STRING = " attribute vec4 position;\n" +
            " attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            " varying vec2 textureCoordinate;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_Position = position;\n" +
            "     textureCoordinate = inputTextureCoordinate.xy;\n" +
            " }";

    public static final String DH_PASS_THROUGH_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            " }";
//
    static float imageVertices[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f,  1.0f,
            1.0f,  1.0f,
    };

    static float noRotationTextureCoordinates[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };

    static float rotateLeftTextureCoordinates[] = {
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };

    static float rotateRightTextureCoordinates[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    static float verticalFlipTextureCoordinates[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f,  0.0f,
            1.0f,  0.0f,
    };

    static float horizontalFlipTextureCoordinates[] = {
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f,  1.0f,
            0.0f,  1.0f,
    };

    static float rotateRightVerticalFlipTextureCoordinates[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    static float rotateRightHorizontalFlipTextureCoordinates[] = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
    };

    static float rotate180TextureCoordinates[] = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    private Semaphore semaphore;

    protected EGLSurface mSourceSurface;
    protected DHImageSurfaceTexture firstInputSurfaceTexture;
    protected GLProgram filterProgram;
    protected int filterPositionAttribute;
    protected int filterTexCoordAttribute;
    protected int filterInputTextureUniform;
    protected float backgroundColorRed, backgroundColorGreen, backgroundColorBlue, backgroundColorAlpha;
    protected boolean isEndProcessing;
    protected DHImageSize currentFilterSize;
    protected DHImageRotationMode inputRotationMode;

    public boolean currentlyReceivingMonochromeInput;
    public boolean preventRendering;

    public DHImageFilter() {
        this(DH_VERTEX_SHADER_STRING, DH_PASS_THROUGH_FRAGMENT_SHADER);
    }

    public DHImageFilter(String fragmentShaderString) {
        this(DH_VERTEX_SHADER_STRING, fragmentShaderString);
    }

    public DHImageFilter(String vertexShaderString, String fragmentShaderString) {
        semaphore = new Semaphore(1);
        preventRendering = false;
        currentlyReceivingMonochromeInput = false;
        inputRotationMode = DHImageRotationMode.NoRotation;
        backgroundColorRed = 0.f;
        backgroundColorGreen = 0.f;
        backgroundColorBlue = 0.f;
        backgroundColorAlpha = 0.f;

        //TO-DO: Run on video processing Queue
        //TO-DO: Add program cache
        filterProgram = new GLProgram(vertexShaderString, fragmentShaderString);
        if (filterProgram != null && !filterProgram.isInitialized()) {
            initializeAttributes();
            boolean linked = filterProgram.link();
            if (linked == false) {
                Log.e("DHImage", "Program Link Log" + filterProgram.getProgramLog());
                Log.e("DHImage", "Fragment Shader Log" + filterProgram.getFragmentShaderLog());
                Log.e("DHImage", "Vertex Shader Log" + filterProgram.getVertexShaderLog());
                return;
            }
        }
        filterPositionAttribute = filterProgram.getAttributeIndex("position");
        filterTexCoordAttribute = filterProgram.getAttributeIndex("inputTextureCoordinate");
        filterInputTextureUniform = filterProgram.getUniformIndex("inputImageTexture");

        GLES20.glEnableVertexAttribArray(filterPositionAttribute);
        GLES20.glEnableVertexAttribArray(filterTexCoordAttribute);

    }

    public void initializeAttributes() {
        filterProgram.addAttribute("position");
        filterProgram.addAttribute("inputTextureCoordinate");
    }

    public void setupFilterForSize(DHImageSize size) {
        mSurface = DHImageContext.getCurrentContext().createOffScreenSurface((int)size.width, (int)size.height);
        frameBuffer = new DHImageFrameBuffer(size);
        mOutputSurfaceTexture = DHImageContext.getCurrentContext().createSurfaceTexture(frameBuffer.getTexture(), mSurface);
    }

    public DHImageSize rotatedSize(DHImageSize sizeToRotate, int textureIndex) {
        DHImageSize rotatedSize = new DHImageSize(sizeToRotate.width, sizeToRotate.height);
        if (inputRotationMode == DHImageRotationMode.Left || inputRotationMode == DHImageRotationMode.Right || inputRotationMode == DHImageRotationMode.RightFlipHorizontal
                || inputRotationMode == DHImageRotationMode.RightFlipVertical) {
            rotatedSize.width = sizeToRotate.height;
            rotatedSize.height = sizeToRotate.width;
        }
        return rotatedSize;
    }

    public DHImagePoint rotatedPoint(DHImagePoint pointToRotate, DHImageRotationMode rotationMode) {
        DHImagePoint rotatedPoint = new DHImagePoint(pointToRotate.x, pointToRotate.y);
        switch (rotationMode) {
            case NoRotation: break;
            case FlipHorizontal: {
                rotatedPoint.x = 1.f - pointToRotate.x;
                rotatedPoint.y = pointToRotate.y;
            } break;
            case FlipVertical: {
                rotatedPoint.x = pointToRotate.x;
                rotatedPoint.y = 1.f - pointToRotate.y;
            }break;
            case Left: {
                rotatedPoint.x = 1.f - pointToRotate.y;
                rotatedPoint.y = pointToRotate.x;
            }break;
            case Right: {
                rotatedPoint.x = pointToRotate.y;
                rotatedPoint.y = 1.f - pointToRotate.x;
            } break;
            case RightFlipHorizontal: {
                rotatedPoint.x = 1.f - pointToRotate.y;
                rotatedPoint.y = 1.f - pointToRotate.x;
            }break;
            case RightFlipVertical: {
                rotatedPoint.x = pointToRotate.y;
                rotatedPoint.y = pointToRotate.x;
            }break;
            case Rotate180: {
                rotatedPoint.x = 1.f - pointToRotate.x;
                rotatedPoint.y = 1.f - pointToRotate.y;
            }break;
        }
        return rotatedPoint;
    }

    public DHImageSize sizeOfFBO() {
        DHImageSize outputSize = maximumOutputSize();
        if (outputSize.isZeroSize() || inputTextureSize.width < outputSize.width) {
            return inputTextureSize;
        } else {
            return outputSize;
        }
    }

    public void renderToTexture(float[] vertices, float[]texCoords) {
        DHImageContext.getCurrentContext().makeSurfaceCurrent(mSurface, mSourceSurface);
        if (preventRendering) {
            return;
        }

        frameBuffer.activate();

        DHImageContext.setActiveProgram(filterProgram);

        setUniformsForProgram(0);
        GLES20.glClearColor(backgroundColorRed, backgroundColorGreen, backgroundColorBlue, backgroundColorAlpha);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, firstInputSurfaceTexture.getTexture());
        GLES20.glUniform1i(filterInputTextureUniform, 2);

        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices).position(0);

        FloatBuffer texCoordsBuffer = ByteBuffer.allocateDirect(texCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoordsBuffer.put(texCoords).position(0);

        GLES20.glVertexAttribPointer(filterPositionAttribute, 2, GLES20.GL_FLOAT, false, 0,vertexBuffer);
        GLES20.glVertexAttribPointer(filterTexCoordAttribute, 2, GLES20.GL_FLOAT, false, 0, texCoordsBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        frameBuffer.deactivate();
        convertToBitMap();
//        firstInputFrameBuffer.unlock();
        if (usingNextFrameForImageCapture) {
            semaphore.release();
        }
    }

    public void informTargetsAboutNewFrameReadyAtTime(float time) {
        //TO-DO: Call frame completion block
        for (IDHImageInput target : targets) {
            if (!target.equals(getTargetToIgnoreForUpdates())) {
                int indexOfObject = targets.indexOf(target);
                int textureIndex = targetTextureIndices.get(indexOfObject);
                target.setInputSurfaceTexture(mSurface, surfaceTextureForOutput(), textureIndex);
                target.setInputSize(outputFrameSize(), textureIndex);
                target.newFrameReady(time, textureIndex);
            }
        }
    }


    public DHImageSize outputFrameSize() {
        return inputTextureSize;
    }

    public void setBackgroundColor(float red, float green, float blue, float alpha) {
        backgroundColorRed = red;
        backgroundColorAlpha = alpha;
        backgroundColorBlue = blue;
        backgroundColorGreen = green;
    }

    public void setIntegerUniform(int value, String uniformName) {
        int uniformLocation = filterProgram.getUniformIndex(uniformName);
        setIntegerUniform(value, uniformLocation, filterProgram);
    }

    public void setIntegerUniform(final int value, final int uniformLocation, final GLProgram program) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(program);
                GLES20.glUniform1i(uniformLocation, value);
            }
        });
    }

    public void setFloatUniform(float value, String uniformName) {
        int uniformLocation = filterProgram.getUniformIndex(uniformName);
        setFloatUniform(value, uniformLocation, filterProgram);
    }

    public void setFloatUniform(final float value, final int uniformLocation, final GLProgram program) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(program);
                GLES20.glUniform1f(uniformLocation, value);
            }
        });
    }

    public void setSizeUniform(DHImageSize size, String uniformName) {
        int uniformLocation = filterProgram.getUniformIndex(uniformName);
        setSizeUniform(size, uniformLocation, filterProgram);
    }

    public void setSizeUniform(final DHImageSize size, final int uniformLocation, final GLProgram program) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(program);
                float array[] = {size.width, size.height};

                GLES20.glUniform2fv(uniformLocation, 1, FloatBuffer.wrap(array));
            }
        });
    }

    public void setPointUniform(DHImagePoint point, String uniformName) {
        int uniformLocation = filterProgram.getUniformIndex(uniformName);
        setPointUniform(point, uniformLocation, filterProgram);
    }

    public void setPointUniform(final DHImagePoint point, final int uniformLocation, final GLProgram program) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(program);
                float array[] = {point.x, point.y};

                GLES20.glUniform2fv(uniformLocation, 1, FloatBuffer.wrap(array));
            }
        });
    }

    public void setFloatVec3Uniform(DHVector3 vector, String uniformName) {
        int uniformLocation = filterProgram.getUniformIndex(uniformName);
        setFloatVec3Uniform(vector, uniformLocation, filterProgram);
    }

    public void setFloatVec3Uniform(final DHVector3 vector, final int uniformLocation, final GLProgram program) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(program);
                float array[] = {vector.one, vector.two, vector.three};

                GLES20.glUniform3fv(uniformLocation, 1, FloatBuffer.wrap(array));
            }
        });
    }

    public void setFloatVec4Uniform(DHVector4 vector, String uniformName) {
        int uniformLocation = filterProgram.getUniformIndex(uniformName);
        setFloatVec4Uniform(vector, uniformLocation, filterProgram);
    }

    public void setFloatVec4Uniform(final DHVector4 vector, final int uniformLocation, final GLProgram program) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(program);
                float array[] = {vector.one, vector.two, vector.three, vector.four};

                GLES20.glUniform4fv(uniformLocation, 1, FloatBuffer.wrap(array));
            }
        });
    }

    public void setFloatArrayUniform(float[] arrayValue, int length, String uniformName) {
        int uniformLocation = filterProgram.getUniformIndex(uniformName);
        setFloatArrayUniform(arrayValue, length, uniformLocation, filterProgram);
    }

    public void setFloatArrayUniform(final float[] arrayValue, final int length, final int uniformLocation, final GLProgram program) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(program);

                GLES20.glUniform1fv(uniformLocation, 1, arrayValue, length);
            }
        });
    }

    public  void setMatrix3X3Uniform(DHMatrix3X3 matrix, String uniformName) {
        int uniformLocation = filterProgram.getUniformIndex(uniformName);
        setMatrix3X3Uniform(matrix, uniformLocation, filterProgram);
    }

    public void setMatrix3X3Uniform(final DHMatrix3X3 matrix, final int uniformLocation, final GLProgram program) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(program);
                float array[] = {matrix.one.one, matrix.one.two, matrix.one.three,
                matrix.two.one, matrix.two.two, matrix.two.three,
                matrix.three.one, matrix.three.two, matrix.three.three};

                GLES20.glUniformMatrix3fv(uniformLocation, 1, false, FloatBuffer.wrap(array));
            }
        });
    }

    public void setMatrix4X4Uniform(DHMatrix4X4 matrix, String uniformName) {
        int uniformLocation = filterProgram.getUniformIndex(uniformName);
        setMatrix4X4Uniform(matrix, uniformLocation, filterProgram);
    }

    public void setMatrix4X4Uniform(final DHMatrix4X4 matrix, final int uniformLocation, final GLProgram program) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                DHImageContext.setActiveProgram(program);
                float array[] = {matrix.one.one, matrix.one.two, matrix.one.three, matrix.one.four,
                        matrix.two.one, matrix.two.two, matrix.two.three, matrix.two.four,
                        matrix.three.one, matrix.three.two, matrix.three.three, matrix.three.four,
                        matrix.four.one, matrix.four.two, matrix.four.three, matrix.four.four};

                GLES20.glUniformMatrix4fv(uniformLocation, 1, false, FloatBuffer.wrap(array));
            }
        });
    }

    public static float[] textureCoordinatesForRotation(DHImageRotationMode rotationMode) {
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

    public void setUniformsForProgram(int programIndex) {

    }

    //Override
    public void useNextFrameForImageCapture() {
        usingNextFrameForImageCapture = true;
//        try {
//            synchronized (semaphore) {
//                semaphore.wait();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public Bitmap newImageFromCurrentlyProcessedOutput() {
        long timeoutForImageCapture = 3 * 1000;
        Bitmap res = null;
        try {
            boolean result = semaphore.tryAcquire(timeoutForImageCapture, TimeUnit.MILLISECONDS);
            if (result == false) {
                return null;
            }
//            DHImageFrameBuffer frameBuffer = frameBufferForOutput();
            usingNextFrameForImageCapture = false;
            semaphore.release();
//            res = frameBuffer.newBitmapFromFrameBufferContents();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            return res;
        }
    }


    //DHImageInput
    @Override
    public void newFrameReady(float time, int index) {
        renderToTexture(imageVertices, textureCoordinatesForRotation(inputRotationMode));
        informTargetsAboutNewFrameReadyAtTime(time);
    }

    @Override
    public void setInputSurfaceTexture(EGLSurface surface, DHImageSurfaceTexture surfaceTexture, int index) {
        mSourceSurface = surface;
        firstInputSurfaceTexture = surfaceTexture;
    }

//    @Override
//    public void setInputFrameBuffer(DHImageFrameBuffer frameBuffer, int index) {
//        firstInputFrameBuffer = frameBuffer;
//        firstInputFrameBuffer.lock();
//    }

    @Override
    public int nextAvailableTextureIndex() {
        return 0;
    }

    @Override
    public void setInputSize(DHImageSize size, int index) {
        if (preventRendering) {
            return;
        }
        if (overrideInputSize) {
//            if (!forcedMaximumSize.isZeroSize()) {
//
//            }
        } else {
            DHImageSize rotatedSize = rotatedSize(size, index);
            if (rotatedSize.isZeroSize()) {
                inputTextureSize = rotatedSize;
            } else if (!rotatedSize.equals(inputTextureSize)) {
                inputTextureSize = rotatedSize;
            }
        }
        setupFilterForSize(sizeOfFBO());
    }

    @Override
    public void setInputRotation(DHImageRotationMode rotationMode, int index) {
        inputRotationMode = rotationMode;
    }

    @Override
    public DHImageSize maximumOutputSize() {
        return DHImageSize.zeroSize();
    }

    @Override
    public void endProcessing() {
        if (!isEndProcessing) {
            isEndProcessing = true;
            for (IDHImageInput target : getTargets()) {
                target.endProcessing();
            }
        }
    }

    @Override
    public boolean shouldIgnoreUpdatesToThisTarget() {
        return false;
    }

    @Override
    public boolean enabled() {
        return isEnabled();
    }

    @Override
    public boolean wantsMonochromeInput() {
        return false;
    }

    @Override
    public void setCurrentlyReceivingMonochromeInput(boolean newValue) {

    }



    public Bitmap convertToBitMap() {
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOutputSurfaceTexture.getTexture());
        IntBuffer ib = IntBuffer.allocate((int)inputTextureSize.width * (int)inputTextureSize.height);
        GLES20.glReadPixels(0, 0, (int)inputTextureSize.width , (int)inputTextureSize.height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
        int ia[] = ib.array();
        Bitmap bitmap = Bitmap.createBitmap((int)inputTextureSize.width , (int)inputTextureSize.height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(ia));
        return bitmap;
    }
}