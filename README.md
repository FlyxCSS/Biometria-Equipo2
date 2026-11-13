# SENDA · Biometría y Medio Ambiente

SENDA es la plataforma de monitorización ambiental para parques naturales del Equipo 2 de la UPV. El personal de campo recoge lecturas de ozono (O₃) durante sus recorridos con un llavero Bluetooth y una aplicación Android. Los responsables del parque pueden consultar los datos geolocalizados desde un panel web. OZORA es la marca bajo la que se presenta SENDA en la web pública.

## Sprint 2 · Integración

El Sprint 0 probó la captura BLE con una medida ficticia. En el Sprint 1 se definieron el producto, sus usuarios y la arquitectura. El Sprint 2 se centra en integrar el recorrido de campo completo, desde el acceso en Android hasta la consulta de una medición en la web. Para las pruebas, el firmware emite una lectura ficticia de 2 ppb con el identificador `S-001`.

| Historias previstas | Alcance |
| --- | --- |
| HU-14, HU-21 y HU-22 | Iniciar sesión, recuperar el acceso a una cuenta y acceder mediante la biometría de Android tras un primer inicio con credenciales. |
| HU-15, HU-16 y HU-17 | Vincular el llavero mediante QR, recibir sus mediciones por Bluetooth y asociarlas a una ubicación GPS válida. |
| HU-19 y HU-20 | Iniciar y finalizar un recorrido, consultar el estado del llavero y ver la última medición recibida en Android. |
| HU-18 | Enviar mediciones al servidor para que puedan consultarse desde la web. |

**Pendiente de confirmar:** HU-09, registrar trabajadores desde el panel del jefe de parque.

## Estructura del proyecto

```text
Biometria-Equipo2/
├── README.md
├── AUTHORS.md
├── doc/                      Documentación, contratos y evidencias
└── src/
    ├── microprocesador/       Firmware Arduino y anuncios iBeacon
    ├── android/              Aplicación de campo y sus pruebas
    ├── communication/        API REST y entrada pública para Plesk
    ├── business_logic/       Validación y operaciones del backend
    ├── database/             Esquema SQL y migraciones
    ├── frontend_business_logic/  Código JavaScript de apoyo
    ├── web/                  Portada OZORA y panel SENDA
    ├── config/               Plantilla de configuración privada
    ├── tools/                Herramientas de instalación y desarrollo
    ├── tests/                Pruebas del backend y ejemplos HTTP
    └── router.php            Entrada del servidor PHP local
```

Los diseños de los componentes se organizarán en [doc/](doc/) siguiendo los módulos de `src/`. Git utiliza `.gitignore` para excluir credenciales y archivos locales.

## Flujo del proyecto

```text
Llavero SparkFun → anuncio BLE / iBeacon → Android + GPS
                                              │
                                   SQLite y cola de envío
                                              │
                                   POST /api/v1/mediciones
                                              ▼
                              Comunicación REST → Lógica de negocio → MySQL
                                              ▲
                      Panel web → consulta de la última medición del parque
```

1. El trabajador inicia sesión en Android y selecciona el llavero mediante el QR o su código. La selección local identifica el dispositivo cuyos anuncios escuchará la app. El servidor comprueba la asignación del llavero cuando recibe una medición.
2. Al iniciar el recorrido, Android escucha los anuncios BLE del llavero y asocia cada lectura válida de ozono a una posición GPS del teléfono. Durante la captura, el trabajador puede consultar el estado del llavero y la última lectura recibida.
3. La app guarda el recorrido y las mediciones en SQLite. Con una sesión real, la cola de sincronización envía las lecturas por HTTPS a la API. El servidor valida los datos y almacena en MySQL las lecturas aceptadas junto con su parque.
4. El trabajador finaliza el recorrido y consulta su resumen en Android. En el panel web, el responsable del parque consulta la última lectura almacenada en el servidor.

El modo de demostración de Android está disponible en Debug y guarda sus recorridos localmente. Los mapas de ejemplo de la portada usan datos ilustrativos. [Contrato REST](doc/contracts/backend-android-v1.md).

## Despliegue

### Web y backend en Plesk

1. En Plesk, prepara PHP 8.2 o superior con `pdo_mysql` y `mbstring`, HTTPS y una base MySQL o MariaDB con un usuario propio.
2. Fuera de `httpdocs`, crea `senda_s2_privado/` y sube la carpeta `src/`. Copia `src/config/example.php` a `src/config/local.php` dentro de esa carpeta privada. Configura el DSN, el usuario, la contraseña, `environment=production` y `app_url=https://TU-DOMINIO/senda`.
3. Para una base vacía, ejecuta `php src/tools/gestionar.php instalar` desde `senda_s2_privado/`. Si la base ya existe, revisa las migraciones que le correspondan antes de aplicar cambios. [Guía del backend](doc/backend-puesta-en-marcha.md).
4. Crea `httpdocs/senda/`. Copia allí [plesk-index.php](src/communication/plesk-index.php) con el nombre `index.php` y [el archivo .htaccess](src/web/.htaccess). El controlador sirve los recursos desde la copia privada y la regla de Apache conserva la cabecera `Authorization`.
5. Abre `https://TU-DOMINIO/senda/` y `https://TU-DOMINIO/senda/acceso`. La base HTTPS de la API para Android es `https://TU-DOMINIO/senda/api/v1`.

### Aplicación Android

1. Abre [src/android/](src/android/) en Android Studio. Usa un JDK 17 o superior y el SDK de Android 36. Sincroniza Gradle.
2. Configura `SENDA_API_BASE=https://TU-DOMINIO/senda/api/v1` en las propiedades de Gradle de tu usuario si quieres apuntar al despliegue correspondiente.
3. Conecta un teléfono Android compatible con BLE mediante USB con depuración habilitada. Selecciónalo en Android Studio y ejecuta la variante **Debug** con **Run**.
4. Concede ubicación precisa y, al usar el llavero, los permisos de Bluetooth que solicite el sistema. Entra con una cuenta del backend para probar la sincronización, o pulsa **Entrar en demostración (Debug)** para probar la captura y los recorridos locales.

### Firmware con Arduino IDE

1. Abre [microprocesador.ino](src/microprocesador/microprocesador.ino) en Arduino IDE, dentro de su carpeta con los archivos `.h`.
2. Instala el soporte nRF52 compatible con la **SparkFun Pro nRF52840 Mini** y Bluefruit. Selecciona el modelo de placa y el puerto USB.
3. Pulsa **Verificar** y luego **Subir**. En el teléfono, selecciona `S-001` y comprueba que la app recibe el anuncio iBeacon al iniciar un recorrido.

## Pruebas

| Componente | Cómo ejecutarlas desde la raíz |
| --- | --- |
| Backend PHP | `php src/tests/validacion.php` |
| Integración HTTP y MySQL | `php src/tests/integracion.php`, con el entorno local y una base de pruebas configurados según la [guía](doc/backend-puesta-en-marcha.md) |
| Android | Desde `src/android/`, ejecuta `./gradlew.bat :app:testDebugUnitTest` en Windows o `./gradlew :app:testDebugUnitTest` en Linux o macOS |
| Firmware | **Verificar** en Arduino IDE ejecuta las comprobaciones de compilación del sketch |

Consulta las [evidencias del backend](doc/evidencia-backend.md) y el [informe de validación Android](doc/INFORME_VALIDACION_ANDROID.md). Los integrantes del equipo figuran en [AUTHORS.md](AUTHORS.md).
