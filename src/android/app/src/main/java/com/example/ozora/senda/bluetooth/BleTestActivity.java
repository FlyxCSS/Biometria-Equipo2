/*
 * ============================================================
 * Nombre: BleTestActivity.java
 * Descripción: Pantalla temporal para verificar anuncios BLE
 *              y valores O3 del llavero SENDA S-001.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ---
 * Aportación: Prueba aislada de escaneo BLE con permisos Android.
 * ============================================================
 */
package com.example.ozora.senda.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Activity temporal: no cambia MainActivity ni los contratos de los demás módulos. */
public final class BleTestActivity extends Activity implements BleScanner.Oyente {
    private static final int SOLICITUD_PERMISOS = 101;
    private BleScanner escaner;
    private TextView textoEstado;
    private TextView textoMedida;
    private Button botonIniciar;
    private Button botonDetener;

    /**
     * Diseño lógico: (Bundle) -> onCreate() -> (void)
     * Descripción: Inicializa la vista de prueba y configura sus botones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ozora.senda.R.layout.activity_ble_test);
        textoEstado = findViewById(com.example.ozora.senda.R.id.textoEstadoBle);
        textoMedida = findViewById(com.example.ozora.senda.R.id.textoMedidaBle);
        botonIniciar = findViewById(com.example.ozora.senda.R.id.botonIniciarBle);
        botonDetener = findViewById(com.example.ozora.senda.R.id.botonDetenerBle);
        escaner = new BleScanner(this, this);
        botonIniciar.setOnClickListener(v -> comprobarEIniciar());
        botonDetener.setOnClickListener(v -> detener());
        mostrarEstado("Listo. Activa Bluetooth y pulsa Iniciar escaneo.");
    }

    /**
     * Diseño lógico: () -> comprobarEIniciar() -> (void)
     * Descripción: Verifica Bluetooth y solicita permisos de forma previa al escaneo.
     */
    private void comprobarEIniciar() {
        BluetoothManager gestor = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adaptador = gestor == null ? null : gestor.getAdapter();
        if (adaptador == null) {
            mostrarEstado("Este teléfono no dispone de Bluetooth.");
            return;
        }
        List<String> pendientes = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            anadirSiFalta(pendientes, Manifest.permission.BLUETOOTH_SCAN);
            anadirSiFalta(pendientes, Manifest.permission.BLUETOOTH_CONNECT);
        }
        anadirSiFalta(pendientes, Manifest.permission.ACCESS_FINE_LOCATION);
        if (!pendientes.isEmpty()) {
            requestPermissions(pendientes.toArray(new String[0]), SOLICITUD_PERMISOS);
            return;
        }
        try {
            if (!adaptador.isEnabled()) {
                mostrarEstado("Activa Bluetooth desde los ajustes y vuelve a pulsar Iniciar.");
                return;
            }
        } catch (SecurityException error) {
            mostrarEstado("Falta permiso para consultar Bluetooth.");
            return;
        }
        if (escaner.iniciarEscaneo()) {
            mostrarEstado("Buscando tramas SENDA... Acerca el llavero S-001.");
        }
    }

    /**
     * Diseño lógico: (List<String>, String) -> anadirSiFalta() -> (void)
     * Descripción: Añade un permiso a la solicitud si Android no lo ha concedido.
     */
    private void anadirSiFalta(List<String> pendientes, String permiso) {
        if (checkSelfPermission(permiso) != PackageManager.PERMISSION_GRANTED) {
            pendientes.add(permiso);
        }
    }

    /**
     * Diseño lógico: (int, String[], int[]) -> onRequestPermissionsResult() -> (void)
     * Descripción: Continua el escaneo solo si se concedieron los permisos.
     */
    @Override
    public void onRequestPermissionsResult(int codigo, String[] permisos, int[] resultados) {
        super.onRequestPermissionsResult(codigo, permisos, resultados);
        if (codigo != SOLICITUD_PERMISOS) return;
        if (resultados.length == 0) {
            mostrarEstado("Solicitud de permisos cancelada.");
            return;
        }
        for (int resultado : resultados) {
            if (resultado != PackageManager.PERMISSION_GRANTED) {
                mostrarEstado("Permiso denegado. Habilítalo en Ajustes de SENDA.");
                return;
            }
        }
        comprobarEIniciar();
    }

    /**
     * Diseño lógico: (IBeaconParser.Resultado, String) -> alRecibirTrama() -> (void)
     * Descripción: Muestra el contenido de una trama iBeacon válida en pantalla.
     */
    @Override
    public void alRecibirTrama(IBeaconParser.Resultado medida, String direccionMac) {
        runOnUiThread(() -> {
            String valor = medida.esOzono()
                    ? String.format(Locale.ROOT, "%d ppb (%.3f ppm)", medida.minor, medida.minor / 1000.0)
                    : "Tipo " + medida.tipo + "; Minor: " + medida.minor;
            textoMedida.setText("Llavero: " + (medida.nombreBle == null ? "nombre no anunciado" : medida.nombreBle)
                    + "\nMAC: " + direccionMac
                    + "\nUUID: " + medida.uuid
                    + "\nMajor: " + medida.major + " / Contador: " + medida.contador
                    + "\nMinor: " + medida.minor
                    + "\nO3: " + valor
                    + "\nRSSI: " + medida.rssi + " dBm");
            mostrarEstado("Trama SENDA recibida correctamente.");
        });
    }

    /**
     * Diseño lógico: (String) -> alOcurrirError() -> (void)
     * Descripción: Muestra los errores informados por el escáner.
     */
    @Override
    public void alOcurrirError(String mensaje) {
        runOnUiThread(() -> mostrarEstado(mensaje));
    }

    /**
     * Diseño lógico: (String) -> mostrarEstado() -> (void)
     * Descripción: Actualiza el texto de estado de la prueba BLE.
     */
    private void mostrarEstado(String mensaje) {
        textoEstado.setText(mensaje);
    }

    /**
     * Diseño lógico: () -> detener() -> (void)
     * Descripción: Detiene el escaneo si está activo.
     */
    private void detener() {
        if (escaner != null) escaner.detenerEscaneo();
        mostrarEstado("Escaneo detenido.");
    }

    /**
     * Diseño lógico: () -> onStop() -> (void)
     * Descripción: Detiene el escaneo cuando la pantalla deja de ser visible.
     */
    @Override
    protected void onStop() {
        detener();
        super.onStop();
    }
}
