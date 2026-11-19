/*
 * Nombre: microprocesador.ino
 * Descripción: Captura de sensores y publicación de anuncios BLE.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Captura de sensores y publicación de anuncios BLE.
 * Base original: Jordi Bataller i Mascarell
 */
// -*-c++-*-

#include <bluefruit.h>

// false conserva la captura real actual; true permite pruebas controladas.
constexpr bool USAR_MEDIDA_FAKE = false;

#undef min
#undef max

#include "LED.h"
#include "PuertoSerie.h"

namespace Globales {

  LED elLED ( 7 );
  PuertoSerie elPuerto ( 115200 );

};

#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"
#include "Bateria.h"

namespace Globales {

  Publicador elPublicador;
  Medidor elMedidor;
  Bateria laBateria;

};

// --------------------------------------------------------------

/** --------------------
 * Diseño lógico: inicializarPlaquita() -->
 * Descripción: Inicializar Plaquita. Captura de sensores y publicación de anuncios BLE
 * -------------------- */
void inicializarPlaquita () {
  // de momento nada
} // ()

// --------------------------------------------------------------
// setup()
// --------------------------------------------------------------

/** --------------------
 * Diseño lógico: setup() -->
 * Descripción: Setup. Captura de sensores y publicación de anuncios BLE
 * -------------------- */
void setup() {

  //Globales::elPuerto.esperarDisponible();

  inicializarPlaquita();

  Globales::elPublicador.encenderEmisora();
  if (!USAR_MEDIDA_FAKE) Globales::elMedidor.iniciarMedidor();

  esperar( 1000 );

  Globales::elPuerto.escribir( "---- setup(): fin ---- \n " );

} // setup ()

// --------------------------------------------------------------

/** --------------------
 * Diseño lógico: lucecitas() -->
 * Descripción: Lucecitas. Captura de sensores y publicación de anuncios BLE
 * -------------------- */
inline void lucecitas() {
  using namespace Globales;

  elLED.brillar( 100 );
  esperar ( 400 );
  elLED.brillar( 100 );
  esperar ( 400 );
  elLED.brillar( 100 );
  esperar ( 400 );
  elLED.brillar( 1000 );
  esperar ( 1000 );
} // ()

// --------------------------------------------------------------
// loop()
// --------------------------------------------------------------
// Cada dato se anuncia 3000 ms: secuencia de unos 9 s más adquisición.

/** --------------------
 * Diseño lógico: loop() -->
 * Descripción: Loop. Captura de sensores y publicación de anuncios BLE
 * -------------------- */
void loop () {
  using namespace Globales;
  static uint8_t contador = 0;
  elLED.brillar(100);
  float ppmO3 = USAR_MEDIDA_FAKE ? elMedidor.medidaFakeO3() : elMedidor.medirO3();
  float temperatura = USAR_MEDIDA_FAKE ? elMedidor.medidaFakeTemperatura() : elMedidor.medirTemperatura();
  uint16_t bateria = laBateria.medirPorcentaje();
  elPuerto.escribir("O3 (ppm): "); elPuerto.escribir(ppmO3);
  elPuerto.escribir(" | Temperatura (C): "); elPuerto.escribir(temperatura);
  elPuerto.escribir(" | Bateria (%; 65535=no disponible): "); elPuerto.escribir(bateria);
  elPuerto.escribir("\n");
  elPublicador.publicarMedida(ProtocoloBLE::O3, ProtocoloBLE::ozono(ppmO3), contador, 1000);
  elPublicador.publicarMedida(ProtocoloBLE::TEMPERATURA, ProtocoloBLE::temperatura(temperatura), contador, 1000);
  elPublicador.publicarMedida(ProtocoloBLE::BATERIA, bateria, contador, 2000);
  ++contador; // uint8_t: despues de 255 vuelve a 0.
}
