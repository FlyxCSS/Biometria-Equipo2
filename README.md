# SENDA · Biometría y Medio Ambiente

## Introducción al proyecto

SENDA es la plataforma del Equipo 2 para recoger y consultar mediciones ambientales de los trabajadores de un parque. El llavero mide ozono y temperatura, Android añade la ubicación y conserva las lecturas, y el servidor las valida y almacena. OZORA es la presentación pública del proyecto; el panel SENDA permite al responsable consultar la última lectura O3 del parque y registrar trabajadores.

## Sprint actual · Sprint 2

El sprint integra acceso, recuperación y biometría (HU-14, HU-21, HU-22), vinculación y recepción BLE (HU-15, HU-16), GPS (HU-17), envío de datos (HU-18), recorridos (HU-19) y estado del llavero (HU-20). El alta de trabajadores (HU-09) está implementada en el panel; su inclusión en la presentación la decide el equipo.

La aplicación usa autenticación real y sesiones cifradas con caducidad. La huella desbloquea una credencial local; no se envían datos biométricos al servidor. La temperatura ya puede persistirse; la última medición del panel web conserva O3 y ppb. La batería se muestra solo si el hardware entrega un porcentaje válido.

## Flujo de datos y conexiones

```text
Medidor + Bateria → Publicador → EmisoraBLE → anuncios iBeacon
                                                   ↓
Android: BleScanner → IBeaconParser → CapturaLlaveroController + GPS
                                       ↓
                   PreparadorMediciones / PreparadorTemperaturas
                                       ↓
                  RutaService → RouteManager (SQLite y cola)
                                       ↓
            MeasurementSync → LogicaFake.java → ApiClient
                                       ↓ HTTPS
              ServidorRest → LogicaNegocio → MySQL/MariaDB
                                       ↑
 Panel web → LogicaFake.js → GET última O3 / POST operaciones de cuenta
```

1. El trabajador entra con su cuenta. El servidor comprueba que un llavero existe, está disponible en su parque y no pertenece a otra persona antes de vincularlo. Desvincular libera la asignación, conservando lecturas.
2. Durante un recorrido, BLE recibe O3, temperatura y batería. El teléfono exige una posición reciente y precisa para guardar las mediciones; puede actualizarla estando quieto. Igual valor de ozono no significa muestra duplicada.
3. SQLite conserva recorrido y cola por usuario/parque. La sincronización se ejecuta fuera de la interfaz y solo marca enviada una lectura tras `guardada=true`. Una pérdida de red mantiene los datos pendientes.
4. El REST valida la petición y resuelve la identidad. El negocio comprueba rangos, unidad, fecha, permisos, propiedad e identidad de lectura; las transacciones y claves de MySQL protegen los datos.
5. La GUI web invoca su proxy `LogicaFake`, nunca la base de datos. La respuesta vuelve por las mismas capas.

«Fake» es el nombre del proxy remoto; no significa respuestas inventadas. El modo de medidas ficticias del firmware es independiente y se selecciona explícitamente.

## Estructura del repositorio

```text
Biometria-Equipo2/
├── doc/
│   ├── android_design.md
│   ├── business_logic_design.md
│   ├── communication_design.md
│   ├── config_design.md
│   ├── database_design.md
│   ├── frontend_business_logic_design.md
│   ├── microprocesador_design.md
│   ├── tests_design.md
│   ├── tools_design.md
│   ├── web_design.md
│   ├── prompts/                 Un xxx_prompt.md por módulo y uso_prompt.md
│   └── materiales/              Guías, contratos, informes, evidencias y referencias
├── src/
│   ├── android/                 App, recursos y pruebas Java
│   ├── business_logic/          Reglas, validación, persistencia y correo
│   ├── communication/           Adaptadores REST y entrada Plesk
│   ├── config/                  Plantilla; configuración efectiva fuera de Git
│   ├── database/                Esquema y migraciones
│   ├── frontend_business_logic/ Proxy JavaScript del negocio
│   ├── microprocesador/         Sketch único y seis cabeceras, incluida batería
│   ├── tests/                   Pruebas PHP/JavaScript y ejemplos
│   ├── tools/                   Administración y desarrollo local
│   ├── web/                     Portada y panel
│   └── router.php               Adaptador del servidor PHP local
├── agents/                     Agentes del equipo; no modificado
├── AUTHORS.md
├── README.md
└── .gitignore
```

La primera capa de `doc` contiene solo los diseños y las carpetas `materiales` y `prompts`. El nombre de cada diseño corresponde exactamente a una carpeta de `src`. Se conserva `AUTHORS.md` como archivo de autores solicitado por el equipo, aunque el agente docente mencione `author.md`. Las licencias de fuentes y el wrapper Gradle se conservan por ser necesarios para distribuir y compilar.

## Diseños generales

| Diseño | Responsabilidad y decisiones principales |
|---|---|
| [android](doc/android_design.md) | Vistas y permisos, sesión/huella, BLE/GPS, cola, recorridos, mapas y estadísticas. Transporte, almacenamiento y captura separados. |
| [microprocesador](doc/microprocesador_design.md) | Adquisición real o ficticia, tipos/unidades de iBeacon y publicación secuencial. Base original Jordi Bataller i Mascarell. |
| [communication](doc/communication_design.md) | Rutas, contenido, autenticación de entrada y traducción de errores. No decide reglas del parque. |
| [business_logic](doc/business_logic_design.md) | Reglas del dominio, autorización y transacciones sobre el catálogo; errores sin códigos de transporte. |
| [database](doc/database_design.md) | Tablas con TABLE, DESCRIPTION, COLUMNS, PRIMARY KEY, FOREIGN KEYS y CONSTRAINTS; roles, usuarios, sensores, medidas y entidades técnicas. |
| [frontend_business_logic](doc/frontend_business_logic_design.md) | Proxy web con el subconjunto de operaciones del negocio. Las firmas lógicas comparten contexto autenticado y datos de dominio. |
| [web](doc/web_design.md) | Portada OZORA, acceso, recuperación, panel y formularios. Consume exclusivamente el proxy. |
| [config](doc/config_design.md) | Plantilla privada y parámetros de entorno, sin secretos versionados. |
| [tools](doc/tools_design.md) | Instalación, migración, correo y utilidades restringidas a CLI/local. |
| [tests](doc/tests_design.md) | Casos de aceptación, límites, errores y aislamiento; distingue pruebas simuladas de integración real. |

Los diseños usan `N`, `Z`, `R`, `B`, `Text`, agregaciones y colecciones. Las flechas distinguen mutación, consulta y operación estática. Los tipos de plataforma se proyectan a adaptadores lógicos. [Inventario de clases y funciones](doc/materiales/informes/revision-funciones.md).

## Despliegue web y backend en Plesk

1. Preparar PHP 8.2 o superior con PDO MySQL, una base MySQL/MariaDB, usuario limitado a esa base y HTTPS. No subir configuraciones privadas a `httpdocs`.
2. Fuera de la raíz pública, crear `senda_s2_privado/` y subir `src/` con sus rutas relativas. Crear `src/config/local.php` a partir de [example.php](src/config/example.php), con credenciales propias, `environment=production`, `app_url=https://TU-DOMINIO/senda`, transporte de correo y remitente autorizados. El proyecto también admite una ruta privada definida mediante `SENDA_CONFIG`.
3. Para una base vacía: `php src/tools/gestionar.php instalar`. Para una instalación existente: comprobar qué migraciones ya tiene y aplicar solo las necesarias; no importar el esquema completo sobre tablas existentes. Temperatura requiere las restricciones de las migraciones 005/006 según el estado anterior. [Guía de temperatura](doc/materiales/guias/temperatura.md).
4. Crear `httpdocs/senda/`. Copiar [plesk-index.php](src/communication/plesk-index.php) como `index.php` y [la regla .htaccess](src/web/.htaccess). El wrapper presupone la carpeta privada anterior; adaptarlo si la disposición del alojamiento cambia.
5. Publicar juntos los adaptadores y el negocio. La separación actual de errores requiere `communication/ContextoWeb.php`, `ServidorRest.php`, `business_logic/Apoyo.php`, `LogicaNegocio.php` y `web/index.php` compatibles. No mezclar versiones de esos archivos.
6. Abrir `/senda/` y `/senda/acceso`. Comprobar que `/senda/api/v1/sesiones/actual` rechaza solicitudes sin credencial. Con una cuenta de pruebas autorizada, comprobar acceso, envío y consulta. No publicar `tools/bbdd`, buzones locales ni archivos de entorno.

[Guía local y operaciones del backend](doc/materiales/guias/backend-puesta-en-marcha.md). Estas instrucciones describen el despliegue; esta reorganización no publica cambios en Plesk automáticamente.

## Despliegue Android

1. Abrir `src/android` en Android Studio, con SDK 36 y JDK compatible con Gradle/AGP (el proyecto se verifica con JBR 21 de Android Studio).
2. Configurar `SENDA_API_BASE=https://TU-DOMINIO/senda/api/v1` mediante propiedades de Gradle del usuario o `-PSENDA_API_BASE=...`. No introducir contraseñas ni tokens en el proyecto.
3. Desde `src/android`, compilar en Windows: `./gradlew.bat :app:assembleDebug`. El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.
4. Conectar el teléfono BLE por USB y seleccionar Run en Android Studio o instalar el APK de desarrollo. No hace falta emulador. Autorizar ubicación precisa, dispositivos cercanos y notificaciones cuando corresponda.
5. Entrar con cuenta real, activar u omitir la huella, vincular el llavero disponible e iniciar un recorrido. El servicio mantiene la captura y ofrece Ver recorrido en la notificación.
6. Para distribución, generar una clave de firma propia fuera de Git y un APK/AAB Release firmado desde Android Studio. `assembleRelease` por sí solo genera actualmente un APK sin firma de distribución.

## Despliegue del sensor

Abrir `src/microprocesador/microprocesador.ino`. Seleccionar SparkFun Pro nRF52840 Mini con el soporte Adafruit nRF52/Bluefruit. Verificar y, cuando el equipo lo decida, cargar por el puerto de la placa.

`USAR_MEDIDA_FAKE=false` conserva la adquisición real existente. Para una prueba explícita puede cambiarse a `true`: Medidor devuelve O3 ficticio de 33 ppb y temperatura ficticia de 23.45 C. La trama actual no marca la simulación; no introducir esas lecturas como observaciones reales del parque. Para ajustar la frecuencia, cambiar el último argumento `3000` de las tres llamadas a `publicarMedida` en `loop`: `1250` por dato aproxima la secuencia a 4 segundos y `10000` a 30 segundos, más la adquisición. No hay temporizador global ni comprobación adicional de intervalo. Batería permanece no disponible sin el divisor externo configurado. No se modifican las constantes de calibración ni el montaje por esta reorganización.

```powershell
arduino-cli compile --fqbn adafruit:nrf52:sparkfunnrf52840mini src/microprocesador
```

## Pruebas y aceptación

Desde la raíz:

```powershell
php src/tests/validacion.php
php src/tests/perfil-llavero.php
php src/tests/historial.php
node --test src/tests/logica-fake.test.cjs
```

Desde `src/android`:

```powershell
./gradlew.bat :app:testDebugUnitTest :app:testReleaseUnitTest
./gradlew.bat :app:assembleDebug :app:assembleRelease :app:assembleDebugAndroidTest
```

`php src/tests/integracion.php` exige un entorno local y una base aislada terminada en `_test`, además de un servidor PHP y PDO MySQL configurados. Comprueba persistencia real y no debe ejecutarse contra producción. Las pruebas instrumentadas requieren un dispositivo seleccionado explícitamente; compilar su APK no certifica su ejecución. BLE, GPS, huella, batería y calibración requieren comprobación física.

Criterios críticos: sin acceso ajeno a cuenta/parque; no vincular un sensor ocupado; conservar lecturas sin red; deduplicar reintentos sin perder temperaturas; rechazar posiciones inválidas; mantener la sesión sin saltarse su caducidad; no transmitir secretos en errores. [Informe de esta reorganización](doc/materiales/informes/reestructuracion-2026-11-19.md).

## Generación guiada con IA

[Guía de uso SDD](doc/prompts/uso_prompt.md): diseños → plan → tareas → implementación y pruebas. Cada módulo dispone de un prompt con entradas, límites, secuencia y aceptación. Se usan en el mismo chat, releyendo los contratos versionados en cada paso. Las [referencias docentes](doc/materiales/referencias/) y los [diseños del proyecto de referencia](https://github.com/FlyxCSS/Sprint-0-Biometria-Fabian/tree/master/doc) sustentan la notación; los comportamientos de Sprint 0 no sustituyen el sistema actual.

Autores y atribuciones: [AUTHORS.md](AUTHORS.md). El código base del firmware mantiene el reconocimiento a Jordi Bataller i Mascarell. Las cabeceras describen responsabilidad y contribución; el histórico Git conserva la autoría individual de cada cambio.
