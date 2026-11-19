/*
 * Nombre: RutaService.java
 * Descripción: Mantiene captura BLE/GPS y notificación durante el recorrido.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Mantiene captura BLE/GPS y notificación durante el recorrido.
 */
package com.example.ozora.senda.routes;

import com.example.ozora.senda.MainActivity;
import com.example.ozora.senda.R;
import android.app.*;
import android.content.*;
import android.location.Location;
import android.os.*;
import com.example.ozora.senda.integration.CapturaLlaveroController;
import com.example.ozora.senda.location.LocationTracker;
import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.model.MedicionTemperatura;
import com.example.ozora.senda.network.MeasurementSync;

/** Mantiene la captura si el trabajador bloquea la pantalla durante el recorrido. */
public final class RutaService extends Service implements CapturaLlaveroController.Oyente,LocationTracker.Oyente {
    public static long actual, ultimoAnuncio, desde;
    public static String estado="";
    private Location previa;
    private long segmento,vence;
    private RouteManager rutas;
    private LocationTracker gps;
    private CapturaLlaveroController captura;
    private final Handler handler=new Handler(Looper.getMainLooper());

    /** --------------------
     * Diseño lógico: i: Navegacion --> onBind() -->
     * IBinder <--
     * Descripción: On Bind. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public IBinder onBind(Intent i) { return null; }

    /** --------------------
     * Diseño lógico: i: Navegacion, flags: Z, startId: Z --> onStartCommand() -->
     * Z <--
     * Descripción: On Start Command. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public int onStartCommand(Intent i,int flags,int startId) {
        com.example.ozora.senda.auth.SessionManager sesion=com.example.ozora.senda.auth.SessionManager.current();
        if(sesion==null) { stopSelf(); return START_NOT_STICKY; }
        if(i==null) { stopSelf(); return START_NOT_STICKY; }
        if(actual!=0) {
            if(actual!=i.getLongExtra("ruta",0) || !i.getBooleanExtra("reanudar",false)) return START_NOT_STICKY;
            detenerSensores();
        }
        // Canal visible (icono en la barra de estado) pero silencioso; el antiguo de baja importancia se retira.
        if(Build.VERSION.SDK_INT>=26) {
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.deleteNotificationChannel("campo");
            NotificationChannel canal=new NotificationChannel("recorrido","Recorrido en curso",NotificationManager.IMPORTANCE_DEFAULT);
            canal.setDescription("Aviso fijo mientras SENDA registra un recorrido.");
            canal.setSound(null,null); canal.enableVibration(false); canal.setShowBadge(false);
            manager.createNotificationChannel(canal);
        }
        Notification.Builder n=Build.VERSION.SDK_INT>=26 ? new Notification.Builder(this,"recorrido") : new Notification.Builder(this);
        Intent home=new Intent(this,MainActivity.class).putExtra(MainActivity.EXTRA_SHOW_ROUTES, true)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent verRecorrido = PendingIntent.getActivity(this, 0, home,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        String llavero=i.getStringExtra("sensor");
        n.setSmallIcon(R.drawable.ic_s_notification).setColor(0xff1f3d2f)
            .setContentTitle("Recorrido en curso")
            .setContentText(llavero==null ? "En curso · Solo GPS" : "En curso · Llavero "+llavero)
            .setSubText("SENDA")
            .setWhen(System.currentTimeMillis()).setShowWhen(true).setUsesChronometer(true)
            .setOngoing(true).setOnlyAlertOnce(true).setCategory(Notification.CATEGORY_SERVICE)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setContentIntent(verRecorrido)
            .addAction(new Notification.Action.Builder(R.drawable.ic_s_notification, "Ver recorrido", verRecorrido).build());
        if(Build.VERSION.SDK_INT>=31) n.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
        try { startForeground(31,n.build());
        } catch(SecurityException ex) {
            estado="Captura interrumpida. Concede ubicación precisa y reanuda desde la app.";
            stopSelf(); return START_NOT_STICKY;
        }
        if(rutas==null) rutas=new RouteManager(this);
        actual=i.getLongExtra("ruta",0); vence=Math.min(i.getLongExtra("vence",0),sesion.expiresAt);
        if(actual==0 || caducada()) { stopSelf(); return START_NOT_STICKY; }
        handler.postDelayed(this::caducada,Math.max(1,vence-System.currentTimeMillis()));
        segmento=SystemClock.elapsedRealtimeNanos(); desde=SystemClock.elapsedRealtime(); ultimoAnuncio=0;
        String sensor=i.getStringExtra("sensor");
        if(sensor!=null) {
            captura=new CapturaLlaveroController(this,this);
            if(captura.seleccionarQr(sensor) && captura.iniciar()) return START_NOT_STICKY;
        }
        if(captura!=null) { captura.detener(); captura=null; }
        gps=new LocationTracker(this,this);
        estado="Registrando GPS";
        if(!gps.iniciar()) { estado="Captura interrumpida. Revisa la ubicación y los permisos."; stopSelf(); }
        return START_NOT_STICKY;
    }

    /** --------------------
     * Diseño lógico: context: Entorno, routes: RouteManager, id: Z --> finalizar() --x
     * Descripción: Finalizar. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    public static void finalizar(Context context, RouteManager routes, long id) {
        routes.finalizar(id);
        if (actual == id) {
            actual = 0;
            context.stopService(new Intent(context, RutaService.class));
        }
    }


    /** --------------------
     * Diseño lógico: caducada() -->
     * B <--
     * Descripción: Caducada. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    private boolean caducada() {
        if(actual==0) return true;
        if(System.currentTimeMillis()<vence) return false;
        rutas.finalizar(actual); actual=0; estado="La sesión ha caducado."; stopSelf(); return true;
    }

    /** --------------------
     * Diseño lógico: s: Text --> alCambiarEstado() -->
     * Descripción: Al Cambiar Estado. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public void alCambiarEstado(String s) { if(actual!=0) estado=s; }

    /** --------------------
     * Diseño lógico: s: Text --> alOcurrirError() -->
     * Descripción: Al Ocurrir Error. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public void alOcurrirError(String s) { alCambiarEstado(s); }

    /** --------------------
     * Diseño lógico: s: Text --> alDescartarMedicion() -->
     * Descripción: Al Descartar Medicion. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public void alDescartarMedicion(String s) { alCambiarEstado(s); }

    /** --------------------
     * Diseño lógico: alRecibirAnuncio() -->
     * Descripción: Al Recibir Anuncio. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public void alRecibirAnuncio() { if(actual!=0) ultimoAnuncio=SystemClock.elapsedRealtime(); }

    /** --------------------
     * Diseño lógico: p: Posicion --> alRecibirUbicacion() -->
     * Descripción: Al Recibir Ubicacion. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public void alRecibirUbicacion(Location p) {
        if(caducada() || !RouteManager.util(p)) return;
        if(previa!=null && p.getElapsedRealtimeNanos()<=previa.getElapsedRealtimeNanos()) return;
        float distancia=previa==null ? 0 : previa.distanceTo(p);
        if(previa!=null && distancia<2) return;
        rutas.punto(actual,p,segmento,distancia); previa=new Location(p);
    }

    /** --------------------
     * Diseño lógico: m: MedicionAmbiental, rssi: Z --> alPrepararMedicion() -->
     * Descripción: Al Preparar Medicion. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public void alPrepararMedicion(MedicionAmbiental m,int rssi) {
        if(!caducada()) {
            rutas.lectura(actual,m);
            estado="GPS y lectura guardados en el móvil";
            MeasurementSync.sincronizar(this);
        }
    }

    /** --------------------
     * Diseño lógico: medicion: MedicionTemperatura --> alPrepararTemperatura() -->
     * Descripción: Al Preparar Temperatura. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public void alPrepararTemperatura(MedicionTemperatura medicion) {
        if (!caducada()) {
            rutas.temperatura(actual, medicion);
            MeasurementSync.sincronizar(this);
        }
    }

    /** --------------------
     * Diseño lógico: detenerSensores() -->
     * Descripción: Detener Sensores. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    private void detenerSensores() {
        handler.removeCallbacksAndMessages(null);
        if(captura!=null) captura.detener();
        if(gps!=null) gps.detener();
        captura=null; gps=null; previa=null;
    }

    /** --------------------
     * Diseño lógico: onDestroy() -->
     * Descripción: On Destroy. Mantiene captura BLE/GPS y notificación durante el recorrido
     * -------------------- */
    @Override public void onDestroy() {
        detenerSensores();
        if(rutas!=null) rutas.close();
        actual=0; ultimoAnuncio=0; super.onDestroy();
    }
}
