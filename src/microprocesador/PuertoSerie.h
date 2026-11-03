// -*- mode: c++ -*-

// --------------------------------------------------------------
// Fichero: PuertoSerie.h
// Descripción: Salida de información de depuración por Serial.
// Fecha: 2026-11-02
// Autor: ---
// Base del código: Jordi Bataller i Mascarell
// Aportación: simplificación para la depuración del Sprint 0.
// Copyright: material académico y modificaciones del autor.
// --------------------------------------------------------------

#ifndef PUERTO_SERIE_H_INCLUIDO
#define PUERTO_SERIE_H_INCLUIDO


class PuertoSerie {

public:

  // ------------------------------------------------------------
  // baudios: N --> PuertoSerie()
  //
  // Inicializa el puerto serie con la velocidad indicada.
  // ------------------------------------------------------------
  PuertoSerie(
    unsigned long baudios
  ) {

    Serial.begin(
      baudios
    );

  } // PuertoSerie()


  // ------------------------------------------------------------
  // tiempo_maximo: N --> esperarDisponible()
  //
  // Espera al puerto serie únicamente durante el tiempo indicado.
  // Después continúa aunque no exista un ordenador conectado.
  // ------------------------------------------------------------
  void esperarDisponible(
    unsigned long tiempoMaximo
  ) {

    unsigned long inicio =
      millis();


    while (
      !Serial
      &&
      millis() - inicio < tiempoMaximo
    ) {

      delay(10);
    }

  } // esperarDisponible()


  // ------------------------------------------------------------
  // valor: ValorImprimible --> escribir()
  //
  // Escribe un valor mediante el puerto serie.
  // ------------------------------------------------------------
  template<typename T>
  void escribir(
    T valor
  ) {

    Serial.print(
      valor
    );

  } // escribir()


  // ------------------------------------------------------------
  // valor: R, decimales: N --> escribirDecimal()
  //
  // Escribe un número real con la precisión indicada.
  // ------------------------------------------------------------
  void escribirDecimal(
    float valor,
    unsigned int decimales
  ) {

    Serial.print(
      valor,
      decimales
    );

  } // escribirDecimal()

}; // class PuertoSerie


#endif