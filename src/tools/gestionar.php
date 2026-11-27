<?php
/*
 * Nombre: gestionar.php
 * Descripción: Operaciones locales de instalación y administración.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Operaciones locales de instalación y administración.
 */


declare(strict_types=1);
if (PHP_SAPI !== 'cli') {
    http_response_code(404);
    exit;
}
require_once dirname(__DIR__) . '/business_logic/Apoyo.php';
require_once dirname(__DIR__) . '/business_logic/Correo.php';



/** --------------------
 * Diseño lógico: db: Deposito --> instalarEsquema() -->
 * Descripción: Importa tablas solo en una base vacía; nunca elimina tablas existentes
 * -------------------- */
function instalarEsquema(PDO $db): void
{
    if ($db->query('SHOW TABLES')->fetch()) {
        throw new RuntimeException('La base ya tiene tablas. No se ha modificado.');
    }
    $sql = file_get_contents(dirname(__DIR__) . '/database/database.sql');
    $db->exec($sql);
    actualizarContacto($db);
    actualizarContrasenas($db);
    actualizarRoles($db);
    actualizarSoporte($db);
    echo "Esquema instalado.\n";
}



/** --------------------
 * Diseño lógico: db: Deposito --> actualizarContacto() -->
 * Descripción: Añade la tabla de contacto a una instalación anterior sin borrar ni alterar datos
 * -------------------- */
function actualizarSoporte(PDO $db): void
{
    // Tablas del soporte técnico tipo foro. Idempotente: no toca datos existentes.
    $db->exec(file_get_contents(dirname(__DIR__) . '/database/migrations/007_soporte.sql'));
    echo "Tablas de soporte preparadas.\n";
}



/** --------------------
 * Diseño lógico: db: Deposito, correo: Text, nombre: Text --> crearTecnico() -->
 * Descripción: Crea una cuenta de técnico (rol proveedor) con contraseña temporal segura; debe cambiarla al entrar
 * -------------------- */
function crearTecnico(PDO $db, string $correo, string $nombre): void
{
    $correo = validarCorreo($correo);
    $nombre = trim($nombre) !== '' ? trim($nombre) : 'Soporte técnico OZORA';
    // Contraseña temporal que cumple las reglas (mayúscula, minúscula, número y símbolo).
    do {
        $temporal = 'Ar-' . strtoupper(bin2hex(random_bytes(3))) . '#' . bin2hex(random_bytes(3)) . 'k';
        try { validarContrasena($temporal, $correo); $valida = true; } catch (ErrorDominio $e) { $valida = false; }
    } while (!$valida);
    $q = $db->prepare('INSERT INTO USUARIOS (nombre, email, password_hash, debe_cambiar_contrasena, id_rol)
        VALUES (?, ?, ?, 1, (SELECT id_rol FROM ROLES WHERE codigo = ?))');
    $q->execute([$nombre, $correo, password_hash($temporal, PASSWORD_BCRYPT), 'proveedor']);
    echo "Cuenta de técnico creada: $correo\nContraseña temporal: $temporal\n"
        . "Entra en /acceso con ella; te pedirá cambiarla por una propia.\n";
}



function actualizarContacto(PDO $db): void
{
    if (!$db->query("SHOW TABLES LIKE 'SOLICITUDES_CONTACTO'")->fetch()) {
        $db->exec(file_get_contents(dirname(__DIR__) . '/database/migrations/001_solicitudes_contacto.sql'));
        echo "Tabla de contacto creada.\n";
    } else {
        echo "La tabla de contacto ya existe.\n";
    }
}



/** --------------------
 * Diseño lógico: db: Deposito --> actualizarContrasenas() -->
 * Descripción: Añade el estado de contraseña inicial sin modificar las contraseñas existentes
 * -------------------- */
function actualizarContrasenas(PDO $db): void
{
    if (!$db->query("SHOW COLUMNS FROM USUARIOS LIKE 'debe_cambiar_contrasena'")->fetch()) {
        $db->exec(file_get_contents(dirname(__DIR__) . '/database/migrations/002_contrasena_inicial.sql'));
        echo "Estado de contraseña inicial añadido.\n";
    } else {
        echo "El estado de contraseña inicial ya existe.\n";
    }
}



/** --------------------
 * Diseño lógico: db: Deposito --> actualizarRoles() -->
 * Descripción: Migra una instalación anterior; no repite una migración completada o parcial
 * -------------------- */
function actualizarRoles(PDO $db): void
{
    $antiguo = $db->query("SHOW COLUMNS FROM USUARIOS LIKE 'rol'")->fetch();
    $catalogo = $db->query("SHOW TABLES LIKE 'ROLES'")->fetch();
    if (!$antiguo && $catalogo) {
        echo "El catálogo de roles ya está instalado.\n";
        return;
    }
    if ($catalogo || !$antiguo) {
        throw new RuntimeException('El esquema de roles necesita revisión; no se ha repetido la migración.');
    }
    $db->exec(file_get_contents(dirname(__DIR__) . '/database/migrations/003_roles.sql'));
    echo "Roles migrados; cuentas y contraseñas conservadas.\n";
}



/** --------------------
 * Diseño lógico: db: Deposito, config: [Dato] --> crearDemo() -->
 * Descripción: Añade un parque ficticio y dos cuentas con contraseña suministrada por entorno
 * -------------------- */
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
        $q = $db->prepare('INSERT INTO USUARIOS (nombre, email, password_hash, id_rol, id_parque, id_organizacion)
            VALUES (?, ?, ?, (SELECT id_rol FROM ROLES WHERE codigo = ?), ?, ?)');
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
        case 'actualizar-roles': actualizarRoles($db); break;
        case 'actualizar-perfil':
            foreach (['ROLES' => "descripcion VARCHAR(255) NOT NULL DEFAULT ''",
                      'USUARIOS' => 'foto_perfil MEDIUMTEXT NULL'] as $tabla => $definicion) {
                $columna = strtok($definicion, ' ');
                if (!$db->query("SHOW COLUMNS FROM $tabla LIKE '$columna'")->fetch())
                    $db->exec("ALTER TABLE $tabla ADD COLUMN $definicion");
            }
            $db->exec("UPDATE ROLES SET descripcion = CASE codigo
    WHEN 'proveedor' THEN 'Administración del servicio y organizaciones clientes'
    WHEN 'organizacion' THEN 'Gestión de parques y responsables de la organización'
    WHEN 'jefe_parque' THEN 'Supervisión del parque y gestión de trabajadores'
    WHEN 'trabajador' THEN 'Captura de mediciones y recorridos de campo'
    ELSE descripcion END;
");
            echo "Perfil y descripción de roles preparados.\n";
            break;
        case 'correo': echo entregarCorreos($db, $config) . " correos entregados.\n"; break;
        case 'actualizar-contacto': actualizarContacto($db); break;
        case 'actualizar-contrasenas': actualizarContrasenas($db); break;
        case 'actualizar-soporte': actualizarSoporte($db); break;
        case 'crear-tecnico': crearTecnico($db, $argv[2] ?? '', $argv[3] ?? ''); break;
        case 'solicitudes':
            // Solo consola del administrador. Nunca publicar solicitudes mediante una ruta GET.
            echo json_encode($db->query('SELECT * FROM SOLICITUDES_CONTACTO ORDER BY recibida_en DESC LIMIT 50')->fetchAll(), JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "\n";
            break;
        case 'limpiar':
            // Solo registros técnicos caducados. No toca personas ni mediciones.
            $db->exec('DELETE FROM SESIONES WHERE expira_en < UTC_TIMESTAMP(3)');
            $db->exec('DELETE FROM TOKENS_ACCESO WHERE expira_en < UTC_TIMESTAMP(3)');
            $db->exec('DELETE FROM LIMITES WHERE ventana < UTC_TIMESTAMP() - INTERVAL 1 DAY');
            $db->exec('DELETE FROM CORREOS_PENDIENTES WHERE enviado_en IS NOT NULL AND enviado_en < UTC_TIMESTAMP(3) - INTERVAL 1 DAY');
            echo "Registros técnicos caducados eliminados.\n";
            break;
        default: echo "Uso: php src/tools/gestionar.php instalar|demo|correo|limpiar|actualizar-contacto|actualizar-contrasenas|actualizar-roles|actualizar-perfil|solicitudes\n"; exit(1);
    }
} catch (Throwable $error) {
    fwrite(STDERR, 'Error de administración: ' . $error->getMessage() . "\n");
    exit(1);
}
