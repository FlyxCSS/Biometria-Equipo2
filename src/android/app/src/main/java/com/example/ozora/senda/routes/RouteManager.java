/*
 * Nombre: RouteManager.java
 * Descripción: Recorridos y lecturas SQLite separados por trabajador y parque.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Equipo Android y Julia (integración con asistencia de Codex).
 * Aportación: Migrar la cola REST sin borrar recorridos anteriores.
 */
package com.example.ozora.senda.routes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.SystemClock;
import com.example.ozora.senda.model.*;
import com.example.ozora.senda.network.MeasurementRepository;
import java.util.ArrayList;
import java.util.List;

public final class RouteManager extends SQLiteOpenHelper {
    public RouteManager(Context context) { this(context, com.example.ozora.senda.auth.SessionManager.isDemo() ? "recorridos-demo.db" : "recorridos.db"); }
    public RouteManager(Context context,String archivo) { super(context,archivo,null,2); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ruta(id INTEGER PRIMARY KEY, usuario INTEGER NOT NULL, parque INTEGER NOT NULL, sensor TEXT, inicio INTEGER NOT NULL, fin INTEGER, metros REAL DEFAULT 0)");
        db.execSQL("CREATE UNIQUE INDEX ruta_activa ON ruta(usuario) WHERE fin IS NULL");
        db.execSQL("CREATE TABLE punto(id INTEGER PRIMARY KEY, ruta INTEGER, lat REAL, lon REAL, fecha INTEGER, segmento INTEGER)");
        db.execSQL("CREATE INDEX puntos_ruta ON punto(ruta,id)");
        db.execSQL("CREATE TABLE lectura(id INTEGER PRIMARY KEY, ruta INTEGER, ppb INTEGER, fecha TEXT, lat REAL, lon REAL, contador INTEGER, nombre_micro TEXT, enviado INTEGER NOT NULL DEFAULT 0, error_envio TEXT)");
        db.execSQL("CREATE INDEX lecturas_ruta ON lectura(ruta,id)");
    }
    /** Diseño lógico: (SQLite, versión anterior, versión nueva) -> onUpgrade -> vacío.
     * Descripción: Conserva el histórico; las lecturas antiguas sin identidad no se envían automáticamente. */
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        if(oldVersion<2) {
            db.execSQL("ALTER TABLE lectura ADD COLUMN nombre_micro TEXT");
            db.execSQL("ALTER TABLE lectura ADD COLUMN enviado INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE lectura ADD COLUMN error_envio TEXT");
        }
    }

    public long iniciar(int usuario,int parque,String sensor) {
        ContentValues v = new ContentValues();
        v.put("usuario",usuario); v.put("parque",parque); v.put("sensor",sensor);
        v.put("inicio",System.currentTimeMillis());
        return getWritableDatabase().insertOrThrow("ruta",null,v);
    }
    public void finalizar(long id) {
        getWritableDatabase().execSQL("UPDATE ruta SET fin=MAX(inicio,?) WHERE id=? AND fin IS NULL",
                new Object[]{System.currentTimeMillis(),id});
    }
    static boolean util(Location p) {
        long edad = SystemClock.elapsedRealtimeNanos() - p.getElapsedRealtimeNanos();
        return p.hasAccuracy() && p.getAccuracy() >= 0 && p.getAccuracy() <= 50
                && edad >= 0 && edad <= 30_000_000_000L
                && Math.abs(p.getLatitude()) <= 90 && Math.abs(p.getLongitude()) <= 180;
    }
    public void punto(long id, Location p,long segmento,float distancia) {
        SQLiteDatabase db=getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO punto(ruta,lat,lon,fecha,segmento) VALUES (?,?,?,?,?)",
                    new Object[]{id,p.getLatitude(),p.getLongitude(),p.getTime(),segmento});
            db.execSQL("UPDATE ruta SET metros=metros+? WHERE id=?",new Object[]{distancia,id});
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }
    
    public void lectura(long id, MedicionAmbiental m) {
        MeasurementRepository.registrar(getWritableDatabase(), id, m);
    }

    public Cursor resumen(int usuario,int parque) {
        return getReadableDatabase().rawQuery("SELECT r.id,inicio,fin,metros,sensor,COUNT(l.id),AVG(ppb),MIN(ppb),MAX(ppb) FROM (SELECT * FROM ruta WHERE usuario=? AND parque=? ORDER BY id DESC LIMIT 1) r LEFT JOIN lectura l ON l.ruta=r.id GROUP BY r.id",
                new String[]{String.valueOf(usuario),String.valueOf(parque)});
    }
    public Cursor ultima(long ruta) {
        return MeasurementRepository.ultima(getReadableDatabase(),ruta);
    }
    /** Diseño lógico: (trabajador) -> estadoEnvios -> resumen de su cola.
     * Descripción: Cuenta solo lecturas con identidad del usuario y parque actuales. */
    public String estadoEnvios(Worker trabajador) {
        try(Cursor c=getReadableDatabase().rawQuery("SELECT SUM(CASE WHEN l.enviado=0 THEN 1 ELSE 0 END),"
                + "SUM(CASE WHEN l.enviado=2 THEN 1 ELSE 0 END) FROM lectura l JOIN ruta r ON r.id=l.ruta "
                + "WHERE r.usuario=? AND r.parque=? AND l.nombre_micro IS NOT NULL",
                new String[]{String.valueOf(trabajador.id),String.valueOf(trabajador.parkId)})) {
            c.moveToFirst();
            return c.getInt(0)+" pendientes · "+c.getInt(1)+" rechazadas (conservadas localmente)";
        }
    }
    /** Diseño lógico: (trabajador) -> reintentarEnvios -> vacío.
     * Descripción: Recupera rechazos de su propia cola tras corregir asignación o datos. */
    public void reintentarEnvios(Worker trabajador) {
        getWritableDatabase().execSQL("UPDATE lectura SET enviado=0,error_envio=NULL WHERE enviado=2 "
                + "AND ruta IN (SELECT id FROM ruta WHERE usuario=? AND parque=?)",
                new Object[]{trabajador.id,trabajador.parkId});
    }
    public List<RoutePoint> puntos(long ruta) {
        List<RoutePoint> puntos=new ArrayList<>();
        try(Cursor c=getReadableDatabase().rawQuery("SELECT lat,lon,segmento FROM punto WHERE ruta=? ORDER BY id", new String[]{String.valueOf(ruta)})) {
            while(c.moveToNext()) puntos.add(new RoutePoint(c.getDouble(0),c.getDouble(1),c.getLong(2)));
        }
        return puntos;
    }
    public Route ultimaRuta(Worker worker) {
        try (Cursor c = resumen(worker.id, worker.parkId)) {
            if (!c.moveToFirst()) return null;
            return new Route(c.getLong(0), c.getLong(1), c.isNull(2) ? null : c.getLong(2),
                    c.getDouble(3), c.getString(4), c.getInt(5), c.getDouble(6), c.getInt(7), c.getInt(8));
        }
    }
}
