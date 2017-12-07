package daniel.cn.dhimagekitandroid.DHFilters.base;

import android.opengl.GLES20;

/**
 * Created by huanghongsen on 2017/12/7.
 */

public class DHImageTextureOptions {
    public int minFilter;
    public int maxFilter;
    public int wrapS;
    public int wrapT;
    public int internalFormat;
    public int format;
    public int type;

    public DHImageTextureOptions() {
        minFilter = GLES20.GL_LINEAR;
        maxFilter = GLES20.GL_LINEAR;
        wrapS = GLES20.GL_CLAMP_TO_EDGE;
        wrapT = GLES20.GL_CLAMP_TO_EDGE;
        internalFormat = GLES20.GL_RGBA;
        format = GLES20.GL_RGBA;
        type = GLES20.GL_UNSIGNED_BYTE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(minFilter).append(":").append(maxFilter).append(":").append(wrapS).append(":").append(wrapT).append(":").append(internalFormat)
                .append(":").append(format).append(":").append(type);
        return sb.toString();
    }
}
