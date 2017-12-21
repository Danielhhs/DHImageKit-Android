package daniel.cn.dhimagekitandroid.DHFilters.base.output;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.FrameLayout;

import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageContext;
import daniel.cn.dhimagekitandroid.DHFilters.base.DHImageFrameBuffer;
import daniel.cn.dhimagekitandroid.DHFilters.base.GLProgram;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageRotationMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.enums.DHImageViewFillMode;
import daniel.cn.dhimagekitandroid.DHFilters.base.executors.DHImageVideoProcessExecutor;
import daniel.cn.dhimagekitandroid.DHFilters.base.interfaces.IDHImageInput;
import daniel.cn.dhimagekitandroid.DHFilters.base.structs.DHImageSize;

/**
 * Created by huanghongsen on 2017/12/20.
 */

public class DHImageView extends FrameLayout implements IDHImageInput {

    private GLSurfaceView mGLServiceView;
    private DHImageViewRenderer renderer;

    public DHImageView(Context context) {
        super(context);
        init(context, null);
    }

    public DHImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mGLServiceView = new GLSurfaceView(context, attrs);

        mGLServiceView.setEGLContextClientVersion(2);
        mGLServiceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLServiceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        renderer = new DHImageViewRenderer();
        mGLServiceView.setRenderer(renderer);
        mGLServiceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        addView(mGLServiceView);
    }

    //IDHImageInput
    @Override
    public void newFrameReady(float time, int index) {
        DHImageVideoProcessExecutor.runTaskOnVideoProcessQueue(new Runnable() {
            @Override
            public void run() {
                mGLServiceView.requestRender();
            }
        });
    }

    @Override
    public void setInputFrameBuffer(DHImageFrameBuffer frameBuffer, int index) {
        renderer.setInputFrameBufferForDisplay(frameBuffer);
        frameBuffer.unlock();
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
        return new DHImageSize(mGLServiceView.getWidth(), mGLServiceView.getHeight());
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

}
