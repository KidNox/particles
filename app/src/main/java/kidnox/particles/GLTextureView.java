package kidnox.particles;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class GLTextureView extends TextureView {

    TextureRenderer renderer;

    public GLTextureView(Context context) {
        super(context);
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GLTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        renderer = null;
    }

    /*public void onResume() {
        if(renderer != null) {
            renderer.onResume();
        }
    }

    public void onPause() {
        if(renderer != null) {
            renderer.onPause();
        }
    }*/

    public void setTextureRenderer(TextureRenderer renderer) {
        if(renderer == null) throw new NullPointerException();
        this.renderer = renderer;
        setSurfaceTextureListener(renderer);
    }
}
