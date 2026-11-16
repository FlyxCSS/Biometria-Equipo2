# Alcance confirmado · Sprint 2

Fuente: las dos capturas de Sprint Backlog facilitadas por Julia. El primer elemento visible es la tarjeta de cabecera. Las diez tarjetas HU siguientes forman el alcance confirmado, incluida HU-09. Los contadores suman 65 ítems visibles de checklist. El significado y texto literal de cada ítem se verifican al incorporar las fichas.

| HU | Título | Ítems visibles |
| --- | --- | ---: |
| HU-14 | Iniciar sesión | 6 |
| HU-15 | Vincular llavero mediante QR | 6 |
| HU-16 | Recibir mediciones mediante Bluetooth | 5 |
| HU-17 | Registrar ubicación GPS | 5 |
| HU-18 | Enviar mediciones al servidor | 6 |
| HU-19 | Iniciar y finalizar un recorrido de medición | 7 |
| HU-20 | Consultar el estado del llavero y la última medición recibida | 6 |
| HU-09 | Registrar trabajadores | 6 |
| HU-21 | Recuperar el acceso a una cuenta | 9 |
| HU-22 | Iniciar sesión mediante autenticación biométrica | 9 |

## Criterios de aceptación

El texto pegado aporta 32 criterios para siete HU, asociados por el contenido de las historias. HU-20 reúne los dos bloques sobre última medición y estado Bluetooth. El bloque sobre almacenamiento sin Internet se conserva como contexto aparte porque el texto lo presenta como una historia independiente y las capturas no le asignan una tarjeta propia.

Los textos actuales difieren de los contadores de todas las tarjetas y HU-20 aporta ocho criterios frente a seis ítems visibles. El total de 65 refleja los ítems de las capturas. Confirma la versión definitiva de cada historia antes de cerrar su aceptación.

### HU-14

Como trabajador forestal, quiero iniciar sesión en la aplicación Android, para acceder a mi cuenta y utilizar el sensor asignado.

- **HU-14-CA01** Puede iniciar sesión con sus credenciales.
- **HU-14-CA02** El sistema comprueba que la cuenta está activa.
- **HU-14-CA03** La aplicación identifica automáticamente el parque al que pertenece.
- **HU-14-CA04** Las credenciales incorrectas muestran un mensaje de error.


### HU-15

Como trabajador forestal, quiero escanear el código QR de mi sensor, para vincularlo a mi cuenta.

- **HU-15-CA01** La aplicación permite escanear el QR.
- **HU-15-CA02** El servidor comprueba que el sensor pertenece al parque.
- **HU-15-CA03** No se permite vincular un sensor asignado a otro trabajador.
- **HU-15-CA04** La aplicación muestra el sensor vinculado.


### HU-16

Como trabajador forestal, quiero recibir automáticamente las mediciones del sensor mediante Bluetooth, para registrar el ozono durante mis recorridos.

- **HU-16-CA01** La aplicación detecta los anuncios BLE del sensor vinculado.
- **HU-16-CA02** Se obtiene correctamente el valor de ozono.
- **HU-16-CA03** El valor aparece en la pantalla.
- **HU-16-CA04** Se indica si existen problemas de conexión.


### HU-17

Como trabajador forestal, quiero que cada medición incorpore su ubicación GPS, para conocer dónde se ha registrado.

- **HU-17-CA01** La aplicación solicita los permisos de ubicación necesarios.
- **HU-17-CA02** Cada medición enviada incluye las coordenadas disponibles.
- **HU-17-CA03** Las coordenadas corresponden a la ubicación obtenida por el dispositivo.
- **HU-17-CA04** Si no se dispone de ubicación, la aplicación informa del problema y no inventa coordenadas.


### HU-18

Como trabajador forestal, quiero enviar las mediciones al servidor de Senda, para que los responsables puedan consultarlas desde la web.

- **HU-18-CA01** La aplicación envía el valor de ozono y su ubicación al servidor.
- **HU-18-CA02** El servidor valida y almacena los datos recibidos.
- **HU-18-CA03** La aplicación muestra una confirmación cuando el servidor acepta la medición.
- **HU-18-CA04** Si falla el envío, muestra un error y no indica que la medición se ha enviado correctamente.


### HU-19

El texto pegado no aporta la historia ni sus criterios.

### HU-20

Como trabajador forestal, quiero visualizar la última medición recibida del sensor, para comprobar que funciona correctamente.

- **HU-20-CA01** La aplicación muestra el último valor de ozono recibido.
- **HU-20-CA02** Se indica la fecha y hora de la medición.
- **HU-20-CA03** Se identifica el sensor vinculado.
- **HU-20-CA04** Se informa cuando todavía no se han recibido mediciones.

Como trabajador forestal, quiero conocer el estado de la comunicación Bluetooth, para saber si mi aplicación está recibiendo datos.

- **HU-20-CA05** La aplicación muestra si está recibiendo anuncios del sensor.
- **HU-20-CA06** Informa cuando Bluetooth está desactivado.
- **HU-20-CA07** Muestra un aviso cuando deja de recibir mediciones durante un periodo determinado.
- **HU-20-CA08** El estado cambia cuando vuelve a recibir datos.


### HU-09

Como jefe de parque, quiero crear cuentas para mis trabajadores forestales, para que puedan utilizar la aplicación Android.

- **HU-09-CA01** Puede registrar trabajadores con sus datos correspondientes.
- **HU-09-CA02** Cada trabajador queda asociado a su parque.
- **HU-09-CA03** Puede consultar y desactivar cuentas de trabajadores.
- **HU-09-CA04** No puede superar el número de plazas activas con sensor asignado permitido para su parque.


### HU-21

El texto pegado no aporta la historia ni sus criterios.

### HU-22

El texto pegado no aporta la historia ni sus criterios.

### Contexto de diseño · funcionamiento sin Internet

Como trabajador forestal, quiero conservar temporalmente las mediciones cuando no tengo Internet, para evitar perder los datos recogidos durante mis recorridos.

- Las mediciones pendientes se almacenan localmente.
- La aplicación diferencia entre mediciones pendientes y enviadas.
- Los datos pendientes se mantienen cuando se cierra y vuelve a abrir la aplicación.
- No se muestra una confirmación de envío mientras el servidor no haya aceptado los datos.
