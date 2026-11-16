package com.example.ozora.senda.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.ozora.senda.R;
import com.example.ozora.senda.model.RoutePoint;

import org.json.JSONArray;

import java.io.IOException;
import java.util.List;

public final class RouteMap {
    private final WebView web;
    private final TextView status;
    private boolean ready, disposed, hasPoints, offline;
    private String draw;
    private final boolean active;

    public RouteMap(Activity activity, WebView web, TextView status, boolean active) {
        this.web = web;
        this.status = status;
        this.active = active;
        web.setBackgroundColor(0xffd2dfc9);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowFileAccess(false);
        web.getSettings().setAllowContentAccess(false);
        web.getSettings().setUserAgentString("SENDA-Equipo2/1.0 (+https://github.com/FlyxCSS/Biometria-Equipo2)");
        web.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                if (disposed) return;
                ready = true;
                render();
            }

            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if ("https".equals(request.getUrl().getScheme())
                        && "www.openstreetmap.org".equals(request.getUrl().getHost())) {
                    try { activity.startActivity(new Intent(Intent.ACTION_VIEW, request.getUrl())); }
                    catch (ActivityNotFoundException ignored) {}
                }
                return true;
            }
        });
        try {
            web.loadDataWithBaseURL("https://senda-map.invalid/",
                    HomeMapController.mapHtml(activity, "route.html"), "text/html", "utf-8", null);
        } catch (IOException error) {
            offline = true;
            showStatus();
        }
    }

    public void update(List<RoutePoint> points, JSONArray readings, int top, int bottom) {
        JSONArray segments = new JSONArray();
        JSONArray segment = null;
        long previous = 0;
        for (RoutePoint point : points) {
            if (segment == null || previous != point.segment) {
                segment = new JSONArray();
                segments.put(segment);
                previous = point.segment;
            }
            segment.put(new JSONArray().put((Object) point.latitude).put((Object) point.longitude));
        }
        hasPoints = !points.isEmpty() || readings.length() > 0;
        String next = "draw(" + segments + "," + readings + "," + active + "," + top + "," + bottom + ")";
        if (!next.equals(draw)) {
            draw = next;
            render();
        }
        showStatus();
        checkTiles();
    }

    private void render() {
        if (ready && !disposed && draw != null) web.evaluateJavascript(draw, null);
    }

    public void checkTiles() {
        if (ready && !disposed && hasPoints) {
            web.evaluateJavascript("window.tilesUnavailable", result -> {
                if (disposed) return;
                offline = "true".equals(result);
                showStatus();
            });
        }
    }

    private void showStatus() {
        status.setText(offline ? R.string.route_map_offline : (active ? R.string.route_wait_gps : R.string.route_no_gps));
        status.setVisibility(offline || !hasPoints ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    public void destroy() {
        disposed = true;
        ((ViewGroup) web.getParent()).removeView(web);
        web.destroy();
    }
}
