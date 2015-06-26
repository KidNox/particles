package kidnox.particles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import kidnox.particles.sample.BlackHoleParticleProgram;


public class MainActivity extends Activity {

    FrameLayout container;
    GLTextureView glTextureView;

    BlackHoleParticleProgram particleProgram;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        container = (FrameLayout) findViewById(R.id.container);
        reset();
    }

    private void reset() {
        container.removeAllViews();
        glTextureView = new GLTextureView(this) {
            /*@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            }*/
        };
        particleProgram = new BlackHoleParticleProgram(this, App.loadConfig());
        glTextureView.setGLProgram(particleProgram);
        container.addView(glTextureView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void onResetClick(View v) {
        if(!disableActionFor(1000)) return;
        reset();
    }

    public void onConfigClick(View v) {
        if(!disableActionFor(1000)) return;
        startActivity(new Intent(this, ConfigActivity.class));
    }

    public void onExpandClick(View v) {
        if(!disableActionFor(1000)) return;
    }

    public void onCollapseClick(View v) {
        if(!disableActionFor(1000)) return;
    }

    public void onMenuClick(View v) {

    }

    boolean actionDisabled;
    private boolean disableActionFor(long delay) {
        if(actionDisabled) return false;
        actionDisabled = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override public void run() {
                actionDisabled = false;
            }
        }, delay);
        return true;
    }


    @Override protected void onResume() {
        super.onResume();
        reset();
    }

    @Override protected void onPause() {
        super.onPause();
        container.removeAllViews();
    }

}
