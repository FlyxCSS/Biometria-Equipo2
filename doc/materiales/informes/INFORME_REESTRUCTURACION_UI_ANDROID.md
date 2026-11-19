# Reestructuración de la interfaz Android de SENDA

Fecha 09/11/2026. Rama local `develop`, base `0439681`. El árbol de trabajo estaba limpio al empezar.

## Navegación

```text
LoginActivity
└─ MainActivity (activity_main.xml)
   ├─ Inicio → estado de campo y acceso a Rutas
   ├─ Rutas → recorrido activo y listado de recorridos guardados
   │  └─ Resumen de la ruta seleccionada → Mapa
   ├─ Historial → mediciones locales en orden cronológico inverso
   ├─ Sensor → selección por Google Code Scanner, código manual o Solo GPS
   └─ Más → ajustes, permisos del sistema, sincronización y cierre de sesión
```

La barra Material inferior pertenece al host. Cada pestaña tiene su propio layout XML. Se conserva la pestaña y la página de lista al recrear la actividad. Atrás desde otra pestaña vuelve a Inicio. QR, mapa y resumen son actividades de tarea y regresan a la pestaña de origen. El recorrido activo es un panel de Rutas, con duración, medición, resumen, mapa y acciones para finalizar o reanudar cuando la captura está interrumpida. La notificación apunta a Rutas y reutiliza el host. Cambiar de pestaña solo sustituye la vista y no vuelve a iniciar el servicio.

Las listas muestran 20 elementos por página. Historial usa fecha y valor reales, y permite refrescar las lecturas locales. Las consultas de recorridos, detalle e historial filtran por trabajador y parque. Se conservan el esquema SQLite y la separación entre la base real y la de demostración.

## Archivos

Rutas Java relativas a `src/android/app/src/main/java/com/example/ozora/senda/`.

| Archivo | Cambio |
| --- | --- |
| `MainActivity.java` | Nuevo host en el paquete raíz. Conserva la coordinación de sesión, captura, permisos, ajustes y sincronización del panel anterior. |
| `ui/HomeActivity.java` | Sustituido por el host. Se retiró la entrada anterior del manifiesto. |
| `auth/LoginActivity.java` | Abre Main después del acceso real o Demo Debug. Conserva la lógica de autenticación y cambio de contraseña inicial. |
| `routes/RutaService.java` | Actualiza el destino de la notificación. Mantiene captura BLE/GPS y sincronización. |
| `routes/RouteManager.java` | Añade consultas de solo lectura para listados, historial y detalle. |
| `routes/RouteActivity.java`, `ui/SummaryActivity.java` | Abren el recorrido seleccionado con su identificador y la sesión vigente. |
| `qr/QrScannerActivity.java` | Comprueba sesión al entrar y volver. Conserva lectura por Google, validación y entrada manual. |
| `ui/FieldScreen.java` | Centraliza el tratamiento de barras del sistema, recortes y teclado. |
| `AndroidManifest.xml` | Registra Main como actividad privada. Login sigue como launcher. Login y QR usan `adjustResize`. |
| `res/layout/activity_main.xml`, `res/layout/section_*.xml` | Contenedor y cinco layouts separados con IDs para adaptar las vistas a Figma. |
| `res/menu/main_navigation.xml`, `res/values/{strings_main,styles_main,colors}.xml` | Destinos, etiquetas, estilos y superficie compartida. |
| `src/android/app/src/androidTest/.../RecorridoTest.java` | Regresión para paginación, aislamiento por trabajador/parque y lecturas por sensor. |

## Barras y permisos

`EdgeToEdge` configura iconos de contraste para el fondo claro. El listener aplica una sola vez los insets de barras, recorte e IME al contenedor. El área desplazable ocupa el espacio que deja la barra inferior, con esta última por encima de la navegación del sistema. Login, QR y pantallas secundarias comparten la protección de insets.

La app solicita ubicación precisa antes de comenzar y, desde Android 12, dispositivos cercanos cuando se usa llavero. Más muestra el estado de los permisos y abre los ajustes del sistema. Se mantienen los permisos del servicio de ubicación. Demo sigue limitado a Debug y el bloqueo en Release está cubierto por los tests de esa variante.

## Comprobaciones

JBR de Android Studio y Gradle Wrapper desde `src/android`.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleRelease :app:testReleaseUnitTest :app:lintDebug :app:assembleDebugAndroidTest --console=plain
```

| Tarea | Resultado |
| --- | --- |
| `assembleDebug` | Correcto. APK en `app/build/outputs/apk/debug/app-debug.apk`. |
| `assembleRelease` | Correcto. APK sin firmar en `app/build/outputs/apk/release/app-release-unsigned.apk`. |
| `testDebugUnitTest` | 15 aprobadas, 1 omitida, 0 fallos. |
| `testReleaseUnitTest` | 15 aprobadas, 1 omitida, 0 fallos. |
| `lintDebug` | 0 errores y 63 avisos. Incluye un aviso de posible repintado del fondo en el contenedor y avisos de recursos, textos y APIs previas. |
| `assembleDebugAndroidTest` | APK instrumental compilado. |
| `connectedDebugAndroidTest` | Pendiente. ADB no detectó un teléfono conectado. |

La prueba HTTP opcional se omitió en ambas variantes porque requiere un backend de pruebas local con MySQL y la fixture `SENDA_CONTRACT_FIXTURE`. No se usó el servidor de producción para poblar datos de prueba. Gradle terminó con `BUILD SUCCESSFUL in 33s`. El log está en `src/android/build/senda-ui-validacion.log`. Los XML nuevos y el manifiesto se analizaron correctamente. `git diff --check` pasó.

## Comprobación en el móvil

1. Instalar Debug como actualización. Abrir Login, mostrar y ocultar el teclado y entrar en Demo. Probar el acceso real con una cuenta habilitada cuando esté disponible.
2. Abrir las cinco pestañas, girar el teléfono y comprobar que se conserva la selección. Verificar que la hora, el recorte de cámara y la barra de gestos no cubren la interfaz. Repetir con navegación de tres botones y tamaño de fuente grande.
3. En Sensor, probar QR válido, código inválido y Solo GPS. Con un llavero físico, iniciar un recorrido y verificar estado y última lectura en Inicio, Rutas y Sensor.
4. Alternar pestañas y bloquear/desbloquear el móvil durante la captura. Verificar que continúa el recorrido. Probar también la notificación del servicio y reanudar desde Rutas si Android interrumpe la captura.
5. Finalizar el recorrido. Abrir un recorrido anterior en Rutas y verificar que su resumen y mapa corresponden a ese registro. Revisar el historial con y sin mediciones y comprobar el cierre de sesión desde Más.

La apariencia, el comportamiento del teclado en teléfonos con recorte y el flujo físico BLE/GPS quedan pendientes de estas pruebas. Por ello no se adjuntan capturas de ejecución.

## Git

`git status --short --branch` muestra `develop...origin/develop` con cambios locales en Android y este informe. Sin commit, push ni merge. No se inició ningún emulador ni se modificaron archivos de configuración del IDE.
