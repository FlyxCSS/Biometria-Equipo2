/*
 * Nombre: Bateria.h
 * Descripción: Estima batería solo si existe el divisor externo configurado.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Estima batería solo si existe el divisor externo configurado.
 * Base original: Jordi Bataller i Mascarell
 */
#ifndef BATERIA_H_INCLUIDO
#define BATERIA_H_INCLUIDO
#include <stdint.h>
#include <math.h>

// La SparkFun Pro nRF52840 Mini NO incorpora lectura del voltaje de bateria.
// Activar SOLO tras montar un divisor externo y confirmar una LiPo 1S (4.2 V max).
namespace ConfiguracionBateria {
  constexpr bool DIVISOR_INSTALADO = false;
  constexpr int PIN = A0;
  // Conexion opcional: BAT+ -- 100k -- A0 -- 100k -- GND; GND comun.
  // No conectar BAT+ directamente a A0. Estos valores describen ese montaje opcional.
  constexpr float FACTOR_DIVISOR = 2.0f;
}
class Bateria {
public:

  /** --------------------
   * Diseño lógico: medirPorcentaje() -->
   * N <--
   * Descripción: Medir Porcentaje. Estima batería solo si existe el divisor externo configurado
   * -------------------- */
  uint16_t medirPorcentaje() {
    if (!ConfiguracionBateria::DIVISOR_INSTALADO) return 0xFFFF;
    float suma = 0;
    analogRead(ConfiguracionBateria::PIN);
    for (int i = 0; i < 20; ++i) {
      suma += analogRead(ConfiguracionBateria::PIN);
      delay(5);
    }
    float voltios = suma / 20.0f * 3.6f / 4095.0f * ConfiguracionBateria::FACTOR_DIVISOR;
    if (!isfinite(voltios) || voltios < 2.5f || voltios > 4.3f)
      return 0xFFFF;
    // Estimacion por tension para LiPo 1S, no un medidor de carga.
    const float tension[] = {3.30f, 3.50f, 3.60f, 3.70f, 3.80f, 3.90f, 4.00f, 4.10f, 4.20f};
    const uint8_t carga[] = {0, 5, 10, 20, 40, 60, 80, 90, 100};
    if (voltios <= tension[0]) return 0;
    for (int i = 1; i < 9; ++i) {
      if (voltios <= tension[i]) return static_cast<uint16_t>(lroundf(carga[i-1] +
        (voltios - tension[i-1]) * (carga[i] - carga[i-1]) / (tension[i] - tension[i-1])));
    }
    return 100;
  }
};
#endif
