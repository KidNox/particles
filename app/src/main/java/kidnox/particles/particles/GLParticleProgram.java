package kidnox.particles.particles;

import kidnox.particles.GLEngine;
import kidnox.particles.GLProgram;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;

public class GLParticleProgram implements GLProgram {

    private ParticleSystem mParticleSystem;

    @Override public void onBegin(GLEngine glEngine) {
        mParticleSystem = new ParticleSystem();
        //GLU.gluPerspective(gl, 15.0f, 48.0f / 80.0f, 1, 100);
        //GLU.gluLookAt(gl, 0f, -20f, 0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f);*
        //
    }

    @Override public void drawFrame(GLEngine glEngine) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        mParticleSystem.update(glEngine.getSurfaceConfig());
        mParticleSystem.draw();
    }

    @Override public void onEnd(GLEngine glEngine) {

    }
}
