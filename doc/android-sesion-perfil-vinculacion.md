# SENDA · Sesión, biometría, perfil y vinculación

Revisión del 18/11/2026. Cambios aplicados al repositorio local y al servidor Plesk. Sin commit, push ni merge. No se ha iniciado ningún emulador ni instalado esta revisión en el teléfono.

## Sesión y acceso biométrico

La pantalla de acceso se llama ahora `auth/Login.java`; el Manifest y sus referencias apuntan a esa clase. El cierre temporal de una actividad, salir al lector QR de Google y cerrar la app ya no bloquean ni borran la sesión.

`SendaApplication` restaura la sesión al arrancar. `SessionManager` guarda token, trabajador, parque y vencimiento cifrados con AES/GCM y una clave del almacén AndroidKeyStore. El archivo de preferencias contiene el cifrado y su IV. La restauración respeta la caducidad original del servidor; no crea sesiones ilimitadas. Una respuesta 401 también elimina el acceso local. Esta versión necesitará un inicio con contraseña si la versión anterior nunca guardó la sesión.

Flujo de la huella:

1. Antes de activarla, el botón biométrico indica que hay que entrar con contraseña y activar la opción.
2. Tras entrar con contraseña se ofrece la pantalla de activación. «Ahora no» permite continuar; la opción sigue disponible en Ajustes.
3. Activarla abre el diálogo biométrico real de Android. Solo tras una comprobación correcta se guarda otra copia del token, cifrada con una clave que exige biometría fuerte en cada uso.
4. Cerrar la app conserva la sesión iniciada, sin pedir huella en cada reapertura.
5. Cerrar sesión borra la sesión automática. Si la biometría está activada, conserva exclusivamente su copia cifrada y la sesión correspondiente del servidor hasta su vencimiento original. Así el siguiente acceso con huella puede descifrar la credencial y validarla mediante GET /sesiones/actual. Sin biometría activada, cerrar sesión también solicita revocar el token en el servidor.
6. Si la sesión vence, se revoca, cambia la contraseña o Android invalida la clave biométrica, se necesita de nuevo la contraseña. La recuperación y el cambio de contraseña continúan borrando el acceso biométrico.

La app no lee ni envía la huella al servidor, ni guarda la contraseña para este acceso. Android comprueba la biometría y permite utilizar la clave. La sesión automática y la credencial biométrica tienen claves y preferencias separadas.

## Perfil y roles

- POST /api/v1/usuarios/actual/foto guarda la foto en USUARIOS.foto_perfil de la cuenta autenticada. El cliente no elige otro id_usuario.
- Android reduce la imagen y la convierte en JPEG en segundo plano. El servidor comprueba formato real, dimensiones máximas de 512 × 512 y hasta 100000 bytes. Rechaza datos ajenos al contrato. La respuesta de sesión devuelve la foto para recuperarla en el dispositivo.
- Nombre visible y teléfono conservan su funcionamiento local, incluso sin cambiar la foto; solo la foto se sincroniza. Las fotos antiguas basadas en una URI local deben volver a seleccionarse para subirlas.
- ROLES ya tenía id_rol, codigo y nombre. Se ha añadido descripcion y rellenado para los cuatro roles existentes. Se conservan identificadores y relaciones.
- Migración: src/database/migrations/004_perfil_roles.sql, para aplicar una sola vez. El comando CLI actualizar-perfil comprueba si faltan columnas antes de crearlas. database.sql incluye el resultado para instalaciones nuevas.

## Vinculación del llavero

La asignación de la base de datos prevalece sobre la selección guardada en el móvil. Al entrar o volver al panel, GET /sesiones/actual devuelve el llavero asignado y actualiza la caché local. Sin conexión se conserva la última información disponible.

El QR y la entrada manual continúan usando el lector existente. Tras obtener el identificador, LogicaFake envía POST /llaveros/actual; no lo da por vinculado hasta la confirmación. El REST comprueba petición y sesión; LogicaNegocio comprueba rol de trabajador, formato, existencia, estado, parque, organización y propietario. Si pertenece a otra persona devuelve 409 con un mensaje explícito. Si el trabajador ya tiene otro llavero también se rechaza. La transacción, los bloqueos de filas y la restricción única protegen la asignación; repetir la vinculación propia es válido.

Desvincular pide confirmación, envía DELETE /llaveros/actual y deja id_usuario a NULL. Conserva el sensor y las mediciones. Durante un recorrido activo sigue deshabilitado. Si no hay confirmación de red, no se presenta un éxito local ficticio. No se han añadido nuevas clases a network: las tres operaciones nuevas están en LogicaFake y reutilizan ApiClient.

## Interfaz

- El botón principal de Inicio comparte AuthButton y ancho útil con el de Rutas.
- Pulsar la pestaña ya seleccionada no vuelve a construir su pantalla ni el mapa.
- «Mi ubicación» es un botón circular con un punto azul y un círculo claro, encima de la tarjeta de última medición.
- El mapa de calor arranca activado y conserva su selección al cambiar de sección o recrear la actividad.
- Se mantienen el tratamiento de barras del sistema y los permisos existentes.

## Plesk y limpieza

Publicados business_logic/LogicaNegocio.php, communication/ServidorRest.php, database/database.sql y la migración 004. El controlador público y la configuración privada existente no se han sustituido. Respaldo previo de los módulos del servidor: /senda_s2_privado/src/respaldo-servidor-sesion-perfil-20261118.zip.

Tras la confirmación expresa del usuario se han eliminado 160 mediciones antiguas y conservado las últimas 20 por fecha_hora descendente, con id_llavero como desempate. Antes se copiaron las 180 originales a MEDICIONES_respaldo_20261118_2139. La limpieza solo afectó a registros incluidos en esa copia; no es una limpieza automática periódica y no borra el historial local Android. El respaldo sigue guardado en la misma base para recuperación.

La comprobación posterior en phpMyAdmin mostró 20 mediciones activas y 180 respaldadas, además de la columna de foto y las descripciones de roles. La web pública devuelve HTTP 200. Las nuevas rutas de foto y vinculación, y la consulta de sesión, responden 401 al intentar acceder sin credenciales.

## Pruebas ejecutadas y límites

- assembleDebug y assembleRelease: correctos. Release genera APK sin firma de distribución, como la configuración actual del proyecto.
- testDebugUnitTest y testReleaseUnitTest: 31 casos por variante, 30 correctos, 1 omitido por necesitar la fixture externa del contrato, sin fallos ni errores.
- assembleDebugAndroidTest: correcto; incluye SessionPersistenceTest y las pruebas de interfaz actualizadas. Compilar estos tests no equivale a ejecutarlos en Android.
- PHP: sintaxis de los archivos modificados correcta; 25 comprobaciones de validación y 28 de perfil/llavero correctas. Estas últimas usan SQLite aislado para comprobar foto válida e inválida, pertenencia, parque, ocupación, repetición y desvinculación. No certifican concurrencia de MySQL.
- Proxy web: 5 pruebas Node correctas.
- No se ha ejecutado la batería HTTP/MySQL completa: este PHP local no tiene pdo_mysql ni una base local aislada de integración. No se han utilizado cuentas reales para pruebas automáticas de escritura en producción.
- La restauración con AndroidKeyStore, huella física, ida/vuelta del lector Google, GPS y BLE con el sensor deben comprobarse en el móvil. Las pruebas de interfaz con identidades ficticias deben usar un backend de pruebas/offline configurado mediante SENDA_API_BASE; el panel ahora valida la sesión real y rechazará esos tokens frente a producción.

## Comprobación manual sugerida

1. Instalar el APK Debug generado, entrar con contraseña y elegir «Ahora no». Cerrar/reabrir la app y abrir/cancelar el lector Google: debe conservarse la cuenta.
2. Activar la biometría desde Ajustes; cerrar sesión y volver con la huella. Cancelar el diálogo debe dejar la pantalla de acceso. Si el servidor ha caducado la sesión, entrar con contraseña.
3. Cambiar y guardar una foto; reabrir el perfil y comprobar que USUARIOS.foto_perfil contiene la imagen. Probar también un cambio de nombre sin foto y sin conexión.
4. Comprobar el llavero asignado. Desvincular fuera de un recorrido y verificar id_usuario a NULL. Vincularlo con otra cuenta autorizada del mismo parque; la primera cuenta debe recibir el rechazo al intentarlo de nuevo.
5. Comprobar Inicio, tamaño del botón, ubicación del control azul y calor activo. Pulsar varias veces la pestaña seleccionada: el mapa y su estado deben mantenerse.
6. Iniciar/finalizar un recorrido real, recibir BLE, capturar GPS y comprobar el envío al servidor y las estadísticas. La limpieza de servidor conserva una copia de las lecturas anteriores.

## Ajustes de interacción posteriores · 18/11/2026

- Inicio y Rutas dejan ahora 12 dp entre el botón principal y la navegación inferior.
- FieldScreen comparte un único aviso Snackbar de 3000 ms por pantalla, con borde redondeado y contraste oscuro. Error rosa/rojo y confirmación verde. Mientras está visible no se encolan nuevas pulsaciones; se descarta al abandonar la pantalla. Los ajustes de accesibilidad de Android pueden ampliar su duración.
- Vinculación y desvinculación muestran la confirmación o el error real del servidor con estos avisos. El acceso los usa para biometría no activada, validación de campos y errores de autenticación. Un inicio correcto cancela inmediatamente el aviso pendiente.
- El perfil permite quitar la foto. Se confirma en el servidor al pulsar Actualizar mediante el contrato ya existente (foto_perfil: null). Solo tras guardarse correctamente regresa a Ajustes; si falla la red permanece en el formulario. El parque se presenta como texto de consulta sin fondo de entrada. La actualización de sesión también elimina de la caché una foto borrada en el servidor.
- El servicio de recorrido conserva su notificación mientras captura y añade la acción «Ver recorrido», que abre el recorrido activo mediante MainActivity. En Android 13 o posterior, al iniciar se solicita el permiso de notificaciones si falta; denegarlo no impide capturar, pero Android puede ocultar la notificación del panel. El canal debe seguir habilitado en el sistema.
- El mapa de información del recorrido captura sus gestos de arrastre y zoom hasta soltarlos, evitando que el ScrollView padre desplace la página. Parque se muestra a 20 sp y en negrita.
- Esta revisión solo necesita cambios Android; el servidor publicado ya admite eliminar la foto. No se han creado nuevas copias de seguridad ni realizado commit, push o merge.
- Validación: Debug, Release y APK de pruebas instrumentadas compilados. Pruebas JVM: 30 correctas y 1 omitida por fixture externa en cada variante; sin fallos. Las comprobaciones físicas de avisos, gestos, permisos y notificación quedan para el teléfono, sin iniciar emuladores ni modificar su instalación.

## Lecturas estando quieto y botón de foto

Eliminar foto queda deshabilitado y gris cuando no hay imagen seleccionada. La captura pedía posiciones cada 2 segundos con un desplazamiento mínimo de 2 metros, pero rechazaba ubicaciones de más de 10 segundos. Se ha eliminado el mínimo de desplazamiento para permitir actualizar la posición estando quieto; se conservan los controles de antigüedad y precisión y el filtro de puntos del recorrido. No hay un límite de cuatro mediciones ni un filtro por igualdad del valor de ozono: la deduplicación BLE usa contador y tiempo. Sin una posición reciente y válida seguirá descartando lecturas. Esta causa es compatible con el síntoma comunicado; falta comprobar la captura física en el móvil para confirmar si es la única causa.
