/*
 * Nombre: BiometricSetupActivity.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import com.example.ozora.senda.MainActivity;
import com.example.ozora.senda.R;
import com.example.ozora.senda.ui.FieldScreen;

/** Consentimiento explícito para proteger la sesión con la biometría de Android. */
public final class BiometricSetupActivity extends FieldScreen {
    private SessionManager session;
    private BiometricHelper bio;
    private boolean fromLogin;

    /** --------------------
     * Diseño lógico: state: EstadoVista --> onCreate() -->
     * Descripción: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        session = SessionManager.current();
        if (session == null) { finish(); return; }
        bio = new BiometricHelper(this);
        fromLogin = getIntent().getBooleanExtra("from_login", false);
        setContentView(R.layout.activity_biometric_setup);
        protegerBarras(findViewById(R.id.bio_scroll));
        findViewById(R.id.bio_back).setOnClickListener(v -> leave());
        findViewById(R.id.bio_skip).setOnClickListener(v -> leave());
        findViewById(R.id.bio_activate).setOnClickListener(v -> activate());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

            /** --------------------
             * Diseño lógico: handleOnBackPressed() -->
             * Descripción: Handle On Back Pressed. Aplicación de campo: acceso, captura, recorridos y presentación Android
             * -------------------- */
            @Override public void handleOnBackPressed() { leave(); }
        });
    }

    /** --------------------
     * Diseño lógico: onStart() -->
     * Descripción: On Start. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onStart() {
        super.onStart();
        if (session == null || session != SessionManager.current()) { finish(); return; }
        render();
    }

    /** --------------------
     * Diseño lógico: render() -->
     * Descripción: Render. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void render() {
        ((TextView)findViewById(R.id.bio_question)).setText(bio.activo() ? R.string.bio_enabled : R.string.bio_question);
        ((TextView)findViewById(R.id.bio_description)).setText(bio.activo() ? R.string.bio_success : R.string.bio_description);
        ((TextView)findViewById(R.id.bio_activate)).setText(bio.activo() ? R.string.bio_disable
                : bio.disponible() ? R.string.bio_enable : R.string.bio_android_settings);
        com.google.android.material.button.MaterialButton action = findViewById(R.id.bio_activate);
        boolean disable = bio.activo();
        int fg = disable ? 0xffffffff : getColor(R.color.senda_ink);
        action.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                getColor(disable ? R.color.status_error_strong : R.color.senda_mint)));
        action.setTextColor(fg);
        action.setIconResource(disable ? R.drawable.ic_s_logout : R.drawable.ic_s_check_circle);
        action.setIconTint(android.content.res.ColorStateList.valueOf(fg));
        ((TextView)findViewById(R.id.bio_skip)).setText(fromLogin && !bio.activo() ? R.string.bio_skip : R.string.flow_back);
        feedback(!bio.activo() && !bio.disponible() ? getString(R.string.bio_unavailable) : "");
    }

    /** --------------------
     * Diseño lógico: activate() -->
     * Descripción: Activate. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void activate() {
        if (bio.enCurso()) return;
        if (session != SessionManager.current()) { finish(); return; }
        if (bio.activo()) {
            new AlertDialog.Builder(this).setTitle(R.string.bio_disable)
                    .setMessage("Podrás seguir accediendo con tu contraseña.")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Desactivar", (d, w) -> { bio.borrar(); render(); }).show();
        } else if (!bio.disponible()) {
            try {
                startActivity(new Intent(Build.VERSION.SDK_INT >= 30
                        ? Settings.ACTION_BIOMETRIC_ENROLL : Settings.ACTION_SECURITY_SETTINGS));
            } catch (android.content.ActivityNotFoundException e) { feedback(getString(R.string.bio_unavailable)); }
        } else {
            bio.activar(session.token, new BiometricHelper.Resultado() {

                /** --------------------
                 * Diseño lógico: token: Text --> aceptar() -->
                 * Descripción: Aceptar. Aplicación de campo: acceso, captura, recorridos y presentación Android
                 * -------------------- */
                public void aceptar(String token) {
                    if (isDestroyed() || isFinishing()) return;
                    if (session != SessionManager.current()) { bio.borrar(); finish(); return; }
                    if (fromLogin) leave(); else render();
                }

                /** --------------------
                 * Diseño lógico: texto: Text --> error() -->
                 * Descripción: Error. Aplicación de campo: acceso, captura, recorridos y presentación Android
                 * -------------------- */
                public void error(String texto) {
                    if (!isDestroyed() && !isFinishing()) feedback(texto);
                }
            });
        }
    }

    /** --------------------
     * Diseño lógico: text: Text --> feedback() -->
     * Descripción: Feedback. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void feedback(String text) {
        TextView feedback = findViewById(R.id.bio_feedback);
        feedback.setText(text);
        feedback.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /** --------------------
     * Diseño lógico: leave() -->
     * Descripción: Leave. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    private void leave() {
        if (bio != null) bio.cancelar();
        if (fromLogin && SessionManager.current() == session)
            startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /** --------------------
     * Diseño lógico: onDestroy() -->
     * Descripción: On Destroy. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    @Override protected void onDestroy() {
        if (bio != null) bio.cancelar();
        super.onDestroy();
    }
}
