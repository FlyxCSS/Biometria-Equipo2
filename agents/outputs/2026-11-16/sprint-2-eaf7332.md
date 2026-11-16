# Revisión inicial · Sprint 2 · 16/11/2026

Commit `eaf7332`. El alcance se confirmó con las dos capturas aportadas por Julia. Se observaron cambios locales preexistentes en tres archivos de configuración de Android Studio. Revisión estática y comprobaciones locales de PHP y sintaxis JavaScript.

## Historias del sprint

Esta sección describe el estado al recibir las capturas. El texto incorporado después se revisa en la [matriz por criterio](sprint-2-eaf7332-criterios.md).

| HU | Ítems visibles | Estado de criterios y comprobación pendiente |
| --- | ---: | --- |
| HU-14 · Iniciar sesión | 6 | ◯ Incorporar criterios y ejecutar el inicio de sesión desde Android con un servidor de prueba. |
| HU-15 · Vincular llavero por QR | 6 | ◯ Incorporar criterios y verificar el escáner, selección y tratamiento de códigos según los casos acordados. |
| HU-16 · Recibir mediciones Bluetooth | 5 | ◯ Incorporar criterios y comprobar recepción y filtrado con el llavero. |
| HU-17 · Registrar ubicación GPS | 5 | ◯ Incorporar criterios y comprobar permisos, disponibilidad y asociación a la lectura. |
| HU-18 · Enviar mediciones al servidor | 6 | ◯ Incorporar criterios y demostrar captura, cola, envío, persistencia y consulta web en la misma ejecución. |
| HU-19 · Iniciar y finalizar recorrido | 7 | ◯ Incorporar criterios y comprobar las transiciones con eventos de captura en curso. |
| HU-20 · Consultar estado del llavero y última medición | 6 | ◯ Incorporar criterios y contrastar los estados visibles con lo recibido en Android. |
| HU-09 · Registrar trabajadores | 6 | ◯ Incorporar criterios y verificar el alta desde el panel del jefe de parque. El contrato describe expresamente un soporte temporal. |
| HU-21 · Recuperar acceso | 9 | ◯ Incorporar criterios y ejecutar interfaz, entrega del enlace y uso del token con una cuenta de prueba. |
| HU-22 · Acceso biométrico | 9 | ◯ Incorporar criterios y verificar en un dispositivo el acceso biométrico y los recorridos alternativos acordados. |

Las comprobaciones sugeridas orientan las pruebas iniciales. El contenido de los 65 ítems y la aceptación del equipo quedan pendientes. Los Hi‑Fi se incorporarán mediante `inputs/specs/GUI.md`.

## Diseño, fake y concurrencia

| Estado | Evidencia | Corrección o prueba pendiente |
| --- | --- | --- |
| ✖ Diseño por componente | `doc/xxx_design.md` está vacío. El contrato REST aporta información de integración. | Documentar el diseño lógico de cada componente, sus firmas, efectos e invariantes. |
| ✖ Proxy de la GUI web | `src/frontend_business_logic/LogicaFake.js` está vacío. Hay llamadas `fetch` en `src/web/js/app.js:68` y `src/web/js/publica.js:25`. | Encapsular el transporte tras el proxy definido para la GUI y comprobar los flujos a través de él. |
| ✖ Invalidación de sesión con una respuesta tardía | `MeasurementSync.java:59` comprueba la sesión y luego la borra en una operación aparte. Entre ambas acciones se puede instalar una sesión nueva. | Invalidar en una sola operación condicionada a la identidad de la sesión. Forzar el intercalado entre el 401 y el nuevo acceso y comprobar que la sesión nueva se conserva. Hallazgo estático. |
| ◯ Concurrencia del resto del sistema | Se observan transacciones PHP, restricciones SQL y una guarda atómica en la sincronización Android. | Verificar las invariantes del informe con peticiones y callbacks solapados. |

## Comprobaciones locales

- ✔ La prueba PHP `src/tests/validacion.php` completó 25 comprobaciones.
- ✔ `node --check` completó la comprobación de sintaxis de `src/web/js/app.js` y `src/web/js/publica.js`.
- ◯ Pendientes de esta revisión: pruebas HTTP/MySQL de extremo a extremo, ejecución Android con dispositivo y llavero, pruebas solapadas y comparación Hi‑Fi. Las evidencias históricas del repositorio corresponden a ejecuciones previas.
