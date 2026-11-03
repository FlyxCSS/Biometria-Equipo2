-- Nombre: 003_roles.sql
-- Descripción: Normaliza los roles de instalaciones anteriores sin eliminar usuarios.
-- Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
-- Fecha: 03/11/2026.
-- Autor: Julia (con asistencia de Codex).
-- Aportación: Catálogo de roles y migración de la relación de cada cuenta.
-- Ejecutar por consola con tools/gestionar.php actualizar-roles, con copia previa.
-- No importar este archivo completo con phpMyAdmin: su analizador puede cambiar
-- el contexto a information_schema al inspeccionar las restricciones antiguas.
-- MySQL/MariaDB confirman DDL implícitamente: no envolver en una transacción ficticia.
CREATE TABLE ROLES (
    id_rol TINYINT UNSIGNED PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(60) NOT NULL
) ENGINE=InnoDB;
INSERT INTO ROLES (id_rol, codigo, nombre) VALUES
    (1, 'proveedor', 'Proveedor'), (2, 'organizacion', 'Organización'),
    (3, 'jefe_parque', 'Jefe de parque'), (4, 'trabajador', 'Trabajador');
ALTER TABLE USUARIOS ADD COLUMN id_rol TINYINT UNSIGNED NULL AFTER debe_cambiar_contrasena;
UPDATE USUARIOS u JOIN ROLES r ON r.codigo = u.rol SET u.id_rol = r.id_rol;
-- Un rol desconocido deja NULL y detiene este paso antes de retirar la columna original.
ALTER TABLE USUARIOS MODIFY id_rol TINYINT UNSIGNED NOT NULL,
    ADD CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES ROLES(id_rol),
    ADD CONSTRAINT usuario_rol_parque CHECK (id_rol NOT IN (3, 4) OR id_parque IS NOT NULL);

-- Los nombres automáticos de los CHECK antiguos varían entre MySQL y MariaDB.
-- Solo retiramos los que mencionan la antigua columna rol, conservando los demás.
SELECT GROUP_CONCAT(CONCAT(
    IF(VERSION() LIKE '%MariaDB%', 'DROP CONSTRAINT `', 'DROP CHECK `'),
    REPLACE(c.CONSTRAINT_NAME, '`', '``'), '`, ') SEPARATOR '') INTO @senda_checks_rol
FROM information_schema.CHECK_CONSTRAINTS c
JOIN information_schema.TABLE_CONSTRAINTS t
  ON t.CONSTRAINT_SCHEMA = c.CONSTRAINT_SCHEMA AND t.CONSTRAINT_NAME = c.CONSTRAINT_NAME
WHERE t.TABLE_SCHEMA = DATABASE() AND t.TABLE_NAME = 'USUARIOS'
  AND t.CONSTRAINT_TYPE = 'CHECK'
  AND REPLACE(LOWER(c.CHECK_CLAUSE), '`', '') REGEXP '(^|[^a-z_])rol([^a-z_]|$)';
SET @senda_ddl_roles = CONCAT('ALTER TABLE USUARIOS ', COALESCE(@senda_checks_rol, ''), 'DROP COLUMN rol');
PREPARE senda_roles FROM @senda_ddl_roles;
EXECUTE senda_roles;
DEALLOCATE PREPARE senda_roles;
