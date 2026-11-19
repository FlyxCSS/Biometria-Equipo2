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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MeasurementSync {
    private static final ExecutorService RED = Executors.newSingleThreadExecutor();
    private static final AtomicBoolean EN_CURSO = new AtomicBoolean();
    public static volatile String estado = "Sincronización pendiente.";
    private static volatile SessionManager owner;
    private MeasurementSync() {}
    public static boolean enCurso() { return EN_CURSO.get(); }
    public static String estado(SessionManager session) {
        return owner == session ? estado : "Sincronización pendiente.";
    }
    public static boolean reintentar(Context context) { return comenzar(context, true); }
    private static boolean comenzar(Context context, boolean retry) {
        SessionManager session = SessionManager.current();
        if (session == null || session.token == null || !EN_CURSO.compareAndSet(false, true)) return false;
        owner = session;
        estado = "Enviando lecturas…";
        Context app = context.getApplicationContext();
        RED.execute(() -> enviarPendientes(app, session, retry));
        return true;
    }

    /** Diseño lógico: (contexto) -> sincronizar -> vacío.
     * Descripción: Reintenta un lote de la cuenta actual.. */
    public static void sincronizar(Context contexto) {
        comenzar(contexto, false);
    }

    /** Diseño lógico: (contexto, sesión capturada) -> enviarPendientes -> vacío.
     * Descripción: Marca enviada solo tras confirmación; conserva la lectura incluso si se rechaza. */
    private static void enviarPendientes(Context contexto, SessionManager sesion, boolean retry) {
        try(RouteManager rutas = new RouteManager(contexto,"recorridos.db")) {
            if (SessionManager.current() != sesion) return;
            if (retry) rutas.reintentarEnvios(sesion.worker);
            SQLiteDatabase db = rutas.getWritableDatabase();
            long last = 0, ceiling;
            int sent = 0, rejected = 0;
            try (Cursor maximum = db.rawQuery("SELECT COALESCE(MAX(l.id),0) FROM lectura l JOIN ruta r ON r.id=l.ruta WHERE r.usuario=? AND r.parque=?",
                    new String[]{String.valueOf(sesion.worker.id),String.valueOf(sesion.worker.parkId)})) {
                maximum.moveToFirst(); ceiling = maximum.getLong(0);
            }
            LogicaFake logica = new LogicaFake(contexto);
            while (last < ceiling) {
            try(Cursor filas = db.rawQuery("SELECT l.id,l.ppb,l.fecha,l.lat,l.lon,l.nombre_micro FROM lectura l "
                    + "JOIN ruta r ON r.id=l.ruta WHERE r.usuario=? AND r.parque=? AND l.enviado=0 "
                    + "AND l.nombre_micro IS NOT NULL AND l.id>? AND l.id<=? ORDER BY l.id LIMIT 50",
                    new String[]{String.valueOf(sesion.worker.id),String.valueOf(sesion.worker.parkId),String.valueOf(last),String.valueOf(ceiling)})) {
                if (filas.getCount() == 0) break;
                while(filas.moveToNext()) {
                    if(SessionManager.current()!=sesion) return; // No reutilizar el token de otra cuenta.
                    long id = filas.getLong(0);
                    last = id;
                    MedicionAmbiental m = new MedicionAmbiental(filas.getString(5),filas.getInt(1),
                            filas.getDouble(3),filas.getDouble(4),0,filas.getString(2),0);
                    try {
                        logica.guardarMedicion(sesion.token,m);
                        db.execSQL("UPDATE lectura SET enviado=1,error_envio=NULL WHERE id=?",new Object[]{id});
                        sent++;
                        estado = sent + " lecturas confirmadas por el servidor…";
                    } catch(ApiClient.Error error) {
                        if(error.status==401) {
                            estado="Sesión caducada. Las mediciones siguen guardadas en el móvil.";
                            if(SessionManager.current()==sesion) SessionManager.clear();
                            return;
                        }
                        if(error.status==403 || error.status==409 || error.status==422) {
                            db.execSQL("UPDATE lectura SET enviado=2,error_envio=? WHERE id=?",new Object[]{error.getMessage(),id});
                            rejected++;
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
            }
            estado = sent == 0 && rejected == 0 ? "No hay lecturas pendientes de envío."
                    : sent + " enviadas · " + rejected + " rechazadas. Las lecturas se conservan en el móvil.";
        } catch(Exception error) {
            estado="No se pudo abrir la cola local. Vuelve a entrar para reintentar.";
        } finally { EN_CURSO.set(false); }
    }
}
