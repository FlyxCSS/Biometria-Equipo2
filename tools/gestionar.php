<?php
/*
 * Nombre: gestionar.php
 * Descripción: Instalación de esquema, datos ficticios y entrega de correos por consola.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Operaciones reproducibles sin exponer administración por HTTP.
 */
declare(strict_types=1);
if (PHP_SAPI !== 'cli') {
    http_response_code(404);
    exit;
}
require_once dirname(__DIR__) . '/src/business_logic/Apoyo.php';
require_once dirname(__DIR__) . '/src/business_logic/Correo.php';

/** Diseño lógico: (PDO) -> instalarEsquema -> vacío.
 * Descripción: Importa tablas solo en una base vacía; nunca elimina tablas existentes. */
function instalarEsquema(PDO $db): void
{
    if ($db->query('SHOW TABLES')->fetch()) {
        throw new RuntimeException('La base ya tiene tablas. No se ha modificado.');
    }
    $sql = file_get_contents(dirname(__DIR__) . '/src/database/database.sql');
    $db->exec($sql);
    echo "Esquema instalado.\n";
}

/** Diseño lógico: (PDO, configuración) -> crearDemo -> vacío.
 * Descripción: Añade un parque ficticio y dos cuentas con contraseña suministrada por entorno. */
function crearDemo(PDO $db, array $config): void
{
    if (($config['environment'] ?? '') !== 'local') {
        throw new RuntimeException('Los datos de demostración solo se crean en entorno local.');
    }
    $contrasena = validarContrasena(getenv('SENDA_DEMO_PASSWORD') ?: null);
    $db->beginTransaction();
    try {
        if ($db->query("SELECT id_usuario FROM USUARIOS WHERE email IN ('jefe@example.org', 'trabajador@example.org')")->fetch()) {
            throw new RuntimeException('Las cuentas de demostración ya existen. No se han sobrescrito.');
        }
        $db->exec("INSERT INTO ORGANIZACIONES (nombre, llaveros_contratados, email_contacto) VALUES ('Organización de prueba', 2, 'contacto@example.org')");
        $organizacion = (int)$db->lastInsertId();
        $q = $db->prepare('INSERT INTO PARQUES (nombre, id_organizacion, cupo_llaveros, ubicacion) VALUES (?, ?, 2, ?)');
        $q->execute(['Parque de prueba', $organizacion, 'Entorno simulado']);
        $parque = (int)$db->lastInsertId();
        $q = $db->prepare('INSERT INTO USUARIOS (nombre, email, password_hash, rol, id_parque, id_organizacion) VALUES (?, ?, ?, ?, ?, ?)');
        $hash = password_hash($contrasena, PASSWORD_BCRYPT);
        $q->execute(['Jefe de prueba', 'jefe@example.org', $hash, 'jefe_parque', $parque, $organizacion]);
        $q->execute(['Trabajador de prueba', 'trabajador@example.org', $hash, 'trabajador', $parque, $organizacion]);
        $trabajador = (int)$db->lastInsertId();
        $q = $db->prepare('INSERT INTO LLAVEROS (id_llavero, identificador_ble, id_parque, id_organizacion, id_usuario) VALUES (?, ?, ?, ?, ?)');
        $q->execute(['DEMO-S-001', 'S-001', $parque, $organizacion, $trabajador]);
        $db->commit();
        echo "Datos ficticios creados. Cuentas: jefe@example.org y trabajador@example.org. Parque $parque. BLE S-001.\n";
    } catch (Throwable $error) {
        $db->rollBack();
        throw $error;
    }
}

try {
    $config = configuracion();
    $db = conectar($config);
    switch ($argv[1] ?? '') {
        case 'instalar': instalarEsquema($db); break;
        case 'demo': crearDemo($db, $config); break;
        case 'correo': echo entregarCorreos($db, $config) . " correos entregados.\n"; break;
        case 'limpiar':
            // Solo registros técnicos caducados. No toca personas ni mediciones.
            $db->exec('DELETE FROM SESIONES WHERE expira_en < UTC_TIMESTAMP(3)');
            $db->exec('DELETE FROM TOKENS_ACCESO WHERE expira_en < UTC_TIMESTAMP(3)');
            $db->exec('DELETE FROM LIMITES WHERE ventana < UTC_TIMESTAMP() - INTERVAL 1 DAY');
            $db->exec('DELETE FROM CORREOS_PENDIENTES WHERE enviado_en IS NOT NULL AND enviado_en < UTC_TIMESTAMP(3) - INTERVAL 1 DAY');
            echo "Registros técnicos caducados eliminados.\n";
            break;
        default: echo "Uso: php tools/gestionar.php instalar|demo|correo|limpiar\n"; exit(1);
    }
} catch (Throwable $error) {
    fwrite(STDERR, 'Error de administración: ' . $error->getMessage() . "\n");
    exit(1);
}
