<!--
Nombre: evidencia-backend.md
Descripción: Resultado de la verificación local del backend y web auxiliar.
Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
Fecha: 02/11/2026
Autor: Julia (con asistencia de Codex).
Aportación: Registrar pruebas ejecutadas y límites de la evidencia.
-->

# Evidencia del backend Sprint 2

Verificación local realizada el 02/11/2026 sobre `feature/backend-web-simulacion`, basada en `develop` (`7dc6588`). Herramientas: PHP 8.4.27, MySQL 8.4.13, Windows y navegador integrado. BD de pruebas independiente `senda_test`; demo en `senda_local`.

| Comprobación ejecutada | Resultado |
|---|---|
| Sintaxis de los archivos PHP añadidos/modificados | Sin errores |
| Sintaxis JavaScript (`node --check`) | Sin errores |
| Análisis del script PowerShell | Sin errores |
| `php tests/validacion.php` | 25 comprobaciones correctas |
| `php tests/integracion.php`, ejecución final | 152 comprobaciones HTTP/MySQL correctas |
| Importación del SQL en las dos bases vacías | Correcta |
| Navegador: login de jefe, parque sin datos y actualización | Correcto |
| Captura ficticia enviada mediante login + POST de la API | Guardada; visible como 42 ppb de S-001 |
| Navegador: alta de trabajador ficticio | Cuenta creada; enlace encolado |
| Buzón local y uso de enlaces en pruebas HTTP | Alta y recuperación correctas |

Las 152 comprobaciones cuentan tanto estados HTTP y estructura de respuesta como aserciones sobre datos: no representan 152 historias o endpoints distintos. No contienen tokens ni datos personales reales.

Cobertura de integración: credenciales incorrectas; cuenta y organización inactivas; sesiones válidas, caducadas, revocadas por logout o cambio de contraseña; rol de jefe y trabajador; aislamiento de dos organizaciones y parques; llavero ajeno/inactivo; JSON incorrecto, arrays, tipo MIME erróneo y cuerpo grande; validación sin inserciones; guardado confirmado; reintento sin duplicar; conflicto sin sobrescribir; milisegundos y precisión completa de coordenadas; última captura entre varios llaveros, empates y orden diferente al de recepción; reasignación sin trasladar histórico; alta con correo único y sin inyección de parque; enlace de alta; respuesta genérica de recuperación; token caducado, consumido y cuenta desactivada; revocación de sesiones; métodos y rutas incorrectos; límite de intentos; archivos privados inaccesibles por HTTP.

La verificación de precisión incluye coordenadas con más decimales de los que preserva la conversión automática de PHP a string. El guardado conserva esos valores para evitar falsos conflictos en reintentos.

Captura de la demo, con cuentas, coordenadas y medición ficticias:

![Panel del jefe y alta confirmada](evidencias/panel-senda.jpg)

## Límites

No se ha probado SMTP/correo a destinatarios reales, HTTPS de despliegue, Apache/Nginx, carga concurrente, Android en teléfono, BLE físico ni persistencia de recorridos. La captura confirma la demo local; no demuestra esos escenarios.

La web auxiliar apoya HU-09, sin cubrir toda la gestión de trabajadores ni asignación y cupos de llaveros. HU-18 y HU-21 requieren además validar su integración con los clientes Android antes de cerrar las historias completas.

Guía de reproducción: `doc/backend-puesta-en-marcha.md`. El servidor de pruebas necesita la misma `SENDA_CONFIG` que el comprobador. No ejecutar las pruebas de integración sobre datos de producción.
