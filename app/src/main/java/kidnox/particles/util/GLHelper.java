package kidnox.particles.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import kidnox.particles.BuildConfig;

import static android.opengl.GLES20.*;

@SuppressWarnings("HardCodedStringLiteral")
public class GLHelper {

    public static int compileVertexShader(Context context, int resId) {
        return compileShader(GL_VERTEX_SHADER, readTextFileFromResource(context, resId));
    }

    public static int compileVertexShader(Context context, String assetPath) {
        return compileShader(GL_VERTEX_SHADER, readFileFromAssets(context, assetPath));
    }

    public static int compileFragmentShader(Context context, int resId) {
        return compileShader(GL_FRAGMENT_SHADER, readTextFileFromResource(context, resId));
    }

    public static int compileFragmentShader(Context context, String assetPath) {
        return compileShader(GL_FRAGMENT_SHADER, readFileFromAssets(context, assetPath));
    }

    private static int compileShader(int type, String shaderCode) {
        // Create a new shader object.
        final int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {
            if (BuildConfig.DEBUG) {
                Log.w("GLHelper", "Could not create new shader.");
            }
            return 0;
        }
        // Pass in the shader source.
        glShaderSource(shaderObjectId, shaderCode);
        // Compile the shader.
        glCompileShader(shaderObjectId);
        // Get the compilation status.
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        if (BuildConfig.DEBUG) {
            // Print the shader info log to the Android log output.
            Log.v("GLHelper", "Results of compiling source:" + "\n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));
        }
        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId);
            if (BuildConfig.DEBUG) {
                Log.w("GLHelper", "Compilation of shader failed.");
            }
            return 0;
        }
        // Return the shader object ID.
        return shaderObjectId;
    }

    /**
     * Links a vertex shader and a fragment shader together into an OpenGL
     * program. Returns the OpenGL program object ID, or 0 if linking failed.
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        // Create a new program object.
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            if (BuildConfig.DEBUG) {
                Log.w("GLHelper", "Could not create new program");
            }
            return 0;
        }
        // Attach the vertex shader to the program.
        glAttachShader(programObjectId, vertexShaderId);
        // Attach the fragment shader to the program.
        glAttachShader(programObjectId, fragmentShaderId);
        // Link the two shaders together into a program.
        glLinkProgram(programObjectId);
        // Get the link status.
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        if (BuildConfig.DEBUG) {
            // Print the program info log to the Android log output.
            Log.v("GLHelper", "Results of linking program:\n" + glGetProgramInfoLog(programObjectId));
        }
        // Verify the link status.
        if (linkStatus[0] == 0) {
            // If it failed, delete the program object.
            glDeleteProgram(programObjectId);
            if (BuildConfig.DEBUG) {
                Log.w("GLHelper", "Linking of program failed.");
            }
            return 0;
        }
        // Return the program object ID.
        return programObjectId;
    }

    /**
     * Validates an OpenGL program. Should only be called when developing the
     * application.
     */
    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v("GLHelper", "Results of validating program: " + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

    //////////////////////////////////////////////////////////////

    private static String readFileFromAssets(Context context, String path) {
        try {
            InputStream is = context.getAssets().open(path);
            return readStringFromInputStream(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readTextFileFromResource(Context context, int resourceId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            return readStringFromInputStream(inputStream);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }
    }

    private static String readStringFromInputStream(InputStream is) {
        StringBuilder body = new StringBuilder();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not open stream", e);
        }
        return body.toString();
    }

}
