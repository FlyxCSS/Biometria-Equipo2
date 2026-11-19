# Diseño de microprocesador

## Diseño del Componente

Programa para SparkFun Pro nRF52840 Mini. Separa adquisición, codificación, publicidad BLE, batería y diagnóstico. El bucle principal publica O3, temperatura y batería sin contactar al servidor.

### Contrato de medidas y modo de prueba

- Solo microprocesador.ino contiene setup y loop. No necesita conexión USB serie para funcionar.
- USAR_MEDIDA_FAKE=false conserva la adquisición real y calibración existentes. true omite la calibración y usa Medidor.medidaFakeO3()=0.033 ppm y medidaFakeTemperatura()=23.45 C. Cambiar el booleano requiere recompilar y cargar firmware.
- Publicador conserva el espacio ProtocoloBLE en su propio archivo: O3=11, TEMPERATURA=12, BATERIA=14; Major=(tipo << 8)|contador. O3 en Minor es ppm × 1000; temperatura es entero con signo × 100, codificado en 16 bits. No disponibles: 0xFFFF para O3/batería y 0x8000 para temperatura.
- Nombre S-001 y UUID ASCII OZORA-GTI-3A-BIO. Cada tipo se anuncia 3000 ms; el ciclo completo dura unos 9 s más adquisición. La duración es el último argumento de las tres llamadas a publicarMedida en loop. Para aproximar una secuencia a 4 s, usar 1250 ms en cada llamada; para unos 30 s, usar 10000 ms en cada una. El tiempo de adquisición se suma y la recepción en Android no está garantizada para cada anuncio.
- Se mantiene Bateria.h: la app interpreta este tipo. Sin divisor externo configurado devuelve no disponible, nunca un porcentaje inventado.
- Base original: Jordi Bataller i Mascarell. Se retiran servicios GATT no utilizados; se conserva la publicidad iBeacon.
- Medidas ficticias son una opción de firmware; la trama actual no transmite una marca de simulación. No usar este modo para introducir datos como observaciones reales de campo.

### Bateria

Implementación: [src/microprocesador/Bateria.h](../src/microprocesador/Bateria.h).

```text
                 -------- Bateria --------
                 |
                 |
                 |
                 medirPorcentaje() -->
      N <--
                 |
                 --------------------------------------
```

### EmisoraBLE

Implementación: [src/microprocesador/EmisoraBLE.h](../src/microprocesador/EmisoraBLE.h).

```text
                 -------- EmisoraBLE --------
                 |
                 |
                 |
nombreEmisora_: Text, fabricanteID_: N, txPower_: Z --> EmisoraBLE() -->
      Dato <--
                 |
                 |
                 encenderEmisora() -->
                 |
                 |
cbce: CallbackConexionEstablecida, cbct: CallbackConexionTerminada --> encenderEmisora() -->
                 |
                 |
                 detenerAnuncio() -->
                 |
                 |
                 estaAnunciando() -->
      B <--
                 |
                 |
beaconUUID: N, major: N, minor: N, rssi: Z --> emitirAnuncioIBeacon() -->
                 |
                 |
carga: Text, tamanyoCarga: N --> emitirAnuncioIBeaconLibre() -->
                 |
                 |
cb: CallbackConexionEstablecida --> instalarCallbackConexionEstablecida() -->
                 |
                 |
cb: CallbackConexionTerminada --> instalarCallbackConexionTerminada() -->
                 |
                 |
connHandle: N --> getConexion() -->
      BLEConnection <--
                 |
                 --------------------------------------
```

### LED

Implementación: [src/microprocesador/LED.h](../src/microprocesador/LED.h).

```text
                 -------- LED --------
                 |
                 |
                 |
tiempo: Z --> esperar() -->
                 |
                 |
numero: Z --> LED() -->
      Dato <--
                 |
                 |
                 encender() -->
                 |
                 |
                 apagar() -->
                 |
                 |
                 alternar() -->
                 |
                 |
tiempo: Z --> brillar() -->
                 |
                 --------------------------------------
```

### Medidor

Implementación: [src/microprocesador/Medidor.h](../src/microprocesador/Medidor.h).

```text
                 -------- Medidor --------
                 |
                 |
                 |
raw: R --> rawAVoltios() -->
      R <--
                 |
                 |
                 medidaFakeO3() -->
      R <--
                 |
                 |
                 medidaFakeTemperatura() -->
      R <--
                 |
                 |
                 Medidor() -->
      Dato <--
                 |
                 |
                 iniciarMedidor() -->
                 |
                 |
                 medirO3() -->
      R <--
                 |
                 |
                 medirTemperatura() -->
      R <--
                 |
                 --------------------------------------
```

### microprocesador

Implementación: [src/microprocesador/microprocesador.ino](../src/microprocesador/microprocesador.ino).

```text
                 -------- microprocesador --------
                 |
                 |
                 |
                 inicializarPlaquita() -->
                 |
                 |
                 setup() -->
                 |
                 |
                 lucecitas() -->
                 |
                 |
                 loop() -->
                 |
                 --------------------------------------
```

### Publicador

Implementación: [src/microprocesador/Publicador.h](../src/microprocesador/Publicador.h).

```text
                 -------- Publicador --------
                 |
                 |
                 |
ppm: R --> ozono() -->
      N <--
                 |
                 |
grados: R --> temperatura() -->
      N <--
                 |
                 |
                 encenderEmisora() -->
                 |
                 |
tipo: TipoMedicion, valor: N, contador: N, tiempo: Z --> publicarMedida() -->
                 |
                 --------------------------------------
```

### PuertoSerie

Implementación: [src/microprocesador/PuertoSerie.h](../src/microprocesador/PuertoSerie.h).

```text
                 -------- PuertoSerie --------
                 |
                 |
                 |
baudios: Z --> PuertoSerie() -->
      Dato <--
                 |
                 |
                 esperarDisponible() -->
                 |
                 |
mensaje: T --> escribir() -->
                 |
                 --------------------------------------
```

### Diccionario de tipos auxiliares

Dato = Text | Z | R | B | Nulo | [Dato]. Datos = [(clave:Text, valor:Dato)]. Los tipos de dominio detallados en el contrato conservan esa estructura.

- BLEConnection: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- C: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- CallbackConexionEstablecida: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- CallbackConexionTerminada: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Jordi: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- O3: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- T: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- TipoMedicion: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.

### Construcción de Major

```text
tipo: TipoMedicion, contador: N --> construirMajor() --x
N <--
```

Operación auxiliar privada del módulo Publicador; comprobación estática O3/5 = 2821.

## Aclaraciones del Diseño

- Se describe el código actual del Sprint 2; el ejemplo del Sprint 0 se usa para notación, no para copiar comportamientos obsoletos.
- N, Z, R, B y Text son naturales, enteros, reales, booleanos y texto. [T] es una colección; Nulo indica ausencia.
- Los tipos de plataforma citados en las fichas se consideran adaptadores opacos; no autorizan dependencias entre capas.
- Las operaciones auxiliares y callbacks completos se trazan en [el inventario](materiales/informes/revision-funciones.md).

## Reglas Generales

- Lenguaje objetivo: C++ para Arduino y Bluefruit nRF52.
- Cada función o método lleva diseño lógico y descripción en un bloque delimitado por líneas discontinuas.
- + representa interfaz pública y - implementación privada; --> muta o produce efectos, <-- consulta, --x operación estática.
- Código legible, responsabilidad única y auxiliares privados mínimos. No inventar getters ni nuevas operaciones públicas.
- Proteger el estado mutable por confinamiento, transacciones o sincronización explícita según el componente.
- Generar y ejecutar pruebas automatizadas de operaciones críticas, errores, límites, aislamiento y concurrencia cuando el entorno lo permita. No acceder a campos privados para afirmar resultados.
- No introducir secretos, autenticación maestra, respuestas ficticias de éxito, commits ni despliegues automáticos.
