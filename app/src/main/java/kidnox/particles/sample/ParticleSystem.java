package kidnox.particles.sample;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

public class ParticleSystem {

    final static int NUM_PARTICLES = 800;
    final static int PARTICLE_SIZE = 7;
    float nTimeCounter = 0;
    //each particle contains
    //r, g, b, a, move, rand
    float[] fVertices = new float[NUM_PARTICLES * PARTICLE_SIZE];
    final FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(fVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    final Random gen = new Random(System.currentTimeMillis());


    /*
    uniform float u_time;
uniform float u_hole_radius;
uniform float u_scle_factor;

attribute vec4 a_color;
attribute float a_move;
attribute float a_rand;

varying vec4 v_color;
    * */

    public void init() {
        for (int i = 0; i < NUM_PARTICLES; i++) {
            //r,g,b,a
            fVertices[i*PARTICLE_SIZE + 0] = gen.nextFloat();
            fVertices[i*PARTICLE_SIZE + 1] = gen.nextFloat();
            fVertices[i*PARTICLE_SIZE + 2] = gen.nextFloat();
            fVertices[i*PARTICLE_SIZE + 3] = gen.nextFloat();

            //move, ring, rand
            fVertices[i*PARTICLE_SIZE + 4] = ((gen.nextFloat() * 4) + 1) / 500;
            fVertices[i*PARTICLE_SIZE + 5] = ((gen.nextFloat() * 4) + 1) / 500;
            fVertices[i*PARTICLE_SIZE + 6] = gen.nextFloat() * 7;

        }
        vertexBuffer.put(fVertices).position(0);
    }

    public void update(int iColor, int iMove, int iRing, int iRand) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(iColor, 4, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(iColor);

        vertexBuffer.position(4);
        GLES20.glVertexAttribPointer(iMove, 1, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(iMove);

        vertexBuffer.position(5);
        GLES20.glVertexAttribPointer(iRand, 1, GLES20.GL_FLOAT, false, PARTICLE_SIZE * 4, vertexBuffer);
        GLES20.glEnableVertexAttribArray(iRand);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, NUM_PARTICLES);
    }

    public float rnd(float min, float max) {
        float fRandNum = (float)gen.nextDouble();
        return min + (max - min) * fRandNum;
    }

}
