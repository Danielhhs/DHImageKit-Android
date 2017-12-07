package daniel.cn.dhimagekitandroid.DHFilters.base;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by huanghongsen on 2017/12/7.
 */

public class DHImageFrameBuffer {
    private int width;
    private int height;
    private DHImageTextureOptions textureOptions;
    private int frameBufferReferenceCount;
    private boolean referenceCountDisabled;
    private boolean missingFrameBuffer;
    private int framebuffer;
    private int texture;

    public DHImageFrameBuffer(int width, int height) {
        this(width, height, new DHImageTextureOptions(), false);
    }

    public DHImageFrameBuffer(int width, int height, DHImageTextureOptions textuerOptions, boolean onlyGenerateTexture) {
        this.width = width;
        this.height = height;
        this.textureOptions = textuerOptions;
        referenceCountDisabled = false;
        frameBufferReferenceCount = 0;
        missingFrameBuffer = onlyGenerateTexture;

        if (missingFrameBuffer) {
            //TO-DO: run on separate thread
            generateTexture();
            framebuffer = 0;
        } else {
            generateFrameBuffer();
        }
    }

    public void activateFrameBuffer() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer);
        GLES20.glViewport(0, 0, width, height);
    }

    public void lock() {
        if (referenceCountDisabled) {return;}
        frameBufferReferenceCount++;
    }

    public void unlock() {
        if (referenceCountDisabled) {return;};
        if (frameBufferReferenceCount <= 0) {
            Log.e("DHImage", "Tried to overrelease a framebuffer, did you forget to call -useNextFrameForImageCapture before using -imageFromCurrentFramebuffer?");
        } else {
            frameBufferReferenceCount--;
        }
    }

    public void clearAllLocks() {
        frameBufferReferenceCount = 0;
    }

    public void disableReferenceCounting() {
        referenceCountDisabled = true;
    }

    public void enableReferenceCounting() {
        referenceCountDisabled = false;
    }

    //Private helpers
    private void generateTexture() {
        int textures[] = new int[1];
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, textureOptions.minFilter);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, textureOptions.maxFilter);
        // This is necessary for non-power-of-two textures
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, textureOptions.wrapS);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, textureOptions.wrapT);
        texture = textures[0];
    }

    private void generateFrameBuffer() {
        //TO-DO: Run on another thread
        int[] frameBuffers = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[0]);
        generateTexture();
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, textureOptions.internalFormat, width, height, 0, textureOptions.format, textureOptions.type, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0);

        int status = GLES20.glCheckFramebufferStatus(frameBuffers[0]);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("DHImage", "Error while creating frame buffer");
        }
        framebuffer = frameBuffers[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public DHImageTextureOptions getTextureOptions() {
        return textureOptions;
    }

    public boolean isMissingFrameBuffer() {
        return missingFrameBuffer;
    }
}
