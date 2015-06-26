package kidnox.particles.sample;

import android.content.Context;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Random;

import kidnox.particles.GLEngine;
import kidnox.particles.GLProgram;
import kidnox.particles.R;
import kidnox.particles.util.GLHelper;

import static android.opengl.GLES20.*;

public class BlackHoleParticleProgram implements GLProgram {

    public static final int FPS = 60;
    final static int PARTICLE_SIZE = 10;// with colors
    final float[] fVertices;
    final Random gen = new Random(System.currentTimeMillis());

    private float[] mvpMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];

    private final Context context;

    private FloatBuffer vertexBuffer;

    int iProgId;
    int iTexId;
    int iTexture;

    int pu_mvpMatrix;

    int pu_hole_r;
    int pu_scale;
    int pu_elapsedTime;
    int pu_velocityDivider;
    int pu_angleMultiplier;
    int pu_rMultiplier;
    int pu_rInitialMultiplier;
    int pu_xDivider;
    int pu_yDivider;
    int pu_ringOffsetMultiplier;
    int pu_minParticleSize;
    int pu_lifeMultiplier;
    int pu_xTransition;
    int pu_yTransition;

    int pa_timeOffset;
    int pa_color;
    int pa_life;//based on particle radius
    int pa_angle;
    int pa_ring;
    int pa_radius;
    int pa_velocity;

    private final ParticleSystemConfig config;
    private final float pxPerDp;

    public BlackHoleParticleProgram(Context context, ParticleSystemConfig config) {
        if(config == null) throw new NullPointerException();
        fVertices = new float[config.particlesCount * PARTICLE_SIZE];
        this.context = context;
        this.config = config;
        pxPerDp = GLHelper.dpToPx(1, context);
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
        iTexId = GLHelper.loadPoint(config.pointSize, context.getResources().getColor(R.color.green));
        iTexture = glGetUniformLocation(iProgId, "u_texture");

        pu_mvpMatrix = glGetUniformLocation(iProgId, "u_mvpMatrix");

        pu_hole_r = glGetUniformLocation(iProgId, "u_hole_r");
        pu_scale = glGetUniformLocation(iProgId, "u_scale");
        pu_velocityDivider = glGetUniformLocation(iProgId, "u_velocityDivider");
        pu_angleMultiplier = glGetUniformLocation(iProgId, "u_angleMultiplier");
        pu_rMultiplier = glGetUniformLocation(iProgId, "u_rMultiplier");
        pu_rInitialMultiplier = glGetUniformLocation(iProgId, "u_rInitialMultiplier");
        pu_xDivider = glGetUniformLocation(iProgId, "u_xDivider");
        pu_yDivider = glGetUniformLocation(iProgId, "u_yDivider");
        pu_ringOffsetMultiplier = glGetUniformLocation(iProgId, "u_ringOffsetMultiplier");
        pu_elapsedTime = glGetUniformLocation(iProgId, "u_elapsedTime");
        pu_minParticleSize = glGetUniformLocation(iProgId, "u_minParticleSize");
        pu_lifeMultiplier = glGetUniformLocation(iProgId, "u_lifeMultiplier");
        pu_xTransition = glGetUniformLocation(iProgId, "u_xTransition");
        pu_yTransition = glGetUniformLocation(iProgId, "u_yTransition");

        pa_timeOffset = glGetAttribLocation(iProgId, "a_timeOffset");
        pa_color = glGetAttribLocation(iProgId, "a_color");
        pa_life = glGetAttribLocation(iProgId, "a_life");
        pa_angle = glGetAttribLocation(iProgId, "a_angle");
        pa_ring = glGetAttribLocation(iProgId, "a_ring");
        pa_radius = glGetAttribLocation(iProgId, "a_radius");
        pa_velocity = glGetAttribLocation(iProgId, "a_velocity");

        for (int i = 0; i < config.particlesCount; i++) {
            //r,g,b,a
            fVertices[i*PARTICLE_SIZE + 0] = rnd(config.rColorOffset, 1);
            fVertices[i*PARTICLE_SIZE + 1] = rnd(config.gColorOffset, 1);
            fVertices[i*PARTICLE_SIZE + 2] = rnd(config.bColorOffset, 1);
            fVertices[i*PARTICLE_SIZE + 3] = rnd(config.aColorOffset, 1) /*> 0.5 ? 0.4f : 1*/;

            float radius = rnd(config.minInitParticleRadius, 1);
            float life = GLHelper.logBase(config.rMultiplier, config.minParticleRadius / radius);//log base rMultiplier
            life = life + (life * config.lifeMultiplier * 2);
            fVertices[i*PARTICLE_SIZE + 7] = radius;
            fVertices[i*PARTICLE_SIZE + 4] = (int)life;

            fVertices[i*PARTICLE_SIZE + 5] = gen.nextFloat();//angle
            fVertices[i*PARTICLE_SIZE + 6] = rnd(config.ringOffset, 1);//ring
            fVertices[i*PARTICLE_SIZE + 8] = gen.nextFloat() * config.velocityMultiplier + 1f;//velocity

            fVertices[i*PARTICLE_SIZE + 9] = gen.nextInt(config.particlesCount);//time offset
        }
        vertexBuffer.put(fVertices).position(0);
    }

    int elapsedTime = 1;

    @Override public void drawFrame(GLEngine glEngine, long currentTime) {
        elapsedTime++;
        //ParticleSystemConfig config = this.config.copy();
        glClear(GL_COLOR_BUFFER_BIT);
        glUseProgram(iProgId);

        Arrays.fill(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        float angleTransition = config.getAngleTransition(elapsedTime);
        if(angleTransition != 0.0f) {
            Matrix.rotateM(viewMatrix, 0, angleTransition, 0, 0, 1);
        }
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        glUniformMatrix4fv(pu_mvpMatrix, 1, false, mvpMatrix, 0);

        glUniform1f(pu_scale, config.pointSize);
        glUniform1f(pu_velocityDivider, config.velocityDivider / FPS);
        glUniform1f(pu_angleMultiplier, (float) (config.angleMultiplier * Math.PI));
        glUniform1f(pu_rMultiplier, config.rMultiplier);
        glUniform1f(pu_rInitialMultiplier, config.rInitialMultiplier);
        glUniform1f(pu_xDivider, config.getXDivider(elapsedTime));
        glUniform1f(pu_yDivider, config.getYDivider(elapsedTime));
        glUniform1f(pu_ringOffsetMultiplier, config.initialOffset);
        glUniform1f(pu_lifeMultiplier, config.lifeMultiplier);
        glUniform1f(pu_minParticleSize, config.minParticleRadius);

        glUniform1f(pu_xTransition, config.getXTransition(elapsedTime));//
        glUniform1f(pu_yTransition, config.getYTransition(elapsedTime));//

        glUniform1f(pu_elapsedTime, elapsedTime);
        glUniform1f(pu_scale, config.pointSize * pxPerDp);
        glUniform1f(pu_hole_r, config.ringSize);

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

        glDrawArrays(GL_POINTS, 0, config.particlesCount);
    }

    @Override public void onEnd(GLEngine glEngine) {
        if(iProgId != 0) {
            glDeleteProgram(iProgId);
        }
    }

    @Override public void onSizeChanged(GLEngine glEngine) {
        glEngine.applyFulSizedViewport();
        final float width = glEngine.getSurfaceConfig().getWidth();
        final float height = glEngine.getSurfaceConfig().getHeight();
        float ratio = width / height;

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override public int getMaxFPS() {
        return FPS;
    }

    public float rnd(float min, float max) {
        float fRandNum = (float)gen.nextDouble();
        return min + (max - min) * fRandNum;
    }
}
