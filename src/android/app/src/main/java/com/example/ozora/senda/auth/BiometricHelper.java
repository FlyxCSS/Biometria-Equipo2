package com.example.ozora.senda.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;

/** La biometría desbloquea la credencial local; el servidor valida la sesión. */
public final class BiometricHelper {
    private static final String ALIAS = "senda.sesion.v2";
    private final FragmentActivity activity;
    private final SharedPreferences prefs;
    private final BiometricPrompt prompt;
    private Resultado pendiente;
    private String tokenPendiente;
    private byte[] datosPendientes;
    public interface Resultado { void aceptar(String token); void error(String mensaje); }

    public BiometricHelper(FragmentActivity activity) {
        this.activity = activity;
        prefs = activity.getSharedPreferences("acceso", Context.MODE_PRIVATE);
        prompt = new BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
                new BiometricPrompt.AuthenticationCallback() {
            @Override public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult r) {
                completar(r);
            }
            @Override public void onAuthenticationError(int code, @NonNull CharSequence msg) {
                fallar(msg.toString());
            }
        });
        // Las credenciales RSA anteriores requieren volver a activar el acceso.
        if (prefs.contains("cifrado") && prefs.getInt("version", 0) != 2) borrar();
    }
    public boolean activo() {
        return prefs.getInt("version", 0) == 2 && prefs.contains("cifrado") && prefs.contains("iv");
    }
    public boolean disponible() {
        return BiometricManager.from(activity).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                == BiometricManager.BIOMETRIC_SUCCESS;
    }
    public boolean enCurso() { return pendiente != null; }
    private KeyStore almacen() throws Exception {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore"); ks.load(null); return ks;
    }
    public void borrar() {
        cancelar();
        prefs.edit().clear().commit();
        try {
            KeyStore ks = almacen();
            ks.deleteEntry(ALIAS);
            ks.deleteEntry("senda.sesion.v1");
        } catch (Exception ignored) { }
    }
    public void cancelar() {
        pendiente = null; tokenPendiente = null; datosPendientes = null;
        prompt.cancelAuthentication();
    }
    /** La activación solo se guarda después de confirmar una operación con la clave. */
    public void activar(String token, Resultado resultado) {
        if (enCurso()) return;
        if (!disponible()) {
            resultado.error("Configura una huella o biometría compatible en los ajustes de Android."); return;
        }
        try {
            KeyGenerator gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            gen.init(new KeyGenParameterSpec.Builder(ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(true)
                    .setInvalidatedByBiometricEnrollment(true).build());
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, gen.generateKey());
            pendiente = resultado;
            tokenPendiente = token;
            mostrar(cipher, "Activar acceso biométrico");
        } catch (Exception e) {
            if (pendiente == null) resultado.error("Android no pudo proteger la credencial. Usa tu contraseña.");
            else fallar("Android no pudo proteger la credencial. Usa tu contraseña.");
        }
    }
    public void abrir(Resultado resultado) {
        if (enCurso()) return;
        if (!activo()) {
            resultado.error("Entra con tu contraseña y activa la biometría en Ajustes."); return;
        }
        if (!disponible()) { resultado.error("Biometría no disponible. Usa tu contraseña."); return; }
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, almacen().getKey(ALIAS, null),
                    new GCMParameterSpec(128, Base64.decode(prefs.getString("iv", ""), Base64.NO_WRAP)));
            datosPendientes = Base64.decode(prefs.getString("cifrado", ""), Base64.NO_WRAP);
            pendiente = resultado;
            mostrar(cipher, "Accede a SENDA");
        } catch (Exception e) {
            borrar();
            resultado.error("Accede con tu contraseña y vuelve a activar la biometría.");
        }
    }
    private void mostrar(Cipher cipher, String titulo) {
        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(titulo)
                .setSubtitle("Confirma tu identidad con la biometría de este móvil")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText("Usar contraseña")
                .build();
        prompt.authenticate(info, new BiometricPrompt.CryptoObject(cipher));
    }
    private void completar(BiometricPrompt.AuthenticationResult result) {
        if (pendiente == null) return;
        Resultado callback = pendiente;
        String token;
        try {
            BiometricPrompt.CryptoObject crypto = result.getCryptoObject();
            if (crypto == null || crypto.getCipher() == null) throw new java.security.GeneralSecurityException();
            Cipher cipher = crypto.getCipher();
            token = tokenPendiente;
            if (token != null) {
                byte[] cifrado = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));
                if (!prefs.edit().putInt("version", 2)
                        .putString("cifrado", Base64.encodeToString(cifrado, Base64.NO_WRAP))
                        .putString("iv", Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP)).commit())
                    throw new java.io.IOException();
            } else {
                token = new String(cipher.doFinal(datosPendientes), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            borrar();
            callback.error("Credencial no disponible. Usa tu contraseña y vuelve a activar la biometría.");
            return;
        }
        pendiente = null; tokenPendiente = null; datosPendientes = null;
        callback.aceptar(token);
    }
    private void fallar(String mensaje) {
        Resultado callback = pendiente;
        pendiente = null; tokenPendiente = null; datosPendientes = null;
        if (callback != null) callback.error(mensaje);
    }
}
