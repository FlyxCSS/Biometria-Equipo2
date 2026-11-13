package com.example.ozora.senda.qr;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;

import com.example.ozora.senda.ui.FieldScreen;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

/**
 * Selecciona localmente un llavero mediante QR o entrada manual.
 * Validar el identificador no equivale a autorizarlo en el servidor.
 * Integración de la pantalla de Persona 2 con el escáner de Persona 1.
 */
public final class QrScannerActivity extends FieldScreen {
    private EditText codigo;
    private Button escanear;

    @Override protected void onStart() {
        super.onStart();
        if (com.example.ozora.senda.auth.SessionManager.current() == null) finish();
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (com.example.ozora.senda.auth.SessionManager.current() == null) {
            finish();
            return;
        }
        pagina("Llavero de campo");
        texto("Escanea la etiqueta o introduce el código del llavero.", 16, false);
        codigo = campo("Ej. S-001", InputType.TYPE_CLASS_TEXT);
        String anterior = getIntent().getStringExtra("sensor");
        if (anterior != null) codigo.setText(anterior);
        contenido.addView(codigo);
        escanear = boton("Escanear QR", this::iniciarEscaneo);
        boton("Seleccionar", () -> seleccionar(codigo.getText().toString()));
        boton("Solo GPS", () -> devolver(null));
        mensajes();
    }

    /** Utiliza Google Play Services Code Scanner sin depender de otra app QR. */
    private void iniciarEscaneo() {
        if (escanear == null || !escanear.isEnabled()) return;
        escanear.setEnabled(false);
        GmsBarcodeScannerOptions opciones = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        try {
            GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this, opciones);
            scanner.startScan()
                    .addOnSuccessListener(barcode -> {
                        if (!isFinishing() && !isDestroyed())
                            seleccionar(barcode.getRawValue());
                    })
                    .addOnCanceledListener(() -> {
                        if (!isFinishing() && !isDestroyed()) mensaje("Lectura cancelada.");
                    })
                    .addOnFailureListener(error -> {
                        if (!isFinishing() && !isDestroyed())
                            mensaje("No se pudo abrir el lector QR. Comprueba Google Play Services y la conexión. También puedes introducir el código manualmente.");
                    })
                    .addOnCompleteListener(tarea -> {
                        if (!isFinishing() && !isDestroyed()) escanear.setEnabled(true);
                    });
        } catch (RuntimeException error) {
            escanear.setEnabled(true);
            mensaje("No se pudo iniciar el lector QR. Introduce el código manualmente.");
        }
    }

    private void seleccionar(String valor) {
        String sensor = QrLlaveroParser.extraerIdentificador(valor);
        if (sensor == null) mensaje("Código inválido. Usa S- seguido de 3 a 6 dígitos.");
        else devolver(sensor);
    }

    private void devolver(String sensor) {
        setResult(RESULT_OK, new android.content.Intent().putExtra("sensor", sensor));
        finish();
    }
}
