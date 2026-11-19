# Reestructuración de SENDA · 19/11/2026

Base revisada: `ca5aa16`. Referencias: agente docente v2, especificación lógica, SDD v2, plantilla de generación y rúbrica S2, conservadas en `doc/materiales/referencias`. La petición del equipo prevalece sobre los nombres de archivos de ejemplo del agente.

## Cambios y comprobaciones

- ✅ Raíz conservada: `doc`, `src`, `agents`, `AUTHORS.md`, `README.md`, `.gitignore`. `agents` no se ha modificado. Se mantiene AUTHORS.md por la petición explícita del equipo frente a author.md en el ejemplo docente.
- ✅ Diez carpetas de primer nivel de src con diez diseños homónimos. Primera capa de doc reservada a esos diseños y a las carpetas materiales/prompts. Documentación anterior trasladada, no duplicada como respaldo.
- ✅ Referencias del profesor, guías, contratos, informes y evidencias clasificados. Se elimina el diseño plantilla vacío. Se mantienen fuentes, licencias, recursos y wrapper Gradle necesarios.
- ✅ Diez prompts SDD y uso_prompt.md con orden, contratos, criterios de aceptación, reglas y restricciones. No prometen reproducción byte a byte de una generación por IA.
- ✅ Microprocesador: un único sketch real con setup/loop, cinco cabeceras originales y Bateria.h. Las conversiones ProtocoloBLE quedan integradas en Publicador.h; se retiran los servicios GATT no utilizados. Atribución de Jordi conservada. Prueba estática Major O3/5=2821.
- ✅ Selector USAR_MEDIDA_FAKE=false conserva adquisición real. Medidor ofrece O3 y temperatura ficticias explícitas para pruebas. La batería sigue usando no disponible si no hay divisor externo. La trama actual no marca simulación: no usar ese modo como dato real de campo.
- ✅ Separación de negocio/transporte: ErrorDominio lleva causa y datos; ContextoWeb adapta códigos REST y rutas. La API mantiene estados y cuerpos públicos. La GUI web sigue usando LogicaFake, cuyo estado de transporte es privado.
- ✅ Cabeceras de fuentes propias y recursos declarativos; fechas históricas conservadas donde existían, autores del equipo y atribución base. No se modifican binarios, fuentes tipográficas, licencias ni archivos de terceros para forzar cabeceras. JSON y otros formatos de máquina conservan su sintaxis sin comentarios incompatibles.
- ✅ Contratos y descripciones de operaciones con nombre entre líneas discontinuas; inventario estático de 767 operaciones originales, más las funciones de mapas embebidos y el helper de Major documentados en sus fuentes. Tipos, firmas y estado se trazan en los diseños.
- ✅ Comparación sintáctica sin comentarios: los 83 archivos Java mantienen sus tokens funcionales respecto a HEAD. Los cambios Android de esta revisión son documentales, no una nueva lógica de captura o sesión.
- ✅ Registros .log retirados; .idea excluida del control de versiones y conservada en el equipo. La retirada de .idea figura en el índice de Git; no se ha creado ningún commit. La mayoría del resto de los cambios permanece sin preparar.

## Validación

- Android: assembleDebug, assembleRelease y assembleDebugAndroidTest correctos. Release conserva su configuración de APK sin firma de distribución. Pruebas JVM de ambas variantes sin fallos; el recuento definitivo figura al pie de este informe.
- Firmware real: compilación correcta con `adafruit:nrf52:sparkfunnrf52840mini`; 100708 bytes de programa y 15436 de variables globales. No se ha cargado en la placa. El modo ficticio también compila correctamente (99080 bytes de programa y 15416 de variables globales). Se ha restablecido y recompilado el modo real; no se ha simulado una medición física.
- PHP: los 17 archivos propios pasan el control de sintaxis. validacion.php completa 43 comprobaciones incluyendo temperatura; perfil-llavero.php completa 28; historial.php verifica 45 lecturas en tres páginas, aislamiento, cursores y rol.
- JavaScript: cinco pruebas del proxy web correctas. Comprobación final de sintaxis de scripts propios.
- Estructura: correspondencia carpeta/diseño/prompt y referencias relativas del README y diseños comprobadas.

## Límites y decisiones explícitas

- No se ha iniciado emulador, instalado en el teléfono, flasheado sensor, modificado datos o código en Plesk ni publicado en GitHub. La revisión prepara el repositorio local para el commit manual del equipo.
- La integración completa HTTP/MySQL, BLE/GPS, calibración real, huella y notificaciones requiere el entorno de pruebas o hardware. Las pruebas instrumentadas solo se han compilado.
- Las firmas del proxy y del negocio comparten tipos lógicos y operación, con adaptación explícita de credencial a identidad; no son idénticas en su sintaxis nativa PHP/Java/JavaScript. guardarTemperatura en Android prepara datos y delega el mismo guardado de negocio.
- Las operaciones extensas de construcción de vistas no se han dividido artificialmente para cumplir una longitud aproximada de diez líneas: se conservan los flujos visuales ya probados. No se certifica con este inventario el cumplimiento absoluto de estilo, concurrencia o todos los criterios académicos.
- Las restricciones y transacciones se mantienen; SQLite no sustituye una prueba de concurrencia en MySQL. La descripción de confinamiento por hilo es el contrato del componente, no evidencia de una prueba de estrés.
- Para actualizar Plesk posteriormente, publicar juntos ContextoWeb, ServidorRest, Apoyo, LogicaNegocio y el controlador web compatibles, según README. No mezclar las clases de error de versiones distintas.

El PDF explicativo se entrega en Descargas, fuera del repositorio.

Recuento JVM definitivo: Debug 42 casos (41 correctos, 1 omitido), Release 42 casos (41 correctos, 1 omitido); cero fallos y errores. La omisión corresponde a la prueba de contrato que necesita fixture externa. PDF final: SENDA_clases_y_flujo_2026-11-19.pdf, fuera de Git.
