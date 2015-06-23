package kidnox.particles;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import kidnox.particles.particles.GLParticleProgram;
import kidnox.particles.sample.ParticleProgram;
import kidnox.particles.test.GLSurfaceViewExt;


public class MainActivity extends Activity {

    GLTextureView glTextureView;
    GLSurfaceViewExt glSurfaceViewExt;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.setBackgroundColor(Color.BLACK);
        setContentView(frameLayout);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        glTextureView = new GLTextureView(this) {
            @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            }
        };
        glTextureView.setGLProgram(new ParticleProgram(this));
        frameLayout.addView(glTextureView, lp);
        /*glSurfaceViewExt = new GLSurfaceViewExt(this);
        glSurfaceViewExt.setGlProgram(new GLProgramImpl(this));
        frameLayout.addView(glSurfaceViewExt);*/
    }

}
