/*
 * Nombre: SummaryActivity.java
 * Descripción: Presenta mapa, estadísticas y lecturas del recorrido.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Presenta mapa, estadísticas y lecturas del recorrido.
 */
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


    /** --------------------
     * Diseño lógico: state: EstadoVista --> onCreate() -->
     * Descripción: On Create. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
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


    /** --------------------
     * Diseño lógico: buildScreen() -->
     * Descripción: Build Screen. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    private void buildScreen() {
        LinearLayout root = column();
        root.setBackgroundColor(getColor(R.color.senda_surface));
        ScrollView scroll = new ScrollView(this);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout content = column();
        content.setPadding(dp(16), dp(12), dp(16), dp(20));
        scroll.addView(content);

        LinearLayout.LayoutParams backSize = new LinearLayout.LayoutParams(dp(48), dp(48));
        backSize.topMargin = dp(28);
        content.addView(backButton(), backSize);
        TextView title = label(route == null ? getString(R.string.route_info) : "Ruta " + route.id, 34, true);
        title.setLetterSpacing(-0.02f);
        title.setPadding(0, dp(10), 0, 0);
        content.addView(title);

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

        TextView subtitle = label(getString(R.string.home_park, session.worker.parkId) + " · "
                + new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date(route.start)), 16, false);
        subtitle.setTextColor(MUTED);
        addWithTopMargin(content, subtitle, 2);
        FrameLayout mapCard = new FrameLayout(this);
        mapCard.setBackgroundResource(R.drawable.s_card);
        mapCard.setClipToOutline(true);
        LinearLayout.LayoutParams mapSize = new LinearLayout.LayoutParams(-1, dp(196));
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
        LinearLayout chips = new LinearLayout(this);
        long seconds = Math.max(0, (route.end == null ? System.currentTimeMillis() : route.end) - route.start) / 1000;
        chips.addView(chip(String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds / 60 % 60, seconds % 60),
                INK, 0xf2f9faf6, 0));
        LinearLayout.LayoutParams second = new LinearLayout.LayoutParams(-2, -2);
        second.setMarginStart(dp(8));
        chips.addView(chip(String.format(Locale.getDefault(), route.meters >= 10000 ? "%.0f km" : "%.1f km", route.meters / 1000),
                INK, 0xf2f9faf6, 0), second);
        FrameLayout.LayoutParams chipsSize = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.START);
        chipsSize.setMargins(dp(10), 0, 0, dp(10));
        mapCard.addView(chips, chipsSize);

        MaterialButtonToggleGroup filters = new MaterialButtonToggleGroup(this);
        filters.setSingleSelection(true);
        filters.setSelectionRequired(true);
        filters.setBackgroundResource(R.drawable.s_segment_track);
        filters.setPadding(dp(4), dp(4), dp(4), dp(4));
        filters.setInnerCornerSize(new com.google.android.material.shape.AbsoluteCornerSize(dp(22)));
        MaterialButton ozone = filter(R.string.route_ozone);
        MaterialButton temp = filter(R.string.route_temperature);
        filters.addView(ozone, new LinearLayout.LayoutParams(0, dp(44), 1));
        filters.addView(temp, new LinearLayout.LayoutParams(0, dp(44), 1));
        filters.check(temperature ? temp.getId() : ozone.getId());
        addWithTopMargin(content, filters, 12);

        LinearLayout stats = new LinearLayout(this);
        average = stat(stats, R.string.route_average, 0);
        maximum = stat(stats, R.string.route_max, 8);
        minimum = stat(stats, R.string.route_min, 8);
        addWithTopMargin(content, stats, 12);

        LinearLayout readings = column();
        card(readings);
        readings.setPadding(dp(16), dp(6), dp(16), dp(6));
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(dp(52));
        row.addView(label(getString(R.string.route_points), 18, true), new LinearLayout.LayoutParams(0, -2, 1));
        all = textButton();
        all.setIconResource(R.drawable.ic_s_chevron);
        all.setIconGravity(MaterialButton.ICON_GRAVITY_END);
        all.setIconTint(ColorStateList.valueOf(INK));
        all.setIconSize(dp(18));
        all.setTextSize(16);
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


    /** --------------------
     * Diseño lógico: title: Z --> filter() -->
     * MaterialButton <--
     * Descripción: Filter. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    private MaterialButton filter(int title) {
        MaterialButton button = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        button.setId(View.generateViewId());
        button.setText(title);
        button.setAllCaps(false);
        button.setElevation(0); button.setStateListAnimator(null);
        button.setBackgroundTintList(new ColorStateList(
                new int[][]{{android.R.attr.state_checked}, {}}, new int[]{0xffffffff, 0x00ffffff}));
        button.setTextColor(new ColorStateList(
                new int[][]{{android.R.attr.state_checked}, {}}, new int[]{INK, MUTED}));
        button.setStrokeWidth(0);
        button.setCornerRadius(dp(22));
        button.setTextSize(16);
        button.setInsetTop(0);
        button.setInsetBottom(0);
        return button;
    }


    /** --------------------
     * Diseño lógico: textButton() -->
     * MaterialButton <--
     * Descripción: Text Button. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    private MaterialButton textButton() {
        MaterialButton button = new MaterialButton(this);
        button.setBackgroundTintList(ColorStateList.valueOf(0x00000000));
        button.setTextColor(INK);
        button.setAllCaps(false);
        return button;
    }


    /** --------------------
     * Diseño lógico: parent: LinearLayout, title: Z, margin: Z --> stat() -->
     * Text <--
     * Descripción: Stat. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    private TextView stat(LinearLayout parent, int title, int margin) {
        LinearLayout block = column();
        card(block);
        block.setPadding(dp(14), dp(14), dp(10), dp(14));
        TextView name = label(getString(title), 15, false);
        name.setTextColor(MUTED);
        TextView value = label("", 26, true);
        value.setSingleLine(true);
        block.addView(name);
        block.addView(value);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0, -2, 1);
        size.setMarginStart(dp(margin));
        parent.addView(block, size);
        return value;
    }

    private int temperatureCount;

    /** --------------------
     * Diseño lógico: limit: Z, offset: Z --> readings() -->
     * Cursor <--
     * Descripción: Readings. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    private Cursor readings(int limit,int offset) {
        return temperature ? manager.temperaturasRuta(session.worker,route.id,limit,offset)
                : manager.lecturasRuta(session.worker,route.id,limit,offset);
    }

    /** --------------------
     * Diseño lógico: refreshReadings() -->
     * Descripción: Refresh Readings. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    private void refreshReadings() {
        if (temperature) {
            double total=0; int min=Integer.MAX_VALUE,max=Integer.MIN_VALUE; temperatureCount=0;
            try(Cursor c=readings(-1,0)) {
                while(c.moveToNext()) { int v=c.getInt(0); total+=v; min=Math.min(min,v); max=Math.max(max,v); temperatureCount++; }
            }
            average.setText(temperatureCount==0 ? "—" : medida((int)Math.round(total/temperatureCount)));
            maximum.setText(temperatureCount==0 ? "—" : medida(max));
            minimum.setText(temperatureCount==0 ? "—" : medida(min));
            rows.removeAllViews(); shown=0;
            all.setVisibility(temperatureCount>PREVIEW ? View.VISIBLE : View.GONE);
            all.setText(expanded ? R.string.route_show_less : R.string.route_show_all);
            more.setVisibility(View.GONE);
            if(temperatureCount==0) addWithTopMargin(rows,label(getString(R.string.route_no_temperature),15,false),8);
            else appendRows(expanded ? PAGE : PREVIEW);
            return;
        }
        boolean empty = temperature || route.count == 0;
        average.setText(empty ? "—" : valueWithUnit(String.valueOf((int) Math.round(route.mean)), "ppb"));
        maximum.setText(empty ? "—" : valueWithUnit(String.valueOf(route.maximum), "ppb"));
        minimum.setText(empty ? "—" : valueWithUnit(String.valueOf(route.minimum), "ppb"));
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


    /** --------------------
     * Diseño lógico: limit: Z --> appendRows() -->
     * Descripción: Append Rows. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    private void appendRows(int limit) {
        try (Cursor readings = readings(limit, shown)) {
            while (readings.moveToNext()) {
                View divider = new View(this);
                divider.setBackgroundColor(0xffece9e2);
                rows.addView(divider, new LinearLayout.LayoutParams(-1, dp(1)));
                LinearLayout row = new LinearLayout(this);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(0, dp(14), 0, dp(14));
                int ppb = readings.getInt(0);
                int umbral = getSharedPreferences("campo-" + session.worker.id + "-" + session.worker.parkId, MODE_PRIVATE).getInt("umbral", 60);
                View dot = new View(this);
                dot.setBackgroundResource(R.drawable.s_circle_ink);
                // Ozono: color según el umbral. Temperatura (centésimas de °C): frío, templado o calor.
                dot.setBackgroundTintList(ColorStateList.valueOf(temperature
                        ? (ppb < 1500 ? 0xff5b8fd6 : ppb <= 2800 ? 0xff6ba57a : 0xffe8873a)
                        : ppb >= umbral * 1.25f ? 0xffd2493a : ppb >= umbral ? 0xffe2b54a : 0xff6ba57a));
                row.addView(dot, new LinearLayout.LayoutParams(dp(10), dp(10)));
                TextView time = label(readingTime(readings.getString(1)), 18, false);
                LinearLayout.LayoutParams timeSize = new LinearLayout.LayoutParams(0, -2, 1);
                timeSize.setMarginStart(dp(18));
                row.addView(time, timeSize);
                row.addView(label(medida(ppb), 18, true));
                rows.addView(row);
                shown++;
            }
        }
        more.setVisibility(expanded && shown < (temperature ? temperatureCount : route.count) ? View.VISIBLE : View.GONE);
    }


    /** --------------------
     * Diseño lógico: refreshMap() -->
     * Descripción: Refresh Map. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    private void refreshMap() {
        JSONArray markers = new JSONArray();
        {
            try (Cursor readings = readings(-1, 0)) {
                while (readings.moveToNext()) {
                    if (readings.isNull(2) || readings.isNull(3)) continue;
                    markers.put(new JSONArray().put((Object) readings.getDouble(2)).put((Object) readings.getDouble(3))
                            .put(readingTime(readings.getString(1)) + " · " + value(readings.getInt(0))));
                }
            }
        }
        map.update(manager.puntos(route.id), markers, 0, 0);
    }


    /** --------------------
     * Diseño lógico: ppb: Z --> value() -->
     * Text <--
     * Descripción: Value. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    /** Valor grande con su unidad pequeña: "72 ppb" para ozono o "21,5 °C" para temperatura. */
    private CharSequence medida(int raw) {
        return temperature ? valueWithUnit(String.format(Locale.getDefault(), "%.1f", raw / 100.0), "°C")
                : valueWithUnit(String.valueOf(raw), "ppb");
    }

    private String value(int ppb) { return temperature ? String.format(Locale.getDefault(), "%.2f °C", ppb / 100.0) : getString(R.string.route_ozone_value, ppb); }


    /** --------------------
     * Diseño lógico: utc: Text --> readingTime() -->
     * Text <--
     * Descripción: Reading Time. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    private String readingTime(String utc) {
        try { return time.format(new Date(SessionManager.instante(utc))); }
        catch (ParseException error) { return utc; }
    }


    /** --------------------
     * Diseño lógico: onStart() -->
     * Descripción: On Start. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    @Override protected void onStart() {
        super.onStart();
        if (SessionManager.current() != session) finish();
    }


    /** --------------------
     * Diseño lógico: state: EstadoVista --> onSaveInstanceState() -->
     * Descripción: On Save Instance State. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean("temperature", temperature);
        state.putBoolean("expanded", expanded);
    }


    /** --------------------
     * Diseño lógico: onDestroy() -->
     * Descripción: On Destroy. Presenta mapa, estadísticas y lecturas del recorrido
     * -------------------- */
    @Override protected void onDestroy() {
        if (map != null) { map.destroy(); map = null; }
        if (manager != null) manager.close();
        super.onDestroy();
    }
}
