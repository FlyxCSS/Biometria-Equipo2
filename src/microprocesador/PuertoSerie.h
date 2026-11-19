/*
 * Nombre: PuertoSerie.h
 * Descripción: Encapsula el puerto de diagnóstico de la placa.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Encapsula el puerto de diagnóstico de la placa.
 * Base original: Jordi Bataller i Mascarell
 */
// -*- mode: c++ -*-

// ----------------------------------------------------------
// Jordi Bataller i Mascarell
// 2019-07-07
// ----------------------------------------------------------

#ifndef PUERTO_SERIE_H_INCLUIDO
#define PUERTO_SERIE_H_INCLUIDO

// ----------------------------------------------------------
// ----------------------------------------------------------
class PuertoSerie  {

public:
  // .........................................................
  // .........................................................

  /** --------------------
   * Diseño lógico: baudios: Z --> PuertoSerie() -->
   * Dato <--
   * Descripción: Construye PuertoSerie. Encapsula el puerto de diagnóstico de la placa
   * -------------------- */
  PuertoSerie (long baudios) {
	Serial.begin( baudios );
	// mejor no poner esto aquí: while ( !Serial ) delay(10);
  } // ()

  // .........................................................
  // .........................................................

  /** --------------------
   * Diseño lógico: esperarDisponible() -->
   * Descripción: Esperar Disponible. Encapsula el puerto de diagnóstico de la placa
   * -------------------- */
  void esperarDisponible() {

	while ( !Serial ) {
	  delay(10);
	}

  } // ()

  // .........................................................
  // .........................................................
  template<typename T>

  /** --------------------
   * Diseño lógico: mensaje: T --> escribir() -->
   * Descripción: Escribir. Encapsula el puerto de diagnóstico de la placa
   * -------------------- */
  void escribir (T mensaje) {
	Serial.print( mensaje );
  } // ()

}; // class PuertoSerie

// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
#endif
