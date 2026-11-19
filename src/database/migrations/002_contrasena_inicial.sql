-- Nombre: 002_contrasena_inicial.sql
-- Descripción: Esquema relacional, integridad y evolución de datos.
-- Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
-- Fecha: 19/11/2026
-- Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
-- Aportación: Recursos y configuración del componente database.
ALTER TABLE USUARIOS ADD COLUMN debe_cambiar_contrasena BOOLEAN NOT NULL DEFAULT FALSE AFTER password_hash;
