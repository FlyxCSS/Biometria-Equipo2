<?php
/*
 * Nombre: index.php
 * Descripción: Controlador público para OZORA, el acceso SENDA y la API.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Servir solo recursos públicos y aplicar cabeceras comunes.
 */
declare(strict_types=1);
ini_set('display_errors', '0');
header('X-Content-Type-Options: nosniff');
header('Referrer-Policy: no-referrer');
header("Content-Security-Policy: default-src 'self'; script-src 'self'; style-src 'self'; connect-src 'self'; img-src 'self'; frame-ancestors 'none'; base-uri 'none'; form-action 'self'");
$ruta = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
if (str_starts_with($ruta, '/api/')) {
    require_once dirname(__DIR__) . '/communication/ServidorRest.php';
    atenderApi();
}
// Lista explícita: nunca entregar configuración, SQL, herramientas o código PHP.
$archivos = ['/' => ['index.html', 'text/html'], '/index.html' => ['index.html', 'text/html'],
    '/index.php' => ['index.html', 'text/html'], '/acceso' => ['acceso.html', 'text/html'],
    '/acceso/' => ['acceso.html', 'text/html'], '/js/app.js' => ['js/app.js', 'text/javascript'],
    '/js/publica.js' => ['js/publica.js', 'text/javascript'], '/css/publica.css' => ['css/publica.css', 'text/css'],
    '/css/estilos.css' => ['css/estilos.css', 'text/css']];
// El nombre debe ser un archivo simple existente, sin segmentos de directorio del solicitante.
if (preg_match('~^/assets/([a-z0-9-]+\.(svg|png))$~D', $ruta, $recurso)) {
    $archivo = 'assets/' . $recurso[1];
    if (is_file(__DIR__ . '/' . $archivo)) $archivos[$ruta] = [$archivo, $recurso[2] === 'svg' ? 'image/svg+xml' : 'image/png'];
}
if (preg_match('~^/assets/fonts/(sora|manrope)\.ttf$~D', $ruta, $fuente)) {
    $archivos[$ruta] = ['assets/fonts/' . $fuente[1] . '.ttf', 'font/ttf'];
}
if (!isset($archivos[$ruta])) {
    http_response_code(404);
    header('Content-Type: text/plain; charset=UTF-8');
    echo 'Página no encontrada.';
    exit;
}
if (!in_array($_SERVER['REQUEST_METHOD'], ['GET', 'HEAD'], true)) {
    http_response_code(405);
    header('Allow: GET, HEAD');
    exit;
}
header('Cache-Control: no-store');
header('Content-Type: ' . $archivos[$ruta][1] . '; charset=UTF-8');
if ($_SERVER['REQUEST_METHOD'] !== 'HEAD') {
    readfile(__DIR__ . '/' . $archivos[$ruta][0]);
}
