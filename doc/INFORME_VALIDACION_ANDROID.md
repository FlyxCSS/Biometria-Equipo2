# Informe de validación Android SENDA

Fecha: 2026-11-03. Base: b45d26b. Rama local: fix/android-integracion-final.
JBR 21.0.8; Gradle 8.13; AGP 8.13.2; compile/target SDK 36; min SDK 24.
Dispositivo detectado: Xiaomi 22101320G, Android 14 (redwood). No se han iniciado emuladores después de la instrucción del usuario; se detuvo el que se había arrancado anteriormente.

## Modo demostración exclusivo de Debug

En la pantalla de acceso aparece «Entrar en demostración (Debug)». No necesita correo, contraseña ni backend. Usa trabajador -1001, parque -2001 y nombre «Trabajador DEMO (ficticio)». La sesión vive en memoria durante un máximo de 8 horas; al morir el proceso hay que entrar de nuevo. No hay contraseña maestra ni token ficticio.

La implementación que crea esta sesión está en src/debug/java/.../auth/DemoAccess.java. Release compila otra implementación desde src/release que devuelve available=false y lanza SecurityException si se intenta abrir el modo. SessionManager también rechaza la inyección de una sesión demo en Release. No hay extras de Intent, preferencias ni interruptores remotos que habiliten el modo.

Las rutas demo usan exclusivamente recorridos-demo.db; las reales siguen en recorridos.db. Los ajustes usan los identificadores negativos de la cuenta ficticia. No se copian ni borran rutas reales. Acceso, Home, mapa, resumen y notificación del servicio identifican el contexto de demostración. BLE y GPS usan los dispositivos y posiciones reales: no se generan mediciones simuladas. El modo permite recorrer las pantallas sin REST, pero no demuestra autenticación ni autorización real de llaveros.

La demostración no permite activar biometría, no elimina las credenciales biométricas existentes al salir y no hace DELETE /sesiones/actual. El formulario de acceso real y sus peticiones POST/GET permanecen disponibles con su contrato original.

## Cambios por archivo

| Archivo bajo src/android/app/src | Cambio |
|---|---|
| debug/java/.../auth/DemoAccess.java | Crea la sesión ficticia local, sin token. |
| release/java/.../auth/DemoAccess.java | Bloquea el modo demo en producción. |
| main/java/.../auth/SessionManager.java | Distingue sesiones demo y reales; protege apertura demo por variante. |
| main/java/.../auth/LoginActivity.java | Botón de entrada demo visible solo en Debug. |
| main/java/.../ui/HomeActivity.java | Aviso demo, biometría bloqueada para demo, cierre sin revocación REST. |
| main/java/.../routes/RouteManager.java | Base SQLite separada para demostración. |
| main/java/.../ui/SummaryActivity.java | Aviso demo en estadísticas/resumen. |
| main/java/.../routes/RouteActivity.java | Aviso demo y destrucción de WebView al cerrar. |
| main/java/.../qr/QrScannerActivity.java | Parche adaptado: Google Code Scanner, cancelación/error, manual y Solo GPS. |
| main/java/.../bluetooth/BleScanner.java | Observa anuncios de nombre separados, caché concurrente por MAC, limpia al detener y descarta callbacks inactivos. Se mantiene validación UUID en parser e identidad/tipo en controller. |
| main/java/.../integration/CapturaLlaveroController.java | Cancela callbacks pendientes al detener captura. |
| main/java/.../integration/PreparadorMediciones.java | Rechaza precisión negativa o no finita. |
| main/java/.../routes/RutaService.java | Exige sesión vigente, limita vencimiento, maneja permiso FGS denegado y libera controller antes de fallback GPS; notificación demo. |
| debug/res/xml/red_emulador.xml | includeSubdomains=false explícito, corrige error de lint sin ampliar HTTP. |
| testDebug y testRelease/.../auth/DemoAccessTest.java | Pruebas de sesión demo, contrato real y bloqueo de Release. |
| test/java/.../bluetooth/IBeaconParserTest.java | UUID/formato/longitud, contador, ppb/ppm, tipo e identidad no inventada. |

Los cambios locales en .idea detectados durante el trabajo se han conservado; no se han restaurado ni incluido en un commit.

## Validación ejecutada

Desde src/android, con JAVA_HOME apuntando al JBR de Android Studio:

```
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleRelease :app:testReleaseUnitTest :app:lintDebug --console=plain
```

Resultado final: BUILD SUCCESSFUL, 22 segundos. Debug: 12 pruebas, 0 fallos/errores. Release: 12 pruebas, 0 fallos/errores. Incluyen pruebas que rechazan DemoAccess.open y SessionManager.openDemo en Release. Lint Debug: 0 errores, 62 advertencias (incluye APIs obsoletas, recursos y recomendaciones; no se han ocultado). Release genera APK sin firma de distribución; no se ha instalado Release ni modificado la configuración de firma.

Evidencia: src/android/sena-validacion-final.log; app/build/test-results/testDebugUnitTest y testReleaseUnitTest; app/build/reports/lint-results-debug.html.

APK Debug: src/android/app/build/outputs/apk/debug/app-debug.apk.
SHA256: 0E4E5BEE04486E380B4B6929763738E05DD351AD068F8FFCA227A141B2B8EA00.

git diff --check: PASÓ. XML de app/src parseados correctamente. Búsqueda de marcadores de conflicto: ninguno.

## Matriz de aceptación del paquete

| IDs | Resultado y alcance |
|---|---|
| C01 | PASÓ: compilación Debug. También compilación Release. |
| C02 | PASÓ: pruebas unitarias Debug y Release. |
| C03 | Ver resultado instrumental al final de este informe. |
| C04, C08, C09, C10, C11, C12, C13, C14, C15, C16, C18 | PENDIENTES de comprobación manual del flujo real en Xiaomi; no equivalen a compilación ni a tests de SQLite. |
| C05, C06, C07, C20 | BLOQUEADAS para extremo a extremo real: falta backend HTTPS operativo y cuenta de trabajador válida; biometría requiere esa sesión real. |
| C17 | Pruebas de persistencia/estadísticas: ver resultado instrumental; visualización manual pendiente. |
| C19 | Caducidad real y detención de captura pendientes de escenario controlado en dispositivo. |

## Pasos exactos en el Xiaomi

1. Instalar el APK Debug actualizado sin desinstalar ni borrar datos. Abrir SENDA y pulsar «Entrar en demostración (Debug)». Confirmar las etiquetas ficticias y parque -2001.
2. Seleccionar llavero: probar código manual S-001, código inválido y escaneo del QR S-001. El escáner necesita Google Play Services y puede requerir descargar su módulo. La entrada manual sirve de alternativa.
3. Elegir Solo GPS; activar ubicación y conceder ubicación precisa. Iniciar recorrido, caminar al aire libre, bloquear/desbloquear la pantalla, finalizar y abrir mapa/resumen. Las teselas del mapa requieren Internet; el trayecto se guarda localmente.
4. Repetir con llavero nRF52840 S-001 encendido y permisos de dispositivos cercanos. Debe anunciar UUID 4F5A4F52-412D-4754-492D-33412D42494F, tipo O3=11 y nombre S-001. Confirmar puntos, lecturas en ppb y resumen. Apagar/encender el llavero para verificar pérdida y recuperación. Un nombre BLE no constituye autenticación criptográfica.
5. Probar denegación/revocación de permisos, Bluetooth apagado, ubicación ausente y bloqueo prolongado del Xiaomi. El comportamiento BLE con pantalla apagada y las restricciones de batería del fabricante necesitan prueba física; no se afirma que estén resueltos sin hardware.
6. Cerrar sesión demo y volver a entrar: comprobar persistencia del histórico demo. El proceso de Android puede terminar; reanudar una captura interrumpida desde Ajustes.
7. Para probar acceso real: configurar SENDA_API_BASE con URL HTTPS válida y aportar cuenta de trabajador del backend. Los archivos ServidorRest.php y LogicaNegocio.php del repositorio tienen 0 bytes; no permiten confirmar endpoints ni respuestas. No se inventaron credenciales ni servicios.

No se hizo push, merge, commit, reset, clean, borrado de datos ni cambios en secretos.
## Resultado instrumental en Xiaomi

Comando: `.\gradlew.bat :app:connectedDebugAndroidTest --console=plain`, con ANDROID_SERIAL fijado al Xiaomi conectado. PASÓ: 2 pruebas en Xiaomi 22101320G, Android 14; BUILD SUCCESSFUL en 56 segundos. No se utilizó emulador. Se instaló el APK Debug actualizado como parte de la tarea de pruebas.

La suite existente verifica contexto de aplicación y persistencia SQLite de recorridos, aislamiento por trabajador/parque, restricción de ruta activa, puntos GPS de prueba, lecturas del modelo MedicionAmbiental y agregación de estadísticas. Usa una base temporal de prueba y la elimina al terminar. C03 PASÓ; C17 PASÓ en el alcance de SQLite/agregados. Esto no equivale a haber escaneado QR, recibido BLE ni caminado con GPS real.

Evidencia: src/android/sena-instrumentacion-xiaomi.log y app/build/reports/androidTests/connected/debug/index.html.