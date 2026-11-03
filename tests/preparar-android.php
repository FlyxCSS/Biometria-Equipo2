<?php
/*
 * Nombre: preparar-android.php
 * Descripción: Cuentas y llaveros ficticios para comprobar Java contra PHP y MySQL.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026.
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Preparar una prueba reproducible sin credenciales reales ni base de producción.
 */
declare(strict_types=1);
require dirname(__DIR__) . '/src/business_logic/Apoyo.php';
$config = configuracion();
$db = conectar($config);
if (($config['environment'] ?? '') !== 'local'
    || !str_ends_with($db->query('SELECT DATABASE()')->fetchColumn(), '_test')) {
    throw new RuntimeException('Solo se permiten bases locales terminadas en _test.');
}
$salida = ['base' => rtrim($config['app_url'], '/') . '/api/v1'];
$db->beginTransaction();
try {
    foreach (['debug', 'release'] as $variante) {
        $sufijo = bin2hex(random_bytes(6));
        $q = $db->prepare('INSERT INTO ORGANIZACIONES (nombre,llaveros_contratados,email_contacto) VALUES (?,1,?)');
        $q->execute(['Prueba Android ' . $sufijo, 'contacto-' . $sufijo . '@example.org']);
        $org = (int)$db->lastInsertId();
        $q = $db->prepare('INSERT INTO PARQUES (nombre,id_organizacion,cupo_llaveros) VALUES (?, ?,1)');
        $q->execute(['Parque Android ' . $sufijo, $org]);
        $parque = (int)$db->lastInsertId();
        $inicial = 'Inicial-' . bin2hex(random_bytes(12));
        $nueva = 'Nueva-' . bin2hex(random_bytes(12));
        $jefe = 'Jefe-' . bin2hex(random_bytes(12));
        $correo = 'android-' . $sufijo . '@example.org';
        $correoJefe = 'jefe-android-' . $sufijo . '@example.org';
        $q = $db->prepare('INSERT INTO USUARIOS (nombre,email,password_hash,debe_cambiar_contrasena,id_rol,id_parque,id_organizacion)
            VALUES (?, ?, ?, ?, (SELECT id_rol FROM ROLES WHERE codigo=?), ?, ?)');
        $q->execute(['Trabajador Android de prueba', $correo, password_hash($inicial, PASSWORD_BCRYPT), 1, 'trabajador', $parque, $org]);
        $trabajador = (int)$db->lastInsertId();
        $q->execute(['Jefe Android de prueba', $correoJefe, password_hash($jefe, PASSWORD_BCRYPT), 0, 'jefe_parque', $parque, $org]);
        do {
            $ble = 'S-' . random_int(100000, 999999);
            $q = $db->prepare('SELECT id_llavero FROM LLAVEROS WHERE identificador_ble=?');
            $q->execute([$ble]);
        } while ($q->fetch());
        $q = $db->prepare('INSERT INTO LLAVEROS (id_llavero,identificador_ble,id_parque,id_organizacion,id_usuario) VALUES (?, ?, ?, ?, ?)');
        $q->execute(['ANDROID-' . $sufijo, $ble, $parque, $org, $trabajador]);
        $salida[$variante] = ['correo' => $correo, 'inicial' => $inicial, 'nueva' => $nueva,
            'correoJefe' => $correoJefe, 'claveJefe' => $jefe, 'parque' => $parque, 'ble' => $ble];
    }
    $db->commit();
} catch (Throwable $error) {
    $db->rollBack();
    throw $error;
}
$ruta = dirname(__DIR__) . '/.local/android-contract.json';
if (!is_dir(dirname($ruta))) mkdir(dirname($ruta), 0700, true);
file_put_contents($ruta, json_encode($salida, JSON_THROW_ON_ERROR));
chmod($ruta, 0600);
echo "Fixture Android preparada en .local/android-contract.json; no contiene cuentas reales.\n";
