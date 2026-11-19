<?php
/*
 * Nombre: validacion.php
 * Descripción: Comprobación automática de contratos, límites y aislamiento.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Comprobación automática de contratos, límites y aislamiento.
 */


declare(strict_types=1);
require_once dirname(__DIR__) . '/communication/ContextoWeb.php';
require_once dirname(__DIR__) . '/business_logic/Apoyo.php';
$comprobaciones = 0;



/** --------------------
 * Diseño lógico: condicion: B, descripcion: Text --> comprobar() -->
 * Descripción: Cuenta verificaciones y detiene una prueba que no cumple el resultado esperado
 * -------------------- */
function comprobar(bool $condicion, string $descripcion): void
{
    global $comprobaciones;
    if (!$condicion) {
        throw new RuntimeException($descripcion);
    }
    $comprobaciones++;
}



/** --------------------
 * Diseño lógico: lectura: [Dato] --> esperarRechazo() -->
 * Descripción: Exige que una entrada incorrecta produzca validación 422
 * -------------------- */
function esperarRechazo(array $lectura): void
{
    try {
        validarMedicion($lectura);
    } catch (ErrorDominio $error) {
        comprobar(estadoErrorDominio($error) === 422, 'El error debe ser 422.');
        return;
    }
    throw new RuntimeException('Se aceptó una entrada inválida.');
}

$lectura = ['nombre_micro' => 'S-001', 'tipo' => 'O3', 'valor' => 42, 'unidad' => 'ppb',
    'fecha_hora' => '2026-01-02T12:00:00.123Z', 'latitud' => 39.4, 'longitud' => -0.3, 'simulada' => true];
comprobar(validarMedicion($lectura)['fecha_sql'] === '2026-01-02 12:00:00.123', 'Se conservan milisegundos.');
comprobar(fechaJson('2026-01-02 12:00:00.123') === $lectura['fecha_hora'], 'Conversión UTC simétrica.');
foreach ([['valor', -1], ['valor', 65536], ['valor', 42.5], ['valor', '42'], ['valor', true],
    ['latitud', 91], ['latitud', null], ['latitud', INF], ['longitud', -181], ['longitud', '0'],
    ['simulada', 1], ['tipo', 'CO2'], ['unidad', 'ppm'], ['nombre_micro', 's-001'],
    ['fecha_hora', '2026-02-30T00:00:00.000Z'], ['fecha_hora', '2026-01-02T12:00:00Z'],
    ['fecha_hora', '2026-01-02T12:00:00.000+01:00'], ['fecha_hora', '9999-01-01T00:00:00.000Z']] as [$campo, $valor]) {
    $invalida = $lectura;
    $invalida[$campo] = $valor;
    esperarRechazo($invalida);
}
$invalida = $lectura;
unset($invalida['latitud']);
esperarRechazo($invalida);
$invalida = $lectura;
$invalida['id_parque'] = 999;
esperarRechazo($invalida);
foreach ([0, 65535] as $valor) {
    $limite = $lectura;
    $limite['valor'] = $valor;
    $limite['latitud'] = -90;
    $limite['longitud'] = 180;
    comprobar(validarMedicion($limite)['valor'] === $valor, 'Los extremos técnicos son válidos.');
}
comprobar(validarCorreo(' Julia@example.org ') === 'julia@example.org', 'Correo normalizado.');
echo "$comprobaciones comprobaciones de validación correctas.\n";

$temperatura = array_replace($lectura, ['tipo' => 'TEMPERATURA', 'unidad' => 'C', 'valor' => 29.39,
    'latitud' => 39.4, 'longitud' => -0.3]);
foreach ([29.39, -5.25, -40, 0, 50] as $valor) {
    $valida = array_replace($temperatura, ['valor' => $valor]);
    comprobar(validarMedicion($valida)['valor'] === $valor, 'Temperatura sin perder decimales.');
}
comprobar(validarMedicion(array_replace($temperatura, ['latitud' => 39.4, 'longitud' => -0.3]))['valor'] === 29.39,
    'Temperatura con posición también válida.');
foreach ([['valor', -40.01], ['valor', 50.01], ['valor', 29.391], ['valor', '29.39'],
    ['valor', true], ['valor', INF], ['valor', NAN], ['unidad', 'ppb'], ['latitud', null], ['longitud', null]] as [$campo, $valor]) {
    esperarRechazo(array_replace($temperatura, [$campo => $valor]));
}
esperarRechazo(array_replace($lectura, ['latitud' => null, 'longitud' => null]));
esperarRechazo(array_replace($temperatura, ['latitud' => null, 'longitud' => null]));
echo "Temperatura: validación correcta ($comprobaciones comprobaciones totales).\n";
