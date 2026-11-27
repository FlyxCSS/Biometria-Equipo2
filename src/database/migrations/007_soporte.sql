-- Nombre: 007_soporte.sql
-- Descripción: Soporte técnico tipo foro: consultas (hilos) y sus respuestas.
-- Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
-- Fecha: 27/11/2026
-- Aportación: Las solicitudes del formulario de contacto y las consultas de jefes de parque
--             y organizaciones llegan al técnico (rol proveedor), que responde desde la web.

CREATE TABLE IF NOT EXISTS SOPORTE_HILOS (
    id_hilo INT PRIMARY KEY AUTO_INCREMENT,
    -- Autor con cuenta (jefe de parque u organización). NULL si llega del formulario público.
    id_autor INT NULL,
    -- Datos de contacto del formulario público (o copia de los del autor).
    nombre_contacto VARCHAR(100) NOT NULL,
    correo_contacto VARCHAR(100) NOT NULL,
    organizacion_contacto VARCHAR(100) NOT NULL DEFAULT '',
    asunto VARCHAR(150) NOT NULL,
    origen ENUM('contacto', 'soporte') NOT NULL,
    estado ENUM('abierto', 'respondido', 'cerrado') NOT NULL DEFAULT 'abierto',
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    actualizado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY hilos_estado (estado, actualizado_en),
    KEY hilos_autor (id_autor, actualizado_en),
    CONSTRAINT fk_hilo_autor FOREIGN KEY (id_autor) REFERENCES USUARIOS(id_usuario) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS SOPORTE_MENSAJES (
    id_mensaje INT PRIMARY KEY AUTO_INCREMENT,
    id_hilo INT NOT NULL,
    -- NULL en el primer mensaje del formulario público.
    id_autor INT NULL,
    es_tecnico BOOLEAN NOT NULL DEFAULT FALSE,
    texto TEXT NOT NULL,
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY mensajes_hilo (id_hilo, id_mensaje),
    CONSTRAINT fk_mensaje_hilo FOREIGN KEY (id_hilo) REFERENCES SOPORTE_HILOS(id_hilo) ON DELETE CASCADE,
    CONSTRAINT fk_mensaje_autor FOREIGN KEY (id_autor) REFERENCES USUARIOS(id_usuario) ON DELETE SET NULL
) ENGINE=InnoDB;
