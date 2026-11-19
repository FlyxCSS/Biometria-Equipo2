/*
 * Nombre: IBeaconParser.java
 * Descripción: Decodifica UUID, tipo, contador y unidades de los anuncios.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Decodifica UUID, tipo, contador y unidades de los anuncios.
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
    public static final int TIPO_BATERIA = 14;
    public static final int NO_DISPONIBLE = 0xFFFF;
    public static final String UUID_PROYECTO = "4F5A4F52-412D-4754-492D-33412D42494F";
    private static final int LONGITUD_IBEACON = 23;

    // Diseño lógico: () -> IBeaconParser() -> (IBeaconParser)
    // Descripción: Evita crear instancias de una clase de utilidades estáticas.

    /** --------------------
     * Diseño lógico: IBeaconParser() -->
     * IBeaconParser <--
     * Descripción: Construye IBeaconParser. Decodifica UUID, tipo, contador y unidades de los anuncios
     * -------------------- */
    private IBeaconParser() {
    }



    /** --------------------
     * Diseño lógico: datosFabricante: [Dato], nombreBle: Text, rssi: Z --> interpretar() --x
     * Resultado <--
     * Descripción: Decodifica un anuncio iBeacon y devuelve sus campos si el
     * -------------------- */
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
        // Formato nuevo: Major completo = tipo. Mantener lectura de llaveros antiguos.
        boolean directo = major == TIPO_O3 || major == TIPO_TEMPERATURA || major == TIPO_BATERIA;
        int tipo = directo ? major : (major >>> 8) & 0xFF;
        int contador = directo ? -1 : major & 0xFF;
        if (tipo != TIPO_O3 && tipo != TIPO_TEMPERATURA && tipo != TIPO_BATERIA) return null;
        int potenciaReferencia = datosFabricante[22];

        return new Resultado(uuid, nombreBle, major, minor, tipo,
                contador, potenciaReferencia, rssi);
    }



    /** --------------------
     * Diseño lógico: datos: [Dato], posicion: Z --> leerUint16() --x
     * Z <--
     * Descripción: Lee un entero de 16 bits sin signo en orden big-endian
     * -------------------- */
    private static int leerUint16(byte[] datos, int posicion) {
        return ((datos[posicion] & 0xFF) << 8) | (datos[posicion + 1] & 0xFF);
    }



    /** --------------------
     * Diseño lógico: datos: [Dato], inicio: Z --> construirUuid() --x
     * Text <--
     * Descripción: Convierte los 16 bytes del UUID al formato estándar 8-4-4-4-12
     * -------------------- */
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

        /** --------------------
         * Diseño lógico: uuid: Text, nombreBle: Text, major: Z, minor: Z, tipo: Z, contador: Z, txPower: Z, rssi: Z --> Resultado() -->
         * Resultado <--
         * Descripción: Construye Resultado. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
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



        /** --------------------
         * Diseño lógico: esTemperatura() -->
         * B <--
         * Descripción: Indica si la medición codificada corresponde al tipo O3
         * -------------------- */
        public boolean esTemperatura() { return tipo == TIPO_TEMPERATURA; }

        /** --------------------
         * Diseño lógico: esBateria() -->
         * B <--
         * Descripción: Es Bateria. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        public boolean esBateria() { return tipo == TIPO_BATERIA; }

        /** --------------------
         * Diseño lógico: obtenerTemperaturaC() -->
         * R <--
         * Descripción: Obtener Temperatura C. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        public Double obtenerTemperaturaC() {
            if (!esTemperatura() || minor == 0x8000) return null;
            double grados = ((short) minor) / 100.0;
            return grados >= -40 && grados <= 50 ? grados : null;
        }

        /** --------------------
         * Diseño lógico: obtenerBateriaPorcentaje() -->
         * Z <--
         * Descripción: Obtener Bateria Porcentaje. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        public Integer obtenerBateriaPorcentaje() {
            return esBateria() && minor <= 100 ? minor : null;
        }

        /** --------------------
         * Diseño lógico: esOzono() -->
         * B <--
         * Descripción: Es Ozono. Aplicación de campo: acceso, captura, recorridos y presentación Android
         * -------------------- */
        public boolean esOzono() {
            return tipo == TIPO_O3;
        }



        /** --------------------
         * Diseño lógico: obtenerOzonoPpb() -->
         * Z <--
         * Descripción: Devuelve Minor como ppb solo si la trama es de ozono
         * -------------------- */
        public Integer obtenerOzonoPpb() {
            return esOzono() && minor != NO_DISPONIBLE ? minor : null;
        }



        /** --------------------
         * Diseño lógico: obtenerOzonoPpm() -->
         * R <--
         * Descripción: Convierte el valor de ozono de ppb a ppm
         * -------------------- */
        public Double obtenerOzonoPpm() {
            Integer ppb = obtenerOzonoPpb();
            return ppb == null ? null : ppb / 1000.0;
        }
    }
}
