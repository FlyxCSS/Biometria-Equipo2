/*
 * Nombre: MeasurementRepository.java
 * Descripción: Persistencia local de lecturas y su estado de envío.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Equipo Android y Julia (integración con asistencia de Codex).
 * Aportación: Guardar identidad del sensor y estado pendiente antes de transmitir.
 */
package com.example.ozora.senda.network;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ozora.senda.model.MedicionAmbiental;

/** Lecturas locales del recorrido, pendientes de validación por el servidor. */
public final class MeasurementRepository {
/** Diseño lógico: (SQLite, ruta, medición) -> registrar -> vacío.
 * Descripción: Guarda lectura e identidad BLE con estado pendiente en una sola inserción. */
public static void registrar(
        SQLiteDatabase db,
        long id,
        MedicionAmbiental m) {

    db.execSQL(
        "INSERT INTO lectura(ruta,ppb,fecha,lat,lon,contador,nombre_micro,enviado) VALUES (?,?,?,?,?,?,?,0)",
        new Object[]{
            id,
            m.valorPpb,
            m.fechaIso8601,
            m.latitud,
            m.longitud,
            m.contador,
            m.idLlavero
        }
    );
}

    /** Diseño lógico: (SQLite, ruta) -> ultima -> cursor.
     * Descripción: Conserva la consulta local utilizada por las estadísticas. */
    public static Cursor ultima(SQLiteDatabase db, long ruta) {
        return db.rawQuery("SELECT ppb,fecha FROM lectura WHERE ruta=? ORDER BY id DESC LIMIT 1", new String[]{String.valueOf(ruta)});
    }
}
