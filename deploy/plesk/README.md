# Instalación en Plesk bajo /senda

Nombre: Guía de despliegue en Plesk.  
Descripción: Distribución pública y privada de la aplicación.  
Copyright: (c) 2026 Equipo 2 — SENDA / OZORA.  
Fecha: 03/11/2026.  
Autor: Julia (con asistencia de Codex).  
Aportación: Publicación sin sustituir las otras webs del dominio.

En el directorio principal de la suscripción, crear `senda_s2_privado` y copiar dentro `src`, `config` y `tools/gestionar.php`. Crear `httpdocs/senda`, copiar allí el `index.php` de esta carpeta, el `.htaccess` de `src/web` y las carpetas `assets`, `css` y `js` de `src/web`. Los recursos CSS servidos directamente deben usar `/senda/` como prefijo de las URL absolutas.

El controlador define `SENDA_BASE_PATH=/senda`; la web y los endpoints REST comparten ese prefijo. La URL de acceso es `https://jvaldeo.upv.edu.es/senda/acceso` y la API comienza en `/senda/api/v1`.

Crear una base y un usuario exclusivo en Plesk. Importar `src/database/database.sql` y después `src/database/migrations/001_solicitudes_contacto.sql`. La migración de contraseña inicial es para instalaciones anteriores: el esquema nuevo ya incluye su columna.

Configurar `senda_s2_privado/config/local.php` con el DSN, usuario y contraseña de esa base, `app_url` con la URL pública y `environment=production`. Las credenciales quedan fuera de `httpdocs` y de Git. Establecer un remitente real y comprobar el transporte de correo antes de dar por operativa la recuperación de contraseñas.

No copiar cuentas ni datos de la demo local al servidor. Preparar la organización, el parque y el primer jefe con sus propios datos y una contraseña almacenada como hash. No ejecutar las pruebas de integración contra esta base: utilizar una base independiente para pruebas.

Estado del 03/11/2026: archivos publicados en estas rutas y esquema importado en `jvaldeo_senda_s2`. Portada HTTPS comprobada. Pendientes la contraseña privada de conexión, el primer usuario y la comprobación funcional remota; este estado no equivale a una validación completa de la instalación.

Validación local del prefijo: 203 comprobaciones de integración en la raíz y 203 en `/senda`, además de comprobación de sintaxis PHP y JavaScript.
