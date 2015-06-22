package kidnox.particles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

public class RSEngine {

    private final Context context;

    private RenderScript rs;
    private Allocation bAllocation;

    public RSEngine(Context context, SurfaceTexture surfaceTexture, int w, int h) {
        this.context = context.getApplicationContext();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        rs = RenderScript.create(context, BuildConfig.DEBUG ? RenderScript.ContextType.DEBUG : RenderScript.ContextType.NORMAL);
        bAllocation = Allocation.createFromBitmap(rs, bitmap);
        
    }

    public void destroy() {
        bAllocation.destroy();
    }

}
