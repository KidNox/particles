package kidnox.particles;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import kidnox.particles.util.DebugUtil;

public class TextureRenderer implements TextureView.SurfaceTextureListener {

    private final GLProgram glProgram;

    private DrawingLoop drawingLoop;

    public TextureRenderer(final GLProgram _glProgram) {
        if(DebugUtil.DEBUG) {
            this.glProgram = new GLProgram() { //debug proxy
                @Override public void onBegin(GLEngine glEngine) {
                    checkThread("onBegin");
                    _glProgram.onBegin(glEngine);
                }

                @Override public void drawFrame(GLEngine glEngine, long currentTime) {
                    //checkThread("drawFrame");
                    fpsPrinter(glEngine);
                    _glProgram.drawFrame(glEngine, currentTime);
                }

                @Override public void onEnd(GLEngine glEngine) {
                    checkThread("onEnd");
                    _glProgram.onEnd(glEngine);
                }

                @Override public void onSizeChanged(GLEngine glEngine) {
                    checkThread("onSizeChanged");
                    _glProgram.onSizeChanged(glEngine);
                }

                @Override public int getMaxFPS() {
                    return _glProgram.getMaxFPS();
                }
            };
        } else {
            this.glProgram = _glProgram;
        }
    }

    @Override public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        log("onSurfaceTextureAvailable");
        drawingLoop = new DrawingLoop(glProgram);
        drawingLoop.enter(surface, new SurfaceConfig(width, height));
        drawingLoop.startLoop();
    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        log("onSurfaceTextureSizeChanged");
        if(drawingLoop != null && drawingLoop.getSurfaceConfig() != null) {
            drawingLoop.getSurfaceConfig().setSize(width, height);
        }
    }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        log("onSurfaceTextureDestroyed");
        if (drawingLoop != null) {
            drawingLoop.exit();
            drawingLoop = null;
        }
        return true;
    }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //log("onSurfaceTextureUpdated");
    }

    void onResume() {
        if(drawingLoop != null) {
            drawingLoop.onResume();
        }
    }

    void onPause() {
        if (drawingLoop != null) {
            drawingLoop.onPause();
        }
    }

    static void log(Object message) {
        Log.d("TextureRenderer", String.valueOf(message));
    }

    long lastPrintTime;
    int frameCounter;
    void fpsPrinter(GLEngine glEngine) {
        if(lastPrintTime == 0) {
            lastPrintTime = System.currentTimeMillis();
        }
        frameCounter++;
        if(System.currentTimeMillis() - lastPrintTime > 1000) {
            log("FPS = " + frameCounter);
            frameCounter = 0;
            lastPrintTime = System.currentTimeMillis();
        }
    }

    void checkThread(String methodName) {
        if(!"DrawingLoop".equals(Thread.currentThread().getName())) {
            throw new IllegalStateException("wrong thread for opengl program, method = " + methodName + ", expected thread = "+drawingLoop + ", actual = "+Thread.currentThread());
        }
    }

}
