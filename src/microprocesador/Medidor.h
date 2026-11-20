/*
 * Nombre: Medidor.h
 * Descripción: Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos.
 * Base original: Jordi Bataller i Mascarell
 */
// -*- mode: c++ -*-

#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO
#include <math.h>
 #include <cstdlib>
// ------------------------------------------------------
// Medidor para el sensor analogico SPEC ULPSM-O3 968-046
// Vgas -> A3, Vref -> A4, Vtemp -> A5
// ------------------------------------------------------
class Medidor {

private:

  const int PIN_VGAS  = A3;
  const int PIN_VREF  = A4;
  const int PIN_VTEMP = A5;

  // ADC de 12 bits del nRF52840: 0..4095.
  // Con la configuracion habitual de esta placa, el rango es aprox. 0..3.6 V.
  const float ADC_MAX = 4095.0f;
  const float ADC_V   = 3.6f; // AR_INTERNAL: referencia 0.6 V con ganancia 1/6.

  // Sensibilidad de ESTE sensor.
  // Código de sensibilidad de la etiqueta: -42.54 nA/ppm
  // TIA Gain O3: 499 kV/A
  // M = -42.54 * 499 * 10^-6 = -0.02122746 V/ppm
  const float SENSIBILIDAD_O3 = -0.02122746f;

  // Numero de muestras para reducir ruido en cada medida.
  const int NUM_MUESTRAS = 20;

  // Se obtiene al arrancar, midiendo en aire limpio.
  float offsetCero = 0.0f;
  float ultimaTemperatura = NAN;
  // Alimentacion del ULPSM desde 3V3; no es la referencia interna del ADC.
  const float ALIMENTACION_SENSOR_V = 3.3f;


  /** --------------------
   * Diseño lógico: raw: R --> rawAVoltios() -->
   * R <--
   * Descripción: Raw AVoltios. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos
   * -------------------- */
  float rawAVoltios( float raw ) {
    return ( raw * ADC_V ) / ADC_MAX;
  }

public:


  /** --------------------
   * Diseño lógico: medidaFakeO3() -->
   * R <--
   * Descripción: Medida Fake O3. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos
   * -------------------- */
 
float medidaFakeO3() const {
    float min = 0.000f;
    float max = 0.033f;

    // Genera un número entre 0.0 y 1.0, y lo escala al rango deseado
    float escala = static_cast<float>(rand()) / static_cast<float>(RAND_MAX);
    return min + escala * (max - min);
}// ppm: 33 ppb.

  /** --------------------
   * Diseño lógico: medidaFakeTemperatura() -->
   * R <--
   * Descripción: Medida Fake Temperatura. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos
   * -------------------- */


float medidaFakeTemperatura() const {
    float min = 20.0f;
    float max = 26.0f;

    // Genera un número entre 0.0 y 1.0, y lo escala al rango deseado
    float escala = static_cast<float>(rand()) / static_cast<float>(RAND_MAX);
    return min + escala * (max - min);
}

/** --------------------
 * Diseño lógico: Medidor() -->
 * Dato <--
 * Descripción: Construye Medidor. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos
 * -------------------- */
Medidor() {
  } // ()

  // ----------------------------------------------------
  // Prepara el ADC y calcula el offset de cero.
  // IMPORTANTE: al ejecutar esta funcion, el sensor debe
  // estar estabilizado y en aire limpio, fuera de la bolsa de O3.
  // ----------------------------------------------------

  /** --------------------
   * Diseño lógico: iniciarMedidor() -->
   * Descripción: Iniciar Medidor. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos
   * -------------------- */
  void iniciarMedidor() {

    analogReference( AR_INTERNAL );
    analogReadResolution( 12 );
    analogSampleTime( 40 ); // Mayor tiempo de adquisicion para las entradas analogicas.

    Globales::elPuerto.escribir( "Medidor O3 iniciado\n" );
    Globales::elPuerto.escribir( "Calibrando cero en aire limpio...\n" );

    const int MUESTRAS_CALIBRACION = 50;
    float sumaGas = 0.0f;
    float sumaRef = 0.0f;

    for ( int i = 0; i < MUESTRAS_CALIBRACION; i++ ) {
      sumaGas += rawAVoltios( analogRead( PIN_VGAS ) );
      sumaRef += rawAVoltios( analogRead( PIN_VREF ) );
      esperar( 20 );
    }

    float vGas0 = sumaGas / MUESTRAS_CALIBRACION;
    float vRef0 = sumaRef / MUESTRAS_CALIBRACION;

    // Vgas0 = Vref + Voffset  ->  Voffset = Vgas0 - Vref
    offsetCero = vGas0 - vRef0;

    Globales::elPuerto.escribir( "Vgas0 = " );
    Globales::elPuerto.escribir( vGas0 );
    Globales::elPuerto.escribir( " V\n" );

    Globales::elPuerto.escribir( "Vref0 = " );
    Globales::elPuerto.escribir( vRef0 );
    Globales::elPuerto.escribir( " V\n" );

    Globales::elPuerto.escribir( "Offset cero = " );
    Globales::elPuerto.escribir( offsetCero );
    Globales::elPuerto.escribir( " V\n" );
  } // ()

  // ----------------------------------------------------
  // Lee los tres pines y devuelve la concentracion de O3 en ppm.
  // Formula del datasheet:
  // ppm = (Vgas - Vgas0) / M
  // donde Vgas0 = Vref + offsetCero.
  // ----------------------------------------------------

  /** --------------------
   * Diseño lógico: medirO3() -->
   * R <--
   * Descripción: Medir O3. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos
   * -------------------- */
  float medirO3() {

    float sumaRawGas = 0.0f;
    float sumaRawRef = 0.0f;
    float sumaRawTemp = 0.0f;

    for ( int i = 0; i < NUM_MUESTRAS; i++ ) {
      sumaRawGas  += analogRead( PIN_VGAS );
      sumaRawRef  += analogRead( PIN_VREF );
      sumaRawTemp += analogRead( PIN_VTEMP );
      esperar( 5 );
    }

    float rawGas  = sumaRawGas / NUM_MUESTRAS;
    float rawRef  = sumaRawRef / NUM_MUESTRAS;
    float rawTemp = sumaRawTemp / NUM_MUESTRAS;

    float vGas  = rawAVoltios( rawGas );
    float vRef  = rawAVoltios( rawRef );
    float vTemp = rawAVoltios( rawTemp );
    // Coeficientes nominales de SPEC-Sensors/ULPSM, ULP::getTemp().
    // Vtemp y Vref requieren la salida acondicionada/buffer del modulo.
    ultimaTemperatura = 87.0f * vTemp / ALIMENTACION_SENSOR_V - 18.0f;
    if (rawTemp <= 0 || rawTemp >= ADC_MAX) ultimaTemperatura = NAN;
    if (rawGas <= 0 || rawGas >= ADC_MAX || rawRef <= 0 || rawRef >= ADC_MAX)
      return NAN;

    float vGas0 = vRef + offsetCero;
    float ppmO3 = ( vGas - vGas0 ) / SENSIBILIDAD_O3;

    // El ruido alrededor de cero puede producir valores negativos pequenos.
    if ( ppmO3 < 0.0f ) {
      ppmO3 = 0.0f;
    }

    // Mostramos tambien RAW y voltajes para poder comprobar el sensor manana.
    Globales::elPuerto.escribir( "Vgas RAW=" );
    Globales::elPuerto.escribir( rawGas );
    Globales::elPuerto.escribir( "  Vref RAW=" );
    Globales::elPuerto.escribir( rawRef );
    Globales::elPuerto.escribir( "  Vtemp RAW=" );
    Globales::elPuerto.escribir( rawTemp );
    Globales::elPuerto.escribir( "\n" );

    Globales::elPuerto.escribir( "Vgas=" );
    Globales::elPuerto.escribir( vGas );
    Globales::elPuerto.escribir( " V  Vref=" );
    Globales::elPuerto.escribir( vRef );
    Globales::elPuerto.escribir( " V  Vtemp=" );
    Globales::elPuerto.escribir( vTemp );
    Globales::elPuerto.escribir( " V\n" );

    // Vtemp se lee y muestra, pero en esta prueba no aplicamos aun
    // compensacion por temperatura.

    return ppmO3;
  } // ()

  // Devuelve la temperatura de la misma tanda de muestras que medirO3().

  /** --------------------
   * Diseño lógico: medirTemperatura() -->
   * R <--
   * Descripción: Medir Temperatura. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos
   * -------------------- */
  float medirTemperatura() const { return ultimaTemperatura; }

}; // class

#endif
