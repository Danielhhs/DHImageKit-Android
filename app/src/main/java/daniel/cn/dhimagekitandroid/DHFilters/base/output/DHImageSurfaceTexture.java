package daniel.cn.dhimagekitandroid.DHFilters.base.output;

import android.graphics.SurfaceTexture;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageTextureOptions;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/25.
 */

public class DHImageSurfaceTexture extends SurfaceTexture {

    private int texture;
    private DHImageSize size;
    DHImageTextureOptions textureOptions;

    public DHImageSurfaceTexture(int texName) {
        super(texName);
        texture = texName;
        textureOptions = new DHImageTextureOptions();
        size = DHImageSize.zeroSize();
    }

    public int getTexture() {
        return texture;
    }

    public DHImageSize getSize() {
        return size;
    }

    public void setSize(DHImageSize size) {
        this.size = size;
    }

    public DHImageTextureOptions getTextureOptions() {
        return textureOptions;
    }

    public void setTextureOptions(DHImageTextureOptions textureOptions) {
        this.textureOptions = textureOptions;
    }
}
