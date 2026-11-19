# Prompt SDD · android

## Objetivo y alcance

Implementa de forma completa `src/android/` a partir de `doc/android_design.md`. Responsabilidad: Aplicación Java de campo: acceso y sesión cifrada, permisos, BLE, GPS, recorridos, estadísticas, perfil y sincronización. La captura se mantiene en un servicio de primer plano; las actividades presentan y coordinan acciones.. Este prompt se envía en el mismo chat que los otros módulos, pero cada contrato debe poder verificarse en archivos.

## Entradas normativas

- Lee `doc/android_design.md` y `doc/materiales/referencias/plantilla-sdd.md`.
- Consulta `doc/communication_design.md` para sus contratos y límites.
- Consulta `doc/business_logic_design.md` para sus contratos y límites.
- Consulta `doc/database_design.md` para sus contratos y límites.
- Consulta `doc/microprocesador_design.md` para sus contratos y límites.

## /specify · Criterios de aceptación

Conservar Login en auth, MainActivity raíz y cinco secciones. Preservar sesión cifrada, huella opcional, QR Google/manual, asignación confirmada, notificación y permisos, O3/temperatura/GPS, cola aislada, mapas y estadísticas. Separar red (LogicaFake, ApiClient, MeasurementSync), SQLite (RouteManager), BLE y vistas. Avisos únicos 3 s, perfil con borrado y confirmaciones. Mantener assets y licencias.

## /plan · Estrategia

Lee el código existente y el inventario de funciones. Expón dependencias, invariantes, archivos afectados y pruebas antes de editar. Si falta una especificación, registra la carencia y solicita la decisión mínima necesaria; no la sustituyas por un supuesto oculto. Para una carpeta vacía, crea toda su estructura conforme al diseño y a los recursos suministrados.

## /tasks · Secuencia de trabajo

1. Contrastar tipos y firmas con módulos ya generados; registrar la adaptación de contexto autenticado entre cliente y servidor.
2. Preparar clases/modelos y dependencias internas; no mover reglas de negocio a vistas ni a transporte.
3. Implementar flujos normales y errores; preservar restricciones y compatibilidad de la implementación previa.
4. Preparar pruebas de comportamiento público, límites, aislamiento y sincronización.
5. Revisar correspondencia diseño/código y referencias entre carpetas.

## /implement · Validación y entrega

Compilar assembleDebug, assembleRelease y assembleDebugAndroidTest; ejecutar testDebugUnitTest y testReleaseUnitTest. Separar pruebas JVM, instrumentadas y BLE/GPS físico; informar cualquier caso omitido.

Entrega los archivos completos, una lista concreta de cambios, comandos ejecutados y sus resultados. Distingue pruebas pasadas, omitidas y bloqueadas. No certifiques comportamiento de hardware o servidor que no hayas observado.

## Reglas compartidas

- Fuente de verdad: el diseño del módulo, el catálogo de datos y las referencias del profesor en doc/materiales/referencias. No depender de recuerdos de este chat; releer los contratos antes de cada módulo.
- Mantener doc/, src/, agents/, AUTHORS.md, README.md y .gitignore. No modificar agents. Una carpeta de primer nivel de src corresponde a un xxx_design.md y un xxx_prompt.md.
- Diseñar primero, implementar después. No inventar interfaces ni añadir métodos públicos ausentes del diseño. Helpers privados mínimos y responsabilidades separadas.
- N, Z, R, B, Text, agregaciones y colecciones para contratos lógicos; + público, - privado; --> efectos, <-- consulta, --x estático. No exponer estado privado en pruebas.
- Cabecera de archivo: Nombre, Descripción, Copyright (c) 2026 Equipo 2 - SENDA / OZORA, Fecha, Autores (Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi), Aportación. Conservar fechas y atribuciones históricas. Base original Jordi Bataller i Mascarell en el firmware.
- Cada función con nombre debe tener contrato de entradas/salida y descripción breve entre líneas discontinuas. No introducir comentarios en JSON ni alterar formatos o código de terceros para forzar una cabecera.
- Estado mutable: confinarlo al hilo del componente o usar mecanismos explícitos. Persistencia concurrente: transacciones, bloqueo y claves de integridad. No prometer thread-safety sin comprobarla.
- No incluir credenciales, datos personales reales, contraseñas maestras ni éxitos simulados. No ejecutar pruebas de escritura en producción.
- No iniciar emuladores, subir firmware, instalar en teléfonos, desplegar, hacer commit, push, merge ni crear copias de seguridad automáticamente.

## Inventario de partida

- `src/android/app/src/androidTest/java/com/example/ozora/senda/AccessScreensTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/AccountScreensTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/ColaMedicionesTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/ExampleInstrumentedTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/FinalScreensTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/HomeMapScreensTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/PreparadorMedicionesTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/PreparadorTemperaturasTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/RecorridoTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/RouteScreensTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/RoutesScreenTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/TemperatureQueueTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/auth/AccessFlowsTest.java`
- `src/android/app/src/androidTest/java/com/example/ozora/senda/auth/SessionPersistenceTest.java`
- `src/android/app/src/main/assets/map/leaflet.js`
- `src/android/app/src/main/java/com/example/ozora/senda/MainActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/SendaApplication.java`
- `src/android/app/src/main/java/com/example/ozora/senda/auth/AuthScreen.java`
- `src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricHelper.java`
- `src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricSetupActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/auth/Login.java`
- `src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryToken.java`
- `src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryViewModel.java`
- `src/android/app/src/main/java/com/example/ozora/senda/auth/SessionManager.java`
- `src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleScanner.java`
- `src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleTestActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/bluetooth/EstadoLlavero.java`
- `src/android/app/src/main/java/com/example/ozora/senda/bluetooth/IBeaconParser.java`
- `src/android/app/src/main/java/com/example/ozora/senda/integration/CapturaLlaveroController.java`
- `src/android/app/src/main/java/com/example/ozora/senda/integration/ControlContadores.java`
- `src/android/app/src/main/java/com/example/ozora/senda/integration/ControlLecturasSinContador.java`
- `src/android/app/src/main/java/com/example/ozora/senda/integration/LlaveroFlowTestActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/integration/MedicionSink.java`
- `src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorMediciones.java`
- `src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorTemperaturas.java`
- `src/android/app/src/main/java/com/example/ozora/senda/location/LocationTracker.java`
- `src/android/app/src/main/java/com/example/ozora/senda/model/Measurement.java`
- `src/android/app/src/main/java/com/example/ozora/senda/model/MedicionAmbiental.java`
- `src/android/app/src/main/java/com/example/ozora/senda/model/MedicionTemperatura.java`
- `src/android/app/src/main/java/com/example/ozora/senda/model/Route.java`
- `src/android/app/src/main/java/com/example/ozora/senda/model/RoutePoint.java`
- `src/android/app/src/main/java/com/example/ozora/senda/model/Worker.java`
- `src/android/app/src/main/java/com/example/ozora/senda/network/ApiClient.java`
- `src/android/app/src/main/java/com/example/ozora/senda/network/LogicaFake.java`
- `src/android/app/src/main/java/com/example/ozora/senda/network/MeasurementSync.java`
- `src/android/app/src/main/java/com/example/ozora/senda/qr/QrLlaveroParser.java`
- `src/android/app/src/main/java/com/example/ozora/senda/qr/QrScannerActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/qr/QrTestActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/routes/RouteActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/routes/RouteManager.java`
- `src/android/app/src/main/java/com/example/ozora/senda/routes/RoutePeriod.java`
- `src/android/app/src/main/java/com/example/ozora/senda/routes/RutaService.java`
- `src/android/app/src/main/java/com/example/ozora/senda/statistics/EnvironmentalAssessment.java`
- `src/android/app/src/main/java/com/example/ozora/senda/statistics/StatisticsCalculator.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/AccountActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/BarChartView.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/FieldScreen.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/HistoryScreen.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/HomeMapController.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/KeyringScreen.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/KeyringStatus.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/PermissionsActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/RouteCardView.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/RouteMap.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/RoutePreviewView.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/RouteScreen.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/SummaryActivity.java`
- `src/android/app/src/main/java/com/example/ozora/senda/ui/WelcomeActivity.java`
- `src/android/app/src/test/java/com/example/ozora/senda/ExampleUnitTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/auth/RecoveryTokenTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/auth/SessionManagerTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/bluetooth/IBeaconParserTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/bluetooth/TelemetriaTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/integration/ControlContadoresTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/integration/ControlLecturasSinContadorTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/model/MedicionAmbientalTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/network/ApiContractTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/network/LogicaFakePayloadTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/network/LogicaFakeTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/network/RecoveryContractTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/qr/QrLlaveroParserTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/routes/RoutePeriodTest.java`
- `src/android/app/src/test/java/com/example/ozora/senda/ui/KeyringStatusTest.java`
