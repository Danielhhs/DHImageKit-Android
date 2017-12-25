package daniel.cn.dhimagekitandroid.DHFilters.base.output;

import android.opengl.GLES20;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageTextureOptions;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/25.
 */

public class DHImageFrameBuffer {
    private int texture;
    private DHImageSize size;
    private DHImageTextureOptions textureOptions;
    private int frameBuffer;

    public DHImageFrameBuffer(DHImageSize size) {
        initialize(size, null);
    }

    public DHImageFrameBuffer(DHImageSize size, DHImageTextureOptions textureOptions) {
        initialize(size, textureOptions);
    }

    private void initialize(DHImageSize size, DHImageTextureOptions textureOptions) {
        this.size = size;
        this.textureOptions = textureOptions;
        if (this.textureOptions == null) {
            this.textureOptions = new DHImageTextureOptions();
        }
        generateFrameBuffer();
    }

    private void generateFrameBuffer() {
        int frameBuffers[] = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        frameBuffer = frameBuffers[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        generateTexture();
    }

    private void generateTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        int textures[] = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        texture = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, textureOptions.maxFilter);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, textureOptions.minFilter);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, textureOptions.wrapS);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, textureOptions.wrapT);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, textureOptions.internalFormat, (int)size.width, (int)size.height, 0, textureOptions.format, textureOptions.type, null);

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0);

        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Incomplete frame buffer");
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void activate() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glViewport(0, 0, (int)size.width, (int)size.height);
    }

    public void deactivate() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int getTexture() {
        return texture;
    }
}
