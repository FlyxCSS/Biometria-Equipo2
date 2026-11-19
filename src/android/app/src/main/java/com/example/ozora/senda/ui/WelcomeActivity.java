package com.example.ozora.senda.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.Login;

/** Entrada visual únicamente al abrir la app desde el icono, nunca al cerrar sesión. */
public final class WelcomeActivity extends FieldScreen {
    private ValueAnimator animator;
    private boolean navigated;
    private final Runnable enter = () -> {
        if (navigated || isFinishing() || isDestroyed()) return;
        navigated = true;
        startActivity(new Intent(this, Login.class));
        finish();
    };
    private Intro view;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!isTaskRoot() && Intent.ACTION_MAIN.equals(getIntent().getAction())) { finish(); return; }
        view = new Intro(this);
        setContentView(view);
        protegerBarras(view);
        view.setContentDescription("SENDA. Conoce el aire. Protege el entorno. Toca para continuar.");
        view.setOnClickListener(v -> enter.run());
        AccessibilityManager accessibility = (AccessibilityManager)getSystemService(ACCESSIBILITY_SERVICE);
        boolean reduced = Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
                || (accessibility != null && accessibility.isTouchExplorationEnabled());
        if (state != null || reduced) { view.progress = 1; view.post(enter); return; }
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(2100);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(a -> {
            view.progress = (float)a.getAnimatedValue();
            view.invalidate();
            if (view.progress >= 1) view.postDelayed(enter, 200);
        });
        animator.start();
    }
    @Override protected void onPause() {
        if (animator != null) animator.pause();
        if (view != null) view.removeCallbacks(enter);
        super.onPause();
    }
    @Override protected void onResume() {
        super.onResume();
        if (animator != null && animator.isPaused()) animator.resume();
        if (view != null && view.progress >= 1) view.post(enter);
    }
    @Override protected void onDestroy() {
        if (animator != null) animator.cancel();
        if (view != null) view.removeCallbacks(enter);
        super.onDestroy();
    }
    private static final class Intro extends View {
        float progress;
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Drawable landscape, logo;
        final Path route = new Path(), trail = new Path();
        final PathMeasure measure = new PathMeasure();
        final float[] position = new float[2];
        Intro(Context context) {
            super(context);
            landscape = context.getDrawable(R.drawable.auth_landscape);
            logo = context.getDrawable(R.drawable.auth_logo);
        }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth(), h = getHeight();
            canvas.drawColor(0xfff8f6eb);
            landscape.setBounds(0, 0, (int)w, (int)h);
            canvas.save();
            canvas.clipRect(0, h * (1-progress), w, h);
            landscape.draw(canvas);
            canvas.restore();
            route.reset();
            route.moveTo(w*.5f,h*.94f);
            route.cubicTo(w*.05f,h*.76f,w*.95f,h*.67f,w*.5f,h*.52f);
            route.cubicTo(w*.13f,h*.40f,w*.82f,h*.31f,w*.5f,h*.27f);
            measure.setPath(route,false);
            measure.getPosTan(measure.getLength()*progress,position,null);
            trail.reset(); measure.getSegment(0,measure.getLength()*progress,trail,true);
            paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(w*.014f);
            paint.setStrokeCap(Paint.Cap.ROUND); paint.setColor(0xfff8f6eb);
            paint.setAlpha((int)(200*(1-progress)));
            canvas.drawPath(trail,paint);
            float size=w*(.13f+.21f*progress);
            logo.setBounds((int)(position[0]-size/2),(int)(position[1]-size/2),
                    (int)(position[0]+size/2),(int)(position[1]+size/2));
            logo.draw(canvas);
            paint.setStyle(Paint.Style.FILL); paint.setColor(0xff1d4032);
            paint.setAlpha((int)(255*Math.max(0,(progress-.65f)/.35f)));
            paint.setTextAlign(Paint.Align.CENTER); paint.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));
            paint.setTextSize(w*.082f);
            canvas.drawText("S E N D A",w/2,h*.42f,paint);
            paint.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL)); paint.setTextSize(w*.041f);
            canvas.drawText("Conoce el aire. Protege el entorno.",w/2,h*.48f,paint);
        }
    }
}
