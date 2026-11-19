// Formato compartido con IBeaconParser.java: Major = tipo, Minor = valor.
#ifndef PROTOCOLO_BLE_H
#define PROTOCOLO_BLE_H
#include <stdint.h>
#include <math.h>
namespace ProtocoloBLE {
  enum Tipo : uint16_t { O3 = 11, TEMPERATURA = 12, BATERIA = 14 };
  constexpr uint16_t NO_DISPONIBLE = 0xFFFF;
  constexpr uint16_t TEMPERATURA_NO_DISPONIBLE = 0x8000;
  inline uint16_t ozono(float ppm) {
    // Rango especificado del ULPSM-O3: 0..20 ppm. No presentar saturacion como una medida.
    if (!isfinite(ppm) || ppm > 20.0f) return NO_DISPONIBLE;
    if (ppm < 0) ppm = 0;
    return static_cast<uint16_t>(lroundf(ppm * 1000.0f));
  }
  inline uint16_t temperatura(float grados) {
    if (!isfinite(grados) || grados < -40.0f || grados > 50.0f)
      return TEMPERATURA_NO_DISPONIBLE;
    return static_cast<uint16_t>(static_cast<int16_t>(lroundf(grados * 100.0f)));
  }
}
#endif
