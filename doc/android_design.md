# Diseño de android

## Diseño del Componente

Aplicación Java de campo: acceso y sesión cifrada, permisos, BLE, GPS, recorridos, estadísticas, perfil y sincronización. La captura se mantiene en un servicio de primer plano; las actividades presentan y coordinan acciones.

### Captura, persistencia y concurrencia

`BleScanner → IBeaconParser → CapturaLlaveroController → PreparadorMediciones / PreparadorTemperaturas → RutaService → RouteManager → MeasurementSync → LogicaFake → ApiClient`.

- O3: ppb enteros. Temperatura: centésimas BLE convertidas a grados al enviar. Batería: porcentaje o no disponible.
- GPS: edad máxima 10 s y precisión máxima 100 m; se solicita incluso sin desplazamiento. No se fabrican posiciones.
- Mayor de iBeacon: tipo en el byte alto y contador en el bajo. El contador vuelve a cero y solo deduplica en una ventana breve; no es una identidad global.
- La cola SQLite se separa por usuario y parque; se marca enviada únicamente con confirmación guardada=true. Temperaturas históricas sin GPS no se envían.
- Las vistas y los callbacks de captura se confinan al hilo principal. La cola usa ejecutor único y guarda atómica; la sesión se publica con visibilidad entre hilos. Los modelos inmutables no exponen setters.
- Cinco secciones: Inicio, Rutas, Historial, Llavero y Ajustes. El acceso real no usa contraseñas maestras ni credenciales de demostración.
- Biometría: clave AndroidKeyStore con autorización biométrica; no transmite huellas. La sesión automática y el token biométrico son distintos almacenes cifrados con caducidad del servidor.
- Perfil: foto JPEG hasta 512 px / 100000 bytes, eliminación mediante valor nulo y confirmaciones antes de quitar o guardar. Nombre visible y teléfono siguen siendo locales.
- Notificación: recorrido en servicio de primer plano con acción Ver recorrido; su visibilidad depende del permiso y canal de Android.
- Los métodos guardarTemperatura y actualizarFotoPerfil son adaptadores: el primero prepara una medición tipada y delega guardarMedicion; el segundo proyecta foto a los datos de perfil.

### leaflet

Implementación: [src/android/app/src/main/assets/map/leaflet.js](../src/android/app/src/main/assets/map/leaflet.js).

```text
                 -------- leaflet --------
                 |
                 |
                 |
t: Dato --> l() -->
      Dato <--
                 |
                 |
                 N() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> a() -->
      Dato <--
                 |
                 |
t: Dato --> h() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> j() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> H() -->
      Dato <--
                 |
                 |
                 u() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> i() -->
      Dato <--
                 |
                 |
t: Dato --> W() -->
      Dato <--
                 |
                 |
t: Dato --> F() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> c() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> U() -->
      Dato <--
                 |
                 |
t: Dato, i: Dato --> q() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> G() -->
      Dato <--
                 |
                 |
t: Dato --> Y() -->
      Dato <--
                 |
                 |
t: Dato --> J() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> x() -->
      Dato <--
                 |
                 |
t: Dato --> r() -->
      Dato <--
                 |
                 |
                 lastId() -->
      Dato <--
                 |
                 |
                 et() -->
      Dato <--
                 |
                 |
                 e() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> p() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> m() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> f() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> _() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> s() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> g() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> v() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> w() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato --> at() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato --> ht() -->
      Dato <--
                 |
                 |
t: Dato --> ct() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> dt() -->
      Dato <--
                 |
                 |
t: Dato --> y() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> Jt() -->
      Dato <--
                 |
                 |
t: Dato --> $t() -->
      Dato <--
                 |
                 |
t: Dato --> Qt() -->
      Dato <--
                 |
                 |
t: Dato --> te() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> ee() -->
      Dato <--
                 |
                 |
t: Dato, i: Dato --> ne() -->
      Dato <--
                 |
                 |
t: Dato --> e() -->
      Dato <--
                 |
                 |
t: Dato --> _e() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> pe() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> P() -->
      Dato <--
                 |
                 |
t: Dato --> T() -->
      Dato <--
                 |
                 |
t: Dato --> me() -->
      Dato <--
                 |
                 |
t: Dato --> fe() -->
      Dato <--
                 |
                 |
t: Dato --> ge() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> ve() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> M() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> z() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> ye() -->
      Dato <--
                 |
                 |
t: Dato --> xe() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> C() -->
      Dato <--
                 |
                 |
t: Dato --> we() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> be() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> Z() -->
      Dato <--
                 |
                 |
t: Dato --> Pe() -->
      Dato <--
                 |
                 |
                 Le() -->
      Dato <--
                 |
                 |
                 Te() -->
      Dato <--
                 |
                 |
t: Dato --> Me() -->
      Dato <--
                 |
                 |
                 ze() -->
      Dato <--
                 |
                 |
t: Dato --> Ce() -->
      Dato <--
                 |
                 |
t: Dato --> Ze() -->
      Dato <--
                 |
                 |
                 disableTextSelection() -->
      Dato <--
                 |
                 |
                 enableTextSelection() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato --> S() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato --> k() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> Se() -->
      Dato <--
                 |
                 |
e: Dato, t: Dato, i: Dato, n: Dato --> ke() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato, o: Dato --> Oe() -->
      Dato <--
                 |
                 |
t: Dato --> Ae() -->
      Dato <--
                 |
                 |
t: Dato --> Be() -->
      Dato <--
                 |
                 |
t: Dato --> Ie() -->
      Dato <--
                 |
                 |
t: Dato --> O() -->
      Dato <--
                 |
                 |
t: Dato --> Re() -->
      Dato <--
                 |
                 |
t: Dato --> Ne() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> De() -->
      Dato <--
                 |
                 |
t: Dato --> He() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> We() -->
      Dato <--
                 |
                 |
t: Dato --> d() -->
      Dato <--
                 |
                 |
t: Dato --> _() -->
      Dato <--
                 |
                 |
t: Dato --> p() -->
      Dato <--
                 |
                 |
t: Dato --> f() -->
      Dato <--
                 |
                 |
t: Dato --> Ue() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> t() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> Je() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> $e() -->
      Dato <--
                 |
                 |
t: Dato --> Qe() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> ei() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> ii() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato, o: Dato --> ni() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato, o: Dato --> oi() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> si() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato --> ri() -->
      Dato <--
                 |
                 |
t: Dato --> I() -->
      Dato <--
                 |
                 |
t: Dato --> ai() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> hi() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> e() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> bi() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato --> Pi() -->
      Dato <--
                 |
                 |
t: Dato --> Li() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato --> Ti() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> Mi() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato, i: Dato, n: Dato --> zi() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> Ci() -->
      Dato <--
                 |
                 |
t: Dato --> Zi() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> Si() -->
      Dato <--
                 |
                 |
t: Dato, e: Dato --> ji() -->
      Dato <--
                 |
                 |
t: Dato --> Ui() -->
      Dato <--
                 |
                 |
t: Dato --> Ki() -->
      Dato <--
                 |
                 --------------------------------------
```

### MainActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/MainActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/MainActivity.java).

```text
                 -------- MainActivity --------
                 | - PAGE_SIZE: Z
                 | - REQUEST_ROUTE_PERMISSIONS: Z
                 | - REQUEST_SENSOR: Z
                 | - REQUEST_MAP_LOCATION: Z
                 | - REQUEST_ROUTE_NOTIFICATION: Z
                 | - REQUEST_SETTINGS_LOCATION: Z
                 | - notificationPermissionAsked: B
                 | - reloj: Dato
                 | - fecha: Dato
                 | - session: Dato
                 | - rutas: Dato
                 | - bio: Dato
                 | - scroll: Dato
                 | - section: Dato
                 | - navigation: Dato
                 | - backToHome: Dato
                 | - tab: Z
                 | - routePage: Z
                 | - historyPage: Z
                 | - routePeriod: Dato
                 | - routeCards: Dato
                 | - routeRangeStart: Z
                 | - routeRangeEnd: Z
                 | - lastPreviewUpdate: Z
                 | - activo: B
                 | - id: Z
                 | - sensorRuta: Text
                 | - ultimoEnvio: Z
                 | - homeMap: Dato
                 | - history: Dato
                 | - heatEnabled: B
                 | - heatWeek: B
                 | - mapPermissionAsked: B
                 | - operacionLlavero: B
                 | - REMOTO: Dato
                 | - tick: Dato
                 |
                 |
                 |
                 run() -->
                 | - onCreate: operación auxiliar privada
                 |
                 |
                 handleOnBackPressed() -->
                 | - onNewIntent: operación auxiliar privada
                 | - onSaveInstanceState: operación auxiliar privada
                 | - onStart: operación auxiliar privada
                 | - onStop: operación auxiliar privada
                 | - onDestroy: operación auxiliar privada
                 | - mostrar: operación auxiliar privada
                 | - ajustarPanelInicio: operación auxiliar privada
                 | - accion: operación auxiliar privada
                 | - visible: operación auxiliar privada
                 | - textoEn: operación auxiliar privada
                 | - mensaje: operación auxiliar privada
                 | - actualizar: operación auxiliar privada
                 | - estadoUbicacion: operación auxiliar privada
                 | - permiso: operación auxiliar privada
                 | - estadoSensor: operación auxiliar privada
                 | - mostrarUltima: operación auxiliar privada
                 | - mostrarNivel: operación auxiliar privada
                 | - horaLectura: operación auxiliar privada
                 | - iniciales: operación auxiliar privada
                 | - fechaLectura: operación auxiliar privada
                 | - nombreSensor: operación auxiliar privada
                 | - cambiarPagina: operación auxiliar privada
                 | - cargarPagina: operación auxiliar privada
                 | - agregarRuta: operación auxiliar privada
                 | - agregarLectura: operación auxiliar privada
                 | - abrirRuta: operación auxiliar privada
                 | - confirmarSalida: operación auxiliar privada
                 | - acceso: operación auxiliar privada
                 | - ajustes: operación auxiliar privada
                 | - elegido: operación auxiliar privada
                 | - seleccionar: operación auxiliar privada
                 | - onActivityResult: operación auxiliar privada
                 | - gestionarLlavero: operación auxiliar privada
                 | - cambiarLlavero: operación auxiliar privada
                 | - guardarLlavero: operación auxiliar privada
                 | - actualizarRemoto: operación auxiliar privada
                 | - abrirNotificaciones: operación auxiliar privada
                 | - cambiarUbicacion: operación auxiliar privada
                 | - abrirAjustesApp: operación auxiliar privada
                 | - mostrarAyuda: operación auxiliar privada
                 | - configurarBiometria: operación auxiliar privada
                 | - configurar: operación auxiliar privada
                 | - ajustarNumero: operación auxiliar privada
                 | - preparar: operación auxiliar privada
                 | - centrarMapa: operación auxiliar privada
                 |
                 |
req: Z, permisos: [Dato], resultados: [Dato] --> onRequestPermissionsResult() -->
                 | - iniciar: operación auxiliar privada
                 | - confirmarFin: operación auxiliar privada
                 | - finalizar: operación auxiliar privada
                 | - salir: operación auxiliar privada
                 |
                 --------------------------------------
```

### SendaApplication

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/SendaApplication.java](../src/android/app/src/main/java/com/example/ozora/senda/SendaApplication.java).

```text
                 -------- SendaApplication --------
                 |
                 |
                 |
                 onCreate() -->
                 |
                 --------------------------------------
```

### AuthScreen

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/AuthScreen.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/AuthScreen.java).

```text
                 -------- AuthScreen --------
                 | - feedback: Dato
                 |
                 | - mostrarFormulario: operación auxiliar privada
                 | - pagina: operación auxiliar privada
                 | - mensaje: operación auxiliar privada
                 | - mostrarEspera: operación auxiliar privada
                 |
                 --------------------------------------
```

### Resultado

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricHelper.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricHelper.java).

```text
                 -------- Resultado --------
                 |
                 |
                 |
token: Text --> aceptar() -->
                 |
                 |
mensaje: Text --> error() -->
                 |
                 --------------------------------------
```

### BiometricHelper

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricHelper.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricHelper.java).

```text
                 -------- BiometricHelper --------
                 | - ALIAS: Text
                 | - activity: Dato
                 | - prefs: Dato
                 | - prompt: Dato
                 | - pendiente: Dato
                 | - tokenPendiente: Text
                 | - datosPendientes: Dato
                 |
                 |
                 |
activity: Pantalla --> BiometricHelper() -->
      BiometricHelper <--
                 |
                 |
r: BiometricPromptAuthenticationResult --> onAuthenticationSucceeded() -->
                 |
                 |
code: Z, msg: Text --> onAuthenticationError() -->
                 |
                 |
                 activo() -->
      B <--
                 |
                 |
                 disponible() -->
      B <--
                 |
                 |
                 enCurso() -->
      B <--
                 | - almacen: operación auxiliar privada
                 |
                 |
                 borrar() -->
                 |
                 |
                 cancelar() -->
                 |
                 |
token: Text, resultado: Resultado --> activar() -->
                 |
                 |
resultado: Resultado --> abrir() -->
                 | - mostrar: operación auxiliar privada
                 | - completar: operación auxiliar privada
                 | - fallar: operación auxiliar privada
                 |
                 --------------------------------------
```

### BiometricSetupActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricSetupActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricSetupActivity.java).

```text
                 -------- BiometricSetupActivity --------
                 | - session: Dato
                 | - bio: Dato
                 | - fromLogin: B
                 |
                 | - onCreate: operación auxiliar privada
                 |
                 |
                 handleOnBackPressed() -->
                 | - onStart: operación auxiliar privada
                 | - render: operación auxiliar privada
                 | - activate: operación auxiliar privada
                 |
                 |
token: Text --> aceptar() -->
                 |
                 |
texto: Text --> error() -->
                 | - feedback: operación auxiliar privada
                 | - leave: operación auxiliar privada
                 | - onDestroy: operación auxiliar privada
                 |
                 --------------------------------------
```

### Login

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/Login.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/Login.java).

```text
                 -------- Login --------
                 | - RED: Dato
                 | - logica: Dato
                 | - bio: Dato
                 | - ocupado: B
                 | - tokenInicial: Text
                 | - venceInicial: Z
                 |
                 | - onCreate: operación auxiliar privada
                 | - abrirPanel: operación auxiliar privada
                 | - onDestroy: operación auxiliar privada
                 | - acceso: operación auxiliar privada
                 |
                 |
token: Text --> aceptar() -->
                 |
                 |
texto: Text --> error() -->
                 | - mensaje: operación auxiliar privada
                 | - entrar: operación auxiliar privada
                 | - autenticar: operación auxiliar privada
                 | - agregarClave: operación auxiliar privada
                 | - primerAcceso: operación auxiliar privada
                 |
                 --------------------------------------
```

### RecoveryActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryActivity.java).

```text
                 -------- RecoveryActivity --------
                 | - model: Dato
                 | - email: Dato
                 | - token: Dato
                 | - next: Dato
                 | - repeat: Dato
                 | - step: Dato
                 |
                 | - onCreate: operación auxiliar privada
                 | - render: operación auxiliar privada
                 | - submit: operación auxiliar privada
                 | - clearSecrets: operación auxiliar privada
                 |
                 --------------------------------------
```

### RecoveryToken

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryToken.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryToken.java).

```text
                 -------- RecoveryToken --------
                 |
                 | - RecoveryToken: operación auxiliar privada
                 |
                 |
input: Text --> extract() --x
      Text <--
                 |
                 --------------------------------------
```

### State

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryViewModel.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryViewModel.java).

```text
                 -------- State --------
                 |
                 | - State: operación auxiliar privada
                 |
                 --------------------------------------
```

### RecoveryViewModel

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryViewModel.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryViewModel.java).

```text
                 -------- RecoveryViewModel --------
                 | - network: Dato
                 |
                 |
                 |
application: Aplicacion --> RecoveryViewModel() -->
      RecoveryViewModel <--
                 | - observe: operación auxiliar privada
                 | - showLink: operación auxiliar privada
                 | - showEmail: operación auxiliar privada
                 | - request: operación auxiliar privada
                 | - reset: operación auxiliar privada
                 | - onCleared: operación auxiliar privada
                 |
                 --------------------------------------
```

### SessionManager

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/auth/SessionManager.java](../src/android/app/src/main/java/com/example/ozora/senda/auth/SessionManager.java).

```text
                 -------- SessionManager --------
                 | - current: Dato
                 | - preferences: Dato
                 | - KEY: Text
                 |
                 | - SessionManager: operación auxiliar privada
                 |
                 |
worker: Worker, expiresAt: Z, token: Text --> open() --x
                 |
                 |
                 current() --x
      SessionManager <--
                 |
                 |
                 clear() --x
                 |
                 |
context: Entorno --> initialize() --x
                 | - cipher: operación auxiliar privada
                 | - guardar: operación auxiliar privada
                 |
                 |
iso: Text --> instante() --x
      Z <--
                 |
                 --------------------------------------
```

### Oyente

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleScanner.java](../src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleScanner.java).

```text
                 -------- Oyente --------
                 |
                 |
                 |
medida: Trama, direccionMac: Text --> alRecibirTrama() -->
                 |
                 |
mensaje: Text --> alOcurrirError() -->
                 |
                 --------------------------------------
```

### BleScanner

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleScanner.java](../src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleScanner.java).

```text
                 -------- BleScanner --------
                 | - contexto: Dato
                 | - oyente: Dato
                 | - escaner: Dato
                 | - escaneando: B
                 | - nombresPorMac: Dato
                 | - callback: Dato
                 |
                 | - onScanResult: operación auxiliar privada
                 | - onScanFailed: operación auxiliar privada
                 |
                 |
contexto: Entorno, oyente: Oyente --> BleScanner() -->
      BleScanner <--
                 |
                 |
                 iniciarEscaneo() -->
      B <--
                 |
                 |
                 detenerEscaneo() -->
                 |
                 |
                 estaEscaneando() -->
      B <--
                 | - tienePermisos: operación auxiliar privada
                 |
                 --------------------------------------
```

### BleTestActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleTestActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleTestActivity.java).

```text
                 -------- BleTestActivity --------
                 | - SOLICITUD_PERMISOS: Z
                 | - escaner: Dato
                 | - textoEstado: Dato
                 | - textoMedida: Dato
                 | - botonIniciar: Dato
                 | - botonDetener: Dato
                 |
                 | - onCreate: operación auxiliar privada
                 | - comprobarEIniciar: operación auxiliar privada
                 | - anadirSiFalta: operación auxiliar privada
                 |
                 |
codigo: Z, permisos: [Dato], resultados: [Dato] --> onRequestPermissionsResult() -->
                 |
                 |
medida: Trama, direccionMac: Text --> alRecibirTrama() -->
                 |
                 |
mensaje: Text --> alOcurrirError() -->
                 | - mostrarEstado: operación auxiliar privada
                 | - detener: operación auxiliar privada
                 | - onStop: operación auxiliar privada
                 |
                 --------------------------------------
```

### EstadoLlavero

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/bluetooth/EstadoLlavero.java](../src/android/app/src/main/java/com/example/ozora/senda/bluetooth/EstadoLlavero.java).

```text
                 -------- EstadoLlavero --------
                 | - sensor: Text
                 | - temperatura: Dato
                 | - bateria: Dato
                 | - instanteTemperatura: Z
                 | - instanteBateria: Z
                 |
                 |
                 |
                 reiniciar() -->
                 |
                 |
trama: Trama, ahora: Z --> actualizar() -->
                 | - vigente: operación auxiliar privada
                 |
                 |
seleccionado: Text, ahora: Z --> temperatura() -->
      R <--
                 |
                 |
seleccionado: Text, ahora: Z --> bateria() -->
      Z <--
                 |
                 --------------------------------------
```

### IBeaconParser

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/bluetooth/IBeaconParser.java](../src/android/app/src/main/java/com/example/ozora/senda/bluetooth/IBeaconParser.java).

```text
                 -------- IBeaconParser --------
                 | - LONGITUD_IBEACON: Z
                 |
                 | - IBeaconParser: operación auxiliar privada
                 |
                 |
datosFabricante: [Dato], nombreBle: Text, rssi: Z --> interpretar() --x
      Resultado <--
                 | - leerUint16: operación auxiliar privada
                 | - construirUuid: operación auxiliar privada
                 |
                 --------------------------------------
```

### Resultado

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/bluetooth/IBeaconParser.java](../src/android/app/src/main/java/com/example/ozora/senda/bluetooth/IBeaconParser.java).

```text
                 -------- Resultado --------
                 |
                 | - Resultado: operación auxiliar privada
                 |
                 |
                 esTemperatura() -->
      B <--
                 |
                 |
                 esBateria() -->
      B <--
                 |
                 |
                 obtenerTemperaturaC() -->
      R <--
                 |
                 |
                 obtenerBateriaPorcentaje() -->
      Z <--
                 |
                 |
                 esOzono() -->
      B <--
                 |
                 |
                 obtenerOzonoPpb() -->
      Z <--
                 |
                 |
                 obtenerOzonoPpm() -->
      R <--
                 |
                 --------------------------------------
```

### Oyente

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/integration/CapturaLlaveroController.java](../src/android/app/src/main/java/com/example/ozora/senda/integration/CapturaLlaveroController.java).

```text
                 -------- Oyente --------
                 |
                 |
                 |
                 alRecibirAnuncio() -->
                 |
                 |
medicion: MedicionTemperatura --> alPrepararTemperatura() -->
                 |
                 |
mensaje: Text --> alCambiarEstado() -->
                 |
                 |
ubicacion: Location --> alRecibirUbicacion() -->
                 |
                 |
medicion: MedicionAmbiental, rssi: Z --> alPrepararMedicion() -->
                 |
                 |
motivo: Text --> alDescartarMedicion() -->
                 |
                 --------------------------------------
```

### CapturaLlaveroController

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/integration/CapturaLlaveroController.java](../src/android/app/src/main/java/com/example/ozora/senda/integration/CapturaLlaveroController.java).

```text
                 -------- CapturaLlaveroController --------
                 | - ble: Dato
                 | - gps: Dato
                 | - oyente: Dato
                 | - principal: Dato
                 | - preparador: Dato
                 | - llavero: Text
                 | - preparadorTemperaturas: Dato
                 | - temperaturaPendiente: Dato
                 | - instanteTemperaturaPendiente: Z
                 | - ultima: Dato
                 | - activo: B
                 | - pendienteUbicacion: Dato
                 | - instantePendienteMs: Z
                 | - MAX_ESPERA_TRAMA_MS: Z
                 |
                 |
                 |
contexto: Entorno, oyente: Oyente --> CapturaLlaveroController() -->
      CapturaLlaveroController <--
                 |
                 |
texto: Text --> seleccionarQr() -->
      B <--
                 |
                 |
                 getLlaveroSeleccionado() -->
      Text <--
                 |
                 |
                 iniciar() -->
      B <--
                 |
                 |
                 detener() -->
                 |
                 |
trama: Trama, mac: Text --> alRecibirTrama() -->
                 |
                 |
ubicacion: Location --> alRecibirUbicacion() -->
                 | - publicarTemperatura: operación auxiliar privada
                 | - publicarSiValida: operación auxiliar privada
                 |
                 |
mensaje: Text --> alOcurrirError() -->
                 | - informar: operación auxiliar privada
                 |
                 --------------------------------------
```

### ControlContadores

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/integration/ControlContadores.java](../src/android/app/src/main/java/com/example/ozora/senda/integration/ControlContadores.java).

```text
                 -------- ControlContadores --------
                 | - ultimoLlavero: Text
                 | - ultimoContador: Z
                 | - ultimoInstante: Z
                 |
                 |
                 |
llavero: Text, contador: Z, ahoraMs: Z --> aceptar() -->
      B <--
                 |
                 |
                 reiniciar() -->
                 |
                 --------------------------------------
```

### ControlLecturasSinContador

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/integration/ControlLecturasSinContador.java](../src/android/app/src/main/java/com/example/ozora/senda/integration/ControlLecturasSinContador.java).

```text
                 -------- ControlLecturasSinContador --------
                 | - sensor: Text
                 | - instante: Z
                 | - secuencia: Z
                 |
                 |
                 |
seleccionado: Text, ahora: Z --> aceptar() -->
      Z <--
                 |
                 |
                 reiniciar() -->
                 |
                 --------------------------------------
```

### LlaveroFlowTestActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/integration/LlaveroFlowTestActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/integration/LlaveroFlowTestActivity.java).

```text
                 -------- LlaveroFlowTestActivity --------
                 | - SOLICITUD: Z
                 | - estado: Dato
                 | - llavero: Dato
                 | - lectura: Dato
                 | - gps: Dato
                 | - controlador: Dato
                 |
                 | - onCreate: operación auxiliar privada
                 | - leerQr: operación auxiliar privada
                 | - solicitarEIniciar: operación auxiliar privada
                 | - siFalta: operación auxiliar privada
                 |
                 |
request: Z, p: [Dato], res: [Dato] --> onRequestPermissionsResult() -->
                 |
                 |
texto: Text --> alCambiarEstado() -->
                 |
                 |
loc: Location --> alRecibirUbicacion() -->
                 |
                 |
m: MedicionAmbiental, rssi: Z --> alPrepararMedicion() -->
                 |
                 |
motivo: Text --> alDescartarMedicion() -->
                 | - detener: operación auxiliar privada
                 | - onStop: operación auxiliar privada
                 |
                 --------------------------------------
```

### MedicionSink

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/integration/MedicionSink.java](../src/android/app/src/main/java/com/example/ozora/senda/integration/MedicionSink.java).

```text
                 -------- MedicionSink --------
                 |
                 |
                 |
medicion: MedicionAmbiental --> enviar() -->
                 |
                 --------------------------------------
```

### PreparadorMediciones

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorMediciones.java](../src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorMediciones.java).

```text
                 -------- PreparadorMediciones --------
                 | - contadores: Dato
                 | - sinContador: Dato
                 |
                 |
                 |
seleccionado: Text, trama: Trama, ubicacion: Location --> preparar() -->
      Resultado <--
                 | - errorUbicacion: operación auxiliar privada
                 |
                 |
                 reiniciar() -->
                 |
                 --------------------------------------
```

### Resultado

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorMediciones.java](../src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorMediciones.java).

```text
                 -------- Resultado --------
                 |
                 | - Resultado: operación auxiliar privada
                 |
                 |
medicion: MedicionAmbiental --> ok() --x
      Resultado <--
                 |
                 |
motivo: Text --> error() --x
      Resultado <--
                 |
                 --------------------------------------
```

### PreparadorTemperaturas

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorTemperaturas.java](../src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorTemperaturas.java).

```text
                 -------- PreparadorTemperaturas --------
                 | - contadores: Dato
                 | - sinContador: Dato
                 |
                 |
                 |
sensor: Text, trama: Trama, ubicacion: Location --> preparar() -->
      MedicionTemperatura <--
                 |
                 |
                 reiniciar() -->
                 |
                 --------------------------------------
```

### Oyente

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/location/LocationTracker.java](../src/android/app/src/main/java/com/example/ozora/senda/location/LocationTracker.java).

```text
                 -------- Oyente --------
                 |
                 |
                 |
ubicacion: Location --> alRecibirUbicacion() -->
                 |
                 |
mensaje: Text --> alOcurrirError() -->
                 |
                 --------------------------------------
```

### LocationTracker

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/location/LocationTracker.java](../src/android/app/src/main/java/com/example/ozora/senda/location/LocationTracker.java).

```text
                 -------- LocationTracker --------
                 | - contexto: Dato
                 | - gestor: Dato
                 | - oyente: Dato
                 | - activo: B
                 |
                 |
                 |
contexto: Entorno, oyente: Oyente --> LocationTracker() -->
      LocationTracker <--
                 |
                 |
                 iniciar() -->
      B <--
                 |
                 |
                 detener() -->
                 |
                 |
ubicacion: Location --> onLocationChanged() -->
                 |
                 |
proveedor: Text --> onProviderDisabled() -->
                 |
                 --------------------------------------
```

### Measurement

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/model/Measurement.java](../src/android/app/src/main/java/com/example/ozora/senda/model/Measurement.java).

```text
                 -------- Measurement --------
                 |
                 |
                 |
idLlavero: Text, valorPpb: Z, latitud: R, longitud: R, precisionMetros: R, fechaIso8601: Text, contador: Z --> Measurement() -->
      Measurement <--
                 |
                 |
                 convertirAPpm() -->
      R <--
                 |
                 |
                 resumen() -->
      Text <--
                 |
                 --------------------------------------
```

### MedicionAmbiental

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/model/MedicionAmbiental.java](../src/android/app/src/main/java/com/example/ozora/senda/model/MedicionAmbiental.java).

```text
                 -------- MedicionAmbiental --------
                 |
                 |
                 |
idLlavero: Text, valorPpb: Z, latitud: R, longitud: R, precisionMetros: R, fechaIso8601: Text, contador: Z --> MedicionAmbiental() -->
      MedicionAmbiental <--
                 |
                 |
                 convertirAPpm() -->
      R <--
                 |
                 |
                 resumen() -->
      Text <--
                 |
                 --------------------------------------
```

### MedicionTemperatura

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/model/MedicionTemperatura.java](../src/android/app/src/main/java/com/example/ozora/senda/model/MedicionTemperatura.java).

```text
                 -------- MedicionTemperatura --------
                 |
                 |
                 |
idLlavero: Text, centesimas: Z, fechaIso8601: Text, latitud: R, longitud: R --> MedicionTemperatura() -->
      MedicionTemperatura <--
                 |
                 --------------------------------------
```

### Route

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/model/Route.java](../src/android/app/src/main/java/com/example/ozora/senda/model/Route.java).

```text
                 -------- Route --------
                 |
                 |
                 |
id: Z, start: Z, end: Z, meters: R, sensor: Text, count: Z, mean: R, minimum: Z, maximum: Z --> Route() -->
      Route <--
                 |
                 --------------------------------------
```

### RoutePoint

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/model/RoutePoint.java](../src/android/app/src/main/java/com/example/ozora/senda/model/RoutePoint.java).

```text
                 -------- RoutePoint --------
                 |
                 |
                 |
latitude: R, longitude: R, segment: Z --> RoutePoint() -->
      RoutePoint <--
                 |
                 --------------------------------------
```

### Worker

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/model/Worker.java](../src/android/app/src/main/java/com/example/ozora/senda/model/Worker.java).

```text
                 -------- Worker --------
                 |
                 |
                 |
id: Z, parkId: Z, name: Text --> Worker() -->
      Worker <--
                 |
                 --------------------------------------
```

### Error

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/network/ApiClient.java](../src/android/app/src/main/java/com/example/ozora/senda/network/ApiClient.java).

```text
                 -------- Error --------
                 |
                 |
                 |
status: Z, msg: Text --> Error() -->
      Error <--
                 |
                 --------------------------------------
```

### ApiClient

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/network/ApiClient.java](../src/android/app/src/main/java/com/example/ozora/senda/network/ApiClient.java).

```text
                 -------- ApiClient --------
                 | - depuracion: B
                 |
                 |
                 |
c: Entorno --> ApiClient() -->
      ApiClient <--
                 | - ApiClient: operación auxiliar privada
                 |
                 |
metodo: Text, ruta: Text, token: Text, datos: Datos --> pedir() -->
      Datos <--
                 | - leer: operación auxiliar privada
                 |
                 --------------------------------------
```

### LogicaFake

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/network/LogicaFake.java](../src/android/app/src/main/java/com/example/ozora/senda/network/LogicaFake.java).

```text
                 -------- LogicaFake --------
                 | - transporte: Dato
                 |
                 |
                 |
contexto: Entorno --> LogicaFake() -->
      LogicaFake <--
                 | - LogicaFake: operación auxiliar privada
                 |
                 |
datos: Datos --> iniciarSesion() -->
      Datos <--
                 |
                 |
token: Text --> consultarSesion() -->
      Datos <--
                 |
                 |
token: Text --> cerrarSesion() -->
      Datos <--
                 |
                 |
token: Text, foto: Text --> actualizarFotoPerfil() -->
      Datos <--
                 |
                 |
token: Text, identificador: Text --> vincularLlavero() -->
      Datos <--
                 |
                 |
token: Text --> desvincularLlavero() -->
      Datos <--
                 |
                 |
token: Text, medicion: MedicionAmbiental --> guardarMedicion() -->
      Datos <--
                 |
                 |
token: Text, medicion: MedicionTemperatura --> guardarTemperatura() -->
      Datos <--
                 | - guardarDatos: operación auxiliar privada
                 | - datosTemperatura: operación auxiliar privada
                 |
                 |
token: Text, datos: Datos --> cambiarContrasena() -->
      Datos <--
                 |
                 |
token: Text, cursor: Text --> historialMediciones() -->
      Datos <--
                 |
                 |
datos: Datos --> solicitarRecuperacion() -->
      Datos <--
                 |
                 |
datos: Datos --> restablecerContrasena() -->
      Datos <--
                 | - datosMedicion: operación auxiliar privada
                 |
                 --------------------------------------
```

### MeasurementSync

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/network/MeasurementSync.java](../src/android/app/src/main/java/com/example/ozora/senda/network/MeasurementSync.java).

```text
                 -------- MeasurementSync --------
                 | - RED: Dato
                 | - EN_CURSO: Dato
                 | - owner: Dato
                 |
                 | - MeasurementSync: operación auxiliar privada
                 |
                 |
                 enCurso() --x
      B <--
                 |
                 |
session: SessionManager --> estado() --x
      Text <--
                 |
                 |
context: Entorno --> reintentar() --x
      B <--
                 | - comenzar: operación auxiliar privada
                 |
                 |
contexto: Entorno --> sincronizar() --x
                 | - enviarPendientes: operación auxiliar privada
                 | - enviarTabla: operación auxiliar privada
                 |
                 --------------------------------------
```

### QrLlaveroParser

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/qr/QrLlaveroParser.java](../src/android/app/src/main/java/com/example/ozora/senda/qr/QrLlaveroParser.java).

```text
                 -------- QrLlaveroParser --------
                 | - FORMATO_ID: Dato
                 |
                 | - QrLlaveroParser: operación auxiliar privada
                 |
                 |
textoQr: Text --> extraerIdentificador() --x
      Text <--
                 |
                 --------------------------------------
```

### QrScannerActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/qr/QrScannerActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/qr/QrScannerActivity.java).

```text
                 -------- QrScannerActivity --------
                 | - scan: Dato
                 | - session: Dato
                 | - manualVisible: B
                 |
                 | - onStart: operación auxiliar privada
                 | - onCreate: operación auxiliar privada
                 |
                 |
s: Text, start: Z, count: Z, after: Z --> beforeTextChanged() -->
                 |
                 |
s: Text, start: Z, before: Z, count: Z --> onTextChanged() -->
                 |
                 |
value: Text --> afterTextChanged() -->
                 | - onSaveInstanceState: operación auxiliar privada
                 | - manual: operación auxiliar privada
                 | - showManual: operación auxiliar privada
                 | - selectManual: operación auxiliar privada
                 | - scan: operación auxiliar privada
                 | - message: operación auxiliar privada
                 | - result: operación auxiliar privada
                 |
                 --------------------------------------
```

### QrTestActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/qr/QrTestActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/qr/QrTestActivity.java).

```text
                 -------- QrTestActivity --------
                 | - textoResultado: Dato
                 | - botonEscanear: Dato
                 |
                 | - onCreate: operación auxiliar privada
                 | - iniciarLecturaQr: operación auxiliar privada
                 | - mostrarIdentificador: operación auxiliar privada
                 |
                 --------------------------------------
```

### RouteActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/routes/RouteActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/routes/RouteActivity.java).

```text
                 -------- RouteActivity --------
                 | - clock: Dato
                 | - tick: Dato
                 | - session: Dato
                 | - manager: Dato
                 | - map: Dato
                 | - id: Z
                 | - silenceSeconds: Z
                 | - header: Dato
                 | - controls: Dato
                 | - signal: Dato
                 | - duration: Dato
                 | - count: Dato
                 | - lastOzone: Dato
                 | - recording: Dato
                 |
                 |
                 |
                 run() -->
                 | - onCreate: operación auxiliar privada
                 | - buildScreen: operación auxiliar privada
                 | - metric: operación auxiliar privada
                 | - refresh: operación auxiliar privada
                 | - confirmFinish: operación auxiliar privada
                 | - showSummary: operación auxiliar privada
                 | - onStart: operación auxiliar privada
                 | - onStop: operación auxiliar privada
                 | - onDestroy: operación auxiliar privada
                 |
                 --------------------------------------
```

### RouteManager

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/routes/RouteManager.java](../src/android/app/src/main/java/com/example/ozora/senda/routes/RouteManager.java).

```text
                 -------- RouteManager --------
                 |
                 |
                 |
context: Entorno --> RouteManager() -->
      RouteManager <--
                 |
                 |
context: Entorno, archivo: Text --> RouteManager() -->
      RouteManager <--
                 |
                 |
db: DepositoLocal --> onCreate() -->
                 |
                 |
db: DepositoLocal, oldVersion: Z, newVersion: Z --> onUpgrade() -->
                 | - crearTemperaturas: operación auxiliar privada
                 |
                 |
ruta: Z, m: MedicionTemperatura --> temperatura() -->
                 |
                 |
worker: Worker, ruta: Z, limite: Z, offset: Z --> temperaturasRuta() -->
      Filas <--
                 |
                 |
usuario: Z, parque: Z, sensor: Text --> iniciar() -->
      Z <--
                 |
                 |
id: Z --> finalizar() -->
                 | - util: operación auxiliar privada
                 |
                 |
id: Z, p: Location, segmento: Z, distancia: R --> punto() -->
                 |
                 |
id: Z, m: MedicionAmbiental --> lectura() -->
                 |
                 |
usuario: Z, parque: Z --> resumen() -->
      Filas <--
                 |
                 |
ruta: Z --> ultima() -->
      Filas <--
                 |
                 |
trabajador: Worker --> estadoEnvios() -->
      Text <--
                 |
                 |
trabajador: Worker --> reintentarEnvios() -->
                 |
                 |
ruta: Z --> puntos() -->
      [Dato] <--
                 |
                 |
worker: Worker, limite: Z, desplazamiento: Z --> listar() -->
      Filas <--
                 |
                 |
worker: Worker, since: Text, until: Text --> puntosCalor() -->
      Filas <--
                 |
                 |
worker: Worker, since: Z, until: Z, limit: Z, offset: Z --> listarPeriodo() -->
      Filas <--
                 |
                 |
worker: Worker, routeId: Z --> vistaPrevia() -->
      [Dato] <--
                 |
                 |
worker: Worker --> ultimaConEnvio() -->
      Filas <--
                 |
                 |
worker: Worker, sensor: Text, limite: Z, desplazamiento: Z --> historial() -->
      Filas <--
                 |
                 |
worker: Worker, since: Text --> lecturasDesde() -->
      Filas <--
                 |
                 |
worker: Worker, id: Z, limite: Z, desplazamiento: Z --> lecturasRuta() -->
      Filas <--
                 |
                 |
worker: Worker, id: Z --> ruta() -->
      Route <--
                 |
                 |
worker: Worker --> ultimaRuta() -->
      Route <--
                 | - leerRuta: operación auxiliar privada
                 |
                 --------------------------------------
```

### RoutePeriod

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/routes/RoutePeriod.java](../src/android/app/src/main/java/com/example/ozora/senda/routes/RoutePeriod.java).

```text
                 -------- RoutePeriod --------
                 |
                 |
                 |
now: Z, zone: TimeZone --> bounds() -->
      [Dato] <--
                 |
                 --------------------------------------
```

### RutaService

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/routes/RutaService.java](../src/android/app/src/main/java/com/example/ozora/senda/routes/RutaService.java).

```text
                 -------- RutaService --------
                 | - previa: Dato
                 | - segmento: Z
                 | - vence: Z
                 | - rutas: Dato
                 | - gps: Dato
                 | - captura: Dato
                 | - handler: Dato
                 |
                 |
                 |
i: Navegacion --> onBind() -->
      IBinder <--
                 |
                 |
i: Navegacion, flags: Z, startId: Z --> onStartCommand() -->
      Z <--
                 |
                 |
context: Entorno, routes: RouteManager, id: Z --> finalizar() --x
                 | - caducada: operación auxiliar privada
                 |
                 |
s: Text --> alCambiarEstado() -->
                 |
                 |
s: Text --> alOcurrirError() -->
                 |
                 |
s: Text --> alDescartarMedicion() -->
                 |
                 |
                 alRecibirAnuncio() -->
                 |
                 |
p: Location --> alRecibirUbicacion() -->
                 |
                 |
m: MedicionAmbiental, rssi: Z --> alPrepararMedicion() -->
                 |
                 |
medicion: MedicionTemperatura --> alPrepararTemperatura() -->
                 | - detenerSensores: operación auxiliar privada
                 |
                 |
                 onDestroy() -->
                 |
                 --------------------------------------
```

### EnvironmentalAssessment

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/statistics/EnvironmentalAssessment.java](../src/android/app/src/main/java/com/example/ozora/senda/statistics/EnvironmentalAssessment.java).

```text
                 -------- EnvironmentalAssessment --------
                 |
                 |
                 |
ppb: Z, threshold: Z --> aviso() --x
      Text <--
                 |
                 --------------------------------------
```

### StatisticsCalculator

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/statistics/StatisticsCalculator.java](../src/android/app/src/main/java/com/example/ozora/senda/statistics/StatisticsCalculator.java).

```text
                 -------- StatisticsCalculator --------
                 |
                 |
                 |
route: Route, now: Z --> resumen() --x
      Text <--
                 |
                 --------------------------------------
```

### AccountActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/AccountActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/AccountActivity.java).

```text
                 -------- AccountActivity --------
                 | - session: Dato
                 | - prefs: Dato
                 | - photo: Text
                 | - busy: B
                 | - picker: Dato
                 | - cameraUri: Dato
                 | - camera: Dato
                 |
                 | - abrirCamara: operación auxiliar privada
                 | - elegirFoto: operación auxiliar privada
                 | - rotacion: operación auxiliar privada
                 | - procesarFoto: operación auxiliar privada
                 | - subirFoto: operación auxiliar privada
                 |
                 |
context: Entorno, session: SessionManager --> preferences() --x
      Preferencias <--
                 | - onCreate: operación auxiliar privada
                 | - onStart: operación auxiliar privada
                 | - onSaveInstanceState: operación auxiliar privada
                 | - showPhoto: operación auxiliar privada
                 |
                 |
view: ImageView, foto: Text --> mostrarFoto() --x
                 | - save: operación auxiliar privada
                 | - security: operación auxiliar privada
                 | - changePassword: operación auxiliar privada
                 |
                 --------------------------------------
```

### BarChartView

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/BarChartView.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/BarChartView.java).

```text
                 -------- BarChartView --------
                 | - bar: Dato
                 | - grid: Dato
                 | - text: Dato
                 | - values: Dato
                 | - labels: Dato
                 | - highlightLast: B
                 | - empty: Text
                 |
                 |
                 |
context: Entorno, attrs: AttributeSet --> BarChartView() -->
      BarChartView <--
                 |
                 |
values: [Dato], labels: [Dato], highlightLast: B, emptyMessage: Text --> setData() -->
                 | - onDraw: operación auxiliar privada
                 |
                 --------------------------------------
```

### FieldScreen

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/FieldScreen.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/FieldScreen.java).

```text
                 -------- FieldScreen --------
                 | - BOSQUE: Z
                 | - TEXTO: Z
                 | - error: Dato
                 | - aviso: Dato
                 |
                 | - aviso: operación auxiliar privada
                 | - cerrarAviso: operación auxiliar privada
                 | - onStop: operación auxiliar privada
                 |
                 |
context: Entorno, bold: B --> inter() --x
      Typeface <--
                 | - dp: operación auxiliar privada
                 | - protegerBarras: operación auxiliar privada
                 | - pagina: operación auxiliar privada
                 | - texto: operación auxiliar privada
                 | - tarjeta: operación auxiliar privada
                 | - boton: operación auxiliar privada
                 | - campo: operación auxiliar privada
                 | - mensajes: operación auxiliar privada
                 | - mensaje: operación auxiliar privada
                 |
                 --------------------------------------
```

### HistoryScreen

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/HistoryScreen.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/HistoryScreen.java).

```text
                 -------- HistoryScreen --------
                 | - IO: Dato
                 | - activity: Dato
                 | - root: Dato
                 | - session: Dato
                 | - cursors: Dato
                 | - page: Z
                 | - generation: Z
                 | - chartGeneration: Z
                 | - period: Z
                 | - remote: B
                 | - closed: B
                 | - busy: B
                 | - temperature: B
                 | - next: Text
                 |
                 |
                 |
activity: Pantalla, root: Vista, session: SessionManager --> HistoryScreen() -->
      HistoryScreen <--
                 |
                 |
                 close() -->
                 | - chart: operación auxiliar privada
                 | - reset: operación auxiliar privada
                 | - text: operación auxiliar privada
                 | - valid: operación auxiliar privada
                 | - load: operación auxiliar privada
                 | - finishLoading: operación auxiliar privada
                 | - dp: operación auxiliar privada
                 | - card: operación auxiliar privada
                 | - label: operación auxiliar privada
                 |
                 --------------------------------------
```

### Reading

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/HistoryScreen.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/HistoryScreen.java).

```text
                 -------- Reading --------
                 |
                 | - Reading: operación auxiliar privada
                 |
                 --------------------------------------
```

### HomeMapController

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/HomeMapController.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/HomeMapController.java).

```text
                 -------- HomeMapController --------
                 | - activity: Dato
                 | - root: Dato
                 | - web: Dato
                 | - location: Dato
                 | - session: Dato
                 | - reader: Dato
                 | - ready: B
                 | - disposed: B
                 | - enabled: B
                 | - week: B
                 | - foreground: B
                 | - mapUnavailable: B
                 | - last: Dato
                 | - points: Dato
                 | - min: Z
                 | - max: Z
                 | - lastRefresh: Z
                 | - temperature: B
                 |
                 |
                 |
activity: Pantalla, root: Vista, session: SessionManager, enabled: B, week: B --> HomeMapController() -->
      HomeMapController <--
                 |
                 |
activity: Pantalla, root: Vista, session: SessionManager, enabled: B, week: B, backdrop: B --> HomeMapController() -->
      HomeMapController <--
                 |
                 |
view: MapaVista, url: Text --> onPageFinished() -->
                 |
                 |
view: MapaVista, request: WebResourceRequest, error: WebResourceError --> onReceivedError() -->
                 |
                 |
view: MapaVista, request: WebResourceRequest --> shouldOverrideUrlLoading() -->
      B <--
                 | - updateMapInset: operación auxiliar privada
                 | - mapHtml: operación auxiliar privada
                 | - asset: operación auxiliar privada
                 |
                 |
                 heatEnabled() -->
      B <--
                 |
                 |
                 weekSelected() -->
      B <--
                 |
                 |
                 start() -->
                 |
                 |
                 stop() -->
                 |
                 |
                 destroy() -->
                 |
                 |
                 recenter() -->
                 |
                 |
position: Location --> alRecibirUbicacion() -->
                 |
                 |
error: Text --> alOcurrirError() -->
                 | - position: operación auxiliar privada
                 | - updateControls: operación auxiliar privada
                 |
                 |
force: B --> refresh() -->
                 | - render: operación auxiliar privada
                 | - showStatus: operación auxiliar privada
                 | - status: operación auxiliar privada
                 |
                 --------------------------------------
```

### KeyringScreen

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/KeyringScreen.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/KeyringScreen.java).

```text
                 -------- KeyringScreen --------
                 | - root: Dato
                 | - context: Dato
                 |
                 |
                 |
root: Vista --> KeyringScreen() -->
      KeyringScreen <--
                 | - text: operación auxiliar privada
                 | - color: operación auxiliar privada
                 |
                 |
session: SessionManager, routes: RouteManager, prefs: Preferencias, sensor: Text, active: B, receiving: B, state: Text --> refresh() -->
                 | - cuando: operación auxiliar privada
                 |
                 --------------------------------------
```

### KeyringStatus

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/KeyringStatus.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/KeyringStatus.java).

```text
                 -------- KeyringStatus --------
                 |
                 | - KeyringStatus: operación auxiliar privada
                 |
                 |
active: B, route: Z, captureRoute: Z, lastAdvertisement: Z, now: Z, timeoutSeconds: Z --> receiving() --x
      B <--
                 |
                 --------------------------------------
```

### PermissionsActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/PermissionsActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/PermissionsActivity.java).

```text
                 -------- PermissionsActivity --------
                 | - notifications: B
                 | - session: Dato
                 |
                 | - onCreate: operación auxiliar privada
                 | - onResume: operación auxiliar privada
                 | - granted: operación auxiliar privada
                 | - gps: operación auxiliar privada
                 | - text: operación auxiliar privada
                 | - refresh: operación auxiliar privada
                 | - enable: operación auxiliar privada
                 | - system: operación auxiliar privada
                 | - open: operación auxiliar privada
                 |
                 |
code: Z, permissions: [Dato], results: [Dato] --> onRequestPermissionsResult() -->
                 |
                 --------------------------------------
```

### RouteCardView

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/RouteCardView.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/RouteCardView.java).

```text
                 -------- RouteCardView --------
                 | - routeId: Z
                 | - start: Z
                 |
                 |
                 |
context: Entorno --> RouteCardView() -->
      RouteCardView <--
                 |
                 |
id: Z, start: Z, end: Z, meters: R, count: Z, points: [Dato] --> bind() -->
                 |
                 |
points: [Dato] --> setPoints() -->
                 |
                 |
route: Route, now: Z --> update() -->
                 | - update: operación auxiliar privada
                 |
                 --------------------------------------
```

### RouteMap

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/RouteMap.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/RouteMap.java).

```text
                 -------- RouteMap --------
                 | - web: Dato
                 | - status: Dato
                 | - ready: B
                 | - disposed: B
                 | - hasPoints: B
                 | - offline: B
                 | - draw: Text
                 | - active: B
                 |
                 |
                 |
activity: Pantalla, web: MapaVista, status: Text, active: B --> RouteMap() -->
      RouteMap <--
                 |
                 |
view: MapaVista, url: Text --> onPageFinished() -->
                 |
                 |
view: MapaVista, request: WebResourceRequest --> shouldOverrideUrlLoading() -->
      B <--
                 |
                 |
points: [Dato], readings: [Dato], top: Z, bottom: Z --> update() -->
                 | - render: operación auxiliar privada
                 |
                 |
                 checkTiles() -->
                 | - showStatus: operación auxiliar privada
                 |
                 |
                 destroy() -->
                 |
                 --------------------------------------
```

### RoutePreviewView

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/RoutePreviewView.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/RoutePreviewView.java).

```text
                 -------- RoutePreviewView --------
                 | - paint: Dato
                 | - points: Dato
                 | - bitmapPaint: Dato
                 | - texture: Dato
                 | - seed: Z
                 |
                 |
                 |
context: Entorno, attrs: AttributeSet --> RoutePreviewView() -->
      RoutePreviewView <--
                 |
                 |
value: Z --> setSeed() -->
                 |
                 |
route: [Dato] --> setPoints() -->
                 | - onDraw: operación auxiliar privada
                 |
                 --------------------------------------
```

### RouteScreen

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/RouteScreen.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/RouteScreen.java).

```text
                 -------- RouteScreen --------
                 |
                 | - column: operación auxiliar privada
                 | - label: operación auxiliar privada
                 | - backButton: operación auxiliar privada
                 | - card: operación auxiliar privada
                 | - valueWithUnit: operación auxiliar privada
                 | - chip: operación auxiliar privada
                 | - addWithTopMargin: operación auxiliar privada
                 |
                 --------------------------------------
```

### SummaryActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/SummaryActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/SummaryActivity.java).

```text
                 -------- SummaryActivity --------
                 | - PREVIEW: Z
                 | - PAGE: Z
                 | - time: Dato
                 | - session: Dato
                 | - manager: Dato
                 | - route: Dato
                 | - map: Dato
                 | - rows: Dato
                 | - all: Dato
                 | - more: Dato
                 | - average: Dato
                 | - maximum: Dato
                 | - minimum: Dato
                 | - temperature: B
                 | - expanded: B
                 | - shown: Z
                 | - temperatureCount: Z
                 |
                 | - onCreate: operación auxiliar privada
                 | - buildScreen: operación auxiliar privada
                 | - filter: operación auxiliar privada
                 | - textButton: operación auxiliar privada
                 | - stat: operación auxiliar privada
                 | - readings: operación auxiliar privada
                 | - refreshReadings: operación auxiliar privada
                 | - appendRows: operación auxiliar privada
                 | - refreshMap: operación auxiliar privada
                 | - value: operación auxiliar privada
                 | - readingTime: operación auxiliar privada
                 | - onStart: operación auxiliar privada
                 | - onSaveInstanceState: operación auxiliar privada
                 | - onDestroy: operación auxiliar privada
                 |
                 --------------------------------------
```

### WelcomeActivity

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/WelcomeActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/WelcomeActivity.java).

```text
                 -------- WelcomeActivity --------
                 | - navigated: B
                 | - animator: Dato
                 | - intro: Dato
                 |
                 | - onCreate: operación auxiliar privada
                 | - playIntro: operación auxiliar privada
                 |
                 |
a: androidanimationAnimator --> onAnimationEnd() -->
                 | - finishIntro: operación auxiliar privada
                 | - enter: operación auxiliar privada
                 | - onPause: operación auxiliar privada
                 | - onResume: operación auxiliar privada
                 | - onDestroy: operación auxiliar privada
                 |
                 --------------------------------------
```

### Intro

Implementación: [src/android/app/src/main/java/com/example/ozora/senda/ui/WelcomeActivity.java](../src/android/app/src/main/java/com/example/ozora/senda/ui/WelcomeActivity.java).

```text
                 -------- Intro --------
                 |
                 | - Intro: operación auxiliar privada
                 | - onDraw: operación auxiliar privada
                 |
                 --------------------------------------
```

### Diccionario de tipos auxiliares

Dato = Text | Z | R | B | Nulo | [Dato]. Datos = [(clave:Text, valor:Dato)]. Los tipos de dominio detallados en el contrato conservan esa estructura.

- ApiClient: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Aplicacion: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- AttributeSet: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- BarChartView: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- BiometricHelper: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- BiometricPromptAuthenticationResult: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- BleScanner: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- CapturaLlaveroController: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- DepositoLocal: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Entorno: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Error: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Filas: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- HistoryScreen: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- HomeMapController: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- IBinder: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- ImageView: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Inicio: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Java: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- KeyringScreen: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Location: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- LocationTracker: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- LogicaFake: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- MapaVista: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Measurement: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- MedicionAmbiental: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- MedicionTemperatura: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Navegacion: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Oyente: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Pantalla: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Preferencias: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- RecoveryViewModel: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Resultado: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Route: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- RouteCardView: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- RouteManager: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- RouteMap: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- RoutePoint: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- RoutePreviewView: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- SessionManager: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- TimeZone: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Trama: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Typeface: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Vista: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- WebResourceError: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- WebResourceRequest: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- Worker: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.

## Aclaraciones del Diseño

- Se describe el código actual del Sprint 2; el ejemplo del Sprint 0 se usa para notación, no para copiar comportamientos obsoletos.
- N, Z, R, B y Text son naturales, enteros, reales, booleanos y texto. [T] es una colección; Nulo indica ausencia.
- Los tipos de plataforma citados en las fichas se consideran adaptadores opacos; no autorizan dependencias entre capas.
- Las operaciones auxiliares y callbacks completos se trazan en [el inventario](materiales/informes/revision-funciones.md).

## Reglas Generales

- Lenguaje objetivo: Java 11, XML y JavaScript para los mapas embebidos.
- Cada función o método lleva diseño lógico y descripción en un bloque delimitado por líneas discontinuas.
- + representa interfaz pública y - implementación privada; --> muta o produce efectos, <-- consulta, --x operación estática.
- Código legible, responsabilidad única y auxiliares privados mínimos. No inventar getters ni nuevas operaciones públicas.
- Proteger el estado mutable por confinamiento, transacciones o sincronización explícita según el componente.
- Generar y ejecutar pruebas automatizadas de operaciones críticas, errores, límites, aislamiento y concurrencia cuando el entorno lo permita. No acceder a campos privados para afirmar resultados.
- No introducir secretos, autenticación maestra, respuestas ficticias de éxito, commits ni despliegues automáticos.
