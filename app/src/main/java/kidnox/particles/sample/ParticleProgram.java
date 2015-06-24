package kidnox.particles.sample;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import kidnox.particles.GLEngine;
import kidnox.particles.GLProgram;
import kidnox.particles.R;
import kidnox.particles.util.GLHelper;

public class ParticleProgram implements GLProgram {

    private static final int pointSize = 10;
    private static final float holeSize = 0.5f;

    int iProgId;
    int iTexture;
    int iTexId;

    int iColor;
    int iRadius;
    int iRing;
    int iRand;

    /*
    attribute float a_radius;
attribute float a_ring;
attribute float a_rand;
    * */

    /*

Space.prototype.moveParticle = function (p) {
        p.ring = Math.max(p.ring - 1, this.r);
        p.random += p.move;
        p.x = Math.cos(p.random + Math.PI) * p.ring;
        p.y = Math.sin(p.random + Math.PI) * p.ring;
    };

Space.prototype.resetParticle = function (p) {
        p.ring = Math.random() * this.r * 3;
        p.radius = Math.random() * 5;
    };
    Space.prototype.disappear = function (p) {
        if (p.radius < 0.8) {
            this.resetParticle(p);
        }
        p.radius *= 0.994;
    };
    Space.prototype.draw = function (p) {
        this.ctx.beginPath();
        this.ctx.fillStyle = p.color;
        this.ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
        this.ctx.fill();
    };


*/

    final Context context;
    ParticleSystem particleSystem;

    public ParticleProgram(Context context) {
        this.context = context;
    }

    @Override public void onBegin(GLEngine glEngine) {
        particleSystem = new ParticleSystem();

        glClearColor(0, 0, 0, 1);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        int vertexShader = GLHelper.compileVertexShader(context, R.raw.particle_vertex);
        int fragmentShader = GLHelper.compileFragmentShader(context, R.raw.particle_fragment);

        iProgId = GLHelper.linkProgram(vertexShader, fragmentShader);
        iTexId = GLHelper.loadPoint(pointSize, Color.GREEN);
        iTexture = glGetUniformLocation(iProgId, "u_texture");

        iColor = glGetAttribLocation(iProgId, "a_color");
        iRadius = glGetAttribLocation(iProgId, "a_radius");
        iRing = glGetAttribLocation(iProgId, "a_ring");
        iRand = glGetAttribLocation(iProgId, "a_rand");

        particleSystem.init();
    }

    @Override public void drawFrame(GLEngine glEngine, long currentTime) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUseProgram(iProgId);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, iTexId);

        glUniform1i(iTexture, 0);

        //fVertices[i*PARTICLE_SIZE + 4] = ((gen.nextFloat() * 4) + 1) / 100;
        //fVertices[i*PARTICLE_SIZE + 5] = gen.nextFloat() * 7;

        particleSystem.update(iColor, iRadius, iRing, iRand);
        if(!requestedLog) {
            requestedLog = true;
            Log.d("GL", "gl errors = "+glGetProgramInfoLog(iProgId));
        }
    }

    boolean requestedLog;

    @Override public void onEnd(GLEngine glEngine) {

    }

    @Override public void onSizeChanged(GLEngine glEngine) {
        glViewport(0, 0, glEngine.getSurfaceConfig().getWidth(), glEngine.getSurfaceConfig().getHeight());
    }

    @Override public int getMaxFPS() {
        return 60;
    }
}
