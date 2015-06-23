package kidnox.particles;

import android.graphics.SurfaceTexture;
import android.util.Log;

import java.util.concurrent.Semaphore;


public class DrawingLoop extends Thread {

    public static int FPS = 60;

    private final GLProgram glProgram;
    private SurfaceTexture surface;
    private SurfaceConfig surfaceConfig;

    private final Semaphore nextFrameAvailable = new Semaphore(0);

    private volatile boolean isRunning;
    private volatile boolean isPausing;

    public DrawingLoop(GLProgram glProgram) {
        super("DrawingLoop");
        if(glProgram == null) throw new NullPointerException();
        this.glProgram = glProgram;
        setUncaughtExceptionHandler(new ExceptionHandler());
    }

    public synchronized void enter(SurfaceTexture surface, SurfaceConfig surfaceConfig) {
        if(this.surface != null) throw new NullPointerException();
        if(this.surfaceConfig != null) throw new NullPointerException();
        this.surface = surface;
        this.surfaceConfig = surfaceConfig;
    }

    public synchronized void startLoop() {
        isRunning = true;
        start();
    }

    private synchronized GLEngine lazyInit() {
        return new GLEngine(glProgram, surface, surfaceConfig);
    }

    public synchronized void exit() {
        Log.d("DrawingLoop", "exit");
        isRunning = false;
        nextFrameAvailable.release();
    }

    protected void onReleaseResources(GLEngine glEngine) {
        Log.d("DrawingLoop", "onReleaseResources");
        glEngine.destroy();
        surface = null;
    }

    @Override public void run() {
        GLEngine glEngine = lazyInit();
        glProgram.onBegin(glEngine);
        try {
            loop(glEngine);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            glProgram.onEnd(glEngine);
            onReleaseResources(glEngine);
        }
    }

    private void loop(GLEngine glEngine) throws InterruptedException {
        final int frameDuration = 1000 / FPS;
        isPausing = false;
        while (isRunning) {
            synchronized (this) {
                if(!isRunning) return;
                if(isPausing) {
                    nextFrameAvailable.drainPermits();
                    nextFrameAvailable.acquire();
                    if(!isRunning) return;
                }
                long startTime = System.currentTimeMillis();
                glProgram.drawFrame(glEngine, startTime);
                glEngine.swapBuffers();
                long deltaTime = frameDuration - (System.currentTimeMillis() - startTime);
                if(deltaTime > 0) {
                    Thread.sleep(deltaTime);
                }
            }
        }
    }

    public final void onPause() {
        if(isRunning) {
            isPausing = true;
        }
    }

    public final void onResume() {
        if(isRunning) {
            isPausing = false;
            nextFrameAvailable.release();
        }
    }

    public SurfaceConfig getSurfaceConfig() {
        return surfaceConfig;
    }

    static class ExceptionHandler implements UncaughtExceptionHandler {
        @SuppressWarnings("HardCodedStringLiteral")
        @Override public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("DrawingLoop", "dead", ex);
        }
    }
}
