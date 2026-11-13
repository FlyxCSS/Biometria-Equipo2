/*
 * Nombre: ColaMedicionesTest.java
 * Descripción: Persistencia e integridad de la cola REST en SQLite real de Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Verificar migración sin borrado, estados y aislamiento entre trabajadores.
 */
package com.example.ozora.senda;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.routes.RouteManager;
import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.model.Worker;
import org.junit.Test;
import static org.junit.Assert.*;

public class ColaMedicionesTest {
    /** Diseño lógico: () -> actualizaSinBorrarLecturasAntiguas -> vacío.
     * Descripción: Las lecturas v1 permanecen locales y las nuevas guardan identidad BLE y estado. */
    @Test public void actualizaSinBorrarLecturasAntiguas() {
        Context contexto=InstrumentationRegistry.getInstrumentation().getTargetContext();
        String archivo="cola-"+java.util.UUID.randomUUID()+".db";
        try {
            try(SQLiteDatabase db=contexto.openOrCreateDatabase(archivo,0,null)) {
                db.execSQL("CREATE TABLE ruta(id INTEGER PRIMARY KEY,usuario INTEGER,parque INTEGER,sensor TEXT,inicio INTEGER,fin INTEGER,metros REAL)");
                db.execSQL("CREATE TABLE lectura(id INTEGER PRIMARY KEY,ruta INTEGER,ppb INTEGER,fecha TEXT,lat REAL,lon REAL,contador INTEGER)");
                db.execSQL("INSERT INTO ruta VALUES(1,12,3,'S-001',1,NULL,0)");
                db.execSQL("INSERT INTO lectura VALUES(1,1,42,'2026-11-03T12:00:00.000Z',39,-0.1,1)");
                db.setVersion(1);
            }
            try(RouteManager rutas=new RouteManager(contexto,archivo)) {
                SQLiteDatabase db=rutas.getWritableDatabase();
                assertEquals(2,db.getVersion());
                try(Cursor c=db.rawQuery("SELECT ppb,nombre_micro FROM lectura WHERE id=1",null)) {
                    assertTrue(c.moveToFirst()); assertEquals(42,c.getInt(0)); assertTrue(c.isNull(1));
                }
                rutas.lectura(1,new MedicionAmbiental("S-001",43,39,-0.1,5,"2026-11-03T12:00:01.000Z",2));
                assertTrue(rutas.estadoEnvios(new Worker(12,3,"Prueba")).startsWith("1 pendientes"));
                assertTrue(rutas.estadoEnvios(new Worker(99,3,"Otra cuenta")).startsWith("0 pendientes"));
                db.execSQL("UPDATE lectura SET enviado=2 WHERE id=2");
                rutas.reintentarEnvios(new Worker(99,3,"Otra cuenta"));
                try(Cursor c=db.rawQuery("SELECT enviado FROM lectura WHERE id=2",null)) {
                    assertTrue(c.moveToFirst()); assertEquals(2,c.getInt(0));
                }
                rutas.reintentarEnvios(new Worker(12,3,"Prueba"));
                assertTrue(rutas.estadoEnvios(new Worker(12,3,"Prueba")).startsWith("1 pendientes"));
            }
        } finally { contexto.deleteDatabase(archivo); }
    }
}
