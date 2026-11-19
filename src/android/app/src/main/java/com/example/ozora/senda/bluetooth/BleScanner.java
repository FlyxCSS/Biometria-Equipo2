/*
 * Nombre: BleScanner.java
 * Descripción: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplicación de campo: acceso, captura, recorridos y presentación Android.
 */
package com.example.ozora.senda.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Escáner BLE aislado de la interfaz gráfica.
 * La Activity que lo utilice se encarga de solicitar los permisos en tiempo de ejecución.
 */
public final class BleScanner {

    /** Recibe las medidas identificadas como anuncios SENDA y posibles errores. */
    public interface Oyente {
        // Diseño lógico: (IBeaconParser.Resultado, String) -> alRecibirTrama() -> (void)
        // Descripción: Notifica una trama válida y la dirección del emisor.

        /** --------------------
         * Diseño lógico: medida: IBeaconParserResultado, direccionMac: Text --> alRecibirTrama() -->
         * Descripción: Al Recibir Trama. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        void alRecibirTrama(IBeaconParser.Resultado medida, String direccionMac);

        // Diseño lógico: (String) -> alOcurrirError() -> (void)
        // Descripción: Comunica un error al componente que inició el escaneo.

        /** --------------------
         * Diseño lógico: mensaje: Text --> alOcurrirError() -->
         * Descripción: Al Ocurrir Error. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        void alOcurrirError(String mensaje);
    }

    private final Context contexto;
    private final Oyente oyente;
    private BluetoothLeScanner escaner;
    private volatile boolean escaneando;
    // El nombre y el iBeacon pueden llegar en anuncios BLE diferentes de la misma MAC.
    private final Map<String, String> nombresPorMac = new ConcurrentHashMap<>();

    private final ScanCallback callback = new ScanCallback() {

        /** --------------------
         * Diseño lógico: callbackType: Z, resultado: ScanResult --> onScanResult() -->
         * Descripción: On Scan Result. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        @Override
        // Diseño lógico: (int, ScanResult) -> onScanResult() -> (void)
        // Descripción: Analiza los datos del fabricante de cada anuncio recibido.
        public void onScanResult(int callbackType, ScanResult resultado) {
            if (!escaneando || resultado == null || resultado.getDevice() == null || resultado.getScanRecord() == null) {
                return;
            }
            String mac;
            try {
                mac = resultado.getDevice().getAddress();
            } catch (SecurityException ex) {
                oyente.alOcurrirError("Permiso Bluetooth denegado al leer el dispositivo.");
                return;
            }
            if (mac == null) return;
            String nombre = resultado.getScanRecord().getDeviceName();
            if (nombre != null && !nombre.trim().isEmpty()) {
                nombresPorMac.put(mac, nombre);
            } else {
                nombre = nombresPorMac.get(mac);
            }
            byte[] fabricante = resultado.getScanRecord()
                    .getManufacturerSpecificData(IBeaconParser.COMPANY_ID_APPLE);
            IBeaconParser.Resultado medida = IBeaconParser.interpretar(
                    fabricante, nombre, resultado.getRssi());
            // El parser verifica la UUID exacta del proyecto antes de notificar.
            if (medida != null) oyente.alRecibirTrama(medida, mac);
        }


        /** --------------------
         * Diseño lógico: codigoError: Z --> onScanFailed() -->
         * Descripción: On Scan Failed. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        @Override
        // Diseño lógico: (int) -> onScanFailed() -> (void)
        // Descripción: Notifica fallos de Android al iniciar o mantener el escaneo.
        public void onScanFailed(int codigoError) {
            escaneando = false;
            oyente.alOcurrirError("Error BLE al escanear. Código: " + codigoError);
        }
    };

    // Diseño lógico: (Context, Oyente) -> BleScanner() -> (BleScanner)
    // Descripción: Prepara el escáner sin iniciarlo.

    /** --------------------
     * Diseño lógico: contexto: Entorno, oyente: Oyente --> BleScanner() -->
     * BleScanner <--
     * Descripción: Construye BleScanner. Aplicación de campo: acceso, captura, recorridos y presentación Android
     * -------------------- */
    public BleScanner(Context contexto, Oyente oyente) {
        if (contexto == null || oyente == null) {
            throw new IllegalArgumentException("El contexto y el oyente son obligatorios");
        }
        this.contexto = contexto.getApplicationContext();
        this.oyente = oyente;
    }



    /** --------------------
     * Diseño lógico: iniciarEscaneo() -->
     * B <--
     * Descripción: Inicia escaneo BLE de baja latencia. Devuelve false y
     * -------------------- */
    public boolean iniciarEscaneo() {
        if (escaneando) {
            return true;
        }
        if (!tienePermisos()) {
            oyente.alOcurrirError("Faltan permisos Bluetooth/ubicación. Solicítalos desde la Activity.");
            return false;
        }
        BluetoothManager gestor = (BluetoothManager) contexto.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adaptador = gestor == null ? null : gestor.getAdapter();
        if (adaptador == null || !adaptador.isEnabled()) {
            oyente.alOcurrirError("Bluetooth no disponible o desactivado.");
            return false;
        }
        escaner = adaptador.getBluetoothLeScanner();
        if (escaner == null) {
            oyente.alOcurrirError("No se ha podido obtener el escáner BLE.");
            return false;
        }
        ScanSettings ajustes = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        try {
            // Sin filtro a nivel del sistema: el nombre puede anunciarse por separado.
            // Solo entregamos tramas iBeacon con UUID SENDA validada por IBeaconParser.
            nombresPorMac.clear();
            escaner.startScan(java.util.Collections.singletonList(new android.bluetooth.le.ScanFilter.Builder().build()), ajustes, callback);
            escaneando = true;
            return true;
        } catch (SecurityException ex) {
            oyente.alOcurrirError("Permiso BLE denegado: " + ex.getMessage());
            return false;
        } catch (IllegalStateException ex) {
            oyente.alOcurrirError("Bluetooth no disponible: " + ex.getMessage());
            return false;
        }
    }



    /** --------------------
     * Diseño lógico: detenerEscaneo() -->
     * Descripción: Detiene la exploración activa y libera la referencia al escáner
     * -------------------- */
    public void detenerEscaneo() {
        escaneando = false;
        nombresPorMac.clear();
        if (escaner == null) return;
        try {
            escaner.stopScan(callback);
        } catch (SecurityException ex) {
            oyente.alOcurrirError("No se ha podido detener el escaneo: " + ex.getMessage());
        } catch (IllegalStateException ex) {
            oyente.alOcurrirError("Bluetooth se ha desactivado.");
        } finally {
            escaneando = false;
            escaner = null;
            nombresPorMac.clear();
        }
    }



    /** --------------------
     * Diseño lógico: estaEscaneando() -->
     * B <--
     * Descripción: Indica si se ha solicitado un escaneo activo
     * -------------------- */
    public boolean estaEscaneando() {
        return escaneando;
    }



    /** --------------------
     * Diseño lógico: tienePermisos() -->
     * B <--
     * Descripción: Comprueba los permisos BLE/ubicación vigentes según la API
     * -------------------- */
    private boolean tienePermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return contexto.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED
                    && contexto.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED
                    && contexto.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return contexto.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
