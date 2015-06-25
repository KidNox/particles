package kidnox.particles.sample;

import android.content.Context;

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

    final static int NUM_PARTICLES = 2000;
    final static int PARTICLE_SIZE = 10;// with colors
    final float[] fVertices = new float[NUM_PARTICLES * PARTICLE_SIZE];
    final Random gen = new Random(System.currentTimeMillis());

    private static final float velocityFactor = 1f;

    private static final float initialOffset = 1f;
    private static final float ringOffset = 0.23f;
    private static final float holeSize = 0.5f;
    private static final int pointSize = 18;

    private static final float minParticleRadius = 0.1f;

    private static final float velocityDivider = 800f;

    private static final float angleMultiplier = (float) (18f * Math.PI);
    private static final float rMultiplier = 0.994f;
    private static final float rInitialMultiplier = 1f;

    private static final float xMultiplier = 1f / 2.4f;
    private static final float yMultiplier = 1f / 1.8f;

    private final Context context;

    private FloatBuffer vertexBuffer;

    int iProgId;
    int iTexId;
    int iTexture;

    int pu_hole_r;
    int pu_scale;
    int pu_elapsedTime;
    int pu_velocityDivider;
    int pu_angleMultiplier;
    int pu_rMultiplier;
    int pu_rInitialMultiplier;
    int pu_xMultiplier;
    int pu_yMultiplier;
    int pu_ringOffsetMultiplier;
    int pa_timeOffset;
    int pa_color;
    int pa_life;//based on particle radius
    int pa_angle;
    int pa_ring;
    int pa_radius;
    int pa_velocity;

    public BlackHoleParticleProgram(Context context) {
        this.context = context;
    }

    @Override public void onBegin(GLEngine glEngine) {
        vertexBuffer = ByteBuffer.allocateDirect(fVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glClearColor(0, 0, 0, 0);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        int vertexShader = GLHelper.compileVertexShader(context, R.raw.black_hole_vertex);
        int fragmentShader = GLHelper.compileFragmentShader(context, R.raw.particle_fragment);

        iProgId = GLHelper.linkProgram(vertexShader, fragmentShader);
        iTexId = GLHelper.loadPoint(pointSize, context.getResources().getColor(R.color.green));
        iTexture = glGetUniformLocation(iProgId, "u_texture");

        pu_hole_r = glGetUniformLocation(iProgId, "u_hole_r");
        pu_scale = glGetUniformLocation(iProgId, "u_scale");
        pu_velocityDivider = glGetUniformLocation(iProgId, "u_velocityDivider");
        pu_angleMultiplier = glGetUniformLocation(iProgId, "u_angleMultiplier");
        pu_rMultiplier = glGetUniformLocation(iProgId, "u_rMultiplier");
        pu_rInitialMultiplier = glGetUniformLocation(iProgId, "u_rInitialMultiplier");
        pu_xMultiplier = glGetUniformLocation(iProgId, "u_xMultiplier");
        pu_yMultiplier = glGetUniformLocation(iProgId, "u_yMultiplier");
        pu_ringOffsetMultiplier = glGetUniformLocation(iProgId, "u_ringOffsetMultiplier");
        pu_elapsedTime = glGetUniformLocation(iProgId, "u_elapsedTime");

        pa_timeOffset = glGetAttribLocation(iProgId, "a_timeOffset");
        pa_color = glGetAttribLocation(iProgId, "a_color");
        pa_life = glGetAttribLocation(iProgId, "a_life");
        pa_angle = glGetAttribLocation(iProgId, "a_angle");
        pa_ring = glGetAttribLocation(iProgId, "a_ring");
        pa_radius = glGetAttribLocation(iProgId, "a_radius");
        pa_velocity = glGetAttribLocation(iProgId, "a_velocity");


        for (int i = 0; i < NUM_PARTICLES; i++) {
            //r,g,b,a
            fVertices[i*PARTICLE_SIZE + 0] = rnd(0.33f, 1);
            fVertices[i*PARTICLE_SIZE + 1] = rnd(0.33f, 1);
            fVertices[i*PARTICLE_SIZE + 2] = rnd(0.33f, 1);
            fVertices[i*PARTICLE_SIZE + 3] = gen.nextFloat() /*> 0.5 ? 0.4f : 1*/;

            float radius = (gen.nextFloat() + 0.66f) / 1.66f;
            float life = GLHelper.logBase(rMultiplier, minParticleRadius / radius);//log base rMultiplier
            fVertices[i*PARTICLE_SIZE + 7] = radius;
            fVertices[i*PARTICLE_SIZE + 4] = (int)life;

            fVertices[i*PARTICLE_SIZE + 5] = gen.nextFloat();//angle
            fVertices[i*PARTICLE_SIZE + 6] = (gen.nextFloat() + ringOffset) / (1f + ringOffset);//ring
            fVertices[i*PARTICLE_SIZE + 8] = gen.nextFloat() * 4f + 1f;//velocity

            fVertices[i*PARTICLE_SIZE + 9] = gen.nextInt(NUM_PARTICLES);//time offset
        }
        vertexBuffer.put(fVertices).position(0);
    }

    int elapsedTime = 1;

    @Override public void drawFrame(GLEngine glEngine, long currentTime) {
        elapsedTime++;
        glClear(GL_COLOR_BUFFER_BIT);
        glUseProgram(iProgId);
        glUniform1f(pu_scale, pointSize);
        glUniform1f(pu_velocityDivider, velocityDivider);
        glUniform1f(pu_angleMultiplier, angleMultiplier);
        glUniform1f(pu_rMultiplier, rMultiplier);
        glUniform1f(pu_rInitialMultiplier, rInitialMultiplier);
        glUniform1f(pu_xMultiplier, xMultiplier);
        glUniform1f(pu_yMultiplier, yMultiplier);
        glUniform1f(pu_ringOffsetMultiplier, initialOffset);

        glUniform1f(pu_elapsedTime, elapsedTime);
        glUniform1f(pu_scale, pointSize);
        glUniform1f(pu_hole_r, holeSize);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, iTexId);

        glUniform1i(iTexture, 0);

        vertexBuffer.position(0);
        glVertexAttribPointer(pa_color, 4, GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        glEnableVertexAttribArray(pa_color);

        vertexBuffer.position(4);
        glVertexAttribPointer(pa_life, 1, GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        glEnableVertexAttribArray(pa_life);

        vertexBuffer.position(5);
        glVertexAttribPointer(pa_angle, 1, GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        glEnableVertexAttribArray(pa_angle);

        vertexBuffer.position(6);
        glVertexAttribPointer(pa_ring, 1, GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        glEnableVertexAttribArray(pa_ring);

        vertexBuffer.position(7);
        glVertexAttribPointer(pa_radius, 1, GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        glEnableVertexAttribArray(pa_radius);

        vertexBuffer.position(8);
        glVertexAttribPointer(pa_velocity, 1, GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        glEnableVertexAttribArray(pa_velocity);

        vertexBuffer.position(9);
        glVertexAttribPointer(pa_timeOffset, 1, GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        glEnableVertexAttribArray(pa_timeOffset);

        glDrawArrays(GL_POINTS, 0, NUM_PARTICLES);
    }

    @Override public void onEnd(GLEngine glEngine) {

    }

    @Override public void onSizeChanged(GLEngine glEngine) {
        glEngine.applyFulSizedViewport();
    }

    @Override public int getMaxFPS() {
        return 30;
    }

    public float rnd(float min, float max) {
        float fRandNum = (float)gen.nextDouble();
        return min + (max - min) * fRandNum;
    }
}
