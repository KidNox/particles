package kidnox.particles;

import android.graphics.SurfaceTexture;
import android.util.Log;

import java.util.concurrent.CountDownLatch;


public class DrawingLoop extends Thread implements SurfaceTexture.OnFrameAvailableListener {

    private volatile boolean initialized;
    private volatile boolean isRunning;
    private final CountDownLatch nextFrameAvailable = new CountDownLatch(1);

    private SurfaceTexture surface;

    {
        setUncaughtExceptionHandler(new ExceptionHandler());
    }

    public void enter(SurfaceTexture surface) {
        if(this.surface != null) throw new IllegalStateException();
        this.surface = surface;
    }

    @Override public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        nextFrameAvailable.countDown();
    }

    public synchronized void startLoop() {
        isRunning = true;
        start();
    }

    private synchronized void lazyInit() {
        if(surface == null) throw new NullPointerException();
        initialized = true;
    }

    public synchronized void exit() {
        isRunning = false;
    }

    protected void onReleaseResources() {
        if(surface != null) {
            surface.setOnFrameAvailableListener(null);
            surface = null;
        }
    }

    @Override public void run() {
        if(!initialized) {
            lazyInit();
        }
        try {
            loop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            onReleaseResources();
        }
    }

    private void loop() throws InterruptedException {
        while (isRunning) {
            nextFrameAvailable.await();
            if(!isRunning) return;
            drawFrame();
        }
    }

    protected synchronized void drawFrame() {

    }

    static class ExceptionHandler implements UncaughtExceptionHandler {
        @SuppressWarnings("HardCodedStringLiteral")
        @Override public void uncaughtException(Thread thread, Throwable ex) {
            Log.e("DrawingLoop", "dead", ex);
        }
    }
}
