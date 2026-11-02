<?php
/*
 * Nombre: ServidorRest.php
 * Descripción: Entrada HTTP de la API REST v1 de SENDA.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Enrutado, JSON, códigos HTTP, autenticación y errores públicos.
 */
declare(strict_types=1);
require_once dirname(__DIR__) . '/business_logic/LogicaNegocio.php';

/** Diseño lógico: (estado, contenido) -> responderJson -> respuesta HTTP y fin.
 * Descripción: Envía JSON uniforme sin permitir que se almacenen credenciales en caché. */
function responderJson(int $estado, array $contenido): never
{
    http_response_code($estado);
    header('Content-Type: application/json; charset=UTF-8');
    header('Cache-Control: no-store');
    echo json_encode($contenido, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES | JSON_THROW_ON_ERROR);
    exit;
}

/** Diseño lógico: () -> leerJson -> objeto como array.
 * Descripción: Exige JSON pequeño y válido; no acepta listas como cuerpo de una operación. */
function leerJson(): array
{
    $tipo = strtolower(trim(explode(';', $_SERVER['CONTENT_TYPE'] ?? '')[0]));
    if ($tipo !== 'application/json') {
        throw new ErrorApi(415, 'CONTENIDO_NO_ADMITIDO', 'Envía application/json.');
    }
    $cuerpo = file_get_contents('php://input', false, null, 0, 16385);
    if ($cuerpo === false || strlen($cuerpo) > 16384) {
        throw new ErrorApi(413, 'PETICION_DEMASIADO_GRANDE', 'La petición supera el tamaño permitido.');
    }
    try {
        $objeto = json_decode($cuerpo, false, 16, JSON_THROW_ON_ERROR);
    } catch (JsonException $error) {
        throw new ErrorApi(400, 'JSON_INVALIDO', 'El cuerpo no contiene JSON válido.');
    }
    if (!$objeto instanceof stdClass) {
        throw new ErrorApi(400, 'JSON_INVALIDO', 'El cuerpo debe ser un objeto JSON.');
    }
    return (array)$objeto;
}

/** Diseño lógico: () -> atenderApi -> respuesta HTTP.
 * Descripción: Despacha solo las rutas conocidas con su método y permisos. */
function atenderApi(): never
{
    try {
        $ruta = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
        $metodo = $_SERVER['REQUEST_METHOD'];
        $rutas = [
            '/api/v1/sesiones' => ['POST'],
            '/api/v1/sesiones/actual' => ['GET', 'DELETE'],
            '/api/v1/mediciones' => ['POST'],
            '/api/v1/trabajadores' => ['POST'],
            '/api/v1/recuperaciones' => ['POST'],
            '/api/v1/recuperaciones/confirmacion' => ['POST'],
            '/api/v1/solicitudes' => ['POST'],
        ];
        $parque = null;
        if (preg_match('~^/api/v1/parques/([1-9][0-9]{0,8})/mediciones/ultima$~D', $ruta, $coincidencias)) {
            $parque = (int)$coincidencias[1];
            $rutas[$ruta] = ['GET'];
        }
        if (!isset($rutas[$ruta])) {
            throw new ErrorApi(404, 'RUTA_NO_ENCONTRADA', 'La ruta no existe.');
        }
        if (!in_array($metodo, $rutas[$ruta], true)) {
            header('Allow: ' . implode(', ', $rutas[$ruta]));
            throw new ErrorApi(405, 'METODO_NO_PERMITIDO', 'Método no permitido para esta ruta.');
        }
        $config = configuracion();
        if (($config['environment'] ?? 'production') !== 'local' && ($_SERVER['HTTPS'] ?? '') !== 'on') {
            throw new ErrorApi(400, 'HTTPS_REQUERIDO', 'Utiliza HTTPS.');
        }
        $db = conectar($config);
        $logica = new LogicaNegocio($db, $config);
        $datos = $metodo === 'POST' ? leerJson() : [];
        $estado = 200;
        if ($ruta === '/api/v1/solicitudes') {
            $correo = validarCorreo($datos['correo'] ?? null);
            $logica->limitar($ruta, $_SERVER['REMOTE_ADDR'] ?? 'desconocida', $correo);
            $resultado = $logica->registrarSolicitud($datos);
            $estado = 201;
        } elseif ($ruta === '/api/v1/sesiones' || $ruta === '/api/v1/recuperaciones') {
            $correo = validarCorreo($datos['correo'] ?? null);
            $logica->limitar($ruta, $_SERVER['REMOTE_ADDR'] ?? 'desconocida', $correo);
            if ($ruta === '/api/v1/sesiones') {
                $resultado = $logica->iniciarSesion($datos);
            } else {
                $resultado = $logica->solicitarRecuperacion($datos);
                $estado = 202;
            }
        } elseif ($ruta === '/api/v1/recuperaciones/confirmacion') {
            $logica->limitar($ruta, $_SERVER['REMOTE_ADDR'] ?? 'desconocida');
            $resultado = $logica->restablecerContrasena($datos);
        } else {
            $cabecera = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
            if (!preg_match('/^Bearer ([a-f0-9]{64})$/D', $cabecera, $coincidencias)) {
                throw new ErrorApi(401, 'SESION_INVALIDA', 'Inicia sesión de nuevo.');
            }
            $usuario = $logica->autenticar($coincidencias[1]);
            if ($ruta === '/api/v1/sesiones/actual') {
                $resultado = $metodo === 'GET' ? $logica->consultarSesion($usuario) : $logica->cerrarSesion($usuario);
            } elseif ($ruta === '/api/v1/mediciones') {
                $resultado = $logica->guardarMedicion($usuario, $datos);
                $estado = $resultado['duplicada'] ? 200 : 201;
            } elseif ($ruta === '/api/v1/trabajadores') {
                $resultado = $logica->registrarTrabajador($usuario, $datos);
                $estado = 201;
            } else {
                $resultado = $logica->ultimaMedicion($usuario, $parque);
            }
        }
        responderJson($estado, ['ok' => true, 'data' => $resultado]);
    } catch (ErrorApi $error) {
        if ($error->estado === 429) {
            header('Retry-After: 900');
        }
        $detalle = ['codigo' => $error->codigo, 'mensaje' => $error->getMessage()];
        if ($error->campos) {
            $detalle['campos'] = $error->campos;
        }
        responderJson($error->estado, ['ok' => false, 'error' => $detalle]);
    } catch (Throwable $error) {
        // No registrar cuerpos, rutas con tokens ni mensajes PDO que puedan incluir datos personales.
        error_log('SENDA: fallo interno de tipo ' . get_class($error));
        responderJson(503, ['ok' => false, 'error' => ['codigo' => 'SERVICIO_NO_DISPONIBLE',
            'mensaje' => 'El servicio no está disponible. Inténtalo más tarde.']]);
    }
}
