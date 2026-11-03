/*
 * Nombre: MedicionSink.java
 * Descripción: Punto de conexión futuro con el módulo REST autenticado.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: Contrato no dependiente de endpoints ni credenciales inventadas.
 */
package com.example.ozora.senda.integration;

import com.example.ozora.senda.model.MedicionAmbiental;

public interface MedicionSink {
    /** Diseño lógico: (MedicionAmbiental) -> enviar() -> (void).
     * Descripción: Implementación futura: transmisión SOLO tras autorización y sesión válida. */
    void enviar(MedicionAmbiental medicion);
}
