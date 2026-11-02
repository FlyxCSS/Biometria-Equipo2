// -*- mode: c++ -*-

// --------------------------------------------------------------
// Fichero: Publicador.h
// Descripción: Codificación y publicación de mediciones BLE.
// Fecha: 2026-11-02
// Autor: ...
// Base del código: Jordi Bataller i Mascarell
// Aportación: publicación genérica de mediciones mediante iBeacon
//             y centralización del nombre del microprocesador.
// Copyright: material académico y modificaciones del autor.
// --------------------------------------------------------------

#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO


// --------------------------------------------------------------
// CONFIGURACIÓN DEL PUBLICADOR
//
// El nombre BLE identifica al microprocesador.
// Android obtiene este nombre del anuncio BLE y lo enviará al
// backend como el campo nombre_micro.
// --------------------------------------------------------------
namespace ConfiguracionPublicador {

  constexpr char NOMBRE_MICRO[] =
    "S-001";

} // namespace ConfiguracionPublicador


class Publicador {

public:

  // Identificadores utilizados en el byte alto de Major.
  enum MedicionesID {
    O3 = 11,
    TEMPERATURA = 12
  };


private:

  // UUID que identifica el proyecto.
  uint8_t beaconUUID[16] = {
    'O', 'Z', 'O', 'R',
    'A', '-', 'G', 'T',
    'I', '-', '3', 'A',
    '-', 'B', 'I', 'O'
  };


  EmisoraBLE laEmisora {
    ConfiguracionPublicador::NOMBRE_MICRO,
    0x004C,
    4
  };


  const int8_t RSSI = -53;


public:

  // ------------------------------------------------------------
  // obtenerNombreMicro() --> Text
  //
  // Devuelve el nombre BLE que identifica al microprocesador.
  // Android utilizará este nombre como nombre_micro.
  // ------------------------------------------------------------
  const char * obtenerNombreMicro() const {

    return
      ConfiguracionPublicador::NOMBRE_MICRO;

  } // obtenerNombreMicro()


  // ------------------------------------------------------------
  // tipo: MedicionID, contador: N --> construirMajor() --x
  // N <--
  //
  // Codifica el tipo en el byte alto y el contador en el bajo.
  // No modifica el estado del objeto.
  // ------------------------------------------------------------
  static constexpr uint16_t construirMajor(
    MedicionesID tipo,
    uint8_t contador
  ) {

    return
      (static_cast<uint16_t>(tipo) << 8)
      |
      contador;

  } // construirMajor()


  // ------------------------------------------------------------
  // encenderEmisora()
  //
  // Inicializa la emisora Bluetooth utilizada por Publicador.
  // ------------------------------------------------------------
  void encenderEmisora() {

    laEmisora.encenderEmisora();

  } // encenderEmisora()


  // ------------------------------------------------------------
  // tipo: MedicionID, valor: N, contador: N, tiempo: N
  //        --> publicarMedida() --> N
  //
  // Publica una medición mediante iBeacon.
  // Major contiene tipo y contador.
  // Minor contiene la medición codificada en ppb.
  // Devuelve el Major utilizado.
  // ------------------------------------------------------------
  uint16_t publicarMedida(
    MedicionesID tipo,
    uint16_t valor,
    uint8_t contador,
    unsigned long tiempoEspera
  ) {

    uint16_t major =
      construirMajor(
        tipo,
        contador
      );


    laEmisora.emitirAnuncioIBeacon(
      beaconUUID,
      major,
      valor,
      RSSI
    );


    // Mantiene activa esta medición durante el tiempo indicado.
    delay(
      tiempoEspera
    );


    laEmisora.detenerAnuncio();


    return major;

  } // publicarMedida()

}; // class Publicador


// --------------------------------------------------------------
// PRUEBA AUTOMÁTICA
//
// Comprueba en cada compilación la codificación de Major.
//
// O3 = 11 y contador = 5
// deben producir Major = 0x0B05 = 2821.
// --------------------------------------------------------------
static_assert(
  Publicador::construirMajor(
    Publicador::O3,
    5
  ) == 2821,
  "Error en la codificacion del campo Major"
);


#endif
