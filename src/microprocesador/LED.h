// -*- mode: c++ -*-

// --------------------------------------------------------------
// Fichero: LED.h
// Descripción: Control del LED utilizado como indicador visual.
// Fecha: 2026-11-02
// Autor: ---
// Base del código: Jordi Bataller i Mascarell
// Aportación: uso del LED para indicar cada nueva medición.
// Copyright: material académico y modificaciones del autor.
// --------------------------------------------------------------

#ifndef LED_H_INCLUIDO
#define LED_H_INCLUIDO


class LED {

private:

  int numeroLED;

  bool encendido;


public:

  // ------------------------------------------------------------
  // numero: Z --> LED()
  //
  // Configura el pin indicado y deja el LED apagado.
  // ------------------------------------------------------------
  LED(
    int numero
  )
    :
    numeroLED(numero),
    encendido(false)
  {

    pinMode(
      numeroLED,
      OUTPUT
    );

    apagar();

  } // LED()


  // ------------------------------------------------------------
  // encender()
  //
  // Enciende el LED.
  // ------------------------------------------------------------
  void encender() {

    digitalWrite(
      numeroLED,
      HIGH
    );

    encendido = true;

  } // encender()


  // ------------------------------------------------------------
  // apagar()
  //
  // Apaga el LED.
  // ------------------------------------------------------------
  void apagar() {

    digitalWrite(
      numeroLED,
      LOW
    );

    encendido = false;

  } // apagar()


  // ------------------------------------------------------------
  // alternar()
  //
  // Cambia el estado actual del LED.
  // ------------------------------------------------------------
  void alternar() {

    if (encendido) {

      apagar();

    } else {

      encender();
    }

  } // alternar()


  // ------------------------------------------------------------
  // tiempo: N --> brillar()
  //
  // Enciende el LED durante el tiempo indicado y después lo apaga.
  // ------------------------------------------------------------
  void brillar(
    unsigned long tiempo
  ) {

    encender();

    delay(
      tiempo
    );

    apagar();

  } // brillar()

}; // class LED


#endif