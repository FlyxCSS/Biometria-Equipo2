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
import com.example.ozora.senda.auth.Login;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.network.ApiClient;
import com.example.ozora.senda.network.LogicaFake;
import java.nio.charset.StandardCharsets;

/** La foto se sincroniza con la cuenta; nombre visible y teléfono se conservan localmente. */
public final class AccountActivity extends FieldScreen {
    private SessionManager session;
    private SharedPreferences prefs;
    private String photo;
    private boolean busy;
    private final ActivityResultLauncher<String[]> picker = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null || session == null || busy) return;
                busy = true;
                findViewById(R.id.profile_save).setEnabled(false);
                new Thread(() -> {
                    try {
                        android.graphics.BitmapFactory.Options bounds = new android.graphics.BitmapFactory.Options();
                        bounds.inJustDecodeBounds = true;
                        try (java.io.InputStream input = getContentResolver().openInputStream(uri)) {
                            android.graphics.BitmapFactory.decodeStream(input, null, bounds);
                        }
                        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw new java.io.IOException();
                        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
                        options.inSampleSize = 1;
                        while (Math.max(bounds.outWidth, bounds.outHeight) / options.inSampleSize > 512) {
                            options.inSampleSize = Math.max(1, options.inSampleSize) * 2;
                        }
                        android.graphics.Bitmap bitmap;
                        try (java.io.InputStream input = getContentResolver().openInputStream(uri)) {
                            bitmap = android.graphics.BitmapFactory.decodeStream(input, null, options);
                        }
                        if (bitmap == null) throw new java.io.IOException();
                        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, output);
                        bitmap.recycle();
                        if (output.size() > 100000) throw new java.io.IOException();
                        String encoded = "data:image/jpeg;base64," + android.util.Base64.encodeToString(
                                output.toByteArray(), android.util.Base64.NO_WRAP);
                        runOnUiThread(() -> { if (!isDestroyed() && SessionManager.current() == session) {
                            photo = encoded; showPhoto(); busy = false;
                            findViewById(R.id.profile_save).setEnabled(true);
                        } });
                    } catch (Exception error) {
                        runOnUiThread(() -> { if (!isDestroyed()) {
                            busy = false; findViewById(R.id.profile_save).setEnabled(true);
                            ((TextView)findViewById(R.id.profile_feedback))
                                .setText("No se pudo preparar la foto. Selecciona otra imagen.");
                        } });
                    }
                }, "senda-foto").start();
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
        findViewById(R.id.profile_change_photo).setOnClickListener(v -> {
            if (!busy) picker.launch(new String[]{"image/*"});
        });
        findViewById(R.id.profile_remove_photo).setOnClickListener(v -> {
            if (busy) return;
            new android.app.AlertDialog.Builder(this)
                    .setMessage("¿Estás seguro de que quieres eliminar la foto de perfil?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        if (!busy && SessionManager.current() == session) { photo = null; showPhoto(); }
                    }).show();
        });
        findViewById(R.id.profile_save).setOnClickListener(v -> {
            if (busy) return;
            new android.app.AlertDialog.Builder(this)
                    .setMessage("¿Estás seguro de que quieres guardar los cambios?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Guardar", (dialog, which) -> save()).show();
        });
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
        mostrarFoto(view, photo);
        TextView eliminar = findViewById(R.id.profile_remove_photo);
        boolean tieneFoto = photo != null && !photo.isEmpty();
        eliminar.setEnabled(tieneFoto);
        eliminar.setTextColor(tieneFoto ? 0xff7a2032 : 0xff9a9e9a);
    }

    public static void mostrarFoto(ImageView view, String foto) {
        view.setImageResource(R.drawable.account_avatar);
        if (foto == null) return;
        try {
            if (foto.startsWith("data:image/jpeg;base64,")) {
                byte[] bytes = android.util.Base64.decode(foto.substring(23), android.util.Base64.NO_WRAP);
                view.setImageBitmap(android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            } else view.setImageURI(Uri.parse(foto));
        } catch (RuntimeException ignored) { view.setImageResource(R.drawable.account_avatar); }
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
        if (busy) return;
        prefs.edit().putString("profile_name", value).putString("profile_phone", number).apply();
        if (android.text.TextUtils.equals(photo, prefs.getString("profile_photo", null))) {
            finish();
            return;
        }
        busy = true;
        findViewById(R.id.profile_save).setEnabled(false);
        final String seleccionada = photo;
        new Thread(() -> {
            try {
                if (seleccionada != null && !seleccionada.startsWith("data:image/jpeg;base64,"))
                    throw new java.io.IOException("Vuelve a seleccionar la foto para guardarla en tu cuenta.");
                new LogicaFake(this).actualizarFotoPerfil(session.token, seleccionada);
                runOnUiThread(() -> {
                    if (isDestroyed() || SessionManager.current() != session) return;
                    prefs.edit().putString("profile_name", value).putString("profile_phone", number)
                            .putString("profile_photo", seleccionada).apply();
                    busy = false; findViewById(R.id.profile_save).setEnabled(true);
                    finish();
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    if (isDestroyed()) return;
                    busy = false; findViewById(R.id.profile_save).setEnabled(true);
                    ((TextView)findViewById(R.id.profile_feedback)).setText(error instanceof ApiClient.Error
                            ? error.getMessage() : "No se pudo guardar el perfil. Selecciona la foto y comprueba la conexión.");
                });
            }
        }, "senda-perfil").start();
    }

    private void security() {
        setContentView(R.layout.activity_security);
        protegerBarras(findViewById(R.id.security_root));
        contenido = findViewById(R.id.security_content);
        findViewById(R.id.security_back).setOnClickListener(v -> finish());
        EditText current = findViewById(R.id.security_current);
        EditText next = findViewById(R.id.security_next);
        EditText confirm = findViewById(R.id.security_confirm);
        findViewById(R.id.security_submit).setOnClickListener(v -> changePassword(current, next, confirm));
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
                    startActivity(new Intent(this, Login.class)
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
