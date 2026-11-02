/*
 * Nombre: LlaveroFlowTestActivity.java
 * Descripción: Pantalla de diagnóstico ordenada para QR, BLE y ubicación.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: UI de prueba desacoplada mediante CapturaLlaveroController.
 */
package com.example.ozora.senda.integration;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import com.example.ozora.senda.R;
import com.example.ozora.senda.model.MedicionAmbiental;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class LlaveroFlowTestActivity extends Activity implements CapturaLlaveroController.Oyente {
    private static final int SOLICITUD = 706;
    private TextView estado, llavero, lectura, gps;
    private CapturaLlaveroController controlador;

    /** Diseño lógico: (Bundle) -> onCreate() -> (void). */
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_llavero_flow_test);
        estado = findViewById(R.id.sendaEstado);
        llavero = findViewById(R.id.sendaLlavero);
        lectura = findViewById(R.id.sendaLectura);
        gps = findViewById(R.id.sendaGps);
        controlador = new CapturaLlaveroController(this, this);
        findViewById(R.id.sendaQr).setOnClickListener(v -> leerQr());
        findViewById(R.id.sendaIniciar).setOnClickListener(v -> solicitarEIniciar());
        findViewById(R.id.sendaDetener).setOnClickListener(v -> detener());
        estado.setText("Preparado. Escanea el QR de un llavero SENDA.");
    }
    /** Diseño lógico: () -> leerQr() -> (void). */
    private void leerQr() {
        GmsBarcodeScannerOptions op = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();
        GmsBarcodeScanning.getClient(this, op).startScan()
            .addOnSuccessListener(c -> {
                if (!controlador.seleccionarQr(c.getRawValue())) {
                    estado.setText("QR incorrecto: usa S-001, S-002..."); return;
                }
                llavero.setText(controlador.getLlaveroSeleccionado() + "  ·  Seleccionado (sin autorización)");
                lectura.setText("Sin mediciones. Inicia BLE y ubicación.");
                gps.setText("Sin ubicación. Esperando inicio.");
            })
            .addOnCanceledListener(() -> estado.setText("Lectura de QR cancelada."))
            .addOnFailureListener(e -> estado.setText("No se pudo leer el QR: " + e.getMessage()));
    }
    /** Diseño lógico: () -> solicitarEIniciar() -> (void). */
    private void solicitarEIniciar() {
        if (controlador.getLlaveroSeleccionado() == null) {
            estado.setText("Escanea primero un QR válido."); return;
        }
        List<String> f = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 31) {
            siFalta(f, Manifest.permission.BLUETOOTH_SCAN);
            siFalta(f, Manifest.permission.BLUETOOTH_CONNECT);
        }
        siFalta(f, Manifest.permission.ACCESS_FINE_LOCATION);
        if (!f.isEmpty()) { requestPermissions(f.toArray(new String[0]), SOLICITUD); return; }
        gps.setText("Buscando una ubicación precisa y reciente...");
        if (!controlador.iniciar()) gps.setText("No se pudo iniciar. Revisa los ajustes del móvil.");
    }
    /** Diseño lógico: (List<String>, String) -> siFalta() -> (void). */
    private void siFalta(List<String> f, String permiso) {
        if (checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED) f.add(permiso);
    }
    /** Diseño lógico: (int, String[], int[]) -> onRequestPermissionsResult() -> (void). */
    @Override public void onRequestPermissionsResult(int request, String[] p, int[] res) {
        super.onRequestPermissionsResult(request, p, res);
        if (request != SOLICITUD) return;
        if (res.length == 0) { estado.setText("Permisos cancelados."); return; }
        for (int r : res) if (r != PackageManager.PERMISSION_GRANTED) {
            estado.setText("Faltan permisos de Bluetooth y ubicación precisa."); return;
        }
        solicitarEIniciar();
    }
    /** Diseño lógico: (String) -> alCambiarEstado() -> (void). */
    @Override public void alCambiarEstado(String texto) { runOnUiThread(() -> estado.setText(texto)); }
    /** Diseño lógico: (Location) -> alRecibirUbicacion() -> (void). */
    @Override public void alRecibirUbicacion(Location loc) {
        gps.setText(String.format(Locale.ROOT,
           "%s  ·  %.6f, %.6f\nPrecisión estimada: ±%.1f m",
           loc.getProvider() == null ? "Ubicación" : loc.getProvider().toUpperCase(Locale.ROOT),
           loc.getLatitude(), loc.getLongitude(), loc.getAccuracy()));
    }
    /** Diseño lógico: (MedicionAmbiental, int) -> alPrepararMedicion() -> (void). */
    @Override public void alPrepararMedicion(MedicionAmbiental m, int rssi) {
        lectura.setText(String.format(Locale.ROOT,
                "%d ppb  ·  %.3f ppm\nLlavero: %s  ·  Contador: %d  ·  RSSI: %d dBm\nUbicación: %.6f, %.6f  ·  ±%.1f m\nUTC: %s\nNO ENVIADA",
                m.valorPpb, m.convertirAPpm(), m.idLlavero, m.contador, rssi,
                m.latitud, m.longitud, m.precisionMetros, m.fechaIso8601));
    }
    /** Diseño lógico: (String) -> alDescartarMedicion() -> (void). */
    @Override public void alDescartarMedicion(String motivo) { lectura.setText("Esperando medición válida: " + motivo); }
    /** Diseño lógico: () -> detener() -> (void). */
    private void detener() { controlador.detener(); estado.setText("Captura detenida."); }
    /** Diseño lógico: () -> onStop() -> (void). */
    @Override protected void onStop() { controlador.detener(); super.onStop(); }
}
