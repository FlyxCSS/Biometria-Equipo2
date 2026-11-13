/*
 * Nombre: MeasurementSync.java
 * Descripción: Cola persistente de mediciones autenticadas hacia PHP.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Envíos fuera del hilo de interfaz, reintentos e aislamiento de cuentas.
 */
package com.example.ozora.senda.network;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.model.MedicionAmbiental;
import com.example.ozora.senda.routes.RouteManager;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MeasurementSync {
    private static final ExecutorService RED = Executors.newSingleThreadExecutor();
    private static final AtomicBoolean EN_CURSO = new AtomicBoolean();
    public static volatile String estado = "Sincronización pendiente.";
    private MeasurementSync() {}

    /** Diseño lógico: (contexto) -> sincronizar -> vacío.
     * Descripción: Reintenta un lote de la cuenta actual.. */
    public static void sincronizar(Context contexto) {
        SessionManager sesion = SessionManager.current();
        if(sesion == null || sesion.token == null || !EN_CURSO.compareAndSet(false,true)) return;
        Context app = contexto.getApplicationContext();
        RED.execute(() -> enviarPendientes(app,sesion));
    }

    /** Diseño lógico: (contexto, sesión capturada) -> enviarPendientes -> vacío.
     * Descripción: Marca enviada solo tras confirmación; conserva la lectura incluso si se rechaza. */
    private static void enviarPendientes(Context contexto, SessionManager sesion) {
        try(RouteManager rutas = new RouteManager(contexto,"recorridos.db")) {
            SQLiteDatabase db = rutas.getWritableDatabase();
            ApiClient api = new ApiClient(contexto);
            try(Cursor filas = db.rawQuery("SELECT l.id,l.ppb,l.fecha,l.lat,l.lon,l.nombre_micro FROM lectura l "
                    + "JOIN ruta r ON r.id=l.ruta WHERE r.usuario=? AND r.parque=? AND l.enviado=0 "
                    + "AND l.nombre_micro IS NOT NULL ORDER BY l.id LIMIT 50",
                    new String[]{String.valueOf(sesion.worker.id),String.valueOf(sesion.worker.parkId)})) {
                while(filas.moveToNext()) {
                    if(SessionManager.current()!=sesion) return; // No reutilizar el token de otra cuenta.
                    long id = filas.getLong(0);
                    MedicionAmbiental m = new MedicionAmbiental(filas.getString(5),filas.getInt(1),
                            filas.getDouble(3),filas.getDouble(4),0,filas.getString(2),0);
                    try {
                        api.pedir("POST","/mediciones",sesion.token,new JSONObject(MeasurementPayload.datos(m)));
                        db.execSQL("UPDATE lectura SET enviado=1,error_envio=NULL WHERE id=?",new Object[]{id});
                        estado = "Medición confirmada por el servidor.";
                    } catch(ApiClient.Error error) {
                        if(error.status==401) {
                            estado="Sesión caducada. Las mediciones siguen guardadas en el móvil.";
                            if(SessionManager.current()==sesion) SessionManager.clear();
                            return;
                        }
                        if(error.status==403 || error.status==409 || error.status==422) {
                            db.execSQL("UPDATE lectura SET enviado=2,error_envio=? WHERE id=?",new Object[]{error.getMessage(),id});
                            estado="Lectura conservada en el móvil; servidor: "+error.getMessage();
                            continue;
                        }
                        estado="Envío pendiente. Se reintentará al recuperar la conexión.";
                        return;
                    } catch(Exception error) {
                        estado="Envío pendiente. La lectura está guardada en el móvil.";
                        return;
                    }
                }
            }
        } catch(Exception error) {
            estado="No se pudo abrir la cola local. Vuelve a entrar para reintentar.";
        } finally { EN_CURSO.set(false); }
    }
}
