// -*-c++-*-

// --------------------------------------------------------------
// Autor: FabiÃ¡n AndrÃ©s Useche Rivera
// Base original: Jordi Bataller i Mascarell
// Adaptacion para medir O3 con el sensor SPEC ULPSM-O3 968-046
// --------------------------------------------------------------

#include <bluefruit.h>

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
void inicializarPlaquita () {
  // de momento nada
} // ()

// --------------------------------------------------------------
// setup()
// --------------------------------------------------------------
void setup() {

  //Globales::elPuerto.esperarDisponible();

  inicializarPlaquita();

  Globales::elPublicador.encenderEmisora();
  Globales::elMedidor.iniciarMedidor();

  esperar( 1000 );

  Globales::elPuerto.escribir( "---- setup(): fin ---- \n " );

} // setup ()

// --------------------------------------------------------------
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
// Cada tipo se anuncia durante 3 segundos. Ciclo completo: aproximadamente 9 s.
void loop () {
  using namespace Globales;
  static uint8_t contador = 0;
  elLED.brillar(100);
  float ppmO3 = elMedidor.medirO3();
  float temperatura = elMedidor.medirTemperatura();
  uint16_t bateria = laBateria.medirPorcentaje();
  elPuerto.escribir("O3 (ppm): "); elPuerto.escribir(ppmO3);
  elPuerto.escribir(" | Temperatura (C): "); elPuerto.escribir(temperatura);
  elPuerto.escribir(" | Bateria (%; 65535=no disponible): "); elPuerto.escribir(bateria);
  elPuerto.escribir("\n");
  elPublicador.publicarMedida(ProtocoloBLE::O3, ProtocoloBLE::ozono(ppmO3), contador, 3000);
  elPublicador.publicarMedida(ProtocoloBLE::TEMPERATURA, ProtocoloBLE::temperatura(temperatura), contador, 3000);
  elPublicador.publicarMedida(ProtocoloBLE::BATERIA, bateria, contador, 3000);
  ++contador; // uint8_t: despues de 255 vuelve a 0.
}
