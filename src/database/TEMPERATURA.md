# Envío de temperatura desde Android

La app envía `POST /api/v1/mediciones` con la sesión del trabajador y el mismo
control de autorización del llavero que se utiliza para O3.

```json
{
  "nombre_micro": "S-001",
  "tipo": "TEMPERATURA",
  "valor": 29.39,
  "unidad": "C",
  "latitud": 39.4,
  "longitud": -0.3,
  "fecha_hora": "2026-11-19T10:00:00.000Z",
  "simulada": false
}
```

`Minor=2939` se convierte en `29.39`, no en 2939 grados. Se aceptan valores entre
-40 y 50 C con hasta dos decimales. El marcador BLE `0x8000` no se envía.
La temperatura y O3 requieren coordenadas válidas. Android valida que la posición
tenga como máximo 10 segundos y una precisión de hasta 100 metros. Una temperatura
recibida sin GPS se muestra en directo y espera hasta 10 segundos una posición
válida antes de guardarse y enviarse. Si no llega, espera una nueva muestra; no
inventa coordenadas. O3 conserva su valor entero en ppb.

## Despliegue en una instalación existente

1. Hacer copia de seguridad de la base de datos.
2. Ejecutar `database/migrations/005_temperatura.sql` una sola vez en la base
   de SENDA, con un cliente que interprete `DELIMITER` y permisos para crear
   temporalmente un procedimiento y modificar la tabla `MEDICIONES`.
   Si ya se ejecutó la versión anterior de 005, usar en su lugar
   `006_temperatura_gps_obligatorio.sql` una sola vez. Si hay registros antiguos
   sin coordenadas, la migración 006 se detiene sin borrarlos; hay que revisar
   esos registros antes de aplicarla, sin inventar posiciones.
3. Desplegar `business_logic/Apoyo.php` y `business_logic/LogicaNegocio.php`.
4. Instalar la nueva versión Android. Iniciar sesión, vincular el llavero e
   iniciar un recorrido. Verificar una fila `TEMPERATURA` en `MEDICIONES`.

Para una instalación nueva, `database.sql` incluye el soporte; no ejecutar 005
sobre ese esquema. Las otras migraciones del proyecto siguen su propio proceso.

La identidad de una lectura incluye fecha, llavero y tipo. Un reintento idéntico
devuelve `guardada=true, duplicada=true`; uno con valores distintos devuelve 409.
La app solo marca la fila enviada después de recibir `guardada=true`, y conserva
los datos pendientes o rechazados para reintento. La cola está separada por cuenta
y parque. La migración local de Android conserva el histórico de temperatura. Las filas
antiguas sin coordenadas permanecen locales y no se envían automáticamente.

El indicador web existente de última medición sigue consultando O3 para conservar
sus unidades. Este cambio añade la subida y persistencia de temperatura; no añade
una pantalla de temperatura a la web.

## Verificación

- Pruebas JVM de Android: contrato JSON, grados negativos, coordenadas obligatorias,
  envío HTTP autenticado y confirmación explícita del guardado.
- `php src/tests/validacion.php`: validación de ambas magnitudes.
- `php src/tests/integracion.php`: ejecutar solo con configuración local y una
  base de pruebas terminada en `_test`; incluye persistencia, autorización,
  conflictos, límites y reintentos de temperatura.
- `TemperatureQueueTest`: prueba Android de migración SQLite de versión 3 a 5,
  persistencia de la cola y aislamiento entre cuentas. Requiere dispositivo/emulador.

Se verificaron la migración y la suite HTTP con una instancia MariaDB local aislada.
No se ha desplegado ni modificado la base de datos del servidor publicado.
