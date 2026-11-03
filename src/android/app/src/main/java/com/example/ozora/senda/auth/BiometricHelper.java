package com.example.ozora.senda.auth;

import android.app.Activity;
import android.content.*;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.biometrics.BiometricManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.*;
import android.security.keystore.*;
import android.util.Base64;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;

public final class BiometricHelper {
    private static final String ALIAS="senda.sesion.v1";
    private final Activity activity;
    private final SharedPreferences prefs;
    private CancellationSignal cancel;
    public interface Resultado { void aceptar(String token); void error(String mensaje); }

    public BiometricHelper(Activity activity) {
        this.activity=activity;
        prefs=activity.getSharedPreferences("acceso",Context.MODE_PRIVATE);
    }
    public boolean activo() { return prefs.contains("cifrado"); }
    public boolean disponible() {
        if(Build.VERSION.SDK_INT>=30) {
            BiometricManager m=activity.getSystemService(BiometricManager.class);
            return m!=null && m.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)==BiometricManager.BIOMETRIC_SUCCESS;
        }
        if(Build.VERSION.SDK_INT==29) {
            BiometricManager m=activity.getSystemService(BiometricManager.class);
            return m!=null && m.canAuthenticate()==BiometricManager.BIOMETRIC_SUCCESS;
        }
        FingerprintManager m=activity.getSystemService(FingerprintManager.class);
        return m!=null && m.isHardwareDetected() && m.hasEnrolledFingerprints();
    }
    private KeyStore almacen() throws Exception {
        KeyStore ks=KeyStore.getInstance("AndroidKeyStore"); ks.load(null); return ks;
    }
    public void borrar() {
        prefs.edit().clear().commit();
        try { almacen().deleteEntry(ALIAS); } catch(Exception ignored) {}
    }
    public void activar(String token) throws Exception {
        borrar();
        KeyPairGenerator gen=KeyPairGenerator.getInstance("RSA","AndroidKeyStore");
        gen.initialize(new KeyGenParameterSpec.Builder(ALIAS,KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(2048).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setUserAuthenticationRequired(true).setInvalidatedByBiometricEnrollment(true).build());
        PublicKey pub=gen.generateKeyPair().getPublic();
        Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,KeyFactory.getInstance("RSA").generatePublic(new java.security.spec.X509EncodedKeySpec(pub.getEncoded())));
        String sealed=Base64.encodeToString(cipher.doFinal(token.getBytes(StandardCharsets.UTF_8)),Base64.NO_WRAP);
        if(!prefs.edit().putString("cifrado",sealed).commit()) throw new java.io.IOException();
    }
    public void cancelar() { if(cancel!=null) cancel.cancel(); }
    public void abrir(Resultado resultado) {
        cancelar();
        try {
            byte[] sealed=Base64.decode(prefs.getString("cifrado",""),Base64.NO_WRAP);
            Cipher cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE,almacen().getKey(ALIAS,null));
            cancel=new CancellationSignal();
            if(Build.VERSION.SDK_INT>=28) {
                BiometricPrompt.Builder builder=new BiometricPrompt.Builder(activity).setTitle("Accede a SENDA")
                    .setNegativeButton("Usar contraseña",activity.getMainExecutor(),(d,w)->resultado.error("Puedes acceder con tu contraseña."));
                if(Build.VERSION.SDK_INT>=30) builder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG);
                builder.build().authenticate(new BiometricPrompt.CryptoObject(cipher),cancel,activity.getMainExecutor(),
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult r) { descifrar(r.getCryptoObject().getCipher(),sealed,resultado); }
                        @Override public void onAuthenticationError(int code,CharSequence msg) { resultado.error(msg.toString()); }
                    });
            } else {
                android.app.AlertDialog dialog=new android.app.AlertDialog.Builder(activity).setTitle("Accede con tu huella")
                    .setMessage("Apoya el dedo en el lector.").setNegativeButton("Cancelar",(d,w)->cancelar()).create();
                dialog.setOnCancelListener(d->cancelar()); dialog.show();
                activity.getSystemService(FingerprintManager.class).authenticate(new FingerprintManager.CryptoObject(cipher),cancel,0,
                    new FingerprintManager.AuthenticationCallback() {
                        @Override public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult r) {
                            dialog.dismiss(); descifrar(r.getCryptoObject().getCipher(),sealed,resultado);
                        }
                        @Override public void onAuthenticationError(int code,CharSequence msg) { dialog.dismiss(); resultado.error(msg.toString()); }
                        @Override public void onAuthenticationFailed() { dialog.setMessage("Huella no reconocida. Vuelve a intentarlo."); }
                    },null);
            }
        } catch(Exception e) { borrar(); resultado.error("Accede con tu contraseña y vuelve a activar la biometría."); }
    }
    private void descifrar(Cipher c,byte[] datos,Resultado r) {
        try { r.aceptar(new String(c.doFinal(datos),StandardCharsets.UTF_8)); }
        catch(Exception e) { borrar(); r.error("Credencial no disponible. Usa tu contraseña."); }
    }
}
