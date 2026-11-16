# Diseño lógico y mapa de componentes

Los enlaces identifican el contrato y el código actuales. Usa los diseños aprobados de `doc/` para evaluar conformidad. Donde falte un diseño, identifica la brecha y rotula cualquier descripción derivada del código como diseño observado.

| Capa | Fuentes que revisar | Operaciones y propiedades que documentar |
| --- | --- | --- |
| Interfaz web | [src/web](../../src/web/) | Acciones por rol, carga, respuestas tardías, doble envío y visualización de datos reales o ilustrativos |
| Proxy de dominio de la web | [LogicaFake.js](../../src/frontend_business_logic/LogicaFake.js), [app.js](../../src/web/js/app.js) y [publica.js](../../src/web/js/publica.js) | Operaciones de dominio de la GUI, traducción del transporte y dependencias |
| Android | [src/android](../../src/android/) | Acceso, recuperación, biometría, QR, captura BLE/GPS, recorrido, cola SQLite y sincronización |
| Transporte REST | [ServidorRest.php](../../src/communication/ServidorRest.php) y [contrato](../../doc/contracts/backend-android-v1.md) | Rutas, método, autenticación, JSON, errores y mapeo a operaciones de dominio |
| Dominio PHP | [LogicaNegocio.php](../../src/business_logic/LogicaNegocio.php) | Sesión, alta, recuperación, validación, almacenamiento y lectura de mediciones |
| Persistencia | [database.sql](../../src/database/database.sql) y [migraciones](../../src/database/migrations/) | Claves, restricciones, transacciones y compatibilidad de datos previos |
| Firmware | [src/microprocesador](../../src/microprocesador/) | Obtención y publicación de medidas y coordinación con callbacks BLE |

## Ficha de diseño por función

Para cada función del flujo revisado registra: ubicación, firma lógica, propósito, precondiciones, efectos, resultados y errores, dependencias, estado compartido y quién lo modifica. Añade la operación atómica o el mecanismo de exclusión, si aplica, y la prueba asociada. Deja el lenguaje objetivo en las reglas de implementación.

Usa tipos abstractos `N`, `Z`, `R`, `B`, `Text`, enumeraciones, agregaciones, listas `[Tipo]` o arreglos `[Tipo]_n`. Escribe firmas como `entrada: Tipo --> operacion() --> Resultado`. Omite la flecha del lado sin datos. Describe mutación o solo lectura explícitamente. Ejemplo ilustrativo: `medicion: Medicion --> registrar_medicion() --> ResultadoRegistro`. El resultado de dominio se traduce a HTTP en la capa de comunicación.

Para cada componente revisa si el `<componente>_design.md` cubre diseño, aclaraciones aplicables y reglas generales, y si los métodos clave tienen pruebas. Para BD documenta tablas, descripción, columnas, claves y restricciones. En el código comprueba que los encabezados de las funciones expliquen su diseño lógico y que los comentarios de invariantes expliquen decisiones y coordinación concurrente. Señala duplicación concreta con ambos lugares y su impacto.
