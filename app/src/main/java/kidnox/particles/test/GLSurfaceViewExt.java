package kidnox.particles.test;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import kidnox.particles.DrawingLoop;
import kidnox.particles.GLProgram;
import kidnox.particles.GLProgramImpl;

public class GLSurfaceViewExt extends GLSurfaceView {

    public GLSurfaceViewExt(Context context) {
        super(context);
    }

    public GLSurfaceViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGlProgram(final GLProgram glProgram) {
        setRenderer(new Renderer() {
            @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                glProgram.onBegin(null);
            }

            @Override public void onSurfaceChanged(GL10 gl, int width, int height) {

            }

            @Override public void onDrawFrame(GL10 gl) {
                glProgram.drawFrame(null, 0);
            }
        });
    }
}
