/*
 * Nombre: SessionPersistenceTest.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.auth;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.ozora.senda.model.Worker;
import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

/** Restauración cifrada con el almacén real de Android, sin usar el servidor. */
public class SessionPersistenceTest {

    /** --------------------
     * Diseño lógico: restauraLaSesionYElCierreLaElimina() -->
     * Descripción: Restaura La Sesion YEl Cierre La Elimina. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Test public void restauraLaSesionYElCierreLaElimina() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences prefs = context.getSharedPreferences("sesion_cifrada", Context.MODE_PRIVATE);
        Map<String, ?> anterior = prefs.getAll();
        String token = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        try {
            SessionManager.open(new Worker(2147477000,2147477000,"Prueba"),System.currentTimeMillis()+60000,token);
            assertTrue(prefs.contains("datos"));
            assertFalse(prefs.getString("datos", "").contains(token));
            SessionManager.initialize(context);
            assertNotNull(SessionManager.current());
            assertEquals(token,SessionManager.current().token);
            assertEquals(2147477000,SessionManager.current().worker.id);
            SessionManager.clear();
            SessionManager.initialize(context);
            assertNull(SessionManager.current());
            assertFalse(prefs.contains("datos"));
            SessionManager.open(new Worker(2147477000,2147477000,"Prueba"),1,token);
            SessionManager.initialize(context);
            assertNull(SessionManager.current());
            assertFalse(prefs.contains("datos"));
        } finally {
            SharedPreferences.Editor restore = prefs.edit().clear();
            for (Map.Entry<String, ?> entry : anterior.entrySet()) restore.putString(entry.getKey(),(String)entry.getValue());
            restore.commit();
            SessionManager.initialize(context);
        }
    }
}
