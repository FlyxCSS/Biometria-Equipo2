/*
 * Nombre: KeyringStatus.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.ui;

/** Un llavero seleccionado no implica estar recibiendo sus anuncios BLE. */
public final class KeyringStatus {

    /** --------------------
     * Diseño lógico: KeyringStatus() -->
     * KeyringStatus <--
     * Descripción: Construye KeyringStatus. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private KeyringStatus() {}

    /** --------------------
     * Diseño lógico: active: B, route: Z, captureRoute: Z, lastAdvertisement: Z, now: Z, timeoutSeconds: Z --> receiving() --x
     * B <--
     * Descripción: Receiving. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public static boolean receiving(boolean active, long route, long captureRoute,
                                    long lastAdvertisement, long now, int timeoutSeconds) {
        long age = now - lastAdvertisement;
        return active && route > 0 && route == captureRoute && lastAdvertisement > 0
                && age >= 0 && age < timeoutSeconds * 1000L;
    }
}
