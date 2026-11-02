/*
 * Nombre: LlaveroFlowTestActivity.java
 * Descripción: Prueba conjunta QR + BLE filtrado + coordenadas GPS.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ...
 * Aportación: Demostración integrada sin atribuir autorización del servidor.
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
import com.example.ozora.senda.bluetooth.BleScanner;
import com.example.ozora.senda.bluetooth.IBeaconParser;
import com.example.ozora.senda.location.LocationTracker;
import com.example.ozora.senda.qr.QrLlaveroParser;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Sólo para desarrollo: la selección por QR NO es una vinculación autorizada. */
public final class LlaveroFlowTestActivity extends Activity implements BleScanner.Oyente, LocationTracker.Oyente {
    private static final int SOLICITUD_PERMISOS = 706;
    private TextView estado, llavero, lectura, gps;
    private BleScanner ble;
    private LocationTracker localizacion;
    private String seleccionado;

    // Diseño lógico: (Bundle) -> onCreate() -> (void)
    // Descripción: Inicializa controles independientes de MainActivity.
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llavero_flow_test);
        estado = findViewById(R.id.sendaEstado);
        llavero = findViewById(R.id.sendaLlavero);
        lectura = findViewById(R.id.sendaLectura);
        gps = findViewById(R.id.sendaGps);
        ble = new BleScanner(this, this);
        localizacion = new LocationTracker(this, this);
        findViewById(R.id.sendaQr).setOnClickListener(v -> leerQr());
        findViewById(R.id.sendaIniciar).setOnClickListener(v -> iniciar());
        findViewById(R.id.sendaDetener).setOnClickListener(v -> detener());
        estado.setText("Escanea QR para elegir un llavero. NO vinculado al servidor.");
    }

    // Diseño lógico: () -> leerQr() -> (void)
    // Descripción: Lee el identificador local, sin guardar una asignación autorizada.
    private void leerQr() {
        GmsBarcodeScannerOptions opciones = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE).build();
        GmsBarcodeScanning.getClient(this, opciones).startScan()
                .addOnSuccessListener(codigo -> {
                    String id = QrLlaveroParser.extraerIdentificador(codigo.getRawValue());
                    if (id == null) { estado.setText("QR no válido: utiliza S-001, S-002, etc."); return; }
                    detener();
                    seleccionado = id;
                    llavero.setText("Seleccionado: " + id + " (SIN autorización del servidor)");
                    lectura.setText("Sin lecturas del llavero elegido.");
                    estado.setText("QR leído. Pulsa Iniciar BLE + GPS para probar el filtro.");
                })
                .addOnCanceledListener(() -> estado.setText("Lectura QR cancelada."))
                .addOnFailureListener(e -> estado.setText("Error de lectura QR: " + e.getMessage()));
    }

    // Diseño lógico: () -> iniciar() -> (void)
    // Descripción: Verifica selección y pide los permisos necesarios al sistema.
    private void iniciar() {
        if (seleccionado == null) { estado.setText("Primero debes escanear un QR válido."); return; }
        List<String> faltantes = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 31) {
            agregarSiFalta(faltantes, Manifest.permission.BLUETOOTH_SCAN);
            agregarSiFalta(faltantes, Manifest.permission.BLUETOOTH_CONNECT);
        }
        agregarSiFalta(faltantes, Manifest.permission.ACCESS_FINE_LOCATION);
        if (!faltantes.isEmpty()) {
            requestPermissions(faltantes.toArray(new String[0]), SOLICITUD_PERMISOS);
            return;
        }
        boolean recibido = ble.iniciarEscaneo();
        // El inicio de escucha NO equivale a recibir una ubicación.
        gps.setText("Buscando ubicación… Activa la ubicación precisa y prueba cerca de una ventana o fuera.");
        boolean posicionado = localizacion.iniciar();
        if (!posicionado) {
            gps.setText("No se ha podido iniciar la ubicación. Revisa permisos y ajustes.");
        }
        if (recibido && posicionado) {
            estado.setText("Escaneando " + seleccionado + " y buscando ubicación (modo prueba).");
        } else if (recibido) {
            estado.setText("BLE activo; la ubicación necesita atención.");
        } else if (posicionado) {
            estado.setText("Buscando ubicación; BLE no ha podido iniciarse.");
        }
    }

    // Diseño lógico: (List<String>, String) -> agregarSiFalta() -> (void)
    private void agregarSiFalta(List<String> faltantes, String permiso) {
        if (checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED) faltantes.add(permiso);
    }

    // Diseño lógico: (int, String[], int[]) -> onRequestPermissionsResult() -> (void)
    @Override public void onRequestPermissionsResult(int codigo, String[] permisos, int[] resultados) {
        super.onRequestPermissionsResult(codigo, permisos, resultados);
        if (codigo != SOLICITUD_PERMISOS) return;
        if (resultados.length == 0) { estado.setText("Solicitud cancelada."); return; }
        for (int resultado : resultados) if (resultado != PackageManager.PERMISSION_GRANTED) {
            estado.setText("Se necesitan permisos BLE y ubicación precisa."); return;
        }
        iniciar();
    }

    // Diseño lógico: (IBeaconParser.Resultado, String) -> alRecibirTrama() -> (void)
    // Descripción: Filtra anuncios SENDA por el nombre BLE escaneado en el QR.
    @Override public void alRecibirTrama(IBeaconParser.Resultado medida, String mac) {
        if (seleccionado == null || medida.nombreBle == null || !seleccionado.equalsIgnoreCase(medida.nombreBle) || !medida.esOzono()) return;
        runOnUiThread(() -> lectura.setText(String.format(Locale.ROOT,
                "Llavero %s | O3: %d ppb (%.3f ppm) | contador %d | RSSI %d dBm",
                seleccionado, medida.minor, medida.minor / 1000.0, medida.contador, medida.rssi)));
    }

    // Diseño lógico: (Location) -> alRecibirUbicacion() -> (void)
    @Override public void alRecibirUbicacion(Location ubicacion) {
        runOnUiThread(() -> gps.setText(String.format(Locale.ROOT,
                "Ubicación (%s): %.6f, %.6f | precisión estimada: %.1f m | tiempo: %d ms",
                ubicacion.getProvider(), ubicacion.getLatitude(), ubicacion.getLongitude(),
                ubicacion.getAccuracy(), ubicacion.getTime())));
    }

    // Diseño lógico: (String) -> alOcurrirError() -> (void)
    @Override public void alOcurrirError(String mensaje) { runOnUiThread(() -> estado.setText(mensaje)); }

    // Diseño lógico: () -> detener() -> (void)
    private void detener() {
        if (ble != null) ble.detenerEscaneo();
        if (localizacion != null) localizacion.detener();
        if (estado != null) estado.setText("Lecturas detenidas.");
    }

    // Diseño lógico: () -> onStop() -> (void)
    @Override protected void onStop() { detener(); super.onStop(); }
}
