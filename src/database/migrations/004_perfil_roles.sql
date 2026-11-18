-- Aplicar una sola vez sobre una instalación existente, con copia previa.
ALTER TABLE ROLES ADD COLUMN descripcion VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE USUARIOS ADD COLUMN foto_perfil MEDIUMTEXT NULL;
UPDATE ROLES SET descripcion = CASE codigo
    WHEN 'proveedor' THEN 'Administración del servicio y organizaciones clientes'
    WHEN 'organizacion' THEN 'Gestión de parques y responsables de la organización'
    WHEN 'jefe_parque' THEN 'Supervisión del parque y gestión de trabajadores'
    WHEN 'trabajador' THEN 'Captura de mediciones y recorridos de campo'
    ELSE descripcion END;
