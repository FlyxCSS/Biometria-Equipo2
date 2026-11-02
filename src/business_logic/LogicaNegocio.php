<?php
/*
 * Nombre: LogicaNegocio.php
 * Descripción: Autenticación, trabajadores, mediciones y recuperación de SENDA.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Persistencia real MySQL y control de permisos para el Sprint 2.
 */
declare(strict_types=1);
require_once __DIR__ . '/Apoyo.php';

final class LogicaNegocio
{
    /** Diseño lógico: (PDO, configuración) -> __construct -> LogicaNegocio.
     * Descripción: Comparte una conexión y configuración entre operaciones. */
    public function __construct(private PDO $db, private array $config)
    {
    }

    /** Diseño lógico: (SQL, parámetros) -> consulta -> PDOStatement.
     * Descripción: Ejecuta consultas preparadas, sin concatenar datos del usuario. */
    private function consulta(string $sql, array $parametros = []): PDOStatement
    {
        $consulta = $this->db->prepare($sql);
        $consulta->execute($parametros);
        return $consulta;
    }

    /** Diseño lógico: (fila usuario) -> usuarioPublico -> array.
     * Descripción: Selecciona solo los campos que se pueden enviar al cliente. */
    private function usuarioPublico(array $usuario): array
    {
        return ['id_usuario' => (int)$usuario['id_usuario'], 'nombre' => $usuario['nombre'],
            'rol' => $usuario['rol'], 'id_parque' => $usuario['id_parque'] === null ? null : (int)$usuario['id_parque']];
    }

    /** Diseño lógico: (operación, IP, correo) -> limitar -> vacío o error 429.
     * Descripción: Cuenta intentos en una ventana de 15 minutos de forma atómica. */
    public function limitar(string $operacion, string $ip, string $correo = ''): void
    {
        // Dos límites: por IP y por cuenta. No confiar en X-Forwarded-For.
        foreach (['ip:' . $ip => 100, 'cuenta:' . $correo => 10] as $identidad => $maximo) {
            if ($identidad === 'cuenta:') {
                continue;
            }
            $clave = hash('sha256', $operacion . ':' . $identidad);
            $this->consulta('INSERT INTO LIMITES (clave, ventana, intentos) VALUES (?, UTC_TIMESTAMP(), 1)
                ON DUPLICATE KEY UPDATE
                intentos = IF(ventana <= UTC_TIMESTAMP() - INTERVAL 15 MINUTE, 1, intentos + 1),
                ventana = IF(ventana <= UTC_TIMESTAMP() - INTERVAL 15 MINUTE, UTC_TIMESTAMP(), ventana)', [$clave]);
            $fila = $this->consulta('SELECT intentos FROM LIMITES WHERE clave = ?', [$clave])->fetch();
            if ((int)$fila['intentos'] > $maximo) {
                throw new ErrorApi(429, 'DEMASIADOS_INTENTOS', 'Espera quince minutos antes de volver a intentarlo.');
            }
        }
    }

    /** Diseño lógico: (correo, contraseña) -> iniciarSesion -> token, usuario, caducidad.
     * Descripción: Verifica la cuenta activa y crea una sesión opaca de duración limitada. */
    public function iniciarSesion(array $datos): array
    {
        exigirCampos($datos, ['correo', 'contrasena']);
        $correo = validarCorreo($datos['correo']);
        if (!is_string($datos['contrasena']) || strlen($datos['contrasena']) > 72 || str_contains($datos['contrasena'], "\0")) {
            throw new ErrorApi(401, 'CREDENCIALES_INVALIDAS', 'No se ha podido iniciar sesión.');
        }
        $this->db->beginTransaction();
        try {
            $usuario = $this->consulta('SELECT u.*, o.estado AS estado_organizacion FROM USUARIOS u
                LEFT JOIN ORGANIZACIONES o ON o.id_organizacion = u.id_organizacion
                WHERE u.email = ? FOR UPDATE', [$correo])->fetch();
            // Hash ficticio válido para realizar también el trabajo de verificación si no existe cuenta.
            $hash = $usuario['password_hash'] ?? '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.';
            $valida = password_verify($datos['contrasena'], $hash);
            if (!$usuario || !$valida || !(bool)$usuario['activo'] || !$usuario['password_hash']
                || ($usuario['id_organizacion'] !== null && $usuario['estado_organizacion'] !== 'activo')) {
                throw new ErrorApi(401, 'CREDENCIALES_INVALIDAS', 'No se ha podido iniciar sesión.');
            }
            $token = bin2hex(random_bytes(32));
            $horas = max(1, min(168, (int)($this->config['session_hours'] ?? 24)));
            $expira = (new DateTimeImmutable('now', new DateTimeZone('UTC')))->modify("+$horas hours");
            $this->consulta('INSERT INTO SESIONES (token_hash, id_usuario, expira_en) VALUES (?, ?, ?)',
                [hash('sha256', $token), $usuario['id_usuario'], $expira->format('Y-m-d H:i:s.v')]);
            $this->db->commit();
            return ['token' => $token, 'expira_en' => $expira->format('Y-m-d\TH:i:s.v\Z'),
                'usuario' => $this->usuarioPublico($usuario)];
        } catch (Throwable $error) {
            $this->db->rollBack();
            throw $error;
        }
    }

    /** Diseño lógico: (token) -> autenticar -> usuario y sesión verificados.
     * Descripción: Comprueba caducidad, cuenta y organización en cada petición. */
    public function autenticar(string $token): array
    {
        if (!preg_match('/^[a-f0-9]{64}$/D', $token)) {
            throw new ErrorApi(401, 'SESION_INVALIDA', 'Inicia sesión de nuevo.');
        }
        $usuario = $this->consulta('SELECT u.*, s.expira_en, s.token_hash FROM SESIONES s
            JOIN USUARIOS u ON u.id_usuario = s.id_usuario
            LEFT JOIN ORGANIZACIONES o ON o.id_organizacion = u.id_organizacion
            WHERE s.token_hash = ? AND s.expira_en > UTC_TIMESTAMP(3) AND u.activo = 1
            AND (u.id_organizacion IS NULL OR o.estado = ?)', [hash('sha256', $token), 'activo'])->fetch();
        if (!$usuario) {
            throw new ErrorApi(401, 'SESION_INVALIDA', 'Inicia sesión de nuevo.');
        }
        return $usuario;
    }

    /** Diseño lógico: (usuario autenticado) -> consultarSesion -> usuario y caducidad.
     * Descripción: Devuelve el perfil actual sin volver a publicar el token. */
    public function consultarSesion(array $usuario): array
    {
        return ['usuario' => $this->usuarioPublico($usuario), 'expira_en' => fechaJson($usuario['expira_en'])];
    }

    /** Diseño lógico: (usuario autenticado) -> cerrarSesion -> confirmación.
     * Descripción: Elimina solo la sesión presentada por el cliente. */
    public function cerrarSesion(array $usuario): array
    {
        $this->consulta('DELETE FROM SESIONES WHERE token_hash = ?', [$usuario['token_hash']]);
        return ['cerrado' => true];
    }

    /** Diseño lógico: (usuario, rol) -> exigirRol -> vacío o error 403.
     * Descripción: Restringe operaciones de jefe o trabajador antes de consultar recursos. */
    private function exigirRol(array $usuario, string $rol): void
    {
        if ($usuario['rol'] !== $rol) {
            throw new ErrorApi(403, 'SIN_PERMISO', 'No tienes permiso para esta operación.');
        }
    }

    /** Diseño lógico: (usuario, lectura) -> guardarMedicion -> identidad y duplicada.
     * Descripción: Autoriza el llavero y guarda una sola vez la captura en su parque histórico. */
    public function guardarMedicion(array $usuario, array $datos): array
    {
        $this->exigirRol($usuario, 'trabajador');
        $datos = validarMedicion($datos);
        $this->db->beginTransaction();
        try {
            // Bloquear el llavero serializa los reintentos y las futuras reasignaciones.
            $llavero = $this->consulta('SELECT * FROM LLAVEROS WHERE identificador_ble = ? FOR UPDATE',
                [$datos['nombre_micro']])->fetch();
            if (!$llavero || !(bool)$llavero['estado'] || (int)$llavero['id_usuario'] !== (int)$usuario['id_usuario']
                || (int)$llavero['id_parque'] !== (int)$usuario['id_parque']
                || (int)$llavero['id_organizacion'] !== (int)$usuario['id_organizacion']) {
                throw new ErrorApi(403, 'LLAVERO_NO_AUTORIZADO', 'El llavero no está autorizado para tu cuenta.');
            }
            $existente = $this->consulta('SELECT * FROM MEDICIONES WHERE fecha_hora = ? AND id_llavero = ?',
                [$datos['fecha_sql'], $llavero['id_llavero']])->fetch();
            if ($existente) {
                $igual = (int)$existente['id_usuario'] === (int)$usuario['id_usuario']
                    && (int)$existente['id_parque'] === (int)$usuario['id_parque']
                    && $existente['nombre_micro'] === $datos['nombre_micro']
                    && $existente['tipo'] === $datos['tipo'] && $existente['unidad'] === $datos['unidad']
                    && (int)$existente['valor'] === $datos['valor']
                    && (float)$existente['latitud'] === (float)$datos['latitud']
                    && (float)$existente['longitud'] === (float)$datos['longitud']
                    && (bool)$existente['simulada'] === $datos['simulada'];
                if (!$igual) {
                    throw new ErrorApi(409, 'MEDICION_EN_CONFLICTO', 'Existe otra lectura con la misma identidad.');
                }
            } else {
                $this->consulta('INSERT INTO MEDICIONES
                    (fecha_hora, id_llavero, tipo, valor, latitud, longitud, unidad, simulada, id_usuario, id_parque, nombre_micro)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                    // JSON conserva la precisión del float; la conversión automática a string de PDO puede recortarla.
                    [$datos['fecha_sql'], $llavero['id_llavero'], $datos['tipo'], $datos['valor'],
                        json_encode((float)$datos['latitud'], JSON_THROW_ON_ERROR),
                        json_encode((float)$datos['longitud'], JSON_THROW_ON_ERROR), $datos['unidad'], (int)$datos['simulada'], $usuario['id_usuario'],
                        $usuario['id_parque'], $datos['nombre_micro']]);
            }
            $this->db->commit();
            return ['nombre_micro' => $datos['nombre_micro'], 'fecha_hora' => $datos['fecha_hora'],
                'guardada' => true, 'duplicada' => (bool)$existente];
        } catch (Throwable $error) {
            $this->db->rollBack();
            throw $error;
        }
    }

    /** Diseño lógico: (jefe, parque) -> ultimaMedicion -> medición o null.
     * Descripción: Consulta por fecha de captura entre todos los llaveros del parque autorizado. */
    public function ultimaMedicion(array $usuario, int $parque): array
    {
        $this->exigirRol($usuario, 'jefe_parque');
        if ($parque !== (int)$usuario['id_parque']) {
            throw new ErrorApi(404, 'PARQUE_NO_DISPONIBLE', 'Parque no disponible.');
        }
        $fila = $this->consulta('SELECT * FROM MEDICIONES WHERE id_parque = ?
            ORDER BY fecha_hora DESC, nombre_micro ASC, id_llavero ASC LIMIT 1', [$parque])->fetch();
        $medicion = null;
        if ($fila) {
            $medicion = ['nombre_micro' => $fila['nombre_micro'], 'tipo' => $fila['tipo'], 'valor' => (int)$fila['valor'],
                'unidad' => $fila['unidad'], 'fecha_hora' => fechaJson($fila['fecha_hora']),
                'recibida_en' => fechaJson($fila['recibida_en']), 'latitud' => (float)$fila['latitud'],
                'longitud' => (float)$fila['longitud'], 'simulada' => (bool)$fila['simulada']];
        }
        return ['id_parque' => $parque, 'medicion' => $medicion];
    }

    /** Diseño lógico: (usuario, finalidad) -> prepararEnlace -> vacío.
     * Descripción: Guarda el hash y encola un correo dentro de la transacción del llamador. */
    private function prepararEnlace(array $usuario, string $finalidad): void
    {
        $token = bin2hex(random_bytes(32));
        $this->consulta('INSERT INTO TOKENS_ACCESO (token_hash, id_usuario, finalidad, expira_en)
            VALUES (?, ?, ?, UTC_TIMESTAMP(3) + INTERVAL 30 MINUTE)',
            [hash('sha256', $token), $usuario['id_usuario'], $finalidad]);
        // El fragmento no viaja al servidor al abrir la web; JS lo consume y lo borra de la barra.
        $enlace = rtrim($this->config['app_url'], '/') . '/#token=' . $token;
        $this->consulta('INSERT INTO CORREOS_PENDIENTES (destinatario, asunto, cuerpo) VALUES (?, ?, ?)',
            [$usuario['email'], 'SENDA: establece tu contraseña',
                "Abre este enlace para establecer tu contraseña. Caduca en 30 minutos y solo puede usarse una vez.\n\n$enlace\n\nSi no lo has solicitado, ignora este correo."]);
    }

    /** Diseño lógico: (jefe, nombre y correo) -> registrarTrabajador -> usuario creado.
     * Descripción: Crea una cuenta de su parque y encola el enlace de primera contraseña. */
    public function registrarTrabajador(array $usuario, array $datos): array
    {
        $this->exigirRol($usuario, 'jefe_parque');
        exigirCampos($datos, ['nombre', 'correo']);
        $correo = validarCorreo($datos['correo']);
        if (!is_string($datos['nombre']) || trim($datos['nombre']) === '' || mb_strlen(trim($datos['nombre']), 'UTF-8') > 100) {
            throw new ErrorApi(422, 'DATOS_INVALIDOS', 'Introduce un nombre de hasta 100 caracteres.');
        }
        $this->db->beginTransaction();
        try {
            $this->consulta('INSERT INTO USUARIOS (nombre, email, rol, id_parque, id_organizacion)
                VALUES (?, ?, ?, ?, ?)', [trim($datos['nombre']), $correo, 'trabajador', $usuario['id_parque'], $usuario['id_organizacion']]);
            $nuevo = ['id_usuario' => (int)$this->db->lastInsertId(), 'nombre' => trim($datos['nombre']),
                'email' => $correo, 'rol' => 'trabajador', 'id_parque' => $usuario['id_parque']];
            $this->prepararEnlace($nuevo, 'alta');
            $this->db->commit();
            return ['usuario' => $this->usuarioPublico($nuevo), 'requiere_establecer_contrasena' => true];
        } catch (Throwable $error) {
            $this->db->rollBack();
            if ($error instanceof PDOException && ($error->errorInfo[1] ?? 0) === 1062) {
                throw new ErrorApi(409, 'ALTA_NO_DISPONIBLE', 'No se puede completar el alta con esos datos.');
            }
            throw $error;
        }
    }

    /** Diseño lógico: (correo) -> solicitarRecuperacion -> mensaje genérico.
     * Descripción: Encola el correo solo para una cuenta activa sin revelar si existe. */
    public function solicitarRecuperacion(array $datos): array
    {
        exigirCampos($datos, ['correo']);
        $correo = validarCorreo($datos['correo']);
        $this->db->beginTransaction();
        try {
            $usuario = $this->consulta('SELECT u.* FROM USUARIOS u
                LEFT JOIN ORGANIZACIONES o ON o.id_organizacion = u.id_organizacion
                WHERE u.email = ? AND u.activo = 1
                AND (u.id_organizacion IS NULL OR o.estado = ?) FOR UPDATE', [$correo, 'activo'])->fetch();
            if ($usuario) {
                $this->prepararEnlace($usuario, 'recuperacion');
            }
            $this->db->commit();
        } catch (Throwable $error) {
            $this->db->rollBack();
            throw $error;
        }
        return ['mensaje' => 'Si la cuenta permite recuperar el acceso, recibirás un enlace por correo.'];
    }

    /** Diseño lógico: (token, contraseña nueva) -> restablecerContrasena -> confirmación.
     * Descripción: Actualiza contraseña, consume enlaces y revoca sesiones de forma atómica. */
    public function restablecerContrasena(array $datos): array
    {
        exigirCampos($datos, ['token', 'contrasena_nueva']);
        $contrasena = validarContrasena($datos['contrasena_nueva']);
        if (!is_string($datos['token']) || !preg_match('/^[a-f0-9]{64}$/D', $datos['token'])) {
            throw new ErrorApi(422, 'ENLACE_INVALIDO', 'El enlace no es válido o ha caducado.');
        }
        $hash = hash('sha256', $datos['token']);
        // Averiguar el usuario sin bloquear; después todas las operaciones bloquean usuario antes que token.
        $referencia = $this->consulta('SELECT id_usuario FROM TOKENS_ACCESO WHERE token_hash = ?', [$hash])->fetch();
        $this->db->beginTransaction();
        try {
            $usuario = $this->consulta('SELECT u.* FROM USUARIOS u
                LEFT JOIN ORGANIZACIONES o ON o.id_organizacion = u.id_organizacion
                WHERE u.id_usuario = ? AND u.activo = 1
                AND (u.id_organizacion IS NULL OR o.estado = ?) FOR UPDATE', [$referencia['id_usuario'] ?? 0, 'activo'])->fetch();
            $token = $this->consulta('SELECT * FROM TOKENS_ACCESO WHERE token_hash = ?
                AND usado_en IS NULL AND expira_en > UTC_TIMESTAMP(3) FOR UPDATE', [$hash])->fetch();
            if (!$usuario || !$token) {
                throw new ErrorApi(422, 'ENLACE_INVALIDO', 'El enlace no es válido o ha caducado.');
            }
            $this->consulta('UPDATE USUARIOS SET password_hash = ? WHERE id_usuario = ?',
                [password_hash($contrasena, PASSWORD_BCRYPT), $usuario['id_usuario']]);
            $this->consulta('UPDATE TOKENS_ACCESO SET usado_en = UTC_TIMESTAMP(3) WHERE id_usuario = ? AND usado_en IS NULL',
                [$usuario['id_usuario']]);
            $this->consulta('DELETE FROM SESIONES WHERE id_usuario = ?', [$usuario['id_usuario']]);
            $this->db->commit();
            return ['actualizada' => true];
        } catch (Throwable $error) {
            $this->db->rollBack();
            throw $error;
        }
    }
}
