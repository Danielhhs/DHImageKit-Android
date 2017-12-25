package daniel.cn.dhimagekitandroid.DHFilters.base;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import daniel.cn.dhimagekitandroid.DHFilters.DHImageEditor;
import daniel.cn.dhimagekitandroid.DHFilters.base.output.DHImageSurfaceTexture;

import static javax.microedition.khronos.egl.EGL10.EGL_ALPHA_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_BLUE_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_DEFAULT_DISPLAY;
import static javax.microedition.khronos.egl.EGL10.EGL_DEPTH_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_GREEN_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_HEIGHT;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT;
import static javax.microedition.khronos.egl.EGL10.EGL_RED_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_STENCIL_SIZE;
import static javax.microedition.khronos.egl.EGL10.EGL_WIDTH;

/**
 * Created by huanghongsen on 2017/12/7.
 */

public class DHImageContext {
    private static GLProgram currentShaderProgram;
    private static DHImageContext currentContext;

    EGL10 mEGL;
    EGLDisplay mEGLDisplay;
    EGLConfig[] mEGLConfigs;
    EGLConfig mEGLConfig;
    EGLContext mEGLContext;

    EGLSurface mCurrentEGLSurface;
    GL10 mGL;

    int mWidth, mHeight;


    public static void setActiveProgram(GLProgram program) {
        currentShaderProgram = program;
        program.use();
    }

    //Surface management
    public EGLSurface createWindowSurface(SurfaceTexture surfaceTexture) {
        int[] attribList = new int[] {
                EGL_NONE
        };

        EGLSurface surface = mEGL.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, surfaceTexture, attribList);
        makeSurfaceCurrent(surface);
        return surface;
    }

    public EGLSurface createOffScreenSurface(int width, int height) {
        int[] attribList = new int[] {
                EGL_WIDTH, width,
                EGL_HEIGHT, height,
                EGL_NONE
        };

        EGLSurface surface = mEGL.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, attribList);
        makeSurfaceCurrent(surface);
        return surface;
    }

    public void makeSurfaceCurrent(EGLSurface surface) {
        mEGL.eglMakeCurrent(mEGLDisplay, surface, surface, mEGLContext);
        mCurrentEGLSurface = surface;
    }

    public void makeSurfaceCurrent(EGLSurface drawSurface, EGLSurface readSurface) {
        mEGL.eglMakeCurrent(mEGLDisplay, drawSurface, readSurface, mEGLContext);
        mCurrentEGLSurface = drawSurface;
    }

    public DHImageSurfaceTexture createSurfaceTexture(DHImageTextureOptions textureOptions, EGLSurface surface) {
        makeSurfaceCurrent(surface);
        int textures[] = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int texture = textures[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, textureOptions.maxFilter);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, textureOptions.minFilter);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, textureOptions.wrapS);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, textureOptions.wrapT);

        DHImageSurfaceTexture surfaceTexture = new DHImageSurfaceTexture(texture);
        return surfaceTexture;
    }

    public DHImageSurfaceTexture createSurfaceTexture(int texture, EGLSurface surface) {
        makeSurfaceCurrent(surface);
        DHImageSurfaceTexture surfaceTexture = new DHImageSurfaceTexture(texture);
        return surfaceTexture;
    }

    public void displayCurrentSurface() {
        mEGL.eglSwapBuffers(mEGLDisplay, mCurrentEGLSurface);
    }

    //Context management
    public void useAsCurrentContext() {
        currentContext = this;
    }

    public static DHImageContext getCurrentContext() {
        synchronized (currentContext) {
            if (currentContext == null) {
                currentContext = new DHImageContext();
            }
        }
        return currentContext;
    }

    //Initializations
    int[] attributes = new int[] {
            EGL10.EGL_RED_SIZE, 8,  //指定RGB中的R大小（bits）
            EGL10.EGL_GREEN_SIZE, 8, //指定G大小
            EGL10.EGL_BLUE_SIZE, 8,  //指定B大小
            EGL10.EGL_ALPHA_SIZE, 8, //指定Alpha大小，以上四项实际上指定了像素格式
            EGL10.EGL_DEPTH_SIZE, 16, //指定深度缓存(Z Buffer)大小
            EGL10.EGL_RENDERABLE_TYPE, 4, //指定渲染api类别, 如上一小节描述，这里或者是硬编码的4，或者是EGL14.EGL_OPENGL_ES2_BIT
            EGL10.EGL_NONE };  //总是以EGL10.EGL_NONE结尾


    public DHImageContext() {
        int version[] = new int[2];

        int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        mEGL = (EGL10)EGLContext.getEGL();
        mEGLDisplay = mEGL.eglGetDisplay(EGL_DEFAULT_DISPLAY);
        mEGL.eglInitialize(mEGLDisplay, version);
        mEGLConfig = chooseConfig();

        int[] attrib_list = {
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        mEGLContext = mEGL.eglCreateContext(mEGLDisplay, mEGLConfig, EGL_NO_CONTEXT, attrib_list);
        mGL = (GL10)mEGLContext.getGL();
    }

    private EGLConfig chooseConfig() {
        int[] attribList = new int[] {
                EGL_DEPTH_SIZE, 0,
                EGL_STENCIL_SIZE, 0,
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, 4,
                EGL_NONE
        };

        // No error checking performed, minimum required code to elucidate logic
        // Expand on this logic to be more selective in choosing a configuration
        int[] numConfig = new int[1];
        mEGL.eglChooseConfig(mEGLDisplay, attribList, null, 0, numConfig);
        int configSize = numConfig[0];
        mEGLConfigs = new EGLConfig[configSize];
        mEGL.eglChooseConfig(mEGLDisplay, attribList, mEGLConfigs, configSize, numConfig);

        return mEGLConfigs[0]; // Best match is probably the first configuration
    }

    public GL10 getmGL() {
        return mGL;
    }

}
