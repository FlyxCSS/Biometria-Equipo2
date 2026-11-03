// -*- mode: c++ -*-

// --------------------------------------------------------------
// Fichero: microprocesador.ino
// Descripción: Programa principal del nodo sensor BLE.
// Fecha: 2026-11-02
// Autor: ---
// Base del código: Jordi Bataller i Mascarell
// Aportación: adaptación para publicar mediciones de O3 mediante
//             iBeacon dentro del Sprint 0.
// Copyright: material académico y modificaciones del autor.
// --------------------------------------------------------------

#include <bluefruit.h>

#undef min
#undef max

#include "LED.h"
#include "PuertoSerie.h"
#include "EmisoraBLE.h"
#include "Publicador.h"
#include "Medidor.h"


// --------------------------------------------------------------
// CONFIGURACIÓN DE LA DEMOSTRACIÓN
// --------------------------------------------------------------
namespace Configuracion {

  // Tiempo durante el cual permanece publicada cada medición.
  const unsigned long INTERVALO_MEDICION_MS = 5000;

  // Duración del destello que indica una nueva medición.
  const unsigned long DURACION_LED_MS = 100;

  // Tiempo máximo de espera inicial del Serial Monitor.
  // Después de este tiempo la placa continúa aunque no exista PC.
  const unsigned long ESPERA_SERIAL_MS = 2500;

} // namespace Configuracion


// --------------------------------------------------------------
// COMPONENTES DEL SISTEMA
// --------------------------------------------------------------
namespace Globales {

  LED elLED(7);

  PuertoSerie elPuerto(115200);

  Publicador elPublicador;

  Medidor elMedidor;

} // namespace Globales


// --------------------------------------------------------------
// ESTADO DEL CICLO PRINCIPAL
// --------------------------------------------------------------
namespace Loop {

  uint32_t numeroLoop = 1;

} // namespace Loop


// ------------------------------------------------------------
// ppb: N --> convertirPpbAPpm() --> R
//
// Convierte una concentración expresada en ppb a ppm.
// Esta conversión se utiliza únicamente para mostrar el valor
// de forma más legible en el Serial Monitor.
// ------------------------------------------------------------
float convertirPpbAPpm(
  uint16_t ppb
) {

  return
    ppb / 1000.0f;

} // convertirPpbAPpm()


// ------------------------------------------------------------
// setup()
//
// Inicializa Serial, la emisora BLE y el sistema de medición.
// La placa continúa funcionando aunque el Serial Monitor
// no esté conectado.
// ------------------------------------------------------------
void setup() {

  using namespace Globales;
  using namespace Configuracion;


  elPuerto.esperarDisponible(
    ESPERA_SERIAL_MS
  );


  elPuerto.escribir(
    "\n====================================\n"
  );

  elPuerto.escribir(
    "      INICIANDO MICROPROCESADOR\n"
  );

  elPuerto.escribir(
    "====================================\n"
  );


  elPublicador.encenderEmisora();

  elMedidor.iniciarMedidor();


  elPuerto.escribir(
    "Sensor: O3\n"
  );

  elPuerto.escribir(
    "Nombre micro: "
  );

  elPuerto.escribir(
    elPublicador.obtenerNombreMicro()
  );

  elPuerto.escribir(
    "\nIntervalo: "
  );

  elPuerto.escribir(
    INTERVALO_MEDICION_MS
  );

  elPuerto.escribir(
    " ms\n"
  );

  elPuerto.escribir(
    "====================================\n"
  );

} // setup()


// ------------------------------------------------------------
// loop()
//
// Obtiene una medición de O3 en ppb y la publica mediante
// una trama iBeacon.
//
// El nombre del microprocesador se anuncia mediante BLE.
// Android lo obtiene y lo envía al backend como nombre_micro.
//
// Major:
// - byte alto: tipo de medición.
// - byte bajo: contador.
//
// Minor:
// - valor de O3 en ppb.
// ------------------------------------------------------------
void loop() {

  using namespace Globales;
  using namespace Loop;
  using namespace Configuracion;


  // Indicación visual de que comienza una nueva medición.
  elLED.brillar(
    DURACION_LED_MS
  );


  // Valor ficticio de O3 expresado directamente en ppb.
  uint16_t valorO3Ppb =
    elMedidor.medirO3();


  // Conversión solo para mostrar el valor en ppm por Serial.
  float valorO3Ppm =
    convertirPpbAPpm(
      valorO3Ppb
    );


  /*
   * El contador incluido en Major ocupa un byte.
   * Después de 255 vuelve automáticamente a 0.
   */
  uint8_t contador =
    static_cast<uint8_t>(
      numeroLoop
    );


  // Calcula el Major utilizando la misma lógica que Publicador.
  uint16_t major =
    Publicador::construirMajor(
      Publicador::O3,
      contador
    );


  // ----------------------------------------------------------
  // INFORMACIÓN DE DEPURACIÓN
  // ----------------------------------------------------------

  elPuerto.escribir(
    "\n----- Nueva medicion - Loop "
  );

  elPuerto.escribir(
    numeroLoop
  );

  elPuerto.escribir(
    " -----\n"
  );


  elPuerto.escribir(
    "Nombre micro: "
  );

  elPuerto.escribir(
    elPublicador.obtenerNombreMicro()
  );


  elPuerto.escribir(
    "\nTipo: O3\n"
  );


  elPuerto.escribir(
    "Contador: "
  );

  elPuerto.escribir(
    contador
  );


  elPuerto.escribir(
    "\nValor O3: "
  );

  elPuerto.escribirDecimal(
    valorO3Ppm,
    3
  );

  elPuerto.escribir(
    " ppm\n"
  );


  elPuerto.escribir(
    "Major enviado: "
  );

  elPuerto.escribir(
    major
  );


  elPuerto.escribir(
    "\nMinor enviado: "
  );

  elPuerto.escribir(
    valorO3Ppb
  );


  elPuerto.escribir(
    "\nIntervalo: "
  );

  elPuerto.escribir(
    INTERVALO_MEDICION_MS
  );

  elPuerto.escribir(
    " ms\n"
  );


  // Publica la medición mediante BLE.
  elPublicador.publicarMedida(
    Publicador::O3,
    valorO3Ppb,
    contador,
    INTERVALO_MEDICION_MS
  );


  numeroLoop++;

} // loop()
