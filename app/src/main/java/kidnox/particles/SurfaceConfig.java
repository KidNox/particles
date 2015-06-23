package kidnox.particles;

public class SurfaceConfig {
    private int width;
    private int height;

    public SurfaceConfig() {
    }

    public SurfaceConfig(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public final void setSize(int w, int h) {
        setWidth(w);
        setHeight(h);
    }
}
