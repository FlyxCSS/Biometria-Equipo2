/*
 * Nombre: PermissionsActivity.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import androidx.core.app.NotificationManagerCompat;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.SessionManager;

/** Información de permisos reales. Android conserva el control de su autorización. */
public final class PermissionsActivity extends FieldScreen {
    private boolean notifications;
    private SessionManager session;

    /** --------------------
     * Diseño lógico: state: EstadoVista --> onCreate() -->
     * Descripción: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        session = SessionManager.current();
        if (session == null) { finish(); return; }
        notifications = getIntent().getBooleanExtra("notifications", false);
        setContentView(R.layout.activity_permissions);
        protegerBarras(findViewById(R.id.permissions_root));
        findViewById(R.id.permissions_back).setOnClickListener(v -> finish());
        text(R.id.permissions_title, notifications ? "Notificaciones" : "Ubicación");
        text(R.id.permissions_subtitle, notifications ? "Los avisos de tu recorrido" : "Cada lectura, en su lugar");
        text(R.id.permissions_explanation, notifications
                ? "SENDA muestra el estado del recorrido y los avisos de captura. Puedes revisar los canales de notificación en Android."
                : "La ubicación precisa permite dibujar tu recorrido y asociar las mediciones a su posición. Se utiliza mientras registras un recorrido, también con la pantalla apagada.");
        findViewById(R.id.permissions_enable).setOnClickListener(v -> enable());
        findViewById(R.id.permissions_system).setOnClickListener(v -> system());
    }

    /** --------------------
     * Diseño lógico: onResume() -->
     * Descripción: On Resume. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onResume() {
        super.onResume();
        if (session == null || SessionManager.current() != session) { finish(); return; }
        refresh();
    }

    /** --------------------
     * Diseño lógico: permission: Text --> granted() -->
     * B <--
     * Descripción: Granted. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private boolean granted(String permission) { return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED; }

    /** --------------------
     * Diseño lógico: gps() -->
     * B <--
     * Descripción: Gps. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private boolean gps() {
        LocationManager manager = (LocationManager)getSystemService(LOCATION_SERVICE);
        return manager != null && (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    /** --------------------
     * Diseño lógico: id: Z, value: Text --> text() -->
     * Descripción: Text. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void text(int id, String value) { ((TextView)findViewById(id)).setText(value); }

    /** --------------------
     * Diseño lógico: refresh() -->
     * Descripción: Refresh. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void refresh() {
        boolean ready = notifications ? NotificationManagerCompat.from(this).areNotificationsEnabled()
                : granted(Manifest.permission.ACCESS_FINE_LOCATION) && gps();
        text(R.id.permissions_status, ready ? "Activadas" : notifications ? "Desactivadas"
                : granted(Manifest.permission.ACCESS_FINE_LOCATION) ? "Ubicación del móvil apagada"
                : granted(Manifest.permission.ACCESS_COARSE_LOCATION) ? "Solo ubicación aproximada" : "Permiso pendiente");
        ((TextView)findViewById(R.id.permissions_status)).setTextColor(getColor(ready ? R.color.status_success : R.color.status_neutral));
        text(R.id.permissions_description, notifications
                ? ready ? "Android permite las notificaciones de SENDA. Revisa los canales si no recibes algún aviso."
                        : "Los avisos están desactivados. Puedes permitirlos para consultar el estado del recorrido."
                : ready ? "La ubicación precisa está permitida y el servicio de ubicación está encendido."
                        : "Para registrar el recorrido, permite la ubicación precisa y activa la ubicación del móvil.");
        text(R.id.permissions_enable, notifications ? "Permitir notificaciones"
                : granted(Manifest.permission.ACCESS_FINE_LOCATION) ? "Activar ubicación del móvil" : "Permitir ubicación precisa");
        findViewById(R.id.permissions_enable).setVisibility(ready ? View.GONE : View.VISIBLE);
    }

    /** --------------------
     * Diseño lógico: enable() -->
     * Descripción: Enable. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void enable() {
        if (notifications) {
            if (Build.VERSION.SDK_INT >= 33 && !granted(Manifest.permission.POST_NOTIFICATIONS))
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            else system();
        } else if (!granted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else open(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    /** --------------------
     * Diseño lógico: system() -->
     * Descripción: System. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void system() {
        open(notifications && Build.VERSION.SDK_INT >= 26
                ? new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                : new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null)));
    }

    /** --------------------
     * Diseño lógico: intent: Navegacion --> open() -->
     * Descripción: Open. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void open(Intent intent) {
        try { startActivity(intent); }
        catch (android.content.ActivityNotFoundException e) { text(R.id.permissions_feedback, "Abre Ajustes de Android → Aplicaciones → SENDA para revisar el permiso."); }
    }

    /** --------------------
     * Diseño lógico: code: Z, permissions: [Dato], results: [Dato] --> onRequestPermissionsResult() -->
     * Descripción: On Request Permissions Result. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override public void onRequestPermissionsResult(int code, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(code, permissions, results);
        if (SessionManager.current() != session) { finish(); return; }
        refresh();
        boolean allowed = notifications ? NotificationManagerCompat.from(this).areNotificationsEnabled()
                : granted(Manifest.permission.ACCESS_FINE_LOCATION);
        text(R.id.permissions_feedback, allowed ? "Permiso actualizado."
                : "El permiso sigue desactivado. Si Android ya no lo pregunta, puedes cambiarlo en sus ajustes.");
    }
}
