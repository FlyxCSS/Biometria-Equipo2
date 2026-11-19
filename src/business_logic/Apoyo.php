<?php
/*
 * Nombre: Apoyo.php
 * Descripción: Define errores de dominio, validaciones y conexión configurada.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Define errores de dominio, validaciones y conexión configurada.
 */


declare(strict_types=1);

final class ErrorDominio extends RuntimeException
{

    /** --------------------
     * Diseño lógico: codigo: Text, mensaje: Text, campos: [Dato] --> __construct() -->
     * Dato <--
     * Descripción: Construye ErrorDominio. Reglas de negocio, autorización y persistencia de SENDA
     * -------------------- */
    public function __construct(public string $codigo, string $mensaje, public array $campos = [])
    {
        parent::__construct($mensaje);
    }
}



/** --------------------
 * Diseño lógico: configuracion() -->
 * [Dato] <--
 * Descripción: Lee la configuración privada local, nunca servida por HTTP
 * -------------------- */
function configuracion(): array
{
    $archivo = getenv('SENDA_CONFIG');
    if (!$archivo) {
        $candidatos = [
            dirname(__DIR__) . '/config/local.php',
            dirname(__DIR__, 2) . '/config/local.php',
        ];
        foreach ($candidatos as $c) {
            if (is_file($c)) {
                $archivo = $c;
                break;
            }
        }
        $archivo ??= $candidatos[0];
    }
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



/** --------------------
 * Diseño lógico: config: [Dato] --> conectar() -->
 * Deposito <--
 * Descripción: Abre MySQL con parámetros reales y fechas UTC
 * -------------------- */
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



/** --------------------
 * Diseño lógico: datos: [Dato], campos: [Dato] --> exigirCampos() -->
 * Descripción: Rechaza campos ausentes y desconocidos para evitar contratos ambiguos
 * -------------------- */
function exigirCampos(array $datos, array $campos): void
{
    if (array_diff($campos, array_keys($datos)) || array_diff(array_keys($datos), $campos)) {
        throw new ErrorDominio('DATOS_INVALIDOS', 'Los campos no coinciden con el contrato.');
    }
}



/** --------------------
 * Diseño lógico: valor: Dato --> validarCorreo() -->
 * Text <--
 * Descripción: Valida el correo ASCII y respeta el tamaño del esquema del equipo
 * -------------------- */
function validarCorreo(mixed $valor): string
{
    if (!is_string($valor) || strlen($valor) > 100 || !filter_var(trim($valor), FILTER_VALIDATE_EMAIL)) {
        throw new ErrorDominio('DATOS_INVALIDOS', 'Introduce un correo válido.', ['correo' => 'Correo inválido.']);
    }
    return strtolower(trim($valor));
}



/** --------------------
 * Diseño lógico: valor: Dato --> validarContrasena() -->
 * Text <--
 * Descripción: Impide contraseñas demasiado cortas o truncadas por bcrypt
 * -------------------- */
function validarContrasena(mixed $valor): string
{
    if (!is_string($valor) || strlen($valor) < 12 || strlen($valor) > 72 || str_contains($valor, "\0")) {
        throw new ErrorDominio('DATOS_INVALIDOS', 'La contraseña debe ocupar entre 12 y 72 bytes UTF-8.');
    }
    return $valor;
}



/** --------------------
 * Diseño lógico: fecha: Text --> fechaJson() -->
 * Text <--
 * Descripción: Conserva los milisegundos al responder a Android
 * -------------------- */
function fechaJson(string $fecha): string
{
    return (new DateTimeImmutable($fecha, new DateTimeZone('UTC')))->format('Y-m-d\TH:i:s.v\Z');
}



/** --------------------
 * Diseño lógico: datos: [Dato] --> validarMedicion() -->
 * [Dato] <--
 * Descripción: Valida una captura geolocalizada O3 sin inventar ubicación ni unidad
 * -------------------- */
function validarMedicion(array $datos): array
{
    exigirCampos($datos, ['nombre_micro', 'tipo', 'valor', 'unidad', 'fecha_hora', 'latitud', 'longitud', 'simulada']);
    $errores = [];
    if (!is_string($datos['nombre_micro']) || !preg_match('/^S-[0-9]{3,6}$/D', $datos['nombre_micro'])) {
        $errores['nombre_micro'] = 'Identificador inválido.';
    }
    $temperatura = $datos['tipo'] === 'TEMPERATURA';
    if (!(($datos['tipo'] === 'O3' && $datos['unidad'] === 'ppb')
        || ($temperatura && $datos['unidad'] === 'C'))) {
        $errores['tipo'] = 'Se admite O3 en ppb o TEMPERATURA en C.';
    }
    if ($temperatura) {
        if ((!is_int($datos['valor']) && !is_float($datos['valor']))
            || !is_finite((float)$datos['valor']) || $datos['valor'] < -40 || $datos['valor'] > 50
            || abs($datos['valor'] * 100 - round($datos['valor'] * 100)) > 0.000001) {
            $errores['valor'] = 'Temperatura entre -40 y 50 C, con hasta dos decimales.';
        }
    } elseif (!is_int($datos['valor']) || $datos['valor'] < 0 || $datos['valor'] > 65535) {
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
        throw new ErrorDominio('DATOS_INVALIDOS', 'Revisa los campos indicados.', $errores);
    }
    $datos['fecha_sql'] = $fecha->format('Y-m-d H:i:s.v');
    return $datos;
}
