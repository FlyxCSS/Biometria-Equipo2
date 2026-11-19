/*
 * Nombre: QrTestActivity.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
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



    /** --------------------
     * Diseño lógico: estado: EstadoVista --> onCreate() -->
     * Descripción: Prepara los elementos de la pantalla y el botón de escaneo
     * -------------------- */
    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.activity_qr_test);
        textoResultado = findViewById(R.id.textoResultadoQr);
        botonEscanear = findViewById(R.id.botonEscanearQr);
        botonEscanear.setOnClickListener(v -> iniciarLecturaQr());
    }



    /** --------------------
     * Diseño lógico: iniciarLecturaQr() -->
     * Descripción: Abre el escáner QR de Google Play Services y procesa su respuesta
     * -------------------- */
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



    /** --------------------
     * Diseño lógico: contenidoQr: Text --> mostrarIdentificador() -->
     * Descripción: Valida el código con QrLlaveroParser y muestra el resultado
     * -------------------- */
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
