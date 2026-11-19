/*
 * Nombre: Publicador.h
 * Descripción: Codifica los tipos y unidades y publica cada ciclo BLE.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Codifica los tipos y unidades y publica cada ciclo BLE.
 * Base original: Jordi Bataller i Mascarell
 */
// Adaptacion SENDA: Major = (tipo << 8) | contador.
#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO
#include <stdint.h>
#include <math.h>
namespace ProtocoloBLE {
  enum Tipo : uint16_t { O3 = 11, TEMPERATURA = 12, BATERIA = 14 };
  constexpr uint16_t NO_DISPONIBLE = 0xFFFF;
  constexpr uint16_t TEMPERATURA_NO_DISPONIBLE = 0x8000;

  /** --------------------
   * Diseño lógico: ppm: R --> ozono() -->
   * N <--
   * Descripción: Ozono. Codifica los tipos y unidades y publica cada ciclo BLE
   * -------------------- */
  inline uint16_t ozono(float ppm) {
    // Rango especificado del ULPSM-O3: 0..20 ppm. No presentar saturacion como una medida.
    if (!isfinite(ppm) || ppm > 20.0f) return NO_DISPONIBLE;
    if (ppm < 0) ppm = 0;
    return static_cast<uint16_t>(lroundf(ppm * 1000.0f));
  }

  /** --------------------
   * Diseño lógico: grados: R --> temperatura() -->
   * N <--
   * Descripción: Temperatura. Codifica los tipos y unidades y publica cada ciclo BLE
   * -------------------- */
  inline uint16_t temperatura(float grados) {
    if (!isfinite(grados) || grados < -40.0f || grados > 50.0f)
      return TEMPERATURA_NO_DISPONIBLE;
    return static_cast<uint16_t>(static_cast<int16_t>(lroundf(grados * 100.0f)));
  }
}


namespace ConfiguracionPublicador {
  /** --------------------
   * Diseño lógico: tipo: TipoMedicion, contador: N --> construirMajor() --x
   * N <--
   * Descripción: Codifica tipo y contador en los dos bytes del Major.
   * -------------------- */
  constexpr uint16_t construirMajor(ProtocoloBLE::Tipo tipo, uint8_t contador) {
    return (static_cast<uint16_t>(tipo) << 8) | contador;
  }
  static_assert(construirMajor(ProtocoloBLE::O3, 5) == 2821, "Major O3 incompatible");
  // Debe coincidir con el llavero seleccionado mediante QR en SENDA.
  constexpr char NOMBRE_MICRO[] = "S-001";
}
class Publicador {
private:
  uint8_t beaconUUID[16] = {
    'O', 'Z', 'O', 'R', 'A', '-', 'G', 'T', 'I', '-', '3', 'A', '-', 'B', 'I', 'O'
  };
public:
  EmisoraBLE laEmisora { ConfiguracionPublicador::NOMBRE_MICRO, 0x004c, 4 };

  /** --------------------
   * Diseño lógico: encenderEmisora() -->
   * Descripción: Encender Emisora. Codifica los tipos y unidades y publica cada ciclo BLE
   * -------------------- */
  void encenderEmisora() { laEmisora.encenderEmisora(); }

  /** --------------------
   * Diseño lógico: tipo: ProtocoloBLETipo, valor: N, contador: N, tiempo: Z --> publicarMedida() -->
   * Descripción: Publicar Medida. Codifica los tipos y unidades y publica cada ciclo BLE
   * -------------------- */
  void publicarMedida(ProtocoloBLE::Tipo tipo, uint16_t valor, uint8_t contador, unsigned long tiempo) {
    const uint16_t major = ConfiguracionPublicador::construirMajor(tipo, contador);
    laEmisora.emitirAnuncioIBeacon(beaconUUID, major, valor, -53);
    Globales::elPuerto.escribir("Major=");
    Globales::elPuerto.escribir(major);
    Globales::elPuerto.escribir(" Tipo=");
    Globales::elPuerto.escribir(static_cast<uint16_t>(tipo));
    Globales::elPuerto.escribir(" Contador=");
    Globales::elPuerto.escribir(contador);
    Globales::elPuerto.escribir(" Minor=");
    Globales::elPuerto.escribir(valor);
    Globales::elPuerto.escribir("\n");
    esperar(tiempo);
    laEmisora.detenerAnuncio();
  }
};
#endif
