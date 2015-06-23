package kidnox.particles;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

public class GLEngine implements SurfaceConfig.OnConfigChangedListener {

    static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    static final int EGL_OPENGL_ES2_BIT = 4;

    private static long lastGlInitTime;

    final GLProgram glProgram;
    final SurfaceConfig surfaceConfig;

    public final EGL10 egl;
    public final EGLDisplay eglDisplay;
    public final EGLContext eglContext;
    public final EGLSurface eglSurface;
    public final GL10 gl;

    public GLEngine(GLProgram glProgram, SurfaceTexture surfaceTexture, SurfaceConfig surfaceConfig) {
        this.glProgram = glProgram;
        this.surfaceConfig = surfaceConfig;
        try {
            egl = (EGL10) EGLContext.getEGL();
        } catch (Exception ex) {
            throw new RuntimeException("EGL10 not supported", ex);
        }

        eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed " + GLUtils.getEGLErrorString(egl.eglGetError()));
        }

        int[] version = new int[2];
        if (!egl.eglInitialize(eglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed " + GLUtils.getEGLErrorString(egl.eglGetError()));
        }

        EGLConfig eglConfig = chooseEglConfig();
        if (eglConfig == null) {
            throw new RuntimeException("eglConfig not initialized");
        }

        eglContext = createContext(egl, eglDisplay, eglConfig);

        eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, surfaceTexture, null);

        if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("createWindowSurface failed " + GLUtils.getEGLErrorString(egl.eglGetError()));
        }

        if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw new RuntimeException("eglMakeCurrent failed " + GLUtils.getEGLErrorString(egl.eglGetError()));
        }

        try {
            gl = (GL10) eglContext.getGL();
        } catch (Exception ex) {
            throw new RuntimeException("GL10 not supported", ex);
        }
        lastGlInitTime = System.currentTimeMillis();

        surfaceConfig.setOnConfigChangedListener(this);
    }

    private EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig) {
        int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
        return egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
    }

    private EGLConfig chooseEglConfig() {
        int[] configsCount = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        int[] configSpec = getConfig();
        if (!egl.eglChooseConfig(eglDisplay, configSpec, configs, 1, configsCount)) {
            throw new IllegalArgumentException("eglChooseConfig failed " + GLUtils.getEGLErrorString(egl.eglGetError()));
        } else if (configsCount[0] > 0) {
            return configs[0];
        }
        return null;
    }

    private int[] getConfig() {
        return new int[] {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL10.EGL_NONE
        };
    }

    void checkEglError() {
        int error = egl.eglGetError();
        if (error != EGL10.EGL_SUCCESS) {
            Log.w(getClass().getSimpleName(), "EGL error = 0x" + Integer.toHexString(error));
        }
    }

    public SurfaceConfig getSurfaceConfig() {
        return surfaceConfig;
    }

    public void destroy() {
        egl.eglDestroyContext(eglDisplay, eglContext);
        egl.eglDestroySurface(eglDisplay, eglSurface);
    }

    public static long getLastGlInitTime() {
        return lastGlInitTime;
    }

    @Override public void onSizeChanged(int w, int h) {
        glProgram.onSizeChanged(this);
    }

    public void applyFulSizedViewport() {
        //Log.d("GL", "applyFulSizedViewport=" + surfaceConfig.getWidth() + ", " + surfaceConfig.getHeight());
        GLES20.glViewport(0, 0, surfaceConfig.getWidth(), surfaceConfig.getHeight());
    }

    public final void swapBuffers() {
        if(!egl.eglSwapBuffers(eglDisplay, eglSurface)) {
            throw new RuntimeException("Cannot swap buffers");
        }
    }
}
