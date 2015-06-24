package kidnox.particles.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


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
            DebugUtil.warn("Could not create new shader.");
            return 0;
        }
        // Pass in the shader source.
        glShaderSource(shaderObjectId, shaderCode);
        // Compile the shader.
        glCompileShader(shaderObjectId);
        // Get the compilation status.
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        DebugUtil.verbose("Results of compiling source: \n %s \n: %s", shaderCode, glGetShaderInfoLog(shaderObjectId));
        // Verify the compile status.
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId);
            DebugUtil.warn("Compilation of shader failed." + glGetError());
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
            DebugUtil.warn("Could not create new program");
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
        DebugUtil.verbose("Results of linking program:\n %s", glGetProgramInfoLog(programObjectId));
        // Verify the link status.
        if (linkStatus[0] == 0) {
            // If it failed, delete the program object.
            glDeleteProgram(programObjectId);
            DebugUtil.warn("Linking of program failed.");
            return 0;
        }
        // Return the program object ID.
        validateProgram(programObjectId);
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
        DebugUtil.verbose("Results of validating program: %s, \nLog:", validateStatus[0], glGetProgramInfoLog(programObjectId));
        return validateStatus[0] != 0;
    }

    public static int loadPoint(int size, int color) {
        Bitmap point = getBitmapPoint(size, color);
        return loadTexture(point);
    }

    public static int loadTexture(Bitmap bitmap) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            DebugUtil.warn("Could not generate a new OpenGL texture object.");
            return 0;
        }
        // Bind to the texture in OpenGL
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // Set filtering: a default must be set, or the texture will be
        // black.
        glTexParameteri(GL_TEXTURE_2D,
                GL_TEXTURE_MIN_FILTER,
                GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,
                GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.
        glGenerateMipmap(GL_TEXTURE_2D);

        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        bitmap.recycle();

        // Unbind from the texture.
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
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

    private static Bitmap getBitmapPoint(int size, int color) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        float i = size/ 2;
        canvas.drawCircle(i, i, i, paint);
        return bitmap;
    }

}
