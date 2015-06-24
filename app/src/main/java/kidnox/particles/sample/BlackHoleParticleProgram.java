package kidnox.particles.sample;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import kidnox.particles.GLEngine;
import kidnox.particles.GLProgram;
import kidnox.particles.R;
import kidnox.particles.util.GLHelper;

import static android.opengl.GLES20.*;

public class BlackHoleParticleProgram implements GLProgram {

    final static int NUM_PARTICLES = 1200;
    final static int PARTICLE_SIZE = 9;// with colors
    //each particle contains
    final float[] fVertices = new float[NUM_PARTICLES * PARTICLE_SIZE];
    final Random gen = new Random(System.currentTimeMillis());

    private static final float holeSize = 0.5f;
    private static final int pointSize = 13;

    private final Context context;

    private FloatBuffer vertexBuffer;

    int iProgId;
    int iTexId;
    int iTexture;

    int pu_hole_r;
    int pu_scale;
    int pu_elapsedTime;
    int pa_color;
    int pa_life;//200 ... 500 frames
    int pa_rand;
    int pa_ring;
    int pa_radius;
    int pa_move;

    public BlackHoleParticleProgram(Context context) {
        this.context = context;
    }

    @Override public void onBegin(GLEngine glEngine) {
        vertexBuffer = ByteBuffer.allocateDirect(fVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glClearColor(0, 0, 0, 1);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        int vertexShader = GLHelper.compileVertexShader(context, R.raw.black_hole_vertex);
        int fragmentShader = GLHelper.compileFragmentShader(context, R.raw.particle_fragment);

        iProgId = GLHelper.linkProgram(vertexShader, fragmentShader);
        iTexId = GLHelper.loadPoint(pointSize, Color.GREEN);
        iTexture = glGetUniformLocation(iProgId, "u_texture");

        pu_hole_r = glGetUniformLocation(iProgId, "u_hole_r");
        pu_scale = glGetUniformLocation(iProgId, "u_scale");
        pu_elapsedTime = glGetUniformLocation(iProgId, "u_elapsedTime");

        pa_color = glGetAttribLocation(iProgId, "a_color");
        pa_life = glGetAttribLocation(iProgId, "a_life");
        pa_rand = glGetAttribLocation(iProgId, "a_rand");
        pa_ring = glGetAttribLocation(iProgId, "a_ring");
        pa_radius = glGetAttribLocation(iProgId, "a_radius");
        pa_move = glGetAttribLocation(iProgId, "a_move");


        for (int i = 0; i < NUM_PARTICLES; i++) {
            //r,g,b,a
            fVertices[i*PARTICLE_SIZE + 0] = 0;
            fVertices[i*PARTICLE_SIZE + 1] = gen.nextFloat();
            fVertices[i*PARTICLE_SIZE + 2] = 0;
            fVertices[i*PARTICLE_SIZE + 3] = gen.nextFloat();

            fVertices[i*PARTICLE_SIZE + 4] = rnd(0.2f, 0.5f) * 1000;//life
            fVertices[i*PARTICLE_SIZE + 5] = gen.nextFloat() * 66;//rand
            fVertices[i*PARTICLE_SIZE + 6] = gen.nextFloat();//ring
            fVertices[i*PARTICLE_SIZE + 7] = rnd(0.5f, 1.0f);//radius
            fVertices[i*PARTICLE_SIZE + 8] = (gen.nextFloat() * 4 + 1) / 5000;//move
        }
        vertexBuffer.put(fVertices).position(0);
    }

    int elapsedTime = 0;

    @Override public void drawFrame(GLEngine glEngine, long currentTime) {
        elapsedTime++;
        glClear(GL_COLOR_BUFFER_BIT);
        glUseProgram(iProgId);
        glUniform1f(pu_scale, pointSize);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, iTexId);

        glUniform1i(iTexture, 0);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(pa_color, 4, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(pa_color);

        vertexBuffer.position(4);
        GLES20.glVertexAttribPointer(pa_life, 1, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(pa_life);

        vertexBuffer.position(5);
        GLES20.glVertexAttribPointer(pa_rand, 1, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(pa_rand);

        vertexBuffer.position(6);
        GLES20.glVertexAttribPointer(pa_ring, 1, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(pa_ring);

        vertexBuffer.position(7);
        GLES20.glVertexAttribPointer(pa_radius, 1, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(pa_radius);

        vertexBuffer.position(7);
        GLES20.glVertexAttribPointer(pa_move, 1, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(pa_move);

        GLES20.glUniform1f(pu_elapsedTime, elapsedTime);
        GLES20.glUniform1f(pu_scale, pointSize);
        GLES20.glUniform1f(pu_hole_r, holeSize);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, NUM_PARTICLES);
    }

    @Override public void onEnd(GLEngine glEngine) {

    }

    @Override public void onSizeChanged(GLEngine glEngine) {

    }

    @Override public int getMaxFPS() {
        return 60;
    }

    public float rnd(float min, float max) {
        float fRandNum = (float)gen.nextDouble();
        return min + (max - min) * fRandNum;
    }
}
