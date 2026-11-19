# Pantallas finales ELIA — 19/11/2026

Rama: feature/elia-pantallas-ajustes-animacion, desde develop c96bfae.

- Entrada animada con el emblema siguiendo una curva y revelado del paisaje; respeta reducción de animaciones.
- Flecha vectorial de regreso en perfil, recuperación, biometría y vinculación.
- Seguridad con formulario Material, visibilidad de contraseña y cambio real mediante la API existente.
- Notificaciones y ubicación con estado real y acceso explícito a los permisos de Android.
- Historial local con estado de envío y consulta de cuenta paginada mediante GET /usuarios/actual/mediciones.
- Reintento de toda la cola existente en lotes de 50, con resultado y aislamiento de sesión.

Validación: compilación debug y pruebas unitarias correctas. En teléfono Xiaomi Android 14: pasan las pruebas de 61 envíos, fallo de red/reintento y migración/aislamiento de SQLite. Las pruebas automáticas de navegación no se completaron: MIUI bloqueó el lanzamiento de actividades internas desde la instrumentación de la copia aislada. No se certifica revisión visual de esas pantallas.

PHP: sintaxis correcta; historial SQLite con 45 lecturas, tres páginas, aislamiento y cursores inválidos correcto; 28 comprobaciones existentes de perfil/llavero correctas. Falta prueba HTTP/MySQL del nuevo endpoint en el servidor real.

Despliegue pendiente: actualizar exclusivamente senda_s2_privado/src/business_logic/LogicaNegocio.php y senda_s2_privado/src/communication/ServidorRest.php, con respaldo previo. No requiere migración. Hasta desplegar, Mi cuenta mostrará error de carga; Este móvil seguirá disponible.

APK independiente fuera del repositorio: ELIA/SENDA-ELIA.apk. Usa la API HTTPS real y applicationId com.example.ozora.senda.elia.validation para coexistir con la app instalada, cuya firma es distinta. No contiene datos o credenciales de pruebas.
