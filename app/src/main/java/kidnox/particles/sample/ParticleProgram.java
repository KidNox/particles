package kidnox.particles.sample;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;

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

    int iProgId;
    int iPosition;
    int iTexture;
    int iColor;
    int iTexId;
    int iMove;
    int iTimes;
    int iLife, iAge;
    int iSize;
    float[] fVertex = {0,0,0};
    FloatBuffer vertexBuffer;

    final Context context;
    ParticleSystem particleSystem;

    public ParticleProgram(Context context) {
        this.context = context;

    }

    @Override public void onBegin(GLEngine glEngine) {
        particleSystem = new ParticleSystem();
        vertexBuffer = ByteBuffer.allocateDirect(fVertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(fVertex).position(0);

        glClearColor(0, 0, 0, 1);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        int vertexShader = GLHelper.compileVertexShader(context, R.raw.particle_vertex);
        int fragmentShader = GLHelper.compileFragmentShader(context, R.raw.particle_fragment);

        iProgId = GLHelper.linkProgram(vertexShader, fragmentShader);
        iTexId = GLHelper.loadPoint(pointSize, Color.GREEN);

        iPosition = glGetAttribLocation(iProgId, "a_position");
        iTexture = glGetUniformLocation(iProgId, "u_texture");
        iColor = glGetAttribLocation(iProgId, "a_color");
        iMove = glGetAttribLocation(iProgId, "a_move");
        iTimes = glGetUniformLocation(iProgId, "u_time");
        iLife = glGetAttribLocation(iProgId, "a_life");
        iAge = glGetAttribLocation(iProgId, "a_age");

        iSize = glGetUniformLocation(iProgId, "u_size");


        particleSystem.init(0, 0, 0);

    }

    @Override public void drawFrame(GLEngine glEngine, long currentTime) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUseProgram(iProgId);
        glUniform1f(iSize, pointSize);
                /*GLES20.glVertexAttribPointer(iPosition, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
                GLES20.glEnableVertexAttribArray(iPosition);*/

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, iTexId);


        glUniform1i(iTexture, 0);
//              GLES20.glUniform4f(iColor, 0.5f, 1f, 0.5f, 1f);

        particleSystem.update(iPosition, iMove, iTimes, iColor, iLife, iAge);
    }

    @Override public void onEnd(GLEngine glEngine) {

    }

    @Override public void onSizeChanged(GLEngine glEngine) {
        glViewport(0, 0, glEngine.getSurfaceConfig().getWidth(), glEngine.getSurfaceConfig().getHeight());
    }

    @Override public int getMaxFPS() {
        return 60;
    }
}
