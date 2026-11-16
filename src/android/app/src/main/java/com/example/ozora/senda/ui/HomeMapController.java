package com.example.ozora.senda.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.SystemClock;
import android.view.View;
import android.webkit.*;
import android.widget.TextView;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.location.LocationTracker;
import com.example.ozora.senda.routes.RouteManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import org.json.JSONArray;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/** Mapa visible de Inicio. Usa el GPS compartido y lecturas reales de la cuenta actual. */
public final class HomeMapController implements LocationTracker.Oyente {
    private final Activity activity;
    private final View root;
    private final WebView web;
    private final LocationTracker location;
    private final SessionManager session;
    private final ExecutorService reader = Executors.newSingleThreadExecutor();
    private boolean ready, disposed, enabled, week, foreground, mapUnavailable;
    private Location last;
    private JSONArray points = new JSONArray();
    private int min, max;
    private long lastRefresh;

    public HomeMapController(Activity activity, View root, SessionManager session, boolean enabled, boolean week) {
        this.activity = activity;
        this.root = root;
        this.session = session;
        this.enabled = enabled;
        this.week = week;
        web = root.findViewById(R.id.home_map);
        location = new LocationTracker(activity, this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowFileAccess(false);
        web.getSettings().setAllowContentAccess(false);
        web.getSettings().setGeolocationEnabled(false);
        web.getSettings().setUserAgentString("SENDA-Equipo2/1.0 (+https://github.com/FlyxCSS/Biometria-Equipo2)");
        web.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                if (disposed) return;
                ready = true;
                render();
                if (last != null) position(true);
                root.findViewById(R.id.home_bottom_card).post(() -> {
                    if (!disposed) web.evaluateJavascript("setBottom(" +
                            (root.findViewById(R.id.home_bottom_card).getHeight()
                            / activity.getResources().getDisplayMetrics().density + 8) + ")", null);
                });
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame() || "tile.openstreetmap.org".equals(request.getUrl().getHost())) {
                    mapUnavailable = true;
                    android.util.Log.w("SendaMap", "Error de cartografía: " + error.getErrorCode() + " " + error.getDescription());
                    if (foreground) status(activity.getString(R.string.home_map_offline));
                }
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                if ("https".equals(url.getScheme()) && "www.openstreetmap.org".equals(url.getHost())) {
                    try { activity.startActivity(new Intent(Intent.ACTION_VIEW, url)); }
                    catch (android.content.ActivityNotFoundException ignored) {}
                }
                return true;
            }
        });
        try {
            String html = mapHtml(activity, "home.html");
            web.loadDataWithBaseURL("https://senda-map.invalid/", html, "text/html", "utf-8", null);
        } catch (IOException error) { status(activity.getString(R.string.home_map_offline)); }
        root.findViewById(R.id.home_heat_toggle).setOnClickListener(v -> {
            this.enabled = !this.enabled;
            updateControls();
            refresh(true);
        });
        MaterialButtonToggleGroup periods = root.findViewById(R.id.home_period);
        periods.check(week ? R.id.home_week : R.id.home_today);
        periods.addOnButtonCheckedListener((group, id, checked) -> {
            if (checked) { this.week = id == R.id.home_week; refresh(true); }
        });
        updateControls();
        refresh(true);
    }

    static String mapHtml(Activity activity, String file) throws IOException {
        return asset(activity, file).replace("/*LEAFLET_CSS*/", asset(activity, "leaflet.css"))
                .replace("/*LEAFLET_JS*/", asset(activity, "leaflet.js"));
    }

    private static String asset(Activity activity, String file) throws IOException {
        try (InputStream stream = activity.getAssets().open("map/" + file);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = stream.read(buffer)) != -1) output.write(buffer, 0, count);
            return output.toString(StandardCharsets.UTF_8.name());
        }
    }

    public boolean heatEnabled() { return enabled; }
    public boolean weekSelected() { return week; }

    public void start() {
        foreground = true;
        web.onResume();
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            status(activity.getString(R.string.home_location_permission));
            return;
        }
        status(activity.getString(R.string.home_wait_location));
        location.iniciar();
        refresh(true);
    }

    public void stop() {
        foreground = false;
        location.detener();
        last = null;
        if (ready) web.evaluateJavascript("clearPosition()", null);
        web.onPause();
    }

    public void destroy() {
        disposed = true;
        stop();
        reader.shutdownNow();
        web.stopLoading();
        web.destroy();
    }

    public void recenter() {
        if (ready && mapUnavailable) web.evaluateJavascript("retryTiles()", null);
        if (last == null) { start(); return; }
        long age = SystemClock.elapsedRealtimeNanos() - last.getElapsedRealtimeNanos();
        if (age < 0 || age > 30_000_000_000L) { last = null; start(); return; }
        position(true);
    }

    @Override public void alRecibirUbicacion(Location position) {
        if (!foreground || disposed || SessionManager.current() != session) return;
        long age = SystemClock.elapsedRealtimeNanos() - position.getElapsedRealtimeNanos();
        if (age < 0 || age > 30_000_000_000L || !position.hasAccuracy()
                || !Float.isFinite(position.getAccuracy()) || position.getAccuracy() < 0
                || position.getAccuracy() > 100 || !Double.isFinite(position.getLatitude())
                || !Double.isFinite(position.getLongitude()) || Math.abs(position.getLatitude()) > 90
                || Math.abs(position.getLongitude()) > 180) return;
        boolean first = last == null;
        last = new Location(position);
        if (ready) position(first);
        showStatus();
    }

    @Override public void alOcurrirError(String error) { if (!disposed && foreground) status(error); }

    private void position(boolean recenter) {
        if (!ready || last == null) return;
        web.evaluateJavascript("setPosition(" + last.getLatitude() + "," + last.getLongitude() + ","
                + last.getAccuracy() + "," + recenter + ")", null);
    }

    private void updateControls() {
        root.findViewById(R.id.home_legend).setVisibility(enabled ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.home_heat_filters).setVisibility(enabled ? View.VISIBLE : View.GONE);
        MaterialButton button = root.findViewById(R.id.home_heat_toggle);
        button.setChecked(enabled);
        button.setIconResource(enabled ? R.drawable.home_eye_off : R.drawable.home_eye);
        button.setContentDescription(activity.getString(enabled ? R.string.home_heat_hide : R.string.home_heat_show));
        render();
        showStatus();
    }

    public void refresh(boolean force) {
        if (ready && foreground && !disposed) {
            web.evaluateJavascript("window.tileState || null", value -> {
                if (disposed || !foreground || "null".equals(value)) return;
                try {
                    org.json.JSONObject state = new org.json.JSONObject(value);
                    mapUnavailable = state.getInt("loaded") == 0 && state.getInt("failed") > 0;
                    showStatus();
                } catch (org.json.JSONException ignored) {}
            });
        }
        long now = SystemClock.elapsedRealtime();
        if (disposed || (!force && now - lastRefresh < 15000)) return;
        lastRefresh = now;
        boolean requestedWeek = week;
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0); start.set(Calendar.MILLISECOND, 0);
        if (requestedWeek) start.add(Calendar.DAY_OF_MONTH, -6);
        SimpleDateFormat utc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);
        utc.setTimeZone(TimeZone.getTimeZone("UTC"));
        String since = utc.format(start.getTime()), until = utc.format(new Date());
        reader.execute(() -> {
            JSONArray result = new JSONArray();
            int minimum = Integer.MAX_VALUE, maximum = Integer.MIN_VALUE;
            try (RouteManager routes = new RouteManager(activity.getApplicationContext());
                 Cursor cursor = routes.puntosCalor(session.worker, since, until)) {
                while (cursor.moveToNext()) {
                    int value = (int) Math.round(cursor.getDouble(2));
                    result.put(new JSONArray().put(cursor.getDouble(0)).put(cursor.getDouble(1)).put(value));
                    minimum = Math.min(minimum, value); maximum = Math.max(maximum, value);
                }
            } catch (Exception error) {
                activity.runOnUiThread(() -> {
                    if (!disposed) status("No se pudieron cargar las mediciones del mapa.");
                });
                return;
            }
            int low = minimum == Integer.MAX_VALUE ? 0 : minimum;
            int high = maximum == Integer.MIN_VALUE ? 0 : maximum;
            activity.runOnUiThread(() -> {
                if (disposed || requestedWeek != week || SessionManager.current() != session) return;
                points = result; min = low; max = high;
                ((TextView) root.findViewById(R.id.home_min)).setText(points.length() == 0 ? "—" : min + " ppb");
                ((TextView) root.findViewById(R.id.home_max)).setText(points.length() == 0 ? "—" : max + " ppb");
                render(); showStatus();
            });
        });
    }

    private void render() {
        if (!ready || disposed) return;
        web.evaluateJavascript("setReadings(" + points + "," + enabled + "," + min + "," + max + ")", null);
    }

    private void showStatus() {
        if (mapUnavailable) { status(activity.getString(R.string.home_map_offline)); return; }
        if (last == null) return;
        String message = enabled ? activity.getString(points.length() == 0 ? R.string.home_no_heat : R.string.home_heat_note) : "";
        if (enabled && points.length() >= 1500) message += " · Últimas 1500 ubicaciones";
        status(message);
    }

    private void status(String message) {
        TextView status = root.findViewById(R.id.home_map_status);
        status.setText(message);
        status.setVisibility(message.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
