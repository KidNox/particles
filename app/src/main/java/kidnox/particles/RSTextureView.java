package kidnox.particles;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class RSTextureView extends TextureView {

    TextureRenderer renderer;

    public RSTextureView(Context context) {
        super(context);
    }

    public RSTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RSTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTextureRenderer(TextureRenderer renderer) {
        if(renderer == null) throw new NullPointerException();
        this.renderer = renderer;
        setSurfaceTextureListener(renderer);
    }

}
