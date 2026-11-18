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
            @Override public void handleOnBackPressed() { leave(); }
        });
    }
    @Override protected void onStart() {
        super.onStart();
        if (session == null || session != SessionManager.current()) { finish(); return; }
        render();
    }
    private void render() {
        ((TextView)findViewById(R.id.bio_question)).setText(bio.activo() ? R.string.bio_enabled : R.string.bio_question);
        ((TextView)findViewById(R.id.bio_description)).setText(bio.activo() ? R.string.bio_success : R.string.bio_description);
        ((TextView)findViewById(R.id.bio_activate)).setText(bio.activo() ? R.string.bio_disable
                : bio.disponible() ? R.string.bio_enable : R.string.bio_android_settings);
        ((TextView)findViewById(R.id.bio_skip)).setText(fromLogin && !bio.activo() ? R.string.bio_skip : R.string.flow_back);
        feedback(!bio.activo() && !bio.disponible() ? getString(R.string.bio_unavailable) : "");
    }
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
                public void aceptar(String token) {
                    if (isDestroyed() || isFinishing()) return;
                    if (session != SessionManager.current()) { bio.borrar(); finish(); return; }
                    if (fromLogin) leave(); else render();
                }
                public void error(String texto) {
                    if (!isDestroyed() && !isFinishing()) feedback(texto);
                }
            });
        }
    }
    private void feedback(String text) {
        TextView feedback = findViewById(R.id.bio_feedback);
        feedback.setText(text);
        feedback.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
    }
    private void leave() {
        if (bio != null) bio.cancelar();
        if (fromLogin && SessionManager.current() == session)
            startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    @Override protected void onDestroy() {
        if (bio != null) bio.cancelar();
        super.onDestroy();
    }
}
