<?php
/*
 * Nombre: LogicaNegocio.php
 * Descripción: Aplica reglas de cuentas, asignación, mediciones y recuperación.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Aplica reglas de cuentas, asignación, mediciones y recuperación.
 */


declare(strict_types=1);
require_once __DIR__ . '/Apoyo.php';

final class LogicaNegocio
{


    /** --------------------
     * Diseño lógico: db: Deposito, config: [Dato] --> __construct() -->
     * Dato <--
     * Descripción: Comparte una conexión y configuración entre operaciones
     * -------------------- */
    public function __construct(private PDO $db, private array $config)
    {
    }



    /** --------------------
     * Diseño lógico: sql: Text, parametros: [Dato] --> consulta() -->
     * Filas <--
     * Descripción: Ejecuta consultas preparadas, sin concatenar datos del usuario
     * -------------------- */
    private function consulta(string $sql, array $parametros = []): PDOStatement
    {
        $consulta = $this->db->prepare($sql);
        $consulta->execute($parametros);
        return $consulta;
    }



    /** --------------------
     * Diseño lógico: usuario: [Dato] --> usuarioPublico() -->
     * [Dato] <--
     * Descripción: Selecciona solo los campos que se pueden enviar al cliente
     * -------------------- */
    private function usuarioPublico(array $usuario): array
    {
        return ['id_usuario' => (int)$usuario['id_usuario'], 'nombre' => $usuario['nombre'],
            'rol' => $usuario['rol'], 'id_parque' => $usuario['id_parque'] === null ? null : (int)$usuario['id_parque'],
            'debe_cambiar_contrasena' => (bool)($usuario['debe_cambiar_contrasena'] ?? false),
            'foto_perfil' => $usuario['foto_perfil'] ?? null,
            'llavero' => ($this->consulta('SELECT identificador_ble FROM LLAVEROS WHERE id_usuario=? AND estado=1',
                [$usuario['id_usuario']])->fetchColumn() ?: null)];
    }



    /** --------------------
     * Diseño lógico: operacion: Text, ip: Text, correo: Text --> limitar() -->
     * Descripción: Cuenta intentos en una ventana de 15 minutos de forma atómica
     * -------------------- */
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
                throw new ErrorDominio('DEMASIADOS_INTENTOS', 'Espera quince minutos antes de volver a intentarlo.');
            }
        }
    }



    /** --------------------
     * Diseño lógico: datos: [Dato] --> iniciarSesion() -->
     * [Dato] <--
     * Descripción: Verifica la cuenta activa y crea una sesión opaca de duración limitada
     * -------------------- */
    public function iniciarSesion(array $datos): array
    {
        exigirCampos($datos, ['correo', 'contrasena']);
        $correo = validarCorreo($datos['correo']);
        if (!is_string($datos['contrasena']) || strlen($datos['contrasena']) > 72 || str_contains($datos['contrasena'], "\0")) {
            throw new ErrorDominio('CREDENCIALES_INVALIDAS', 'No se ha podido iniciar sesión.');
        }
        $this->db->beginTransaction();
        try {
            $usuario = $this->consulta('SELECT u.*, r.codigo AS rol, o.estado AS estado_organizacion FROM USUARIOS u
                JOIN ROLES r ON r.id_rol = u.id_rol
                LEFT JOIN ORGANIZACIONES o ON o.id_organizacion = u.id_organizacion
                WHERE u.email = ? FOR UPDATE', [$correo])->fetch();
            // Hash ficticio válido para realizar también el trabajo de verificación si no existe cuenta.
            $hash = $usuario['password_hash'] ?? '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.';
            $valida = password_verify($datos['contrasena'], $hash);
            if (!$usuario || !$valida || !(bool)$usuario['activo'] || !$usuario['password_hash']
                || ($usuario['id_organizacion'] !== null && $usuario['estado_organizacion'] !== 'activo')) {
                throw new ErrorDominio('CREDENCIALES_INVALIDAS', 'No se ha podido iniciar sesión.');
            }
            $token = bin2hex(random_bytes(32));
            $horas = max(1, min(168, (int)($this->config['session_hours'] ?? 24)));
            $expira = (new DateTimeImmutable('now', new DateTimeZone('UTC')))->modify("+$horas hours");
            $this->consulta('INSERT INTO SESIONES (token_hash, id_usuario, expira_en) VALUES (?, ?, ?)',
                [hash('sha256', $token), $usuario['id_usuario'], $expira->format('Y-m-d H:i:s.v')]);
            $publico = $this->usuarioPublico($usuario);
            $this->db->commit();
            return ['token' => $token, 'expira_en' => $expira->format('Y-m-d\TH:i:s.v\Z'),
                'usuario' => $publico];
        } catch (Throwable $error) {
            $this->db->rollBack();
            throw $error;
        }
    }



    /** --------------------
     * Diseño lógico: token: Text --> autenticar() -->
     * [Dato] <--
     * Descripción: Comprueba caducidad, cuenta y organización en cada petición
     * -------------------- */
    public function autenticar(string $token): array
    {
        if (!preg_match('/^[a-f0-9]{64}$/D', $token)) {
            throw new ErrorDominio('SESION_INVALIDA', 'Inicia sesión de nuevo.');
        }
        $usuario = $this->consulta('SELECT u.*, r.codigo AS rol, s.expira_en, s.token_hash FROM SESIONES s
            JOIN USUARIOS u ON u.id_usuario = s.id_usuario
            JOIN ROLES r ON r.id_rol = u.id_rol
            LEFT JOIN ORGANIZACIONES o ON o.id_organizacion = u.id_organizacion
            WHERE s.token_hash = ? AND s.expira_en > UTC_TIMESTAMP(3) AND u.activo = 1
            AND (u.id_organizacion IS NULL OR o.estado = ?)', [hash('sha256', $token), 'activo'])->fetch();
        if (!$usuario) {
            throw new ErrorDominio('SESION_INVALIDA', 'Inicia sesión de nuevo.');
        }
        return $usuario;
    }



    /** --------------------
     * Diseño lógico: usuario: [Dato] --> consultarSesion() -->
     * [Dato] <--
     * Descripción: Devuelve el perfil actual sin volver a publicar el token
     * -------------------- */
    public function consultarSesion(array $usuario): array
    {
        return ['usuario' => $this->usuarioPublico($usuario), 'expira_en' => fechaJson($usuario['expira_en'])];
    }



    /** --------------------
     * Diseño lógico: usuario: [Dato] --> cerrarSesion() -->
     * [Dato] <--
     * Descripción: Elimina solo la sesión presentada por el cliente
     * -------------------- */
    public function cerrarSesion(array $usuario): array
    {
        $this->consulta('DELETE FROM SESIONES WHERE token_hash = ?', [$usuario['token_hash']]);
        return ['cerrado' => true];
    }



    /** --------------------
     * Diseño lógico: usuario: [Dato], rol: Text --> exigirRol() -->
     * Descripción: Restringe operaciones de jefe o trabajador antes de consultar recursos
     * -------------------- */
    private function exigirRol(array $usuario, string $rol): void
    {
        if ($usuario['rol'] !== $rol) {
            throw new ErrorDominio('SIN_PERMISO', 'No tienes permiso para esta operación.');
        }
    }

    /** Foto JPEG normalizada por Android, validada y guardada en la cuenta autenticada. */

    /** --------------------
     * Diseño lógico: usuario: [Dato], datos: [Dato] --> actualizarFotoPerfil() -->
     * [Dato] <--
     * Descripción: Actualizar Foto Perfil. Aplica reglas de cuentas, asignación, mediciones y recuperación
     * -------------------- */
    public function actualizarFotoPerfil(array $usuario, array $datos): array
    {
        exigirCampos($datos, ['foto_perfil']);
        $foto = $datos['foto_perfil'];
        if ($foto !== null) {
            if (!is_string($foto) || !str_starts_with($foto, 'data:image/jpeg;base64,'))
                throw new ErrorDominio('FOTO_INVALIDA', 'Selecciona una fotografía JPEG válida.');
            $binario = base64_decode(substr($foto, 23), true);
            $imagen = $binario === false ? false : @getimagesizefromstring($binario);
            if (!$imagen || strlen($binario) > 100000 || $imagen[2] !== IMAGETYPE_JPEG
                || $imagen[0] > 512 || $imagen[1] > 512)
                throw new ErrorDominio('FOTO_INVALIDA', 'La fotografía supera el tamaño permitido.');
        }
        $this->consulta('UPDATE USUARIOS SET foto_perfil=? WHERE id_usuario=?', [$foto, $usuario['id_usuario']]);
        return ['foto_perfil' => $foto];
    }

    /** Serializa la asignación por trabajador y llavero para evitar dobles vinculaciones. */

    /** --------------------
     * Diseño lógico: usuario: [Dato], datos: [Dato] --> vincularLlavero() -->
     * [Dato] <--
     * Descripción: Vincular Llavero. Aplica reglas de cuentas, asignación, mediciones y recuperación
     * -------------------- */
    public function vincularLlavero(array $usuario, array $datos): array
    {
        $this->exigirRol($usuario, 'trabajador');
        exigirCampos($datos, ['nombre_micro']);
        if (!is_string($datos['nombre_micro']) || !preg_match('/^S-[0-9]{3,6}$/D', $datos['nombre_micro']))
            throw new ErrorDominio('LLAVERO_INVALIDO', 'El código del llavero no es válido.');
        $this->db->beginTransaction();
        try {
            $this->consulta('SELECT id_usuario FROM USUARIOS WHERE id_usuario=? FOR UPDATE', [$usuario['id_usuario']]);
            $actual = $this->consulta('SELECT identificador_ble FROM LLAVEROS WHERE id_usuario=? FOR UPDATE',
                [$usuario['id_usuario']])->fetchColumn();
            if ($actual && $actual !== $datos['nombre_micro'])
                throw new ErrorDominio('YA_TIENE_LLAVERO', 'Desvincula tu llavero actual antes de vincular otro.');
            $llavero = $this->consulta('SELECT * FROM LLAVEROS WHERE identificador_ble=? FOR UPDATE',
                [$datos['nombre_micro']])->fetch();
            if (!$llavero || !(bool)$llavero['estado']
                || (int)$llavero['id_parque'] !== (int)$usuario['id_parque']
                || (int)$llavero['id_organizacion'] !== (int)$usuario['id_organizacion'])
                throw new ErrorDominio('LLAVERO_NO_DISPONIBLE', 'El llavero no existe o no está disponible en tu parque.');
            if ($llavero['id_usuario'] !== null && (int)$llavero['id_usuario'] !== (int)$usuario['id_usuario'])
                throw new ErrorDominio('LLAVERO_OCUPADO', 'Este llavero ya está vinculado a otra persona.');
            $this->consulta('UPDATE LLAVEROS SET id_usuario=? WHERE id_llavero=?',
                [$usuario['id_usuario'], $llavero['id_llavero']]);
            $this->db->commit();
            return ['llavero' => $llavero['identificador_ble']];
        } catch (Throwable $error) { $this->db->rollBack(); throw $error; }
    }


    /** --------------------
     * Diseño lógico: usuario: [Dato] --> desvincularLlavero() -->
     * [Dato] <--
     * Descripción: Desvincular Llavero. Aplica reglas de cuentas, asignación, mediciones y recuperación
     * -------------------- */
    public function desvincularLlavero(array $usuario): array
    {
        $this->exigirRol($usuario, 'trabajador');
        $this->db->beginTransaction();
        try {
            $this->consulta('SELECT id_usuario FROM USUARIOS WHERE id_usuario=? FOR UPDATE', [$usuario['id_usuario']]);
            $this->consulta('UPDATE LLAVEROS SET id_usuario=NULL WHERE id_usuario=?', [$usuario['id_usuario']]);
            $this->db->commit();
            return ['llavero' => null];
        } catch (Throwable $error) { $this->db->rollBack(); throw $error; }
    }



    /** --------------------
     * Diseño lógico: usuario: [Dato], datos: [Dato] --> guardarMedicion() -->
     * [Dato] <--
     * Descripción: Autoriza el llavero y guarda una sola vez la captura en su parque histórico
     * -------------------- */
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
                throw new ErrorDominio('LLAVERO_NO_AUTORIZADO', 'El llavero no está autorizado para tu cuenta.');
            }
            $existente = $this->consulta('SELECT * FROM MEDICIONES WHERE fecha_hora = ? AND id_llavero = ? AND tipo = ?',
                [$datos['fecha_sql'], $llavero['id_llavero'], $datos['tipo']])->fetch();
            if ($existente) {
                $igual = (int)$existente['id_usuario'] === (int)$usuario['id_usuario']
                    && (int)$existente['id_parque'] === (int)$usuario['id_parque']
                    && $existente['nombre_micro'] === $datos['nombre_micro']
                    && $existente['tipo'] === $datos['tipo'] && $existente['unidad'] === $datos['unidad']
                    && (float)$existente['valor'] === (float)$datos['valor']
                    && (($existente['latitud'] === null && $datos['latitud'] === null)
                        || ($existente['latitud'] !== null && $datos['latitud'] !== null
                            && (float)$existente['latitud'] === (float)$datos['latitud']))
                    && (($existente['longitud'] === null && $datos['longitud'] === null)
                        || ($existente['longitud'] !== null && $datos['longitud'] !== null
                            && (float)$existente['longitud'] === (float)$datos['longitud']))
                    && (bool)$existente['simulada'] === $datos['simulada'];
                if (!$igual) {
                    throw new ErrorDominio('MEDICION_EN_CONFLICTO', 'Existe otra lectura con la misma identidad.');
                }
            } else {
                $this->consulta('INSERT INTO MEDICIONES
                    (fecha_hora, id_llavero, tipo, valor, latitud, longitud, unidad, simulada, id_usuario, id_parque, nombre_micro)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
                    // JSON conserva la precisión del float; la conversión automática a string de PDO puede recortarla.
                    [$datos['fecha_sql'], $llavero['id_llavero'], $datos['tipo'], $datos['valor'],
                        $datos['latitud'] === null ? null : json_encode((float)$datos['latitud'], JSON_THROW_ON_ERROR),
                        $datos['longitud'] === null ? null : json_encode((float)$datos['longitud'], JSON_THROW_ON_ERROR), $datos['unidad'], (int)$datos['simulada'], $usuario['id_usuario'],
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

    /** Historial propio por clave estable: nuevas capturas no desplazan las páginas ya vistas. */

    /** --------------------
     * Diseño lógico: usuario: [Dato], cursor: Dato --> historialMediciones() -->
     * [Dato] <--
     * Descripción: Historial Mediciones. Aplica reglas de cuentas, asignación, mediciones y recuperación
     * -------------------- */
    public function historialMediciones(array $usuario, mixed $cursor = null): array
    {
        $this->exigirRol($usuario, 'trabajador');
        $parametros = [$usuario['id_usuario'], $usuario['id_parque']];
        $continuacion = '';
        if ($cursor !== null) {
            if (!is_string($cursor) || strlen($cursor) > 1024) {
                throw new ErrorDominio('CURSOR_INVALIDO', 'Actualiza el historial para comenzar de nuevo.');
            }
            $clave = json_decode(base64_decode($cursor, true) ?: '', true);
            if (!is_array($clave) || count($clave) !== 2 || !isset($clave[0], $clave[1])
                || !is_string($clave[0]) || !preg_match('/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{3}$/D', $clave[0])
                || !is_string($clave[1]) || strlen($clave[1]) < 1 || strlen($clave[1]) > 100) {
                throw new ErrorDominio('CURSOR_INVALIDO', 'Actualiza el historial para comenzar de nuevo.');
            }
            $continuacion = ' AND (fecha_hora < ? OR (fecha_hora = ? AND id_llavero < ?))';
            array_push($parametros, $clave[0], $clave[0], $clave[1]);
        }
        $filas = $this->consulta('SELECT * FROM MEDICIONES WHERE id_usuario=? AND id_parque=?'
            . $continuacion . ' ORDER BY fecha_hora DESC, id_llavero DESC LIMIT 21', $parametros)->fetchAll();
        $siguiente = null;
        if (count($filas) > 20) {
            array_pop($filas);
            $ultima = $filas[count($filas)-1];
            $siguiente = base64_encode(json_encode([$ultima['fecha_hora'], $ultima['id_llavero']], JSON_THROW_ON_ERROR));
        }
        $mediciones = array_map(static fn(array $fila): array => [
            'nombre_micro' => $fila['nombre_micro'], 'valor' => (int)$fila['valor'],
            'fecha_hora' => fechaJson($fila['fecha_hora']), 'unidad' => $fila['unidad'],
            'latitud' => (float)$fila['latitud'], 'longitud' => (float)$fila['longitud'],
            'simulada' => (bool)$fila['simulada'],
        ], $filas);
        return ['mediciones' => $mediciones, 'siguiente' => $siguiente];
    }



    /** --------------------
     * Diseño lógico: usuario: [Dato], parque: Z --> ultimaMedicion() -->
     * [Dato] <--
     * Descripción: Consulta por fecha de captura entre todos los llaveros del parque autorizado
     * -------------------- */
    public function ultimaMedicion(array $usuario, int $parque): array
    {
        $this->exigirRol($usuario, 'jefe_parque');
        if ($parque !== (int)$usuario['id_parque']) {
            throw new ErrorDominio('PARQUE_NO_DISPONIBLE', 'Parque no disponible.');
        }
        $fila = $this->consulta('SELECT * FROM MEDICIONES WHERE id_parque = ? AND tipo = \'O3\'
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



    /** --------------------
     * Diseño lógico: usuario: [Dato], finalidad: Text --> prepararEnlace() -->
     * Descripción: Guarda el hash y encola un correo dentro de la transacción del llamador
     * -------------------- */
    private function prepararEnlace(array $usuario, string $finalidad): void
    {
        $token = bin2hex(random_bytes(32));
        $this->consulta('INSERT INTO TOKENS_ACCESO (token_hash, id_usuario, finalidad, expira_en)
            VALUES (?, ?, ?, UTC_TIMESTAMP(3) + INTERVAL 30 MINUTE)',
            [hash('sha256', $token), $usuario['id_usuario'], $finalidad]);
        // El fragmento no viaja al servidor al abrir la web; JS lo consume y lo borra de la barra.
        $enlace = rtrim($this->config['app_url'], '/') . '/acceso#token=' . $token;
        $this->consulta('INSERT INTO CORREOS_PENDIENTES (destinatario, asunto, cuerpo) VALUES (?, ?, ?)',
            [$usuario['email'], 'SENDA: establece tu contraseña',
                "Abre este enlace para establecer tu contraseña. Caduca en 30 minutos y solo puede usarse una vez.\n\n$enlace\n\nSi no lo has solicitado, ignora este correo."]);
    }



    /** --------------------
     * Diseño lógico: usuario: [Dato], datos: [Dato] --> registrarTrabajador() -->
     * [Dato] <--
     * Descripción: Guarda el hash inicial y exige al trabajador cambiarlo antes de operar
     * -------------------- */
    public function registrarTrabajador(array $usuario, array $datos): array
    {
        $this->exigirRol($usuario, 'jefe_parque');
        exigirCampos($datos, ['nombre', 'correo', 'contrasena_inicial']);
        $correo = validarCorreo($datos['correo']);
        $contrasena = validarContrasena($datos['contrasena_inicial']);
        if (!is_string($datos['nombre']) || trim($datos['nombre']) === '' || mb_strlen(trim($datos['nombre']), 'UTF-8') > 100) {
            throw new ErrorDominio('DATOS_INVALIDOS', 'Introduce un nombre de hasta 100 caracteres.');
        }
        $this->db->beginTransaction();
        try {
            $this->consulta('INSERT INTO USUARIOS (nombre, email, password_hash, debe_cambiar_contrasena, id_rol, id_parque, id_organizacion)
                VALUES (?, ?, ?, 1, (SELECT id_rol FROM ROLES WHERE codigo = ?), ?, ?)', [trim($datos['nombre']), $correo, password_hash($contrasena, PASSWORD_BCRYPT),
                    'trabajador', $usuario['id_parque'], $usuario['id_organizacion']]);
            $nuevo = ['id_usuario' => (int)$this->db->lastInsertId(), 'nombre' => trim($datos['nombre']),
                'email' => $correo, 'rol' => 'trabajador', 'id_parque' => $usuario['id_parque'], 'debe_cambiar_contrasena' => true];
            $this->db->commit();
            return ['usuario' => $this->usuarioPublico($nuevo), 'requiere_cambiar_contrasena' => true,
                'mensaje' => 'Cuenta creada con contraseña inicial. Comunícala al trabajador: deberá cambiarla en su primer acceso.'];
        } catch (Throwable $error) {
            $this->db->rollBack();
            if ($error instanceof PDOException && ($error->errorInfo[1] ?? 0) === 1062) {
                throw new ErrorDominio('ALTA_NO_DISPONIBLE', 'Ya existe una cuenta con ese correo electrónico. Utiliza otro.',
                    ['correo' => 'Este correo ya está registrado.']);
            }
            throw $error;
        }
    }



    /** --------------------
     * Diseño lógico: usuario: [Dato], datos: [Dato] --> cambiarContrasena() -->
     * [Dato] <--
     * Descripción: Cambia la contraseña propia, elimina la obligación inicial y revoca sesiones y enlaces
     * -------------------- */
    public function cambiarContrasena(array $usuario, array $datos): array
    {
        exigirCampos($datos, ['contrasena_actual', 'contrasena_nueva']);
        $nueva = validarContrasena($datos['contrasena_nueva']);
        $actual = $datos['contrasena_actual'];
        if (!is_string($actual) || strlen($actual) > 72 || str_contains($actual, "\0")) {
            throw new ErrorDominio('DATOS_INVALIDOS', 'Introduce la contraseña actual.');
        }
        $this->db->beginTransaction();
        try {
            $cuenta = $this->consulta('SELECT u.* FROM USUARIOS u JOIN SESIONES s ON s.id_usuario = u.id_usuario
                WHERE u.id_usuario = ? AND s.token_hash = ? AND s.expira_en > UTC_TIMESTAMP(3)
                AND u.activo = 1 FOR UPDATE', [$usuario['id_usuario'], $usuario['token_hash']])->fetch();
            if (!$cuenta) throw new ErrorDominio('SESION_INVALIDA', 'Inicia sesión de nuevo.');
            if (!$cuenta['password_hash'] || !password_verify($actual, $cuenta['password_hash'])) {
                throw new ErrorDominio('CONTRASENA_ACTUAL_INVALIDA', 'La contraseña actual no es correcta.');
            }
            if (password_verify($nueva, $cuenta['password_hash'])) {
                throw new ErrorDominio('DATOS_INVALIDOS', 'La contraseña nueva debe ser diferente de la inicial o actual.');
            }
            $this->consulta('UPDATE USUARIOS SET password_hash = ?, debe_cambiar_contrasena = 0 WHERE id_usuario = ?',
                [password_hash($nueva, PASSWORD_BCRYPT), $cuenta['id_usuario']]);
            $this->consulta('UPDATE TOKENS_ACCESO SET usado_en = UTC_TIMESTAMP(3) WHERE id_usuario = ? AND usado_en IS NULL', [$cuenta['id_usuario']]);
            $this->consulta('DELETE FROM SESIONES WHERE id_usuario = ?', [$cuenta['id_usuario']]);
            $this->db->commit();
            return ['actualizada' => true, 'requiere_iniciar_sesion' => true];
        } catch (Throwable $error) {
            $this->db->rollBack();
            throw $error;
        }
    }



    /** --------------------
     * Diseño lógico: datos: [Dato] --> solicitarRecuperacion() -->
     * [Dato] <--
     * Descripción: Encola el correo solo para una cuenta activa sin revelar si existe
     * -------------------- */
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



    /** --------------------
     * Diseño lógico: datos: [Dato] --> registrarSolicitud() -->
     * [Dato] <--
     * Descripción: Guarda una solicitud pública sin crear una cuenta ni enviar correos en su nombre
     * -------------------- */
    public function registrarSolicitud(array $datos): array
    {
        exigirCampos($datos, ['nombre', 'correo', 'organizacion', 'mensaje']);
        $correo = validarCorreo($datos['correo']);
        foreach (['nombre' => 100, 'organizacion' => 100, 'mensaje' => 4000] as $campo => $maximo) {
            if (!is_string($datos[$campo]) || trim($datos[$campo]) === ''
                || mb_strlen(trim($datos[$campo]), 'UTF-8') > $maximo || str_contains($datos[$campo], "\0")) {
                throw new ErrorDominio('DATOS_INVALIDOS', 'Revisa los datos de contacto.', [$campo => 'Campo obligatorio o demasiado largo.']);
            }
        }
        if (mb_strlen(trim($datos['mensaje']), 'UTF-8') < 10) {
            throw new ErrorDominio('DATOS_INVALIDOS', 'Describe tu consulta con al menos 10 caracteres.');
        }
        $this->consulta('INSERT INTO SOLICITUDES_CONTACTO (nombre, correo, organizacion, mensaje) VALUES (?, ?, ?, ?)',
            [trim($datos['nombre']), $correo, trim($datos['organizacion']), trim($datos['mensaje'])]);
        return ['mensaje' => 'Hemos recibido tu solicitud de información sobre SENDA. Gracias por contarnos tu proyecto.'];
    }



    /** --------------------
     * Diseño lógico: datos: [Dato] --> restablecerContrasena() -->
     * [Dato] <--
     * Descripción: Actualiza contraseña, consume enlaces y revoca sesiones de forma atómica
     * -------------------- */
    public function restablecerContrasena(array $datos): array
    {
        exigirCampos($datos, ['token', 'contrasena_nueva']);
        $contrasena = validarContrasena($datos['contrasena_nueva']);
        if (!is_string($datos['token']) || !preg_match('/^[a-f0-9]{64}$/D', $datos['token'])) {
            throw new ErrorDominio('ENLACE_INVALIDO', 'El enlace no es válido o ha caducado.');
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
                throw new ErrorDominio('ENLACE_INVALIDO', 'El enlace no es válido o ha caducado.');
            }
            $this->consulta('UPDATE USUARIOS SET password_hash = ?, debe_cambiar_contrasena = 0 WHERE id_usuario = ?',
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
