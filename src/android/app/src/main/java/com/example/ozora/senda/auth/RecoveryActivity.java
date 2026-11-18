package com.example.ozora.senda.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import com.example.ozora.senda.R;
import com.example.ozora.senda.ui.FieldScreen;
import java.nio.charset.StandardCharsets;

/** Solicitud y confirmación de recuperación mediante el enlace real del backend. */
public final class RecoveryActivity extends FieldScreen {
    private RecoveryViewModel model;
    private EditText email, token, next, repeat;
    private RecoveryViewModel.Step step = RecoveryViewModel.Step.EMAIL;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_recovery);
        protegerBarras(findViewById(R.id.auth_scroll));
        email = findViewById(R.id.auth_email);
        token = findViewById(R.id.recovery_token);
        next = findViewById(R.id.recovery_password);
        repeat = findViewById(R.id.recovery_repeat);
        if (state == null) email.setText(getIntent().getStringExtra("correo"));
        model = new ViewModelProvider(this).get(RecoveryViewModel.class);
        findViewById(R.id.auth_back).setOnClickListener(v -> finish());
        findViewById(R.id.auth_submit).setOnClickListener(v -> submit());
        findViewById(R.id.recovery_existing_link).setOnClickListener(v -> {
            clearSecrets();
            if (step == RecoveryViewModel.Step.EMAIL) model.showLink(); else model.showEmail();
        });
        email.setOnEditorActionListener((v, action, event) -> {
            if (action != EditorInfo.IME_ACTION_SEND) return false;
            submit(); return true;
        });
        repeat.setOnEditorActionListener((v, action, event) -> {
            if (action != EditorInfo.IME_ACTION_DONE) return false;
            submit(); return true;
        });
        model.observe().observe(this, this::render);
    }
    private void render(RecoveryViewModel.State state) {
        step = state.step;
        boolean sent = step == RecoveryViewModel.Step.LINK;
        boolean done = step == RecoveryViewModel.Step.DONE;
        findViewById(R.id.recovery_email_group).setVisibility(step == RecoveryViewModel.Step.EMAIL ? View.VISIBLE : View.GONE);
        findViewById(R.id.recovery_link_group).setVisibility(sent ? View.VISIBLE : View.GONE);
        ((TextView)findViewById(R.id.recovery_question)).setText(done ? R.string.recovery_done
                : sent ? R.string.recovery_link_question : R.string.recovery_question);
        ((TextView)findViewById(R.id.recovery_description)).setText(done ? R.string.recovery_done_description
                : sent ? R.string.recovery_link_description : R.string.recovery_description);
        ((TextView)findViewById(R.id.auth_submit)).setText(done ? R.string.auth_login
                : sent ? R.string.recovery_confirm : R.string.auth_send);
        ((TextView)findViewById(R.id.recovery_existing_link)).setText(sent
                ? R.string.recovery_other_email : R.string.recovery_link_label);
        findViewById(R.id.recovery_existing_link).setVisibility(done ? View.GONE : View.VISIBLE);
        findViewById(R.id.auth_progress).setVisibility(state.busy ? View.VISIBLE : View.GONE);
        for (int id : new int[]{R.id.auth_submit, R.id.recovery_existing_link, R.id.auth_email,
                R.id.recovery_token, R.id.recovery_password, R.id.recovery_repeat}) findViewById(id).setEnabled(!state.busy);
        TextView feedback = findViewById(R.id.auth_feedback);
        feedback.setText(state.message);
        feedback.setVisibility(state.message.isEmpty() ? View.GONE : View.VISIBLE);
        if (done) {
            clearSecrets();
            new BiometricHelper(this).borrar();
            SessionManager.clear();
        }
    }
    private void submit() {
        if (model.state.getValue().busy) return;
        if (step == RecoveryViewModel.Step.DONE) {
            startActivity(new Intent(this, Login.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .putExtra("mensaje", getString(R.string.recovery_done_description)));
            finish(); return;
        }
        if (step == RecoveryViewModel.Step.EMAIL) {
            String value = email.getText().toString().trim();
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                email.setError(getString(R.string.auth_email_invalid)); email.requestFocus(); return;
            }
            model.request(value); return;
        }
        String value = RecoveryToken.extract(token.getText().toString());
        if (value == null) {
            token.setError(getString(R.string.recovery_link_invalid)); token.requestFocus(); return;
        }
        String password = next.getText().toString();
        int length = password.getBytes(StandardCharsets.UTF_8).length;
        if (length < 12 || length > 72 || password.indexOf(0) >= 0) {
            next.setError(getString(R.string.recovery_password_invalid)); next.requestFocus(); return;
        }
        if (!password.equals(repeat.getText().toString())) {
            repeat.setError(getString(R.string.recovery_mismatch)); repeat.requestFocus(); return;
        }
        model.reset(value, password);
    }
    private void clearSecrets() { token.setText(""); next.setText(""); repeat.setText(""); }
}
