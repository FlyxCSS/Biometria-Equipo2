// -*- mode: c++ -*-

#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO
#include <math.h>

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

  float rawAVoltios( float raw ) {
    return ( raw * ADC_V ) / ADC_MAX;
  }

public:

  Medidor() {
  } // ()

  // ----------------------------------------------------
  // Prepara el ADC y calcula el offset de cero.
  // IMPORTANTE: al ejecutar esta funcion, el sensor debe
  // estar estabilizado y en aire limpio, fuera de la bolsa de O3.
  // ----------------------------------------------------
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
  float medirTemperatura() const { return ultimaTemperatura; }

}; // class

#endif
