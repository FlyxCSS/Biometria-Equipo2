package com.example.ozora.senda.network;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ozora.senda.model.MedicionAmbiental;

/** Lecturas locales del recorrido, pendientes de validación por el servidor. */
public final class MeasurementRepository {
    
public static void registrar(
        SQLiteDatabase db,
        long id,
        MedicionAmbiental m) {

    db.execSQL(
        "INSERT INTO lectura(ruta,ppb,fecha,lat,lon,contador) VALUES (?,?,?,?,?,?)",
        new Object[]{
            id,
            m.valorPpb,
            m.fechaIso8601,
            m.latitud,
            m.longitud,
            m.contador
        }
    );
}

    public static Cursor ultima(SQLiteDatabase db, long ruta) {
        return db.rawQuery("SELECT ppb,fecha FROM lectura WHERE ruta=? ORDER BY id DESC LIMIT 1", new String[]{String.valueOf(ruta)});
    }
}
