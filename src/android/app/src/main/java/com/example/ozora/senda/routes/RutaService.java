/*
 * Nombre: RutaService.java
 * Descripción: Captura BLE/GPS durante un recorrido y solicita su sincronización.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Equipo Android y Julia (integración con asistencia de Codex).
 * Aportación: Mantener el guardado local antes del envío REST.
 */
package com.example.ozora.senda.routes;

import com.example.ozora.senda.ui.HomeActivity;
import android.app.*;
import android.content.*;
import android.location.Location;
import android.os.*;
import com.example.ozora.senda.integration.CapturaLlaveroController;
import com.example.ozora.senda.location.LocationTracker;
import com.example.ozora.senda.model.MedicionAmbiental;
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
    @Override public IBinder onBind(Intent i) { return null; }
    @Override public int onStartCommand(Intent i,int flags,int startId) {
        com.example.ozora.senda.auth.SessionManager sesion=com.example.ozora.senda.auth.SessionManager.current();
        if(sesion==null) { stopSelf(); return START_NOT_STICKY; }
        if(i==null) { stopSelf(); return START_NOT_STICKY; }
        if(actual!=0) {
            if(actual!=i.getLongExtra("ruta",0) || !i.getBooleanExtra("reanudar",false)) return START_NOT_STICKY;
            detenerSensores();
        }
        if(Build.VERSION.SDK_INT>=26) getSystemService(NotificationManager.class).createNotificationChannel(
                new NotificationChannel("campo","Recorrido en curso",NotificationManager.IMPORTANCE_LOW));
        Notification.Builder n=Build.VERSION.SDK_INT>=26 ? new Notification.Builder(this,"campo") : new Notification.Builder(this);
        Intent home=new Intent(this,HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try { startForeground(31,n.setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle(sesion.demo ? "SENDA · DEMOSTRACIÓN DEBUG" : "SENDA · Recorrido en curso")
            .setContentText("Abre la app para ver o finalizar el recorrido.").setOngoing(true)
                        .setContentIntent(PendingIntent.getActivity(this,0,home,PendingIntent.FLAG_IMMUTABLE)).build());
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
    private boolean caducada() {
        if(actual==0) return true;
        if(System.currentTimeMillis()<vence) return false;
        rutas.finalizar(actual); actual=0; estado="La sesión ha caducado."; stopSelf(); return true;
    }
    @Override public void alCambiarEstado(String s) { if(actual!=0) estado=s; }
    @Override public void alOcurrirError(String s) { alCambiarEstado(s); }
    @Override public void alDescartarMedicion(String s) { alCambiarEstado(s); }
    @Override public void alRecibirAnuncio() { if(actual!=0) ultimoAnuncio=SystemClock.elapsedRealtime(); }
    @Override public void alRecibirUbicacion(Location p) {
        if(caducada() || !RouteManager.util(p)) return;
        if(previa!=null && p.getElapsedRealtimeNanos()<=previa.getElapsedRealtimeNanos()) return;
        float distancia=previa==null ? 0 : previa.distanceTo(p);
        if(previa!=null && distancia<2) return;
        rutas.punto(actual,p,segmento,distancia); previa=new Location(p);
    }
    @Override public void alPrepararMedicion(MedicionAmbiental m,int rssi) {
        if(!caducada()) {
            rutas.lectura(actual,m);
            estado="GPS y lectura guardados en el móvil";
            MeasurementSync.sincronizar(this);
        }
    }
    private void detenerSensores() {
        handler.removeCallbacksAndMessages(null);
        if(captura!=null) captura.detener();
        if(gps!=null) gps.detener();
        captura=null; gps=null; previa=null;
    }
    @Override public void onDestroy() {
        detenerSensores();
        if(rutas!=null) rutas.close();
        actual=0; ultimoAnuncio=0; super.onDestroy();
    }
}
