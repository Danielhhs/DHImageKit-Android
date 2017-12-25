package daniel.cn.dhimagekitandroid.DHFilters.base.interfaces;

import javax.microedition.khronos.egl.EGLSurface;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImageSurfaceTexture;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/8.
 */

public interface IDHImageInput {
    public void newFrameReady(float time, int index);
    public void setInputSurfaceTexture(EGLSurface inputSurface, DHImageSurfaceTexture surfaceTexture, int index);
    public int nextAvailableTextureIndex();
    public void setInputSize(DHImageSize size, int index);
    public void setInputRotation(DHImageRotationMode rotationMode, int index);
    public DHImageSize maximumOutputSize();
    public void endProcessing();
    public boolean shouldIgnoreUpdatesToThisTarget();
    public boolean enabled();
    public boolean wantsMonochromeInput();
    public void setCurrentlyReceivingMonochromeInput(boolean newValue);
}
