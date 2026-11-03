-- Nombre: 002_contrasena_inicial.sql
-- Descripción: Marca el cambio obligatorio de la contraseña inicial asignada por el jefe.
-- Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
-- Fecha: 03/11/2026
-- Autor: Julia (con asistencia de Codex).
-- Aportación: Ampliar las cuentas existentes sin cambiar ni inventar sus contraseñas.
ALTER TABLE USUARIOS ADD COLUMN debe_cambiar_contrasena BOOLEAN NOT NULL DEFAULT FALSE AFTER password_hash;
