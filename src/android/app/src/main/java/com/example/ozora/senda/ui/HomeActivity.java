/*
 * Nombre: HomeActivity.java
 * Descripción: Panel del trabajador, recorridos y estado de sincronización.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Equipo Android y Julia (integración con asistencia de Codex).
 * Aportación: Informar de lecturas locales y confirmadas y reintentar las pendientes.
 */
package com.example.ozora.senda.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.os.Bundle;
import android.os.Build;
import android.os.SystemClock;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.auth.LoginActivity;
import com.example.ozora.senda.auth.BiometricHelper;
import com.example.ozora.senda.model.Route;
import com.example.ozora.senda.network.ApiClient;
import com.example.ozora.senda.network.MeasurementSync;
import com.example.ozora.senda.qr.QrScannerActivity;
import com.example.ozora.senda.routes.RouteActivity;
import com.example.ozora.senda.routes.RutaService;
import com.example.ozora.senda.routes.RouteManager;
import com.example.ozora.senda.statistics.StatisticsCalculator;
import com.example.ozora.senda.statistics.EnvironmentalAssessment;

public final class HomeActivity extends FieldScreen {
    private SessionManager session;
    private RouteManager rutas;
    private BiometricHelper bio;
    private TextView ultima, dispositivo, resumen, aviso, sincronizacion;
    private Button accion, seleccion;
    private boolean activo;
    private long id;
    private String sensorRuta;
    private long ultimoEnvio;
    private final Handler reloj=new Handler(Looper.getMainLooper());
    private final Runnable tick=new Runnable() { public void run() {
        if(SessionManager.current()==null) { salir(false); return; }
        if(!session.demo && SystemClock.elapsedRealtime()-ultimoEnvio>=15000) {
            ultimoEnvio=SystemClock.elapsedRealtime();
            MeasurementSync.sincronizar(HomeActivity.this);
        }
        actualizar(); reloj.postDelayed(this,1000);
    }};
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        session=SessionManager.current();
        if(session==null) { acceso("Vuelve a identificarte."); return; }
        rutas=new RouteManager(this); bio=new BiometricHelper(this); panel();
    }
    private void acceso(String mensaje) {
        startActivity(new Intent(this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK).putExtra("mensaje",mensaje));
        finish();
    }
    @Override protected void onStart() { super.onStart(); if(session!=null) reloj.post(tick); }
    @Override protected void onStop() { reloj.removeCallbacks(tick); super.onStop(); }
    @Override protected void onDestroy() {
        if(rutas!=null) rutas.close();
        if(isFinishing() && !activo && session==SessionManager.current()) SessionManager.clear();
        super.onDestroy();
    }
    private SharedPreferences ajustes() { return getSharedPreferences("campo-"+session.worker.id+"-"+session.worker.parkId,MODE_PRIVATE); }
    private String elegido() { return ajustes().getString("sensor",null); }
    private void panel() {
        pagina("Cuaderno de campo"); if(session.demo) texto("DEMOSTRACIÓN DEBUG · Trabajador y parque ficticios · Datos locales de prueba",16,true); texto(session.worker.name+"  ·  Parque "+session.worker.parkId,17,false);
        ultima=tarjeta("Última medición de O₃",23); dispositivo=tarjeta("Llavero y señal BLE",16);
        aviso=texto("",16,true); aviso.setTextColor(0xff8a4919); resumen=tarjeta("Resumen del recorrido",17);
        sincronizacion=tarjeta("Envío al servidor",15);
        if(!session.demo) boton("Reintentar envíos pendientes",()->{
            rutas.reintentarEnvios(session.worker);
            MeasurementSync.sincronizar(this);
            actualizar();
        });
        accion=boton("",()->{ if(activo) confirmarFin(); else preparar(); });
        boton("Ver mapa del recorrido",()->startActivity(new Intent(this,RouteActivity.class)));
        boton("Consultar resumen",()->startActivity(new Intent(this,SummaryActivity.class)));
        seleccion=boton("Seleccionar llavero",this::seleccionar);
        boton("Ajustes",this::configurar); boton("Cerrar sesión",()->{
            new AlertDialog.Builder(this).setMessage(activo?"Al cerrar sesión finalizará el recorrido.":"¿Cerrar la sesión en este dispositivo?")
                .setNegativeButton("Volver",null).setPositiveButton("Cerrar sesión",(d,w)->salir(true)).show();
        });
        mensajes(); actualizar();
    }
    private void actualizar() {
        if(session==null || resumen==null) return;
        sincronizacion.setText(session.demo ? "Demostración: las lecturas permanecen en el móvil, sin envío REST."
                : rutas.estadoEnvios(session.worker)+"\n"+MeasurementSync.estado);
        Route route=rutas.ultimaRuta(session.worker);
        id=route==null ? 0 : route.id;
        activo=route!=null && route.end==null;
        sensorRuta=activo ? route.sensor : elegido();
        accion.setText(activo?"Finalizar recorrido":"Iniciar recorrido"); seleccion.setEnabled(!activo);
        String estado=sensorRuta==null?"Sin llavero seleccionado":"Selección local · "+sensorRuta;
        if(activo && RutaService.actual==id) {
            long referencia=RutaService.ultimoAnuncio==0?RutaService.desde:RutaService.ultimoAnuncio;
            if(sensorRuta!=null) {
                long edad=(SystemClock.elapsedRealtime()-referencia)/1000;
                estado+="\n"+(edad>=ajustes().getInt("silencio",20)?"Sin anuncios BLE · "+edad+" s":
                        RutaService.ultimoAnuncio==0?"Buscando anuncios…":"Recibiendo anuncios BLE");
            }
            estado+="\n"+RutaService.estado;
        } else if(activo) estado+="\nCaptura interrumpida. Puedes reanudarla en Ajustes.";
        dispositivo.setText(estado);
        ultima.setText("Sin mediciones"); aviso.setText(""); aviso.setVisibility(View.GONE);
        resumen.setText(StatisticsCalculator.resumen(route,System.currentTimeMillis()));
        if(route==null) return;
        DateFormat fmt=DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
        try(Cursor m=rutas.ultima(id)) {
            if(m.moveToFirst()) {
                int ppb=m.getInt(0); String fecha=m.getString(1);
                try { fecha=fmt.format(new Date(SessionManager.instante(fecha))); } catch(ParseException ignored) {}
                ultima.setText(ppb+" ppb\n"+fecha+" · "+route.sensor);
                String warning=EnvironmentalAssessment.aviso(ppb,ajustes().getInt("umbral",60));
                aviso.setText(warning);
                aviso.setVisibility(warning.isEmpty() ? View.GONE : View.VISIBLE);
            }
        }
    }
    private void seleccionar() {
        startActivityForResult(new Intent(this,QrScannerActivity.class).putExtra("sensor",elegido()),20);
    }
    @Override protected void onActivityResult(int req,int result,Intent data) {
        super.onActivityResult(req,result,data);
        if(req==20 && result==RESULT_OK && data!=null && !activo && SessionManager.current()!=null) {
            ajustes().edit().putString("sensor",data.getStringExtra("sensor")).apply();
            actualizar();
        }
    }
    private void configurar() {
        List<String> opciones=new ArrayList<>();
        opciones.add(session.demo?"Biometría no disponible en demostración":bio.activo()?"Desactivar biometría":"Activar biometría");
        opciones.add("Umbral orientativo de ozono"); opciones.add("Tiempo sin anuncios BLE");
        if(activo) opciones.add("Reanudar captura");
        new AlertDialog.Builder(this).setTitle("Ajustes de campo").setItems(opciones.toArray(new String[0]),(d,w)->{
            if(w==0) {
                if(session.demo) { mensaje("La demostración no tiene credenciales ni acceso biométrico."); return; }
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
            requestPermissions(permisos.toArray(new String[0]),19); return;
        }
        iniciar();
    }
    @Override public void onRequestPermissionsResult(int req,String[] permisos,int[] resultados) {
        super.onRequestPermissionsResult(req,permisos,resultados);
        if(req!=19 || SessionManager.current()==null) return;
        for(int r:resultados) if(r!=PackageManager.PERMISSION_GRANTED) { mensaje("Para registrar el recorrido permite la ubicación precisa y, si usas llavero, los dispositivos cercanos en Ajustes."); return; }
        if(resultados.length>0) iniciar();
    }
    private void iniciar() {
        if(System.currentTimeMillis()>=session.expiresAt) { salir(false); return; }
        if(!activo) {
            sensorRuta=elegido();
            try { id=rutas.iniciar(session.worker.id,session.worker.parkId,sensorRuta); }
            catch(android.database.sqlite.SQLiteConstraintException ex) { mensaje("Ya existe un recorrido activo para esta cuenta."); actualizar(); return; }
        }
        Intent i=new Intent(this,RutaService.class).putExtra("ruta",id).putExtra("sensor",sensorRuta)
                .putExtra("vence",session.expiresAt).putExtra("reanudar",activo);
        try {
            if(Build.VERSION.SDK_INT>=26) startForegroundService(i); else startService(i);
            mensaje("");
        } catch(RuntimeException e) { mensaje("Android no pudo iniciar la captura. Revisa los permisos y reanúdala desde Ajustes."); }
        actualizar();
    }
    private void confirmarFin() {
        new AlertDialog.Builder(this).setTitle("¿Finalizar recorrido?").setMessage("Se cerrará la captura y podrás consultar el resumen y el mapa.")
            .setPositiveButton("Finalizar",(d,w)->{ finalizar(); actualizar(); }).setNegativeButton("Continuar",null).show();
    }
    private void finalizar() {
        if(activo) {
            RutaService.actual=0; stopService(new Intent(this,RutaService.class)); rutas.finalizar(id); activo=false;
        }
    }
    private void salir(boolean revocar) {
        String anterior=session.token;
        finalizar(); if(!session.demo) bio.borrar(); SessionManager.clear();
        if(revocar && !session.demo) {
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
