/*
 * Nombre: SessionManager.java
 * Descripción: Restaura y protege la sesión persistente con caducidad.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Restaura y protege la sesión persistente con caducidad.
 */
package com.example.ozora.senda.auth;

import com.example.ozora.senda.model.Worker;
import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public final class SessionManager {
    private static volatile SessionManager current;
    private static SharedPreferences preferences;
    private static final String KEY = "senda.sesion.persistente";
    public final Worker worker;
    public final long expiresAt;
    public final String token;


    /** --------------------
     * Diseño lógico: worker: Worker, expiresAt: Z, token: Text --> SessionManager() -->
     * SessionManager <--
     * Descripción: Construye SessionManager. Restaura y protege la sesión persistente con caducidad
     * -------------------- */
    private SessionManager(Worker worker, long expiresAt, String token) {
        this.worker = worker;
        this.expiresAt = expiresAt;
        this.token = token;
    }

    /** --------------------
     * Diseño lógico: worker: Worker, expiresAt: Z, token: Text --> open() --x
     * Descripción: Open. Restaura y protege la sesión persistente con caducidad
     * -------------------- */
    public static synchronized void open(Worker worker, long expiresAt, String token) {
        if (token == null || token.trim().isEmpty() || worker.id <= 0 || worker.parkId <= 0)
            throw new IllegalArgumentException("La sesión requiere un trabajador y una credencial del servidor.");
        current = new SessionManager(worker, expiresAt, token);
        guardar();
    }

    /** --------------------
     * Diseño lógico: current() --x
     * SessionManager <--
     * Descripción: Current. Restaura y protege la sesión persistente con caducidad
     * -------------------- */
    public static SessionManager current() {
        SessionManager session = current;
        return session != null && System.currentTimeMillis() < session.expiresAt ? session : null;
    }

    /** --------------------
     * Diseño lógico: clear() --x
     * Descripción: Clear. Restaura y protege la sesión persistente con caducidad
     * -------------------- */
    public static synchronized void clear() {
        current = null;
        if (preferences != null) preferences.edit().clear().commit();
    }


    /** --------------------
     * Diseño lógico: context: Entorno --> initialize() --x
     * Descripción: Initialize. Restaura y protege la sesión persistente con caducidad
     * -------------------- */
    public static synchronized void initialize(Context context) {
        preferences = context.getSharedPreferences("sesion_cifrada", Context.MODE_PRIVATE);
        current = null;
        if (!preferences.contains("datos")) return;
        try {
            Cipher cipher = cipher();
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            cipher.init(Cipher.DECRYPT_MODE, ks.getKey(KEY, null),
                    new GCMParameterSpec(128, Base64.decode(preferences.getString("iv", ""), Base64.NO_WRAP)));
            JSONObject data = new JSONObject(new String(cipher.doFinal(
                    Base64.decode(preferences.getString("datos", ""), Base64.NO_WRAP)), StandardCharsets.UTF_8));
            if (data.getLong("vence") <= System.currentTimeMillis()) { clear(); return; }
            current = new SessionManager(new Worker(data.getInt("usuario"), data.getInt("parque"),
                    data.getString("nombre")), data.getLong("vence"), data.getString("token"));
        } catch (Exception error) { clear(); }
    }


    /** --------------------
     * Diseño lógico: cipher() --x
     * Cipher <--
     * Descripción: Cipher. Restaura y protege la sesión persistente con caducidad
     * -------------------- */
    private static Cipher cipher() throws Exception { return Cipher.getInstance("AES/GCM/NoPadding"); }


    /** --------------------
     * Diseño lógico: guardar() --x
     * Descripción: Guardar. Restaura y protege la sesión persistente con caducidad
     * -------------------- */
    private static void guardar() {
        if (preferences == null) return; // Las pruebas JVM no inicializan Android.
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            if (!ks.containsAlias(KEY)) {
                KeyGenerator gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                gen.init(new KeyGenParameterSpec.Builder(KEY, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build());
                gen.generateKey();
            }
            Cipher cipher = cipher();
            cipher.init(Cipher.ENCRYPT_MODE, ks.getKey(KEY, null));
            JSONObject data = new JSONObject().put("usuario", current.worker.id).put("parque", current.worker.parkId)
                    .put("nombre", current.worker.name).put("vence", current.expiresAt).put("token", current.token);
            byte[] sealed = cipher.doFinal(data.toString().getBytes(StandardCharsets.UTF_8));
            if (!preferences.edit().putString("iv", Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP))
                    .putString("datos", Base64.encodeToString(sealed, Base64.NO_WRAP)).commit())
                throw new java.io.IOException();
        } catch (Exception error) {
            clear();
            throw new IllegalStateException("No se pudo guardar la sesión de forma segura.", error);
        }
    }

    /** --------------------
     * Diseño lógico: iso: Text --> instante() --x
     * Z <--
     * Descripción: Instante. Restaura y protege la sesión persistente con caducidad
     * -------------------- */
    public static long instante(String iso) throws ParseException {
        SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.ROOT);
        f.setTimeZone(TimeZone.getTimeZone("UTC")); return f.parse(iso).getTime();
    }
}
