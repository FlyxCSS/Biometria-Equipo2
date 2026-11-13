package com.example.ozora.senda.auth;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ozora.senda.R;
import com.example.ozora.senda.network.ApiClient;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Solicita al backend el enlace que se entrega mediante el correo configurado por el equipo. */
public final class RecoveryActivity extends AuthScreen {
    private static final ExecutorService RED = Executors.newSingleThreadExecutor();
    private boolean ocupado;
    private boolean enviada;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        mostrarFormulario();
        ((TextView) findViewById(R.id.auth_title)).setText(R.string.auth_recovery_title);
        ((ImageView) findViewById(R.id.auth_emblem)).setImageResource(R.drawable.auth_mail);
        findViewById(R.id.auth_brand).setVisibility(View.GONE);
        findViewById(R.id.auth_description).setVisibility(View.VISIBLE);
        findViewById(R.id.auth_password_group).setVisibility(View.GONE);
        findViewById(R.id.auth_forgot).setVisibility(View.GONE);
        findViewById(R.id.auth_biometric_group).setVisibility(View.GONE);
        findViewById(R.id.auth_back).setVisibility(View.VISIBLE);
        findViewById(R.id.auth_back).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.auth_submit)).setText(R.string.auth_send);
        email.setText(getIntent().getStringExtra("correo"));
        email.setImeOptions(EditorInfo.IME_ACTION_SEND);
        email.setOnEditorActionListener((v, action, event) -> {
            if (action != EditorInfo.IME_ACTION_SEND) return false;
            solicitar();
            return true;
        });
        findViewById(R.id.auth_submit).setOnClickListener(v -> solicitar());
        enviada = state != null && state.getBoolean("enviada");
        if (enviada) confirmarSolicitud();
    }

    private void solicitar() {
        if (ocupado || enviada) return;
        String correo = email.getText().toString().trim();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            email.setError(getString(R.string.auth_email_invalid));
            email.requestFocus();
            return;
        }
        ocupado = true;
        mostrarEspera(true);
        mensaje(getString(R.string.auth_sending));
        ApiClient api = new ApiClient(this);
        RED.execute(() -> {
            try {
                api.pedir("POST", "/recuperaciones", null, new JSONObject().put("correo", correo));
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    ocupado = false;
                    enviada = true;
                    confirmarSolicitud();
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    ocupado = false;
                    mostrarEspera(false);
                    mensaje(error instanceof ApiClient.Error ? error.getMessage()
                            : getString(R.string.auth_recovery_error));
                });
            }
        });
    }

    private void confirmarSolicitud() {
        mostrarEspera(false);
        mensaje(getString(R.string.auth_recovery_received));
        ((TextView) findViewById(R.id.auth_feedback)).setTextColor(0xff1d4032);
        email.setEnabled(false);
        TextView volver = findViewById(R.id.auth_submit);
        volver.setText(R.string.auth_back);
        volver.setOnClickListener(v -> finish());
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putBoolean("enviada", enviada);
        super.onSaveInstanceState(state);
    }
}
