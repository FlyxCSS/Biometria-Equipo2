-- Nombre: database.sql
-- Descripción: Esquema MySQL 8.4 basado en el diagrama del equipo y ampliaciones técnicas.
-- Copyright: (c) 2026 Equipo 2 - SENDA / OZORA.
-- Fecha: 02/11/2026
-- Autor: Julia (con asistencia de Codex).
-- Aportación: Tablas, integridad, sesiones, recuperación e índices del Sprint 2.
-- Importar en una BD vacía elegida previamente. No elimina tablas existentes.
SET NAMES utf8mb4;
SET time_zone = '+00:00';

CREATE TABLE ORGANIZACIONES (
    id_organizacion INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    llaveros_contratados INT NOT NULL,
    email_contacto VARCHAR(100) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'activo',
    CHECK (llaveros_contratados >= 0)
) ENGINE=InnoDB;

CREATE TABLE PARQUES (
    id_parque INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    id_organizacion INT NOT NULL,
    cupo_llaveros INT NOT NULL,
    ubicacion VARCHAR(100),
    UNIQUE KEY parque_organizacion (id_parque, id_organizacion),
    FOREIGN KEY (id_organizacion) REFERENCES ORGANIZACIONES(id_organizacion),
    CHECK (cupo_llaveros >= 0)
) ENGINE=InnoDB;

CREATE TABLE USUARIOS (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255), -- NULL solo en cuentas antiguas que aún no han establecido su contraseña.
    debe_cambiar_contrasena BOOLEAN NOT NULL DEFAULT FALSE,
    rol VARCHAR(20) NOT NULL,
    id_parque INT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    id_organizacion INT,
    UNIQUE KEY usuario_parque (id_usuario, id_parque),
    FOREIGN KEY (id_organizacion) REFERENCES ORGANIZACIONES(id_organizacion),
    FOREIGN KEY (id_parque, id_organizacion) REFERENCES PARQUES(id_parque, id_organizacion),
    CHECK (id_parque IS NULL OR id_organizacion IS NOT NULL),
    CHECK (rol IN ('proveedor', 'organizacion', 'jefe_parque', 'trabajador')),
    CHECK (rol NOT IN ('jefe_parque', 'trabajador') OR id_parque IS NOT NULL)
) ENGINE=InnoDB;

CREATE TABLE LLAVEROS (
    id_llavero VARCHAR(100) PRIMARY KEY,
    identificador_ble VARCHAR(100) NOT NULL UNIQUE,
    id_parque INT NOT NULL,
    id_organizacion INT NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    codigo_activacion_hash VARCHAR(255),
    id_usuario INT UNIQUE,
    FOREIGN KEY (id_parque, id_organizacion) REFERENCES PARQUES(id_parque, id_organizacion),
    FOREIGN KEY (id_usuario, id_parque) REFERENCES USUARIOS(id_usuario, id_parque)
) ENGINE=InnoDB;

CREATE TABLE MEDICIONES (
    fecha_hora DATETIME(3) NOT NULL,
    id_llavero VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    valor DOUBLE NOT NULL,
    latitud DOUBLE NOT NULL,
    longitud DOUBLE NOT NULL,
    -- Ampliaciones: unidad, procedencia de prueba, autor y parque histórico.
    unidad VARCHAR(10) NOT NULL,
    simulada BOOLEAN NOT NULL,
    id_usuario INT NOT NULL,
    id_parque INT NOT NULL,
    nombre_micro VARCHAR(100) NOT NULL,
    recibida_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (fecha_hora, id_llavero),
    KEY ultima_parque (id_parque, fecha_hora, nombre_micro),
    FOREIGN KEY (id_llavero) REFERENCES LLAVEROS(id_llavero),
    FOREIGN KEY (id_usuario) REFERENCES USUARIOS(id_usuario),
    FOREIGN KEY (id_parque) REFERENCES PARQUES(id_parque),
    CHECK (tipo = 'O3' AND unidad = 'ppb'),
    CHECK (valor BETWEEN 0 AND 65535 AND valor = FLOOR(valor)),
    CHECK (latitud BETWEEN -90 AND 90),
    CHECK (longitud BETWEEN -180 AND 180)
) ENGINE=InnoDB;

CREATE TABLE SESIONES (
    token_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
    id_usuario INT NOT NULL,
    expira_en DATETIME(3) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES USUARIOS(id_usuario),
    KEY sesiones_usuario (id_usuario)
) ENGINE=InnoDB;

CREATE TABLE TOKENS_ACCESO (
    token_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
    id_usuario INT NOT NULL,
    finalidad VARCHAR(20) NOT NULL,
    expira_en DATETIME(3) NOT NULL,
    usado_en DATETIME(3),
    FOREIGN KEY (id_usuario) REFERENCES USUARIOS(id_usuario),
    KEY tokens_usuario (id_usuario),
    CHECK (finalidad IN ('alta', 'recuperacion'))
) ENGINE=InnoDB;

-- Buzón transaccional: no se pierde el correo si falla el transporte después del alta.
CREATE TABLE CORREOS_PENDIENTES (
    id_correo INT PRIMARY KEY AUTO_INCREMENT,
    destinatario VARCHAR(100) NOT NULL,
    asunto VARCHAR(150) NOT NULL,
    cuerpo TEXT NOT NULL,
    enviado_en DATETIME(3),
    intentos INT NOT NULL DEFAULT 0,
    creado_en DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB;

-- Límite por operación, IP y correo normalizado, sin almacenar estos últimos en claro.
CREATE TABLE LIMITES (
    clave CHAR(64) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
    ventana DATETIME NOT NULL,
    intentos INT NOT NULL
) ENGINE=InnoDB;
