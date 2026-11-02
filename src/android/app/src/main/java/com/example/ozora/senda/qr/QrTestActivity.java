/*
 * ============================================================
 * Nombre: QrTestActivity.java
 * Descripción: Prueba independiente de lectura de QR para llaveros SENDA.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: Lectura de un código QR y validación local del identificador.
 * ============================================================
 */
package com.example.ozora.senda.qr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.ozora.senda.R;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

/** Pantalla temporal de escaneo QR, separada de MainActivity. */
public final class QrTestActivity extends Activity {
    private TextView textoResultado;
    private Button botonEscanear;

    /*
     * Diseño lógico: (Bundle estado) -> onCreate(Bundle) -> (void)
     * Descripción: Prepara los elementos de la pantalla y el botón de escaneo.
     */
    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.activity_qr_test);
        textoResultado = findViewById(R.id.textoResultadoQr);
        botonEscanear = findViewById(R.id.botonEscanearQr);
        botonEscanear.setOnClickListener(v -> iniciarLecturaQr());
    }

    /*
     * Diseño lógico: () -> iniciarLecturaQr() -> (void)
     * Descripción: Abre el escáner QR de Google Play Services y procesa su respuesta.
     */
    private void iniciarLecturaQr() {
        botonEscanear.setEnabled(false);
        textoResultado.setText("Abriendo cámara para leer QR...");

        GmsBarcodeScannerOptions opciones = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        GmsBarcodeScanner escaner = GmsBarcodeScanning.getClient(this, opciones);
        escaner.startScan()
                .addOnSuccessListener(codigo -> mostrarIdentificador(codigo.getRawValue()))
                .addOnCanceledListener(() -> textoResultado.setText("Lectura cancelada."))
                .addOnFailureListener(error -> textoResultado.setText(
                        "No se pudo leer el QR: " + error.getMessage()))
                .addOnCompleteListener(tarea -> botonEscanear.setEnabled(true));
    }

    /*
     * Diseño lógico: (String contenidoQr) -> mostrarIdentificador(String) -> (void)
     * Descripción: Valida el código con QrLlaveroParser y muestra el resultado.
     *              No realiza ninguna vinculación ni llama al servidor.
     */
    private void mostrarIdentificador(String contenidoQr) {
        String identificador = QrLlaveroParser.extraerIdentificador(contenidoQr);
        if (identificador == null) {
            textoResultado.setText("QR no válido. Debe contener S-001, S-002, etc.");
            return;
        }
        textoResultado.setText("QR leído: " + identificador
                + "\nIdentificador válido. Vinculación pendiente de validar en el servidor.");
    }
}
