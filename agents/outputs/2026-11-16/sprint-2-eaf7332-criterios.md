# Evidencia por criterio · Sprint 2 · 16/11/2026

Commit `eaf7332`. Revisión estática de los 32 criterios aportados por el equipo. Se detectaron cuatro incumplimientos sustentados por el código o el alcance explícito del contrato. Los demás requieren una comprobación funcional. Los resultados se refieren al código disponible en la revisión. Véase también el [informe de diseño, concurrencia y pruebas](sprint-2-eaf7332.md).

| Criterio | Texto del equipo | Estado | Evidencia y comprobación pendiente |
| --- | --- | --- | --- |
| HU-14-CA01 | Puede iniciar sesión con sus credenciales. | ◯ Pendiente de comprobar | Android llama a `POST /sesiones` (`LoginActivity.java:101`). Probar credenciales válidas. |
| HU-14-CA02 | El sistema comprueba que la cuenta está activa. | ◯ Pendiente de comprobar | El dominio comprueba si la cuenta está activa (`LogicaNegocio.php:78–79`). Probar una cuenta desactivada. |
| HU-14-CA03 | La aplicación identifica automáticamente el parque al que pertenece. | ◯ Pendiente de comprobar | El backend devuelve el parque y la interfaz lo usa (`LogicaNegocio.php:34–35`, `MainActivity.java:282`). Probar con cuentas de dos parques. |
| HU-14-CA04 | Las credenciales incorrectas muestran un mensaje de error. | ◯ Pendiente de comprobar | El acceso muestra el error de API (`LoginActivity.java:132`). Verificar el texto con credenciales incorrectas. |
| HU-15-CA01 | La aplicación permite escanear el QR. | ◯ Pendiente de comprobar | La interfaz abre `QrScannerActivity` (`MainActivity.java:459`). Probar lectura en dispositivo. |
| HU-15-CA02 | El servidor comprueba que el sensor pertenece al parque. | ✖ Incumple | La selección se guarda localmente (`MainActivity.java:461–464`). El parque se comprueba al guardar una medición (`LogicaNegocio.php:141–151`), después de vincular. Validar también al vincular. |
| HU-15-CA03 | No se permite vincular un sensor asignado a otro trabajador. | ✖ Incumple | El mismo flujo permite guardar localmente el QR antes de obtener validación de asignación. Hacer que el servidor confirme la asignación y verificar dos intentos simultáneos con cuentas distintas. |
| HU-15-CA04 | La aplicación muestra el sensor vinculado. | ◯ Pendiente de comprobar | La interfaz muestra el identificador seleccionado (`MainActivity.java:331–336`). Probar que el identificador corresponde a la vinculación aceptada. |
| HU-16-CA01 | La aplicación detecta los anuncios BLE del sensor vinculado. | ◯ Pendiente de comprobar | La captura filtra por emisora (`PreparadorMediciones.java:27–34`). Verificar con dos llaveros activos. |
| HU-16-CA02 | Se obtiene correctamente el valor de ozono. | ◯ Pendiente de comprobar | El preparador transforma la trama en medición (`PreparadorMediciones.java:47–51`). Verificar el valor contra una trama conocida. |
| HU-16-CA03 | El valor aparece en la pantalla. | ◯ Pendiente de comprobar | La pantalla consulta la última lectura (`MainActivity.java:345–375`). Verificar una recepción real en el recorrido. |
| HU-16-CA04 | Se indica si existen problemas de conexión. | ◯ Pendiente de comprobar | La captura informa de problemas de inicio (`CapturaLlaveroController.java:74`). Probar Bluetooth desactivado y pérdida de señal durante el recorrido. |
| HU-17-CA01 | La aplicación solicita los permisos de ubicación necesarios. | ◯ Pendiente de comprobar | La app solicita permiso de ubicación (`MainActivity.java:494–499`). Probar autorización y denegación. |
| HU-17-CA02 | Cada medición enviada incluye las coordenadas disponibles. | ◯ Pendiente de comprobar | La carga JSON incluye latitud y longitud (`MeasurementPayload.java:31–32`). Verificar captura y petición. |
| HU-17-CA03 | Las coordenadas corresponden a la ubicación obtenida por el dispositivo. | ◯ Pendiente de comprobar | Se extraen coordenadas de `Location` (`PreparadorMediciones.java:41`). Verificar con una ubicación conocida. |
| HU-17-CA04 | Si no se dispone de ubicación, la aplicación informa del problema y no inventa coordenadas. | ◯ Pendiente de comprobar | El preparador devuelve un error si falta ubicación (`PreparadorMediciones.java:34`). Verificar el aviso visible y la ausencia de datos inventados. |
| HU-18-CA01 | La aplicación envía el valor de ozono y su ubicación al servidor. | ◯ Pendiente de comprobar | Sincronización por `POST /mediciones`, con coordenadas en `MeasurementPayload.java:31–32`. Probar envío real. |
| HU-18-CA02 | El servidor valida y almacena los datos recibidos. | ◯ Pendiente de comprobar | `LogicaNegocio.php:141–177` valida asignación y persiste los datos. Verificar la fila y su lectura web. |
| HU-18-CA03 | La aplicación muestra una confirmación cuando el servidor acepta la medición. | ◯ Pendiente de comprobar | `MeasurementSync.java:53–55` registra confirmación tras la respuesta y `MainActivity.java:318` presenta el estado. Verificar en ejecución. |
| HU-18-CA04 | Si falla el envío, muestra un error y no indica que la medición se ha enviado correctamente. | ◯ Pendiente de comprobar | `MeasurementSync.java:57–72` conserva las lecturas y comunica los errores. Probar red caída y confirmación perdida. |
| HU-20-CA01 | La aplicación muestra el último valor de ozono recibido. | ◯ Pendiente de comprobar | La pantalla consulta y formatea el valor (`MainActivity.java:345–375`). Probar que se actualiza en ejecución. |
| HU-20-CA02 | Se indica la fecha y hora de la medición. | ◯ Pendiente de comprobar | Se formatea la fecha de la lectura (`MainActivity.java:361–363`). Contrastar hora local y valor almacenado. |
| HU-20-CA03 | Se identifica el sensor vinculado. | ◯ Pendiente de comprobar | La pantalla usa el identificador del llavero (`MainActivity.java:361–363`). Cambiar de sensor y verificar coherencia. |
| HU-20-CA04 | Se informa cuando todavía no se han recibido mediciones. | ◯ Pendiente de comprobar | La interfaz contempla el estado sin lecturas (`MainActivity.java:309`). Probar con un sensor recién seleccionado. |
| HU-20-CA05 | La aplicación muestra si está recibiendo anuncios del sensor. | ◯ Pendiente de comprobar | Existe una función de estado del sensor (`MainActivity.java:331`). Contrastar con anuncios reales durante un recorrido. |
| HU-20-CA06 | Informa cuando Bluetooth está desactivado. | ◯ Pendiente de comprobar | El controlador informa de fallo al iniciar BLE (`CapturaLlaveroController.java:74`). Probar que apagar Bluetooth durante el recorrido produce el aviso específico. |
| HU-20-CA07 | Muestra un aviso cuando deja de recibir mediciones durante un periodo determinado. | ◯ Pendiente de comprobar | Buscar el umbral acordado y verificar que hay aviso tras el silencio BLE con un reloj controlado. |
| HU-20-CA08 | El estado cambia cuando vuelve a recibir datos. | ◯ Pendiente de comprobar | Interrumpir y restablecer los anuncios del llavero y comprobar que vuelve el estado de recepción. |
| HU-09-CA01 | Puede registrar trabajadores con sus datos correspondientes. | ◯ Pendiente de comprobar | Formulario de alta → `POST /trabajadores` (`app.js:203`). Probar con cuenta de jefe de parque. |
| HU-09-CA02 | Cada trabajador queda asociado a su parque. | ◯ Pendiente de comprobar | El INSERT toma el parque del jefe autenticado (`LogicaNegocio.php:236–240`). Comprobar el registro persistido. |
| HU-09-CA03 | Puede consultar y desactivar cuentas de trabajadores. | ✖ Incumple | La API de trabajadores solo declara POST (`ServidorRest.php:59`) y el contrato califica el alta como temporal. Implementar listado y desactivación por parque. |
| HU-09-CA04 | No puede superar el número de plazas activas con sensor asignado permitido para su parque. | ✖ Incumple | El alta temporal carece de gestión de asignación y cupo (`doc/contracts/backend-android-v1.md`, sección HU-09). Definir y aplicar la invariante transaccional cuando se asigna o activa una plaza. |

## Alcance pendiente de confirmar a nivel de criterio

| HU | Criterios aportados | Ítems de captura | Estado de la historia |
| --- | ---: | ---: | --- |
| HU-14 | 4 | 6 | ◯ Pendiente |
| HU-15 | 4 | 6 | ✖ Incompleta según los criterios disponibles |
| HU-16 | 4 | 5 | ◯ Pendiente |
| HU-17 | 4 | 5 | ◯ Pendiente |
| HU-18 | 4 | 6 | ◯ Pendiente |
| HU-19 | 0 | 7 | ◯ Faltan criterios |
| HU-20 | 8 | 6 | ◯ Confirmar agrupación y versión |
| HU-09 | 4 | 6 | ✖ Incompleta según los criterios disponibles |
| HU-21 | 0 | 9 | ◯ Faltan criterios |
| HU-22 | 0 | 9 | ◯ Faltan criterios |

La aceptación formal del equipo y la comparación Hi‑Fi siguen pendientes.
