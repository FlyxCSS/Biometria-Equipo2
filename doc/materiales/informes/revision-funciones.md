# Revisión estática de clases y funciones · 19/11/2026

Base: ca5aa16. Inventario extraído con analizadores de sintaxis de Java, JavaScript, PHP y C++. No certifica corrección física del sensor ni concurrencia de producción.

Operaciones con nombre identificadas: 767. Las funciones anónimas se documentan por la operación que las contiene.

## src/android/app/src/androidTest/java/com/example/ozora/senda/AccessScreensTest.java

### AccessScreensTest.loginReutilizaLaSesionVigente
- Contrato: `loginReutilizaLaSesionVigente() → sin valor de retorno`.
- Responsabilidad: Login Reutiliza La Sesion Vigente. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccessScreensTest.captura
- Contrato: `nombre: Text --> captura() → sin valor de retorno`.
- Responsabilidad: Captura. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccessScreensTest.accesoYRecuperacionValidanSinCrearSesion
- Contrato: `accesoYRecuperacionValidanSinCrearSesion() → sin valor de retorno`.
- Responsabilidad: Acceso YRecuperacion Validan Sin Crear Sesion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/AccountScreensTest.java

### AccountScreensTest.prefs
- Contrato: `prefs() → SharedPreferences`.
- Responsabilidad: Prefs. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountScreensTest.open
- Contrato: `open() → sin valor de retorno`.
- Responsabilidad: Open. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountScreensTest.section
- Contrato: `tab: Z --> section() → Intent`.
- Responsabilidad: Section. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountScreensTest.llaveroSinVincularDesvinculacionYAjustesDelPerfil
- Contrato: `llaveroSinVincularDesvinculacionYAjustesDelPerfil() → sin valor de retorno`.
- Responsabilidad: Llavero Sin Vincular Desvinculacion YAjustes Del Perfil. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountScreensTest.unRecorridoActivoImpideDesvincularYLaRecepcionCaduca
- Contrato: `unRecorridoActivoImpideDesvincularYLaRecepcionCaduca() → sin valor de retorno`.
- Responsabilidad: Un Recorrido Activo Impide Desvincular YLa Recepcion Caduca. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountScreensTest.capture
- Contrato: `name: Text --> capture() → sin valor de retorno`.
- Responsabilidad: Capture. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/ColaMedicionesTest.java

### ColaMedicionesTest.actualizaSinBorrarLecturasAntiguas
- Contrato: `actualizaSinBorrarLecturasAntiguas() → sin valor de retorno`.
- Responsabilidad: Las lecturas v1 permanecen locales y las nuevas guardan identidad BLE y estado.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, actualización persistente.

## src/android/app/src/androidTest/java/com/example/ozora/senda/ExampleInstrumentedTest.java

### ExampleInstrumentedTest.useAppContext
- Contrato: `useAppContext() → sin valor de retorno`.
- Responsabilidad: Use App Context. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/FinalScreensTest.java

### FinalScreensTest.setup
- Contrato: `setup() → sin valor de retorno`.
- Responsabilidad: Setup. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### FinalScreensTest.cleanup
- Contrato: `cleanup() → sin valor de retorno`.
- Responsabilidad: Cleanup. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: borrado o desvinculación.

### FinalScreensTest.waitSync
- Contrato: `waitSync() → sin valor de retorno`.
- Responsabilidad: Wait Sync. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FinalScreensTest.retryDrainsMoreThanOneBatchAndPreservesOtherAccounts
- Contrato: `retryDrainsMoreThanOneBatchAndPreservesOtherAccounts() → sin valor de retorno`.
- Responsabilidad: Retry Drains More Than One Batch And Preserves Other Accounts. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente.

### FinalScreensTest.networkFailurePreservesQueueAndRetryConfirms
- Contrato: `networkFailurePreservesQueueAndRetryConfirms() → sin valor de retorno`.
- Responsabilidad: Network Failure Preserves Queue And Retry Confirms. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FinalScreensTest.securityAndPermissionsRenderWithBackNavigation
- Contrato: `securityAndPermissionsRenderWithBackNavigation() → sin valor de retorno`.
- Responsabilidad: Security And Permissions Render With Back Navigation. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FinalScreensTest.historyReadsServerAndEmptyState
- Contrato: `historyReadsServerAndEmptyState() → sin valor de retorno`.
- Responsabilidad: History Reads Server And Empty State. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FinalScreensTest.awaitHistory
- Contrato: `screen: ActivityScenarioMainActivity --> awaitHistory() → sin valor de retorno`.
- Responsabilidad: Await History. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FinalScreensTest.capture
- Contrato: `name: Text --> capture() → sin valor de retorno`.
- Responsabilidad: Capture. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/HomeMapScreensTest.java

### HomeMapScreensTest.mapaYFiltrosConservanElEstadoEntreSecciones
- Contrato: `mapaYFiltrosConservanElEstadoEntreSecciones() → sin valor de retorno`.
- Responsabilidad: Mapa YFiltros Conservan El Estado Entre Secciones. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapScreensTest.calorAislaCuentaYFechasYAgrupaConcentraciones
- Contrato: `calorAislaCuentaYFechasYAgrupaConcentraciones() → sin valor de retorno`.
- Responsabilidad: Calor Aisla Cuenta YFechas YAgrupa Concentraciones. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapScreensTest.esperarMapa
- Contrato: `screen: ActivityScenarioMainActivity --> esperarMapa() → sin valor de retorno`.
- Responsabilidad: Esperar Mapa. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapScreensTest.captura
- Contrato: `name: Text --> captura() → sin valor de retorno`.
- Responsabilidad: Captura. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/PreparadorMedicionesTest.java

### PreparadorMedicionesTest.aceptaMasDeCuatroLecturasIgualesConPosicionRecienteSinMoverse
- Contrato: `aceptaMasDeCuatroLecturasIgualesConPosicionRecienteSinMoverse() → sin valor de retorno`.
- Responsabilidad: Acepta Mas De Cuatro Lecturas Iguales Con Posicion Reciente Sin Moverse. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/PreparadorTemperaturasTest.java

### PreparadorTemperaturasTest.trama
- Contrato: `valor: Z --> trama() → IBeaconParserResultado`.
- Responsabilidad: Trama. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PreparadorTemperaturasTest.ubicacion
- Contrato: `ubicacion() → Location`.
- Responsabilidad: Ubicacion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PreparadorTemperaturasTest.esperaGpsYConservaLaPosicionDeLaMuestraSinDuplicarla
- Contrato: `esperaGpsYConservaLaPosicionDeLaMuestraSinDuplicarla() → sin valor de retorno`.
- Responsabilidad: Espera Gps YConserva La Posicion De La Muestra Sin Duplicarla. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PreparadorTemperaturasTest.rechazaGpsAntiguoImprecisoInvalidoYOtroLlavero
- Contrato: `rechazaGpsAntiguoImprecisoInvalidoYOtroLlavero() → sin valor de retorno`.
- Responsabilidad: Rechaza Gps Antiguo Impreciso Invalido YOtro Llavero. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/RecorridoTest.java

### RecorridoTest.conservaElRecorridoYSusEstadisticasPorTrabajador
- Contrato: `conservaElRecorridoYSusEstadisticasPorTrabajador() → sin valor de retorno`.
- Responsabilidad: Conserva El Recorrido YSus Estadisticas Por Trabajador. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecorridoTest.historialYPaginacionSoloDevuelvenDatosDelTrabajadorYParque
- Contrato: `historialYPaginacionSoloDevuelvenDatosDelTrabajadorYParque() → sin valor de retorno`.
- Responsabilidad: Historial YPaginacion Solo Devuelven Datos Del Trabajador YParque. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/RouteScreensTest.java

### RouteScreensTest.finalizarAbreElResumenConLasLecturasDeLaRuta
- Contrato: `finalizarAbreElResumenConLasLecturasDeLaRuta() → sin valor de retorno`.
- Responsabilidad: Finalizar Abre El Resumen Con Las Lecturas De La Ruta. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/RoutesScreenTest.java

### RoutesScreenTest.insert
- Contrato: `db: RouteManager, user: Z, park: Z, start: Z, active: B --> insert() → Z`.
- Responsabilidad: Insert. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RoutesScreenTest.filtraPorInicioYCuentaSinDuplicarLimites
- Contrato: `filtraPorInicioYCuentaSinDuplicarLimites() → sin valor de retorno`.
- Responsabilidad: Filtra Por Inicio YCuenta Sin Duplicar Limites. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RoutesScreenTest.tarjetasYFiltrosMuestranLosDatosDelPeriodo
- Contrato: `tarjetasYFiltrosMuestranLosDatosDelPeriodo() → sin valor de retorno`.
- Responsabilidad: Tarjetas YFiltros Muestran Los Datos Del Periodo. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, borrado o desvinculación.

### RoutesScreenTest.capture
- Contrato: `name: Text --> capture() → sin valor de retorno`.
- Responsabilidad: Capture. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/TemperatureQueueTest.java

### TemperatureQueueTest.versionCuatroConservaLecturasSinGpsComoHistoricoLocal
- Contrato: `versionCuatroConservaLecturasSinGpsComoHistoricoLocal() → sin valor de retorno`.
- Responsabilidad: Version Cuatro Conserva Lecturas Sin Gps Como Historico Local. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente.

### TemperatureQueueTest.migraVersionTresYConservaTemperaturasSinReenviarElHistorico
- Contrato: `migraVersionTresYConservaTemperaturasSinReenviarElHistorico() → sin valor de retorno`.
- Responsabilidad: Migra Version Tres YConserva Temperaturas Sin Reenviar El Historico. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, actualización persistente.

## src/android/app/src/androidTest/java/com/example/ozora/senda/auth/AccessFlowsTest.java

### AccessFlowsTest.capture
- Contrato: `file: Text --> capture() → sin valor de retorno`.
- Responsabilidad: Capture. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccessFlowsTest.recuperacionValidaCamposYConservaPasoSinEnviarPeticiones
- Contrato: `recuperacionValidaCamposYConservaPasoSinEnviarPeticiones() → sin valor de retorno`.
- Responsabilidad: Recuperacion Valida Campos YConserva Paso Sin Enviar Peticiones. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccessFlowsTest.biometriaSePuedeOmitirSinGuardarUnaCredencial
- Contrato: `biometriaSePuedeOmitirSinGuardarUnaCredencial() → sin valor de retorno`.
- Responsabilidad: Biometria Se Puede Omitir Sin Guardar Una Credencial. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccessFlowsTest.entradaManualQrRechazaErroresYDevuelveCodigoNormalizado
- Contrato: `entradaManualQrRechazaErroresYDevuelveCodigoNormalizado() → sin valor de retorno`.
- Responsabilidad: Entrada Manual Qr Rechaza Errores YDevuelve Codigo Normalizado. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/androidTest/java/com/example/ozora/senda/auth/SessionPersistenceTest.java

### SessionPersistenceTest.restauraLaSesionYElCierreLaElimina
- Contrato: `restauraLaSesionYElCierreLaElimina() → sin valor de retorno`.
- Responsabilidad: Restaura La Sesion YEl Cierre La Elimina. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/assets/map/leaflet.js

### leaflet.l
- Contrato: `t: Dato --> l() → Dato`.
- Responsabilidad: L. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.N
- Contrato: `N() → Dato`.
- Responsabilidad: N. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.a
- Contrato: `t: Dato, e: Dato --> a() → Dato`.
- Responsabilidad: A. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.h
- Contrato: `t: Dato --> h() → Dato`.
- Responsabilidad: H. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.j
- Contrato: `t: Dato, e: Dato, i: Dato --> j() → Dato`.
- Responsabilidad: J. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.H
- Contrato: `t: Dato, e: Dato, i: Dato --> H() → Dato`.
- Responsabilidad: H. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.u
- Contrato: `u() → Dato`.
- Responsabilidad: U. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.i
- Contrato: `t: Dato, e: Dato --> i() → Dato`.
- Responsabilidad: I. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.W
- Contrato: `t: Dato --> W() → Dato`.
- Responsabilidad: W. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.F
- Contrato: `t: Dato --> F() → Dato`.
- Responsabilidad: F. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.c
- Contrato: `t: Dato, e: Dato --> c() → Dato`.
- Responsabilidad: C. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.U
- Contrato: `t: Dato, e: Dato, i: Dato --> U() → Dato`.
- Responsabilidad: U. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.q
- Contrato: `t: Dato, i: Dato --> q() → Dato`.
- Responsabilidad: Q. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### leaflet.G
- Contrato: `t: Dato, e: Dato --> G() → Dato`.
- Responsabilidad: G. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Y
- Contrato: `t: Dato --> Y() → Dato`.
- Responsabilidad: Y. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.J
- Contrato: `t: Dato --> J() → Dato`.
- Responsabilidad: J. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.x
- Contrato: `t: Dato, e: Dato, i: Dato --> x() → Dato`.
- Responsabilidad: X. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.r
- Contrato: `t: Dato --> r() → Dato`.
- Responsabilidad: R. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.lastId
- Contrato: `lastId() → Dato`.
- Responsabilidad: Last Id. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.et
- Contrato: `et() → Dato`.
- Responsabilidad: Et. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.e
- Contrato: `e() → Dato`.
- Responsabilidad: E. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.p
- Contrato: `t: Dato, e: Dato, i: Dato --> p() → Dato`.
- Responsabilidad: P. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.m
- Contrato: `t: Dato, e: Dato, i: Dato --> m() → Dato`.
- Responsabilidad: M. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.f
- Contrato: `t: Dato, e: Dato --> f() → Dato`.
- Responsabilidad: F. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet._
- Contrato: `t: Dato, e: Dato --> _() → Dato`.
- Responsabilidad:  . Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.s
- Contrato: `t: Dato, e: Dato --> s() → Dato`.
- Responsabilidad: S. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.g
- Contrato: `t: Dato, e: Dato --> g() → Dato`.
- Responsabilidad: G. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.v
- Contrato: `t: Dato, e: Dato, i: Dato --> v() → Dato`.
- Responsabilidad: V. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### leaflet.w
- Contrato: `t: Dato, e: Dato, i: Dato --> w() → Dato`.
- Responsabilidad: W. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.at
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato --> at() → Dato`.
- Responsabilidad: At. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ht
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato --> ht() → Dato`.
- Responsabilidad: Ht. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ct
- Contrato: `t: Dato --> ct() → Dato`.
- Responsabilidad: Ct. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.dt
- Contrato: `t: Dato, e: Dato --> dt() → Dato`.
- Responsabilidad: Dt. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.y
- Contrato: `t: Dato --> y() → Dato`.
- Responsabilidad: Y. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Jt
- Contrato: `t: Dato, e: Dato, i: Dato --> Jt() → Dato`.
- Responsabilidad: Jt. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.$t
- Contrato: `t: Dato --> $t() → Dato`.
- Responsabilidad: $t. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Qt
- Contrato: `t: Dato --> Qt() → Dato`.
- Responsabilidad: Qt. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.te
- Contrato: `t: Dato --> te() → Dato`.
- Responsabilidad: Te. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ee
- Contrato: `t: Dato, e: Dato --> ee() → Dato`.
- Responsabilidad: Ee. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ne
- Contrato: `t: Dato, i: Dato --> ne() → Dato`.
- Responsabilidad: Ne. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.e
- Contrato: `t: Dato --> e() → Dato`.
- Responsabilidad: E. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet._e
- Contrato: `t: Dato --> _e() → Dato`.
- Responsabilidad:  e. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.pe
- Contrato: `t: Dato, e: Dato --> pe() → Dato`.
- Responsabilidad: Pe. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.P
- Contrato: `t: Dato, e: Dato, i: Dato --> P() → Dato`.
- Responsabilidad: P. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.T
- Contrato: `t: Dato --> T() → Dato`.
- Responsabilidad: T. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.me
- Contrato: `t: Dato --> me() → Dato`.
- Responsabilidad: Me. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.fe
- Contrato: `t: Dato --> fe() → Dato`.
- Responsabilidad: Fe. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ge
- Contrato: `t: Dato --> ge() → Dato`.
- Responsabilidad: Ge. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ve
- Contrato: `t: Dato, e: Dato --> ve() → Dato`.
- Responsabilidad: Ve. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.M
- Contrato: `t: Dato, e: Dato --> M() → Dato`.
- Responsabilidad: M. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.z
- Contrato: `t: Dato, e: Dato --> z() → Dato`.
- Responsabilidad: Z. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ye
- Contrato: `t: Dato, e: Dato --> ye() → Dato`.
- Responsabilidad: Ye. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.xe
- Contrato: `t: Dato --> xe() → Dato`.
- Responsabilidad: Xe. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.C
- Contrato: `t: Dato, e: Dato --> C() → Dato`.
- Responsabilidad: C. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.we
- Contrato: `t: Dato --> we() → Dato`.
- Responsabilidad: We. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.be
- Contrato: `t: Dato, e: Dato, i: Dato --> be() → Dato`.
- Responsabilidad: Be. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Z
- Contrato: `t: Dato, e: Dato --> Z() → Dato`.
- Responsabilidad: Z. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Pe
- Contrato: `t: Dato --> Pe() → Dato`.
- Responsabilidad: Pe. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Le
- Contrato: `Le() → Dato`.
- Responsabilidad: Le. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Te
- Contrato: `Te() → Dato`.
- Responsabilidad: Te. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Me
- Contrato: `t: Dato --> Me() → Dato`.
- Responsabilidad: Me. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ze
- Contrato: `ze() → Dato`.
- Responsabilidad: Ze. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ce
- Contrato: `t: Dato --> Ce() → Dato`.
- Responsabilidad: Ce. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ze
- Contrato: `t: Dato --> Ze() → Dato`.
- Responsabilidad: Ze. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.disableTextSelection
- Contrato: `disableTextSelection() → Dato`.
- Responsabilidad: Disable Text Selection. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.enableTextSelection
- Contrato: `enableTextSelection() → Dato`.
- Responsabilidad: Enable Text Selection. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.S
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato --> S() → Dato`.
- Responsabilidad: S. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.k
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato --> k() → Dato`.
- Responsabilidad: K. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Se
- Contrato: `t: Dato, e: Dato --> Se() → Dato`.
- Responsabilidad: Se. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ke
- Contrato: `e: Dato, t: Dato, i: Dato, n: Dato --> ke() → Dato`.
- Responsabilidad: Ke. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Oe
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato, o: Dato --> Oe() → Dato`.
- Responsabilidad: Oe. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ae
- Contrato: `t: Dato --> Ae() → Dato`.
- Responsabilidad: Ae. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Be
- Contrato: `t: Dato --> Be() → Dato`.
- Responsabilidad: Be. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ie
- Contrato: `t: Dato --> Ie() → Dato`.
- Responsabilidad: Ie. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.O
- Contrato: `t: Dato --> O() → Dato`.
- Responsabilidad: O. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Re
- Contrato: `t: Dato --> Re() → Dato`.
- Responsabilidad: Re. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ne
- Contrato: `t: Dato --> Ne() → Dato`.
- Responsabilidad: Ne. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.De
- Contrato: `t: Dato, e: Dato --> De() → Dato`.
- Responsabilidad: De. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.He
- Contrato: `t: Dato --> He() → Dato`.
- Responsabilidad: He. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.We
- Contrato: `t: Dato, e: Dato --> We() → Dato`.
- Responsabilidad: We. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.d
- Contrato: `t: Dato --> d() → Dato`.
- Responsabilidad: D. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet._
- Contrato: `t: Dato --> _() → Dato`.
- Responsabilidad:  . Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.p
- Contrato: `t: Dato --> p() → Dato`.
- Responsabilidad: P. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.f
- Contrato: `t: Dato --> f() → Dato`.
- Responsabilidad: F. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ue
- Contrato: `t: Dato --> Ue() → Dato`.
- Responsabilidad: Ue. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.t
- Contrato: `t: Dato, e: Dato --> t() → Dato`.
- Responsabilidad: T. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Je
- Contrato: `t: Dato, e: Dato, i: Dato --> Je() → Dato`.
- Responsabilidad: Je. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.$e
- Contrato: `t: Dato, e: Dato --> $e() → Dato`.
- Responsabilidad: $e. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### leaflet.Qe
- Contrato: `t: Dato --> Qe() → Dato`.
- Responsabilidad: Qe. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ei
- Contrato: `t: Dato, e: Dato --> ei() → Dato`.
- Responsabilidad: Ei. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ii
- Contrato: `t: Dato, e: Dato, i: Dato --> ii() → Dato`.
- Responsabilidad: Ii. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ni
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato, o: Dato --> ni() → Dato`.
- Responsabilidad: Ni. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.oi
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato, o: Dato --> oi() → Dato`.
- Responsabilidad: Oi. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.si
- Contrato: `t: Dato, e: Dato --> si() → Dato`.
- Responsabilidad: Si. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ri
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato --> ri() → Dato`.
- Responsabilidad: Ri. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.I
- Contrato: `t: Dato --> I() → Dato`.
- Responsabilidad: I. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ai
- Contrato: `t: Dato --> ai() → Dato`.
- Responsabilidad: Ai. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.hi
- Contrato: `t: Dato, e: Dato --> hi() → Dato`.
- Responsabilidad: Hi. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### leaflet.e
- Contrato: `t: Dato, e: Dato, i: Dato --> e() → Dato`.
- Responsabilidad: E. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.bi
- Contrato: `t: Dato, e: Dato --> bi() → Dato`.
- Responsabilidad: Bi. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### leaflet.Pi
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato --> Pi() → Dato`.
- Responsabilidad: Pi. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Li
- Contrato: `t: Dato --> Li() → Dato`.
- Responsabilidad: Li. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ti
- Contrato: `t: Dato, e: Dato, i: Dato --> Ti() → Dato`.
- Responsabilidad: Ti. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Mi
- Contrato: `t: Dato, e: Dato --> Mi() → Dato`.
- Responsabilidad: Mi. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.zi
- Contrato: `t: Dato, e: Dato, i: Dato, n: Dato --> zi() → Dato`.
- Responsabilidad: Zi. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ci
- Contrato: `t: Dato, e: Dato --> Ci() → Dato`.
- Responsabilidad: Ci. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Zi
- Contrato: `t: Dato --> Zi() → Dato`.
- Responsabilidad: Zi. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Si
- Contrato: `t: Dato, e: Dato --> Si() → Dato`.
- Responsabilidad: Si. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.ji
- Contrato: `t: Dato, e: Dato --> ji() → Dato`.
- Responsabilidad: Ji. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ui
- Contrato: `t: Dato --> Ui() → Dato`.
- Responsabilidad: Ui. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### leaflet.Ki
- Contrato: `t: Dato --> Ki() → Dato`.
- Responsabilidad: Ki. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/MainActivity.java

### MainActivity.run
- Contrato: `run() → sin valor de retorno`.
- Responsabilidad: Run. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.handleOnBackPressed
- Contrato: `handleOnBackPressed() → sin valor de retorno`.
- Responsabilidad: Handle On Back Pressed. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.onNewIntent
- Contrato: `intent: Intent --> onNewIntent() → sin valor de retorno`.
- Responsabilidad: On New Intent. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.onSaveInstanceState
- Contrato: `state: Bundle --> onSaveInstanceState() → sin valor de retorno`.
- Responsabilidad: On Save Instance State. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.onStart
- Contrato: `onStart() → sin valor de retorno`.
- Responsabilidad: On Start. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.onStop
- Contrato: `onStop() → sin valor de retorno`.
- Responsabilidad: On Stop. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.onDestroy
- Contrato: `onDestroy() → sin valor de retorno`.
- Responsabilidad: On Destroy. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.mostrar
- Contrato: `destino: Z --> mostrar() → sin valor de retorno`.
- Responsabilidad: Mostrar. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### MainActivity.ajustarPanelInicio
- Contrato: `ajustarPanelInicio() → sin valor de retorno`.
- Responsabilidad: Ajustar Panel Inicio. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.accion
- Contrato: `viewId: Z, action: Runnable --> accion() → sin valor de retorno`.
- Responsabilidad: Accion. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.visible
- Contrato: `viewId: Z, visible: B --> visible() → sin valor de retorno`.
- Responsabilidad: Visible. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.textoEn
- Contrato: `viewId: Z, text: CharSequence --> textoEn() → sin valor de retorno`.
- Responsabilidad: Texto En. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.mensaje
- Contrato: `texto: Text --> mensaje() → sin valor de retorno`.
- Responsabilidad: Mensaje. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.actualizar
- Contrato: `actualizar() → sin valor de retorno`.
- Responsabilidad: Actualizar. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.estadoUbicacion
- Contrato: `estadoUbicacion() → Text`.
- Responsabilidad: Estado Ubicacion. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.permiso
- Contrato: `permiso: Text --> permiso() → B`.
- Responsabilidad: Permiso. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.estadoSensor
- Contrato: `estadoSensor() → Text`.
- Responsabilidad: Estado Sensor. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.mostrarUltima
- Contrato: `sensor: Text, conAviso: B --> mostrarUltima() → sin valor de retorno`.
- Responsabilidad: Mostrar Ultima. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.mostrarNivel
- Contrato: `value: Z --> mostrarNivel() → sin valor de retorno`.
- Responsabilidad: Mostrar Nivel. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.horaLectura
- Contrato: `iso: Text --> horaLectura() → Text`.
- Responsabilidad: Hora Lectura. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.iniciales
- Contrato: `name: Text, max: Z --> iniciales() → Text`.
- Responsabilidad: Iniciales. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.fechaLectura
- Contrato: `iso: Text --> fechaLectura() → Text`.
- Responsabilidad: Fecha Lectura. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.nombreSensor
- Contrato: `sensor: Text --> nombreSensor() → Text`.
- Responsabilidad: Nombre Sensor. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.cambiarPagina
- Contrato: `incremento: Z --> cambiarPagina() → sin valor de retorno`.
- Responsabilidad: Cambiar Pagina. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.cargarPagina
- Contrato: `desplazar: B --> cargarPagina() → sin valor de retorno`.
- Responsabilidad: Cargar Pagina. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.agregarRuta
- Contrato: `lista: LinearLayout, ruta: Cursor --> agregarRuta() → sin valor de retorno`.
- Responsabilidad: Agregar Ruta. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.agregarLectura
- Contrato: `lista: LinearLayout, lectura: Cursor --> agregarLectura() → sin valor de retorno`.
- Responsabilidad: Agregar Lectura. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.abrirRuta
- Contrato: `pantalla: Class, rutaId: Z --> abrirRuta() → sin valor de retorno`.
- Responsabilidad: Abrir Ruta. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### MainActivity.confirmarSalida
- Contrato: `confirmarSalida() → sin valor de retorno`.
- Responsabilidad: Confirmar Salida. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.acceso
- Contrato: `mensaje: Text --> acceso() → sin valor de retorno`.
- Responsabilidad: Acceso. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### MainActivity.ajustes
- Contrato: `ajustes() → SharedPreferences`.
- Responsabilidad: Ajustes. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.elegido
- Contrato: `elegido() → Text`.
- Responsabilidad: Elegido. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.seleccionar
- Contrato: `seleccionar() → sin valor de retorno`.
- Responsabilidad: Seleccionar. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### MainActivity.onActivityResult
- Contrato: `req: Z, result: Z, data: Intent --> onActivityResult() → sin valor de retorno`.
- Responsabilidad: On Activity Result. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.gestionarLlavero
- Contrato: `gestionarLlavero() → sin valor de retorno`.
- Responsabilidad: Gestionar Llavero. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.cambiarLlavero
- Contrato: `sensor: Text --> cambiarLlavero() → sin valor de retorno`.
- Responsabilidad: Cambiar Llavero. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, ejecución delegada.

### MainActivity.guardarLlavero
- Contrato: `sensor: Text --> guardarLlavero() → sin valor de retorno`.
- Responsabilidad: Guardar Llavero. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.actualizarRemoto
- Contrato: `actualizarRemoto() → sin valor de retorno`.
- Responsabilidad: Actualizar Remoto. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, ejecución delegada.

### MainActivity.abrirNotificaciones
- Contrato: `abrirNotificaciones() → sin valor de retorno`.
- Responsabilidad: Abrir Notificaciones. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### MainActivity.cambiarUbicacion
- Contrato: `cambiarUbicacion() → sin valor de retorno`.
- Responsabilidad: Cambiar Ubicacion. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.abrirAjustesApp
- Contrato: `abrirAjustesApp() → sin valor de retorno`.
- Responsabilidad: Abrir Ajustes App. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### MainActivity.mostrarAyuda
- Contrato: `mostrarAyuda() → sin valor de retorno`.
- Responsabilidad: Mostrar Ayuda. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.configurarBiometria
- Contrato: `configurarBiometria() → sin valor de retorno`.
- Responsabilidad: Configurar Biometria. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### MainActivity.configurar
- Contrato: `configurar() → sin valor de retorno`.
- Responsabilidad: Configurar. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.ajustarNumero
- Contrato: `key: Text, titulo: Text, inicial: Z, max: Z --> ajustarNumero() → sin valor de retorno`.
- Responsabilidad: Ajustar Numero. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### MainActivity.preparar
- Contrato: `preparar() → sin valor de retorno`.
- Responsabilidad: Preparar. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.centrarMapa
- Contrato: `centrarMapa() → sin valor de retorno`.
- Responsabilidad: Centrar Mapa. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.onRequestPermissionsResult
- Contrato: `req: Z, permisos: [Dato], resultados: [Dato] --> onRequestPermissionsResult() → sin valor de retorno`.
- Responsabilidad: On Request Permissions Result. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.iniciar
- Contrato: `iniciar() → sin valor de retorno`.
- Responsabilidad: Iniciar. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.confirmarFin
- Contrato: `confirmarFin() → sin valor de retorno`.
- Responsabilidad: Confirmar Fin. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.finalizar
- Contrato: `finalizar() → sin valor de retorno`.
- Responsabilidad: Finalizar. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MainActivity.salir
- Contrato: `revocar: B --> salir() → sin valor de retorno`.
- Responsabilidad: Salir. Coordina cinco secciones, permisos y acciones del trabajador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz.

## src/android/app/src/main/java/com/example/ozora/senda/SendaApplication.java

### SendaApplication.onCreate
- Contrato: `onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/auth/AuthScreen.java

### AuthScreen.mostrarFormulario
- Contrato: `mostrarFormulario() → sin valor de retorno`.
- Responsabilidad: Mostrar Formulario. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AuthScreen.pagina
- Contrato: `titulo: Text --> pagina() → sin valor de retorno`.
- Responsabilidad: Pagina. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AuthScreen.mensaje
- Contrato: `texto: Text --> mensaje() → sin valor de retorno`.
- Responsabilidad: Mensaje. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AuthScreen.mostrarEspera
- Contrato: `esperando: B --> mostrarEspera() → sin valor de retorno`.
- Responsabilidad: Mostrar Espera. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricHelper.java

### Resultado.aceptar
- Contrato: `token: Text --> aceptar() → sin valor de retorno`.
- Responsabilidad: Aceptar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.error
- Contrato: `mensaje: Text --> error() → sin valor de retorno`.
- Responsabilidad: Error. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.BiometricHelper
- Contrato: `activity: FragmentActivity --> BiometricHelper() → BiometricHelper`.
- Responsabilidad: Construye BiometricHelper. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.onAuthenticationSucceeded
- Contrato: `r: BiometricPromptAuthenticationResult --> onAuthenticationSucceeded() → sin valor de retorno`.
- Responsabilidad: On Authentication Succeeded. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.onAuthenticationError
- Contrato: `code: Z, msg: CharSequence --> onAuthenticationError() → sin valor de retorno`.
- Responsabilidad: On Authentication Error. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.activo
- Contrato: `activo() → B`.
- Responsabilidad: Activo. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.disponible
- Contrato: `disponible() → B`.
- Responsabilidad: Disponible. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.enCurso
- Contrato: `enCurso() → B`.
- Responsabilidad: En Curso. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.almacen
- Contrato: `almacen() → KeyStore`.
- Responsabilidad: Almacen. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.borrar
- Contrato: `borrar() → sin valor de retorno`.
- Responsabilidad: Borrar. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.cancelar
- Contrato: `cancelar() → sin valor de retorno`.
- Responsabilidad: Cancelar. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.activar
- Contrato: `token: Text, resultado: Resultado --> activar() → sin valor de retorno`.
- Responsabilidad: Activar. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.abrir
- Contrato: `resultado: Resultado --> abrir() → sin valor de retorno`.
- Responsabilidad: Abrir. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.mostrar
- Contrato: `cipher: Cipher, titulo: Text --> mostrar() → sin valor de retorno`.
- Responsabilidad: Mostrar. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricHelper.completar
- Contrato: `result: BiometricPromptAuthenticationResult --> completar() → sin valor de retorno`.
- Responsabilidad: Completar. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### BiometricHelper.fallar
- Contrato: `mensaje: Text --> fallar() → sin valor de retorno`.
- Responsabilidad: Fallar. Protege una credencial con el almacén y la biometría de Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/auth/BiometricSetupActivity.java

### BiometricSetupActivity.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricSetupActivity.handleOnBackPressed
- Contrato: `handleOnBackPressed() → sin valor de retorno`.
- Responsabilidad: Handle On Back Pressed. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricSetupActivity.onStart
- Contrato: `onStart() → sin valor de retorno`.
- Responsabilidad: On Start. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricSetupActivity.render
- Contrato: `render() → sin valor de retorno`.
- Responsabilidad: Render. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricSetupActivity.activate
- Contrato: `activate() → sin valor de retorno`.
- Responsabilidad: Activate. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### BiometricSetupActivity.aceptar
- Contrato: `token: Text --> aceptar() → sin valor de retorno`.
- Responsabilidad: Aceptar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricSetupActivity.error
- Contrato: `texto: Text --> error() → sin valor de retorno`.
- Responsabilidad: Error. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricSetupActivity.feedback
- Contrato: `text: Text --> feedback() → sin valor de retorno`.
- Responsabilidad: Feedback. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BiometricSetupActivity.leave
- Contrato: `leave() → sin valor de retorno`.
- Responsabilidad: Leave. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### BiometricSetupActivity.onDestroy
- Contrato: `onDestroy() → sin valor de retorno`.
- Responsabilidad: On Destroy. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/auth/Login.java

### Login.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: Reutiliza la sesión vigente; la biometría valida su credencial con el servidor al acceder de nuevo.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Login.abrirPanel
- Contrato: `abrirPanel() → sin valor de retorno`.
- Responsabilidad: Abre el cuaderno tras un login sin cambio pendiente.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### Login.onDestroy
- Contrato: `onDestroy() → sin valor de retorno`.
- Responsabilidad: Cancela la biometría y descarta el token temporal de primer acceso.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Login.acceso
- Contrato: `acceso() → sin valor de retorno`.
- Responsabilidad: Acceso. Gestiona acceso, errores y primer cambio de contraseña.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### Login.aceptar
- Contrato: `token: Text --> aceptar() → sin valor de retorno`.
- Responsabilidad: Aceptar. Gestiona acceso, errores y primer cambio de contraseña.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Login.error
- Contrato: `texto: Text --> error() → sin valor de retorno`.
- Responsabilidad: Error. Gestiona acceso, errores y primer cambio de contraseña.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Login.mensaje
- Contrato: `texto: Text --> mensaje() → sin valor de retorno`.
- Responsabilidad: Mensaje. Gestiona acceso, errores y primer cambio de contraseña.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Login.entrar
- Contrato: `entrar() → sin valor de retorno`.
- Responsabilidad: Entrar. Gestiona acceso, errores y primer cambio de contraseña.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Login.autenticar
- Contrato: `correo: Text, clave: Text, guardado: Text --> autenticar() → sin valor de retorno`.
- Responsabilidad: Verifica sesión y deriva al primer cambio antes de guardar el acceso al panel.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, ejecución delegada, puede rechazar datos o propagar errores, navegación de pantalla.

### Login.agregarClave
- Contrato: `clave: Text --> agregarClave() → sin valor de retorno`.
- Responsabilidad: Añade un icono de ojo con nombre accesible sin cambiar el texto escrito.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Login.primerAcceso
- Contrato: `correo: Text --> primerAcceso() → sin valor de retorno`.
- Responsabilidad: Solicita la clave actual y la nueva; el token temporal no abre recorridos ni biometría.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, ejecución delegada.

## src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryActivity.java

### RecoveryActivity.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryActivity.render
- Contrato: `state: RecoveryViewModelState --> render() → sin valor de retorno`.
- Responsabilidad: Render. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryActivity.submit
- Contrato: `submit() → sin valor de retorno`.
- Responsabilidad: Submit. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### RecoveryActivity.clearSecrets
- Contrato: `clearSecrets() → sin valor de retorno`.
- Responsabilidad: Clear Secrets. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryToken.java

### RecoveryToken.RecoveryToken
- Contrato: `RecoveryToken() → RecoveryToken`.
- Responsabilidad: Construye RecoveryToken. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryToken.extract
- Contrato: `input: Text --> extract() → Text`.
- Responsabilidad: Extract. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/auth/RecoveryViewModel.java

### State.State
- Contrato: `step: Step, busy: B, message: Text --> State() → State`.
- Responsabilidad: Construye State. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryViewModel.RecoveryViewModel
- Contrato: `application: Application --> RecoveryViewModel() → RecoveryViewModel`.
- Responsabilidad: Construye RecoveryViewModel. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryViewModel.observe
- Contrato: `observe() → LiveDataState`.
- Responsabilidad: Observe. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryViewModel.showLink
- Contrato: `showLink() → sin valor de retorno`.
- Responsabilidad: Show Link. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryViewModel.showEmail
- Contrato: `showEmail() → sin valor de retorno`.
- Responsabilidad: Show Email. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryViewModel.request
- Contrato: `email: Text --> request() → sin valor de retorno`.
- Responsabilidad: Request. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: ejecución delegada.

### RecoveryViewModel.reset
- Contrato: `token: Text, password: Text --> reset() → sin valor de retorno`.
- Responsabilidad: Reset. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: ejecución delegada.

### RecoveryViewModel.onCleared
- Contrato: `onCleared() → sin valor de retorno`.
- Responsabilidad: On Cleared. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/auth/SessionManager.java

### SessionManager.SessionManager
- Contrato: `worker: Worker, expiresAt: Z, token: Text --> SessionManager() → SessionManager`.
- Responsabilidad: Construye SessionManager. Restaura y protege la sesión persistente con caducidad.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SessionManager.open
- Contrato: `worker: Worker, expiresAt: Z, token: Text --> open() → sin valor de retorno`.
- Responsabilidad: Open. Restaura y protege la sesión persistente con caducidad.
- Visibilidad: pública; estática.
- Efectos detectados: puede rechazar datos o propagar errores.

### SessionManager.current
- Contrato: `current() → SessionManager`.
- Responsabilidad: Current. Restaura y protege la sesión persistente con caducidad.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SessionManager.clear
- Contrato: `clear() → sin valor de retorno`.
- Responsabilidad: Clear. Restaura y protege la sesión persistente con caducidad.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SessionManager.initialize
- Contrato: `context: Context --> initialize() → sin valor de retorno`.
- Responsabilidad: Initialize. Restaura y protege la sesión persistente con caducidad.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SessionManager.cipher
- Contrato: `cipher() → Cipher`.
- Responsabilidad: Cipher. Restaura y protege la sesión persistente con caducidad.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SessionManager.guardar
- Contrato: `guardar() → sin valor de retorno`.
- Responsabilidad: Guardar. Restaura y protege la sesión persistente con caducidad.
- Visibilidad: interna; estática.
- Efectos detectados: puede rechazar datos o propagar errores.

### SessionManager.instante
- Contrato: `iso: Text --> instante() → Z`.
- Responsabilidad: Instante. Restaura y protege la sesión persistente con caducidad.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleScanner.java

### Oyente.alRecibirTrama
- Contrato: `medida: IBeaconParserResultado, direccionMac: Text --> alRecibirTrama() → sin valor de retorno`.
- Responsabilidad: Al Recibir Trama. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Oyente.alOcurrirError
- Contrato: `mensaje: Text --> alOcurrirError() → sin valor de retorno`.
- Responsabilidad: Al Ocurrir Error. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleScanner.onScanResult
- Contrato: `callbackType: Z, resultado: ScanResult --> onScanResult() → sin valor de retorno`.
- Responsabilidad: On Scan Result. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleScanner.onScanFailed
- Contrato: `codigoError: Z --> onScanFailed() → sin valor de retorno`.
- Responsabilidad: On Scan Failed. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleScanner.BleScanner
- Contrato: `contexto: Context, oyente: Oyente --> BleScanner() → BleScanner`.
- Responsabilidad: Construye BleScanner. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### BleScanner.iniciarEscaneo
- Contrato: `iniciarEscaneo() → B`.
- Responsabilidad: Inicia escaneo BLE de baja latencia. Devuelve false y.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleScanner.detenerEscaneo
- Contrato: `detenerEscaneo() → sin valor de retorno`.
- Responsabilidad: Detiene la exploración activa y libera la referencia al escáner.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleScanner.estaEscaneando
- Contrato: `estaEscaneando() → B`.
- Responsabilidad: Indica si se ha solicitado un escaneo activo.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleScanner.tienePermisos
- Contrato: `tienePermisos() → B`.
- Responsabilidad: Comprueba los permisos BLE/ubicación vigentes según la API.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/bluetooth/BleTestActivity.java

### BleTestActivity.onCreate
- Contrato: `savedInstanceState: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: Inicializa la vista de prueba y configura sus botones.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleTestActivity.comprobarEIniciar
- Contrato: `comprobarEIniciar() → sin valor de retorno`.
- Responsabilidad: Verifica Bluetooth y solicita permisos de forma previa al escaneo.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleTestActivity.anadirSiFalta
- Contrato: `pendientes: [Dato], permiso: Text --> anadirSiFalta() → sin valor de retorno`.
- Responsabilidad: Añade un permiso a la solicitud si Android no lo ha concedido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleTestActivity.onRequestPermissionsResult
- Contrato: `codigo: Z, permisos: [Dato], resultados: [Dato] --> onRequestPermissionsResult() → sin valor de retorno`.
- Responsabilidad: Continua el escaneo solo si se concedieron los permisos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleTestActivity.alRecibirTrama
- Contrato: `medida: IBeaconParserResultado, direccionMac: Text --> alRecibirTrama() → sin valor de retorno`.
- Responsabilidad: Muestra el contenido de una trama iBeacon válida en pantalla.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz.

### BleTestActivity.alOcurrirError
- Contrato: `mensaje: Text --> alOcurrirError() → sin valor de retorno`.
- Responsabilidad: Muestra los errores informados por el escáner.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz.

### BleTestActivity.mostrarEstado
- Contrato: `mensaje: Text --> mostrarEstado() → sin valor de retorno`.
- Responsabilidad: Actualiza el texto de estado de la prueba BLE.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleTestActivity.detener
- Contrato: `detener() → sin valor de retorno`.
- Responsabilidad: Detiene el escaneo si está activo.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BleTestActivity.onStop
- Contrato: `onStop() → sin valor de retorno`.
- Responsabilidad: Detiene el escaneo cuando la pantalla deja de ser visible.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/bluetooth/EstadoLlavero.java

### EstadoLlavero.reiniciar
- Contrato: `reiniciar() → sin valor de retorno`.
- Responsabilidad: Reiniciar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EstadoLlavero.actualizar
- Contrato: `trama: IBeaconParserResultado, ahora: Z --> actualizar() → sin valor de retorno`.
- Responsabilidad: Actualizar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EstadoLlavero.vigente
- Contrato: `seleccionado: Text, instante: Z, ahora: Z --> vigente() → B`.
- Responsabilidad: Vigente. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EstadoLlavero.temperatura
- Contrato: `seleccionado: Text, ahora: Z --> temperatura() → Double`.
- Responsabilidad: Temperatura. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EstadoLlavero.bateria
- Contrato: `seleccionado: Text, ahora: Z --> bateria() → Integer`.
- Responsabilidad: Bateria. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/bluetooth/IBeaconParser.java

### IBeaconParser.IBeaconParser
- Contrato: `IBeaconParser() → IBeaconParser`.
- Responsabilidad: Construye IBeaconParser. Decodifica UUID, tipo, contador y unidades de los anuncios.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### IBeaconParser.interpretar
- Contrato: `datosFabricante: [Dato], nombreBle: Text, rssi: Z --> interpretar() → Resultado`.
- Responsabilidad: Decodifica un anuncio iBeacon y devuelve sus campos si el.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### IBeaconParser.leerUint16
- Contrato: `datos: [Dato], posicion: Z --> leerUint16() → Z`.
- Responsabilidad: Lee un entero de 16 bits sin signo en orden big-endian.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### IBeaconParser.construirUuid
- Contrato: `datos: [Dato], inicio: Z --> construirUuid() → Text`.
- Responsabilidad: Convierte los 16 bytes del UUID al formato estándar 8-4-4-4-12.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.Resultado
- Contrato: `uuid: Text, nombreBle: Text, major: Z, minor: Z, tipo: Z, contador: Z, txPower: Z, rssi: Z --> Resultado() → Resultado`.
- Responsabilidad: Construye Resultado. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.esTemperatura
- Contrato: `esTemperatura() → B`.
- Responsabilidad: Indica si la medición codificada corresponde al tipo O3.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.esBateria
- Contrato: `esBateria() → B`.
- Responsabilidad: Es Bateria. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.obtenerTemperaturaC
- Contrato: `obtenerTemperaturaC() → Double`.
- Responsabilidad: Obtener Temperatura C. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.obtenerBateriaPorcentaje
- Contrato: `obtenerBateriaPorcentaje() → Integer`.
- Responsabilidad: Obtener Bateria Porcentaje. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.esOzono
- Contrato: `esOzono() → B`.
- Responsabilidad: Es Ozono. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.obtenerOzonoPpb
- Contrato: `obtenerOzonoPpb() → Integer`.
- Responsabilidad: Devuelve Minor como ppb solo si la trama es de ozono.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.obtenerOzonoPpm
- Contrato: `obtenerOzonoPpm() → Double`.
- Responsabilidad: Convierte el valor de ozono de ppb a ppm.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/integration/CapturaLlaveroController.java

### Oyente.alRecibirAnuncio
- Contrato: `alRecibirAnuncio() → sin valor de retorno`.
- Responsabilidad: Al Recibir Anuncio. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Oyente.alPrepararTemperatura
- Contrato: `medicion: MedicionTemperatura --> alPrepararTemperatura() → sin valor de retorno`.
- Responsabilidad: Al Preparar Temperatura. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Oyente.alCambiarEstado
- Contrato: `mensaje: Text --> alCambiarEstado() → sin valor de retorno`.
- Responsabilidad: Al Cambiar Estado. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Oyente.alRecibirUbicacion
- Contrato: `ubicacion: Location --> alRecibirUbicacion() → sin valor de retorno`.
- Responsabilidad: Al Recibir Ubicacion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Oyente.alPrepararMedicion
- Contrato: `medicion: MedicionAmbiental, rssi: Z --> alPrepararMedicion() → sin valor de retorno`.
- Responsabilidad: Al Preparar Medicion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Oyente.alDescartarMedicion
- Contrato: `motivo: Text --> alDescartarMedicion() → sin valor de retorno`.
- Responsabilidad: Al Descartar Medicion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.CapturaLlaveroController
- Contrato: `contexto: Context, oyente: Oyente --> CapturaLlaveroController() → CapturaLlaveroController`.
- Responsabilidad: Construye CapturaLlaveroController. Relaciona anuncios del llavero con ubicación y preparadores.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### CapturaLlaveroController.seleccionarQr
- Contrato: `texto: Text --> seleccionarQr() → B`.
- Responsabilidad: Selecciona localmente; no consulta inventario ni asigna trabajador.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.getLlaveroSeleccionado
- Contrato: `getLlaveroSeleccionado() → Text`.
- Responsabilidad: Get Llavero Seleccionado. Relaciona anuncios del llavero con ubicación y preparadores.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.iniciar
- Contrato: `iniciar() → B`.
- Responsabilidad: Requiere permisos concedidos previamente por Activity.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.detener
- Contrato: `detener() → sin valor de retorno`.
- Responsabilidad: Detener. Relaciona anuncios del llavero con ubicación y preparadores.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.alRecibirTrama
- Contrato: `trama: IBeaconParserResultado, mac: Text --> alRecibirTrama() → sin valor de retorno`.
- Responsabilidad: Al Recibir Trama. Relaciona anuncios del llavero con ubicación y preparadores.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.alRecibirUbicacion
- Contrato: `ubicacion: Location --> alRecibirUbicacion() → sin valor de retorno`.
- Responsabilidad: Al Recibir Ubicacion. Relaciona anuncios del llavero con ubicación y preparadores.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.publicarTemperatura
- Contrato: `trama: IBeaconParserResultado --> publicarTemperatura() → sin valor de retorno`.
- Responsabilidad: Prepara una lectura solo cuando haya posición válida; evita reenviar repetidas.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.publicarSiValida
- Contrato: `trama: IBeaconParserResultado --> publicarSiValida() → sin valor de retorno`.
- Responsabilidad: Publicar Si Valida. Relaciona anuncios del llavero con ubicación y preparadores.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.alOcurrirError
- Contrato: `mensaje: Text --> alOcurrirError() → sin valor de retorno`.
- Responsabilidad: Al Ocurrir Error. Relaciona anuncios del llavero con ubicación y preparadores.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### CapturaLlaveroController.informar
- Contrato: `mensaje: Text --> informar() → sin valor de retorno`.
- Responsabilidad: Informar. Relaciona anuncios del llavero con ubicación y preparadores.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/integration/ControlContadores.java

### ControlContadores.aceptar
- Contrato: `llavero: Text, contador: Z, ahoraMs: Z --> aceptar() → B`.
- Responsabilidad: Descarta el mismo contador solo si aparece inmediatamente otra vez.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ControlContadores.reiniciar
- Contrato: `reiniciar() → sin valor de retorno`.
- Responsabilidad: Reiniciar. Filtra contadores repetidos en una ventana temporal.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/integration/ControlLecturasSinContador.java

### ControlLecturasSinContador.aceptar
- Contrato: `seleccionado: Text, ahora: Z --> aceptar() → Z`.
- Responsabilidad: Aceptar. Limita duplicados cuando la trama no aporta contador.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ControlLecturasSinContador.reiniciar
- Contrato: `reiniciar() → sin valor de retorno`.
- Responsabilidad: Reiniciar. Limita duplicados cuando la trama no aporta contador.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/integration/LlaveroFlowTestActivity.java

### LlaveroFlowTestActivity.onCreate
- Contrato: `b: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LlaveroFlowTestActivity.leerQr
- Contrato: `leerQr() → sin valor de retorno`.
- Responsabilidad: Leer Qr. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LlaveroFlowTestActivity.solicitarEIniciar
- Contrato: `solicitarEIniciar() → sin valor de retorno`.
- Responsabilidad: Solicitar EIniciar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LlaveroFlowTestActivity.siFalta
- Contrato: `f: [Dato], permiso: Text --> siFalta() → sin valor de retorno`.
- Responsabilidad: Si Falta. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LlaveroFlowTestActivity.onRequestPermissionsResult
- Contrato: `request: Z, p: [Dato], res: [Dato] --> onRequestPermissionsResult() → sin valor de retorno`.
- Responsabilidad: On Request Permissions Result. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LlaveroFlowTestActivity.alCambiarEstado
- Contrato: `texto: Text --> alCambiarEstado() → sin valor de retorno`.
- Responsabilidad: Al Cambiar Estado. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz.

### LlaveroFlowTestActivity.alRecibirUbicacion
- Contrato: `loc: Location --> alRecibirUbicacion() → sin valor de retorno`.
- Responsabilidad: Al Recibir Ubicacion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LlaveroFlowTestActivity.alPrepararMedicion
- Contrato: `m: MedicionAmbiental, rssi: Z --> alPrepararMedicion() → sin valor de retorno`.
- Responsabilidad: Al Preparar Medicion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LlaveroFlowTestActivity.alDescartarMedicion
- Contrato: `motivo: Text --> alDescartarMedicion() → sin valor de retorno`.
- Responsabilidad: Al Descartar Medicion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LlaveroFlowTestActivity.detener
- Contrato: `detener() → sin valor de retorno`.
- Responsabilidad: Detener. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LlaveroFlowTestActivity.onStop
- Contrato: `onStop() → sin valor de retorno`.
- Responsabilidad: On Stop. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/integration/MedicionSink.java

### MedicionSink.enviar
- Contrato: `medicion: MedicionAmbiental --> enviar() → sin valor de retorno`.
- Responsabilidad: Implementación futura: transmisión SOLO tras autorización y sesión válida.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorMediciones.java

### PreparadorMediciones.preparar
- Contrato: `seleccionado: Text, trama: IBeaconParserResultado, ubicacion: Location --> preparar() → Resultado`.
- Responsabilidad: Rechaza otra emisora, tramas no O3, ubicación antigua o imprecisa y duplicados recientes.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PreparadorMediciones.errorUbicacion
- Contrato: `ubicacion: Location --> errorUbicacion() → Text`.
- Responsabilidad: Error Ubicacion. Valida ubicación y deduplica lecturas de ozono.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PreparadorMediciones.reiniciar
- Contrato: `reiniciar() → sin valor de retorno`.
- Responsabilidad: Limpia los controles de duplicados al comenzar otra prueba.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.Resultado
- Contrato: `medicion: MedicionAmbiental, mensaje: Text --> Resultado() → Resultado`.
- Responsabilidad: Construye Resultado. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.ok
- Contrato: `medicion: MedicionAmbiental --> ok() → Resultado`.
- Responsabilidad: Ok. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Resultado.error
- Contrato: `motivo: Text --> error() → Resultado`.
- Responsabilidad: Error. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/integration/PreparadorTemperaturas.java

### PreparadorTemperaturas.preparar
- Contrato: `sensor: Text, trama: IBeaconParserResultado, ubicacion: Location --> preparar() → MedicionTemperatura`.
- Responsabilidad: Preparar. Valida ubicación y deduplica temperatura de forma independiente.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PreparadorTemperaturas.reiniciar
- Contrato: `reiniciar() → sin valor de retorno`.
- Responsabilidad: Reiniciar. Valida ubicación y deduplica temperatura de forma independiente.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/location/LocationTracker.java

### Oyente.alRecibirUbicacion
- Contrato: `ubicacion: Location --> alRecibirUbicacion() → sin valor de retorno`.
- Responsabilidad: Al Recibir Ubicacion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Oyente.alOcurrirError
- Contrato: `mensaje: Text --> alOcurrirError() → sin valor de retorno`.
- Responsabilidad: Al Ocurrir Error. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LocationTracker.LocationTracker
- Contrato: `contexto: Context, oyente: Oyente --> LocationTracker() → LocationTracker`.
- Responsabilidad: Construye LocationTracker. Solicita posiciones recientes aunque el teléfono permanezca quieto.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LocationTracker.iniciar
- Contrato: `iniciar() → B`.
- Responsabilidad: Iniciar. Solicita posiciones recientes aunque el teléfono permanezca quieto.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LocationTracker.detener
- Contrato: `detener() → sin valor de retorno`.
- Responsabilidad: Detener. Solicita posiciones recientes aunque el teléfono permanezca quieto.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LocationTracker.onLocationChanged
- Contrato: `ubicacion: Location --> onLocationChanged() → sin valor de retorno`.
- Responsabilidad: On Location Changed. Solicita posiciones recientes aunque el teléfono permanezca quieto.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LocationTracker.onProviderDisabled
- Contrato: `proveedor: Text --> onProviderDisabled() → sin valor de retorno`.
- Responsabilidad: On Provider Disabled. Solicita posiciones recientes aunque el teléfono permanezca quieto.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/model/Measurement.java

### Measurement.Measurement
- Contrato: `idLlavero: Text, valorPpb: Z, latitud: R, longitud: R, precisionMetros: R, fechaIso8601: Text, contador: Z --> Measurement() → Measurement`.
- Responsabilidad: Construye una lectura inmutable con unidad ppb.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Measurement.convertirAPpm
- Contrato: `convertirAPpm() → R`.
- Responsabilidad: Obtiene la equivalencia para la interfaz; se conservan ppb en el modelo.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Measurement.resumen
- Contrato: `resumen() → Text`.
- Responsabilidad: Resumen local; nunca imprime credenciales.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/model/MedicionAmbiental.java

### MedicionAmbiental.MedicionAmbiental
- Contrato: `idLlavero: Text, valorPpb: Z, latitud: R, longitud: R, precisionMetros: R, fechaIso8601: Text, contador: Z --> MedicionAmbiental() → MedicionAmbiental`.
- Responsabilidad: Construye una lectura inmutable con unidad ppb.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MedicionAmbiental.convertirAPpm
- Contrato: `convertirAPpm() → R`.
- Responsabilidad: Obtiene la equivalencia para la interfaz; se conservan ppb en el modelo.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MedicionAmbiental.resumen
- Contrato: `resumen() → Text`.
- Responsabilidad: Resumen local; nunca imprime credenciales.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/model/MedicionTemperatura.java

### MedicionTemperatura.MedicionTemperatura
- Contrato: `idLlavero: Text, centesimas: Z, fechaIso8601: Text, latitud: R, longitud: R --> MedicionTemperatura() → MedicionTemperatura`.
- Responsabilidad: Construye MedicionTemperatura. Representa temperatura en centésimas con ubicación.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/model/Route.java

### Route.Route
- Contrato: `id: Z, start: Z, end: Long, meters: R, sensor: Text, count: Z, mean: R, minimum: Z, maximum: Z --> Route() → Route`.
- Responsabilidad: Construye Route. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/model/RoutePoint.java

### RoutePoint.RoutePoint
- Contrato: `latitude: R, longitude: R, segment: Z --> RoutePoint() → RoutePoint`.
- Responsabilidad: Construye RoutePoint. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/model/Worker.java

### Worker.Worker
- Contrato: `id: Z, parkId: Z, name: Text --> Worker() → Worker`.
- Responsabilidad: Construye Worker. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/network/ApiClient.java

### Error.Error
- Contrato: `status: Z, msg: Text --> Error() → Error`.
- Responsabilidad: Conserva el estado para decidir si reintentar o pedir acceso.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ApiClient.ApiClient
- Contrato: `c: Context --> ApiClient() → ApiClient`.
- Responsabilidad: Lee la URL pública configurada sin incluir secretos en el APK.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ApiClient.ApiClient
- Contrato: `direccion: Text, debug: B --> ApiClient() → ApiClient`.
- Responsabilidad: Permite comprobar el transporte Java contra PHP sin inicializar una pantalla.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ApiClient.pedir
- Contrato: `metodo: Text, ruta: Text, token: Text, datos: JSONObject --> pedir() → JSONObject`.
- Responsabilidad: Usa HTTPS, límites de tiempo y mensajes JSON del servidor.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### ApiClient.leer
- Contrato: `flujo: InputStream --> leer() → Text`.
- Responsabilidad: Cierra el flujo y limita el tamaño de respuesta sin registrar cuerpos.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

## src/android/app/src/main/java/com/example/ozora/senda/network/LogicaFake.java

### LogicaFake.LogicaFake
- Contrato: `contexto: Context --> LogicaFake() → LogicaFake`.
- Responsabilidad: Construye LogicaFake. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.LogicaFake
- Contrato: `transporte: ApiClient --> LogicaFake() → LogicaFake`.
- Responsabilidad: Construye LogicaFake. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.iniciarSesion
- Contrato: `datos: JSONObject --> iniciarSesion() → JSONObject`.
- Responsabilidad: Iniciar Sesion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.consultarSesion
- Contrato: `token: Text --> consultarSesion() → JSONObject`.
- Responsabilidad: Consultar Sesion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.cerrarSesion
- Contrato: `token: Text --> cerrarSesion() → JSONObject`.
- Responsabilidad: Cerrar Sesion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: borrado o desvinculación.

### LogicaFake.actualizarFotoPerfil
- Contrato: `token: Text, foto: Text --> actualizarFotoPerfil() → JSONObject`.
- Responsabilidad: Actualizar Foto Perfil. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.vincularLlavero
- Contrato: `token: Text, identificador: Text --> vincularLlavero() → JSONObject`.
- Responsabilidad: Vincular Llavero. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.desvincularLlavero
- Contrato: `token: Text --> desvincularLlavero() → JSONObject`.
- Responsabilidad: Desvincular Llavero. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: borrado o desvinculación.

### LogicaFake.guardarMedicion
- Contrato: `token: Text, medicion: MedicionAmbiental --> guardarMedicion() → JSONObject`.
- Responsabilidad: Guardar Medicion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.guardarTemperatura
- Contrato: `token: Text, medicion: MedicionTemperatura --> guardarTemperatura() → JSONObject`.
- Responsabilidad: Guardar Temperatura. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.guardarDatos
- Contrato: `token: Text, datos: [Dato] --> guardarDatos() → JSONObject`.
- Responsabilidad: Guardar Datos. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### LogicaFake.datosTemperatura
- Contrato: `m: MedicionTemperatura --> datosTemperatura() → [Dato]`.
- Responsabilidad: Datos Temperatura. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: interna; estática.
- Efectos detectados: puede rechazar datos o propagar errores.

### LogicaFake.cambiarContrasena
- Contrato: `token: Text, datos: JSONObject --> cambiarContrasena() → JSONObject`.
- Responsabilidad: Cambiar Contrasena. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.historialMediciones
- Contrato: `token: Text, cursor: Text --> historialMediciones() → JSONObject`.
- Responsabilidad: Historial Mediciones. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.solicitarRecuperacion
- Contrato: `datos: JSONObject --> solicitarRecuperacion() → JSONObject`.
- Responsabilidad: Solicitar Recuperacion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.restablecerContrasena
- Contrato: `datos: JSONObject --> restablecerContrasena() → JSONObject`.
- Responsabilidad: Restablecer Contrasena. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.datosMedicion
- Contrato: `m: MedicionAmbiental --> datosMedicion() → [Dato]`.
- Responsabilidad: Conserva ppb, fecha UTC y coordenadas; el servidor comprueba la asignación.
- Visibilidad: interna; estática.
- Efectos detectados: puede rechazar datos o propagar errores.

## src/android/app/src/main/java/com/example/ozora/senda/network/MeasurementSync.java

### MeasurementSync.MeasurementSync
- Contrato: `MeasurementSync() → MeasurementSync`.
- Responsabilidad: Construye MeasurementSync. Envía la cola local por cuenta y conserva las lecturas no confirmadas.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MeasurementSync.enCurso
- Contrato: `enCurso() → B`.
- Responsabilidad: En Curso. Envía la cola local por cuenta y conserva las lecturas no confirmadas.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MeasurementSync.estado
- Contrato: `session: SessionManager --> estado() → Text`.
- Responsabilidad: Estado. Envía la cola local por cuenta y conserva las lecturas no confirmadas.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MeasurementSync.reintentar
- Contrato: `context: Context --> reintentar() → B`.
- Responsabilidad: Reintentar. Envía la cola local por cuenta y conserva las lecturas no confirmadas.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MeasurementSync.comenzar
- Contrato: `context: Context, retry: B --> comenzar() → B`.
- Responsabilidad: Comenzar. Envía la cola local por cuenta y conserva las lecturas no confirmadas.
- Visibilidad: interna; estática.
- Efectos detectados: ejecución delegada.

### MeasurementSync.sincronizar
- Contrato: `contexto: Context --> sincronizar() → sin valor de retorno`.
- Responsabilidad: Reintenta un lote de la cuenta actual.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MeasurementSync.enviarPendientes
- Contrato: `contexto: Context, sesion: SessionManager, retry: B --> enviarPendientes() → sin valor de retorno`.
- Responsabilidad: Marca enviada solo tras confirmación; conserva la lectura incluso si se rechaza.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### MeasurementSync.enviarTabla
- Contrato: `db: SQLiteDatabase, logica: LogicaFake, sesion: SessionManager, temperatura: B, recuento: Recuento --> enviarTabla() → B`.
- Responsabilidad: Enviar Tabla. Envía la cola local por cuenta y conserva las lecturas no confirmadas.
- Visibilidad: interna; estática.
- Efectos detectados: actualización persistente.

## src/android/app/src/main/java/com/example/ozora/senda/qr/QrLlaveroParser.java

### QrLlaveroParser.QrLlaveroParser
- Contrato: `QrLlaveroParser() → QrLlaveroParser`.
- Responsabilidad: Impide crear instancias de una clase con métodos estáticos.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrLlaveroParser.extraerIdentificador
- Contrato: `textoQr: Text --> extraerIdentificador() → Text`.
- Responsabilidad: Normaliza y valida un identificador SENDA del tipo S-001.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/qr/QrScannerActivity.java

### QrScannerActivity.onStart
- Contrato: `onStart() → sin valor de retorno`.
- Responsabilidad: On Start. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.beforeTextChanged
- Contrato: `s: CharSequence, start: Z, count: Z, after: Z --> beforeTextChanged() → sin valor de retorno`.
- Responsabilidad: Before Text Changed. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.onTextChanged
- Contrato: `s: CharSequence, start: Z, before: Z, count: Z --> onTextChanged() → sin valor de retorno`.
- Responsabilidad: On Text Changed. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.afterTextChanged
- Contrato: `value: androidtextEditable --> afterTextChanged() → sin valor de retorno`.
- Responsabilidad: After Text Changed. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.onSaveInstanceState
- Contrato: `state: Bundle --> onSaveInstanceState() → sin valor de retorno`.
- Responsabilidad: On Save Instance State. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.manual
- Contrato: `manual() → sin valor de retorno`.
- Responsabilidad: Manual. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.showManual
- Contrato: `showManual() → sin valor de retorno`.
- Responsabilidad: Show Manual. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.selectManual
- Contrato: `selectManual() → sin valor de retorno`.
- Responsabilidad: Select Manual. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.scan
- Contrato: `scan() → sin valor de retorno`.
- Responsabilidad: Scan. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.message
- Contrato: `text: Text --> message() → sin valor de retorno`.
- Responsabilidad: Message. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrScannerActivity.result
- Contrato: `sensor: Text --> result() → sin valor de retorno`.
- Responsabilidad: Result. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/qr/QrTestActivity.java

### QrTestActivity.onCreate
- Contrato: `estado: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: Prepara los elementos de la pantalla y el botón de escaneo.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrTestActivity.iniciarLecturaQr
- Contrato: `iniciarLecturaQr() → sin valor de retorno`.
- Responsabilidad: Abre el escáner QR de Google Play Services y procesa su respuesta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrTestActivity.mostrarIdentificador
- Contrato: `contenidoQr: Text --> mostrarIdentificador() → sin valor de retorno`.
- Responsabilidad: Valida el código con QrLlaveroParser y muestra el resultado.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/routes/RouteActivity.java

### RouteActivity.run
- Contrato: `run() → sin valor de retorno`.
- Responsabilidad: Run. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteActivity.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteActivity.buildScreen
- Contrato: `route: Route --> buildScreen() → sin valor de retorno`.
- Responsabilidad: Build Screen. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteActivity.metric
- Contrato: `parent: LinearLayout, name: Z, margin: Z --> metric() → Text`.
- Responsabilidad: Metric. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteActivity.refresh
- Contrato: `refresh() → B`.
- Responsabilidad: Refresh. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteActivity.confirmFinish
- Contrato: `confirmFinish() → sin valor de retorno`.
- Responsabilidad: Confirm Finish. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteActivity.showSummary
- Contrato: `showSummary() → sin valor de retorno`.
- Responsabilidad: Show Summary. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### RouteActivity.onStart
- Contrato: `onStart() → sin valor de retorno`.
- Responsabilidad: On Start. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteActivity.onStop
- Contrato: `onStop() → sin valor de retorno`.
- Responsabilidad: On Stop. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteActivity.onDestroy
- Contrato: `onDestroy() → sin valor de retorno`.
- Responsabilidad: On Destroy. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/routes/RouteManager.java

### RouteManager.RouteManager
- Contrato: `context: Context --> RouteManager() → RouteManager`.
- Responsabilidad: Construye RouteManager. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.RouteManager
- Contrato: `context: Context, archivo: Text --> RouteManager() → RouteManager`.
- Responsabilidad: Construye RouteManager. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.onCreate
- Contrato: `db: SQLiteDatabase --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.onUpgrade
- Contrato: `db: SQLiteDatabase, oldVersion: Z, newVersion: Z --> onUpgrade() → sin valor de retorno`.
- Responsabilidad: Conserva el histórico; las lecturas antiguas sin identidad no se envían automáticamente.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.crearTemperaturas
- Contrato: `db: SQLiteDatabase --> crearTemperaturas() → sin valor de retorno`.
- Responsabilidad: Crear Temperaturas. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.temperatura
- Contrato: `ruta: Z, m: MedicionTemperatura --> temperatura() → sin valor de retorno`.
- Responsabilidad: Temperatura. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, puede rechazar datos o propagar errores.

### RouteManager.temperaturasRuta
- Contrato: `worker: Worker, ruta: Z, limite: Z, offset: Z --> temperaturasRuta() → Cursor`.
- Responsabilidad: Temperaturas Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.iniciar
- Contrato: `usuario: Z, parque: Z, sensor: Text --> iniciar() → Z`.
- Responsabilidad: Iniciar. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.finalizar
- Contrato: `id: Z --> finalizar() → sin valor de retorno`.
- Responsabilidad: Finalizar. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente.

### RouteManager.util
- Contrato: `p: Location --> util() → B`.
- Responsabilidad: Util. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.punto
- Contrato: `id: Z, p: Location, segmento: Z, distancia: R --> punto() → sin valor de retorno`.
- Responsabilidad: Punto. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, actualización persistente, unidad transaccional.

### RouteManager.lectura
- Contrato: `id: Z, m: MedicionAmbiental --> lectura() → sin valor de retorno`.
- Responsabilidad: Lectura. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente.

### RouteManager.resumen
- Contrato: `usuario: Z, parque: Z --> resumen() → Cursor`.
- Responsabilidad: Resumen. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.ultima
- Contrato: `ruta: Z --> ultima() → Cursor`.
- Responsabilidad: Ultima. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.estadoEnvios
- Contrato: `trabajador: Worker --> estadoEnvios() → Text`.
- Responsabilidad: Cuenta solo lecturas con identidad del usuario y parque actuales.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.reintentarEnvios
- Contrato: `trabajador: Worker --> reintentarEnvios() → sin valor de retorno`.
- Responsabilidad: Recupera rechazos de su propia cola tras corregir asignación o datos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente.

### RouteManager.puntos
- Contrato: `ruta: Z --> puntos() → [Dato]`.
- Responsabilidad: Puntos. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.listar
- Contrato: `worker: Worker, limite: Z, desplazamiento: Z --> listar() → Cursor`.
- Responsabilidad: Listar. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.puntosCalor
- Contrato: `worker: Worker, since: Text, until: Text --> puntosCalor() → Cursor`.
- Responsabilidad: Puntos Calor. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.listarPeriodo
- Contrato: `worker: Worker, since: Z, until: Z, limit: Z, offset: Z --> listarPeriodo() → Cursor`.
- Responsabilidad: Listar Periodo. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.vistaPrevia
- Contrato: `worker: Worker, routeId: Z --> vistaPrevia() → [Dato]`.
- Responsabilidad: Vista Previa. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.ultimaConEnvio
- Contrato: `worker: Worker --> ultimaConEnvio() → Cursor`.
- Responsabilidad: Ultima Con Envio. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.historial
- Contrato: `worker: Worker, sensor: Text, limite: Z, desplazamiento: Z --> historial() → Cursor`.
- Responsabilidad: Historial. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.lecturasDesde
- Contrato: `worker: Worker, since: Text --> lecturasDesde() → Cursor`.
- Responsabilidad: Lecturas Desde. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.lecturasRuta
- Contrato: `worker: Worker, id: Z, limite: Z, desplazamiento: Z --> lecturasRuta() → Cursor`.
- Responsabilidad: Lecturas Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.ruta
- Contrato: `worker: Worker, id: Z --> ruta() → Route`.
- Responsabilidad: Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.ultimaRuta
- Contrato: `worker: Worker --> ultimaRuta() → Route`.
- Responsabilidad: Ultima Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteManager.leerRuta
- Contrato: `c: Cursor --> leerRuta() → Route`.
- Responsabilidad: Leer Ruta. Persiste recorridos, posiciones, O3 y temperatura en SQLite.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/routes/RoutePeriod.java

### RoutePeriod.bounds
- Contrato: `now: Z, zone: TimeZone --> bounds() → [Dato]`.
- Responsabilidad: Bounds. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/routes/RutaService.java

### RutaService.onBind
- Contrato: `i: Intent --> onBind() → IBinder`.
- Responsabilidad: On Bind. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.onStartCommand
- Contrato: `i: Intent, flags: Z, startId: Z --> onStartCommand() → Z`.
- Responsabilidad: On Start Command. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente.

### RutaService.finalizar
- Contrato: `context: Context, routes: RouteManager, id: Z --> finalizar() → sin valor de retorno`.
- Responsabilidad: Finalizar. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.caducada
- Contrato: `caducada() → B`.
- Responsabilidad: Caducada. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.alCambiarEstado
- Contrato: `s: Text --> alCambiarEstado() → sin valor de retorno`.
- Responsabilidad: Al Cambiar Estado. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.alOcurrirError
- Contrato: `s: Text --> alOcurrirError() → sin valor de retorno`.
- Responsabilidad: Al Ocurrir Error. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.alDescartarMedicion
- Contrato: `s: Text --> alDescartarMedicion() → sin valor de retorno`.
- Responsabilidad: Al Descartar Medicion. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.alRecibirAnuncio
- Contrato: `alRecibirAnuncio() → sin valor de retorno`.
- Responsabilidad: Al Recibir Anuncio. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.alRecibirUbicacion
- Contrato: `p: Location --> alRecibirUbicacion() → sin valor de retorno`.
- Responsabilidad: Al Recibir Ubicacion. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.alPrepararMedicion
- Contrato: `m: MedicionAmbiental, rssi: Z --> alPrepararMedicion() → sin valor de retorno`.
- Responsabilidad: Al Preparar Medicion. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.alPrepararTemperatura
- Contrato: `medicion: MedicionTemperatura --> alPrepararTemperatura() → sin valor de retorno`.
- Responsabilidad: Al Preparar Temperatura. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.detenerSensores
- Contrato: `detenerSensores() → sin valor de retorno`.
- Responsabilidad: Detener Sensores. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RutaService.onDestroy
- Contrato: `onDestroy() → sin valor de retorno`.
- Responsabilidad: On Destroy. Mantiene captura BLE/GPS y notificación durante el recorrido.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/statistics/EnvironmentalAssessment.java

### EnvironmentalAssessment.aviso
- Contrato: `ppb: Z, threshold: Z --> aviso() → Text`.
- Responsabilidad: Aviso. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/statistics/StatisticsCalculator.java

### StatisticsCalculator.resumen
- Contrato: `route: Route, now: Z --> resumen() → Text`.
- Responsabilidad: Resumen. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/AccountActivity.java

### AccountActivity.abrirCamara
- Contrato: `abrirCamara() → sin valor de retorno`.
- Responsabilidad: Abrir Camara. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### AccountActivity.elegirFoto
- Contrato: `elegirFoto() → sin valor de retorno`.
- Responsabilidad: Elegir Foto. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountActivity.rotacion
- Contrato: `uri: Uri --> rotacion() → Z`.
- Responsabilidad: Rotacion. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountActivity.procesarFoto
- Contrato: `uri: Uri --> procesarFoto() → sin valor de retorno`.
- Responsabilidad: Procesar Foto. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, puede rechazar datos o propagar errores.

### AccountActivity.subirFoto
- Contrato: `encoded: Text --> subirFoto() → sin valor de retorno`.
- Responsabilidad: Subir Foto. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz.

### AccountActivity.preferences
- Contrato: `context: androidcontentContext, session: SessionManager --> preferences() → SharedPreferences`.
- Responsabilidad: Preferences. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountActivity.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountActivity.onStart
- Contrato: `onStart() → sin valor de retorno`.
- Responsabilidad: On Start. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountActivity.onSaveInstanceState
- Contrato: `state: Bundle --> onSaveInstanceState() → sin valor de retorno`.
- Responsabilidad: On Save Instance State. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountActivity.showPhoto
- Contrato: `showPhoto() → sin valor de retorno`.
- Responsabilidad: Show Photo. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountActivity.mostrarFoto
- Contrato: `view: ImageView, foto: Text --> mostrarFoto() → sin valor de retorno`.
- Responsabilidad: Mostrar Foto. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountActivity.save
- Contrato: `save() → sin valor de retorno`.
- Responsabilidad: Save. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, puede rechazar datos o propagar errores.

### AccountActivity.security
- Contrato: `security() → sin valor de retorno`.
- Responsabilidad: Security. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### AccountActivity.changePassword
- Contrato: `current: Text, next: Text, confirm: Text --> changePassword() → sin valor de retorno`.
- Responsabilidad: Change Password. Edita datos locales y sincroniza la fotografía de la cuenta.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, navegación de pantalla.

## src/android/app/src/main/java/com/example/ozora/senda/ui/BarChartView.java

### BarChartView.BarChartView
- Contrato: `context: Context, attrs: AttributeSet --> BarChartView() → BarChartView`.
- Responsabilidad: Construye BarChartView. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BarChartView.setData
- Contrato: `values: [Dato], labels: [Dato], highlightLast: B, emptyMessage: Text --> setData() → sin valor de retorno`.
- Responsabilidad: Set Data. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BarChartView.onDraw
- Contrato: `canvas: Canvas --> onDraw() → sin valor de retorno`.
- Responsabilidad: On Draw. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/FieldScreen.java

### FieldScreen.aviso
- Contrato: `texto: Text, correcto: B --> aviso() → sin valor de retorno`.
- Responsabilidad: Aviso. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.cerrarAviso
- Contrato: `cerrarAviso() → sin valor de retorno`.
- Responsabilidad: Cerrar Aviso. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.onStop
- Contrato: `onStop() → sin valor de retorno`.
- Responsabilidad: On Stop. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.inter
- Contrato: `context: androidcontentContext, bold: B --> inter() → Typeface`.
- Responsabilidad: Inter. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.dp
- Contrato: `n: Z --> dp() → Z`.
- Responsabilidad: Dp. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.protegerBarras
- Contrato: `root: View --> protegerBarras() → sin valor de retorno`.
- Responsabilidad: Proteger Barras. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.pagina
- Contrato: `titulo: Text --> pagina() → sin valor de retorno`.
- Responsabilidad: Pagina. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.texto
- Contrato: `valor: Text, tam: Z, negrita: B --> texto() → Text`.
- Responsabilidad: Texto. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.tarjeta
- Contrato: `titulo: Text, tam: Z --> tarjeta() → Text`.
- Responsabilidad: Tarjeta. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.boton
- Contrato: `label: Text, click: Runnable --> boton() → Button`.
- Responsabilidad: Boton. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.campo
- Contrato: `hint: Text, tipo: Z --> campo() → Text`.
- Responsabilidad: Campo. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.mensajes
- Contrato: `mensajes() → sin valor de retorno`.
- Responsabilidad: Mensajes. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### FieldScreen.mensaje
- Contrato: `s: Text --> mensaje() → sin valor de retorno`.
- Responsabilidad: Mensaje. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/HistoryScreen.java

### HistoryScreen.HistoryScreen
- Contrato: `activity: Activity, root: View, session: SessionManager --> HistoryScreen() → HistoryScreen`.
- Responsabilidad: Construye HistoryScreen. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HistoryScreen.close
- Contrato: `close() → sin valor de retorno`.
- Responsabilidad: Close. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HistoryScreen.chart
- Contrato: `chart() → sin valor de retorno`.
- Responsabilidad: Chart. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, ejecución delegada.

### HistoryScreen.reset
- Contrato: `reset() → sin valor de retorno`.
- Responsabilidad: Reset. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HistoryScreen.text
- Contrato: `id: Z --> text() → Text`.
- Responsabilidad: Text. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HistoryScreen.valid
- Contrato: `request: Z --> valid() → B`.
- Responsabilidad: Valid. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HistoryScreen.load
- Contrato: `targetPage: Z --> load() → sin valor de retorno`.
- Responsabilidad: Load. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, ejecución delegada.

### HistoryScreen.finishLoading
- Contrato: `finishLoading() → sin valor de retorno`.
- Responsabilidad: Finish Loading. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HistoryScreen.dp
- Contrato: `value: Z --> dp() → Z`.
- Responsabilidad: Dp. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HistoryScreen.card
- Contrato: `list: LinearLayout, reading: Reading --> card() → sin valor de retorno`.
- Responsabilidad: Card. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HistoryScreen.label
- Contrato: `card: LinearLayout, value: Text, size: Z, bold: B --> label() → Text`.
- Responsabilidad: Label. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Reading.Reading
- Contrato: `ppb: Z, date: Text, sensor: Text, status: Text, error: Text --> Reading() → Reading`.
- Responsabilidad: Construye Reading. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/HomeMapController.java

### HomeMapController.HomeMapController
- Contrato: `activity: Activity, root: View, session: SessionManager, enabled: B, week: B --> HomeMapController() → HomeMapController`.
- Responsabilidad: Construye HomeMapController. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.HomeMapController
- Contrato: `activity: Activity, root: View, session: SessionManager, enabled: B, week: B, backdrop: B --> HomeMapController() → HomeMapController`.
- Responsabilidad: Construye HomeMapController. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### HomeMapController.onPageFinished
- Contrato: `view: WebView, url: Text --> onPageFinished() → sin valor de retorno`.
- Responsabilidad: On Page Finished. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.onReceivedError
- Contrato: `view: WebView, request: WebResourceRequest, error: WebResourceError --> onReceivedError() → sin valor de retorno`.
- Responsabilidad: On Received Error. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.shouldOverrideUrlLoading
- Contrato: `view: WebView, request: WebResourceRequest --> shouldOverrideUrlLoading() → B`.
- Responsabilidad: Should Override Url Loading. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### HomeMapController.updateMapInset
- Contrato: `updateMapInset() → sin valor de retorno`.
- Responsabilidad: Update Map Inset. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.mapHtml
- Contrato: `activity: Activity, file: Text --> mapHtml() → Text`.
- Responsabilidad: Map Html. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.asset
- Contrato: `activity: Activity, file: Text --> asset() → Text`.
- Responsabilidad: Asset. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.heatEnabled
- Contrato: `heatEnabled() → B`.
- Responsabilidad: Heat Enabled. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.weekSelected
- Contrato: `weekSelected() → B`.
- Responsabilidad: Week Selected. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.start
- Contrato: `start() → sin valor de retorno`.
- Responsabilidad: Start. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.stop
- Contrato: `stop() → sin valor de retorno`.
- Responsabilidad: Stop. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.destroy
- Contrato: `destroy() → sin valor de retorno`.
- Responsabilidad: Destroy. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.recenter
- Contrato: `recenter() → sin valor de retorno`.
- Responsabilidad: Recenter. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.alRecibirUbicacion
- Contrato: `position: Location --> alRecibirUbicacion() → sin valor de retorno`.
- Responsabilidad: Al Recibir Ubicacion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.alOcurrirError
- Contrato: `error: Text --> alOcurrirError() → sin valor de retorno`.
- Responsabilidad: Al Ocurrir Error. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.position
- Contrato: `recenter: B --> position() → sin valor de retorno`.
- Responsabilidad: Position. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.updateControls
- Contrato: `updateControls() → sin valor de retorno`.
- Responsabilidad: Update Controls. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.refresh
- Contrato: `force: B --> refresh() → sin valor de retorno`.
- Responsabilidad: Refresh. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: retorno al hilo de interfaz, ejecución delegada.

### HomeMapController.render
- Contrato: `render() → sin valor de retorno`.
- Responsabilidad: Render. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.showStatus
- Contrato: `showStatus() → sin valor de retorno`.
- Responsabilidad: Show Status. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### HomeMapController.status
- Contrato: `message: Text --> status() → sin valor de retorno`.
- Responsabilidad: Status. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/KeyringScreen.java

### KeyringScreen.KeyringScreen
- Contrato: `root: View --> KeyringScreen() → KeyringScreen`.
- Responsabilidad: Construye KeyringScreen. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### KeyringScreen.text
- Contrato: `id: Z, value: Text --> text() → sin valor de retorno`.
- Responsabilidad: Text. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### KeyringScreen.color
- Contrato: `id: Z --> color() → Z`.
- Responsabilidad: Color. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### KeyringScreen.refresh
- Contrato: `session: SessionManager, routes: RouteManager, prefs: SharedPreferences, sensor: Text, active: B, receiving: B, state: Text --> refresh() → sin valor de retorno`.
- Responsabilidad: Refresh. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### KeyringScreen.cuando
- Contrato: `instant: Z --> cuando() → Text`.
- Responsabilidad: Cuando. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/KeyringStatus.java

### KeyringStatus.KeyringStatus
- Contrato: `KeyringStatus() → KeyringStatus`.
- Responsabilidad: Construye KeyringStatus. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### KeyringStatus.receiving
- Contrato: `active: B, route: Z, captureRoute: Z, lastAdvertisement: Z, now: Z, timeoutSeconds: Z --> receiving() → B`.
- Responsabilidad: Receiving. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; estática.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/PermissionsActivity.java

### PermissionsActivity.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PermissionsActivity.onResume
- Contrato: `onResume() → sin valor de retorno`.
- Responsabilidad: On Resume. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PermissionsActivity.granted
- Contrato: `permission: Text --> granted() → B`.
- Responsabilidad: Granted. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PermissionsActivity.gps
- Contrato: `gps() → B`.
- Responsabilidad: Gps. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PermissionsActivity.text
- Contrato: `id: Z, value: Text --> text() → sin valor de retorno`.
- Responsabilidad: Text. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PermissionsActivity.refresh
- Contrato: `refresh() → sin valor de retorno`.
- Responsabilidad: Refresh. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PermissionsActivity.enable
- Contrato: `enable() → sin valor de retorno`.
- Responsabilidad: Enable. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PermissionsActivity.system
- Contrato: `system() → sin valor de retorno`.
- Responsabilidad: System. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PermissionsActivity.open
- Contrato: `intent: Intent --> open() → sin valor de retorno`.
- Responsabilidad: Open. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### PermissionsActivity.onRequestPermissionsResult
- Contrato: `code: Z, permissions: [Dato], results: [Dato] --> onRequestPermissionsResult() → sin valor de retorno`.
- Responsabilidad: On Request Permissions Result. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/RouteCardView.java

### RouteCardView.RouteCardView
- Contrato: `context: Context --> RouteCardView() → RouteCardView`.
- Responsabilidad: Construye RouteCardView. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteCardView.bind
- Contrato: `id: Z, start: Z, end: Long, meters: R, count: Z, points: [Dato] --> bind() → sin valor de retorno`.
- Responsabilidad: Bind. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteCardView.setPoints
- Contrato: `points: [Dato] --> setPoints() → sin valor de retorno`.
- Responsabilidad: Set Points. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteCardView.update
- Contrato: `route: Route, now: Z --> update() → sin valor de retorno`.
- Responsabilidad: Update. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteCardView.update
- Contrato: `end: Long, meters: R, count: Z, now: Z --> update() → sin valor de retorno`.
- Responsabilidad: Update. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/RouteMap.java

### RouteMap.RouteMap
- Contrato: `activity: Activity, web: WebView, status: Text, active: B --> RouteMap() → RouteMap`.
- Responsabilidad: Construye RouteMap. Representa el trazado y conserva la interacción táctil del mapa.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### RouteMap.onPageFinished
- Contrato: `view: WebView, url: Text --> onPageFinished() → sin valor de retorno`.
- Responsabilidad: On Page Finished. Representa el trazado y conserva la interacción táctil del mapa.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteMap.shouldOverrideUrlLoading
- Contrato: `view: WebView, request: WebResourceRequest --> shouldOverrideUrlLoading() → B`.
- Responsabilidad: Should Override Url Loading. Representa el trazado y conserva la interacción táctil del mapa.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### RouteMap.update
- Contrato: `points: [Dato], readings: JSONArray, top: Z, bottom: Z --> update() → sin valor de retorno`.
- Responsabilidad: Update. Representa el trazado y conserva la interacción táctil del mapa.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteMap.render
- Contrato: `render() → sin valor de retorno`.
- Responsabilidad: Render. Representa el trazado y conserva la interacción táctil del mapa.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteMap.checkTiles
- Contrato: `checkTiles() → sin valor de retorno`.
- Responsabilidad: Check Tiles. Representa el trazado y conserva la interacción táctil del mapa.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteMap.showStatus
- Contrato: `showStatus() → sin valor de retorno`.
- Responsabilidad: Show Status. Representa el trazado y conserva la interacción táctil del mapa.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteMap.destroy
- Contrato: `destroy() → sin valor de retorno`.
- Responsabilidad: Destroy. Representa el trazado y conserva la interacción táctil del mapa.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/RoutePreviewView.java

### RoutePreviewView.RoutePreviewView
- Contrato: `context: Context, attrs: AttributeSet --> RoutePreviewView() → RoutePreviewView`.
- Responsabilidad: Construye RoutePreviewView. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RoutePreviewView.setSeed
- Contrato: `value: Z --> setSeed() → sin valor de retorno`.
- Responsabilidad: Set Seed. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RoutePreviewView.setPoints
- Contrato: `route: [Dato] --> setPoints() → sin valor de retorno`.
- Responsabilidad: Set Points. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RoutePreviewView.onDraw
- Contrato: `canvas: Canvas --> onDraw() → sin valor de retorno`.
- Responsabilidad: On Draw. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/RouteScreen.java

### RouteScreen.column
- Contrato: `column() → LinearLayout`.
- Responsabilidad: Column. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteScreen.label
- Contrato: `value: CharSequence, size: Z, bold: B --> label() → Text`.
- Responsabilidad: Label. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteScreen.backButton
- Contrato: `backButton() → MaterialButton`.
- Responsabilidad: Back Button. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteScreen.card
- Contrato: `view: View --> card() → sin valor de retorno`.
- Responsabilidad: Card. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteScreen.valueWithUnit
- Contrato: `value: Text, unit: Text --> valueWithUnit() → CharSequence`.
- Responsabilidad: Value With Unit. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteScreen.chip
- Contrato: `value: CharSequence, tone: Z, surface: Z, icon: Z --> chip() → Text`.
- Responsabilidad: Chip. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RouteScreen.addWithTopMargin
- Contrato: `parent: LinearLayout, child: View, margin: Z --> addWithTopMargin() → sin valor de retorno`.
- Responsabilidad: Add With Top Margin. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/SummaryActivity.java

### SummaryActivity.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.buildScreen
- Contrato: `buildScreen() → sin valor de retorno`.
- Responsabilidad: Build Screen. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### SummaryActivity.filter
- Contrato: `title: Z --> filter() → MaterialButton`.
- Responsabilidad: Filter. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.textButton
- Contrato: `textButton() → MaterialButton`.
- Responsabilidad: Text Button. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.stat
- Contrato: `parent: LinearLayout, title: Z, margin: Z --> stat() → Text`.
- Responsabilidad: Stat. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.readings
- Contrato: `limit: Z, offset: Z --> readings() → Cursor`.
- Responsabilidad: Readings. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.refreshReadings
- Contrato: `refreshReadings() → sin valor de retorno`.
- Responsabilidad: Refresh Readings. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.appendRows
- Contrato: `limit: Z --> appendRows() → sin valor de retorno`.
- Responsabilidad: Append Rows. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.refreshMap
- Contrato: `refreshMap() → sin valor de retorno`.
- Responsabilidad: Refresh Map. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.value
- Contrato: `ppb: Z --> value() → Text`.
- Responsabilidad: Value. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.readingTime
- Contrato: `utc: Text --> readingTime() → Text`.
- Responsabilidad: Reading Time. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.onStart
- Contrato: `onStart() → sin valor de retorno`.
- Responsabilidad: On Start. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.onSaveInstanceState
- Contrato: `state: Bundle --> onSaveInstanceState() → sin valor de retorno`.
- Responsabilidad: On Save Instance State. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SummaryActivity.onDestroy
- Contrato: `onDestroy() → sin valor de retorno`.
- Responsabilidad: On Destroy. Presenta mapa, estadísticas y lecturas del recorrido.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/main/java/com/example/ozora/senda/ui/WelcomeActivity.java

### WelcomeActivity.onCreate
- Contrato: `state: Bundle --> onCreate() → sin valor de retorno`.
- Responsabilidad: On Create. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### WelcomeActivity.playIntro
- Contrato: `playIntro() → sin valor de retorno`.
- Responsabilidad: Play Intro. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### WelcomeActivity.onAnimationEnd
- Contrato: `a: androidanimationAnimator --> onAnimationEnd() → sin valor de retorno`.
- Responsabilidad: On Animation End. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### WelcomeActivity.finishIntro
- Contrato: `finishIntro() → sin valor de retorno`.
- Responsabilidad: Finish Intro. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### WelcomeActivity.enter
- Contrato: `enter() → sin valor de retorno`.
- Responsabilidad: Enter. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: navegación de pantalla.

### WelcomeActivity.onPause
- Contrato: `onPause() → sin valor de retorno`.
- Responsabilidad: On Pause. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### WelcomeActivity.onResume
- Contrato: `onResume() → sin valor de retorno`.
- Responsabilidad: On Resume. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### WelcomeActivity.onDestroy
- Contrato: `onDestroy() → sin valor de retorno`.
- Responsabilidad: On Destroy. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Intro.Intro
- Contrato: `context: Context --> Intro() → Intro`.
- Responsabilidad: Construye Intro. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Intro.onDraw
- Contrato: `canvas: Canvas --> onDraw() → sin valor de retorno`.
- Responsabilidad: On Draw. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/ExampleUnitTest.java

### ExampleUnitTest.addition_isCorrect
- Contrato: `addition_isCorrect() → sin valor de retorno`.
- Responsabilidad: Addition is Correct. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/auth/RecoveryTokenTest.java

### RecoveryTokenTest.aceptaTokenCompletoYEnlaceDelServidor
- Contrato: `aceptaTokenCompletoYEnlaceDelServidor() → sin valor de retorno`.
- Responsabilidad: Acepta Token Completo YEnlace Del Servidor. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryTokenTest.rechazaCodigosCortosYFormatosAjenos
- Contrato: `rechazaCodigosCortosYFormatosAjenos() → sin valor de retorno`.
- Responsabilidad: Rechaza Codigos Cortos YFormatos Ajenos. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryTokenTest.rechazaTokensAmbiguosYFragmentosMalformados
- Contrato: `rechazaTokensAmbiguosYFragmentosMalformados() → sin valor de retorno`.
- Responsabilidad: Rechaza Tokens Ambiguos YFragmentos Malformados. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RecoveryTokenTest.extraerNoDependeDeContactarConElHost
- Contrato: `extraerNoDependeDeContactarConElHost() → sin valor de retorno`.
- Responsabilidad: Extraer No Depende De Contactar Con El Host. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/auth/SessionManagerTest.java

### SessionManagerTest.limpiar
- Contrato: `limpiar() → sin valor de retorno`.
- Responsabilidad: Limpiar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SessionManagerTest.requiereCredencialYTrabajadorReal
- Contrato: `requiereCredencialYTrabajadorReal() → sin valor de retorno`.
- Responsabilidad: Requiere Credencial YTrabajador Real. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SessionManagerTest.caducidadYCierreImpidenReutilizarLaSesion
- Contrato: `caducidadYCierreImpidenReutilizarLaSesion() → sin valor de retorno`.
- Responsabilidad: Caducidad YCierre Impiden Reutilizar La Sesion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### SessionManagerTest.consultarLaSesionConservaLaIdentidadHastaCerrar
- Contrato: `consultarLaSesionConservaLaIdentidadHastaCerrar() → sin valor de retorno`.
- Responsabilidad: Consultar La Sesion Conserva La Identidad Hasta Cerrar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/bluetooth/IBeaconParserTest.java

### IBeaconParserTest.trama
- Contrato: `trama() → [Dato]`.
- Responsabilidad: Trama. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### IBeaconParserTest.conservaIdentidadContadorYUnidades
- Contrato: `conservaIdentidadContadorYUnidades() → sin valor de retorno`.
- Responsabilidad: Conserva Identidad Contador YUnidades. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### IBeaconParserTest.rechazaUuidFormatoYLongitudAjenos
- Contrato: `rechazaUuidFormatoYLongitudAjenos() → sin valor de retorno`.
- Responsabilidad: Rechaza Uuid Formato YLongitud Ajenos. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### IBeaconParserTest.noInventaNombreNiConvierteOtroTipoEnOzono
- Contrato: `noInventaNombreNiConvierteOtroTipoEnOzono() → sin valor de retorno`.
- Responsabilidad: No Inventa Nombre Ni Convierte Otro Tipo En Ozono. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/bluetooth/TelemetriaTest.java

### TelemetriaTest.trama
- Contrato: `tipo: Z, valor: Z, nombre: Text --> trama() → IBeaconParserResultado`.
- Responsabilidad: Trama. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### TelemetriaTest.majorDirectoYUnidades
- Contrato: `majorDirectoYUnidades() → sin valor de retorno`.
- Responsabilidad: Major Directo YUnidades. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### TelemetriaTest.recibeLaSecuenciaRealDelMicroConContador
- Contrato: `recibeLaSecuenciaRealDelMicroConContador() → sin valor de retorno`.
- Responsabilidad: Recibe La Secuencia Real Del Micro Con Contador. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### TelemetriaTest.valoresDesconocidosYLimites
- Contrato: `valoresDesconocidosYLimites() → sin valor de retorno`.
- Responsabilidad: Valores Desconocidos YLimites. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### TelemetriaTest.caducaCadaLecturaYSeparaLlaveros
- Contrato: `caducaCadaLecturaYSeparaLlaveros() → sin valor de retorno`.
- Responsabilidad: Caduca Cada Lectura YSepara Llaveros. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/integration/ControlContadoresTest.java

### ControlContadoresTest.rechazaAnuncioInmediato
- Contrato: `rechazaAnuncioInmediato() → sin valor de retorno`.
- Responsabilidad: Rechaza Anuncio Inmediato. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ControlContadoresTest.permiteVueltaYReinicio
- Contrato: `permiteVueltaYReinicio() → sin valor de retorno`.
- Responsabilidad: Permite Vuelta YReinicio. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ControlContadoresTest.separaDispositivos
- Contrato: `separaDispositivos() → sin valor de retorno`.
- Responsabilidad: Separa Dispositivos. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/integration/ControlLecturasSinContadorTest.java

### ControlLecturasSinContadorTest.descartaRafagaPeroAceptaLaSiguienteAunqueElValorSeaIgual
- Contrato: `descartaRafagaPeroAceptaLaSiguienteAunqueElValorSeaIgual() → sin valor de retorno`.
- Responsabilidad: Descarta Rafaga Pero Acepta La Siguiente Aunque El Valor Sea Igual. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ControlLecturasSinContadorTest.contadorLocalValidoParaContrato
- Contrato: `contadorLocalValidoParaContrato() → sin valor de retorno`.
- Responsabilidad: Contador Local Valido Para Contrato. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/model/MedicionAmbientalTest.java

### MedicionAmbientalTest.convierteAConcentracionPpm
- Contrato: `convierteAConcentracionPpm() → sin valor de retorno`.
- Responsabilidad: Convierte AConcentracion Ppm. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/network/ApiContractTest.java

### ApiContractTest.entrar
- Contrato: `api: ApiClient, correo: Text, clave: Text --> entrar() → JSONObject`.
- Responsabilidad: Usa exactamente el transporte HTTP de Android sin imprimir tokens.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ApiContractTest.trabajadorEnviaYJefeRecibe
- Contrato: `trabajadorEnviaYJefeRecibe() → sin valor de retorno`.
- Responsabilidad: Solo se ejecuta con la fixture local explícita; las cuentas son distintas por variante.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: borrado o desvinculación.

### ApiContractTest.releaseRechazaHttp
- Contrato: `releaseRechazaHttp() → sin valor de retorno`.
- Responsabilidad: La excepción de HTTP local requiere explícitamente depuración.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/network/LogicaFakePayloadTest.java

### LogicaFakePayloadTest.temperaturaSeEnviaEnGradosConSignoYCoordenadas
- Contrato: `temperaturaSeEnviaEnGradosConSignoYCoordenadas() → sin valor de retorno`.
- Responsabilidad: Temperatura Se Envia En Grados Con Signo YCoordenadas. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakePayloadTest.noEnviaTemperaturaNoDisponible
- Contrato: `noEnviaTemperaturaNoDisponible() → sin valor de retorno`.
- Responsabilidad: No Envia Temperatura No Disponible. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakePayloadTest.rechazaTemperaturaSinCoordenadasValidas
- Contrato: `rechazaTemperaturaSinCoordenadasValidas() → sin valor de retorno`.
- Responsabilidad: Rechaza Temperatura Sin Coordenadas Validas. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakePayloadTest.contratoConservaFechaUnidadYPrecision
- Contrato: `contratoConservaFechaUnidadYPrecision() → sin valor de retorno`.
- Responsabilidad: Comprueba el contrato real, sin contador ni precisión GPS adicionales.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakePayloadTest.rechazaCoordenadaNoFinita
- Contrato: `rechazaCoordenadaNoFinita() → sin valor de retorno`.
- Responsabilidad: No deja construir JSON con valores GPS no representables.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/network/LogicaFakeTest.java

### LogicaFakeTest.preparar
- Contrato: `preparar() → sin valor de retorno`.
- Responsabilidad: Preparar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakeTest.cerrar
- Contrato: `cerrar() → sin valor de retorno`.
- Responsabilidad: Cerrar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakeTest.linea
- Contrato: `entrada: BufferedInputStream --> linea() → Text`.
- Responsabilidad: Linea. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakeTest.historialAutenticadoConservaCursorSinAlterarlo
- Contrato: `historialAutenticadoConservaCursorSinAlterarlo() → sin valor de retorno`.
- Responsabilidad: Historial Autenticado Conserva Cursor Sin Alterarlo. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakeTest.guardarMedicionConservaContratoYConfirmacion
- Contrato: `guardarMedicionConservaContratoYConfirmacion() → sin valor de retorno`.
- Responsabilidad: Guardar Medicion Conserva Contrato YConfirmacion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakeTest.sesionYRecuperacionUsanLasRutasYCredencialesAdecuadas
- Contrato: `sesionYRecuperacionUsanLasRutasYCredencialesAdecuadas() → sin valor de retorno`.
- Responsabilidad: Sesion YRecuperacion Usan Las Rutas YCredenciales Adecuadas. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: borrado o desvinculación.

### LogicaFakeTest.propagaErroresDelServidorSinConfirmarGuardado
- Contrato: `propagaErroresDelServidorSinConfirmarGuardado() → sin valor de retorno`.
- Responsabilidad: Propaga Errores Del Servidor Sin Confirmar Guardado. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakeTest.requiereConfirmacionExplicitaAntesDeDarPorGuardadaUnaLectura
- Contrato: `requiereConfirmacionExplicitaAntesDeDarPorGuardadaUnaLectura() → sin valor de retorno`.
- Responsabilidad: Requiere Confirmacion Explicita Antes De Dar Por Guardada Una Lectura. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakeTest.perfilYLlaveroUsanCuentaAutenticada
- Contrato: `perfilYLlaveroUsanCuentaAutenticada() → sin valor de retorno`.
- Responsabilidad: Perfil YLlavero Usan Cuenta Autenticada. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: borrado o desvinculación.

### LogicaFakeTest.temperaturaUsaElEndpointAutenticadoYExigeConfirmacion
- Contrato: `temperaturaUsaElEndpointAutenticadoYExigeConfirmacion() → sin valor de retorno`.
- Responsabilidad: Temperatura Usa El Endpoint Autenticado YExige Confirmacion. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFakeTest.comprobar
- Contrato: `esperado: Text, destino: Text, token: Text --> comprobar() → sin valor de retorno`.
- Responsabilidad: Comprobar. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/network/RecoveryContractTest.java

### RecoveryContractTest.responder
- Contrato: `server: ServerSocket, status: Z, response: Text --> responder() → Text`.
- Responsabilidad: Responder. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### RecoveryContractTest.solicitudAceptadaYLimiteDeIntentos
- Contrato: `solicitudAceptadaYLimiteDeIntentos() → sin valor de retorno`.
- Responsabilidad: Solicitud Aceptada YLimite De Intentos. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/qr/QrLlaveroParserTest.java

### QrLlaveroParserTest.permiteCodigosSenda
- Contrato: `permiteCodigosSenda() → sin valor de retorno`.
- Responsabilidad: Permite Codigos Senda. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### QrLlaveroParserTest.rechazaOtrosCodigos
- Contrato: `rechazaOtrosCodigos() → sin valor de retorno`.
- Responsabilidad: Rechaza Otros Codigos. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/routes/RoutePeriodTest.java

### RoutePeriodTest.time
- Contrato: `date: Text --> time() → Z`.
- Responsabilidad: Time. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RoutePeriodTest.semanaEmpiezaElLunesYSieteDiasIncluyeHoy
- Contrato: `semanaEmpiezaElLunesYSieteDiasIncluyeHoy() → sin valor de retorno`.
- Responsabilidad: Semana Empieza El Lunes YSiete Dias Incluye Hoy. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### RoutePeriodTest.cambioDeHoraMantieneLosDiasDelCalendario
- Contrato: `cambioDeHoraMantieneLosDiasDelCalendario() → sin valor de retorno`.
- Responsabilidad: Cambio De Hora Mantiene Los Dias Del Calendario. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/android/app/src/test/java/com/example/ozora/senda/ui/KeyringStatusTest.java

### KeyringStatusTest.requiereRecorridoYAnuncioDelServicioActual
- Contrato: `requiereRecorridoYAnuncioDelServicioActual() → sin valor de retorno`.
- Responsabilidad: Requiere Recorrido YAnuncio Del Servicio Actual. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### KeyringStatusTest.elSilencioCaducaEnElLimiteYDescartaRelojesIncompatibles
- Contrato: `elSilencioCaducaEnElLimiteYDescartaRelojesIncompatibles() → sin valor de retorno`.
- Responsabilidad: El Silencio Caduca En El Limite YDescarta Relojes Incompatibles. Aplicación de campo: acceso, captura, recorridos y presentación Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/business_logic/Apoyo.php

### ErrorDominio.__construct
- Contrato: `codigo: Text, mensaje: Text, campos: [Dato] --> __construct() → Dato`.
- Responsabilidad: Construye ErrorDominio. Reglas de negocio, autorización y persistencia de SENDA.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Apoyo.configuracion
- Contrato: `configuracion() → [Dato]`.
- Responsabilidad: Lee la configuración privada local, nunca servida por HTTP.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### Apoyo.conectar
- Contrato: `config: [Dato] --> conectar() → PDO`.
- Responsabilidad: Abre MySQL con parámetros reales y fechas UTC.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Apoyo.exigirCampos
- Contrato: `datos: [Dato], campos: [Dato] --> exigirCampos() → sin valor de retorno`.
- Responsabilidad: Rechaza campos ausentes y desconocidos para evitar contratos ambiguos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### Apoyo.validarCorreo
- Contrato: `valor: Dato --> validarCorreo() → Text`.
- Responsabilidad: Valida el correo ASCII y respeta el tamaño del esquema del equipo.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### Apoyo.validarContrasena
- Contrato: `valor: Dato --> validarContrasena() → Text`.
- Responsabilidad: Impide contraseñas demasiado cortas o truncadas por bcrypt.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### Apoyo.fechaJson
- Contrato: `fecha: Text --> fechaJson() → Text`.
- Responsabilidad: Conserva los milisegundos al responder a Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Apoyo.validarMedicion
- Contrato: `datos: [Dato] --> validarMedicion() → [Dato]`.
- Responsabilidad: Valida una captura geolocalizada O3 sin inventar ubicación ni unidad.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

## src/business_logic/Correo.php

### Correo.entregarCorreos
- Contrato: `db: PDO, config: [Dato] --> entregarCorreos() → Z`.
- Responsabilidad: Envía pendientes sin exponer tokens por la API; conserva fallos para reintentar.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente, unidad transaccional, ejecución delegada, puede rechazar datos o propagar errores.

## src/business_logic/LogicaNegocio.php

### LogicaNegocio.__construct
- Contrato: `db: PDO, config: [Dato] --> __construct() → Dato`.
- Responsabilidad: Comparte una conexión y configuración entre operaciones.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaNegocio.consulta
- Contrato: `sql: Text, parametros: [Dato] --> consulta() → PDOStatement`.
- Responsabilidad: Ejecuta consultas preparadas, sin concatenar datos del usuario.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: ejecución delegada.

### LogicaNegocio.usuarioPublico
- Contrato: `usuario: [Dato] --> usuarioPublico() → [Dato]`.
- Responsabilidad: Selecciona solo los campos que se pueden enviar al cliente.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaNegocio.limitar
- Contrato: `operacion: Text, ip: Text, correo: Text --> limitar() → sin valor de retorno`.
- Responsabilidad: Cuenta intentos en una ventana de 15 minutos de forma atómica.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, actualización persistente, puede rechazar datos o propagar errores.

### LogicaNegocio.iniciarSesion
- Contrato: `datos: [Dato] --> iniciarSesion() → [Dato]`.
- Responsabilidad: Verifica la cuenta activa y crea una sesión opaca de duración limitada.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, actualización persistente, unidad transaccional, puede rechazar datos o propagar errores.

### LogicaNegocio.autenticar
- Contrato: `token: Text --> autenticar() → [Dato]`.
- Responsabilidad: Comprueba caducidad, cuenta y organización en cada petición.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### LogicaNegocio.consultarSesion
- Contrato: `usuario: [Dato] --> consultarSesion() → [Dato]`.
- Responsabilidad: Devuelve el perfil actual sin volver a publicar el token.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaNegocio.cerrarSesion
- Contrato: `usuario: [Dato] --> cerrarSesion() → [Dato]`.
- Responsabilidad: Elimina solo la sesión presentada por el cliente.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: borrado o desvinculación.

### LogicaNegocio.exigirRol
- Contrato: `usuario: [Dato], rol: Text --> exigirRol() → sin valor de retorno`.
- Responsabilidad: Restringe operaciones de jefe o trabajador antes de consultar recursos.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### LogicaNegocio.actualizarFotoPerfil
- Contrato: `usuario: [Dato], datos: [Dato] --> actualizarFotoPerfil() → [Dato]`.
- Responsabilidad: Actualizar Foto Perfil. Aplica reglas de cuentas, asignación, mediciones y recuperación.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente, puede rechazar datos o propagar errores.

### LogicaNegocio.vincularLlavero
- Contrato: `usuario: [Dato], datos: [Dato] --> vincularLlavero() → [Dato]`.
- Responsabilidad: Vincular Llavero. Aplica reglas de cuentas, asignación, mediciones y recuperación.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente, unidad transaccional, puede rechazar datos o propagar errores.

### LogicaNegocio.desvincularLlavero
- Contrato: `usuario: [Dato] --> desvincularLlavero() → [Dato]`.
- Responsabilidad: Desvincular Llavero. Aplica reglas de cuentas, asignación, mediciones y recuperación.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente, unidad transaccional, puede rechazar datos o propagar errores.

### LogicaNegocio.guardarMedicion
- Contrato: `usuario: [Dato], datos: [Dato] --> guardarMedicion() → [Dato]`.
- Responsabilidad: Autoriza el llavero y guarda una sola vez la captura en su parque histórico.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, actualización persistente, unidad transaccional, puede rechazar datos o propagar errores.

### LogicaNegocio.historialMediciones
- Contrato: `usuario: [Dato], cursor: Dato --> historialMediciones() → [Dato]`.
- Responsabilidad: Historial Mediciones. Aplica reglas de cuentas, asignación, mediciones y recuperación.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### LogicaNegocio.ultimaMedicion
- Contrato: `usuario: [Dato], parque: Z --> ultimaMedicion() → [Dato]`.
- Responsabilidad: Consulta por fecha de captura entre todos los llaveros del parque autorizado.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### LogicaNegocio.prepararEnlace
- Contrato: `usuario: [Dato], finalidad: Text --> prepararEnlace() → sin valor de retorno`.
- Responsabilidad: Guarda el hash y encola un correo dentro de la transacción del llamador.
- Visibilidad: interna; de instancia / función de módulo.
- Efectos detectados: inserción persistente.

### LogicaNegocio.registrarTrabajador
- Contrato: `usuario: [Dato], datos: [Dato] --> registrarTrabajador() → [Dato]`.
- Responsabilidad: Guarda el hash inicial y exige al trabajador cambiarlo antes de operar.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, unidad transaccional, puede rechazar datos o propagar errores.

### LogicaNegocio.cambiarContrasena
- Contrato: `usuario: [Dato], datos: [Dato] --> cambiarContrasena() → [Dato]`.
- Responsabilidad: Cambia la contraseña propia, elimina la obligación inicial y revoca sesiones y enlaces.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente, borrado o desvinculación, unidad transaccional, puede rechazar datos o propagar errores.

### LogicaNegocio.solicitarRecuperacion
- Contrato: `datos: [Dato] --> solicitarRecuperacion() → [Dato]`.
- Responsabilidad: Encola el correo solo para una cuenta activa sin revelar si existe.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente, unidad transaccional, puede rechazar datos o propagar errores.

### LogicaNegocio.registrarSolicitud
- Contrato: `datos: [Dato] --> registrarSolicitud() → [Dato]`.
- Responsabilidad: Guarda una solicitud pública sin crear una cuenta ni enviar correos en su nombre.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, puede rechazar datos o propagar errores.

### LogicaNegocio.restablecerContrasena
- Contrato: `datos: [Dato] --> restablecerContrasena() → [Dato]`.
- Responsabilidad: Actualiza contraseña, consume enlaces y revoca sesiones de forma atómica.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente, borrado o desvinculación, unidad transaccional, puede rechazar datos o propagar errores.

## src/communication/ContextoWeb.php

### ContextoWeb.rutaBaseAplicacion
- Contrato: `rutaBaseAplicacion() → Text`.
- Responsabilidad: Permite instalar SENDA en su propia carpeta, sin cambiar otras webs del dominio.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### ContextoWeb.rutaPeticionAplicacion
- Contrato: `rutaPeticionAplicacion() → Text`.
- Responsabilidad: Retira únicamente el prefijo de esta instalación, respetando los demás proyectos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ErrorApi.__construct
- Contrato: `estado: Z, codigo: Text, mensaje: Text, campos: [Dato] --> __construct() → Dato`.
- Responsabilidad: Construye ErrorApi. Adaptación de entradas y salidas entre clientes y negocio.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ContextoWeb.estadoErrorDominio
- Contrato: `error: ErrorDominio --> estadoErrorDominio() → Z`.
- Responsabilidad: Estado Error Dominio. Resuelve rutas de instalación y adapta errores al transporte.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/communication/ServidorRest.php

### ServidorRest.responderJson
- Contrato: `estado: Z, contenido: [Dato] --> responderJson() → sin valor de retorno`.
- Responsabilidad: Envía JSON uniforme sin permitir que se almacenen credenciales en caché.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### ServidorRest.leerJson
- Contrato: `maximo: Z --> leerJson() → [Dato]`.
- Responsabilidad: Exige JSON pequeño y válido; no acepta listas como cuerpo de una operación.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### ServidorRest.atenderApi
- Contrato: `atenderApi() → sin valor de retorno`.
- Responsabilidad: Despacha solo las rutas conocidas con su método y permisos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: borrado o desvinculación, puede rechazar datos o propagar errores.

## src/frontend_business_logic/LogicaFake.js

### LogicaFake.constructor
- Contrato: `rutaBase: Dato --> constructor() → Dato`.
- Responsabilidad: Construye LogicaFake. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.iniciarSesion
- Contrato: `datos: Dato --> iniciarSesion() → Dato`.
- Responsabilidad: Iniciar Sesion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.consultarSesion
- Contrato: `token: Text --> consultarSesion() → Dato`.
- Responsabilidad: Consultar Sesion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.cerrarSesion
- Contrato: `token: Text --> cerrarSesion() → Dato`.
- Responsabilidad: Cerrar Sesion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: borrado o desvinculación.

### LogicaFake.ultimaMedicion
- Contrato: `token: Text, parque: Dato --> ultimaMedicion() → Dato`.
- Responsabilidad: Ultima Medicion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.registrarTrabajador
- Contrato: `token: Text, datos: Dato --> registrarTrabajador() → Dato`.
- Responsabilidad: Registrar Trabajador. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.cambiarContrasena
- Contrato: `token: Text, datos: Dato --> cambiarContrasena() → Dato`.
- Responsabilidad: Cambiar Contrasena. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.solicitarRecuperacion
- Contrato: `datos: Dato --> solicitarRecuperacion() → Dato`.
- Responsabilidad: Solicitar Recuperacion. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.restablecerContrasena
- Contrato: `datos: Dato --> restablecerContrasena() → Dato`.
- Responsabilidad: Restablecer Contrasena. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.registrarSolicitud
- Contrato: `datos: Dato --> registrarSolicitud() → Dato`.
- Responsabilidad: Registrar Solicitud. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LogicaFake.#pedir
- Contrato: `ruta: Text, metodo: Text, token: Text, datos: Dato --> #pedir() → Dato`.
- Responsabilidad: #pedir. Expone operaciones del negocio y encapsula su invocación remota.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

## src/microprocesador/Bateria.h

### Bateria.medirPorcentaje
- Contrato: `medirPorcentaje() → N`.
- Responsabilidad: Medir Porcentaje. Estima batería solo si existe el divisor externo configurado.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/microprocesador/EmisoraBLE.h

### EmisoraBLE.EmisoraBLE
- Contrato: `nombreEmisora_: Text, fabricanteID_: N, txPower_: Z --> EmisoraBLE() → Dato`.
- Responsabilidad: Construye EmisoraBLE. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EmisoraBLE.encenderEmisora
- Contrato: `encenderEmisora() → sin valor de retorno`.
- Responsabilidad: Encender Emisora. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EmisoraBLE.encenderEmisora
- Contrato: `cbce: CallbackConexionEstablecida, cbct: CallbackConexionTerminada --> encenderEmisora() → sin valor de retorno`.
- Responsabilidad: Encender Emisora. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EmisoraBLE.detenerAnuncio
- Contrato: `detenerAnuncio() → sin valor de retorno`.
- Responsabilidad: Detener Anuncio. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EmisoraBLE.estaAnunciando
- Contrato: `estaAnunciando() → B`.
- Responsabilidad: Esta Anunciando. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EmisoraBLE.emitirAnuncioIBeacon
- Contrato: `beaconUUID: N, major: N, minor: N, rssi: Z --> emitirAnuncioIBeacon() → sin valor de retorno`.
- Responsabilidad: Emitir Anuncio IBeacon. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EmisoraBLE.emitirAnuncioIBeaconLibre
- Contrato: `carga: Text, tamanyoCarga: N --> emitirAnuncioIBeaconLibre() → sin valor de retorno`.
- Responsabilidad: Emitir Anuncio IBeacon Libre. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EmisoraBLE.instalarCallbackConexionEstablecida
- Contrato: `cb: CallbackConexionEstablecida --> instalarCallbackConexionEstablecida() → sin valor de retorno`.
- Responsabilidad: Instalar Callback Conexion Establecida. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EmisoraBLE.instalarCallbackConexionTerminada
- Contrato: `cb: CallbackConexionTerminada --> instalarCallbackConexionTerminada() → sin valor de retorno`.
- Responsabilidad: Instalar Callback Conexion Terminada. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### EmisoraBLE.getConexion
- Contrato: `connHandle: N --> getConexion() → BLEConnection`.
- Responsabilidad: Get Conexion. Encapsula el anuncio iBeacon con Bluefruit.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/microprocesador/LED.h

### LED.esperar
- Contrato: `tiempo: Z --> esperar() → sin valor de retorno`.
- Responsabilidad: Esperar. Controla la indicación luminosa sin lógica de negocio.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LED.LED
- Contrato: `numero: Z --> LED() → Dato`.
- Responsabilidad: Construye LED. Controla la indicación luminosa sin lógica de negocio.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LED.encender
- Contrato: `encender() → sin valor de retorno`.
- Responsabilidad: Encender. Controla la indicación luminosa sin lógica de negocio.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LED.apagar
- Contrato: `apagar() → sin valor de retorno`.
- Responsabilidad: Apagar. Controla la indicación luminosa sin lógica de negocio.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LED.alternar
- Contrato: `alternar() → sin valor de retorno`.
- Responsabilidad: Alternar. Controla la indicación luminosa sin lógica de negocio.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### LED.brillar
- Contrato: `tiempo: Z --> brillar() → sin valor de retorno`.
- Responsabilidad: Brillar. Controla la indicación luminosa sin lógica de negocio.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/microprocesador/Medidor.h

### Medidor.rawAVoltios
- Contrato: `raw: R --> rawAVoltios() → R`.
- Responsabilidad: Raw AVoltios. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Medidor.medidaFakeO3
- Contrato: `medidaFakeO3() → R`.
- Responsabilidad: Medida Fake O3. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Medidor.medidaFakeTemperatura
- Contrato: `medidaFakeTemperatura() → R`.
- Responsabilidad: Medida Fake Temperatura. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Medidor.Medidor
- Contrato: `Medidor() → Dato`.
- Responsabilidad: Construye Medidor. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Medidor.iniciarMedidor
- Contrato: `iniciarMedidor() → sin valor de retorno`.
- Responsabilidad: Iniciar Medidor. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Medidor.medirO3
- Contrato: `medirO3() → R`.
- Responsabilidad: Medir O3. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Medidor.medirTemperatura
- Contrato: `medirTemperatura() → R`.
- Responsabilidad: Medir Temperatura. Adquiere O3 y temperatura reales y ofrece valores ficticios explícitos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/microprocesador/Publicador.h

### Publicador.ozono
- Contrato: `ppm: R --> ozono() → N`.
- Responsabilidad: Ozono. Codifica los tipos y unidades y publica cada ciclo BLE.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Publicador.temperatura
- Contrato: `grados: R --> temperatura() → N`.
- Responsabilidad: Temperatura. Codifica los tipos y unidades y publica cada ciclo BLE.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Publicador.encenderEmisora
- Contrato: `encenderEmisora() → sin valor de retorno`.
- Responsabilidad: Encender Emisora. Codifica los tipos y unidades y publica cada ciclo BLE.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### Publicador.publicarMedida
- Contrato: `tipo: ProtocoloBLETipo, valor: N, contador: N, tiempo: Z --> publicarMedida() → sin valor de retorno`.
- Responsabilidad: Publicar Medida. Codifica los tipos y unidades y publica cada ciclo BLE.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/microprocesador/PuertoSerie.h

### PuertoSerie.PuertoSerie
- Contrato: `baudios: Z --> PuertoSerie() → Dato`.
- Responsabilidad: Construye PuertoSerie. Encapsula el puerto de diagnóstico de la placa.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PuertoSerie.esperarDisponible
- Contrato: `esperarDisponible() → sin valor de retorno`.
- Responsabilidad: Esperar Disponible. Encapsula el puerto de diagnóstico de la placa.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### PuertoSerie.escribir
- Contrato: `mensaje: T --> escribir() → sin valor de retorno`.
- Responsabilidad: Escribir. Encapsula el puerto de diagnóstico de la placa.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/microprocesador/microprocesador.ino

### microprocesador.inicializarPlaquita
- Contrato: `inicializarPlaquita() → sin valor de retorno`.
- Responsabilidad: Inicializar Plaquita. Captura de sensores y publicación de anuncios BLE.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### microprocesador.setup
- Contrato: `setup() → sin valor de retorno`.
- Responsabilidad: Setup. Captura de sensores y publicación de anuncios BLE.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### microprocesador.lucecitas
- Contrato: `lucecitas() → sin valor de retorno`.
- Responsabilidad: Lucecitas. Captura de sensores y publicación de anuncios BLE.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### microprocesador.loop
- Contrato: `loop() → sin valor de retorno`.
- Responsabilidad: Loop. Captura de sensores y publicación de anuncios BLE.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/tests/integracion.php

### integracion.comprobar
- Contrato: `condicion: B, descripcion: Text --> comprobar() → sin valor de retorno`.
- Responsabilidad: Cuenta aserciones y detiene el proceso si una expectativa falla.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### integracion.pedir
- Contrato: `metodo: Text, ruta: Text, datos: Dato, token: Text, esperado: Z, tipo: Text --> pedir() → [Dato]`.
- Responsabilidad: Hace una petición HTTP real y exige estado y formato coherentes.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### integracion.sql
- Contrato: `consulta: Text, parametros: [Dato] --> sql() → PDOStatement`.
- Responsabilidad: Prepara los datos ficticios y verifica las filas realmente guardadas.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: ejecución delegada.

### integracion.tokenDelCorreo
- Contrato: `correo: Text --> tokenDelCorreo() → Text`.
- Responsabilidad: Lee solo el buzón ficticio privado para probar el uso del enlace.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

## src/tests/logica-fake.test.cjs

### logica-fake.test.cliente
- Contrato: `responder: Dato --> cliente() → Dato`.
- Responsabilidad: Cliente. Comprobación automática de contratos, límites y aislamiento.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/tests/perfil-llavero.php

### BasePrueba.__construct
- Contrato: `__construct() → Dato`.
- Responsabilidad: Construye BasePrueba. Comprobación automática de contratos, límites y aislamiento.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### BasePrueba.prepare
- Contrato: `query: Text, options: [Dato] --> prepare() → PDOStatementfalse`.
- Responsabilidad: Prepare. Comprobación automática de contratos, límites y aislamiento.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: actualización persistente.

### perfil-llavero.verificar
- Contrato: `ok: B --> verificar() → sin valor de retorno`.
- Responsabilidad: Verificar. Comprobación automática de contratos, límites y aislamiento.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### perfil-llavero.rechazo
- Contrato: `operacion: Closure, estado: Z --> rechazo() → sin valor de retorno`.
- Responsabilidad: Rechazo. Comprobación automática de contratos, límites y aislamiento.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

## src/tests/validacion.php

### validacion.comprobar
- Contrato: `condicion: B, descripcion: Text --> comprobar() → sin valor de retorno`.
- Responsabilidad: Cuenta verificaciones y detiene una prueba que no cumple el resultado esperado.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### validacion.esperarRechazo
- Contrato: `lectura: [Dato] --> esperarRechazo() → sin valor de retorno`.
- Responsabilidad: Exige que una entrada incorrecta produzca validación 422.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

## src/tools/bbdd/index.php

### index.escapar
- Contrato: `valor: Dato --> escapar() → Text`.
- Responsabilidad: Presenta los datos como texto, sin interpretar contenido guardado.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/tools/enviar-demo.php

### enviar-demo.enviarDemo
- Contrato: `ruta: Text, datos: [Dato], token: Text --> enviarDemo() → [Dato]`.
- Responsabilidad: Publica JSON en la URL local y comprueba la confirmación del servidor.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

## src/tools/gestionar.php

### gestionar.instalarEsquema
- Contrato: `db: PDO --> instalarEsquema() → sin valor de retorno`.
- Responsabilidad: Importa tablas solo en una base vacía; nunca elimina tablas existentes.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### gestionar.actualizarContacto
- Contrato: `db: PDO --> actualizarContacto() → sin valor de retorno`.
- Responsabilidad: Añade la tabla de contacto a una instalación anterior sin borrar ni alterar datos.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### gestionar.actualizarContrasenas
- Contrato: `db: PDO --> actualizarContrasenas() → sin valor de retorno`.
- Responsabilidad: Añade el estado de contraseña inicial sin modificar las contraseñas existentes.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### gestionar.actualizarRoles
- Contrato: `db: PDO --> actualizarRoles() → sin valor de retorno`.
- Responsabilidad: Migra una instalación anterior; no repite una migración completada o parcial.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### gestionar.crearDemo
- Contrato: `db: PDO, config: [Dato] --> crearDemo() → sin valor de retorno`.
- Responsabilidad: Añade un parque ficticio y dos cuentas con contraseña suministrada por entorno.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: inserción persistente, unidad transaccional, ejecución delegada, puede rechazar datos o propagar errores.

## src/web/js/app.js

### app.elemento
- Contrato: `id: Dato --> elemento() → Dato`.
- Responsabilidad: Localiza un control conocido de la página.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.mensaje
- Contrato: `texto: Text, error: Dato, destino: Dato --> mensaje() → Dato`.
- Responsabilidad: Informa del resultado con texto seguro y aviso accesible.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.mostrar
- Contrato: `seccion: Dato --> mostrar() → Dato`.
- Responsabilidad: Cambia de pantalla sin duplicar formularios.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.ocultarContrasenas
- Contrato: `contenedor: Dato --> ocultarContrasenas() → Dato`.
- Responsabilidad: Restablece la privacidad de los campos al navegar o terminar un alta.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.conSesion
- Contrato: `operacion: Dato --> conSesion() → Dato`.
- Responsabilidad: Con Sesion. Presentación pública OZORA y panel del parque.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### app.ejecutarFormulario
- Contrato: `evento: Dato, operacion: Dato --> ejecutarFormulario() → Dato`.
- Responsabilidad: Evita envíos repetidos y presenta errores sin perder el formulario.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.parrafo
- Contrato: `texto: Text, clase: Dato --> parrafo() → Dato`.
- Responsabilidad: Crea texto de lectura sin interpretar HTML procedente del servidor.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.actualizarLectura
- Contrato: `actualizarLectura() → Dato`.
- Responsabilidad: Consulta la captura más reciente y distingue error, ausencia y simulación.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.iniciarSesion
- Contrato: `evento: Dato --> iniciarSesion() → Dato`.
- Responsabilidad: Abre el panel adecuado al rol devuelto por el servidor.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.registrarTrabajador
- Contrato: `evento: Dato --> registrarTrabajador() → Dato`.
- Responsabilidad: Crea la cuenta del parque con contraseña inicial elegida por el jefe.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### app.solicitarRecuperacion
- Contrato: `evento: Dato --> solicitarRecuperacion() → Dato`.
- Responsabilidad: Solicita un enlace sin revelar cuentas registradas.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.restablecerContrasena
- Contrato: `evento: Dato --> restablecerContrasena() → Dato`.
- Responsabilidad: Comprueba la repetición y consume el enlace de alta o recuperación.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### app.cambiarContrasenaInicial
- Contrato: `evento: Dato --> cambiarContrasenaInicial() → Dato`.
- Responsabilidad: Permite probar el mismo cambio autenticado que utilizará Android.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: puede rechazar datos o propagar errores.

### app.cerrarSesion
- Contrato: `cerrarSesion() → Dato`.
- Responsabilidad: Revoca la sesión remota y conserva la posibilidad de reintentar si falla.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.abrirRecuperacion
- Contrato: `abrirRecuperacion() → Dato`.
- Responsabilidad: Muestra el formulario de solicitud de enlace.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.volverAcceso
- Contrato: `volverAcceso() → Dato`.
- Responsabilidad: Descarta el enlace local y limpia los campos de contraseña.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.alternarContrasena
- Contrato: `evento: Dato --> alternarContrasena() → Dato`.
- Responsabilidad: Muestra u oculta exclusivamente el campo asociado al botón.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.prepararVisibilidadContrasenas
- Contrato: `prepararVisibilidadContrasenas() → Dato`.
- Responsabilidad: Añade controles accesibles a las claves de acceso, alta y cambio.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

### app.prepararAvisosFormularios
- Contrato: `prepararAvisosFormularios() → Dato`.
- Responsabilidad: Coloca cada resultado junto al botón donde se realiza la operación.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.

## src/web/js/publica.js

### publica.solicitarInformacion
- Contrato: `evento: Dato --> solicitarInformacion() → Dato`.
- Responsabilidad: Guarda la solicitud en el servidor y solo confirma cuando este la acepta.
- Visibilidad: pública; de instancia / función de módulo.
- Efectos detectados: sin acceso persistente directo detectado; revisar el contrato de las dependencias invocadas.
