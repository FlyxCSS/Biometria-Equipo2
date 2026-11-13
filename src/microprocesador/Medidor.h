// -*- mode: c++ -*-

// --------------------------------------------------------------
// Fichero: Medidor.h
// Descripción: Obtención de las mediciones ambientales.
// Fecha: 2026-11-02
// Autor: ---
// Base del código: Jordi Bataller i Mascarell
// Aportación: adaptación del medidor para trabajar con O3.
// Copyright: material académico y modificaciones del autor.
// --------------------------------------------------------------

#ifndef MEDIDOR_H_INCLUIDO
#define MEDIDOR_H_INCLUIDO


class Medidor {

private:

  // Valor ficticio utilizado durante el Sprint 0.
  // Se expresa directamente en ppb.
  const uint16_t VALOR_O3_PRUEBA_PPB = 44;


public:

  // ------------------------------------------------------------
  // iniciarMedidor()
  //
  // Inicializa el sistema de medición.
  // En Sprint 0 no requiere configurar el sensor real.
  // ------------------------------------------------------------
  void iniciarMedidor() {

    // Reservado para la inicialización del sensor real de O3.

  } // iniciarMedidor()


  // ------------------------------------------------------------
  // medirO3() --> N
  //
  // Obtiene la concentración de O3 expresada en ppb.
  // Durante el Sprint 0 devuelve una medición ficticia.
  // ------------------------------------------------------------
  uint16_t medirO3() {

    return VALOR_O3_PRUEBA_PPB;

  } // medirO3()

}; // class Medidor


#endif