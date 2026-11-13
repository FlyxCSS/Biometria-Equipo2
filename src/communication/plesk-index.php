<?php
/*
 * Nombre: index.php
 * Descripción: Entrada pública de SENDA para instalarla en /senda en Plesk.
 * Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
 * Fecha: 03/11/2026
 * Autor: Julia (con asistencia de Codex).
 * Aportación: Aislar el código privado fuera de httpdocs y conservar las otras webs del dominio.
 */
declare(strict_types=1);
define('SENDA_BASE_PATH', '/senda');
require dirname(__DIR__, 2) . '/senda_s2_privado/src/web/index.php';
