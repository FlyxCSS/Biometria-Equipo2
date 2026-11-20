<?php
/*
 * Nombre: Correo.php
 * Descripción: Entrega el buzón transaccional y registra el resultado.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Entrega el buzón transaccional y registra el resultado.
 */


declare(strict_types=1);



/** --------------------
 * Diseño lógico: db: Deposito, config: [Dato] --> entregarCorreos() -->
 * Z <--
 * Descripción: Envía pendientes sin exponer tokens por la API; conserva fallos para reintentar
 * -------------------- */
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
            $mensaje = componerCorreo($correo, $config);
            if (($config['mail_transport'] ?? '') === 'file' && ($config['environment'] ?? '') === 'local') {
                $directorio = $config['mail_directory'];
                if (!is_dir($directorio) && !mkdir($directorio, 0700, true) && !is_dir($directorio)) {
                    throw new RuntimeException('No se puede abrir el buzón local');
                }
                $cabeceras = '';
                foreach ($mensaje['cabeceras'] as $nombre => $valor) $cabeceras .= "$nombre: $valor\r\n";
                $texto = "To: {$correo['destinatario']}\r\nSubject: {$mensaje['asunto']}\r\n$cabeceras\r\n{$mensaje['cuerpo']}";
                $enviado = file_put_contents($directorio . '/' . $id . '.eml', $texto, LOCK_EX) !== false;
            } elseif (($config['mail_transport'] ?? '') === 'mail') {
                if (!filter_var($config['mail_from'] ?? '', FILTER_VALIDATE_EMAIL)) {
                    throw new RuntimeException('Remitente no válido');
                }
                $enviado = mail($correo['destinatario'], $mensaje['asunto'], $mensaje['cuerpo'],
                    ['From' => 'SENDA · OZORA <' . $config['mail_from'] . '>'] + $mensaje['cabeceras']);
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



/** --------------------
 * Diseño lógico: correo: [Dato], config: [Dato] --> componerCorreo() -->
 * [Dato] <--
 * Descripción: Convierte el texto guardado en un correo multiparte (texto + HTML con la imagen
 * de OZORA/SENDA). El texto plano se conserva para clientes que no muestran HTML.
 * -------------------- */
function componerCorreo(array $correo, array $config): array
{
    $texto = (string)$correo['cuerpo'];
    $html = plantillaCorreoHtml((string)$correo['asunto'], $texto, (string)($config['app_url'] ?? ''));
    $limite = 'senda-' . bin2hex(random_bytes(12));
    $cuerpo = "--$limite\r\nContent-Type: text/plain; charset=UTF-8\r\nContent-Transfer-Encoding: base64\r\n\r\n"
        . chunk_split(base64_encode($texto))
        . "--$limite\r\nContent-Type: text/html; charset=UTF-8\r\nContent-Transfer-Encoding: base64\r\n\r\n"
        . chunk_split(base64_encode($html))
        . "--$limite--\r\n";
    return [
        'asunto' => '=?UTF-8?B?' . base64_encode((string)$correo['asunto']) . '?=',
        'cabeceras' => ['MIME-Version' => '1.0', 'Content-Type' => "multipart/alternative; boundary=\"$limite\""],
        'cuerpo' => $cuerpo,
    ];
}



/** --------------------
 * Diseño lógico: asunto: Texto, texto: Texto, urlApp: Texto --> plantillaCorreoHtml() -->
 * Texto <--
 * Descripción: Plantilla HTML con tablas y estilos en línea (compatible con Gmail, Outlook y móvil).
 * El enlace del correo se convierte en botón; el resto de párrafos se escapan como texto.
 * -------------------- */
function plantillaCorreoHtml(string $asunto, string $texto, string $urlApp): string
{
    $e = static fn(string $valor): string => htmlspecialchars($valor, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
    $enlace = preg_match('~https?://\S+~', $texto, $coincidencia) ? $coincidencia[0] : '';
    $parrafos = '';
    foreach (preg_split('/\R{2,}/', trim($texto)) as $bloque) {
        $bloque = trim($bloque);
        if ($bloque === '') continue;
        if ($bloque === $enlace) { $parrafos .= "{{BOTON}}"; continue; }
        $parrafos .= '<p style="margin:0 0 16px;font-size:15px;line-height:1.6;color:#4f6b69;">' . nl2br($e($bloque)) . '</p>';
    }
    $base = rtrim($urlApp, '/');
    $logo = $base !== '' ? '<img src="' . $e($base . '/assets/ozora-correo.png') . '" width="140" height="82" alt="OZORA" style="display:block;border:0;width:140px;height:auto;">'
        : '<span style="font:700 26px Arial,sans-serif;letter-spacing:3px;color:#1f3f8f;">OZORA</span>';
    $cabecera = $base !== '' ? '<tr><td><img src="' . $e($base . '/assets/ozora-correo-cabecera.jpg') . '" width="600" alt="" style="display:block;border:0;width:100%;height:auto;border-radius:18px 18px 0 0;"></td></tr>' : '';
    $boton = $enlace === '' ? '' : '
          <table role="presentation" cellpadding="0" cellspacing="0" border="0" style="margin:8px 0 24px;"><tr>
            <td style="border-radius:12px;background:#123f42;">
              <a href="' . $e($enlace) . '" style="display:inline-block;padding:15px 34px;font:700 16px Arial,sans-serif;color:#ffffff;text-decoration:none;border-radius:12px;">Establecer contraseña</a>
            </td></tr></table>
          <p style="margin:0 0 6px;font-size:13px;color:#7b9391;">Si el botón no funciona, copia este enlace en tu navegador:</p>
          <p style="margin:0 0 20px;font-size:13px;line-height:1.5;word-break:break-all;"><a href="' . $e($enlace) . '" style="color:#123f42;">' . $e($enlace) . '</a></p>';
    return '<!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<meta name="color-scheme" content="light"><title>' . $e($asunto) . '</title></head>
<body style="margin:0;padding:0;background:#f6f8f2;">
<div style="display:none;max-height:0;overflow:hidden;">Tu enlace para entrar en SENDA. Caduca en 30 minutos.</div>
<table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="background:#f6f8f2;"><tr><td align="center" style="padding:28px 12px;">
  <table role="presentation" width="600" cellpadding="0" cellspacing="0" border="0" style="width:100%;max-width:600px;background:#ffffff;border-radius:18px;font-family:Arial,Helvetica,sans-serif;color:#123f42;">
    ' . $cabecera . '
    <tr><td style="padding:30px 40px 6px;">' . $logo . '</td></tr>
    <tr><td style="padding:12px 40px 8px;">
      <p style="margin:0 0 8px;font-size:12px;letter-spacing:1.5px;text-transform:uppercase;color:#5f7e7d;">Acceso a SENDA</p>
      <h1 style="margin:0 0 18px;font-size:28px;line-height:1.2;font-weight:700;color:#123f42;">Establece tu contraseña</h1>
      ' . (str_contains($parrafos, '{{BOTON}}') ? str_replace('{{BOTON}}', $boton, $parrafos) : $parrafos . $boton) . '
      <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="margin:0 0 28px;"><tr>
        <td style="padding:14px 16px;background:#e3f3ea;border-radius:12px;font-size:13px;line-height:1.5;color:#2d5b55;">
          <strong>Por seguridad:</strong> el enlace caduca en 30 minutos y solo puede usarse una vez. OZORA nunca te pedirá tu contraseña por correo.
        </td></tr></table>
    </td></tr>
    <tr><td style="padding:20px 40px 30px;border-top:1px solid #e1ebe5;font-size:12px;line-height:1.6;color:#7b9391;">
      <strong style="color:#123f42;">OZORA</strong> · El aire, con más contexto<br>
      Has recibido este correo porque se solicitó acceso a una cuenta de SENDA con esta dirección.
    </td></tr>
  </table>
</td></tr></table>
</body></html>';
}
