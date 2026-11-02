<?php
/*
 * Nombre: index.php
 * Descripción: Visor local de MySQL de solo lectura, separado de la web pública.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Permitir consultar registros y estructura sin mostrar secretos ni ejecutar SQL libre.
 */
declare(strict_types=1);
ini_set('display_errors', '0');
header('Cache-Control: no-store');
header('Content-Type: text/html; charset=UTF-8');
header('X-Content-Type-Options: nosniff');
header('Referrer-Policy: no-referrer');
header("Content-Security-Policy: default-src 'none'; style-src 'unsafe-inline'; frame-ancestors 'none'; base-uri 'none'; form-action 'self'");
// Rechazar accesos de red y nombres ajenos, incluso si alguien cambia accidentalmente el arranque.
if (!in_array($_SERVER['REMOTE_ADDR'] ?? '', ['127.0.0.1', '::1'], true)
    || ($_SERVER['HTTP_HOST'] ?? '') !== '127.0.0.1:8082'
    || !in_array($_SERVER['REQUEST_METHOD'], ['GET', 'HEAD'], true)
    || parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH) !== '/') {
    http_response_code(403);
    exit('Este visor solo está disponible en http://127.0.0.1:8082/.');
}
require_once dirname(__DIR__, 2) . '/src/business_logic/Apoyo.php';

/** Diseño lógico: (valor) -> escapar -> texto HTML seguro.
 * Descripción: Presenta los datos como texto, sin interpretar contenido guardado. */
function escapar(mixed $valor): string
{
    return htmlspecialchars($valor === null ? 'NULL' : (string)$valor, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
}

$consultas = [
    'USUARIOS' => 'SELECT id_usuario, nombre, email, rol, id_parque, activo, id_organizacion FROM USUARIOS ORDER BY id_usuario DESC LIMIT 100',
    'ORGANIZACIONES' => 'SELECT id_organizacion, nombre, llaveros_contratados, email_contacto, estado FROM ORGANIZACIONES LIMIT 100',
    'PARQUES' => 'SELECT id_parque, nombre, id_organizacion, cupo_llaveros, ubicacion FROM PARQUES LIMIT 100',
    'LLAVEROS' => 'SELECT id_llavero, identificador_ble, id_parque, id_organizacion, estado, id_usuario FROM LLAVEROS LIMIT 100',
    'MEDICIONES' => 'SELECT fecha_hora, id_llavero, nombre_micro, tipo, valor, unidad, latitud, longitud, simulada, id_usuario, id_parque, recibida_en FROM MEDICIONES ORDER BY fecha_hora DESC LIMIT 100',
    'SOLICITUDES_CONTACTO' => 'SELECT id_solicitud, nombre, correo, organizacion, mensaje, recibida_en FROM SOLICITUDES_CONTACTO ORDER BY id_solicitud DESC LIMIT 100',
    'SESIONES' => 'SELECT id_usuario, expira_en FROM SESIONES ORDER BY expira_en DESC LIMIT 100',
    'TOKENS_ACCESO' => 'SELECT id_usuario, finalidad, expira_en, usado_en FROM TOKENS_ACCESO ORDER BY expira_en DESC LIMIT 100',
    'CORREOS_PENDIENTES' => 'SELECT id_correo, destinatario, asunto, enviado_en, intentos, creado_en FROM CORREOS_PENDIENTES ORDER BY id_correo DESC LIMIT 100',
    'LIMITES' => 'SELECT ventana, intentos FROM LIMITES ORDER BY ventana DESC LIMIT 100',
];
$tabla = $_GET['tabla'] ?? 'USUARIOS';
$modo = $_GET['modo'] ?? 'registros';
if (!is_string($tabla) || !isset($consultas[$tabla]) || !in_array($modo, ['registros', 'estructura'], true)) {
    http_response_code(400);
    exit('Selecciona una tabla y un modo válidos.');
}
try {
    $config = configuracion();
    if (($config['environment'] ?? '') !== 'local') {
        http_response_code(403);
        exit('Visor disponible solo en el entorno local.');
    }
    $db = conectar($config);
    $db->exec('SET TRANSACTION READ ONLY');
    $db->beginTransaction();
    $nombre = $db->query('SELECT DATABASE()')->fetchColumn();
    $total = (int)$db->query("SELECT COUNT(*) FROM `$tabla`")->fetchColumn();
    $q = $db->query($modo === 'estructura' ? "SHOW COLUMNS FROM `$tabla`" : $consultas[$tabla]);
    $columnas = [];
    for ($i = 0; $i < $q->columnCount(); $i++) $columnas[] = $q->getColumnMeta($i)['name'];
    $filas = $q->fetchAll();
    $db->commit();
} catch (Throwable $error) {
    http_response_code(503);
    exit('No se puede consultar MySQL. Comprueba que el entorno local está arrancado.');
}
?>
<!doctype html>
<html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>SENDA · Base de datos local</title>
<style>
*{box-sizing:border-box}body{margin:0;background:#f5f2eb;color:#1e3d2f;font:16px system-ui,sans-serif}header{padding:24px 32px;background:#1e3d2f;color:#f5f2eb;display:flex;justify-content:space-between;gap:20px;align-items:center}header strong{font-size:22px}header span{font-size:13px;color:#d3dece}main{padding:28px 32px}h1{font-size:24px;margin:28px 0 8px}p{line-height:1.6}form{display:flex;align-items:end;gap:16px;flex-wrap:wrap}label{display:grid;gap:8px;font-size:14px}select,button{font:inherit;padding:12px;border:1px solid #a9b9ae;border-radius:7px}select{background:white;color:#1e3d2f;max-width:100%}button{background:#1e3d2f;color:white;cursor:pointer}a{color:inherit}.tabla{overflow-x:auto;background:white;border-radius:10px;margin-top:24px}table{border-collapse:collapse;width:100%;font-size:14px}th,td{padding:13px 16px;text-align:left;border-bottom:1px solid #e0e7df;max-width:340px;overflow-wrap:anywhere}th{background:#d3dece;white-space:nowrap}td{vertical-align:top;min-width:90px}.nota{font-size:13px;color:#526a59}footer{padding:20px 32px;font-size:13px}@media(max-width:600px){header,main,footer{padding:20px}header{align-items:flex-start;flex-direction:column}label{max-width:100%}form{align-items:stretch;flex-direction:column}}
</style></head><body>
<header><strong>SENDA · Base de datos</strong><span>Local · Solo lectura · Contraseñas y tokens ocultos</span></header>
<main>
<form method="get"><label>Tabla<select name="tabla"><?php foreach ($consultas as $opcion => $sql): ?><option value="<?= escapar($opcion) ?>" <?= $opcion === $tabla ? 'selected' : '' ?>><?= escapar($opcion) ?></option><?php endforeach; ?></select></label>
<label>Mostrar<select name="modo"><option value="registros" <?= $modo === 'registros' ? 'selected' : '' ?>>Registros</option><option value="estructura" <?= $modo === 'estructura' ? 'selected' : '' ?>>Estructura</option></select></label><button type="submit">Ver / actualizar</button></form>
<h1><?= escapar($tabla) ?></h1><p class="nota">Base <?= escapar($nombre) ?> · <?= $total ?> registros · Consulta del <?= escapar((new DateTimeImmutable('now', new DateTimeZone('Europe/Madrid')))->format('d/m/Y H:i:s')) ?></p>
<div class="tabla"><table><thead><tr><?php foreach ($columnas as $columna): ?><th scope="col"><?= escapar($columna) ?></th><?php endforeach; ?></tr></thead><tbody><?php foreach ($filas as $fila): ?><tr><?php foreach ($columnas as $columna): ?><td><?= escapar($fila[$columna]) ?></td><?php endforeach; ?></tr><?php endforeach; ?></tbody></table></div>
<?php if (!$filas): ?><p>Esta tabla todavía no tiene registros.</p><?php endif; ?>
<p class="nota"><?= $modo === 'registros' ? 'Se muestran hasta 100 filas. Pulsa Ver / actualizar para consultar de nuevo los datos actuales.' : 'Field: campo · Type: tipo · Null: admite vacío · Key: clave o índice · Default: valor inicial.' ?></p>
</main><footer>Herramienta de desarrollo local; no forma parte del área pública de OZORA.</footer></body></html>
