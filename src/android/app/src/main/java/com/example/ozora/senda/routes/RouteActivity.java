/*
 * Nombre: RouteActivity.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.routes;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ozora.senda.MainActivity;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.Route;
import com.example.ozora.senda.model.RoutePoint;
import com.example.ozora.senda.ui.RouteMap;
import com.example.ozora.senda.ui.RouteScreen;
import com.example.ozora.senda.ui.SummaryActivity;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class RouteActivity extends RouteScreen {
    private final Handler clock = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {

        /** --------------------
         * Diseño lógico: run() -->
         * Descripción: Run. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        @Override public void run() {
            if (refresh()) clock.postDelayed(this, 1000);
        }
    };
    private SessionManager session;
    private RouteManager manager;
    private RouteMap map;
    private long id;
    private int silenceSeconds;
    private LinearLayout header, controls;
    private TextView signal, duration, count, lastOzone, recording;
    private com.example.ozora.senda.ui.CalidadAire calidadAvisada;
    private int conteoAvisado = -1;

    /** Vibración corta al entrar en una zona de mala calidad (si el móvil lo permite). */
    private void vibrarAviso() {
        android.os.Vibrator vibrador = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrador == null || !vibrador.hasVibrator()) return;
        if (android.os.Build.VERSION.SDK_INT >= 26)
            vibrador.vibrate(android.os.VibrationEffect.createWaveform(new long[]{0, 180, 120, 180}, -1));
        else vibrador.vibrate(new long[]{0, 180, 120, 180}, -1);
    }



    /** --------------------
     * Diseño lógico: state: EstadoVista --> onCreate() -->
     * Descripción: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        session = SessionManager.current();
        if (session == null) { finish(); return; }
        manager = new RouteManager(this);
        id = getIntent().getLongExtra(MainActivity.EXTRA_ROUTE_ID, 0);
        Route route = manager.ruta(session.worker, id);
        if (route == null) { finish(); return; }
        if (route.end != null) { showSummary(); return; }
        silenceSeconds = getSharedPreferences("campo-" + session.worker.id + "-" + session.worker.parkId, MODE_PRIVATE)
                .getInt("silencio", 20);
        buildScreen(route);
    }


    /** --------------------
     * Diseño lógico: route: Route --> buildScreen() -->
     * Descripción: Build Screen. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void buildScreen(Route route) {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(SAGE);
        WebView web = new WebView(this);
        web.setContentDescription(getString(R.string.route_map_description));
        root.addView(web, new FrameLayout.LayoutParams(-1, -1));

        header = column();
        header.setPadding(dp(16), dp(16), dp(16), 0);
        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        heading.setBackgroundResource(R.drawable.s_card_glass);
        heading.setElevation(dp(3));
        heading.setPadding(dp(12), dp(12), dp(14), dp(12));
        heading.addView(backButton(), new LinearLayout.LayoutParams(dp(44), dp(44)));
        LinearLayout info = column();
        LinearLayout.LayoutParams infoSize = new LinearLayout.LayoutParams(0, -2, 1);
        infoSize.setMarginStart(dp(12));
        heading.addView(info, infoSize);
        TextView title = label(getString(R.string.route_live), 19, true);
        title.setSingleLine(true);
        info.addView(title);
        String start = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(route.start));
        TextView started = label("Inicio · " + start, 15, false);
        started.setTextColor(MUTED);
        info.addView(started);
        signal = chip("", 0xff2b6fd6, 0xffe3edfb, R.drawable.ic_s_dot_ring);
        signal.setTextSize(13);
        heading.addView(signal, new LinearLayout.LayoutParams(-2, -2));
        header.addView(heading);

        TextView status = label("", 13, true);
        status.setBackgroundResource(R.drawable.s_card_glass);
        status.setPadding(dp(14), dp(7), dp(14), dp(7));
        LinearLayout.LayoutParams statusSize = new LinearLayout.LayoutParams(-2, -2);
        statusSize.gravity = Gravity.CENTER_HORIZONTAL;
        statusSize.topMargin = dp(6);
        header.addView(status, statusSize);
        root.addView(header, new FrameLayout.LayoutParams(-1, -2, Gravity.TOP));
        map = new RouteMap(this, web, status, true);

        controls = column();
        controls.setBackgroundResource(R.drawable.s_sheet);
        controls.setElevation(dp(6));
        controls.setPadding(dp(18), dp(18), dp(18), dp(18));
        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        recording = label(getString(R.string.route_recording), 16, true);
        recording.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_s_dot_ring, 0, 0, 0);
        recording.setCompoundDrawablePadding(dp(8));
        recording.setCompoundDrawableTintList(ColorStateList.valueOf(0xff3e9b5a));
        top.addView(recording, new LinearLayout.LayoutParams(0, -2, 1));
        TextView keyring = label(route.sensor == null ? getString(R.string.route_gps_only) : "Llavero " + route.sensor, 15, false);
        keyring.setTextColor(route.sensor == null ? MUTED : 0xff2b6fd6);
        keyring.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_s_bluetooth, 0, 0, 0);
        keyring.setCompoundDrawablePadding(dp(6));
        keyring.setCompoundDrawableTintList(ColorStateList.valueOf(route.sensor == null ? MUTED : 0xff2b6fd6));
        keyring.setGravity(Gravity.CENTER_VERTICAL);
        top.addView(keyring);
        controls.addView(top);
        LinearLayout stats = new LinearLayout(this);
        duration = metric(stats, R.string.route_duration, 0);
        count = metric(stats, R.string.route_measurements, 8);
        lastOzone = metric(stats, R.string.route_last_o3, 8);
        addWithTopMargin(controls, stats, 14);
        MaterialButton finishRoute = new MaterialButton(this);
        finishRoute.setAllCaps(false);
        finishRoute.setText(R.string.main_finish);
        finishRoute.setTextSize(18);
        finishRoute.setTypeface(com.example.ozora.senda.ui.FieldScreen.inter(this, true));
        finishRoute.setMinHeight(dp(56));
        finishRoute.setInsetTop(0);
        finishRoute.setInsetBottom(0);
        finishRoute.setCornerRadius(dp(20));
        finishRoute.setIconResource(R.drawable.ic_s_stop);
        finishRoute.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
        finishRoute.setIconPadding(dp(14));
        finishRoute.setBackgroundTintList(ColorStateList.valueOf(0xffd23b2f));
        finishRoute.setOnClickListener(view -> confirmFinish());
        addWithTopMargin(controls, finishRoute, 14);
        FrameLayout.LayoutParams controlsSize = new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM);
        controlsSize.setMargins(dp(12), 0, dp(12), dp(16));
        root.addView(controls, controlsSize);

        setContentView(root);
        protegerBarras(root);
    }


    /** --------------------
     * Diseño lógico: parent: LinearLayout, name: Z, margin: Z --> metric() -->
     * Text <--
     * Descripción: Metric. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private TextView metric(LinearLayout parent, int name, int margin) {
        LinearLayout card = column();
        card.setBackgroundResource(R.drawable.s_stat_tile);
        card.setPadding(dp(12), dp(12), dp(10), dp(12));
        TextView title = label(getString(name), 14, false);
        title.setTextColor(MUTED);
        title.setSingleLine(true);
        card.addView(title);
        TextView value = label("", 21, true);
        value.setMaxLines(1);
        value.setAutoSizeTextTypeUniformWithConfiguration(11, 21, 1, android.util.TypedValue.COMPLEX_UNIT_SP);
        card.addView(value, new LinearLayout.LayoutParams(-1, dp(34)));
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0, -2, 1);
        size.setMarginStart(dp(margin));
        parent.addView(card, size);
        return value;
    }


    /** --------------------
     * Diseño lógico: refresh() -->
     * B <--
     * Descripción: Refresh. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private boolean refresh() {
        if (map == null || isFinishing()) return false;
        if (SessionManager.current() != session) { finish(); return false; }
        Route route = manager.ruta(session.worker, id);
        if (route == null) { finish(); return false; }
        if (route.end != null) { showSummary(); return false; }

        boolean running = RutaService.actual == id;
        boolean receiving = running && RutaService.ultimoAnuncio > 0
                && SystemClock.elapsedRealtime() - RutaService.ultimoAnuncio < silenceSeconds * 1000L;
        int status = !running ? R.string.route_paused : route.sensor == null ? R.string.route_gps_only
                : receiving ? R.string.route_receiving : R.string.route_no_signal;
        if (!getString(status).contentEquals(signal.getText())) {
            signal.setText(status);
            int tone = receiving ? 0xff2b6fd6 : !running ? 0xffb3261e : MUTED;
            signal.setTextColor(tone);
            signal.setCompoundDrawableTintList(ColorStateList.valueOf(tone));
            signal.setBackgroundTintList(ColorStateList.valueOf(receiving ? 0xffe3edfb : !running ? 0xfffbe7e4 : 0xffefece5));
            recording.setText(running ? R.string.route_recording : R.string.route_paused);
            recording.setCompoundDrawableTintList(ColorStateList.valueOf(running ? 0xff3e9b5a : 0xffd23b2f));
        }
        try (android.database.Cursor last = manager.ultima(id)) {
            boolean hay = last.moveToFirst();
            lastOzone.setText(hay ? valueWithUnit(String.valueOf(last.getInt(0)), "ppb") : "—");
            if (hay && route.count != conteoAvisado) {
                // Aviso de calidad en directo: color del último valor y mensaje cuando la calidad cambia.
                int ppb = last.getInt(0);
                com.example.ozora.senda.ui.CalidadAire calidad =
                        com.example.ozora.senda.ui.CalidadAire.de(ppb, com.example.ozora.senda.ui.CalidadAire.umbral(this, session));
                ((android.view.View) lastOzone.getParent()).setBackgroundTintList(ColorStateList.valueOf(calidad.fondo));
                lastOzone.setTextColor(calidad.texto);
                if (calidad != calidadAvisada) {
                    aviso(calidad.corta + " · " + ppb + " ppb. " + calidad.consejo(),
                            calidad == com.example.ozora.senda.ui.CalidadAire.BUENA);
                    if (calidad == com.example.ozora.senda.ui.CalidadAire.MALA) vibrarAviso();
                    calidadAvisada = calidad;
                }
                conteoAvisado = route.count;
            }
        }
        long seconds = Math.max(0, System.currentTimeMillis() - route.start) / 1000;
        duration.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d",
                seconds / 3600, seconds / 60 % 60, seconds % 60));
        count.setText(String.format(Locale.getDefault(), "%d", route.count));

        List<RoutePoint> points = manager.puntos(id);
        JSONArray markers = new JSONArray();
        try (android.database.Cursor readings = manager.lecturasRuta(session.worker, id, -1, 0)) {
            while (readings.moveToNext()) {
                if (readings.isNull(2) || readings.isNull(3)) continue;
                markers.put(new JSONArray().put((Object) readings.getDouble(2)).put((Object) readings.getDouble(3))
                        .put(getString(R.string.route_ozone_value, readings.getInt(0))));
            }
        }
        float density = getResources().getDisplayMetrics().density;
        map.update(points, markers, Math.round(header.getHeight() / density), Math.round(controls.getHeight() / density));
        return true;
    }


    /** --------------------
     * Diseño lógico: confirmFinish() -->
     * Descripción: Confirm Finish. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void confirmFinish() {
        new AlertDialog.Builder(this).setTitle(R.string.route_confirm_finish)
                .setMessage(R.string.route_confirm_message)
                .setPositiveButton(R.string.route_confirm, (dialog, which) -> {
                    if (SessionManager.current() != session) { finish(); return; }
                    RutaService.finalizar(this, manager, id);
                    showSummary();
                })
                .setNegativeButton(R.string.route_keep_running, null).show();
    }


    /** --------------------
     * Diseño lógico: showSummary() -->
     * Descripción: Show Summary. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void showSummary() {
        startActivity(new Intent(this, SummaryActivity.class).putExtra(MainActivity.EXTRA_ROUTE_ID, id));
        finish();
    }


    /** --------------------
     * Diseño lógico: onStart() -->
     * Descripción: On Start. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onStart() {
        super.onStart();
        if (map != null) header.post(tick);
    }


    /** --------------------
     * Diseño lógico: onStop() -->
     * Descripción: On Stop. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onStop() {
        if (header != null) header.removeCallbacks(tick);
        clock.removeCallbacks(tick);
        super.onStop();
    }


    /** --------------------
     * Diseño lógico: onDestroy() -->
     * Descripción: On Destroy. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onDestroy() {
        if (map != null) map.destroy();
        if (manager != null) manager.close();
        super.onDestroy();
    }
}
