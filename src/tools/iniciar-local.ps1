# Nombre: iniciar-local.ps1
# Descripción: Arranca las herramientas portables ya preparadas para la demo local.
# Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
# Fecha: 02/11/2026
# Autor: Julia (con asistencia de Codex).
# Aportación: Facilitar el arranque tras cerrar Windows sin instalar servicios.
$ErrorActionPreference = 'Stop'
$raizSenda = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$phpSenda = Join-Path $raizSenda '.local/tools/php/php.exe'
$mysqlSenda = @(Get-ChildItem (Join-Path $raizSenda '.local/tools/mysql') -Directory -Filter 'mysql-*-winx64')
if (!(Test-Path $phpSenda) -or $mysqlSenda.Count -ne 1 -or !(Test-Path (Join-Path $raizSenda 'src/config/local.php'))) {
    throw 'Falta el entorno portable o su configuración. Consulta doc/backend-puesta-en-marcha.md.'
}
$datosSenda = Join-Path $raizSenda '.local/mysql-data'
if (!(Get-NetTCPConnection -LocalPort 3307 -State Listen -ErrorAction SilentlyContinue)) {
    $baseSenda = $mysqlSenda[0].FullName
    Start-Process -FilePath (Join-Path $baseSenda 'bin/mysqld.exe') -ArgumentList @('--no-defaults', "--basedir=`"$baseSenda`"", "--datadir=`"$datosSenda`"", '--bind-address=127.0.0.1', '--port=3307', '--mysqlx=OFF', "--log-error=`"$datosSenda/servidor.log`"") -WindowStyle Hidden
    Start-Sleep -Seconds 3
}
if (!(Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue)) {
    Start-Process -FilePath $phpSenda -ArgumentList @('-S', '127.0.0.1:8080', '-t', "`"$raizSenda/src/web`"", "`"$raizSenda/src/router.php`"") -WorkingDirectory $raizSenda -WindowStyle Hidden -RedirectStandardOutput (Join-Path $raizSenda '.local/http.out.log') -RedirectStandardError (Join-Path $raizSenda '.local/http.err.log')
}
Write-Output 'Web local: http://127.0.0.1:8080. Los servicios solo escuchan en este equipo.'
if (!(Get-NetTCPConnection -LocalPort 8082 -State Listen -ErrorAction SilentlyContinue)) {
    Start-Process -FilePath $phpSenda -ArgumentList @('-S', '127.0.0.1:8082', '-t', "`"$raizSenda/src/tools/bbdd`"", "`"$raizSenda/src/tools/bbdd/index.php`"") -WorkingDirectory $raizSenda -WindowStyle Hidden -RedirectStandardOutput (Join-Path $raizSenda '.local/bbdd.out.log') -RedirectStandardError (Join-Path $raizSenda '.local/bbdd.err.log')
}
Write-Output 'Visor de base de datos: http://127.0.0.1:8082 (solo lectura).'
