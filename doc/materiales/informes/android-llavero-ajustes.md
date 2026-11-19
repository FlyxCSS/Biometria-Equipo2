# Android · Mi llavero, Ajustes y perfil

> Actualización del 18/11/2026: la sesión persistente, la vinculación en el servidor y la foto de cuenta se describen en [el informe actual](android-sesion-perfil-vinculacion.md). Los resultados de emulador de este documento son históricos; no se ha iniciado ningún emulador en esta revisión.

Integración de las cuatro referencias del archivo PBIO PROYECTO (6).zip.
Se implementan pantallas nativas Android, con tarjetas blancas, verde bosque,
ilustración vectorial del llavero y la navegación existente de cinco secciones.

## Comportamiento

- Mi llavero distingue selección local, ausencia de señal y recepción reciente.
  Solo muestra recepción si hay un recorrido activo y un anuncio del servicio de
  ese recorrido dentro del tiempo de silencio configurado.
- El QR y la entrada manual reutilizan el lector existente. La selección guarda
  su fecha local. Las selecciones anteriores sin fecha muestran «No disponible».
- Desvincular requiere confirmación y conserva las mediciones. Durante un
  recorrido el botón queda deshabilitado. La asignación en el servidor sigue
  siendo responsabilidad del parque.
- La batería y el firmware muestran «No disponible»: el protocolo BLE actual
  no expone esos datos.
- Ajustes ofrece perfil, contraseña, biometría, notificaciones, permisos,
  ayuda, configuración de campo, sincronización y cierre de sesión.
- Notificaciones solicita el permiso de Android 13 o posterior cuando hace
  falta; los estados mostrados proceden de los permisos reales del sistema.
- El perfil permite guardar nombre visible, teléfono y una imagen seleccionada
  con el selector de documentos de Android. Se guardan por trabajador y parque,
  solo en este dispositivo. El formulario explica esta limitación.
- El correo utilizado al iniciar sesión se muestra como dato de consulta.
  El parque también es de consulta. No se usan el nombre, la foto ni los datos
  personales de ejemplo de las referencias.
- La contraseña usa POST /usuarios/actual/contrasena, exige la contraseña
  actual, coincidencia y longitud de 12 a 72 bytes UTF-8. Tras confirmación del
  servidor se borra el acceso biométrico y se solicita iniciar sesión de nuevo.
  Se impide abrir el cambio desde Ajustes si hay un recorrido activo.

## Verificación

- Compilación: :app:assembleDebug y :app:assembleDebugAndroidTest.
- Pruebas JVM: :app:testDebugUnitTest.
- KeyringStatusTest comprueba asociación al recorrido, ausencia de anuncios,
  caducidad exacta del aviso y marcas de tiempo incompatibles.
- AccountScreensTest comprueba los estados del llavero, la confirmación de
  desvinculación, el bloqueo durante un recorrido, la caducidad de la recepción
  y el guardado local del perfil. Usa exclusivamente una cuenta y rutas de
  prueba sin mediciones ni credenciales del servidor.
- Las capturas de las pruebas representan datos locales de prueba. No prueban
  recepción BLE física, envío de correos o un cambio de contraseña en producción.

Para ejecutar las pruebas conectadas, seleccionar explícitamente el emulador
mediante ANDROID_SERIAL; así no se incluyen teléfonos que se conecten durante
la ejecución.

Resultado de la comprobación del 17/11/2026: compilación Debug y APK de pruebas correctos, 21 pruebas JVM y 2 pruebas de interacción superadas en el emulador Small Phone (Android 16).
