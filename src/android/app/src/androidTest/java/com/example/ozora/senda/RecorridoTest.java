package com.example.ozora.senda;

import com.example.ozora.senda.routes.RouteManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.location.Location;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.model.MedicionAmbiental;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecorridoTest {
    @Test public void conservaElRecorridoYSusEstadisticasPorTrabajador() {
        android.content.Context contexto=InstrumentationRegistry.getInstrumentation().getTargetContext();
        String archivo="test-"+java.util.UUID.randomUUID()+".db";
        try(RouteManager rutas=new RouteManager(contexto,archivo)) {
            long id=rutas.iniciar(12,3,"S-001");
            try {
                rutas.iniciar(12,3,"S-001");
                fail("No se deben iniciar dos recorridos simultáneos");
            } catch(SQLiteConstraintException esperado) {}
            try(Cursor c=rutas.resumen(12,3)) {
                assertTrue(c.moveToFirst()); assertTrue(c.isNull(2));
                assertEquals(0,c.getInt(5)); assertTrue(c.isNull(6));
            }
            Location punto=new Location("gps");
            punto.setLatitude(39); punto.setLongitude(-0.1);
            rutas.punto(id,punto,1,0);
            Location segundo=new Location(punto); segundo.setLatitude(39.001);
            rutas.punto(id,segundo,1,punto.distanceTo(segundo));
            for(int ppb:new int[]{0,2,10}) {
                rutas.lectura(id,new MedicionAmbiental("S-001",ppb,39,-0.1,5,"2026-11-02T12:00:00.000Z",ppb));
            }
            rutas.finalizar(id);
        }
        try(RouteManager rutas=new RouteManager(contexto,archivo); Cursor c=rutas.resumen(12,3);
                Cursor ajeno=rutas.resumen(17,3); Cursor otroParque=rutas.resumen(12,2)) {
            assertTrue(c.moveToFirst()); assertFalse(c.isNull(2)); assertTrue(c.getLong(2)>=c.getLong(1));
            assertEquals(3,c.getInt(5)); assertEquals(4,c.getDouble(6),0); assertEquals(0,c.getInt(7)); assertEquals(10,c.getInt(8));
            assertEquals(111,c.getDouble(3),1); assertEquals(2,rutas.puntos(c.getLong(0)).size());
            try(Cursor m=rutas.ultima(c.getLong(0))) { assertTrue(m.moveToFirst()); assertEquals(10,m.getInt(0)); }
            assertFalse(ajeno.moveToFirst()); assertFalse(otroParque.moveToFirst());
            assertTrue(rutas.iniciar(12,3,null)>c.getLong(0));
        } finally { contexto.deleteDatabase(archivo); }
    }
}
