package kidnox.particles;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class GLProgramImpl implements GLProgram {

    private static final String sSimpleVS =
            "attribute vec4 position;\n" +
                    "attribute vec2 texCoords;\n" +
                    "varying vec2 outTexCoords;\n" +
                    "\nvoid main(void) {\n" +
                    "    outTexCoords = texCoords;\n" +
                    "    gl_Position = position;\n" +
                    "}\n\n";
    private static final String sSimpleFS =
            "precision mediump float;\n\n" +
                    "varying vec2 outTexCoords;\n" +
                    "uniform sampler2D texture;\n" +
                    "\nvoid main(void) {\n" +
                    "    gl_FragColor = texture2D(texture, outTexCoords);\n" +
                    "}\n\n";

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
            1.0f,  1.0f, 0.0f, 1.0f, 1.0f,
    };

    private FloatBuffer triangleVertices;
    private int attribPosition;
    private int attribTexCoords;

    @Override public void onBegin(GLEngine glEngine) {
        triangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        triangleVertices.put(mTriangleVerticesData).position(0);

        //int texture = loadTexture(R.drawable.large_photo);
        int program = buildProgram(sSimpleVS, sSimpleFS);

        attribPosition = glGetAttribLocation(program, "position");
        checkGlError();

        attribTexCoords = glGetAttribLocation(program, "texCoords");
        checkGlError();

        /*int uniformTexture = glGetUniformLocation(program, "texture");
        checkGlError();

        glBindTexture(GL_TEXTURE_2D, texture);
        checkGlError();*/

        glUseProgram(program);
        checkGlError();

        glEnableVertexAttribArray(attribPosition);
        checkGlError();

        glEnableVertexAttribArray(attribTexCoords);
        checkGlError();

        /*glUniform1i(uniformTexture, texture);
        checkGlError();*/
    }

    private int buildProgram(String vertex, String fragment) {
        int vertexShader = buildShader(vertex, GL_VERTEX_SHADER);
        if (vertexShader == 0) return 0;

        int fragmentShader = buildShader(fragment, GL_FRAGMENT_SHADER);
        if (fragmentShader == 0) return 0;

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        checkGlError();

        glAttachShader(program, fragmentShader);
        checkGlError();

        glLinkProgram(program);
        checkGlError();

        int[] status = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, status, 0);
        if (status[0] != GL_TRUE) {
            String error = glGetProgramInfoLog(program);
            Log.d("GL", "Error while linking program:\n" + error);
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            glDeleteProgram(program);
            return 0;
        }
        return program;
    }

    private int buildShader(String source, int type) {
        int shader = glCreateShader(type);

        glShaderSource(shader, source);
        checkGlError();

        glCompileShader(shader);
        checkGlError();

        int[] status = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, status, 0);
        if (status[0] != GL_TRUE) {
            String error = glGetShaderInfoLog(shader);
            Log.d("GL", "Error while compiling shader:\n" + error);
            glDeleteShader(shader);
            return 0;
        }

        return shader;
    }


    @Override public void drawFrame(GLEngine glEngine) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        // drawQuad
        triangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        glVertexAttribPointer(attribPosition, 3, GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);

        triangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        glVertexAttribPointer(attribTexCoords, 3, GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, triangleVertices);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        if (!glEngine.egl.eglSwapBuffers(glEngine.eglDisplay, glEngine.eglSurface)) {
            throw new RuntimeException("Cannot swap buffers");
        }
    }

    @Override public void onEnd(GLEngine glEngine) {

    }


    private static void checkGlError() {
        int error = glGetError();
        if (error != GL10.GL_NO_ERROR) {
            Log.w("GL", "GL error = 0x" + Integer.toHexString(error));
        }
    }
}
