/*
 * Nombre: WelcomeActivity.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.Login;

/**
 * Entrada al abrir la app desde el icono (nunca al cerrar sesión): el logo recorre la senda
 * mientras crece el bosque y termina en su sitio de la pantalla de bienvenida.
 */
public final class WelcomeActivity extends FieldScreen {
    private boolean navigated;
    private ValueAnimator animator;
    private Intro intro;


    /** --------------------
     * Diseño lógico: state: EstadoVista --> onCreate() -->
     * Descripción: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!isTaskRoot() && Intent.ACTION_MAIN.equals(getIntent().getAction())) { finish(); return; }
        setContentView(R.layout.activity_welcome);
        protegerBarras(findViewById(R.id.welcome_root));
        findViewById(R.id.welcome_start).setOnClickListener(v -> enter());
        AccessibilityManager accessibility = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        boolean reduced = Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
                || (accessibility != null && accessibility.isTouchExplorationEnabled());
        if (state == null && !reduced) playIntro();
    }


    /** --------------------
     * Diseño lógico: playIntro() -->
     * Descripción: Play Intro. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void playIntro() {
        View brand = findViewById(R.id.welcome_brand);
        View tagline = findViewById(R.id.welcome_tagline);
        View logo = findViewById(R.id.welcome_logo);
        View start = findViewById(R.id.welcome_start);
        View forest = findViewById(R.id.welcome_forest);
        logo.setAlpha(0f); tagline.setAlpha(0f); start.setAlpha(0f); forest.setAlpha(0f);
        start.setVisibility(View.INVISIBLE);
        intro = new Intro(this);
        intro.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        intro.setElevation(dp(24));
        ViewGroup root = findViewById(R.id.welcome_root);
        root.addView(intro, new ViewGroup.LayoutParams(-1, -1));
        // Tocar durante la animación la salta.
        intro.setOnClickListener(v -> { if (animator != null) animator.end(); });
        intro.post(() -> {
            if (isFinishing()) return;
            int[] origin = new int[2], target = new int[2];
            intro.getLocationInWindow(origin);
            logo.getLocationInWindow(target);
            intro.target.set(target[0] - origin[0], target[1] - origin[1],
                    target[0] - origin[0] + logo.getWidth(), target[1] - origin[1] + logo.getHeight());
            animator = ValueAnimator.ofFloat(0, 1);
            animator.setDuration(2300);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(a -> { intro.progress = (float) a.getAnimatedValue(); intro.invalidate(); });
            animator.addListener(new android.animation.AnimatorListenerAdapter() {

                /** --------------------
                 * Diseño lógico: a: androidanimationAnimator --> onAnimationEnd() -->
                 * Descripción: On Animation End. Aplicación de campo: acceso, captura, recorridos y presentación Android
                 * -------------------- */
                @Override public void onAnimationEnd(android.animation.Animator a) { finishIntro(); }
            });
            animator.start();
        });
    }

    /** Sustituye el lienzo por la pantalla real sin salto visual. */

    /** --------------------
     * Diseño lógico: finishIntro() -->
     * Descripción: Finish Intro. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void finishIntro() {
        if (intro == null) return;
        Intro done = intro;
        intro = null;
        DecelerateInterpolator ease = new DecelerateInterpolator(2f);
        findViewById(R.id.welcome_logo).setAlpha(1f);
        findViewById(R.id.welcome_forest).setAlpha(1f);
        done.animate().alpha(0f).setDuration(250).withEndAction(() -> {
            if (done.getParent() != null) ((ViewGroup) done.getParent()).removeView(done);
        }).start();
        View tagline = findViewById(R.id.welcome_tagline);
        View start = findViewById(R.id.welcome_start);
        start.setVisibility(View.VISIBLE);
        tagline.setTranslationY(dp(12)); start.setTranslationY(dp(24));
        tagline.animate().alpha(1f).translationY(0).setStartDelay(100).setDuration(500).setInterpolator(ease).start();
        start.animate().alpha(1f).translationY(0).setStartDelay(300).setDuration(500).setInterpolator(ease).start();
    }


    /** --------------------
     * Diseño lógico: enter() -->
     * Descripción: Enter. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void enter() {
        if (navigated || isFinishing() || isDestroyed()) return;
        navigated = true;
        startActivity(new Intent(this, Login.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


    /** --------------------
     * Diseño lógico: onPause() -->
     * Descripción: On Pause. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onPause() {
        if (animator != null) animator.pause();
        super.onPause();
    }


    /** --------------------
     * Diseño lógico: onResume() -->
     * Descripción: On Resume. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onResume() {
        super.onResume();
        if (animator != null && animator.isPaused()) animator.resume();
    }


    /** --------------------
     * Diseño lógico: onDestroy() -->
     * Descripción: On Destroy. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onDestroy() {
        if (animator != null) { animator.removeAllListeners(); animator.cancel(); }
        super.onDestroy();
    }

    /** Animación original: el bosque aparece desde abajo y el logo sigue la senda creciendo. */
    private static final class Intro extends View {
        float progress;
        final RectF target = new RectF();
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        final Bitmap forest;
        final Drawable logo;
        final Path route = new Path(), trail = new Path();
        final PathMeasure measure = new PathMeasure();
        final float[] position = new float[2];


        /** --------------------
         * Diseño lógico: context: Entorno --> Intro() -->
         * Intro <--
         * Descripción: Construye Intro. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        Intro(Context context) {
            super(context);
            setClickable(true);
            forest = BitmapFactory.decodeResource(context.getResources(), R.drawable.senda_forest);
            logo = context.getDrawable(R.drawable.senda_logo);
        }


        /** --------------------
         * Diseño lógico: canvas: Canvas --> onDraw() -->
         * Descripción: On Draw. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth(), h = getHeight();
            canvas.drawColor(0xfff4f1ea);
            // Bosque anclado abajo, igual que en la pantalla final, revelándose de abajo arriba.
            float scale = w / forest.getWidth();
            float fh = forest.getHeight() * scale;
            RectF dst = new RectF(0, h - fh, w, h);
            canvas.save();
            canvas.clipRect(0, h - fh * Math.min(1f, progress * 1.15f), w, h);
            canvas.drawBitmap(forest, null, dst, bitmapPaint);
            canvas.restore();

            float tx = target.isEmpty() ? w / 2 : target.centerX();
            float ty = target.isEmpty() ? h * .3f : target.centerY();
            route.reset();
            route.moveTo(w * .5f, h * .97f);
            route.cubicTo(w * .05f, h * .80f, w * .95f, h * .68f, w * .5f, h * .55f);
            route.cubicTo(w * .15f, h * .45f, w * .80f, ty + h * .08f, tx, ty);
            measure.setPath(route, false);
            float length = measure.getLength();
            measure.getPosTan(length * progress, position, null);
            trail.reset();
            measure.getSegment(0, length * progress, trail, true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(w * .014f);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(0xfff4f1ea);
            paint.setAlpha((int) (210 * (1 - progress)));
            canvas.drawPath(trail, paint);

            float fullW = target.isEmpty() ? w * .4f : target.width();
            float fullH = target.isEmpty() ? w * .45f : target.height();
            float s = .32f + .68f * progress;
            float lw = fullW * s, lh = fullH * s;
            logo.setBounds((int) (position[0] - lw / 2), (int) (position[1] - lh / 2),
                    (int) (position[0] + lw / 2), (int) (position[1] + lh / 2));
            logo.draw(canvas);
        }
    }
}
