// -*- mode: c++ -*-

// --------------------------------------------------------------
// Fichero: EmisoraBLE.h
// Descripción: Emisión de anuncios Bluetooth Low Energy.
// Fecha: 2026-11-02
// Autor: ---
// Base del código: Jordi Bataller i Mascarell
// Aportación: simplificación de la emisora para publicar iBeacon.
// Copyright: material académico y modificaciones del autor.
// --------------------------------------------------------------

#ifndef EMISORA_BLE_H_INCLUIDO
#define EMISORA_BLE_H_INCLUIDO


class EmisoraBLE {

private:

  const char * nombreEmisora;

  const uint16_t fabricanteID;

  const int8_t txPower;


public:

  // ------------------------------------------------------------
  // nombre: Text, fabricante: N, potencia: Z --> EmisoraBLE()
  //
  // Configura los parámetros básicos de la emisora.
  // ------------------------------------------------------------
  EmisoraBLE(
    const char * nombre,
    uint16_t fabricante,
    int8_t potencia
  )
    :
    nombreEmisora(nombre),
    fabricanteID(fabricante),
    txPower(potencia)
  {
  }


  // ------------------------------------------------------------
  // encenderEmisora()
  //
  // Inicializa Bluefruit y configura la emisora BLE.
  // ------------------------------------------------------------
  void encenderEmisora() {

    Bluefruit.begin();

    Bluefruit.setTxPower(
      txPower
    );

    Bluefruit.setName(
      nombreEmisora
    );

    detenerAnuncio();

  } // encenderEmisora()


  // ------------------------------------------------------------
  // estaAnunciando() --> B
  //
  // Indica si existe actualmente un anuncio BLE activo.
  // ------------------------------------------------------------
  bool estaAnunciando() {

    return
      Bluefruit.Advertising.isRunning();

  } // estaAnunciando()


  // ------------------------------------------------------------
  // detenerAnuncio()
  //
  // Detiene el anuncio BLE activo.
  // ------------------------------------------------------------
  void detenerAnuncio() {

    if (estaAnunciando()) {

      Bluefruit.Advertising.stop();
    }

  } // detenerAnuncio()


  // ------------------------------------------------------------
  // uuid: [N]_16, major: N, minor: N, tx_power: Z
  //        --> emitirAnuncioIBeacon()
  //
  // Construye y publica una trama compatible con iBeacon.
  // ------------------------------------------------------------
  void emitirAnuncioIBeacon(
    uint8_t * beaconUUID,
    uint16_t major,
    uint16_t minor,
    int8_t potenciaReferencia
  ) {

    detenerAnuncio();


    // Elimina los datos correspondientes al anuncio anterior.
    Bluefruit.Advertising.clearData();

    Bluefruit.ScanResponse.clearData();


    BLEBeacon beacon(
      beaconUUID,
      major,
      minor,
      potenciaReferencia
    );


    beacon.setManufacturer(
      fabricanteID
    );


    // Permite que Android localice el dispositivo por su nombre.
    Bluefruit.ScanResponse.addName();


    Bluefruit.Advertising.setBeacon(
      beacon
    );


    /*
     * Intervalo entre anuncios de una misma medición.
     * 100 unidades BLE equivalen aproximadamente a 62,5 ms.
     */
    Bluefruit.Advertising.setInterval(
      100,
      100
    );


    // 0 mantiene el anuncio activo hasta detenerAnuncio().
    Bluefruit.Advertising.start(
      0
    );

  } // emitirAnuncioIBeacon()

}; // class EmisoraBLE


#endif