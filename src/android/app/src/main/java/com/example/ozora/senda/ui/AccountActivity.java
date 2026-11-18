package com.example.ozora.senda.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.BiometricHelper;
import com.example.ozora.senda.auth.LoginActivity;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.network.ApiClient;
import com.example.ozora.senda.network.LogicaFake;
import java.nio.charset.StandardCharsets;

/** El perfil visual es local; la contraseña se cambia exclusivamente en el servidor. */
public final class AccountActivity extends FieldScreen {
    private SessionManager session;
    private SharedPreferences prefs;
    private String photo;
    private boolean busy;
    private final ActivityResultLauncher<String[]> picker = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null || session == null) return;
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    photo = uri.toString();
                    showPhoto();
                } catch (SecurityException e) {
                    ((TextView)findViewById(R.id.profile_feedback)).setText("No se pudo abrir la foto. Selecciona otra imagen.");
                }
            });
    public static SharedPreferences preferences(android.content.Context context, SessionManager session) {
        return context.getSharedPreferences("campo-" + session.worker.id + "-" + session.worker.parkId,
                MODE_PRIVATE);
    }
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        session = SessionManager.current();
        if (session == null) { finish(); return; }
        prefs = preferences(this, session);
        if (getIntent().getBooleanExtra("security", false)) { security(); return; }
        setContentView(R.layout.activity_account);
        protegerBarras(findViewById(R.id.account_root));
        findViewById(R.id.account_back).setOnClickListener(v -> finish());
        ((TextView)findViewById(R.id.profile_name)).setText(prefs.getString("profile_name", session.worker.name));
        ((TextView)findViewById(R.id.profile_email)).setText(prefs.getString("account_email",
                getString(R.string.account_no_email)));
        ((TextView)findViewById(R.id.profile_phone)).setText(prefs.getString("profile_phone", ""));
        ((TextView)findViewById(R.id.profile_park)).setText(getString(R.string.home_park, session.worker.parkId));
        photo = state != null ? state.getString("photo") : prefs.getString("profile_photo", null);
        showPhoto();
        findViewById(R.id.profile_change_photo).setOnClickListener(v -> picker.launch(new String[]{"image/*"}));
        findViewById(R.id.profile_save).setOnClickListener(v -> save());
    }
    @Override protected void onStart() {
        super.onStart();
        if (session == null || SessionManager.current() != session) finish();
    }
    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("photo", photo);
    }
    private void showPhoto() {
        ImageView view = findViewById(R.id.profile_photo);
        view.setImageResource(R.drawable.account_avatar);
        if (photo != null) {
            try { view.setImageURI(Uri.parse(photo)); if (view.getDrawable() == null) view.setImageResource(R.drawable.account_avatar); }
            catch (RuntimeException e) { view.setImageResource(R.drawable.account_avatar); photo = null; }
        }
    }
    private void save() {
        if (SessionManager.current() != session) { finish(); return; }
        EditText name = findViewById(R.id.profile_name);
        EditText phone = findViewById(R.id.profile_phone);
        String value = name.getText().toString().trim();
        if (value.isEmpty()) { name.setError("Introduce tu nombre."); name.requestFocus(); return; }
        String number = phone.getText().toString().trim();
        if (!number.isEmpty() && (!number.matches("[+0-9() .-]{6,24}")
                || number.replaceAll("[^0-9]", "").length() < 6)) {
            phone.setError("Introduce un teléfono válido o deja el campo vacío."); phone.requestFocus(); return;
        }
        prefs.edit().putString("profile_name", value).putString("profile_phone", number)
                .putString("profile_photo", photo).apply();
        ((TextView)findViewById(R.id.profile_feedback)).setText(R.string.account_saved);
    }
    private void security() {
        pagina(getString(R.string.account_security));
        boton("‹ Volver a Ajustes", this::finish);
        texto("Cambia tu contraseña de acceso a SENDA. Al guardarla se cerrarán las sesiones de la cuenta.", 16, false);
        EditText current = campo("Contraseña actual", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText next = campo("Nueva contraseña", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText confirm = campo("Repetir nueva contraseña", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        current.setId(android.view.View.generateViewId());
        next.setId(android.view.View.generateViewId());
        confirm.setId(android.view.View.generateViewId());
        current.setSaveEnabled(false); next.setSaveEnabled(false); confirm.setSaveEnabled(false);
        contenido.addView(current); contenido.addView(next); contenido.addView(confirm);
        Button submit = boton("Actualizar contraseña", () -> changePassword(current, next, confirm));
        submit.setTag("submit");
        mensajes();
    }
    private void changePassword(EditText current, EditText next, EditText confirm) {
        if (busy) return;
        if (SessionManager.current() != session) { finish(); return; }
        String old = current.getText().toString(), value = next.getText().toString();
        int bytes = value.getBytes(StandardCharsets.UTF_8).length;
        if (old.isEmpty()) { current.setError("Introduce la contraseña actual."); return; }
        if (bytes < 12 || bytes > 72 || value.indexOf(0) >= 0 || value.equals(old)) {
            next.setError("Usa una contraseña distinta de entre 12 y 72 bytes UTF-8."); return;
        }
        if (!value.equals(confirm.getText().toString())) { confirm.setError("Las contraseñas no coinciden."); return; }
        busy = true;
        Button submit = contenido.findViewWithTag("submit");
        submit.setEnabled(false);
        mensaje("Guardando contraseña…");
        LogicaFake logica = new LogicaFake(this);
        new Thread(() -> {
            try {
                logica.cambiarContrasena(session.token,
                        new org.json.JSONObject().put("contrasena_actual", old).put("contrasena_nueva", value));
                runOnUiThread(() -> {
                    if (isDestroyed() || isFinishing()) return;
                    if (SessionManager.current() != session) { finish(); return; }
                    new BiometricHelper(this).borrar();
                    SessionManager.clear();
                    current.setText(""); next.setText(""); confirm.setText("");
                    startActivity(new Intent(this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .putExtra("mensaje", "Contraseña actualizada. Inicia sesión con tu nueva contraseña."));
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isDestroyed() || isFinishing()) return;
                    busy = false; submit.setEnabled(true);
                    mensaje(e instanceof ApiClient.Error ? e.getMessage()
                            : "No se pudo confirmar el cambio. Comprueba la conexión. Si se guardó, accede con la nueva contraseña.");
                });
            }
        }, "senda-password").start();
    }
}
