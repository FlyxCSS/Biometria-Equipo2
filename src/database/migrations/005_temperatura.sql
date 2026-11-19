-- Aplicar antes de desplegar la nueva API. Conserva todas las mediciones existentes.
-- Los nombres automáticos de CHECK varían entre instalaciones MySQL y MariaDB.
DELIMITER //
CREATE PROCEDURE migrar_temperatura_005()
BEGIN
    DECLARE terminado INT DEFAULT 0;
    DECLARE nombre VARCHAR(128);
    DECLARE controles CURSOR FOR
        SELECT tc.CONSTRAINT_NAME FROM information_schema.TABLE_CONSTRAINTS tc
        JOIN information_schema.CHECK_CONSTRAINTS cc
          ON cc.CONSTRAINT_SCHEMA=tc.CONSTRAINT_SCHEMA AND cc.CONSTRAINT_NAME=tc.CONSTRAINT_NAME
        WHERE tc.TABLE_SCHEMA=DATABASE() AND tc.TABLE_NAME='MEDICIONES'
          AND tc.CONSTRAINT_TYPE='CHECK'
          AND (LOWER(cc.CHECK_CLAUSE) LIKE '%tipo%' OR LOWER(cc.CHECK_CLAUSE) LIKE '%valor%');
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET terminado=1;
    OPEN controles;
    bucle: LOOP
        FETCH controles INTO nombre;
        IF terminado THEN LEAVE bucle; END IF;
        SET @ddl=CONCAT('ALTER TABLE MEDICIONES DROP ',
            IF(VERSION() LIKE '%MariaDB%', 'CONSTRAINT ', 'CHECK '), '`', REPLACE(nombre,'`','``'), '`');
        PREPARE sentencia FROM @ddl;
        EXECUTE sentencia;
        DEALLOCATE PREPARE sentencia;
    END LOOP;
    CLOSE controles;
END//
DELIMITER ;
CALL migrar_temperatura_005();
DROP PROCEDURE migrar_temperatura_005;
ALTER TABLE MEDICIONES
    MODIFY latitud DOUBLE NOT NULL,
    MODIFY longitud DOUBLE NOT NULL,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (fecha_hora, id_llavero, tipo),
    ADD CONSTRAINT medicion_tipo_unidad CHECK ((tipo = 'O3' AND unidad = 'ppb') OR (tipo = 'TEMPERATURA' AND unidad = 'C')),
    ADD CONSTRAINT medicion_valor CHECK ((tipo = 'O3' AND valor BETWEEN 0 AND 65535 AND valor = FLOOR(valor))
        OR (tipo = 'TEMPERATURA' AND valor BETWEEN -40 AND 50 AND ABS(valor * 100 - ROUND(valor * 100)) < 0.000001)),
    ADD CONSTRAINT medicion_posicion CHECK (latitud IS NOT NULL AND longitud IS NOT NULL);
