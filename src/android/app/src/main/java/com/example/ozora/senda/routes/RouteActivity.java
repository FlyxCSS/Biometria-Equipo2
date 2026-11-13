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
    private TextView signal, duration, count;


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

    private void buildScreen(Route route) {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(SAGE);
        WebView web = new WebView(this);
        web.setContentDescription(getString(R.string.route_map_description));
        root.addView(web, new FrameLayout.LayoutParams(-1, -1));

        header = column();
        header.setPadding(dp(16), dp(12), dp(16), 0);
        LinearLayout heading = new LinearLayout(this);
        heading.addView(backButton(), new LinearLayout.LayoutParams(dp(48), dp(48)));
        LinearLayout info = column();
        card(info);
        info.setPadding(dp(14), dp(12), dp(14), dp(12));
        LinearLayout.LayoutParams infoSize = new LinearLayout.LayoutParams(0, -2, 1);
        infoSize.setMarginStart(dp(8));
        heading.addView(info, infoSize);
        TextView title = label(getString(R.string.route_live), 20, true);
        info.addView(title);
        String start = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(route.start));
        info.addView(label(getString(R.string.route_started, start), 14, false));
        signal = label("", 13, true);
        signal.setPadding(dp(8), dp(3), dp(8), dp(3));
        LinearLayout.LayoutParams signalSize = new LinearLayout.LayoutParams(-2, -2);
        signalSize.topMargin = dp(6);
        info.addView(signal, signalSize);
        header.addView(heading);

        TextView status = label("", 13, true);
        status.setBackgroundResource(R.drawable.home_card);
        status.setPadding(dp(10), dp(5), dp(10), dp(5));
        LinearLayout.LayoutParams statusSize = new LinearLayout.LayoutParams(-2, -2);
        statusSize.gravity = Gravity.CENTER_HORIZONTAL;
        statusSize.topMargin = dp(6);
        header.addView(status, statusSize);
        root.addView(header, new FrameLayout.LayoutParams(-1, -2, Gravity.TOP));
        map = new RouteMap(this, web, status, true);

        controls = column();
        controls.setPadding(dp(16), 0, dp(16), dp(24));
        LinearLayout stats = new LinearLayout(this);
        duration = metric(stats, R.string.route_duration, 0);
        count = metric(stats, R.string.route_measurements, 8);
        controls.addView(stats);
        MaterialButton finishRoute = new MaterialButton(this);
        finishRoute.setAllCaps(false);
        finishRoute.setText(R.string.main_finish);
        finishRoute.setTextSize(17);
        finishRoute.setMinHeight(dp(56));
        finishRoute.setCornerRadius(dp(18));
        finishRoute.setBackgroundTintList(ColorStateList.valueOf(INK));
        finishRoute.setOnClickListener(view -> confirmFinish());
        addWithTopMargin(controls, finishRoute, 8);
        root.addView(controls, new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM));

        setContentView(root);
        protegerBarras(root);
    }

    private TextView metric(LinearLayout parent, int name, int margin) {
        LinearLayout card = column();
        card(card);
        card.setPadding(dp(12), dp(12), dp(12), dp(12));
        card.addView(label(getString(name), 13, true));
        TextView value = label("", 23, true);
        card.addView(value);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0, -2, 1);
        size.setMarginStart(dp(margin));
        parent.addView(card, size);
        return value;
    }

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
            signal.setBackgroundResource(R.drawable.home_card);
            signal.setBackgroundTintList(ColorStateList.valueOf(receiving ? SAGE : 0xfff4ebd7));
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

    private void showSummary() {
        startActivity(new Intent(this, SummaryActivity.class).putExtra(MainActivity.EXTRA_ROUTE_ID, id));
        finish();
    }

    @Override protected void onStart() {
        super.onStart();
        if (map != null) header.post(tick);
    }

    @Override protected void onStop() {
        if (header != null) header.removeCallbacks(tick);
        clock.removeCallbacks(tick);
        super.onStop();
    }

    @Override protected void onDestroy() {
        if (map != null) map.destroy();
        if (manager != null) manager.close();
        super.onDestroy();
    }
}
