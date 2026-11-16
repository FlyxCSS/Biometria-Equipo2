package com.example.ozora.senda.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ozora.senda.MainActivity;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.Route;
import com.example.ozora.senda.routes.RouteManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class SummaryActivity extends RouteScreen {
    private static final int PREVIEW = 3;
    private static final int PAGE = 30;
    private final SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SessionManager session;
    private RouteManager manager;
    private Route route;
    private RouteMap map;
    private LinearLayout rows;
    private MaterialButton all, more;
    private TextView average, maximum, minimum;
    private boolean temperature, expanded;
    private int shown;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        session = SessionManager.current();
        if (session == null) { finish(); return; }
        manager = new RouteManager(this);
        long id = getIntent().getLongExtra(MainActivity.EXTRA_ROUTE_ID, 0);
        route = id == 0 ? manager.ultimaRuta(session.worker) : manager.ruta(session.worker, id);
        temperature = state != null && state.getBoolean("temperature");
        expanded = state != null && state.getBoolean("expanded");
        buildScreen();
    }

    private void buildScreen() {
        LinearLayout root = column();
        root.setBackgroundColor(getColor(R.color.senda_surface));
        ScrollView scroll = new ScrollView(this);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout content = column();
        content.setPadding(dp(16), dp(12), dp(16), dp(20));
        scroll.addView(content);

        LinearLayout heading = new LinearLayout(this);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        heading.addView(backButton(), new LinearLayout.LayoutParams(dp(48), dp(48)));
        TextView title = label(getString(R.string.route_info), 25, true);
        title.setPadding(dp(8), 0, 0, 0);
        heading.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        content.addView(heading);

        BottomNavigationView nav = (BottomNavigationView) getLayoutInflater()
                .inflate(R.layout.main_bottom_navigation, root, false);
        root.addView(nav);
        nav.setSelectedItemId(R.id.tab_rutas);
        nav.setOnItemReselectedListener(item -> finish());
        nav.setOnItemSelectedListener(item -> {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    .putExtra(MainActivity.EXTRA_SECTION, item.getItemId()));
            finish();
            return true;
        });
        setContentView(root);
        protegerBarras(root);
        if (route == null) {
            addWithTopMargin(content, label(getString(R.string.route_unavailable), 16, false), 16);
            return;
        }

        addWithTopMargin(content, label(getString(R.string.home_park, session.worker.parkId), 16, false), 8);
        FrameLayout mapCard = new FrameLayout(this);
        card(mapCard);
        LinearLayout.LayoutParams mapSize = new LinearLayout.LayoutParams(-1, dp(310));
        mapSize.topMargin = dp(14);
        content.addView(mapCard, mapSize);
        WebView web = new WebView(this);
        web.setContentDescription(getString(R.string.route_map_description));
        mapCard.addView(web, new FrameLayout.LayoutParams(-1, -1));
        TextView status = label("", 13, true);
        status.setBackgroundResource(R.drawable.home_card);
        status.setPadding(dp(12), dp(6), dp(12), dp(6));
        FrameLayout.LayoutParams statusSize = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        statusSize.topMargin = dp(8);
        mapCard.addView(status, statusSize);
        map = new RouteMap(this, web, status, false);

        MaterialButtonToggleGroup filters = new MaterialButtonToggleGroup(this);
        filters.setSingleSelection(true);
        filters.setSelectionRequired(true);
        MaterialButton ozone = filter(R.string.route_ozone);
        MaterialButton temp = filter(R.string.route_temperature);
        filters.addView(ozone, new LinearLayout.LayoutParams(0, dp(48), 1));
        filters.addView(temp, new LinearLayout.LayoutParams(0, dp(48), 1));
        filters.check(temperature ? temp.getId() : ozone.getId());
        addWithTopMargin(content, filters, 10);

        LinearLayout stats = new LinearLayout(this);
        average = stat(stats, R.string.route_average, 0);
        maximum = stat(stats, R.string.route_max, 8);
        minimum = stat(stats, R.string.route_min, 8);
        addWithTopMargin(content, stats, 12);

        LinearLayout readings = column();
        card(readings);
        readings.setPadding(dp(16), dp(8), dp(16), dp(10));
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(label(getString(R.string.route_points), 17, true), new LinearLayout.LayoutParams(0, -2, 1));
        all = textButton();
        all.setOnClickListener(view -> {
            expanded = !expanded;
            refreshReadings();
        });
        row.addView(all);
        readings.addView(row);
        rows = column();
        readings.addView(rows);
        more = textButton();
        more.setText(R.string.route_show_more);
        more.setOnClickListener(view -> appendRows(PAGE));
        readings.addView(more, new LinearLayout.LayoutParams(-1, -2));
        addWithTopMargin(content, readings, 12);

        filters.addOnButtonCheckedListener((group, checkedId, checked) -> {
            if (!checked) return;
            temperature = checkedId == temp.getId();
            refreshReadings();
            refreshMap();
        });
        refreshReadings();
        refreshMap();
        root.postDelayed(() -> { if (map != null) map.checkTiles(); }, 2500);
    }

    private MaterialButton filter(int title) {
        MaterialButton button = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        button.setId(View.generateViewId());
        button.setText(title);
        button.setAllCaps(false);
        button.setBackgroundTintList(new ColorStateList(
                new int[][]{{android.R.attr.state_checked}, {}}, new int[]{GREEN, 0xffffffff}));
        button.setTextColor(new ColorStateList(
                new int[][]{{android.R.attr.state_checked}, {}}, new int[]{0xffffffff, INK}));
        return button;
    }

    private MaterialButton textButton() {
        MaterialButton button = new MaterialButton(this);
        button.setBackgroundTintList(ColorStateList.valueOf(0x00000000));
        button.setTextColor(INK);
        button.setAllCaps(false);
        return button;
    }

    private TextView stat(LinearLayout parent, int title, int margin) {
        LinearLayout block = column();
        card(block);
        block.setPadding(dp(8), dp(14), dp(8), dp(14));
        TextView name = label(getString(title), 13, false);
        TextView value = label("", 18, true);
        block.addView(name);
        block.addView(value);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0, -2, 1);
        size.setMarginStart(dp(margin));
        parent.addView(block, size);
        return value;
    }

    private void refreshReadings() {
        boolean empty = temperature || route.count == 0;
        average.setText(empty ? "—" : value((int) Math.round(route.mean)));
        maximum.setText(empty ? "—" : value(route.maximum));
        minimum.setText(empty ? "—" : value(route.minimum));
        rows.removeAllViews();
        shown = 0;
        all.setVisibility(!temperature && route.count > PREVIEW ? View.VISIBLE : View.GONE);
        all.setText(expanded ? R.string.route_show_less : R.string.route_show_all);
        more.setVisibility(View.GONE);
        if (empty) {
            addWithTopMargin(rows, label(getString(temperature ? R.string.route_no_temperature
                    : R.string.main_no_readings), 15, false), 8);
        } else {
            appendRows(expanded ? PAGE : PREVIEW);
        }
    }

    private void appendRows(int limit) {
        try (Cursor readings = manager.lecturasRuta(session.worker, route.id, limit, shown)) {
            while (readings.moveToNext()) {
                View divider = new View(this);
                divider.setBackgroundColor(SAGE);
                rows.addView(divider, new LinearLayout.LayoutParams(-1, dp(1)));
                LinearLayout row = new LinearLayout(this);
                row.setPadding(dp(4), dp(10), dp(4), dp(10));
                row.addView(label(readingTime(readings.getString(1)), 16, false), new LinearLayout.LayoutParams(0, -2, 1));
                row.addView(label(value(readings.getInt(0)), 16, true), new LinearLayout.LayoutParams(0, -2, 1));
                rows.addView(row);
                shown++;
            }
        }
        more.setVisibility(expanded && shown < route.count ? View.VISIBLE : View.GONE);
    }

    private void refreshMap() {
        JSONArray markers = new JSONArray();
        if (!temperature) {
            try (Cursor readings = manager.lecturasRuta(session.worker, route.id, -1, 0)) {
                while (readings.moveToNext()) {
                    if (readings.isNull(2) || readings.isNull(3)) continue;
                    markers.put(new JSONArray().put((Object) readings.getDouble(2)).put((Object) readings.getDouble(3))
                            .put(readingTime(readings.getString(1)) + " · " + value(readings.getInt(0))));
                }
            }
        }
        map.update(manager.puntos(route.id), markers, 0, 0);
    }

    private String value(int ppb) { return getString(R.string.route_ozone_value, ppb); }

    private String readingTime(String utc) {
        try { return time.format(new Date(SessionManager.instante(utc))); }
        catch (ParseException error) { return utc; }
    }

    @Override protected void onStart() {
        super.onStart();
        if (SessionManager.current() != session) finish();
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean("temperature", temperature);
        state.putBoolean("expanded", expanded);
    }

    @Override protected void onDestroy() {
        if (map != null) { map.destroy(); map = null; }
        if (manager != null) manager.close();
        super.onDestroy();
    }
}
