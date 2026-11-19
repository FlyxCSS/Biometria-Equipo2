/*
 * Nombre: LED.h
 * Descripción: Controla la indicación luminosa sin lógica de negocio.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Controla la indicación luminosa sin lógica de negocio.
 * Base original: Jordi Bataller i Mascarell
 */
// -*- mode: c++ -*-

#ifndef LED_H_INCLUIDO
#define LED_H_INCLUIDO

// ----------------------------------------------------------
// ----------------------------------------------------------

// ----------------------------------------------------------
// ----------------------------------------------------------

/** --------------------
 * Diseño lógico: tiempo: Z --> esperar() -->
 * Descripción: Esperar. Controla la indicación luminosa sin lógica de negocio
 * -------------------- */
void esperar (long tiempo) {
  delay (tiempo);
}

// ----------------------------------------------------------
// ----------------------------------------------------------
class LED {
private:
  int numeroLED;
  bool encendido;
public:

  // .........................................................
  // .........................................................

  /** --------------------
   * Diseño lógico: numero: Z --> LED() -->
   * Dato <--
   * Descripción: Construye LED. Controla la indicación luminosa sin lógica de negocio
   * -------------------- */
  LED (int numero)
	: numeroLED (numero), encendido(false)
  {
	pinMode(numeroLED, OUTPUT);
	apagar ();
  }

  // .........................................................
  // .........................................................

  /** --------------------
   * Diseño lógico: encender() -->
   * Descripción: Encender. Controla la indicación luminosa sin lógica de negocio
   * -------------------- */
  void encender () {
	digitalWrite(numeroLED, HIGH);
	encendido = true;
  }

  // .........................................................
  // .........................................................

  /** --------------------
   * Diseño lógico: apagar() -->
   * Descripción: Apagar. Controla la indicación luminosa sin lógica de negocio
   * -------------------- */
  void apagar () {
	  digitalWrite(numeroLED, LOW);
	  encendido = false;
  }

  // .........................................................
  // .........................................................

  /** --------------------
   * Diseño lógico: alternar() -->
   * Descripción: Alternar. Controla la indicación luminosa sin lógica de negocio
   * -------------------- */
  void alternar () {
	if (encendido) {
	  apagar();
	} else {
	  encender ();
	}
  } // ()

  // .........................................................
  // .........................................................

  /** --------------------
   * Diseño lógico: tiempo: Z --> brillar() -->
   * Descripción: Brillar. Controla la indicación luminosa sin lógica de negocio
   * -------------------- */
  void brillar (long tiempo) {
	encender ();
	esperar(tiempo);
	apagar ();
  }
}; // class

// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
#endif
