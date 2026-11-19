// Base: Jordi Bataller i Mascarell. Adaptacion SENDA: Major = (tipo << 8) | contador.
#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO
#include "ProtocoloBLE.h"

namespace ConfiguracionPublicador {
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
  void encenderEmisora() { laEmisora.encenderEmisora(); }
  void publicarMedida(ProtocoloBLE::Tipo tipo, uint16_t valor, uint8_t contador, unsigned long tiempo) {
    const uint16_t major = (static_cast<uint16_t>(tipo) << 8) | contador;
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
