/*
 * Nombre: MainActivity.java
 * Descripción: Coordina cinco secciones, permisos y acciones del trabajador.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Coordina cinco secciones, permisos y acciones del trabajador.
 */
package com.example.ozora.senda;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.example.ozora.senda.auth.BiometricHelper;
import com.example.ozora.senda.auth.Login;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.Route;
import com.example.ozora.senda.network.LogicaFake;
import com.example.ozora.senda.network.MeasurementSync;
import com.example.ozora.senda.qr.QrScannerActivity;
import com.example.ozora.senda.routes.RouteActivity;
import com.example.ozora.senda.routes.RouteManager;
import com.example.ozora.senda.routes.RoutePeriod;
import com.example.ozora.senda.ui.RouteCardView;
import com.example.ozora.senda.ui.KeyringScreen;
import com.example.ozora.senda.ui.KeyringStatus;
import com.example.ozora.senda.ui.AccountActivity;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.example.ozora.senda.routes.RutaService;
import com.example.ozora.senda.statistics.EnvironmentalAssessment;
import com.example.ozora.senda.statistics.StatisticsCalculator;
import com.example.ozora.senda.ui.FieldScreen;
import com.example.ozora.senda.ui.HomeMapController;
import com.example.ozora.senda.ui.SummaryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/** Mantiene la sesión y las acciones de campo al cambiar de sección. */
public final class      MainActivity extends FieldScreen {
    public static final String EXTRA_SHOW_ROUTES = "mostrar_rutas";
    public static final String EXTRA_SECTION = "section";
    public static final String EXTRA_ROUTE_ID = "ruta";

    private static final int PAGE_SIZE = 20;
    private static final int REQUEST_ROUTE_PERMISSIONS = 19;
    private static final int REQUEST_SENSOR = 20;
    private static final int REQUEST_MAP_LOCATION = 21;
    private static final int REQUEST_ROUTE_NOTIFICATION = 23;
    private static final int REQUEST_SETTINGS_LOCATION = 24;
    private boolean notificationPermissionAsked;

    private final Handler reloj = new Handler(Looper.getMainLooper());
    private final DateFormat fecha = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private SessionManager session;
    private RouteManager rutas;
    private BiometricHelper bio;
    private ScrollView scroll;
    private FrameLayout section;
    private BottomNavigationView navigation;
    private OnBackPressedCallback backToHome;
    private int tab, routePage, historyPage;
    private RoutePeriod routePeriod=RoutePeriod.TODAY;
    private final java.util.Map<Long,RouteCardView> routeCards=new java.util.HashMap<>();
    private long routeRangeStart,routeRangeEnd,lastPreviewUpdate;
    private boolean activo;
    private long id;
    private String sensorRuta;
    private long ultimoEnvio;
    private HomeMapController homeMap;
    private com.example.ozora.senda.ui.HistoryScreen history;
    private boolean heatEnabled, heatWeek, mapPermissionAsked;
    private boolean operacionLlavero;
    private static final java.util.concurrent.ExecutorService REMOTO = java.util.concurrent.Executors.newSingleThreadExecutor();

    private final Runnable tick = new Runnable() {

        /** --------------------
         * Diseño lógico: run() -->
         * Descripción: Run. Coordina cinco secciones, permisos y acciones del trabajador
         * -------------------- */
        @Override public void run() {
            if (SessionManager.current() != session) {
                salir(false);
                return;
            }
            if (SystemClock.elapsedRealtime() - ultimoEnvio >= 15000) {
                ultimoEnvio = SystemClock.elapsedRealtime();
                MeasurementSync.sincronizar(MainActivity.this);
            }
            actualizar();
            reloj.postDelayed(this, 1000);
        }
    };


    /** --------------------
     * Diseño lógico: state: EstadoVista --> onCreate() -->
     * Descripción: On Create. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        session = SessionManager.current();
        if (session == null) {
            acceso("Vuelve a identificarte.");
            return;
        }
        rutas = new RouteManager(this);
        bio = new BiometricHelper(this);
        setContentView(R.layout.activity_main);
        protegerBarras(findViewById(R.id.main_root));
        scroll = findViewById(R.id.main_scroll);
        section = findViewById(R.id.section_host);
        navigation = findViewById(R.id.bottom_navigation);

        backToHome = new OnBackPressedCallback(false) {

            /** --------------------
             * Diseño lógico: handleOnBackPressed() -->
             * Descripción: Handle On Back Pressed. Coordina cinco secciones, permisos y acciones del trabajador
             * -------------------- */
            @Override public void handleOnBackPressed() {
                navigation.setSelectedItemId(R.id.tab_inicio);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backToHome);
        navigation.setOnItemReselectedListener(item -> { });
        navigation.setOnItemSelectedListener(item -> {
            mostrar(item.getItemId());
            return true;
        });
        int initial = getIntent().getIntExtra(EXTRA_SECTION,
                getIntent().getBooleanExtra(EXTRA_SHOW_ROUTES, false) ? R.id.tab_rutas : R.id.tab_inicio);
        if (state != null) {
            initial = state.getInt("section", initial);
            routePage = state.getInt("route_page");
            routePeriod=RoutePeriod.values()[Math.max(0,Math.min(3,state.getInt("route_period")))];
            historyPage = state.getInt("history_page");
        }
        if (state != null) {
            heatEnabled = state.getBoolean("heat_enabled", false);
            heatWeek = state.getBoolean("heat_week");
            mapPermissionAsked = state.getBoolean("map_permission_asked");
            notificationPermissionAsked = state.getBoolean("notification_permission_asked");
        }
        navigation.setSelectedItemId(initial);
        if (tab == 0) mostrar(initial);
        if (state == null && getIntent().getBooleanExtra(EXTRA_SHOW_ROUTES, false) && activo)
            abrirRuta(RouteActivity.class, id);
    }


    /** --------------------
     * Diseño lógico: intent: Navegacion --> onNewIntent() -->
     * Descripción: On New Intent. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (navigation == null) return;
        if (intent.hasExtra(EXTRA_SECTION)) {
            navigation.setSelectedItemId(intent.getIntExtra(EXTRA_SECTION, R.id.tab_inicio));
        } else if (intent.getBooleanExtra(EXTRA_SHOW_ROUTES, false)) {
            navigation.setSelectedItemId(R.id.tab_rutas);
            actualizar();
            if (activo) abrirRuta(RouteActivity.class, id);
        }
    }


    /** --------------------
     * Diseño lógico: state: EstadoVista --> onSaveInstanceState() -->
     * Descripción: On Save Instance State. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean("heat_enabled", homeMap != null ? homeMap.heatEnabled() : heatEnabled);
        state.putBoolean("heat_week", homeMap != null ? homeMap.weekSelected() : heatWeek);
        state.putBoolean("map_permission_asked", mapPermissionAsked);
        state.putBoolean("notification_permission_asked", notificationPermissionAsked);
        state.putInt("section", tab);
        state.putInt("route_page", routePage);
        state.putInt("route_period",routePeriod.ordinal());
        state.putInt("history_page", historyPage);
    }


    /** --------------------
     * Diseño lógico: onStart() -->
     * Descripción: On Start. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    @Override protected void onStart() {
        super.onStart();
        if (session != null) {
            if (SessionManager.current() != session) {
                salir(false);
                return;
            }
            if (homeMap != null) homeMap.start();
            actualizar();
            actualizarRemoto();
            if (tab == R.id.tab_rutas) cargarPagina(false);
            reloj.post(tick);
        }
    }


    /** --------------------
     * Diseño lógico: onStop() -->
     * Descripción: On Stop. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    @Override protected void onStop() {
        reloj.removeCallbacks(tick);
        if (homeMap != null) homeMap.stop();
        super.onStop();
    }


    /** --------------------
     * Diseño lógico: onDestroy() -->
     * Descripción: On Destroy. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    @Override protected void onDestroy() {
        if (bio != null) bio.cancelar();
        if (homeMap != null) homeMap.destroy();
        if (history != null) history.close();
        if (rutas != null) rutas.close();
        super.onDestroy();
    }


    /** --------------------
     * Diseño lógico: destino: Z --> mostrar() -->
     * Descripción: Mostrar. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void mostrar(int destino) {
        if (history != null) { history.close(); history = null; }
        int layout = R.layout.section_inicio;
        if (destino == R.id.tab_rutas) layout = R.layout.section_rutas;
        else if (destino == R.id.tab_historial) layout = R.layout.section_historial;
        else if (destino == R.id.tab_sensor) layout = R.layout.section_sensor;
        else if (destino == R.id.tab_mas) layout = R.layout.section_mas;

        if (homeMap != null) {
            heatEnabled = homeMap.heatEnabled();
            heatWeek = homeMap.weekSelected();
            homeMap.destroy();
            homeMap = null;
        }
        FrameLayout previous = section;
        previous.removeAllViews();
        scroll.setVisibility(destino == R.id.tab_inicio ? View.GONE : View.VISIBLE);
        FrameLayout homeHost = findViewById(R.id.home_host);
        if (destino != R.id.tab_inicio) homeHost.removeAllViews();
        homeHost.setVisibility(destino == R.id.tab_inicio ? View.VISIBLE : View.GONE);
        section = findViewById(destino == R.id.tab_inicio ? R.id.home_host : R.id.section_host);
        routeCards.clear();
        findViewById(R.id.routes_primary_action).setVisibility(destino==R.id.tab_rutas?View.VISIBLE:View.GONE);
        tab = destino;
        backToHome.setEnabled(destino != R.id.tab_inicio);
        section.removeAllViews();
        getLayoutInflater().inflate(layout, section, true);
        // En Rutas, título y filtros quedan fijos arriba; solo se desplaza la lista.
        FrameLayout fixed = findViewById(R.id.fixed_header);
        fixed.removeAllViews();
        View routesHeader = section.findViewById(R.id.routes_header);
        if (routesHeader != null) {
            ((android.view.ViewGroup) routesHeader.getParent()).removeView(routesHeader);
            fixed.addView(routesHeader);
        }
        fixed.setVisibility(routesHeader != null ? View.VISIBLE : View.GONE);
        findViewById(R.id.main_content).setPadding(dp(16), routesHeader != null ? 0 : dp(16), dp(16), dp(8));
        scroll.scrollTo(0, 0);
        mensaje("");

        if (destino == R.id.tab_inicio) {
            homeMap = new HomeMapController(this, section, session, heatEnabled, heatWeek);
            homeMap.start();
            accion(R.id.home_recenter, this::centrarMapa);
            accion(R.id.home_avatar_button, () -> startActivity(new Intent(this, AccountActivity.class)));
            ajustarPanelInicio();
            navigation.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> { if (t != ot) ajustarPanelInicio(); });
            if (!permiso(Manifest.permission.ACCESS_FINE_LOCATION) && !mapPermissionAsked) centrarMapa();
            accion(R.id.button_start_route, () -> {
                if (activo) abrirRuta(RouteActivity.class, id);
                else preparar();
            });
        } else if (destino == R.id.tab_rutas) {
            findViewById(R.id.routes_primary_action).setOnClickListener(v -> { if (activo) confirmarFin(); else preparar(); });
            int[] periodIds={R.id.routes_today,R.id.routes_yesterday,R.id.routes_week,R.id.routes_seven};
            MaterialButtonToggleGroup periods=findViewById(R.id.routes_period);
            periods.check(periodIds[routePeriod.ordinal()]);
            periods.addOnButtonCheckedListener((group,buttonId,checked) -> {
                if(!checked) return;
                for(int index=0;index<periodIds.length;index++) {
                    if(periodIds[index]==buttonId) {
                        routePeriod=RoutePeriod.values()[index];
                        routePage=0;
                        cargarPagina(false);
                        break;
                    }
                }
            });
            accion(R.id.button_resume_route, this::preparar);
            accion(R.id.button_route_map, () -> abrirRuta(RouteActivity.class, id));
            accion(R.id.button_route_summary, () -> abrirRuta(SummaryActivity.class, id));
        } else if (destino == R.id.tab_historial) {
            history = new com.example.ozora.senda.ui.HistoryScreen(this, section, session);
        } else if (destino == R.id.tab_sensor) {
            accion(R.id.button_select_sensor, this::gestionarLlavero);
            accion(R.id.button_test_ble, () -> startActivity(new Intent(this,
                    com.example.ozora.senda.bluetooth.BleTestActivity.class)));
        } else if (destino == R.id.tab_mas) {
            accion(R.id.button_profile, () -> startActivity(new Intent(this, AccountActivity.class)));
            accion(R.id.account_card, () -> startActivity(new Intent(this, AccountActivity.class)));
            accion(R.id.button_security, () -> {
                if (activo) mensaje("Finaliza el recorrido antes de cambiar la contraseña.");
                else startActivity(new Intent(this, AccountActivity.class).putExtra("security", true));
            });
            accion(R.id.button_biometric, this::configurarBiometria);
            accion(R.id.button_notifications, this::abrirNotificaciones);
            accion(R.id.button_help, this::mostrarAyuda);
            accion(R.id.button_settings, this::configurar);
            accion(R.id.button_permissions, this::cambiarUbicacion);
            accion(R.id.button_retry_sync, () -> {
                if (MeasurementSync.reintentar(this)) aviso("Reintentando los envíos pendientes…", true);
                else aviso("Ya hay un envío en curso.", false);
                actualizar();
            });
            accion(R.id.button_logout, this::confirmarSalida);
        }
        if (destino == R.id.tab_rutas) {
            accion(R.id.button_previous, () -> cambiarPagina(-1));
            accion(R.id.button_next, () -> cambiarPagina(1));
            cargarPagina(false);
        }
        actualizar();
    }

    /** El panel de medidas queda siempre por encima de la barra flotante, mida lo que mida. */

    /** --------------------
     * Diseño lógico: ajustarPanelInicio() -->
     * Descripción: Ajustar Panel Inicio. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void ajustarPanelInicio() {
        navigation.post(() -> {
            if (tab != R.id.tab_inicio) return;
            View card = findViewById(R.id.home_bottom_card);
            View host = findViewById(R.id.home_host);
            if (card == null || navigation.getTop() == 0) return;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) card.getLayoutParams();
            int margin = host.getHeight() - navigation.getTop() + dp(12);
            if (params.bottomMargin != margin) { params.bottomMargin = margin; card.setLayoutParams(params); }
        });
    }


    /** --------------------
     * Diseño lógico: viewId: Z, action: Runnable --> accion() -->
     * Descripción: Accion. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void accion(int viewId, Runnable action) {
        section.findViewById(viewId).setOnClickListener(v -> action.run());
    }


    /** --------------------
     * Diseño lógico: viewId: Z, visible: B --> visible() -->
     * Descripción: Visible. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void visible(int viewId, boolean visible) {
        section.findViewById(viewId).setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    /** --------------------
     * Diseño lógico: viewId: Z, text: Text --> textoEn() -->
     * Descripción: Texto En. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void textoEn(int viewId, CharSequence text) {
        TextView view = section.findViewById(viewId);
        if (!android.text.TextUtils.equals(view.getText(), text)) view.setText(text);
    }


    /** --------------------
     * Diseño lógico: texto: Text --> mensaje() -->
     * Descripción: Mensaje. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    @Override protected void mensaje(String texto) {
        TextView feedback = findViewById(R.id.main_feedback);
        if (feedback == null) return;
        feedback.setText(texto);
        feedback.setVisibility(texto == null || texto.isEmpty() ? View.GONE : View.VISIBLE);

    }


    /** --------------------
     * Diseño lógico: actualizar() -->
     * Descripción: Actualizar. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void actualizar() {
        Route route = rutas.ultimaRuta(session.worker);
        long anterior = id;
        boolean antesActivo = activo;
        id = route == null ? 0 : route.id;
        activo = route != null && route.end == null;
        sensorRuta = activo ? route.sensor : elegido();

        if (tab == R.id.tab_inicio || tab == R.id.tab_mas) {
            textoEn(R.id.text_account, tab == R.id.tab_inicio
                    ? getString(R.string.home_hello, ajustes().getString("profile_name", session.worker.name).split(" ")[0])
                    : ajustes().getString("profile_name", session.worker.name));
        }

        if (tab == R.id.tab_inicio) {
            textoEn(R.id.home_park, getString(R.string.home_park, session.worker.parkId));
            boolean receiving = activo && RutaService.actual == id && RutaService.ultimoAnuncio > 0
                    && SystemClock.elapsedRealtime() - RutaService.ultimoAnuncio < ajustes().getInt("silencio", 20) * 1000L;
            textoEn(R.id.home_avatar, iniciales(ajustes().getString("profile_name", session.worker.name), 1));
            android.widget.ImageView homePhoto = section.findViewById(R.id.home_avatar_photo);
            String foto = ajustes().getString("profile_photo", null);
            if (!android.text.TextUtils.equals((String) homePhoto.getTag(), foto)) {
                AccountActivity.mostrarFoto(homePhoto, foto);
                homePhoto.setTag(foto);
            }
            section.findViewById(R.id.home_avatar).setVisibility(foto != null && !foto.isEmpty() ? View.GONE : View.VISIBLE);
            textoEn(R.id.text_device, receiving ? "Conectado" : "Sin conexión");
            TextView connection = section.findViewById(R.id.text_device);
            int tone = getColor(receiving ? R.color.status_connected : R.color.status_neutral);
            connection.setTextColor(tone);
            connection.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_s_dot_ring, 0, 0, 0);
            connection.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(tone));
            connection.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getColor(receiving ? R.color.status_connected_surface : R.color.status_neutral_surface)));
            connection.setContentDescription(receiving ? "Llavero conectado por Bluetooth" : "Sin conexión Bluetooth con el llavero");
            if (homeMap != null) homeMap.refresh(false);
        }
        if (tab == R.id.tab_inicio) {
            textoEn(R.id.text_location, estadoUbicacion());
            textoEn(R.id.text_route_summary, activo ? StatisticsCalculator.resumen(route, System.currentTimeMillis())
                    : getString(R.string.main_no_route));
            textoEn(R.id.button_start_route, getString(activo ? R.string.main_open_active : R.string.main_start));
            mostrarUltima(null, true);
        } else if (tab == R.id.tab_rutas) {
            textoEn(R.id.text_route_summary, estadoSensor());
            MaterialButton primary = findViewById(R.id.routes_primary_action);
            primary.setText(activo ? R.string.main_finish : R.string.main_start);
            primary.setIconResource(activo ? R.drawable.ic_s_stop : R.drawable.ic_s_play);
            primary.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getColor(activo ? R.color.status_error_strong : R.color.senda_ink)));
            visible(R.id.routes_active_controls,activo);
            RouteCardView currentCard=routeCards.get(id);
            if(currentCard!=null && activo) {
                currentCard.update(route,System.currentTimeMillis());
                if(SystemClock.elapsedRealtime()-lastPreviewUpdate>=10000) {
                    currentCard.setPoints(rutas.vistaPrevia(session.worker,id));
                    lastPreviewUpdate=SystemClock.elapsedRealtime();
                }
            }
            visible(R.id.button_resume_route, activo && RutaService.actual != id);
            visible(R.id.button_route_summary, activo);
            visible(R.id.text_last_o3, activo);
            if (activo) mostrarUltima(sensorRuta, false);
            long[] range=routePeriod.bounds(System.currentTimeMillis(),java.util.TimeZone.getDefault());
            if(range[0]!=routeRangeStart || range[1]!=routeRangeEnd) {
                routePage=0;
                cargarPagina(false);
            } else if(anterior!=id || antesActivo!=activo) cargarPagina(false);
        } else if (tab == R.id.tab_sensor) {
            boolean receiving = KeyringStatus.receiving(activo, id, RutaService.actual,
                    RutaService.ultimoAnuncio, SystemClock.elapsedRealtime(), ajustes().getInt("silencio", 20));
            new KeyringScreen(section).refresh(session, rutas, ajustes(), sensorRuta, activo, receiving, estadoSensor());
        } else if (tab == R.id.tab_mas) {
            String permisos = estadoUbicacion();
            if (Build.VERSION.SDK_INT >= 31) {
                boolean ble = permiso(Manifest.permission.BLUETOOTH_SCAN) && permiso(Manifest.permission.BLUETOOTH_CONNECT);
                permisos += "\n" + getString(ble ? R.string.main_nearby_ready : R.string.main_nearby_missing);
            }
            textoEn(R.id.text_permissions, permisos);
            textoEn(R.id.account_email, ajustes().getString("account_email", getString(R.string.home_park, session.worker.parkId)));
            textoEn(R.id.biometric_state, bio.activo() ? "Activada" : "Desactivada");
            boolean notifications = NotificationManagerCompat.from(this).areNotificationsEnabled();
            boolean location = permiso(Manifest.permission.ACCESS_FINE_LOCATION);
            textoEn(R.id.notifications_state, notifications ? "Activadas" : "Desactivadas");
            textoEn(R.id.location_state, location ? "Ubicación precisa permitida" : "Pendiente de permitir");
            section.findViewById(R.id.button_notifications).setContentDescription(getString(notifications
                    ? R.string.account_enabled : R.string.account_disabled, getString(R.string.account_notifications)));
            section.findViewById(R.id.button_permissions).setContentDescription(getString(location
                    ? R.string.account_location_ready : R.string.account_location_missing));
            android.widget.ImageView avatar = section.findViewById(R.id.account_avatar);
            String photo = ajustes().getString("profile_photo", null);
            if (!android.text.TextUtils.equals((String)avatar.getTag(), photo)) {
                avatar.setImageResource(R.drawable.account_avatar);
                AccountActivity.mostrarFoto(avatar, photo);
                avatar.setTag(photo);
            }
            boolean hasPhoto = photo != null && !photo.isEmpty();
            section.findViewById(R.id.account_initials).setVisibility(hasPhoto ? View.GONE : View.VISIBLE);
            textoEn(R.id.account_initials, iniciales(ajustes().getString("profile_name", session.worker.name), 2));
            textoEn(R.id.text_sync, rutas.estadoEnvios(session.worker) + "\n" + MeasurementSync.estado(session));
            section.findViewById(R.id.button_retry_sync).setEnabled(!MeasurementSync.enCurso());
        }
    }


    /** --------------------
     * Diseño lógico: estadoUbicacion() -->
     * Text <--
     * Descripción: Estado Ubicacion. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private String estadoUbicacion() {
        return getString(permiso(Manifest.permission.ACCESS_FINE_LOCATION)
                ? R.string.main_location_ready : R.string.main_location_missing);
    }


    /** --------------------
     * Diseño lógico: permiso: Text --> permiso() -->
     * B <--
     * Descripción: Permiso. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private boolean permiso(String permiso) {
        return checkSelfPermission(permiso) == PackageManager.PERMISSION_GRANTED;
    }


    /** --------------------
     * Diseño lógico: estadoSensor() -->
     * Text <--
     * Descripción: Estado Sensor. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private String estadoSensor() {
        String estado = sensorRuta == null ? getString(R.string.main_no_device)
                : getString(R.string.main_device_id, sensorRuta);
        if (!activo) return estado;
        if (RutaService.actual != id) return estado + "\n" + getString(R.string.main_active_missing);
        if (sensorRuta != null) {
            long referencia = RutaService.ultimoAnuncio == 0 ? RutaService.desde : RutaService.ultimoAnuncio;
            long edad = (SystemClock.elapsedRealtime() - referencia) / 1000;
            estado += "\n" + (edad >= ajustes().getInt("silencio", 20) ? getString(R.string.main_silence_ble, edad)
                    : getString(RutaService.ultimoAnuncio == 0 ? R.string.main_finding_ble : R.string.main_receiving_ble));
        }
        return estado + "\n" + RutaService.estado;
    }


    /** --------------------
     * Diseño lógico: sensor: Text, conAviso: B --> mostrarUltima() -->
     * Descripción: Mostrar Ultima. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void mostrarUltima(String sensor, boolean conAviso) {
        String lectura = getString(R.string.main_no_readings);
        String aviso = "";
        if (tab == R.id.tab_inicio) {
            String detail = "Todavía no hay lecturas";
            int value = -1, sent = 0;
            try (Cursor c = rutas.ultimaConEnvio(session.worker)) {
                if (c.moveToFirst()) {
                    value = c.getInt(0);
                    sent = c.getInt(3);
                    textoEn(R.id.home_clock, horaLectura(c.getString(1)));
                    detail = sent == 1 ? "Medición enviada" : sent == 2 ? "Envío rechazado" : "Guardada en el móvil";
                    aviso = EnvironmentalAssessment.aviso(value, ajustes().getInt("umbral", 60));
                }
            }
            textoEn(R.id.home_last_detail, detail);
            TextView detailView = section.findViewById(R.id.home_last_detail);
            int detailIcon = value < 0 ? 0 : sent == 1 ? R.drawable.ic_s_check_circle : R.drawable.ic_s_clock;
            detailView.setCompoundDrawablesRelativeWithIntrinsicBounds(detailIcon, 0, 0, 0);
            detailView.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(getColor(
                    sent == 1 ? R.color.status_success : sent == 2 ? R.color.status_error : R.color.senda_muted)));
            mostrarNivel(value);
            android.text.SpannableStringBuilder big = new android.text.SpannableStringBuilder(value < 0 ? "—" : String.valueOf(value));
            if (value >= 0) {
                int from = big.length();
                big.append(" ppb");
                big.setSpan(new android.text.style.RelativeSizeSpan(0.42f), from, big.length(), 0);
                big.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.NORMAL), from, big.length(), 0);
                big.setSpan(new android.text.style.ForegroundColorSpan(getColor(R.color.senda_muted)), from, big.length(), 0);
            }
            textoEn(R.id.text_last_o3, big);
            if (conAviso) {
                textoEn(R.id.text_o3_warning, aviso);
                visible(R.id.text_o3_warning, !aviso.isEmpty());
            }
            return;
        } else if (tab == R.id.tab_rutas) {
            try (Cursor c = rutas.ultima(id)) {
                if (c.moveToFirst()) lectura = getString(R.string.main_last_reading_value,
                        c.getInt(0), fechaLectura(c.getString(1)), nombreSensor(sensor));
            }
        } else {
            try (Cursor c = rutas.historial(session.worker, sensor, 1, 0)) {
                if (c.moveToFirst()) {
                    int ppb = c.getInt(0);
                    lectura = getString(R.string.main_last_reading_value, ppb,
                            fechaLectura(c.getString(1)), nombreSensor(c.getString(2)));
                    if (conAviso) aviso = EnvironmentalAssessment.aviso(ppb, ajustes().getInt("umbral", 60));
                }
            }
        }
        textoEn(R.id.text_last_o3, lectura);
        if (conAviso) {
            textoEn(R.id.text_o3_warning, aviso);
            visible(R.id.text_o3_warning, !aviso.isEmpty());
        }
    }

    /** Nivel orientativo respecto al umbral configurado y posición en la barra 0–120 ppb. */

    /** --------------------
     * Diseño lógico: value: Z --> mostrarNivel() -->
     * Descripción: Mostrar Nivel. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void mostrarNivel(int value) {
        TextView level = section.findViewById(R.id.home_level);
        if (value < 0) { level.setVisibility(View.GONE); return; }
        int umbral = ajustes().getInt("umbral", 60);
        boolean high = value >= umbral * 1.25f, moderate = !high && value >= umbral * 0.75f;
        textoEn(R.id.home_level, high ? "Mala calidad" : moderate ? "Calidad moderada" : "Buena calidad");
        int tone = getColor(high ? R.color.status_error : moderate ? R.color.senda_moderate : R.color.status_success);
        int surface = getColor(high ? R.color.status_error_surface : moderate ? R.color.senda_moderate_surface : R.color.status_success_surface);
        int dot = high ? getColor(R.color.status_error_strong) : moderate ? 0xffd99a2b : getColor(R.color.status_success);
        level.setTextColor(tone);
        level.setBackgroundTintList(android.content.res.ColorStateList.valueOf(surface));
        level.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_s_dot_ring, 0, 0, 0);
        level.setCompoundDrawableTintList(android.content.res.ColorStateList.valueOf(dot));
        level.setVisibility(View.VISIBLE);
    }

    /** Hora de la última lectura: "12:46" si es de hoy, "18/11 · 12:46" si no. */

    /** --------------------
     * Diseño lógico: iso: Text --> horaLectura() -->
     * Text <--
     * Descripción: Hora Lectura. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private String horaLectura(String iso) {
        try {
            long instant = SessionManager.instante(iso);
            java.util.Calendar now = java.util.Calendar.getInstance(), when = java.util.Calendar.getInstance();
            when.setTimeInMillis(instant);
            boolean today = now.get(java.util.Calendar.YEAR) == when.get(java.util.Calendar.YEAR)
                    && now.get(java.util.Calendar.DAY_OF_YEAR) == when.get(java.util.Calendar.DAY_OF_YEAR);
            return new java.text.SimpleDateFormat(today ? "HH:mm" : "dd/MM · HH:mm", java.util.Locale.getDefault()).format(new Date(instant));
        } catch (ParseException e) { return ""; }
    }


    /** --------------------
     * Diseño lógico: name: Text, max: Z --> iniciales() --x
     * Text <--
     * Descripción: Iniciales. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private static String iniciales(String name, int max) {
        if (name == null) return "";
        StringBuilder out = new StringBuilder();
        for (String part : name.trim().split("\\s+")) {
            if (!part.isEmpty() && out.length() < max) out.appendCodePoint(Character.toUpperCase(part.codePointAt(0)));
        }
        return out.toString();
    }


    /** --------------------
     * Diseño lógico: iso: Text --> fechaLectura() -->
     * Text <--
     * Descripción: Fecha Lectura. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private String fechaLectura(String iso) {
        try {
            return fecha.format(new Date(SessionManager.instante(iso)));
        } catch (ParseException ignored) {
            return iso;
        }
    }


    /** --------------------
     * Diseño lógico: sensor: Text --> nombreSensor() -->
     * Text <--
     * Descripción: Nombre Sensor. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private String nombreSensor(String sensor) {
        return sensor == null ? getString(R.string.main_unknown_sensor) : sensor;
    }


    /** --------------------
     * Diseño lógico: incremento: Z --> cambiarPagina() -->
     * Descripción: Cambiar Pagina. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void cambiarPagina(int incremento) {
        if (tab == R.id.tab_rutas) routePage += incremento;
        else historyPage += incremento;
        cargarPagina(true);
    }


    /** --------------------
     * Diseño lógico: desplazar: B --> cargarPagina() -->
     * Descripción: Cargar Pagina. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void cargarPagina(boolean desplazar) {
        boolean esRuta = tab == R.id.tab_rutas;
        int pagina = esRuta ? routePage : historyPage;
        if(esRuta) {
            long[] range=routePeriod.bounds(System.currentTimeMillis(),java.util.TimeZone.getDefault());
            routeRangeStart=range[0]; routeRangeEnd=range[1];
            routeCards.clear();
        }
        LinearLayout lista = section.findViewById(esRuta ? R.id.list_routes : R.id.list_history);
        lista.removeAllViews();
        int filas = 0;
        boolean haySiguiente;
        try (Cursor c = esRuta ? rutas.listarPeriodo(session.worker,routeRangeStart,routeRangeEnd,PAGE_SIZE+1,pagina*PAGE_SIZE)
                : rutas.historial(session.worker, null, PAGE_SIZE + 1, pagina * PAGE_SIZE)) {
            while (filas < PAGE_SIZE && c.moveToNext()) {
                if (esRuta) agregarRuta(lista, c);
                else agregarLectura(lista, c);
                filas++;
            }
            haySiguiente=c.moveToNext();
            section.findViewById(R.id.button_next).setEnabled(haySiguiente);
        }
        visible(R.id.text_list_empty, filas == 0 && pagina == 0);
        if(esRuta) {
            visible(R.id.routes_pages,haySiguiente||pagina>0);
            visible(R.id.page_number,haySiguiente||pagina>0);
            lastPreviewUpdate=SystemClock.elapsedRealtime();
        }
        section.findViewById(R.id.button_previous).setEnabled(pagina > 0);
        textoEn(R.id.page_number, getString(R.string.main_page, pagina + 1));
        if (desplazar) scroll.scrollTo(0, 0);
    }


    /** --------------------
     * Diseño lógico: lista: LinearLayout, ruta: Cursor --> agregarRuta() -->
     * Descripción: Agregar Ruta. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void agregarRuta(LinearLayout lista, Cursor ruta) {
        long routeId=ruta.getLong(0);
        RouteCardView card=new RouteCardView(this);
        card.bind(routeId,ruta.getLong(1),ruta.isNull(2)?null:ruta.getLong(2),ruta.getDouble(3),
                ruta.getInt(4),rutas.vistaPrevia(session.worker,routeId));
        boolean rutaActiva=ruta.isNull(2);
        card.setOnClickListener(v -> abrirRuta(rutaActiva ? RouteActivity.class : SummaryActivity.class,routeId));
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(-1,-2);
        params.topMargin=dp(12); params.bottomMargin=dp(4);
        lista.addView(card,params);
        routeCards.put(routeId,card);
    }


    /** --------------------
     * Diseño lógico: lista: LinearLayout, lectura: Cursor --> agregarLectura() -->
     * Descripción: Agregar Lectura. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void agregarLectura(LinearLayout lista, Cursor lectura) {
        TextView fila = new TextView(this);
        fila.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
        fila.setPadding(0, dp(8), 0, dp(8));
        fila.setText(getString(R.string.main_reading_row, lectura.getInt(0), fechaLectura(lectura.getString(1)),
                nombreSensor(lectura.getString(2)), lectura.getLong(3)));
        lista.addView(fila);
    }


    /** --------------------
     * Diseño lógico: pantalla: Class, rutaId: Z --> abrirRuta() -->
     * Descripción: Abrir Ruta. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void abrirRuta(Class<?> pantalla, long rutaId) {
        if (rutaId > 0) startActivity(new Intent(this, pantalla).putExtra(EXTRA_ROUTE_ID, rutaId));
    }


    /** --------------------
     * Diseño lógico: confirmarSalida() -->
     * Descripción: Confirmar Salida. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void confirmarSalida() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(activo ? "Al cerrar sesión finalizará el recorrido." : "¿Cerrar la sesión en este dispositivo?")
                .setNegativeButton("Volver", null)
                .setPositiveButton(R.string.main_logout, (d, w) -> salir(true)).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.status_error));
    }


    /** --------------------
     * Diseño lógico: mensaje: Text --> acceso() -->
     * Descripción: Acceso. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void acceso(String mensaje) {
        startActivity(new Intent(this,Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("mensaje",mensaje));
        finish();
    }

    /** --------------------
     * Diseño lógico: ajustes() -->
     * Preferencias <--
     * Descripción: Ajustes. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private SharedPreferences ajustes() { return getSharedPreferences("campo-"+session.worker.id+"-"+session.worker.parkId,MODE_PRIVATE); }

    /** --------------------
     * Diseño lógico: elegido() -->
     * Text <--
     * Descripción: Elegido. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private String elegido() { return ajustes().getString("sensor",null); }

    /** --------------------
     * Diseño lógico: seleccionar() -->
     * Descripción: Seleccionar. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void seleccionar() {
        startActivityForResult(new Intent(this,QrScannerActivity.class).putExtra("sensor",elegido()),REQUEST_SENSOR);
    }

    /** --------------------
     * Diseño lógico: req: Z, result: Z, data: Navegacion --> onActivityResult() -->
     * Descripción: On Activity Result. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    @Override protected void onActivityResult(int req,int result,Intent data) {
        super.onActivityResult(req,result,data);
        if(req==REQUEST_SENSOR && result==RESULT_OK && data!=null && !activo && SessionManager.current()!=null) {
            cambiarLlavero(data.getStringExtra("sensor"));
        }
    }

    /** --------------------
     * Diseño lógico: gestionarLlavero() -->
     * Descripción: Gestionar Llavero. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void gestionarLlavero() {
        if (activo || operacionLlavero) { mensaje(getString(R.string.keyring_active_hint)); return; }
        if (elegido() == null) { seleccionar(); return; }
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.keyring_unlink)
                .setMessage("El llavero quedará libre para otro trabajador de tu parque. Se conservarán las mediciones.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Desvincular", (d, w) -> cambiarLlavero(null)).create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.status_error));
    }


    /** --------------------
     * Diseño lógico: sensor: Text --> cambiarLlavero() -->
     * Descripción: Cambiar Llavero. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void cambiarLlavero(String sensor) {
        actualizar();
        if (activo || operacionLlavero || SessionManager.current() != session) return;
        operacionLlavero = true;
        mensaje("Comprobando el llavero en el servidor…");
        REMOTO.execute(() -> {
            try {
                LogicaFake logica = new LogicaFake(this);
                org.json.JSONObject resultado = sensor == null ? logica.desvincularLlavero(session.token)
                        : logica.vincularLlavero(session.token, sensor);
                runOnUiThread(() -> {
                    if (isDestroyed() || SessionManager.current() != session) return;
                    guardarLlavero(resultado.isNull("llavero") ? null : resultado.optString("llavero", null));
                    mensaje("");
                    aviso(sensor == null ? "Llavero desvinculado correctamente." : "Llavero vinculado correctamente.", true);
                    operacionLlavero = false;
                    actualizar();
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    if (isDestroyed() || SessionManager.current() != session) return;
                    operacionLlavero = false;
                    mensaje("");
                    aviso(error instanceof com.example.ozora.senda.network.ApiClient.Error ? error.getMessage()
                            : "No se pudo confirmar la vinculación. Comprueba la conexión y vuelve a intentarlo.", false);
                });
            }
        });
    }


    /** --------------------
     * Diseño lógico: sensor: Text --> guardarLlavero() -->
     * Descripción: Guardar Llavero. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void guardarLlavero(String sensor) {
        SharedPreferences.Editor editor = ajustes().edit().putString("sensor", sensor);
        if (!android.text.TextUtils.equals(sensor, elegido())) {
            if (sensor == null) editor.remove("sensor_linked_at");
            else editor.putLong("sensor_linked_at", System.currentTimeMillis());
        }
        editor.apply();
    }


    /** --------------------
     * Diseño lógico: actualizarRemoto() -->
     * Descripción: Actualizar Remoto. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void actualizarRemoto() {
        REMOTO.execute(() -> {
            try {
                org.json.JSONObject usuario = new LogicaFake(this).consultarSesion(session.token).getJSONObject("usuario");
                runOnUiThread(() -> {
                    if (isDestroyed() || SessionManager.current() != session) return;
                    if (usuario.has("llavero")) guardarLlavero(usuario.isNull("llavero") ? null : usuario.optString("llavero", null));
                    if (usuario.has("foto_perfil")) ajustes().edit()
                            .putString("profile_photo", usuario.isNull("foto_perfil") ? null : usuario.optString("foto_perfil")).apply();
                    actualizar();
                });
            } catch (Exception error) {
                if (error instanceof com.example.ozora.senda.network.ApiClient.Error
                        && ((com.example.ozora.senda.network.ApiClient.Error)error).status == 401) {
                    runOnUiThread(() -> { if (!isDestroyed() && SessionManager.current() == session) salir(false); });
                }
            }
        });
    }

    /** Activa pidiendo el permiso aquí mismo; Android solo permite quitarlo desde sus ajustes. */

    /** --------------------
     * Diseño lógico: abrirNotificaciones() -->
     * Descripción: Abrir Notificaciones. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void abrirNotificaciones() {
        boolean enabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!enabled && Build.VERSION.SDK_INT >= 33 && !permiso(Manifest.permission.POST_NOTIFICATIONS)
                && (!ajustes().getBoolean("notif_asked", false) || shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))) {
            ajustes().edit().putBoolean("notif_asked", true).apply();
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 22);
            return;
        }
        Intent settings = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        if (enabled) aviso("Para desactivarlas, apágalas en los ajustes de Android.", true);
        try { startActivity(settings); }
        catch (android.content.ActivityNotFoundException e) { abrirAjustesApp(); }
    }


    /** --------------------
     * Diseño lógico: cambiarUbicacion() -->
     * Descripción: Cambiar Ubicacion. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void cambiarUbicacion() {
        if (!permiso(Manifest.permission.ACCESS_FINE_LOCATION)
                && (!ajustes().getBoolean("location_asked", false)
                || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))) {
            ajustes().edit().putBoolean("location_asked", true).apply();
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_SETTINGS_LOCATION);
            return;
        }
        if (permiso(Manifest.permission.ACCESS_FINE_LOCATION))
            aviso(activo ? "Finaliza el recorrido antes de quitar la ubicación." : "Para quitarla, cambia el permiso de Ubicación en Android.", !activo);
        if (!activo || !permiso(Manifest.permission.ACCESS_FINE_LOCATION)) abrirAjustesApp();
    }


    /** --------------------
     * Diseño lógico: abrirAjustesApp() -->
     * Descripción: Abrir Ajustes App. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void abrirAjustesApp() {
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null)));
        } catch (android.content.ActivityNotFoundException ignored) { }
    }


    /** --------------------
     * Diseño lógico: mostrarAyuda() -->
     * Descripción: Mostrar Ayuda. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void mostrarAyuda() {
        new AlertDialog.Builder(this).setTitle(R.string.account_help)
                .setMessage("Llavero: escanea su QR y comienza un recorrido para recibir lecturas por Bluetooth. La batería y el firmware no están disponibles con el protocolo actual.\n\nUbicación: permite la ubicación precisa y los dispositivos cercanos para registrar el recorrido.\n\nPerfil: la foto se guarda en tu cuenta del servidor; el nombre visible y el teléfono se guardan en este móvil. Para cambiar el correo o el parque, contacta con tu responsable.\n\nEnvíos: si falla la conexión, las lecturas se conservan en el móvil. Puedes reintentarlas desde Ajustes.")
                .setPositiveButton("Entendido", null).show();
    }

    /** --------------------
     * Diseño lógico: configurarBiometria() -->
     * Descripción: Configurar Biometria. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void configurarBiometria() {
        startActivity(new Intent(this, com.example.ozora.senda.auth.BiometricSetupActivity.class));
    }

    /** --------------------
     * Diseño lógico: configurar() -->
     * Descripción: Configurar. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void configurar() {
        List<String> opciones=new ArrayList<>();
        opciones.add(bio.activo()?"Desactivar biometría":"Activar biometría");
        opciones.add("Umbral orientativo de ozono"); opciones.add("Tiempo sin anuncios BLE");
        if(activo && RutaService.actual != id) opciones.add("Reanudar captura");
        new AlertDialog.Builder(this).setTitle("Ajustes de campo").setItems(opciones.toArray(new String[0]),(d,w)->{
            if(w==0) {
                configurarBiometria();
            } else if(w==1) ajustarNumero("umbral","Aviso orientativo (ppb)",60,65535);
            else if(w==2) ajustarNumero("silencio","Aviso de silencio BLE (segundos)",20,3600);
            else preparar();
        }).show();
    }

    /** --------------------
     * Diseño lógico: key: Text, titulo: Text, inicial: Z, max: Z --> ajustarNumero() -->
     * Descripción: Ajustar Numero. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void ajustarNumero(String key,String titulo,int inicial,int max) {
        EditText e=campo(titulo,InputType.TYPE_CLASS_NUMBER); e.setText(String.valueOf(ajustes().getInt(key,inicial)));
        new AlertDialog.Builder(this).setTitle(titulo).setView(e).setNegativeButton("Cancelar",null).setPositiveButton("Guardar",(d,w)->{
            try { int n=Integer.parseInt(e.getText().toString());
                if(n<1 || n>max) throw new NumberFormatException();
                ajustes().edit().putInt(key,n).apply(); actualizar();
            } catch(NumberFormatException ex) { mensaje("Introduce un número entre 1 y "+max+"."); }
        }).show();
    }

    /** --------------------
     * Diseño lógico: preparar() -->
     * Descripción: Preparar. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void preparar() {
        List<String> permisos=new ArrayList<>(Arrays.asList(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION));
        if(Build.VERSION.SDK_INT>=31 && (activo?sensorRuta:elegido())!=null) {
            permisos.add(Manifest.permission.BLUETOOTH_SCAN); permisos.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        for(String p:permisos) if(checkSelfPermission(p)!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permisos.toArray(new String[0]),REQUEST_ROUTE_PERMISSIONS); return;
        }
        if (Build.VERSION.SDK_INT >= 33 && !permiso(Manifest.permission.POST_NOTIFICATIONS)
                && !notificationPermissionAsked) {
            notificationPermissionAsked = true;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_ROUTE_NOTIFICATION);
            return;
        }
        iniciar();
    }

    /** --------------------
     * Diseño lógico: centrarMapa() -->
     * Descripción: Centrar Mapa. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void centrarMapa() {
        if (!permiso(Manifest.permission.ACCESS_FINE_LOCATION)) {
            mapPermissionAsked = true;
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_MAP_LOCATION);
        } else if (homeMap != null) homeMap.recenter();
    }


    /** --------------------
     * Diseño lógico: req: Z, permisos: [Dato], resultados: [Dato] --> onRequestPermissionsResult() -->
     * Descripción: On Request Permissions Result. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    @Override public void onRequestPermissionsResult(int req,String[] permisos,int[] resultados) {
        super.onRequestPermissionsResult(req,permisos,resultados);
        if (req == REQUEST_ROUTE_NOTIFICATION) {
            if (SessionManager.current() == session) iniciar();
            return;
        }
        if (req == 22) {
            actualizar();
            if (!permiso(Manifest.permission.POST_NOTIFICATIONS))
                mensaje("Puedes activar las notificaciones desde los ajustes de Android.");
            return;
        }
        if (req == REQUEST_MAP_LOCATION) {
            if (homeMap != null) homeMap.start();
            return;
        }
        if (req == REQUEST_SETTINGS_LOCATION) {
            actualizar();
            aviso(permiso(Manifest.permission.ACCESS_FINE_LOCATION) ? "Ubicación precisa permitida."
                    : "Sin ubicación precisa no se puede registrar el recorrido.", permiso(Manifest.permission.ACCESS_FINE_LOCATION));
            return;
        }
        if(req!=REQUEST_ROUTE_PERMISSIONS || SessionManager.current()==null) return;
        for(int r:resultados) if(r!=PackageManager.PERMISSION_GRANTED) { mensaje("Para registrar el recorrido permite la ubicación precisa y, si usas llavero, los dispositivos cercanos en Ajustes."); return; }
        if(resultados.length>0) preparar();
    }

    /** --------------------
     * Diseño lógico: iniciar() -->
     * Descripción: Iniciar. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void iniciar() {
        if(System.currentTimeMillis()>=session.expiresAt) { salir(false); return; }
        actualizar();
        if(activo && RutaService.actual == id) return;
        boolean nuevoRecorrido=!activo;
        if(!activo) {
            sensorRuta=elegido();
            try { id=rutas.iniciar(session.worker.id,session.worker.parkId,sensorRuta); }
            catch(android.database.sqlite.SQLiteConstraintException ex) { mensaje("Ya existe un recorrido activo para esta cuenta."); actualizar(); return; }
        }
        Intent i=new Intent(this,RutaService.class).putExtra("ruta",id).putExtra("sensor",sensorRuta)
                .putExtra("vence",session.expiresAt).putExtra("reanudar",activo);
        try {
            if(Build.VERSION.SDK_INT>=26) startForegroundService(i); else startService(i);
            if(nuevoRecorrido) {
                routePeriod=RoutePeriod.TODAY;
                routePage=0;
                if(tab==R.id.tab_rutas) {
                    ((MaterialButtonToggleGroup)findViewById(R.id.routes_period)).check(R.id.routes_today);
                    cargarPagina(false);
                }
            }
            navigation.setSelectedItemId(R.id.tab_rutas);
            abrirRuta(RouteActivity.class, id);
        } catch(RuntimeException e) { mensaje("Android no pudo iniciar la captura. Revisa los permisos y reanúdala desde Rutas."); }
        actualizar();
    }

    /** --------------------
     * Diseño lógico: confirmarFin() -->
     * Descripción: Confirmar Fin. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void confirmarFin() {
        new AlertDialog.Builder(this).setTitle("¿Finalizar recorrido?").setMessage("Se cerrará la captura y podrás consultar el resumen y el mapa.")
            .setPositiveButton("Finalizar",(d,w)->{
                long terminada = id;
                finalizar();
                actualizar();
                if (tab == R.id.tab_rutas) cargarPagina(false);
                abrirRuta(SummaryActivity.class, terminada);
            }).setNegativeButton("Continuar",null).show();
    }

    /** --------------------
     * Diseño lógico: finalizar() -->
     * Descripción: Finalizar. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void finalizar() {
        if(activo) {
            RutaService.finalizar(this, rutas, id); activo=false;
        }
    }

    /** --------------------
     * Diseño lógico: revocar: B --> salir() -->
     * Descripción: Salir. Coordina cinco secciones, permisos y acciones del trabajador
     * -------------------- */
    private void salir(boolean revocar) {
        String anterior=session.token;
        finalizar();
        // La huella necesita su credencial cifrada vigente tras salir; no se prorroga su caducidad.
        boolean conservarBiometria = bio.activo();
        SessionManager.clear();
        if(revocar && !conservarBiometria) {
            LogicaFake logica=new LogicaFake(this);
            new Thread(()->{
                try { logica.cerrarSesion(anterior); }
                catch(Exception e) { runOnUiThread(()->Toast.makeText(getApplicationContext(),
                        "Sesión cerrada en el móvil. Revocación sin confirmar.",Toast.LENGTH_LONG).show()); }
            }).start();
        }
        acceso(revocar ? null : "Sesión caducada. Vuelve a identificarte.");
    }
}
