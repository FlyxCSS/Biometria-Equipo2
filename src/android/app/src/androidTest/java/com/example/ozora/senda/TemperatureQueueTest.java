package com.example.ozora.senda;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.model.Worker;
import com.example.ozora.senda.routes.RouteManager;
import org.junit.Test;
import static org.junit.Assert.*;

/** SQLite real: migración conservadora, cola persistente y aislamiento entre cuentas. */
public class TemperatureQueueTest {
    @Test public void versionCuatroConservaLecturasSinGpsComoHistoricoLocal() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        String archivo="temperatura-v4-test.db";
        context.deleteDatabase(archivo);
        Worker worker=new Worker(123,456,"Prueba");
        try {
            try(RouteManager routes=new RouteManager(context,archivo)) {
                long ruta=routes.iniciar(worker.id,worker.parkId,"S-001");
                SQLiteDatabase db=routes.getWritableDatabase();
                db.execSQL("DROP TABLE temperatura");
                db.execSQL("CREATE TABLE temperatura(id INTEGER PRIMARY KEY,ruta INTEGER,valor INTEGER,fecha TEXT,nombre_micro TEXT,enviado INTEGER,error_envio TEXT)");
                db.execSQL("INSERT INTO temperatura VALUES (1,?,2939,'2026-11-19T10:00:00.000Z','S-001',0,NULL)",new Object[]{ruta});
                db.setVersion(4);
            }
            try(RouteManager routes=new RouteManager(context,archivo);
                Cursor c=routes.getReadableDatabase().rawQuery("SELECT valor,lat,lon FROM temperatura",null)) {
                assertEquals(5,routes.getReadableDatabase().getVersion());
                assertTrue(c.moveToFirst()); assertEquals(2939,c.getInt(0));
                assertTrue(c.isNull(1)); assertTrue(c.isNull(2));
                assertTrue(routes.estadoEnvios(worker).startsWith("0 pendientes"));
            }
        } finally { context.deleteDatabase(archivo); }
    }
    @Test public void migraVersionTresYConservaTemperaturasSinReenviarElHistorico() {
        Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        String archivo="temperatura-cola-test.db";
        context.deleteDatabase(archivo);
        Worker worker=new Worker(123,456,"Prueba");
        long ruta;
        try {
            try(RouteManager routes=new RouteManager(context,archivo)) {
                ruta=routes.iniciar(worker.id,worker.parkId,"S-001");
                SQLiteDatabase db=routes.getWritableDatabase();
                db.execSQL("DROP TABLE temperatura");
                db.execSQL("CREATE TABLE temperatura(id INTEGER PRIMARY KEY,ruta INTEGER NOT NULL,valor INTEGER NOT NULL,fecha TEXT NOT NULL)");
                db.execSQL("INSERT INTO temperatura VALUES (1,?,2941,'2026-11-19T10:00:00.000Z')",new Object[]{ruta});
                db.setVersion(3);
            }
            try(RouteManager routes=new RouteManager(context,archivo)) {
                assertEquals(5,routes.getReadableDatabase().getVersion());
                assertEquals("0 pendientes · 0 rechazadas (conservadas localmente)",routes.estadoEnvios(worker));
                routes.temperatura(ruta,new com.example.ozora.senda.model.MedicionTemperatura("S-001",2939,"2026-11-19T10:01:00.000Z",39.4,-0.3));
                assertTrue(routes.estadoEnvios(worker).startsWith("1 pendientes"));
                assertTrue(routes.estadoEnvios(new Worker(999,456,"Otra cuenta")).startsWith("0 pendientes"));
                try(Cursor c=routes.temperaturasRuta(worker,ruta,-1,0)) {
                    assertEquals(2,c.getCount());
                    assertTrue(c.moveToFirst()); assertEquals(2941,c.getInt(0));
                    assertTrue(c.moveToNext()); assertEquals(2939,c.getInt(0));
                }
                SQLiteDatabase db=routes.getWritableDatabase();
                db.execSQL("UPDATE temperatura SET enviado=2,error_envio='rechazo de prueba' WHERE nombre_micro IS NOT NULL");
                routes.reintentarEnvios(new Worker(999,456,"Otra cuenta"));
                assertTrue(routes.estadoEnvios(worker).contains("1 rechazadas"));
                routes.reintentarEnvios(worker);
                assertTrue(routes.estadoEnvios(worker).startsWith("1 pendientes"));
            }
            try(RouteManager routes=new RouteManager(context,archivo);
                Cursor c=routes.getReadableDatabase().rawQuery("SELECT valor,nombre_micro,enviado,lat,lon FROM temperatura WHERE nombre_micro IS NOT NULL",null)) {
                assertTrue(c.moveToFirst());
                assertEquals(2939,c.getInt(0)); assertEquals("S-001",c.getString(1)); assertEquals(0,c.getInt(2));
                assertEquals(39.4,c.getDouble(3),0); assertEquals(-0.3,c.getDouble(4),0);
            }
        } finally { context.deleteDatabase(archivo); }
    }
}
