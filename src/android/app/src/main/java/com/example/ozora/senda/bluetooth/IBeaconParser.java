/*
 * ============================================================
 * Nombre: IBeaconParser.java
 * Descripción: Interpreta los datos de fabricante de anuncios
 *              iBeacon emitidos por los llaveros SENDA.
 * Copyright (c) 2026 - Equipo 2 - SENDA / OZORA
 * Fecha: 02/11/2026
 * Autor: ---
 * Aportación: decodificación de UUID, tipo, contador, valor
 *             de medición y potencia de referencia BLE.
 * ============================================================
 */
package com.example.ozora.senda.bluetooth;

import java.util.Locale;

/**
 * Analizador independiente de la interfaz y de los permisos Android.
 * Recibe el contenido de los datos del fabricante (SIN el Company ID).
 */
public final class IBeaconParser {

    public static final int COMPANY_ID_APPLE = 0x004C;
    public static final int TIPO_O3 = 11;
    public static final int TIPO_TEMPERATURA = 12;
    public static final String UUID_PROYECTO = "45505347-2D47-5449-2D50-524F592D3341";
    private static final int LONGITUD_IBEACON = 23;

    // Diseño lógico: () -> IBeaconParser() -> (IBeaconParser)
    // Descripción: Evita crear instancias de una clase de utilidades estáticas.
    private IBeaconParser() {
    }

    /**
     * Diseño lógico: (byte[], String, int) -> interpretar() -> (Resultado|null)
     * Descripción: Decodifica un anuncio iBeacon y devuelve sus campos si el
     *              formato y UUID corresponden a SENDA. El nombre puede ser null.
     *              Devuelve null si el paquete no es válido o no es del proyecto.
     * @param datosFabricante Contenido de getManufacturerSpecificData(0x004C).
     * @param nombreBle Nombre anunciado por el dispositivo, si está disponible.
     * @param rssi Intensidad de señal capturada en dBm.
     */
    public static Resultado interpretar(byte[] datosFabricante, String nombreBle, int rssi) {
        if (datosFabricante == null || datosFabricante.length < LONGITUD_IBEACON) {
            return null;
        }
        if ((datosFabricante[0] & 0xFF) != 0x02 || (datosFabricante[1] & 0xFF) != 0x15) {
            return null;
        }

        String uuid = construirUuid(datosFabricante, 2);
        if (!UUID_PROYECTO.equals(uuid)) {
            return null;
        }

        int major = leerUint16(datosFabricante, 18);
        int minor = leerUint16(datosFabricante, 20);
        int tipo = (major >>> 8) & 0xFF;
        int contador = major & 0xFF;
        int potenciaReferencia = datosFabricante[22];

        return new Resultado(uuid, nombreBle, major, minor, tipo,
                contador, potenciaReferencia, rssi);
    }

    /**
     * Diseño lógico: (byte[], int) -> leerUint16() -> (int)
     * Descripción: Lee un entero de 16 bits sin signo en orden big-endian.
     */
    private static int leerUint16(byte[] datos, int posicion) {
        return ((datos[posicion] & 0xFF) << 8) | (datos[posicion + 1] & 0xFF);
    }

    /**
     * Diseño lógico: (byte[], int) -> construirUuid() -> (String)
     * Descripción: Convierte los 16 bytes del UUID al formato estándar 8-4-4-4-12.
     */
    private static String construirUuid(byte[] datos, int inicio) {
        StringBuilder cadena = new StringBuilder(36);
        for (int i = 0; i < 16; i++) {
            if (i == 4 || i == 6 || i == 8 || i == 10) {
                cadena.append('-');
            }
            cadena.append(String.format(Locale.ROOT, "%02X", datos[inicio + i] & 0xFF));
        }
        return cadena.toString();
    }

    /** Resultado inmutable del análisis de una trama iBeacon. */
    public static final class Resultado {
        public final String uuid;
        public final String nombreBle;
        public final int major;
        public final int minor;
        public final int tipo;
        public final int contador;
        public final int txPower;
        public final int rssi;

        // Diseño lógico: (String, String, int, int, int, int, int, int)
        //                 -> Resultado() -> (Resultado)
        // Descripción: Guarda los campos decodificados de un anuncio.
        private Resultado(String uuid, String nombreBle, int major, int minor,
                          int tipo, int contador, int txPower, int rssi) {
            this.uuid = uuid;
            this.nombreBle = nombreBle;
            this.major = major;
            this.minor = minor;
            this.tipo = tipo;
            this.contador = contador;
            this.txPower = txPower;
            this.rssi = rssi;
        }

        /**
         * Diseño lógico: () -> esOzono() -> (boolean)
         * Descripción: Indica si la medición codificada corresponde al tipo O3.
         */
        public boolean esOzono() {
            return tipo == TIPO_O3;
        }

        /**
         * Diseño lógico: () -> obtenerOzonoPpb() -> (Integer|null)
         * Descripción: Devuelve Minor como ppb solo si la trama es de ozono.
         */
        public Integer obtenerOzonoPpb() {
            return esOzono() ? minor : null;
        }

        /**
         * Diseño lógico: () -> obtenerOzonoPpm() -> (Double|null)
         * Descripción: Convierte el valor de ozono de ppb a ppm.
         */
        public Double obtenerOzonoPpm() {
            return esOzono() ? minor / 1000.0 : null;
        }
    }
}
