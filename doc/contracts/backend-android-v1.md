<!--
Nombre: backend-android-v1.md
Descripción: Revisión inicial y propuesta de contrato REST para el Sprint 2.
Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
Fecha: 02/11/2026
Autor: Julia (con asistencia de Codex).
Aportación: Documentar la base existente, los JSON y las decisiones de integración pendientes.
-->

# Backend y Android: propuesta v1

Estado: IMPLEMENTADO LOCALMENTE como contrato provisional v1; pendiente de revisar con las Personas 1 y 2. Los ejemplos son ficticios. No constituye un acuerdo ya aprobado por los otros programadores.

Cambio solicitado por Julia el 03/11/2026: el jefe establece `contrasena_inicial` al registrar al trabajador. El usuario debe cambiarla en su primer acceso; el backend devuelve `usuario.debe_cambiar_contrasena`. Se incorpora `POST /usuarios/actual/contrasena`. Android debe integrar esta pantalla antes de usar mediciones con una cuenta nueva. La recuperación por correo se mantiene para contraseñas olvidadas; el alta nueva ya no depende del correo.

Actualización con vuestro diagrama: se conservan las cinco tablas y la PK `(fecha_hora, id_llavero)` de MEDICIONES. `nombre_micro` en JSON se resuelve por `LLAVEROS.identificador_ble` a la PK `id_llavero`. Se usan milisegundos (`DATETIME(3)`) y campos técnicos de unidad, procedencia, autor y parque histórico. Detalles en `doc/backend-puesta-en-marcha.md`.

Decisiones provisionales implementadas: sesión de 24 horas sin renovación; roles `jefe_parque` y `trabajador`; coordenadas obligatorias; O3 entero en ppb; máximo cinco minutos de adelanto del reloj de captura. Todas las rutas de la sección 4 están implementadas. El correo se encola y se entrega con `php tools/gestionar.php correo`; probado en buzón privado local, sin destinatarios reales. Se añade el error 413 para cuerpos mayores de 16 KiB.

## 1. Revisión del repositorio

Base: `develop`, commit `7dc6588`. Rama de trabajo: `feature/backend-web-simulacion`.

En la revisión inicial de ese commit estos archivos existían, pero tenían 0 bytes:

- `src/communication/ServidorRest.php`: sin rutas ni servidor REST.
- `src/business_logic/LogicaNegocio.php`: sin lógica de negocio.
- `src/database/database.sql`: sin tablas ni restricciones.
- `src/frontend_business_logic/LogicaFake.js`: sin cliente web.
- `src/web/index.html`, `src/web/js/app.js`, `src/web/css/estilos.css` y `src/web/senda/senda.html`: sin web funcional.

Ese commit no tenía autenticación, sesiones, PDO ni pruebas. Esta rama ya las implementa conservando la separación existente, sin trasladarla a `src/server/`.

Referencia consultada, sin mezclar ramas: `origin/feature/android-ble-qr-gps`, commit `0fe7066`:

- `QrLlaveroParser.java` acepta identificadores `S-` seguidos de 3 a 6 dígitos; normaliza espacios exteriores y mayúsculas.
- `Publicador.h` anuncia `S-001` y prevé enviarlo al backend como `nombre_micro`.
- `IBeaconParser.java` interpreta tipo BLE 11 como O3 y Minor como ppb, con conversión opcional a ppm. El contador BLE tiene 8 bits y no es un identificador único de medición.

El JSON conservará `nombre_micro`. No utilizar UUID del proyecto ni contador BLE como identidad del llavero. Validar QR o nombre BLE localmente no acredita permisos: el servidor comprobará la asignación.

## 2. Alcance y propiedad

Persona 3: servidor, MySQL, autenticación, recuperación, guardado y consulta de mediciones, web auxiliar y pruebas REST. Persona 1: cliente de envío Android, BLE, QR y GPS. Persona 2: cliente de autenticación, sesión local, biometría y recorridos.

HU-18 es compartida con Persona 1. HU-21 incluye el servidor de recuperación y su integración con la interfaz. La web temporal da soporte a HU-09; no permite declarar terminada toda la historia de gestión de trabajadores.

El documento anterior del backlog situaba HU-18 en Sprint 3; el nuevo contexto la incorpora al Sprint 2. Este borrador sigue el reparto nuevo y no modifica Trello.

## 3. Convenciones propuestas

- Base lógica `/api/v1`; la URL real y la configuración del servidor están pendientes. No se presupone que PHP resuelva estas rutas sin configurar un enrutador.
- HTTPS en el despliegue; HTTP solo para pruebas locales controladas.
- Peticiones con cuerpo: `Content-Type: application/json`. Respuestas JSON; UTF-8.
- Fechas UTC con milisegundos y sufijo Z: `2026-11-02T12:00:00.000Z`. Android genera una sola fecha por lectura y la conserva al reintentar.
- IDs de usuario y parque: enteros positivos. `nombre_micro`: cadena canónica, por ejemplo `S-001`.
- El servidor obtiene usuario, rol, parque y organización desde la sesión y la BD; no acepta esos permisos desde el cuerpo de una medición.
- Tokens opacos de sesión, aleatorios, enviados como `Authorization: Bearer <token>`. Guardar solo su hash en BD. Nunca en URLs ni registros. Android deberá proteger el token localmente.
- Propuesta inicial: sesión de 24 horas, sin renovación automática en v1; al caducar se vuelve a iniciar sesión. Requiere acuerdo con Persona 2. La biometría local no sustituye la autorización del servidor.

Respuesta correcta: `{"ok":true,"data":{...}}`.

Respuesta de error, sin SQL, contraseñas, tokens ni trazas:

```json
{"ok":false,"error":{"codigo":"DATOS_INVALIDOS","mensaje":"Revisa los campos indicados.","campos":{"latitud":"Debe estar entre -90 y 90."}}}
```

`campos` es opcional. Estados comunes: 400 JSON mal formado; 401 sesión ausente, inválida o caducada; 403 sin permiso; 404 recurso no accesible o inexistente; 405 método no permitido; 409 conflicto; 415 contenido no JSON; 422 validación; 429 demasiados intentos; 500 error interno; 503 dependencia temporalmente no disponible. No mostrar detalles internos al cliente.

## 4. Operaciones propuestas

La notación es entrada -> nombre de operación -> salida. Las operaciones de la tabla están implementadas en esta rama; los nombres lógicos describen el contrato HTTP.

| Entrada -> operación -> salida | Método y ruta | Descripción |
|---|---|---|
| correo, contraseña -> iniciarSesion -> sesión, usuario | POST `/sesiones` | Comprobar cuenta activa y contraseña; crear sesión. |
| sesión -> consultarSesion -> usuario | GET `/sesiones/actual` | Verificar sesión y devolver usuario, rol y parque actuales. |
| sesión -> cerrarSesion -> cerrado | DELETE `/sesiones/actual` | Revocar la sesión presentada. |
| sesión, medición -> guardarMedicion -> identidad, resultado | POST `/mediciones` | Validar permisos y persistir una lectura. |
| sesión, parque -> consultarUltimaMedicion -> medición o null | GET `/parques/{id_parque}/mediciones/ultima` | Última lectura de cualquier llavero del parque autorizado. |
| sesión de jefe, nombre, correo, contraseña inicial -> registrarTrabajador -> usuario | POST `/trabajadores` | Crear trabajador con contraseña temporal en el parque del jefe. |
| sesión, contraseña actual y nueva -> cambiarContrasena -> actualizado | POST `/usuarios/actual/contrasena` | Cambiar la contraseña propia y revocar sesiones. |
| correo -> solicitarRecuperacion -> mensaje genérico | POST `/recuperaciones` | Solicitar enlace sin revelar si existe la cuenta. |
| token, contraseña nueva -> restablecerContrasena -> actualizado | POST `/recuperaciones/confirmacion` | Consumir un enlace válido y cambiar contraseña. |

### Sesiones

Petición de inicio:

```json
{"correo":"trabajador@example.org","contrasena":"EjemploSoloParaPruebas-2026"}
```

Respuesta 200:

```json
{"ok":true,"data":{"token":"<token-opaco>","expira_en":"2026-11-03T12:00:00.000Z","usuario":{"id_usuario":12,"nombre":"Trabajador de prueba","rol":"trabajador","id_parque":3,"debe_cambiar_contrasena":true}}}
```

Propuesta de roles de esta entrega: `trabajador` y `jefe_parque`; otros roles del producto quedan pendientes. `GET /sesiones/actual` devuelve `data.usuario` con la misma estructura y `data.expira_en`, sin reemitir el token. Cierre válido: 200 con `{"ok":true,"data":{"cerrado":true}}`.

Correo o contraseña incorrectos y cuenta inactiva: mismo error 401 `CREDENCIALES_INVALIDAS`. Contraseñas con `password_hash` y `password_verify`; limitar intentos. Comprobar cuenta activa también en cada petición protegida, no solo al iniciar sesión.

### Guardado de mediciones: HU-18

Petición:

```json
{"nombre_micro":"S-001","tipo":"O3","valor":42,"unidad":"ppb","fecha_hora":"2026-11-02T12:00:00.000Z","latitud":39.4699,"longitud":-0.3763,"simulada":true}
```

Propuesta v1: todos esos campos son obligatorios. `simulada` es booleano y permite presentar los datos de prueba como tales. La referencia técnica actual admite O3 en ppb; no convertir silenciosamente ppm ni admitir temperatura sin ampliar el contrato.

Validaciones: nombre canónico `^S-[0-9]{3,6}$`; llavero activo y asignado al trabajador y a su parque; tipo `O3`; unidad `ppb`; valor entero de 0 a 65535 (rango técnico de Minor, no umbral de calidad del aire); fecha real en el formato acordado; latitud numérica entre -90 y 90 y longitud entre -180 y 180. Rechazar booleanos o cadenas donde se esperan números.

Sin GPS: se propone que Android conserve la lectura pendiente y no la envíe a este endpoint hasta disponer de ubicación válida asociada a esa captura. Nunca adjuntar después una ubicación de otro instante como si fuera la original. Acordar con Persona 1 si se necesita almacenar lecturas sin ubicación mediante otro contrato; no inventar coordenadas.

Respuesta 201 solo después de confirmar el guardado:

```json
{"ok":true,"data":{"nombre_micro":"S-001","fecha_hora":"2026-11-02T12:00:00.000Z","guardada":true,"duplicada":false}}
```

Aplicar la unicidad del diagrama `(fecha_hora, id_llavero)`, resolviendo primero `nombre_micro` a `id_llavero`. Para reintentos: si ya existe exactamente la misma lectura y pertenece al mismo autor y parque histórico, responder 200 con `guardada:true` y `duplicada:true`, sin insertar otra fila. Si cambia algún dato, 409 `MEDICION_EN_CONFLICTO`. No sobrescribir. Comprobar autorización antes de responder sobre duplicados.

Sin permiso: 403 `LLAVERO_NO_AUTORIZADO`. Campos incorrectos: 422 `DATOS_INVALIDOS`. Android solo muestra guardado confirmado con 200/201 y `guardada:true`; debe conservar la misma identidad y cuerpo al reintentar tras pérdida de conexión.

Recorridos: pendiente acordar persistencia con Persona 2. Esta v1 no recibe `id_recorrido` ni promete guardar rutas. Su incorporación requerirá definir propiedad, inicio/cierre y relación con puntos GPS antes de implementarla.

### Última medición del parque

Solo jefe autorizado del parque en esta primera propuesta. Seleccionar por `fecha_hora DESC, nombre_micro ASC` para resolver empates de forma estable. Última se refiere a fecha de captura, no de recepción. Guardar también `recibida_en` en el servidor. Usar el parque registrado en la medición al capturarla; reasignar un llavero no debe trasladar el histórico a otro parque.

Respuesta 200:

```json
{"ok":true,"data":{"id_parque":3,"medicion":{"nombre_micro":"S-001","tipo":"O3","valor":42,"unidad":"ppb","fecha_hora":"2026-11-02T12:00:00.000Z","recibida_en":"2026-11-02T12:00:02.000Z","latitud":39.4699,"longitud":-0.3763,"simulada":true}}}
```

Sin lecturas: 200 con `{"ok":true,"data":{"id_parque":3,"medicion":null}}`. Otro parque: 404 `PARQUE_NO_DISPONIBLE`. La web distingue sin datos, error de conexión y lectura simulada; no transforma ninguno en un valor cero.

### Alta temporal de trabajadores: soporte HU-09

Solo jefe activo. El parque se obtiene de su sesión, no de un selector enviado por el navegador.

```json
{"nombre":"Trabajador de prueba","correo":"trabajador@example.org","contrasena_inicial":"InicialSoloParaPruebas-2026"}
```

El jefe introduce una contraseña inicial de 12 a 72 bytes UTF-8. Se guarda su hash y `debe_cambiar_contrasena=1`; nunca se devuelve la contraseña. El jefe debe comunicársela al trabajador. No se genera correo de alta ni se crea una cuenta si falta la contraseña o incumple la política.

Respuesta 201: `{"ok":true,"data":{"usuario":{"id_usuario":12,"nombre":"Trabajador de prueba","rol":"trabajador","id_parque":3,"debe_cambiar_contrasena":true},"requiere_cambiar_contrasena":true,"mensaje":"Cuenta creada con contraseña inicial. Comunícala al trabajador: deberá cambiarla en su primer acceso."}}`.

Validar nombre y correo y aplicar unicidad de correo en BD. Si no puede crearse: 409 `ALTA_NO_DISPONIBLE`, sin identificar cuentas de otros parques. La asignación de llavero y la desactivación/listado completos de HU-09 no forman parte de esta operación temporal; preparar asignaciones de prueba en datos SQL identificados como ficticios.

### Cambio de contraseña desde Android o la web de prueba

Tras el login, si `usuario.debe_cambiar_contrasena=true`, mostrar obligatoriamente la pantalla de cambio. La sesión permite consultar su propio estado, cerrar sesión y cambiar contraseña; cualquier otra operación protegida responde 403 `CAMBIO_CONTRASENA_REQUERIDO`.

`POST /api/v1/usuarios/actual/contrasena`, con `Authorization: Bearer <token>`:

```json
{"contrasena_actual":"InicialSoloParaPruebas-2026","contrasena_nueva":"PersonalSoloParaPruebas-2026"}
```

Respuesta 200: `{"ok":true,"data":{"actualizada":true,"requiere_iniciar_sesion":true}}`. Android debe borrar la sesión local y pedir un nuevo login. El servidor revoca todas las sesiones y enlaces pendientes del usuario, guarda el hash nuevo y elimina el indicador de cambio obligatorio en una transacción. No recibe un ID de usuario: solo permite cambiar la contraseña propia.

Contraseña actual incorrecta: 403 `CONTRASENA_ACTUAL_INVALIDA`. Contraseña nueva igual a la actual o fuera de la política: 422 `DATOS_INVALIDOS`. Sesión inexistente/caducada: 401. La misma operación permite al usuario cambiar voluntariamente su contraseña posteriormente, verificando la actual. La contraseña inicial deja de funcionar tras el cambio.

### Recuperación: HU-21

Petición: `{"correo":"trabajador@example.org"}`.

Respuesta 202 para correo sintácticamente válido, exista o no cuenta activa:

```json
{"ok":true,"data":{"mensaje":"Si la cuenta permite recuperar el acceso, recibirás un enlace por correo."}}
```

Token aleatorio, hash en BD, caducidad de 30 minutos y un solo uso. No devolverlo en JSON. Limitar intentos sin revelar existencia de cuentas. Usar URL base configurada, no la cabecera Host del solicitante. En pruebas locales, un buzón de pruebas puede sustituir SMTP; no declarar probado el envío real por ello.

Confirmación: `{"token":"<token-del-enlace>","contrasena_nueva":"OtroEjemploSoloParaPruebas-2026"}`.

Respuesta 200: `{"ok":true,"data":{"actualizada":true}}`. Token inválido, usado o caducado: 422 `ENLACE_INVALIDO`, con el mismo mensaje. No reactivar cuentas inactivas. Consumir token y actualizar contraseña en una transacción; revocar sesiones e invalidar otros enlaces pendientes tras el cambio. Propuesta de longitud de contraseña: 12 a 72 bytes UTF-8, pendiente de acuerdo con Persona 2 y del algoritmo elegido.

## 5. Modelo MySQL propuesto

La tabla siguiente conserva la propuesta conceptual inicial. El esquema implementado se basa en el diagrama aportado después por Julia; ver la correspondencia exacta en `doc/backend-puesta-en-marcha.md`. LLAVEROS usa PK `id_llavero` y BLE único; MEDICIONES usa PK `(fecha_hora, id_llavero)`. No importar el esquema sobre datos existentes sin una migración.

| Entidad | Datos y restricciones principales |
|---|---|
| Organizaciones | Identidad y estado; aislamiento de clientes. |
| Parques | Identidad, organización y estado. |
| Usuarios | Nombre, correo único normalizado, hash de contraseña, rol, parque, estado. |
| Llaveros | PK `id_llavero`, `identificador_ble` único, parque, trabajador asignado y estado; impedir asignaciones incompatibles. |
| Mediciones | PK `(fecha_hora, id_llavero)`; nombre BLE histórico, valor, tipo, unidad, coordenadas, simulada, autor, parque histórico y recepción. |
| Sesiones | Hash único del token, usuario, caducidad y revocación. |
| TokensAcceso | Hash único, usuario, finalidad (alta o recuperación), caducidad y uso. |

Usar InnoDB, claves foráneas, consultas preparadas PDO y UTC con precisión de milisegundos. Índice de consulta por parque y fecha. Evitar borrados en cascada del histórico; desactivar usuarios y llaveros. No guardar secretos del entorno en Git.

## 6. Pruebas de aceptación a preparar al implementar

1. Login válido, contraseña incorrecta, cuenta inactiva y límite de intentos.
2. Acceso sin token, con token caducado y tras cierre o desactivación.
3. Alta por jefe del parque; correo repetido; impedir alta por trabajador.
4. Medición válida y verificación directa de la fila MySQL; error JSON y campos fuera de rango sin insertar.
5. Rechazar llavero ajeno o inactivo y comprobar aislamiento entre dos parques.
6. Reintento idéntico sin duplicar; contenido diferente con la misma clave da 409.
7. Última medición entre dos llaveros, orden de captura diferente del de recepción, empate y parque vacío.
8. Reasignación de llavero sin alterar el parque de las mediciones anteriores.
9. Recuperación con respuesta genérica, caducidad, segundo uso, cuenta inactiva y revocación de sesiones.
10. Web con alta real, última lectura simulada claramente indicada y tratamiento de error sin mostrar datos ficticios como reales.

Guardar en `tests/` ejemplos y evidencias sin credenciales ni tokens reales. La entrega ya ejecuta pruebas con PHP y MySQL; resultados y alcance en `tests/evidencia-backend.md`.

## 7. Decisiones pendientes de coordinar con el equipo

Ampliación de web pública (independiente de Android): `POST /api/v1/solicitudes`, sin sesión, recibe `nombre`, `correo`, `organizacion` y `mensaje`. Devuelve 201 con `ok:true` y `data.mensaje` después de persistir la solicitud. Nombre y organización: 1 a 100 caracteres; correo válido hasta 100 bytes; mensaje: 10 a 4000 caracteres. Errores 422 por validación, 429 por límite y 405 si se intenta consultar la lista públicamente. No crea usuarios ni envía correo externo. Los endpoints Android conservan sus rutas y JSON.

- Personas 1 y 2: aceptar rutas, nombres, UTC con milisegundos, ppb, respuesta de errores e idempotencia.
- Persona 1: acordar comportamiento sin GPS, posible reloj adelantado y frecuencia de capturas para evitar colisiones temporales.
- Persona 2: acordar duración/renovación de sesión, roles, enlace de recuperación, política de contraseña y persistencia de recorridos.
- Equipo: validar esquema completo del Sprint 1, asignaciones de llavero y política temporal de fechas futuras.
- Entorno: confirmar PHP/MySQL disponibles, despliegue HTTPS, credenciales locales fuera de Git y servicio de correo de pruebas/real.

Orden aplicado en esta entrega: SQL y datos de prueba -> PDO y sesiones -> mediciones y consulta por parque -> recuperación/alta -> web auxiliar. Siguiente paso compartido: contrastar el contrato e integrar con Android.

En cada fichero de código se incluirán nombre, descripción, copyright, fecha, autor y aportación. Cada función tendrá entrada -> nombre -> salida y una descripción breve. Mantener comentarios útiles y código sencillo.
