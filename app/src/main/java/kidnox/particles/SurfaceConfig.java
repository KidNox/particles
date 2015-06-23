package kidnox.particles;

public class SurfaceConfig {
    private int width;
    private int height;

    private volatile OnConfigChangedListener onConfigChangedListener;

    public SurfaceConfig() {
    }

    public SurfaceConfig(int width, int height) {
        setSize(width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public synchronized final void setSize(int w, int h) {
        this.width = w;
        this.height = h;
        if(onConfigChangedListener != null) {
            onConfigChangedListener.onSizeChanged(w, h);
        }
    }

    public synchronized void setOnConfigChangedListener(OnConfigChangedListener listener) {
        listener.onSizeChanged(width, height);
        this.onConfigChangedListener = listener;
    }

    public interface OnConfigChangedListener {
        void onSizeChanged(int w, int h);
    }
}
