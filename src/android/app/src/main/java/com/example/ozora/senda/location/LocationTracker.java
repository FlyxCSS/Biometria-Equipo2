/*
 * Nombre: LocationTracker.java
 * Descripción: Recibe posiciones mediante GPS y red, sin registrar trayectos.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: Ubicación Android reutilizable por el módulo de rutas.
 */
package com.example.ozora.senda.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

/** Obtención de ubicaciones; la Activity gestiona los permisos y el ciclo de vida. */
public final class LocationTracker implements LocationListener {
    public interface Oyente {
        // Diseño lógico: (Location) -> alRecibirUbicacion() -> (void)
        void alRecibirUbicacion(Location ubicacion);
        // Diseño lógico: (String) -> alOcurrirError() -> (void)
        void alOcurrirError(String mensaje);
    }

    private final Context contexto;
    private final LocationManager gestor;
    private final Oyente oyente;
    private boolean activo;

    // Diseño lógico: (Context, Oyente) -> LocationTracker() -> (LocationTracker)
    public LocationTracker(Context contexto, Oyente oyente) {
        this.contexto = contexto.getApplicationContext();
        this.oyente = oyente;
        this.gestor = (LocationManager) this.contexto.getSystemService(Context.LOCATION_SERVICE);
    }

    // Diseño lógico: () -> iniciar() -> (boolean)
    // Descripción: Escucha ubicaciones nuevas mediante GPS y red, si están habilitados.
    public boolean iniciar() {
        if (activo) return true;
        if (gestor == null) {
            oyente.alOcurrirError("Servicio de ubicación no disponible.");
            return false;
        }
        if (contexto.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            oyente.alOcurrirError("Concede ubicación precisa en los permisos de SENDA.");
            return false;
        }

        try {
            boolean gps = gestor.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean red = gestor.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!gps && !red) {
                oyente.alOcurrirError("Activa Ubicación en los ajustes del móvil.");
                return false;
            }

            // La red suele ofrecer una primera ubicación más rápidamente en interiores.
            if (red) {
                gestor.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        2000L, 2f, this, Looper.getMainLooper());
                activo = true;
            }
            if (gps) {
                gestor.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        2000L, 2f, this, Looper.getMainLooper());
                activo = true;
            }
            return activo;
        } catch (SecurityException ex) {
            detener();
            oyente.alOcurrirError("Android ha denegado el permiso de ubicación precisa.");
            return false;
        } catch (IllegalArgumentException ex) {
            detener();
            oyente.alOcurrirError("No se pudo iniciar el proveedor de ubicación.");
            return false;
        }
    }

    // Diseño lógico: () -> detener() -> (void)
    // Descripción: Deja de recibir actualizaciones de todos los proveedores.
    public void detener() {
        if (gestor != null) {
            try { gestor.removeUpdates(this); }
            catch (SecurityException ignored) { }
        }
        activo = false;
    }

    // Diseño lógico: (Location) -> onLocationChanged() -> (void)
    // Descripción: Entrega ubicaciones nuevas sin simular ni reutilizar posiciones antiguas.
    @Override public void onLocationChanged(Location ubicacion) {
        if (activo && ubicacion != null) oyente.alRecibirUbicacion(ubicacion);
    }

    // Diseño lógico: (String) -> onProviderDisabled() -> (void)
    // Descripción: Informa cuando ambos proveedores dejan de estar disponibles.
    @Override public void onProviderDisabled(String proveedor) {
        boolean gps = gestor.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean red = gestor.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps && !red) oyente.alOcurrirError("Ubicación desactivada. Actívala en Ajustes.");
    }
}
