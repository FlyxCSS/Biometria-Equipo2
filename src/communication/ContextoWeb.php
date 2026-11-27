<?php
/*
 * Nombre: ContextoWeb.php
 * Descripción: Resuelve rutas de instalación y adapta errores al transporte.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 19/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Resuelve rutas de instalación y adapta errores al transporte.
 */

declare(strict_types=1);
require_once dirname(__DIR__) . '/business_logic/Apoyo.php';



/** --------------------
 * Diseño lógico: rutaBaseAplicacion() -->
 * Text <--
 * Descripción: Permite instalar SENDA en su propia carpeta, sin cambiar otras webs del dominio
 * -------------------- */
function rutaBaseAplicacion(): string
{
    $base = defined('SENDA_BASE_PATH') ? SENDA_BASE_PATH : (getenv('SENDA_BASE_PATH') ?: '');
    if (!is_string($base) || !preg_match('~^(?:/[a-zA-Z0-9_-]+)*$~D', $base)) {
        throw new RuntimeException('Ruta base de instalación inválida');
    }
    return $base;
}



/** --------------------
 * Diseño lógico: rutaPeticionAplicacion() -->
 * Text <--
 * Descripción: Retira únicamente el prefijo de esta instalación, respetando los demás proyectos
 * -------------------- */
function rutaPeticionAplicacion(): ?string
{
    $ruta = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
    $base = rutaBaseAplicacion();
    if (!is_string($ruta) || ($base !== '' && $ruta !== $base && !str_starts_with($ruta, $base . '/'))) return null;
    return substr($ruta, strlen($base)) ?: '/';
}


final class ErrorApi extends RuntimeException
{

    /** --------------------
     * Diseño lógico: estado: Z, codigo: Text, mensaje: Text, campos: [Dato] --> __construct() -->
     * Dato <--
     * Descripción: Construye ErrorApi. Adaptación de entradas y salidas entre clientes y negocio
     * -------------------- */
    public function __construct(public int $estado, public string $codigo, string $mensaje, public array $campos = [])
    { parent::__construct($mensaje); }
}


/** --------------------
 * Diseño lógico: error: ErrorDominio --> estadoErrorDominio() -->
 * Z <--
 * Descripción: Estado Error Dominio. Resuelve rutas de instalación y adapta errores al transporte
 * -------------------- */
function estadoErrorDominio(ErrorDominio $error): int
{
    return match ($error->codigo) {
        'DATOS_INVALIDOS' => 422,
        'DEMASIADOS_INTENTOS' => 429,
        'CREDENCIALES_INVALIDAS' => 401,
        'CUENTA_NO_EXISTE' => 404,
        'CONTRASENA_INCORRECTA' => 401,
        'CUENTA_DESACTIVADA' => 403,
        'CONTRASENA_DEBIL' => 422,
        'CORREO_INEXISTENTE' => 422,
        'HILO_NO_EXISTE' => 404,
        'SESION_INVALIDA' => 401,
        'SIN_PERMISO' => 403,
        'FOTO_INVALIDA' => 422,
        'LLAVERO_INVALIDO' => 422,
        'YA_TIENE_LLAVERO' => 409,
        'LLAVERO_NO_DISPONIBLE' => 404,
        'LLAVERO_OCUPADO' => 409,
        'LLAVERO_NO_AUTORIZADO' => 403,
        'MEDICION_EN_CONFLICTO' => 409,
        'CURSOR_INVALIDO' => 422,
        'PARQUE_NO_DISPONIBLE' => 404,
        'ALTA_NO_DISPONIBLE' => 409,
        'CONTRASENA_ACTUAL_INVALIDA' => 403,
        'ENLACE_INVALIDO' => 422,
        default => 422,
    };
}
