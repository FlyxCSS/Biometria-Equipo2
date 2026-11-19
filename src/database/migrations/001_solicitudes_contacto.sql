-- Nombre: 001_solicitudes_contacto.sql
-- Descripción: Esquema relacional, integridad y evolución de datos.
-- Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
-- Fecha: 19/11/2026
-- Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
-- Aportación: Recursos y configuración del componente database.
CREATE TABLE SOLICITUDES_CONTACTO (
    id_solicitud INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    organizacion VARCHAR(100) NOT NULL,
    mensaje TEXT NOT NULL,
    recibida_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
