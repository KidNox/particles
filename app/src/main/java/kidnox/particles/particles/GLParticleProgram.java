package kidnox.particles.particles;

import android.util.Log;

import kidnox.particles.GLEngine;
import kidnox.particles.GLProgram;

import static android.opengl.GLES20.*;

public class GLParticleProgram implements GLProgram {

    private ParticleSystem mParticleSystem;

    private int program;

    @Override public void onBegin(GLEngine glEngine) {
        Log.d("GL", "onBegin");
        glEngine.applyFulSizedViewport();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //GLU.gluPerspective(gl, 15.0f, 48.0f / 80.0f, 1, 100);
        //GLU.gluLookAt(gl, 0f, -20f, 0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f);*
        //
    }

    @Override public void drawFrame(GLEngine glEngine, long currentTime) {
        glClear(GL_COLOR_BUFFER_BIT);
    }

    @Override public void onEnd(GLEngine glEngine) {
        Log.d("GL", "onEnd");
    }

    @Override public void onSizeChanged(GLEngine glEngine) {
        Log.d("GL", "onSizeChanged");
        //glEngine.applyFulSizedViewport();
    }

    @Override public int getMaxFPS() {
        return 60;
    }
}
