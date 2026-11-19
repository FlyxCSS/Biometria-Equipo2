package com.example.ozora.senda.qr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.ozora.senda.R;
import com.example.ozora.senda.auth.SessionManager;
import com.example.ozora.senda.ui.FieldScreen;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

/** Selección local; el servidor sigue comprobando la asignación del llavero. */
public final class QrScannerActivity extends FieldScreen {
    private Button scan;
    private SessionManager session;
    private boolean manualVisible;
    @Override protected void onStart() {
        super.onStart();
        if (session == null || SessionManager.current() != session) finish();
    }
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        session = SessionManager.current();
        if (session == null) { finish(); return; }
        setContentView(R.layout.activity_link_keyring);
        protegerBarras(findViewById(R.id.qr_scroll));
        scan = findViewById(R.id.qr_scan);
        scan.setOnClickListener(v -> scan());
        findViewById(R.id.qr_frame).setOnClickListener(v -> scan());
        androidx.core.view.WindowInsetsControllerCompat bars =
                new androidx.core.view.WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        bars.setAppearanceLightStatusBars(false);
        findViewById(R.id.qr_back).setOnClickListener(v -> finish());
        findViewById(R.id.qr_manual).setOnClickListener(v -> manual());
        findViewById(R.id.qr_gps).setOnClickListener(v -> result(null));
        EditText code = findViewById(R.id.qr_code);
        if (state == null) code.setText(getIntent().getStringExtra("sensor"));
        code.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { code.setError(null); }
            @Override public void afterTextChanged(android.text.Editable value) {}
        });
        manualVisible = state != null && state.getBoolean("manual");
        showManual();
        findViewById(R.id.qr_confirm).setOnClickListener(v -> selectManual());
        code.setOnEditorActionListener((v, action, event) -> {
            if (action != android.view.inputmethod.EditorInfo.IME_ACTION_DONE) return false;
            selectManual(); return true;
        });
    }
    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean("manual", manualVisible);
    }
    private void manual() { manualVisible = !manualVisible; showManual(); }
    private void showManual() {
        findViewById(R.id.qr_manual_group).setVisibility(manualVisible ? View.VISIBLE : View.GONE);
        findViewById(R.id.qr_camera_panel).setVisibility(manualVisible ? View.GONE : View.VISIBLE);
        ((TextView)findViewById(R.id.qr_manual)).setText(manualVisible ? R.string.qr_camera : R.string.qr_manual);
    }
    private void selectManual() {
        EditText code = findViewById(R.id.qr_code);
        String sensor = QrLlaveroParser.extraerIdentificador(code.getText().toString());
        if (sensor == null) { code.setError(getString(R.string.qr_invalid)); code.requestFocus(); }
        else result(sensor);
    }
    private void scan() {
        if (!scan.isEnabled()) return;
        scan.setEnabled(false); message(getString(R.string.qr_opening));
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();
        try {
            GmsBarcodeScanning.getClient(this, options).startScan()
                    .addOnSuccessListener(barcode -> {
                        if (isFinishing() || isDestroyed()) return;
                        String sensor = QrLlaveroParser.extraerIdentificador(barcode.getRawValue());
                        if (sensor == null) message(getString(R.string.qr_invalid)); else result(sensor);
                    })
                    .addOnCanceledListener(() -> message(getString(R.string.qr_cancelled)))
                    .addOnFailureListener(error -> message(getString(R.string.qr_failure)))
                    .addOnCompleteListener(task -> {
                        if (!isFinishing() && !isDestroyed()) scan.setEnabled(true);
                    });
        } catch (RuntimeException error) {
            scan.setEnabled(true); message(getString(R.string.qr_failure));
        }
    }
    private void message(String text) {
        if (isFinishing() || isDestroyed()) return;
        TextView view = findViewById(R.id.qr_feedback);
        view.setText(text); view.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
    }
    private void result(String sensor) {
        if (SessionManager.current() != session) { finish(); return; }
        setResult(RESULT_OK, new android.content.Intent().putExtra("sensor", sensor));
        finish();
    }
}
