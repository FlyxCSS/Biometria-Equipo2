<?php
/*
 * Nombre: example.php
 * Descripción: Plantilla privada de configuración del entorno.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 02/11/2026
 * Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
 * Aportación: Plantilla privada de configuración del entorno.
 */


return [
    'dsn' => 'mysql:host=127.0.0.1;port=3306;dbname=senda;charset=utf8mb4',
    'db_user' => 'senda',
    'db_password' => 'CAMBIAR_EN_LOCAL',
    'app_url' => 'http://127.0.0.1:8080',
    'environment' => 'local', // En producción exigir HTTPS y usar transporte mail.
    'mail_transport' => 'mail', // file: buzón local privado; mail: transporte configurado de PHP.
    'mail_from' => 'senda@example.org',
    'mail_directory' => dirname(__DIR__, 2) . '/var/mail',
    'session_hours' => 24,
];
