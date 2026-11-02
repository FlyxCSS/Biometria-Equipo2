-- Nombre: 001_solicitudes_contacto.sql
-- Descripción: Amplía la BD con solicitudes de información de la web pública.
-- Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
-- Fecha: 02/11/2026
-- Autor: Julia (con asistencia de Codex).
-- Aportación: Persistencia del formulario de contacto, sin alterar datos existentes.
CREATE TABLE SOLICITUDES_CONTACTO (
    id_solicitud INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    organizacion VARCHAR(100) NOT NULL,
    mensaje TEXT NOT NULL,
    recibida_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
