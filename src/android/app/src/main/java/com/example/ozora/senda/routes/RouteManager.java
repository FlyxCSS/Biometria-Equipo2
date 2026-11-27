/*
 * Nombre: RouteManager.java
 * Descripción: Persiste recorridos, posiciones, O3 y temperatura en SQLite.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Persiste recorridos, posiciones, O3 y temperatura en SQLite.
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
import java.util.ArrayList;
import java.util.List;

public final class RouteManager extends SQLiteOpenHelper {

    /** --------------------
     * Diseño lógico: context: Entorno --> RouteManager() -->
     * RouteManager <--
     * Descripción: Construye RouteManager. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public RouteManager(Context context) { this(context, "recorridos.db"); }

    /** --------------------
     * Diseño lógico: context: Entorno, archivo: Text --> RouteManager() -->
     * RouteManager <--
     * Descripción: Construye RouteManager. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public RouteManager(Context context,String archivo) { super(context,archivo,null,5); }

    /** --------------------
     * Diseño lógico: db: DepositoLocal --> onCreate() -->
     * Descripción: On Create. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ruta(id INTEGER PRIMARY KEY, usuario INTEGER NOT NULL, parque INTEGER NOT NULL, sensor TEXT, inicio INTEGER NOT NULL, fin INTEGER, metros REAL DEFAULT 0)");
        db.execSQL("CREATE UNIQUE INDEX ruta_activa ON ruta(usuario) WHERE fin IS NULL");
        db.execSQL("CREATE TABLE punto(id INTEGER PRIMARY KEY, ruta INTEGER, lat REAL, lon REAL, fecha INTEGER, segmento INTEGER)");
        db.execSQL("CREATE INDEX puntos_ruta ON punto(ruta,id)");
        db.execSQL("CREATE TABLE lectura(id INTEGER PRIMARY KEY, ruta INTEGER, ppb INTEGER, fecha TEXT, lat REAL, lon REAL, contador INTEGER, nombre_micro TEXT, enviado INTEGER NOT NULL DEFAULT 0, error_envio TEXT)");
        db.execSQL("CREATE INDEX lecturas_ruta ON lectura(ruta,id)");
        crearTemperaturas(db);
    }


    /** --------------------
     * Diseño lógico: db: DepositoLocal, oldVersion: Z, newVersion: Z --> onUpgrade() -->
     * Descripción: Conserva el histórico; las lecturas antiguas sin identidad no se envían automáticamente
     * -------------------- */
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        if(oldVersion<3) crearTemperaturas(db);
        if(oldVersion<2) {
            db.execSQL("ALTER TABLE lectura ADD COLUMN nombre_micro TEXT");
            db.execSQL("ALTER TABLE lectura ADD COLUMN enviado INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE lectura ADD COLUMN error_envio TEXT");
        }
        if(oldVersion>=3 && oldVersion<4) {
            db.execSQL("ALTER TABLE temperatura ADD COLUMN nombre_micro TEXT");
            db.execSQL("ALTER TABLE temperatura ADD COLUMN enviado INTEGER NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE temperatura ADD COLUMN error_envio TEXT");
        }
        if(oldVersion>=3 && oldVersion<5) {
            db.execSQL("ALTER TABLE temperatura ADD COLUMN lat REAL");
            db.execSQL("ALTER TABLE temperatura ADD COLUMN lon REAL");
        }
    }


    /** --------------------
     * Diseño lógico: db: DepositoLocal --> crearTemperaturas() -->
     * Descripción: Crear Temperaturas. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    private void crearTemperaturas(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE temperatura(id INTEGER PRIMARY KEY, ruta INTEGER NOT NULL, valor INTEGER NOT NULL, fecha TEXT NOT NULL, lat REAL, lon REAL, nombre_micro TEXT, enviado INTEGER NOT NULL DEFAULT 0, error_envio TEXT)");
        db.execSQL("CREATE INDEX temperaturas_ruta ON temperatura(ruta,id)");
    }

    /** --------------------
     * Diseño lógico: ruta: Z, m: MedicionTemperatura --> temperatura() -->
     * Descripción: Temperatura. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public void temperatura(long ruta, MedicionTemperatura m) {
        if (m.centesimas < -4000 || m.centesimas > 5000
                || !Double.isFinite(m.latitud) || !Double.isFinite(m.longitud)
                || Math.abs(m.latitud)>90 || Math.abs(m.longitud)>180)
            throw new IllegalArgumentException("Temperatura o coordenadas inválidas");
        getWritableDatabase().execSQL("INSERT INTO temperatura(ruta,valor,fecha,nombre_micro,lat,lon) VALUES (?,?,?,?,?,?)",
                new Object[]{ruta,m.centesimas,m.fechaIso8601,m.idLlavero,m.latitud,m.longitud});
    }

    /** --------------------
     * Diseño lógico: worker: Worker, ruta: Z, limite: Z, offset: Z --> temperaturasRuta() -->
     * Cursor <--
     * Descripción: Temperaturas Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor temperaturasRuta(Worker worker,long ruta,int limite,int offset) {
        return getReadableDatabase().rawQuery("SELECT t.valor,t.fecha,t.lat,t.lon FROM temperatura t JOIN ruta r ON r.id=t.ruta WHERE r.usuario=? AND r.parque=? AND r.id=? ORDER BY t.id LIMIT ? OFFSET ?",
            new String[]{""+worker.id,""+worker.parkId,""+ruta,""+limite,""+offset});
    }

    /** --------------------
     * Diseño lógico: usuario: Z, parque: Z, sensor: Text --> iniciar() -->
     * Z <--
     * Descripción: Iniciar. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public long iniciar(int usuario,int parque,String sensor) {
        ContentValues v = new ContentValues();
        v.put("usuario",usuario); v.put("parque",parque); v.put("sensor",sensor);
        v.put("inicio",System.currentTimeMillis());
        return getWritableDatabase().insertOrThrow("ruta",null,v);
    }

    /** --------------------
     * Diseño lógico: id: Z --> finalizar() -->
     * Descripción: Finalizar. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public void finalizar(long id) {
        getWritableDatabase().execSQL("UPDATE ruta SET fin=MAX(inicio,?) WHERE id=? AND fin IS NULL",
                new Object[]{System.currentTimeMillis(),id});
    }

    /** --------------------
     * Diseño lógico: p: Posicion --> util() --x
     * B <--
     * Descripción: Util. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    static boolean util(Location p) {
        long edad = SystemClock.elapsedRealtimeNanos() - p.getElapsedRealtimeNanos();
        return p.hasAccuracy() && p.getAccuracy() >= 0 && p.getAccuracy() <= 50
                && edad >= 0 && edad <= 30_000_000_000L
                && Math.abs(p.getLatitude()) <= 90 && Math.abs(p.getLongitude()) <= 180;
    }

    /** --------------------
     * Diseño lógico: id: Z, p: Posicion, segmento: Z, distancia: R --> punto() -->
     * Descripción: Punto. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
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


    /** --------------------
     * Diseño lógico: id: Z, m: MedicionAmbiental --> lectura() -->
     * Descripción: Lectura. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public void lectura(long id, MedicionAmbiental m) {
        getWritableDatabase().execSQL(
                "INSERT INTO lectura(ruta,ppb,fecha,lat,lon,contador,nombre_micro,enviado) VALUES (?,?,?,?,?,?,?,0)",
                new Object[]{id, m.valorPpb, m.fechaIso8601, m.latitud, m.longitud, m.contador, m.idLlavero});
    }


    /** --------------------
     * Diseño lógico: usuario: Z, parque: Z --> resumen() -->
     * Cursor <--
     * Descripción: Resumen. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor resumen(int usuario,int parque) {
        return getReadableDatabase().rawQuery("SELECT r.id,inicio,fin,metros,sensor,COUNT(l.id),AVG(ppb),MIN(ppb),MAX(ppb) FROM (SELECT * FROM ruta WHERE usuario=? AND parque=? ORDER BY (fin IS NULL) DESC,id DESC LIMIT 1) r LEFT JOIN lectura l ON l.ruta=r.id GROUP BY r.id",
                new String[]{String.valueOf(usuario),String.valueOf(parque)});
    }

    /** --------------------
     * Diseño lógico: ruta: Z --> ultima() -->
     * Cursor <--
     * Descripción: Ultima. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor ultima(long ruta) {
        return getReadableDatabase().rawQuery(
                "SELECT ppb,fecha FROM lectura WHERE ruta=? ORDER BY id DESC LIMIT 1",
                new String[]{String.valueOf(ruta)});
    }


    /** --------------------
     * Diseño lógico: trabajador: Worker --> estadoEnvios() -->
     * Text <--
     * Descripción: Cuenta solo lecturas con identidad del usuario y parque actuales
     * -------------------- */
    public String estadoEnvios(Worker trabajador) {
        try(Cursor c=getReadableDatabase().rawQuery("SELECT SUM(CASE WHEN l.enviado=0 THEN 1 ELSE 0 END),"
                + "SUM(CASE WHEN l.enviado=2 THEN 1 ELSE 0 END) FROM (SELECT ruta,nombre_micro,enviado FROM lectura UNION ALL SELECT ruta,nombre_micro,enviado FROM temperatura WHERE lat IS NOT NULL AND lon IS NOT NULL) l JOIN ruta r ON r.id=l.ruta "
                + "WHERE r.usuario=? AND r.parque=? AND l.nombre_micro IS NOT NULL",
                new String[]{String.valueOf(trabajador.id),String.valueOf(trabajador.parkId)})) {
            c.moveToFirst();
            return c.getInt(0)+" pendientes · "+c.getInt(1)+" rechazadas (conservadas localmente)";
        }
    }


    /** --------------------
     * Diseño lógico: trabajador: Worker --> reintentarEnvios() -->
     * Descripción: Recupera rechazos de su propia cola tras corregir asignación o datos
     * -------------------- */
    public void reintentarEnvios(Worker trabajador) {
        getWritableDatabase().execSQL("UPDATE lectura SET enviado=0,error_envio=NULL WHERE enviado=2 "
                + "AND ruta IN (SELECT id FROM ruta WHERE usuario=? AND parque=?)",
                new Object[]{trabajador.id,trabajador.parkId});
        getWritableDatabase().execSQL("UPDATE temperatura SET enviado=0,error_envio=NULL WHERE enviado=2 "
                + "AND ruta IN (SELECT id FROM ruta WHERE usuario=? AND parque=?)",
                new Object[]{trabajador.id,trabajador.parkId});
    }

    /** --------------------
     * Diseño lógico: ruta: Z --> puntos() -->
     * [Dato] <--
     * Descripción: Puntos. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public List<RoutePoint> puntos(long ruta) {
        List<RoutePoint> puntos=new ArrayList<>();
        try(Cursor c=getReadableDatabase().rawQuery("SELECT lat,lon,segmento FROM punto WHERE ruta=? ORDER BY id", new String[]{String.valueOf(ruta)})) {
            while(c.moveToNext()) puntos.add(new RoutePoint(c.getDouble(0),c.getDouble(1),c.getLong(2)));
        }
        return puntos;
    }
    /** Páginas de solo lectura, restringidas al trabajador y parque actuales. */

    /** --------------------
     * Diseño lógico: worker: Worker, limite: Z, desplazamiento: Z --> listar() -->
     * Cursor <--
     * Descripción: Listar. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor listar(Worker worker, int limite, int desplazamiento) {
        return getReadableDatabase().rawQuery(
                "SELECT id,inicio,fin FROM ruta WHERE usuario=? AND parque=? ORDER BY id DESC LIMIT ? OFFSET ?",
                new String[]{String.valueOf(worker.id), String.valueOf(worker.parkId),
                        String.valueOf(limite), String.valueOf(desplazamiento)});
    }

    /** Lecturas de la cuenta actual, agrupadas por posición y dentro del periodo UTC. */

    /** --------------------
     * Diseño lógico: worker: Worker, since: Text, until: Text --> puntosCalor() -->
     * Cursor <--
     * Descripción: Puntos Calor. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor puntosCalor(Worker worker, String since, String until) {
        return getReadableDatabase().rawQuery(
                "SELECT ROUND(l.lat,5),ROUND(l.lon,5),AVG(l.ppb) FROM lectura l JOIN ruta r ON r.id=l.ruta"
                + " WHERE r.usuario=? AND r.parque=? AND l.fecha>=? AND l.fecha<=?"
                + " AND l.lat BETWEEN -90 AND 90 AND l.lon BETWEEN -180 AND 180"
                + " GROUP BY ROUND(l.lat,5),ROUND(l.lon,5) ORDER BY MAX(l.fecha) DESC LIMIT 1500",
                new String[]{String.valueOf(worker.id),String.valueOf(worker.parkId),since,until});
    }


    /** --------------------
     * Diseño lógico: worker: Worker, since: Z, until: Z, limit: Z, offset: Z --> listarPeriodo() -->
     * Cursor <--
     * Descripción: Listar Periodo. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor listarPeriodo(Worker worker, long since, long until, int limit, int offset) {
        return getReadableDatabase().rawQuery(
                "SELECT r.id,r.inicio,r.fin,r.metros,(SELECT COUNT(*) FROM lectura l WHERE l.ruta=r.id),"
                + "(SELECT AVG(l.ppb) FROM lectura l WHERE l.ruta=r.id)"
                + " FROM ruta r WHERE r.usuario=? AND r.parque=? AND r.inicio>=? AND r.inicio<?"
                + " ORDER BY (r.fin IS NULL) DESC,r.inicio DESC,r.id DESC LIMIT ? OFFSET ?",
                new String[]{String.valueOf(worker.id),String.valueOf(worker.parkId),String.valueOf(since),
                    String.valueOf(until),String.valueOf(limit),String.valueOf(offset)});
    }

    /** Muestreo acotado de puntos reales para la miniatura, conservando inicio y final. */

    /** --------------------
     * Diseño lógico: worker: Worker, routeId: Z --> vistaPrevia() -->
     * [Dato] <--
     * Descripción: Vista Previa. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public List<RoutePoint> vistaPrevia(Worker worker,long routeId) {
        List<RoutePoint> points=new ArrayList<>();
        try(Cursor cursor=getReadableDatabase().rawQuery(
                "SELECT p.lat,p.lon,p.segmento FROM punto p JOIN ruta r ON r.id=p.ruta"
                + " WHERE r.id=? AND r.usuario=? AND r.parque=? ORDER BY p.id",
                new String[]{String.valueOf(routeId),String.valueOf(worker.id),String.valueOf(worker.parkId)})) {
            int count=cursor.getCount();
            if(count==0) return points;
            int step=Math.max(1,(int)Math.ceil((count-1)/149.0));
            for(int index=0;index<count;index+=step) {
                cursor.moveToPosition(index);
                points.add(new RoutePoint(cursor.getDouble(0),cursor.getDouble(1),cursor.getLong(2)));
            }
            if((count-1)%step!=0) {
                cursor.moveToLast();
                points.add(new RoutePoint(cursor.getDouble(0),cursor.getDouble(1),cursor.getLong(2)));
            }
        }
        return points;
    }


    /** --------------------
     * Diseño lógico: worker: Worker --> ultimaConEnvio() -->
     * Cursor <--
     * Descripción: Ultima Con Envio. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor ultimaConEnvio(Worker worker) {
        return getReadableDatabase().rawQuery(
                "SELECT l.ppb,l.fecha,COALESCE(l.nombre_micro,r.sensor),l.enviado FROM lectura l"
                + " JOIN ruta r ON r.id=l.ruta WHERE r.usuario=? AND r.parque=? ORDER BY l.fecha DESC,l.id DESC LIMIT 1",
                new String[]{String.valueOf(worker.id),String.valueOf(worker.parkId)});
    }


    /** --------------------
     * Diseño lógico: worker: Worker, sensor: Text, limite: Z, desplazamiento: Z --> historial() -->
     * Cursor <--
     * Descripción: Historial. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor historial(Worker worker, String sensor, int limite, int desplazamiento) {
        String identidad = "COALESCE(l.nombre_micro,r.sensor)";
        String sql = "SELECT l.ppb,l.fecha," + identidad + ",r.id,l.enviado,l.error_envio,l.lat,l.lon FROM lectura l JOIN ruta r ON r.id=l.ruta"
                + " WHERE r.usuario=? AND r.parque=?" + (sensor == null ? "" : " AND " + identidad + "=?")
                + " ORDER BY l.fecha DESC,l.id DESC LIMIT ? OFFSET ?";
        String usuario = String.valueOf(worker.id), parque = String.valueOf(worker.parkId);
        String max = String.valueOf(limite), offset = String.valueOf(desplazamiento);
        return getReadableDatabase().rawQuery(sql, sensor == null
                ? new String[]{usuario, parque, max, offset}
                : new String[]{usuario, parque, sensor, max, offset});
    }

    /** Lecturas locales de la cuenta desde un instante UTC ISO, para el gráfico del historial. */

    /** --------------------
     * Diseño lógico: worker: Worker, since: Text --> lecturasDesde() -->
     * Cursor <--
     * Descripción: Lecturas Desde. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor lecturasDesde(Worker worker, String since) {
        return getReadableDatabase().rawQuery(
                "SELECT l.ppb,l.fecha FROM lectura l JOIN ruta r ON r.id=l.ruta"
                + " WHERE r.usuario=? AND r.parque=? AND l.fecha>=? ORDER BY l.fecha LIMIT 20000",
                new String[]{String.valueOf(worker.id), String.valueOf(worker.parkId), since});
    }


    /** --------------------
     * Diseño lógico: worker: Worker, id: Z, limite: Z, desplazamiento: Z --> lecturasRuta() -->
     * Cursor <--
     * Descripción: Lecturas Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Cursor lecturasRuta(Worker worker, long id, int limite, int desplazamiento) {
        return getReadableDatabase().rawQuery(
                "SELECT l.ppb,l.fecha,l.lat,l.lon FROM lectura l JOIN ruta r ON r.id=l.ruta"
                + " WHERE r.usuario=? AND r.parque=? AND r.id=? ORDER BY l.id LIMIT ? OFFSET ?",
                new String[]{String.valueOf(worker.id), String.valueOf(worker.parkId), String.valueOf(id),
                        String.valueOf(limite), String.valueOf(desplazamiento)});
    }


    /** --------------------
     * Diseño lógico: worker: Worker, id: Z --> ruta() -->
     * Route <--
     * Descripción: Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Route ruta(Worker worker, long id) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT r.id,inicio,fin,metros,sensor,COUNT(l.id),AVG(ppb),MIN(ppb),MAX(ppb)"
                        + " FROM ruta r LEFT JOIN lectura l ON l.ruta=r.id"
                        + " WHERE r.usuario=? AND r.parque=? AND r.id=? GROUP BY r.id",
                new String[]{String.valueOf(worker.id), String.valueOf(worker.parkId), String.valueOf(id)})) {
            return leerRuta(c);
        }
    }


    /** --------------------
     * Diseño lógico: worker: Worker --> ultimaRuta() -->
     * Route <--
     * Descripción: Ultima Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    public Route ultimaRuta(Worker worker) {
        try (Cursor c = resumen(worker.id, worker.parkId)) {
            return leerRuta(c);
        }
    }


    /** --------------------
     * Diseño lógico: c: Cursor --> leerRuta() -->
     * Route <--
     * Descripción: Leer Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite
     * -------------------- */
    private Route leerRuta(Cursor c) {
        if (!c.moveToFirst()) return null;
        return new Route(c.getLong(0), c.getLong(1), c.isNull(2) ? null : c.getLong(2),
                c.getDouble(3), c.getString(4), c.getInt(5), c.getDouble(6), c.getInt(7), c.getInt(8));
    }
}
