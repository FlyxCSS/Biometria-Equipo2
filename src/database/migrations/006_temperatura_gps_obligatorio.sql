-- Nombre: 006_temperatura_gps_obligatorio.sql
-- Descripción: Esquema relacional, integridad y evolución de datos.
-- Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
-- Fecha: 19/11/2026
-- Autores: Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi
-- Aportación: Recursos y configuración del componente database.
-- Solo si se ejecutó la versión inicial de 005 que admitía temperatura sin GPS.
-- Rechaza nuevas escrituras sin coordenadas. No borra ni modifica mediciones.
-- Si existen filas sin GPS, el ALTER falla conservando los datos: resolver su
-- procedencia antes de aplicar; no rellenar con coordenadas inventadas.
ALTER TABLE MEDICIONES ADD CONSTRAINT medicion_gps_obligatorio
    CHECK (latitud IS NOT NULL AND longitud IS NOT NULL);
