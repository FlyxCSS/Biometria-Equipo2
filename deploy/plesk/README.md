# Instalación en Plesk bajo /senda

Nombre: Guía de despliegue en Plesk.  
Descripción: Distribución pública y privada de la aplicación.  
Copyright: (c) 2026 Equipo 2 — SENDA / OZORA.  
Fecha: 03/11/2026.  
Autor: Julia (con asistencia de Codex).  
Aportación: Publicación sin sustituir las otras webs del dominio.

En el directorio principal de la suscripción, crear `senda_s2_privado` y copiar dentro `src`, `config` y `tools/gestionar.php`. Crear `httpdocs/senda`, copiar allí el `index.php` de esta carpeta, el `.htaccess` de `src/web` y las carpetas `assets`, `css` y `js` de `src/web`. Los recursos CSS servidos directamente deben usar `/senda/` como prefijo de las URL absolutas.

El controlador define `SENDA_BASE_PATH=/senda`; la web y los endpoints REST comparten ese prefijo. La URL de acceso es `https://jvaldeo.upv.edu.es/senda/acceso` y la API comienza en `/senda/api/v1`.

Con Apache y PHP-FPM, conservar la regla de `.htaccess` que reenvía `Authorization` como `HTTP_AUTHORIZATION`. El controlador REST admite también `REDIRECT_HTTP_AUTHORIZATION`. Sin ese reenvío el login puede responder correctamente, pero las peticiones posteriores devuelven 401. Se ha reproducido este fallo en Plesk y confirmado la corrección consultando la sesión con HTTP 200.

Crear una base y un usuario exclusivo en Plesk. Importar `src/database/database.sql` y después `src/database/migrations/001_solicitudes_contacto.sql`. La migración de contraseña inicial es para instalaciones anteriores: el esquema nuevo ya incluye su columna.

La instalación nueva incluye ROLES. Para una instalación anterior, usar por consola `php tools/gestionar.php actualizar-roles` después de exportar la base. En este despliegue la actualización se ha completado en phpMyAdmin con DDL adaptado a las restricciones reales de MariaDB, verificadas con `SHOW CREATE TABLE USUARIOS`. No volver a importar `003_roles.sql`: la tabla antigua `rol` ya ha sido sustituida por `id_rol`. El archivo portátil de migración está pensado para ejecución por PDO/consola, evitando que el analizador de phpMyAdmin cambie el contexto a `information_schema`.

Configurar `senda_s2_privado/config/local.php` con el DSN, usuario y contraseña de esa base, `app_url` con la URL pública y `environment=production`. Las credenciales quedan fuera de `httpdocs` y de Git. Establecer un remitente real y comprobar el transporte de correo antes de dar por operativa la recuperación de contraseñas.

No copiar cuentas ni datos de la demo local al servidor. Preparar la organización, el parque y el primer jefe con sus propios datos y una contraseña almacenada como hash. No ejecutar las pruebas de integración contra esta base: utilizar una base independiente para pruebas.

Estado del 03/11/2026: archivos publicados en estas rutas y esquema importado en `jvaldeo_senda_s2`. Conexión configurada por la propietaria. Una solicitud ficticia enviada desde la portada se ha guardado y comprobado en phpMyAdmin. Acceso web HTTP 200; consulta de sesión sin credenciales HTTP 401; rutas de configuración privada HTTP 404. La propietaria ha importado las cuentas ficticias con claves iniciales aleatorias y cambio obligatorio. Login del jefe y consulta autenticada de sesión comprobados. Los formularios permiten mostrar y ocultar cada contraseña. Cambio de clave completado por la propietaria y panel del jefe comprobado: muestra la medición ficticia de 42 ppb del llavero S-001 y permite actualizarla. El correo saliente todavía no está configurado ni validado.

Validación local del prefijo: 203 comprobaciones de integración en la raíz y 203 en `/senda`, además de comprobación de sintaxis PHP y JavaScript.

Actualización de roles y avisos del 03/11/2026: copia privada de la base antes de migrar; los tres usuarios conservan sus hashes originales y quedan relacionados con ROLES (jefe 3, trabajadores 4). Login y sesión correctos después de la migración. Reintento de alta con el correo ficticio existente rechazado; el aviso aparece dentro del formulario y marca el correo. La base conserva tres usuarios después del intento. Comprobadas instalación nueva del esquema, migración local conservando datos, 216 comprobaciones HTTP/MySQL y 25 de validación. Los iconos de ojo mantienen nombres accesibles y estado de visibilidad.
