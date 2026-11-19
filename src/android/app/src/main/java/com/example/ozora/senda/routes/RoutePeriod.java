/*
 * Nombre: RoutePeriod.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.routes;

import java.util.Calendar;
import java.util.TimeZone;

/** Intervalos locales, con fin exclusivo para evitar duplicar las rutas de medianoche. */
public enum RoutePeriod {
    TODAY, YESTERDAY, THIS_WEEK, LAST_SEVEN_DAYS;


    /** --------------------
     * Diseño lógico: now: Z, zone: TimeZone --> bounds() -->
     * [Dato] <--
     * Descripción: Bounds. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public long[] bounds(long now, TimeZone zone) {
        Calendar start=Calendar.getInstance(zone);
        start.setTimeInMillis(now);
        start.set(Calendar.HOUR_OF_DAY,0);
        start.set(Calendar.MINUTE,0);
        start.set(Calendar.SECOND,0);
        start.set(Calendar.MILLISECOND,0);
        Calendar end=(Calendar)start.clone();
        end.add(Calendar.DAY_OF_MONTH,1);
        if(this==YESTERDAY) {
            end=(Calendar)start.clone();
            start.add(Calendar.DAY_OF_MONTH,-1);
        } else if(this==THIS_WEEK) {
            int days=(start.get(Calendar.DAY_OF_WEEK)+5)%7;
            start.add(Calendar.DAY_OF_MONTH,-days);
            end=(Calendar)start.clone();
            end.add(Calendar.DAY_OF_MONTH,7);
        } else if(this==LAST_SEVEN_DAYS) start.add(Calendar.DAY_OF_MONTH,-6);
        return new long[]{start.getTimeInMillis(),end.getTimeInMillis()};
    }
}
