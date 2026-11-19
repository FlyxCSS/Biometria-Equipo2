-- Solo si se ejecutó la versión inicial de 005 que admitía temperatura sin GPS.
-- Rechaza nuevas escrituras sin coordenadas. No borra ni modifica mediciones.
-- Si existen filas sin GPS, el ALTER falla conservando los datos: resolver su
-- procedencia antes de aplicar; no rellenar con coordenadas inventadas.
ALTER TABLE MEDICIONES ADD CONSTRAINT medicion_gps_obligatorio
    CHECK (latitud IS NOT NULL AND longitud IS NOT NULL);
