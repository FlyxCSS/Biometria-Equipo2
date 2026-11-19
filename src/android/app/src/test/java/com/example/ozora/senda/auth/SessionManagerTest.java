/*
 * Nombre: SessionManagerTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.auth;

import com.example.ozora.senda.model.Worker;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class SessionManagerTest {

    /** --------------------
     * Diseño lógico: limpiar() -->
     * Descripción: Limpiar. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @After public void limpiar() { SessionManager.clear(); }

    /** --------------------
     * Diseño lógico: requiereCredencialYTrabajadorReal() -->
     * Descripción: Requiere Credencial YTrabajador Real. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void requiereCredencialYTrabajadorReal() {
        for (String token : new String[]{null, "", "  "}) {
            try {
                SessionManager.open(new Worker(1, 1, "Trabajador"), Long.MAX_VALUE, token);
                fail("Debe rechazar una sesión sin credencial");
            } catch (IllegalArgumentException expected) { assertNull(SessionManager.current()); }
        }
        try {
            SessionManager.open(new Worker(-1001, -2001, "Ficticio"), Long.MAX_VALUE, "token");
            fail("Debe rechazar identidades ficticias del acceso anterior");
        } catch (IllegalArgumentException expected) { assertNull(SessionManager.current()); }
    }

    /** --------------------
     * Diseño lógico: caducidadYCierreImpidenReutilizarLaSesion() -->
     * Descripción: Caducidad YCierre Impiden Reutilizar La Sesion. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void caducidadYCierreImpidenReutilizarLaSesion() {
        SessionManager.open(new Worker(1, 1, "Trabajador"), System.currentTimeMillis()-1, "token");
        assertNull(SessionManager.current());
        SessionManager.open(new Worker(1, 1, "Trabajador"), Long.MAX_VALUE, "token");
        assertNotNull(SessionManager.current());
        SessionManager.clear();
        assertNull(SessionManager.current());
    }

    /** --------------------
     * Diseño lógico: consultarLaSesionConservaLaIdentidadHastaCerrar() -->
     * Descripción: Consultar La Sesion Conserva La Identidad Hasta Cerrar. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void consultarLaSesionConservaLaIdentidadHastaCerrar() {
        SessionManager.open(new Worker(1, 1, "Trabajador"), Long.MAX_VALUE, "token");
        assertSame(SessionManager.current(), SessionManager.current());
        SessionManager.clear();
        assertNull(SessionManager.current());
    }
}
