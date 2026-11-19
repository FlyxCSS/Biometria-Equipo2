/*
 * Nombre: SendaApplication.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda;

import android.app.Application;
import com.example.ozora.senda.auth.SessionManager;

/** Restaura la sesión antes de abrir actividades o servicios. */
public final class SendaApplication extends Application {

    /** --------------------
     * Diseño lógico: onCreate() -->
     * Descripción: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override public void onCreate() {
        super.onCreate();
        SessionManager.initialize(this);
    }
}
