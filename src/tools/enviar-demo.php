<?php
/*
 * Nombre: enviar-demo.php
 * Descripción: Envía una lectura ficticia por la misma API que utilizará Android.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Demostrar el flujo login -> HTTP -> MySQL -> web sin disponer del llavero.
 */
declare(strict_types=1);
require_once dirname(__DIR__) . '/business_logic/Apoyo.php';
$config = configuracion();
if (PHP_SAPI !== 'cli' || ($config['environment'] ?? '') !== 'local') {
    throw new RuntimeException('La simulación solo puede ejecutarse por consola en local.');
}

/** Diseño lógico: (ruta, datos, token) -> enviarDemo -> datos de respuesta.
 * Descripción: Publica JSON en la URL local y comprueba la confirmación del servidor. */
function enviarDemo(string $ruta, array $datos, string $token = ''): array
{
    global $config;
    $headers = "Content-Type: application/json\r\n";
    if ($token !== '') $headers .= "Authorization: Bearer $token\r\n";
    $contexto = stream_context_create(['http' => ['method' => 'POST', 'header' => $headers,
        'content' => json_encode($datos, JSON_THROW_ON_ERROR), 'ignore_errors' => true, 'timeout' => 15]]);
    $texto = file_get_contents(rtrim($config['app_url'], '/') . '/api/v1' . $ruta, false, $contexto);
    $respuesta = json_decode($texto, true, 16, JSON_THROW_ON_ERROR);
    if (!($respuesta['ok'] ?? false)) throw new RuntimeException($respuesta['error']['mensaje'] ?? 'No hay confirmación.');
    return $respuesta['data'];
}

$login = enviarDemo('/sesiones', ['correo' => 'trabajador@example.org', 'contrasena' => getenv('SENDA_DEMO_PASSWORD') ?: '']);
$lectura = json_decode(file_get_contents(dirname(__DIR__) . '/tests/ejemplos/medicion.json'), true, 16, JSON_THROW_ON_ERROR);
$lectura['fecha_hora'] = (new DateTimeImmutable('now', new DateTimeZone('UTC')))->format('Y-m-d\TH:i:s.v\Z');
$respuesta = enviarDemo('/mediciones', $lectura, $login['token']);
// Revocar el token temporal de esta demostración.
$headers = "Authorization: Bearer {$login['token']}\r\n";
file_get_contents(rtrim($config['app_url'], '/') . '/api/v1/sesiones/actual', false,
    stream_context_create(['http' => ['method' => 'DELETE', 'header' => $headers, 'ignore_errors' => true, 'timeout' => 15]]));
echo json_encode($respuesta, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "\n";
