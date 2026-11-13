<?php
/*
 * Nombre: Correo.php
 * Descripción: Entrega de la cola de correo, por buzón privado local o PHP mail.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Correo recuperable y separado del tiempo de respuesta de la API.
 */
declare(strict_types=1);

/** Diseño lógico: (PDO, configuración) -> entregarCorreos -> cantidad enviada.
 * Descripción: Envía pendientes sin exponer tokens por la API; conserva fallos para reintentar. */
function entregarCorreos(PDO $db, array $config): int
{
    $cantidad = 0;
    $ids = $db->query('SELECT id_correo FROM CORREOS_PENDIENTES WHERE enviado_en IS NULL ORDER BY id_correo LIMIT 100')->fetchAll(PDO::FETCH_COLUMN);
    foreach ($ids as $id) {
        $db->beginTransaction();
        try {
            $q = $db->prepare('SELECT * FROM CORREOS_PENDIENTES WHERE id_correo = ? AND enviado_en IS NULL FOR UPDATE');
            $q->execute([$id]);
            $correo = $q->fetch();
            if (!$correo) {
                $db->commit();
                continue;
            }
            $enviado = false;
            if (($config['mail_transport'] ?? '') === 'file' && ($config['environment'] ?? '') === 'local') {
                $directorio = $config['mail_directory'];
                if (!is_dir($directorio) && !mkdir($directorio, 0700, true) && !is_dir($directorio)) {
                    throw new RuntimeException('No se puede abrir el buzón local');
                }
                $texto = "To: {$correo['destinatario']}\nSubject: {$correo['asunto']}\n\n{$correo['cuerpo']}";
                $enviado = file_put_contents($directorio . '/' . $id . '.eml', $texto, LOCK_EX) !== false;
            } elseif (($config['mail_transport'] ?? '') === 'mail') {
                if (!filter_var($config['mail_from'] ?? '', FILTER_VALIDATE_EMAIL)) {
                    throw new RuntimeException('Remitente no válido');
                }
                $enviado = mail($correo['destinatario'], $correo['asunto'], $correo['cuerpo'], [
                    'From' => $config['mail_from'], 'Content-Type' => 'text/plain; charset=UTF-8',
                ]);
            }
            // Borrar cuerpo y dirección tras entrega: el enlace solo queda en el correo recibido.
            $q = $db->prepare('UPDATE CORREOS_PENDIENTES SET intentos = intentos + 1,
                enviado_en = IF(?, UTC_TIMESTAMP(3), NULL), cuerpo = IF(?, ?, cuerpo),
                destinatario = IF(?, ?, destinatario) WHERE id_correo = ?');
            $q->execute([(int)$enviado, (int)$enviado, '', (int)$enviado, '', $id]);
            $db->commit();
            $cantidad += (int)$enviado;
        } catch (Throwable $error) {
            $db->rollBack();
            throw $error;
        }
    }
    return $cantidad;
}
