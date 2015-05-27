package kidnox.particles;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {

    ParticlesView particlesView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(frameLayout);


        Paint paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setColor(Color.GREEN);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        Random random = new Random(System.currentTimeMillis());
        particlesView = new ParticlesView(this, 450, paint, random);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        particlesView.setLayoutParams(lp);
        frameLayout.addView(particlesView);

    }

    @Override protected void onResume() {
        super.onResume();
        particlesView.setStartValues();
        animate(Executors.newSingleThreadScheduledExecutor(), particlesView);
    }

    private void animate(final ScheduledExecutorService executor, final ParticlesView view) {
        executor.schedule(new Runnable() {
            @Override public void run() {
                view.calculateTranslations(0.95f);
                view.postInvalidate();
                animate(executor, view);
            }
        }, 16, TimeUnit.MILLISECONDS);
    }


    private class ParticlesView extends View {

        final int count;
        final float[] particles;
        final Paint paint;
        final Random random;

        int width;

        public ParticlesView(Context context, int particlesCount, Paint paint, Random random) {
            super(context);
            this.count = particlesCount;
            this.particles = new float[particlesCount * 2];
            this.paint = paint;
            this.random = random;
            width = getResources().getDisplayMetrics().widthPixels / 2;
        }

        public void setStartValues() {
            for(int i = 0; i < count - 1; i++) {
                particles[i] = width + random.nextInt(width);
            }
        }

        public synchronized void calculateTranslations(float speed) {
            for (int i = 0; i < particles.length - 1; i++) {
                float value = particles[i] * speed;
                if(value < 1) {
                    value = width + random.nextInt(width);
                }
                particles[i] = value;
            }
        }

        @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }

        @Override protected void onDraw(Canvas canvas) {
            canvas.drawPoints(particles, paint);
        }
    }

}
