<?php
/*
 * Nombre: integracion.php
 * Descripción: Pruebas HTTP y MySQL reales del backend sobre una BD de pruebas.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Evidencia de permisos, persistencia, sesiones, alta y recuperación.
 */
declare(strict_types=1);
require_once dirname(__DIR__) . '/src/business_logic/Apoyo.php';
require_once dirname(__DIR__) . '/src/business_logic/Correo.php';
$config = configuracion();
$db = conectar($config);
$nombreBd = $db->query('SELECT DATABASE()')->fetchColumn();
if (($config['environment'] ?? '') !== 'local' || !str_ends_with($nombreBd, '_test')) {
    throw new RuntimeException('Ejecutar solo en entorno local y una base terminada en _test.');
}
$base = rtrim($config['app_url'], '/');
$comprobaciones = 0;

/** Diseño lógico: (condición, descripción) -> comprobar -> vacío o fallo.
 * Descripción: Cuenta aserciones y detiene el proceso si una expectativa falla. */
function comprobar(bool $condicion, string $descripcion): void
{
    global $comprobaciones;
    if (!$condicion) throw new RuntimeException($descripcion);
    $comprobaciones++;
}

/** Diseño lógico: (método, ruta, datos, token, estado esperado) -> pedir -> JSON.
 * Descripción: Hace una petición HTTP real y exige estado y formato coherentes. */
function pedir(string $metodo, string $ruta, mixed $datos, string $token, int $esperado, string $tipo = 'application/json'): array
{
    global $base;
    $cabeceras = "Content-Type: $tipo\r\nAccept: application/json\r\n";
    if ($token !== '') $cabeceras .= "Authorization: Bearer $token\r\n";
    $opciones = ['method' => $metodo, 'header' => $cabeceras, 'ignore_errors' => true, 'timeout' => 20];
    if ($datos !== null) $opciones['content'] = is_string($datos) ? $datos : json_encode($datos, JSON_THROW_ON_ERROR);
    $texto = file_get_contents($base . '/api/v1' . $ruta, false, stream_context_create(['http' => $opciones]));
    $cabecerasRespuesta = $http_response_header ?? [];
    preg_match('/\s(\d{3})\s/', $cabecerasRespuesta[0] ?? '', $estado);
    comprobar((int)($estado[1] ?? 0) === $esperado, "$metodo $ruta: se esperaba $esperado; recibido " . ($estado[1] ?? 'sin respuesta'));
    $json = json_decode($texto, true, 16, JSON_THROW_ON_ERROR);
    comprobar($json['ok'] === ($esperado < 400), 'El indicador ok coincide con el HTTP.');
    return $json;
}

/** Diseño lógico: (SQL, parámetros) -> sql -> PDOStatement.
 * Descripción: Prepara los datos ficticios y verifica las filas realmente guardadas. */
function sql(string $consulta, array $parametros = []): PDOStatement
{
    global $db;
    $q = $db->prepare($consulta);
    $q->execute($parametros);
    return $q;
}

/** Diseño lógico: (correo) -> tokenDelCorreo -> token.
 * Descripción: Lee solo el buzón ficticio privado para probar el uso del enlace. */
function tokenDelCorreo(string $correo): string
{
    global $db, $config;
    entregarCorreos($db, $config);
    $archivos = glob($config['mail_directory'] . '/*.eml');
    rsort($archivos, SORT_NATURAL);
    foreach ($archivos as $archivo) {
        $texto = file_get_contents($archivo);
        if (str_contains($texto, "To: $correo\n") && preg_match('/#token=([a-f0-9]{64})/', $texto, $coincidencia)) {
            return $coincidencia[1];
        }
    }
    throw new RuntimeException('No se ha recibido el correo de prueba.');
}

// Cada ejecución crea sus propios registros y no elimina bases ni información anterior.
$sufijo = bin2hex(random_bytes(5));
$password = 'PruebaDeIntegracion-2026';
$hash = password_hash($password, PASSWORD_BCRYPT);
sql('INSERT INTO ORGANIZACIONES (nombre, llaveros_contratados, email_contacto) VALUES (?, 4, ?)', ["Prueba $sufijo", "$sufijo@example.org"]);
$organizacion = (int)$db->lastInsertId();
sql('INSERT INTO ORGANIZACIONES (nombre, llaveros_contratados, email_contacto) VALUES (?, 4, ?)', ["Otra $sufijo", "otra$sufijo@example.org"]);
$otraOrganizacion = (int)$db->lastInsertId();
$parques = [];
foreach ([$organizacion, $otraOrganizacion] as $org) {
    sql('INSERT INTO PARQUES (nombre, id_organizacion, cupo_llaveros) VALUES (?, ?, 2)', ["Parque $sufijo", $org]);
    $parques[] = (int)$db->lastInsertId();
}
$cuentas = [];
foreach ([['jefe', 'jefe_parque', 0], ['trabajador', 'trabajador', 0], ['otro', 'trabajador', 1], ['jefeOtro', 'jefe_parque', 1], ['segundo', 'trabajador', 0]] as [$alias, $rol, $indice]) {
    $correo = "$alias-$sufijo@example.org";
    sql('INSERT INTO USUARIOS (nombre, email, password_hash, rol, id_parque, id_organizacion) VALUES (?, ?, ?, ?, ?, ?)',
        [$alias, $correo, $hash, $rol, $parques[$indice], $indice === 0 ? $organizacion : $otraOrganizacion]);
    $cuentas[$alias] = ['id' => (int)$db->lastInsertId(), 'correo' => $correo];
}
$ble = 'S-' . random_int(100000, 999999);
while (sql('SELECT id_llavero FROM LLAVEROS WHERE identificador_ble = ?', [$ble])->fetch()) $ble = 'S-' . random_int(100000, 999999);
$bleSegundo = 'S-' . random_int(100000, 999999);
while ($bleSegundo === $ble || sql('SELECT id_llavero FROM LLAVEROS WHERE identificador_ble = ?', [$bleSegundo])->fetch()) $bleSegundo = 'S-' . random_int(100000, 999999);
sql('INSERT INTO LLAVEROS (id_llavero, identificador_ble, id_parque, id_organizacion, id_usuario) VALUES (?, ?, ?, ?, ?)',
    ["LL-$sufijo", $ble, $parques[0], $organizacion, $cuentas['trabajador']['id']]);
sql('INSERT INTO LLAVEROS (id_llavero, identificador_ble, id_parque, id_organizacion, id_usuario) VALUES (?, ?, ?, ?, ?)',
    ["LL2-$sufijo", $bleSegundo, $parques[0], $organizacion, $cuentas['segundo']['id']]);

// Integridad de asignaciones: un usuario no puede recibir dos llaveros ni uno de otro parque.
try {
    sql('INSERT INTO LLAVEROS (id_llavero, identificador_ble, id_parque, id_organizacion, id_usuario) VALUES (?, ?, ?, ?, ?)',
        ["INVALIDO-$sufijo", "INVALIDO-$sufijo", $parques[1], $otraOrganizacion, $cuentas['trabajador']['id']]);
    throw new RuntimeException('Se aceptó una asignación incompatible.');
} catch (PDOException $error) {
    comprobar($error->getCode() === '23000', 'MySQL rechaza asignaciones incompatibles.');
}

pedir('POST', '/sesiones', ['correo' => $cuentas['trabajador']['correo'], 'contrasena' => 'incorrecta'], '', 401);
$tokens = [];
foreach ($cuentas as $alias => $cuenta) {
    $login = pedir('POST', '/sesiones', ['correo' => $cuenta['correo'], 'contrasena' => $password], '', 200);
    $tokens[$alias] = $login['data']['token'];
    comprobar(strlen($tokens[$alias]) === 64, 'Sesión aleatoria de 256 bits.');
    comprobar(sql('SELECT token_hash FROM SESIONES WHERE token_hash = ?', [hash('sha256', $tokens[$alias])])->fetch() !== false, 'Solo se persiste el hash de sesión.');
}
pedir('GET', '/sesiones/actual', null, '', 401);
pedir('GET', '/sesiones/actual', null, $tokens['trabajador'], 200);
pedir('GET', '/parques/' . $parques[0] . '/mediciones/ultima', null, $tokens['trabajador'], 403);
pedir('GET', '/parques/' . $parques[1] . '/mediciones/ultima', null, $tokens['jefe'], 404);
$vacia = pedir('GET', '/parques/' . $parques[0] . '/mediciones/ultima', null, $tokens['jefe'], 200);
comprobar($vacia['data']['medicion'] === null, 'Parque sin lecturas devuelve null.');

$medicion = ['nombre_micro' => $ble, 'tipo' => 'O3', 'valor' => 42, 'unidad' => 'ppb',
    'fecha_hora' => '2026-01-02T12:00:00.123Z', 'latitud' => 39.123456789012345, 'longitud' => -0.3763123456789012, 'simulada' => true];
pedir('POST', '/mediciones', $medicion, $tokens['otro'], 403);
pedir('POST', '/mediciones', $medicion, $tokens['jefe'], 403);
pedir('POST', '/mediciones', '{mal JSON', $tokens['trabajador'], 400);
pedir('POST', '/mediciones', '[]', $tokens['trabajador'], 400);
pedir('POST', '/mediciones', $medicion, $tokens['trabajador'], 415, 'text/plain');
pedir('POST', '/mediciones', str_repeat('x', 16385), $tokens['trabajador'], 413);
$mala = $medicion;
$mala['latitud'] = 91;
pedir('POST', '/mediciones', $mala, $tokens['trabajador'], 422);
comprobar((int)sql('SELECT COUNT(*) FROM MEDICIONES WHERE id_llavero = ?', ["LL-$sufijo"])->fetchColumn() === 0, 'Rechazos sin filas insertadas.');
$guardada = pedir('POST', '/mediciones', $medicion, $tokens['trabajador'], 201);
comprobar($guardada['data']['guardada'] && !$guardada['data']['duplicada'], 'Primera lectura confirmada.');
$reintento = pedir('POST', '/mediciones', $medicion, $tokens['trabajador'], 200);
comprobar($reintento['data']['duplicada'] === true, 'Reintento idempotente.');
comprobar((int)sql('SELECT COUNT(*) FROM MEDICIONES WHERE id_llavero = ?', ["LL-$sufijo"])->fetchColumn() === 1, 'Una sola fila tras reintentar.');
$mala = $medicion;
$mala['valor'] = 43;
pedir('POST', '/mediciones', $mala, $tokens['trabajador'], 409);
$fila = sql('SELECT * FROM MEDICIONES WHERE id_llavero = ?', ["LL-$sufijo"])->fetch();
comprobar($fila['fecha_hora'] === '2026-01-02 12:00:00.123' && (int)$fila['valor'] === 42, 'Fecha y valor intactos en MySQL.');
comprobar((float)$fila['latitud'] === $medicion['latitud'] && (float)$fila['longitud'] === $medicion['longitud'], 'Se conserva precisión de coordenadas en los reintentos.');
sql('UPDATE LLAVEROS SET estado = 0 WHERE id_llavero = ?', ["LL-$sufijo"]);
pedir('POST', '/mediciones', $medicion, $tokens['trabajador'], 403);
sql('UPDATE LLAVEROS SET estado = 1 WHERE id_llavero = ?', ["LL-$sufijo"]);

$anterior = $medicion;
$anterior['fecha_hora'] = '2026-01-01T12:00:00.000Z';
$anterior['valor'] = 99;
pedir('POST', '/mediciones', $anterior, $tokens['trabajador'], 201);
$ultima = pedir('GET', '/parques/' . $parques[0] . '/mediciones/ultima', null, $tokens['jefe'], 200);
comprobar($ultima['data']['medicion']['valor'] === 42, 'Orden de captura, no de recepción.');
comprobar($ultima['data']['medicion']['simulada'] === true, 'Procedencia simulada visible.');
$segunda = $medicion;
$segunda['nombre_micro'] = $bleSegundo;
$segunda['fecha_hora'] = '2026-01-03T12:00:00.000Z';
$segunda['valor'] = 55;
pedir('POST', '/mediciones', $segunda, $tokens['segundo'], 201);
$ultima = pedir('GET', '/parques/' . $parques[0] . '/mediciones/ultima', null, $tokens['jefe'], 200);
comprobar($ultima['data']['medicion']['nombre_micro'] === $bleSegundo, 'Última de cualquier llavero.');
$empate = $medicion;
$empate['fecha_hora'] = $segunda['fecha_hora'];
pedir('POST', '/mediciones', $empate, $tokens['trabajador'], 201);
$ultima = pedir('GET', '/parques/' . $parques[0] . '/mediciones/ultima', null, $tokens['jefe'], 200);
comprobar($ultima['data']['medicion']['nombre_micro'] === min($ble, $bleSegundo), 'Empate resuelto de forma estable.');

// Reasignar sin mover el histórico. La FK conserva integridad del nuevo parque.
sql('UPDATE LLAVEROS SET id_usuario = NULL, id_parque = ?, id_organizacion = ? WHERE id_llavero = ?', [$parques[1], $otraOrganizacion, "LL-$sufijo"]);
$otroParque = pedir('GET', '/parques/' . $parques[1] . '/mediciones/ultima', null, $tokens['jefeOtro'], 200);
comprobar($otroParque['data']['medicion'] === null, 'El histórico no migra al reasignar el llavero.');

$correoAlta = "alta-$sufijo@example.org";
pedir('POST', '/trabajadores', ['nombre' => 'Nueva persona', 'correo' => $correoAlta], $tokens['trabajador'], 403);
$alta = pedir('POST', '/trabajadores', ['nombre' => 'Nueva persona', 'correo' => $correoAlta], $tokens['jefe'], 201);
comprobar($alta['data']['usuario']['id_parque'] === $parques[0], 'Alta vinculada al parque de sesión.');
pedir('POST', '/trabajadores', ['nombre' => 'Duplicada', 'correo' => strtoupper($correoAlta)], $tokens['jefe'], 409);
pedir('POST', '/trabajadores', ['nombre' => 'Intrusión', 'correo' => "intrusion-$sufijo@example.org", 'id_parque' => $parques[1]], $tokens['jefe'], 422);
$tokenAlta = tokenDelCorreo($correoAlta);
pedir('POST', '/recuperaciones/confirmacion', ['token' => $tokenAlta, 'contrasena_nueva' => $password], '', 200);
pedir('POST', '/sesiones', ['correo' => $correoAlta, 'contrasena' => $password], '', 200);
pedir('POST', '/recuperaciones/confirmacion', ['token' => $tokenAlta, 'contrasena_nueva' => $password], '', 422);

$recuperacion = pedir('POST', '/recuperaciones', ['correo' => $cuentas['trabajador']['correo']], '', 202);
$inexistente = pedir('POST', '/recuperaciones', ['correo' => "nadie-$sufijo@example.org"], '', 202);
comprobar($recuperacion['data'] === $inexistente['data'], 'Respuesta genérica para cuentas inexistentes.');
$token = tokenDelCorreo($cuentas['trabajador']['correo']);
$nueva = 'NuevaContrasenaDePrueba-2026';
pedir('POST', '/recuperaciones/confirmacion', ['token' => $token, 'contrasena_nueva' => $nueva], '', 200);
pedir('GET', '/sesiones/actual', null, $tokens['trabajador'], 401);
pedir('POST', '/sesiones', ['correo' => $cuentas['trabajador']['correo'], 'contrasena' => $password], '', 401);
pedir('POST', '/sesiones', ['correo' => $cuentas['trabajador']['correo'], 'contrasena' => $nueva], '', 200);
pedir('POST', '/recuperaciones/confirmacion', ['token' => $token, 'contrasena_nueva' => $nueva], '', 422);

pedir('POST', '/recuperaciones', ['correo' => $correoAlta], '', 202);
$caducado = tokenDelCorreo($correoAlta);
sql('UPDATE TOKENS_ACCESO SET expira_en = UTC_TIMESTAMP() - INTERVAL 1 SECOND WHERE token_hash = ?', [hash('sha256', $caducado)]);
pedir('POST', '/recuperaciones/confirmacion', ['token' => $caducado, 'contrasena_nueva' => $nueva], '', 422);

pedir('POST', '/recuperaciones', ['correo' => $cuentas['otro']['correo']], '', 202);
$desactivado = tokenDelCorreo($cuentas['otro']['correo']);
sql('UPDATE USUARIOS SET activo = 0 WHERE id_usuario = ?', [$cuentas['otro']['id']]);
pedir('POST', '/recuperaciones/confirmacion', ['token' => $desactivado, 'contrasena_nueva' => $nueva], '', 422);
pedir('GET', '/sesiones/actual', null, $tokens['otro'], 401);
pedir('POST', '/sesiones', ['correo' => $cuentas['otro']['correo'], 'contrasena' => $password], '', 401);
pedir('POST', '/recuperaciones', ['correo' => $cuentas['otro']['correo']], '', 202);
comprobar((int)sql('SELECT activo FROM USUARIOS WHERE id_usuario = ?', [$cuentas['otro']['id']])->fetchColumn() === 0, 'Recuperar no reactiva cuentas.');
sql('UPDATE SESIONES SET expira_en = UTC_TIMESTAMP() - INTERVAL 1 SECOND WHERE token_hash = ?', [hash('sha256', $tokens['segundo'])]);
pedir('GET', '/sesiones/actual', null, $tokens['segundo'], 401);
pedir('DELETE', '/sesiones/actual', null, $tokens['jefe'], 200);
pedir('GET', '/sesiones/actual', null, $tokens['jefe'], 401);
pedir('PUT', '/mediciones', null, '', 405);
pedir('GET', '/no-existe', null, '', 404);

sql('UPDATE ORGANIZACIONES SET estado = ? WHERE id_organizacion = ?', ['inactivo', $otraOrganizacion]);
pedir('GET', '/sesiones/actual', null, $tokens['jefeOtro'], 401);
pedir('POST', '/sesiones', ['correo' => $cuentas['jefeOtro']['correo'], 'contrasena' => $password], '', 401);

// El servidor no publica ni fuentes ni herramientas, aunque se conozca su ruta.
foreach (['/config/local.php', '/.local/config-test.php', '/src/database/database.sql', '/tools/gestionar.php', '/.git/config'] as $privada) {
    $texto = file_get_contents($base . $privada, false, stream_context_create(['http' => ['ignore_errors' => true, 'timeout' => 10]]));
    comprobar(str_contains($http_response_header[0] ?? '', '404'), "Recurso privado inaccesible: $privada");
    comprobar($texto === 'Página no encontrada.', 'El rechazo no entrega contenido privado.');
}

// Forzar el límite de una cuenta ficticia, sin bloquear otras ejecuciones por IP.
$clave = hash('sha256', '/api/v1/sesiones:cuenta:limite-' . $sufijo . '@example.org');
sql('INSERT INTO LIMITES (clave, ventana, intentos) VALUES (?, UTC_TIMESTAMP(), 10)', [$clave]);
pedir('POST', '/sesiones', ['correo' => "limite-$sufijo@example.org", 'contrasena' => $password], '', 429);

echo "$comprobaciones comprobaciones HTTP/MySQL correctas.\n";
