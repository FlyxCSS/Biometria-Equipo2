# Actualizacion: Major con tipo y contador

El firmware envia Major = (tipo << 8) | contador. El byte alto identifica
11=O3, 12=temperatura, 14=bateria. El byte bajo cuenta ciclos de 0 a 255.
Las tres mediciones de una tanda comparten contador. Reinicia a 0 al arrancar.
Minor conserva las unidades y los valores de no disponible descritos debajo.
Android ya interpreta este formato y utiliza el contador transmitido.
La descripcion de Major directo debajo corresponde a la version anterior.

# O3, temperatura y bateria en SENDA

Abrir `microprocesador.ino` con todos los archivos de esta carpeta. La logica principal
permanece en `TareaSensorMedicionOZONO.ino`. Placa: SparkFun Pro nRF52840 Mini,
core Adafruit nRF52. No mezclar estos archivos con la copia de Documents/GitHub.

## Formato

UUID: `4F5A4F52-412D-4754-492D-33412D42494F` (OZORA-GTI-3A-BIO).
Nombre BLE: `S-001`, configurable en `Publicador.h`; debe coincidir con el QR.
Fabricante 0x004C, formato iBeacon. Major y Minor son uint16 big-endian.
Se alternan tres anuncios, 3 segundos cada uno (~9 segundos por ciclo).

| Major (tipo completo) | Minor | Ejemplo |
| --- | --- | --- |
| 11: O3 | ppb = ppm × 1000; 65535 significa no disponible | 347 = 0,347 ppm |
| 12: temperatura | int16 en complemento a dos, grados C × 100; 0x8000 no disponible | 2345 = 23,45 C; 65011 = -5,25 C |
| 14: bateria | porcentaje estimado 0..100; 65535 no disponible | 72 = aproximadamente 72 % |

El tipo 13 queda reservado al antiguo RUIDO. Major ya no contiene contador.
Android acepta tambien el antiguo formato tipo/contador. Para el nuevo limita
repeticiones a una lectura de O3 cada 4 segundos y genera una secuencia local de
recepcion 0..255 para conservar el contrato REST existente. Sin ID de muestra
transmitido no se puede garantizar la deduplicacion exacta; una rafaga dura 3 s.
Temperatura y bateria se muestran en Llavero durante la captura de un recorrido,
sin esperar al GPS; caducan a los 30 s y se limpian al parar la captura/cambiar llavero.
Solo O3 se guarda y envia al backend: el contrato actual no admite las otras magnitudes.

## Lecturas reales

ULPSM-O3 968-046: Vgas A3, Vref A4 y Vtemp A5, alimentacion 3,3 V y GND comun.
Se mantiene la sensibilidad -42,54 nA/ppm, ganancia 499 kV/A y el cero al arrancar.
El ADC se configura explicitamente a 12 bits, referencia interna de 3,6 V y
adquisicion de 40 us. Antes la conversion usaba 3,3 V pese al rango real de 3,6 V.
Temperatura: T = 87 × Vtemp / V_alimentacion_sensor - 18, coeficientes nominales
de la biblioteca del fabricante; la alimentacion configurada es 3,3 V.
Los decimales transmitidos no equivalen a una precision calibrada de 0,01 C.
No se modifica la compensacion termica del O3: se conserva la calibracion existente.

Al arrancar se calcula el cero: el sensor debe estar estabilizado en aire libre de
ozono. El fabricante recomienda 60 min de estabilizacion tras alimentar el sensor.
El programa conserva el arranque existente, sin imponer esa espera; reiniciar
para obtener el cero cuando el sensor este estable. Vref y Vtemp requieren
acondicionamiento/buffer; prolongar el tiempo del ADC no sustituye ese circuito.

## Bateria: limitacion del hardware actual

Esta SparkFun no tiene un circuito integrado para leer la bateria. Conectarla al
conector de alimentacion no permite obtener un porcentaje por software. Por eso
`Bateria.h` tiene `DIVISOR_INSTALADO=false` y envia 65535, no una carga ficticia.
La app presenta «No disponible», tanto con USB como con bateria sin circuito de lectura.

Se deja soporte opcional para una LiPo 1S y un divisor externo:
BAT+ -- 100 kohm -- A0 -- 100 kohm -- GND, con masa comun. No conectar BAT+
directamente a A0. Solo habilitar tras confirmar la bateria (LiPo 1S, 4,2 V max.),
montar/verificar el divisor y comprobar su tension con multimetro. Cambiar los
valores en ConfiguracionBateria si el montaje es distinto. El porcentaje es una
estimacion por voltaje, dependiente de carga, temperatura y estado de la bateria.
Sin bateria conectada, dejar la lectura deshabilitada: la placa no ofrece una
deteccion fiable de presencia de bateria y el cargador puede producir una tension.

## Fuentes y comprobacion fisica pendiente

- [Ficha ULPSM-O3](https://www.interlinkelectronics.com/downloads/subsidiary-docs/spec-sensors/ULPSM-O3-968-046_8-25-17.pdf)
- [Conversion de temperatura del fabricante](https://github.com/SPEC-Sensors/ULPSM/blob/master/src/ULP.cpp)
- [SparkFun: ausencia de lectura de bateria](https://community.sparkfun.com/t/read-nrf52840-battery-level/61214)

Tras subir el firmware y actualizar la app, seleccionar S-001 e iniciar un recorrido
con permisos BLE y ubicacion. La pantalla de prueba BLE debe alternar Major 11/12/14.
Comprobar temperatura con un termometro y O3 con una referencia adecuada; la
compilacion y las pruebas de formato no verifican calibracion ni cableado fisico.
