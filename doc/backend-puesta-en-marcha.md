<!--
Nombre: backend-puesta-en-marcha.md
Descripción: Arranque, demostración y límites del backend del Sprint 2.
Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
Fecha: 02/11/2026
Autor: Julia (con asistencia de Codex).
Aportación: Instrucciones reproducibles para Julia y los clientes Android.
-->

# Backend del Sprint 2

Implementación local en `feature/backend-web-simulacion`. Contrato provisional en `doc/contracts/backend-android-v1.md`; pendiente de contrastar con los otros dos programadores. No se ha modificado Android ni mezclado su rama.

La portada pública OZORA está en `/`; el acceso y el panel SENDA, en `/acceso`. Se han seguido la landing HiFi `54:220`, el panel `59:1206` y la arquitectura de información de la carpeta secundaria. La web pública presenta el diseño previsto y distingue los mapas e históricos conceptuales de las funciones ya operativas.

Actualización para una instalación de la versión anterior: `php tools/gestionar.php actualizar-contacto`. Añade solo `SOLICITUDES_CONTACTO`, sin borrar ni cambiar registros existentes. La instalación nueva con `instalar` ya aplica esta ampliación. Si se importa únicamente `database.sql` manualmente, importar después `src/database/migrations/001_solicitudes_contacto.sql`.

El formulario público usa `POST /api/v1/solicitudes`: nombre, correo, organización y mensaje, con validación y límite de intentos. La confirmación corresponde al guardado real. No envía correos externos ni da de alta cuentas. El administrador puede consultar las últimas 50 solicitudes mediante `php tools/gestionar.php solicitudes`, desde su consola privada; no hay un endpoint público para listarlas. Proteger estos registros y no copiarlos a las evidencias.

Los nuevos correos de recuperación enlazan a `/acceso#token=...`. La landing redirige los antiguos enlaces `/#token=...` al acceso y allí se borra el fragmento. Se conserva compatibilidad con los enlaces ya emitidos.

## Entorno preparado en este ordenador

PHP 8.4.27 y MySQL 8.4.13 portables, descargados desde sus distribuidores oficiales bajo `.local/tools/`, sin instalar servicios de Windows. MySQL escucha solo en `127.0.0.1:3307`; la web de demo en `http://127.0.0.1:8080`. Bases independientes: `senda_local` para demo y `senda_test` para pruebas.

Las credenciales privadas están en `config/local.php` y `.local/config-test.php`. No versionar estos archivos, el buzón, las herramientas ni los datos MySQL. `.gitignore` ya los excluye.

Para volver a arrancar desde PowerShell en la raíz:

```powershell
./tools/iniciar-local.ps1
```

Para consultar MySQL en el navegador, abrir **http://127.0.0.1:8082/**. El visor separado permite elegir tabla y alternar Registros/Estructura; cada pulsación de Ver / actualizar consulta la BD actual. No permite editar ni ejecutar SQL, limita la vista a 100 filas y oculta contraseñas, hashes, tokens y cuerpos de correo. Solo responde en loopback, con Host `127.0.0.1:8082`, y en entorno local. No desplegar `tools/bbdd` en un servidor público.

Si la política local impide ejecutar scripts, no cambiarla: usar las instrucciones de arranque PHP/MySQL de la siguiente sección. El script usa los puertos del entorno portable preparado y no inicializa ni instala servicios.

Demo ficticia:

| Uso | Correo | Contraseña exclusivamente de prueba |
|---|---|---|
| Jefe | jefe@example.org | EjemploSoloParaPruebas-2026 |
| Trabajador | trabajador@example.org | EjemploSoloParaPruebas-2026 |

El llavero `DEMO-S-001` tiene identificador BLE `S-001`, está asignado al trabajador y pertenece al parque 1. Las coordenadas son ficticias y las lecturas llevan `simulada:true`.

Para enviar una captura nueva por HTTP y después pulsar Actualizar en la web del jefe:

```powershell
$env:SENDA_DEMO_PASSWORD = 'EjemploSoloParaPruebas-2026'
./.local/tools/php/php.exe tools/enviar-demo.php
Remove-Item Env:/SENDA_DEMO_PASSWORD
```

## Preparar otro ordenador o servidor

1. PHP 8.2 o superior con `pdo_mysql` y `mbstring`, y MySQL 8.4. Comprobado con PHP 8.4.27 / MySQL 8.4.13. No necesita Composer.
2. Crear una BD vacía UTF-8 y usuario exclusivo. Para instalar requiere permisos de esquema; en producción separar la cuenta de migraciones de la de aplicación con permisos mínimos.
3. Copiar `config/example.php` como `config/local.php` y ajustar conexión, URL y correo. No se carga `.env` automáticamente. La variable de proceso `SENDA_CONFIG` permite elegir otra configuración privada.
4. Ejecutar `php tools/gestionar.php instalar`. Se niega a continuar si la BD ya contiene tablas. No elimina tablas. Si la importación falla a medias, revisar el esquema antes de repetir.
5. Solo en local: establecer `SENDA_DEMO_PASSWORD` y ejecutar `php tools/gestionar.php demo`. No sobrescribe las cuentas existentes.
6. Arrancar desde la raíz: `php -S 127.0.0.1:8080 -t src/web router.php`. El servidor integrado es solo de desarrollo.

En Apache, DocumentRoot **solo `src/web`**, con reescritura y `.htaccess` habilitados. En otros servidores, dirigir rutas a `src/web/index.php`. Nunca publicar la raíz del repositorio. No se ha probado Apache/Nginx en este equipo.

Para producción configurar HTTPS, `environment=production`, `app_url=https://...` y `mail_transport=mail`. La comprobación de HTTPS usa `HTTPS=on`; si hay un proxy, configurar ese dato en el servidor de confianza, sin aceptar cabeceras arbitrarias. Configurar TLS o red privada de MySQL si sale del mismo equipo.

## Correo de alta y recuperación

El endpoint encola el correo en `CORREOS_PENDIENTES` en la misma transacción que el alta o el token. Entregar pendientes por consola:

```powershell
./.local/tools/php/php.exe tools/gestionar.php correo
```

En local aparecen `.eml` en `var/mail/`, fuera de la web. Abrir el enlace del mensaje para establecer contraseña. Contiene un token en el fragmento `#token=...`, que no viaja al servidor al abrir la página y se retira de la barra. No compartir el mensaje: permite cambiar la contraseña durante 30 minutos. Solo se ha probado el buzón local, no un destinatario real.

En producción configurar `mail()` de PHP y programar `php tools/gestionar.php correo`, por ejemplo cada minuto. No se ha creado una tarea de Windows. `mail` entrega al transporte configurado, no garantiza llegada al destinatario: verificarla con el servicio elegido. Un fallo deja la tarea pendiente. Si el proceso se corta entre envío y confirmación de BD puede repetirse el correo; el enlace sigue siendo de un solo uso.

Restringir acceso a la tabla de correos y sus copias: contiene el enlace en claro hasta entregarlo; después se vacían cuerpo y dirección. Sesiones y tokens solo guardan hashes. Encolar no equivale a enviar.

`php tools/gestionar.php limpiar` elimina registros técnicos caducados y correos enviados antiguos. No elimina personas ni mediciones.

## Correspondencia con vuestro diagrama

Se conservan las cinco tablas y sus campos. La fecha pasa a `DATETIME(3)` y la PK de MEDICIONES sigue siendo `(fecha_hora, id_llavero)`.

API `correo` -> BD `USUARIOS.email`. API `nombre_micro` (`S-001`) -> búsqueda por `LLAVEROS.identificador_ble` único -> guardado del `id_llavero` (`DEMO-S-001` en la demo). No se presupone que el ID de BD coincida con el nombre BLE.

Ampliaciones documentadas:

- MEDICIONES: unidad, simulada, autor, parque histórico, nombre BLE histórico y fecha de recepción. Reasignar un llavero no desplaza el histórico a otro parque.
- SESIONES: hash del token y caducidad.
- TOKENS_ACCESO: finalidad, caducidad y consumo de enlaces.
- CORREOS_PENDIENTES: entrega recuperable del correo.
- LIMITES: control de intentos por cuenta y por IP.

`password_hash` admite NULL antes de completar el enlace de alta: la cuenta no puede entrar mientras tanto. Las FK compuestas impiden asignaciones entre parques u organizaciones incompatibles. Roles operativos: `jefe_parque` y `trabajador`; el esquema admite además `proveedor` y `organizacion`, sin sus pantallas administrativas.

El SQL es una creación inicial, **no una migración para una BD poblada**. Si hay datos reales, preparar antes migración y copia de seguridad. Se conservan los cupos, pero no se implementan aún las operaciones administrativas de alta/asignación de llaveros ni sus validaciones de cupos.

## Android

Base `/api/v1`, token en `Authorization: Bearer ...`, sesión de 24 horas sin renovación. Guardado confirmado por 200/201 y `guardada:true`; repetición idéntica: 200 con `duplicada:true`; misma clave y contenido distinto: 409. Conservar fecha y cuerpo al reintentar.

Fechas UTC con milisegundos y Z, con máximo cinco minutos de adelanto del reloj. Coordenadas válidas obligatorias; nunca sustituirlas por una posición de otro instante. O3 entero en ppb de 0 a 65535: rango técnico de BLE, no criterio ambiental.

La web guarda la sesión solo en memoria; recargar exige entrar de nuevo. Android deberá proteger su token y no sustituirlo por permiso biométrico. La persistencia de recorridos sigue pendiente de acuerdo con Persona 2.

Un móvil físico no alcanza el `127.0.0.1` del ordenador. La demo está limitada al equipo; acordar una URL HTTPS de integración. No está probado el flujo desde Android ni desde el llavero físico.

## Pruebas

`php tests/validacion.php` comprueba tipos y límites. `php tests/integracion.php` hace HTTP y verifica MySQL: exige entorno local y BD terminada en `_test`. Crea datos ficticios nuevos sin borrar los anteriores. Ejemplos manuales en `tests/endpoints.http` y `tests/ejemplos/`.

Crear una configuración privada independiente para pruebas y establecer `SENDA_CONFIG` tanto en el servidor como en el comprobador. `app_url` debe apuntar a ese servidor, separado de la demo. En el entorno ya preparado:

```powershell
$env:SENDA_CONFIG = (Resolve-Path .local/config-test.php).Path
# Consola 1, mantener el servidor en primer plano:
./.local/tools/php/php.exe -S 127.0.0.1:8081 -t src/web router.php
# Consola 2, con la misma variable SENDA_CONFIG:
./.local/tools/php/php.exe tests/integracion.php
Remove-Item Env:/SENDA_CONFIG
```

Si el servidor de pruebas ya está activo no arrancar otro sobre el mismo puerto. Repetir muchas veces puede agotar el límite global de 100 intentos de autenticación por IP en 15 minutos. Esperar a la siguiente ventana; no desactivar el límite en producción.

Ver `tests/evidencia-backend.md`. Antes de cerrar historias completas falta coordinar el contrato con compañeros, correo real, HTTPS e integración Android/BLE.

Referencias: [consultas PDO](https://www.php.net/manual/en/pdo.prepared-statements.php), [hash de contraseñas](https://www.php.net/manual/en/function.password-hash.php) y [bloqueos MySQL](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking-reads.html).
