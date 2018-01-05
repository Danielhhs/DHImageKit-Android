package daniel.cn.dhimagekitandroid.DHFilters.base.output;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.File;
import java.nio.IntBuffer;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageInput;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImagePictureCallback;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public class DHImagePicture extends DHImageOutput {

    private DHImageSize pixelSizeOfImage;
    private boolean hasProcessedImage;
    private boolean shouldSmoothlyScaleOutput;

    public DHImagePicture(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            initializeWithBitMap(bitmap, false);
        }
    }

    public DHImagePicture(Bitmap bitmap) {
        this(bitmap, false);
    }

    public DHImagePicture(Bitmap bitmap, boolean smoothlyScaleOutput) {
        initializeWithBitMap(bitmap, smoothlyScaleOutput);

    }

    public void processImage() {
        processImage(null);
    }

    public void processImage(IDHImagePictureCallback callback) {
        hasProcessedImage = true;
        //TO-DO: Use semaphore to ensure only one task will be executed at a time;
        //TO-DO: Run on video processing queue;
                for (IDHImageInput target : targets) {
                    int indexOfTarget = targets.indexOf(target);
                    Integer textureIndexOfTarget = targetTextureIndices.get(indexOfTarget);

                    target.setCurrentlyReceivingMonochromeInput(false);
                    target.setInputSize(pixelSizeOfImage, textureIndexOfTarget);
                    target.setInputFrame(outputFrameBuffer, textureIndexOfTarget);
                    target.newFrameReady(0, textureIndexOfTarget);
                }
        if (callback != null) {
            callback.onImageProcessFinished();
        }
    }

    public void processImage(final DHImageOutput filter, final IDHImagePictureCallback callback) {
        filter.useNextFrameForImageCapture();
        processImage(new IDHImagePictureCallback() {
            @Override
            public void onImageProcessFinished() {
                Bitmap bitmap = filter.imageFromCurrentFrameBuffer();
                if (callback != null) {
                    callback.onImageProcessFinished(bitmap);
                }
            }

            @Override
            public void onImageProcessFinished(Bitmap bitmap) {

            }
        });
    }

    public DHImageSize outputImageSize() {
        return pixelSizeOfImage;
    }

    @Override
    public void destroy() {
        super.destroy();
        outputFrameBuffer.unlock();
        outputFrameBuffer = null;
    }

    @Override
    public void removeAllTargets() {
        super.removeAllTargets();
        hasProcessedImage = false;
    }

    //PRIVATE HELPER
    private void initializeWithBitMap(Bitmap bitmap, boolean smoothlyScaleOutput) {
        if (bitmap == null) return;
        hasProcessedImage = false;
        shouldSmoothlyScaleOutput = smoothlyScaleOutput;
        pixelSizeOfImage = new DHImageSize(bitmap.getWidth(), bitmap.getHeight());
        DHImageSize pixelSizeForTexture = new DHImageSize(pixelSizeOfImage.width, pixelSizeOfImage.height);

//        mSurface = DHImageContext.getCurrentContext().createOffScreenSurface((int)pixelSizeForTexture.width, (int)pixelSizeForTexture.height);
//        DHImageContext.getCurrentContext().makeSurfaceCurrent(mSurface);
        outputFrameBuffer = DHImageContext.sharedFrameBufferCache().fetchFrameBuffer(pixelSizeForTexture, getOutputTextureOptions(), false);
        outputFrameBuffer.disableReferenceCount();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, outputFrameBuffer.getTexture());
        if (shouldSmoothlyScaleOutput) {
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        }

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        if (shouldSmoothlyScaleOutput) {
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        convertToBitMap();

        outputFrameBuffer.deactivate();
    }

    private Bitmap convertToBitMap() {
        final IntBuffer pixelBuffer = IntBuffer.allocate((int)pixelSizeOfImage.width * (int)pixelSizeOfImage.height);
        GLES20.glReadPixels(0, 0, (int)pixelSizeOfImage.width, (int)pixelSizeOfImage.height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
        int[] pixelArray = pixelBuffer.array();
        Bitmap bitmap = Bitmap.createBitmap((int)pixelSizeOfImage.width, (int)pixelSizeOfImage.height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(pixelBuffer);
        return bitmap;
    }
}
