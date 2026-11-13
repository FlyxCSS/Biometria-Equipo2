<?php
/*
 * Nombre: Apoyo.php
 * Descripción: Errores públicos, configuración, validación y conexión PDO.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Utilidades comunes sencillas para el backend.
 */
declare(strict_types=1);

final class ErrorApi extends RuntimeException
{
    /** Diseño lógico: (estado, código, mensaje, campos) -> __construct -> ErrorApi.
     * Descripción: Representa un rechazo controlado, sin detalles internos. */
    public function __construct(public int $estado, public string $codigo, string $mensaje, public array $campos = [])
    {
        parent::__construct($mensaje);
    }
}

/** Diseño lógico: () -> rutaBaseAplicacion -> prefijo de instalación.
 * Descripción: Permite instalar SENDA en su propia carpeta, sin cambiar otras webs del dominio. */
function rutaBaseAplicacion(): string
{
    $base = defined('SENDA_BASE_PATH') ? SENDA_BASE_PATH : (getenv('SENDA_BASE_PATH') ?: '');
    if (!is_string($base) || !preg_match('~^(?:/[a-zA-Z0-9_-]+)*$~D', $base)) {
        throw new RuntimeException('Ruta base de instalación inválida');
    }
    return $base;
}

/** Diseño lógico: () -> rutaPeticionAplicacion -> ruta relativa o null.
 * Descripción: Retira únicamente el prefijo de esta instalación, respetando los demás proyectos. */
function rutaPeticionAplicacion(): ?string
{
    $ruta = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
    $base = rutaBaseAplicacion();
    if (!is_string($ruta) || ($base !== '' && $ruta !== $base && !str_starts_with($ruta, $base . '/'))) return null;
    return substr($ruta, strlen($base)) ?: '/';
}

/** Diseño lógico: () -> configuracion -> array.
 * Descripción: Lee la configuración privada local, nunca servida por HTTP. */
function configuracion(): array
{
    $archivo = getenv('SENDA_CONFIG') ?: dirname(__DIR__) . '/config/local.php';
    if (!is_file($archivo)) {
        throw new RuntimeException('Falta src/config/local.php');
    }
    $config = require $archivo;
    if (!is_array($config) || !isset($config['dsn'], $config['db_user'], $config['db_password'], $config['app_url'])) {
        throw new RuntimeException('Configuración incompleta');
    }
    $url = parse_url($config['app_url']);
    if (!isset($url['host']) || !in_array($url['scheme'] ?? '', ['http', 'https'], true)
        || isset($url['user']) || isset($url['query']) || isset($url['fragment'])) {
        throw new RuntimeException('APP URL inválida');
    }
    if (($config['environment'] ?? 'production') !== 'local'
        && (($url['scheme'] ?? '') !== 'https' || ($config['mail_transport'] ?? '') !== 'mail')) {
        throw new RuntimeException('Producción requiere HTTPS y correo real');
    }
    return $config;
}

/** Diseño lógico: (configuración) -> conectar -> PDO.
 * Descripción: Abre MySQL con parámetros reales y fechas UTC. */
function conectar(array $config): PDO
{
    $pdo = new PDO($config['dsn'], $config['db_user'], $config['db_password'], [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false,
    ]);
    $pdo->exec("SET time_zone = '+00:00'");
    return $pdo;
}

/** Diseño lógico: (datos, lista de campos) -> exigirCampos -> vacío o error.
 * Descripción: Rechaza campos ausentes y desconocidos para evitar contratos ambiguos. */
function exigirCampos(array $datos, array $campos): void
{
    if (array_diff($campos, array_keys($datos)) || array_diff(array_keys($datos), $campos)) {
        throw new ErrorApi(422, 'DATOS_INVALIDOS', 'Los campos no coinciden con el contrato.');
    }
}

/** Diseño lógico: (valor) -> validarCorreo -> correo normalizado.
 * Descripción: Valida el correo ASCII y respeta el tamaño del esquema del equipo. */
function validarCorreo(mixed $valor): string
{
    if (!is_string($valor) || strlen($valor) > 100 || !filter_var(trim($valor), FILTER_VALIDATE_EMAIL)) {
        throw new ErrorApi(422, 'DATOS_INVALIDOS', 'Introduce un correo válido.', ['correo' => 'Correo inválido.']);
    }
    return strtolower(trim($valor));
}

/** Diseño lógico: (valor) -> validarContrasena -> contraseña.
 * Descripción: Impide contraseñas demasiado cortas o truncadas por bcrypt. */
function validarContrasena(mixed $valor): string
{
    if (!is_string($valor) || strlen($valor) < 12 || strlen($valor) > 72 || str_contains($valor, "\0")) {
        throw new ErrorApi(422, 'DATOS_INVALIDOS', 'La contraseña debe ocupar entre 12 y 72 bytes UTF-8.');
    }
    return $valor;
}

/** Diseño lógico: (fecha SQL UTC) -> fechaJson -> fecha ISO UTC.
 * Descripción: Conserva los milisegundos al responder a Android. */
function fechaJson(string $fecha): string
{
    return (new DateTimeImmutable($fecha, new DateTimeZone('UTC')))->format('Y-m-d\TH:i:s.v\Z');
}

/** Diseño lógico: (JSON de lectura) -> validarMedicion -> datos normalizados.
 * Descripción: Valida una captura geolocalizada O3 sin inventar ubicación ni unidad. */
function validarMedicion(array $datos): array
{
    exigirCampos($datos, ['nombre_micro', 'tipo', 'valor', 'unidad', 'fecha_hora', 'latitud', 'longitud', 'simulada']);
    $errores = [];
    if (!is_string($datos['nombre_micro']) || !preg_match('/^S-[0-9]{3,6}$/D', $datos['nombre_micro'])) {
        $errores['nombre_micro'] = 'Identificador inválido.';
    }
    if ($datos['tipo'] !== 'O3' || $datos['unidad'] !== 'ppb') {
        $errores['tipo'] = 'Esta versión admite O3 en ppb.';
    }
    if (!is_int($datos['valor']) || $datos['valor'] < 0 || $datos['valor'] > 65535) {
        $errores['valor'] = 'Debe ser un entero entre 0 y 65535.';
    }
    foreach (['latitud' => 90, 'longitud' => 180] as $campo => $limite) {
        if ((!is_int($datos[$campo]) && !is_float($datos[$campo]))
            || !is_finite((float)$datos[$campo]) || abs($datos[$campo]) > $limite) {
            $errores[$campo] = "Debe ser un número entre -$limite y $limite.";
        }
    }
    if (!is_bool($datos['simulada'])) {
        $errores['simulada'] = 'Debe ser un booleano.';
    }
    $fecha = is_string($datos['fecha_hora'])
        ? DateTimeImmutable::createFromFormat('!Y-m-d\TH:i:s.v\Z', $datos['fecha_hora'], new DateTimeZone('UTC')) : false;
    if (!$fecha || $fecha->format('Y-m-d\TH:i:s.v\Z') !== $datos['fecha_hora'] || (int)$fecha->format('Y') < 1000) {
        $errores['fecha_hora'] = 'Usa UTC con milisegundos: 2026-11-02T12:00:00.000Z.';
    } elseif ($fecha > new DateTimeImmutable('+5 minutes', new DateTimeZone('UTC'))) {
        $errores['fecha_hora'] = 'La captura no puede adelantarse más de cinco minutos al servidor.';
    }
    if ($errores) {
        throw new ErrorApi(422, 'DATOS_INVALIDOS', 'Revisa los campos indicados.', $errores);
    }
    $datos['fecha_sql'] = $fecha->format('Y-m-d H:i:s.v');
    return $datos;
}
