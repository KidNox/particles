package kidnox.particles;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

public class TextureRenderer implements TextureView.SurfaceTextureListener {

    private DrawingLoop drawingLoop;
    private RSEngine rsEngine;

    @Override public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        log("onSurfaceTextureAvailable");
        drawingLoop = new DrawingLoop();
        rsEngine = new RSEngine(surface, width, height);
        drawingLoop.enter(surface);
    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        log("onSurfaceTextureSizeChanged");//TODO use instead onSurfaceTextureAvailable?
    }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        log("onSurfaceTextureDestroyed");
        if (drawingLoop != null) {
            drawingLoop.exit();
            rsEngine.destroy();

        }
        return true;
    }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        log("onSurfaceTextureUpdated");
    }

    static void log(Object message) {
        Log.d("TextureRenderer", String.valueOf(message));
    }

}
