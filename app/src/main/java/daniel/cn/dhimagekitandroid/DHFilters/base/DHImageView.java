package daniel.cn.dhimagekitandroid.DHFilters.base;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.FrameLayout;

import javax.microedition.khronos.egl.EGLSurface;

import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageInput;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageSurfaceListener;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/20.
 */

public class DHImageView extends FrameLayout implements IDHImageInput {

    private TextureView mTextureView;
    private DHImageViewRenderer renderer;
    private IDHImageSurfaceListener surfaceListener;
    private DHImageContext mContext;

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (surfaceListener == null) {
                throw new RuntimeException("Did Not Set Surface Listener For DHImageView");
            }
            mContext = new DHImageContext();
            mContext.useAsCurrentContext();

            renderer.initialize(width, height, surface);
            surfaceListener.onSurfaceTextureAvailable();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    public DHImageView(Context context) {
        super(context);
        init(context, null);
    }

    public DHImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mTextureView = new TextureView(context, attrs);

        addView(mTextureView);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        renderer = new DHImageViewRenderer();
    }

    //IDHImageInput
    @Override
    public void newFrameReady(float time, int index) {
        renderer.render();
        DHImageContext.getCurrentContext().displayCurrentSurface();
    }

    @Override
    public void setInputSurfaceTexture(EGLSurface surface, DHImageSurfaceTexture surfaceTexture, int index) {
        renderer.setSurfaceTexture(surface, surfaceTexture);
    }

    @Override
    public int nextAvailableTextureIndex() {
        return 0;
    }

    @Override
    public void setInputSize(final DHImageSize size, int index) {
        renderer.setInputImageSize(size);
    }

    @Override
    public void setInputRotation(DHImageRotationMode rotationMode, int index) {
        renderer.setInputRotation(rotationMode);
    }

    @Override
    public DHImageSize maximumOutputSize() {
        return new DHImageSize(mTextureView.getWidth(), mTextureView.getHeight());
    }

    @Override
    public void endProcessing() {

    }

    @Override
    public boolean shouldIgnoreUpdatesToThisTarget() {
        return false;
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public boolean wantsMonochromeInput() {
        return false;
    }

    @Override
    public void setCurrentlyReceivingMonochromeInput(boolean newValue) {

    }

    //Getters & Setters

    public DHImageViewRenderer getRenderer() {
        return renderer;
    }

    public IDHImageSurfaceListener getSurfaceListener() {
        return surfaceListener;
    }

    public void setSurfaceListener(IDHImageSurfaceListener surfaceListener) {
        this.surfaceListener = surfaceListener;
    }
}
