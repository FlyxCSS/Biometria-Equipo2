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
import com.example.ozora.senda.auth.LoginActivity;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.Route;
import com.example.ozora.senda.network.ApiClient;
import com.example.ozora.senda.network.MeasurementSync;
import com.example.ozora.senda.qr.QrScannerActivity;
import com.example.ozora.senda.routes.RouteActivity;
import com.example.ozora.senda.routes.RouteManager;
import com.example.ozora.senda.routes.RutaService;
import com.example.ozora.senda.statistics.EnvironmentalAssessment;
import com.example.ozora.senda.statistics.StatisticsCalculator;
import com.example.ozora.senda.ui.FieldScreen;
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
    public static final String EXTRA_ROUTE_ID = "ruta";

    private static final int PAGE_SIZE = 20;
    private static final int REQUEST_ROUTE_PERMISSIONS = 19;
    private static final int REQUEST_SENSOR = 20;

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
    private boolean activo;
    private long id;
    private String sensorRuta;
    private long ultimoEnvio;

    private final Runnable tick = new Runnable() {
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
            @Override public void handleOnBackPressed() {
                navigation.setSelectedItemId(R.id.tab_inicio);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backToHome);
        navigation.setOnItemSelectedListener(item -> {
            mostrar(item.getItemId());
            return true;
        });
        int initial = getIntent().getBooleanExtra(EXTRA_SHOW_ROUTES, false) ? R.id.tab_rutas : R.id.tab_inicio;
        if (state != null) {
            initial = state.getInt("section", initial);
            routePage = state.getInt("route_page");
            historyPage = state.getInt("history_page");
        }
        navigation.setSelectedItemId(initial);
        if (tab == 0) mostrar(initial);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (navigation != null && intent.getBooleanExtra(EXTRA_SHOW_ROUTES, false)) {
            navigation.setSelectedItemId(R.id.tab_rutas);
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("section", tab);
        state.putInt("route_page", routePage);
        state.putInt("history_page", historyPage);
    }

    @Override protected void onStart() {
        super.onStart();
        if (session != null) {
            if (SessionManager.current() != session) {
                salir(false);
                return;
            }
            actualizar();
            if (tab == R.id.tab_rutas || tab == R.id.tab_historial) cargarPagina(false);
            reloj.post(tick);
        }
    }

    @Override protected void onStop() {
        reloj.removeCallbacks(tick);
        super.onStop();
    }

    @Override protected void onDestroy() {
        if (rutas != null) rutas.close();
        if (isFinishing() && !activo && session == SessionManager.current()) SessionManager.clear();
        super.onDestroy();
    }

    private void mostrar(int destino) {
        int layout = R.layout.section_inicio;
        if (destino == R.id.tab_rutas) layout = R.layout.section_rutas;
        else if (destino == R.id.tab_historial) layout = R.layout.section_historial;
        else if (destino == R.id.tab_sensor) layout = R.layout.section_sensor;
        else if (destino == R.id.tab_mas) layout = R.layout.section_mas;

        tab = destino;
        backToHome.setEnabled(destino != R.id.tab_inicio);
        section.removeAllViews();
        getLayoutInflater().inflate(layout, section, true);
        scroll.scrollTo(0, 0);
        mensaje("");

        if (destino == R.id.tab_inicio) {
            accion(R.id.button_start_route, () -> {
                if (activo) navigation.setSelectedItemId(R.id.tab_rutas);
                else preparar();
            });
        } else if (destino == R.id.tab_rutas) {
            accion(R.id.button_start_route, () -> { if (activo) confirmarFin(); else preparar(); });
            accion(R.id.button_resume_route, this::preparar);
            accion(R.id.button_route_map, () -> abrirRuta(RouteActivity.class, id));
            accion(R.id.button_route_summary, () -> abrirRuta(SummaryActivity.class, id));
        } else if (destino == R.id.tab_historial) {
            accion(R.id.button_refresh_history, () -> { historyPage = 0; cargarPagina(true); });
        } else if (destino == R.id.tab_sensor) {
            accion(R.id.button_select_sensor, this::seleccionar);
        } else if (destino == R.id.tab_mas) {
            accion(R.id.button_settings, this::configurar);
            accion(R.id.button_permissions, () -> startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null))));
            accion(R.id.button_retry_sync, () -> {
                rutas.reintentarEnvios(session.worker);
                MeasurementSync.sincronizar(this);
                actualizar();
            });
            accion(R.id.button_logout, this::confirmarSalida);
        }
        if (destino == R.id.tab_rutas || destino == R.id.tab_historial) {
            accion(R.id.button_previous, () -> cambiarPagina(-1));
            accion(R.id.button_next, () -> cambiarPagina(1));
            cargarPagina(false);
        }
        actualizar();
    }

    private void accion(int viewId, Runnable action) {
        section.findViewById(viewId).setOnClickListener(v -> action.run());
    }

    private void visible(int viewId, boolean visible) {
        section.findViewById(viewId).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void textoEn(int viewId, CharSequence text) {
        TextView view = section.findViewById(viewId);
        if (!android.text.TextUtils.equals(view.getText(), text)) view.setText(text);
    }

    @Override protected void mensaje(String texto) {
        TextView feedback = findViewById(R.id.main_feedback);
        if (feedback == null) return;
        feedback.setText(texto);
        feedback.setVisibility(texto == null || texto.isEmpty() ? View.GONE : View.VISIBLE);
        if (texto != null && !texto.isEmpty()) scroll.post(() -> scroll.smoothScrollTo(0, feedback.getBottom()));
    }

    private void actualizar() {
        Route route = rutas.ultimaRuta(session.worker);
        long anterior = id;
        boolean antesActivo = activo;
        id = route == null ? 0 : route.id;
        activo = route != null && route.end == null;
        sensorRuta = activo ? route.sensor : elegido();

        if (tab == R.id.tab_inicio || tab == R.id.tab_mas) {
            textoEn(R.id.text_account, getString(R.string.main_account, session.worker.name, session.worker.parkId));
        }
        if (tab == R.id.tab_inicio || tab == R.id.tab_sensor) textoEn(R.id.text_device, estadoSensor());
        if (tab == R.id.tab_inicio) {
            textoEn(R.id.text_location, estadoUbicacion());
            textoEn(R.id.text_route_summary, activo ? StatisticsCalculator.resumen(route, System.currentTimeMillis())
                    : getString(R.string.main_no_route));
            textoEn(R.id.button_start_route, getString(activo ? R.string.main_open_active : R.string.main_start));
            mostrarUltima(null, true);
        } else if (tab == R.id.tab_rutas) {
            textoEn(R.id.text_route_summary, activo ? StatisticsCalculator.resumen(route, System.currentTimeMillis())
                    + "\n" + estadoSensor() : getString(R.string.main_no_route));
            textoEn(R.id.button_start_route, getString(activo ? R.string.main_finish : R.string.main_start));
            visible(R.id.button_resume_route, activo && RutaService.actual != id);
            visible(R.id.button_route_map, activo);
            visible(R.id.button_route_summary, activo);
            visible(R.id.text_last_o3, activo);
            if (activo) mostrarUltima(sensorRuta, false);
            if (anterior != id || antesActivo != activo) cargarPagina(false);
        } else if (tab == R.id.tab_sensor) {
            section.findViewById(R.id.button_select_sensor).setEnabled(!activo);
            if (sensorRuta == null) textoEn(R.id.text_last_o3, getString(R.string.main_no_readings));
            else mostrarUltima(sensorRuta, false);
        } else if (tab == R.id.tab_mas) {
            String permisos = estadoUbicacion();
            if (Build.VERSION.SDK_INT >= 31) {
                boolean ble = permiso(Manifest.permission.BLUETOOTH_SCAN) && permiso(Manifest.permission.BLUETOOTH_CONNECT);
                permisos += "\n" + getString(ble ? R.string.main_nearby_ready : R.string.main_nearby_missing);
            }
            textoEn(R.id.text_permissions, permisos);
            textoEn(R.id.text_sync, rutas.estadoEnvios(session.worker) + "\n" + MeasurementSync.estado);
        }
    }

    private String estadoUbicacion() {
        return getString(permiso(Manifest.permission.ACCESS_FINE_LOCATION)
                ? R.string.main_location_ready : R.string.main_location_missing);
    }

    private boolean permiso(String permiso) {
        return checkSelfPermission(permiso) == PackageManager.PERMISSION_GRANTED;
    }

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

    private void mostrarUltima(String sensor, boolean conAviso) {
        String lectura = getString(R.string.main_no_readings);
        String aviso = "";
        if (tab == R.id.tab_rutas) {
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

    private String fechaLectura(String iso) {
        try {
            return fecha.format(new Date(SessionManager.instante(iso)));
        } catch (ParseException ignored) {
            return iso;
        }
    }

    private String nombreSensor(String sensor) {
        return sensor == null ? getString(R.string.main_unknown_sensor) : sensor;
    }

    private void cambiarPagina(int incremento) {
        if (tab == R.id.tab_rutas) routePage += incremento;
        else historyPage += incremento;
        cargarPagina(true);
    }

    private void cargarPagina(boolean desplazar) {
        boolean esRuta = tab == R.id.tab_rutas;
        int pagina = esRuta ? routePage : historyPage;
        LinearLayout lista = section.findViewById(esRuta ? R.id.list_routes : R.id.list_history);
        lista.removeAllViews();
        int filas = 0;
        try (Cursor c = esRuta ? rutas.listar(session.worker, PAGE_SIZE + 1, pagina * PAGE_SIZE)
                : rutas.historial(session.worker, null, PAGE_SIZE + 1, pagina * PAGE_SIZE)) {
            while (filas < PAGE_SIZE && c.moveToNext()) {
                if (esRuta) agregarRuta(lista, c);
                else agregarLectura(lista, c);
                filas++;
            }
            section.findViewById(R.id.button_next).setEnabled(c.moveToNext());
        }
        visible(R.id.text_list_empty, filas == 0 && pagina == 0);
        section.findViewById(R.id.button_previous).setEnabled(pagina > 0);
        textoEn(R.id.page_number, getString(R.string.main_page, pagina + 1));
        if (desplazar) scroll.scrollTo(0, 0);
    }

    private void agregarRuta(LinearLayout lista, Cursor ruta) {
        long routeId = ruta.getLong(0);
        String estado = getString(ruta.isNull(2) ? R.string.main_route_active : R.string.main_route_done);
        MaterialButton fila = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        fila.setText(getString(R.string.main_route_row, fecha.format(new Date(ruta.getLong(1))), estado));
        fila.setMinHeight(dp(48));
        fila.setOnClickListener(v -> abrirRuta(SummaryActivity.class, routeId));
        lista.addView(fila, new LinearLayout.LayoutParams(-1, -2));
    }

    private void agregarLectura(LinearLayout lista, Cursor lectura) {
        TextView fila = new TextView(this);
        fila.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge);
        fila.setPadding(0, dp(8), 0, dp(8));
        fila.setText(getString(R.string.main_reading_row, lectura.getInt(0), fechaLectura(lectura.getString(1)),
                nombreSensor(lectura.getString(2)), lectura.getLong(3)));
        lista.addView(fila);
    }

    private void abrirRuta(Class<?> pantalla, long rutaId) {
        if (rutaId > 0) startActivity(new Intent(this, pantalla).putExtra(EXTRA_ROUTE_ID, rutaId));
    }

    private void confirmarSalida() {
        new AlertDialog.Builder(this)
                .setMessage(activo ? "Al cerrar sesión finalizará el recorrido." : "¿Cerrar la sesión en este dispositivo?")
                .setNegativeButton("Volver", null)
                .setPositiveButton(R.string.main_logout, (d, w) -> salir(true)).show();
    }

    private void acceso(String mensaje) {
        startActivity(new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("mensaje",mensaje));
        finish();
    }
    private SharedPreferences ajustes() { return getSharedPreferences("campo-"+session.worker.id+"-"+session.worker.parkId,MODE_PRIVATE); }
    private String elegido() { return ajustes().getString("sensor",null); }
    private void seleccionar() {
        startActivityForResult(new Intent(this,QrScannerActivity.class).putExtra("sensor",elegido()),REQUEST_SENSOR);
    }
    @Override protected void onActivityResult(int req,int result,Intent data) {
        super.onActivityResult(req,result,data);
        if(req==REQUEST_SENSOR && result==RESULT_OK && data!=null && !activo && SessionManager.current()!=null) {
            ajustes().edit().putString("sensor",data.getStringExtra("sensor")).apply();
            actualizar();
        }
    }
    private void configurar() {
        List<String> opciones=new ArrayList<>();
        opciones.add(bio.activo()?"Desactivar biometría":"Activar biometría");
        opciones.add("Umbral orientativo de ozono"); opciones.add("Tiempo sin anuncios BLE");
        if(activo && RutaService.actual != id) opciones.add("Reanudar captura");
        new AlertDialog.Builder(this).setTitle("Ajustes de campo").setItems(opciones.toArray(new String[0]),(d,w)->{
            if(w==0) {
                if(bio.activo()) { bio.borrar(); mensaje("Acceso biométrico desactivado."); }
                else if(!bio.disponible()) mensaje("Configura una huella o biometría compatible en los ajustes de Android.");
                else try { bio.activar(session.token); mensaje("Biometría activada para la próxima apertura de la app."); }
                    catch(Exception e) { mensaje("Android no pudo proteger la credencial biométrica."); }
            } else if(w==1) ajustarNumero("umbral","Aviso orientativo (ppb)",60,65535);
            else if(w==2) ajustarNumero("silencio","Aviso de silencio BLE (segundos)",20,3600);
            else preparar();
        }).show();
    }
    private void ajustarNumero(String key,String titulo,int inicial,int max) {
        EditText e=campo(titulo,InputType.TYPE_CLASS_NUMBER); e.setText(String.valueOf(ajustes().getInt(key,inicial)));
        new AlertDialog.Builder(this).setTitle(titulo).setView(e).setNegativeButton("Cancelar",null).setPositiveButton("Guardar",(d,w)->{
            try { int n=Integer.parseInt(e.getText().toString());
                if(n<1 || n>max) throw new NumberFormatException();
                ajustes().edit().putInt(key,n).apply(); actualizar();
            } catch(NumberFormatException ex) { mensaje("Introduce un número entre 1 y "+max+"."); }
        }).show();
    }
    private void preparar() {
        List<String> permisos=new ArrayList<>(Arrays.asList(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION));
        if(Build.VERSION.SDK_INT>=31 && (activo?sensorRuta:elegido())!=null) {
            permisos.add(Manifest.permission.BLUETOOTH_SCAN); permisos.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
        for(String p:permisos) if(checkSelfPermission(p)!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permisos.toArray(new String[0]),REQUEST_ROUTE_PERMISSIONS); return;
        }
        iniciar();
    }
    @Override public void onRequestPermissionsResult(int req,String[] permisos,int[] resultados) {
        super.onRequestPermissionsResult(req,permisos,resultados);
        if(req!=REQUEST_ROUTE_PERMISSIONS || SessionManager.current()==null) return;
        for(int r:resultados) if(r!=PackageManager.PERMISSION_GRANTED) { mensaje("Para registrar el recorrido permite la ubicación precisa y, si usas llavero, los dispositivos cercanos en Ajustes."); return; }
        if(resultados.length>0) iniciar();
    }
    private void iniciar() {
        if(System.currentTimeMillis()>=session.expiresAt) { salir(false); return; }
        actualizar();
        if(activo && RutaService.actual == id) return;
        if(!activo) {
            sensorRuta=elegido();
            try { id=rutas.iniciar(session.worker.id,session.worker.parkId,sensorRuta); }
            catch(android.database.sqlite.SQLiteConstraintException ex) { mensaje("Ya existe un recorrido activo para esta cuenta."); actualizar(); return; }
        }
        Intent i=new Intent(this,RutaService.class).putExtra("ruta",id).putExtra("sensor",sensorRuta)
                .putExtra("vence",session.expiresAt).putExtra("reanudar",activo);
        try {
            if(Build.VERSION.SDK_INT>=26) startForegroundService(i); else startService(i);
            navigation.setSelectedItemId(R.id.tab_rutas);
        } catch(RuntimeException e) { mensaje("Android no pudo iniciar la captura. Revisa los permisos y reanúdala desde Rutas."); }
        actualizar();
    }
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
    private void finalizar() {
        if(activo) {
            RutaService.actual=0; stopService(new Intent(this,RutaService.class)); rutas.finalizar(id); activo=false;
        }
    }
    private void salir(boolean revocar) {
        String anterior=session.token;
        finalizar(); bio.borrar(); SessionManager.clear();
        if(revocar) {
            ApiClient api=new ApiClient(this);
            new Thread(()->{
                try { api.pedir("DELETE","/sesiones/actual",anterior,null); }
                catch(Exception e) { runOnUiThread(()->Toast.makeText(getApplicationContext(),
                        "Sesión cerrada en el móvil. Revocación sin confirmar.",Toast.LENGTH_LONG).show()); }
            }).start();
        }
        acceso(revocar ? null : "Sesión caducada. Vuelve a identificarte.");
    }
}
