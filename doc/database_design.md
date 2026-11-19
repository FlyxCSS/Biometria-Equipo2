# Diseño de database

## Diseño del Componente

Define el catálogo relacional MySQL/MariaDB del Sprint 2, sus relaciones, índices, límites y migraciones. SQLite Android es almacenamiento local del componente android y no sustituye este catálogo.


```text
==========================================================================
TABLE: ORGANIZACIONES
DESCRIPTION: Entidad del catálogo SENDA

COLUMNS:
+ id_organizacion | INT | NOT NULL | Auto-Increment
+ nombre | VARCHAR(100) | NOT NULL | sin valor explícito
+ llaveros_contratados | INT | NOT NULL | sin valor explícito
+ email_contacto | VARCHAR(100) | NOT NULL | sin valor explícito
+ estado | VARCHAR(20) | NOT NULL | 'activo'

PRIMARY KEY: id_organizacion

FOREIGN KEYS:
+ Ninguna

CONSTRAINTS:
+ CHECK (llaveros_contratados >= 0)
==========================================================================
```

```text
==========================================================================
TABLE: PARQUES
DESCRIPTION: Entidad del catálogo SENDA

COLUMNS:
+ id_parque | INT | NOT NULL | Auto-Increment
+ nombre | VARCHAR(100) | NOT NULL | sin valor explícito
+ id_organizacion | INT | NOT NULL | sin valor explícito
+ cupo_llaveros | INT | NOT NULL | sin valor explícito
+ ubicacion | VARCHAR(100) | NULL | sin valor explícito

PRIMARY KEY: id_parque

FOREIGN KEYS:
+ FOREIGN KEY (id_organizacion) REFERENCES ORGANIZACIONES(id_organizacion)

CONSTRAINTS:
+ UNIQUE KEY parque_organizacion (id_parque, id_organizacion)
+ CHECK (cupo_llaveros >= 0)
==========================================================================
```

```text
==========================================================================
TABLE: ROLES
DESCRIPTION: Catálogo de roles con código y descripción

COLUMNS:
+ id_rol | TINYINT UNSIGNED | NOT NULL | sin valor explícito
+ codigo | VARCHAR(20) | NOT NULL | sin valor explícito
+ nombre | VARCHAR(60) | NOT NULL | sin valor explícito
+ descripcion | VARCHAR(255) | NOT NULL | ''

PRIMARY KEY: id_rol

FOREIGN KEYS:
+ Ninguna

CONSTRAINTS:
+ UNIQUE codigo
==========================================================================
```

```text
==========================================================================
TABLE: USUARIOS
DESCRIPTION: Cuentas, rol, parque y fotografía

COLUMNS:
+ id_usuario | INT | NOT NULL | Auto-Increment
+ nombre | VARCHAR(100) | NOT NULL | sin valor explícito
+ email | VARCHAR(100) | NOT NULL | sin valor explícito
+ foto_perfil | MEDIUMTEXT | NULL | sin valor explícito
+ password_hash | VARCHAR(255) | NULL | sin valor explícito
+ debe_cambiar_contrasena | BOOLEAN | NOT NULL | FALSE
+ id_rol | TINYINT UNSIGNED | NOT NULL | sin valor explícito
+ id_parque | INT | NULL | sin valor explícito
+ activo | BOOLEAN | NOT NULL | TRUE
+ id_organizacion | INT | NULL | sin valor explícito

PRIMARY KEY: id_usuario

FOREIGN KEYS:
+ CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES ROLES(id_rol)
+ FOREIGN KEY (id_organizacion) REFERENCES ORGANIZACIONES(id_organizacion)
+ FOREIGN KEY (id_parque, id_organizacion) REFERENCES PARQUES(id_parque, id_organizacion)

CONSTRAINTS:
+ UNIQUE email
+ UNIQUE KEY usuario_parque (id_usuario, id_parque)
+ CHECK (id_parque IS NULL OR id_organizacion IS NOT NULL)
+ CONSTRAINT usuario_rol_parque CHECK (id_rol NOT IN (3, 4) OR id_parque IS NOT NULL)
==========================================================================
```

```text
==========================================================================
TABLE: LLAVEROS
DESCRIPTION: Inventario y asignación única del sensor

COLUMNS:
+ id_llavero | VARCHAR(100) | NOT NULL | sin valor explícito
+ identificador_ble | VARCHAR(100) | NOT NULL | sin valor explícito
+ id_parque | INT | NOT NULL | sin valor explícito
+ id_organizacion | INT | NOT NULL | sin valor explícito
+ estado | BOOLEAN | NOT NULL | TRUE
+ codigo_activacion_hash | VARCHAR(255) | NULL | sin valor explícito
+ id_usuario | INT | NULL | sin valor explícito

PRIMARY KEY: id_llavero

FOREIGN KEYS:
+ FOREIGN KEY (id_parque, id_organizacion) REFERENCES PARQUES(id_parque, id_organizacion)
+ FOREIGN KEY (id_usuario, id_parque) REFERENCES USUARIOS(id_usuario, id_parque)

CONSTRAINTS:
+ UNIQUE identificador_ble
+ UNIQUE id_usuario
==========================================================================
```

```text
==========================================================================
TABLE: MEDICIONES
DESCRIPTION: Lecturas históricas con autor, parque y posición obligatoria

COLUMNS:
+ fecha_hora | DATETIME(3) | NOT NULL | sin valor explícito
+ id_llavero | VARCHAR(100) | NOT NULL | sin valor explícito
+ tipo | VARCHAR(50) | NOT NULL | sin valor explícito
+ valor | DOUBLE | NOT NULL | sin valor explícito
+ latitud | DOUBLE | NOT NULL | sin valor explícito
+ longitud | DOUBLE | NOT NULL | sin valor explícito
+ unidad | VARCHAR(10) | NOT NULL | sin valor explícito
+ simulada | BOOLEAN | NOT NULL | sin valor explícito
+ id_usuario | INT | NOT NULL | sin valor explícito
+ id_parque | INT | NOT NULL | sin valor explícito
+ nombre_micro | VARCHAR(100) | NOT NULL | sin valor explícito
+ recibida_en | DATETIME(3) | NOT NULL | CURRENT_TIMESTAMP(3)

PRIMARY KEY: PRIMARY KEY (fecha_hora, id_llavero, tipo)

FOREIGN KEYS:
+ FOREIGN KEY (id_llavero) REFERENCES LLAVEROS(id_llavero)
+ FOREIGN KEY (id_usuario) REFERENCES USUARIOS(id_usuario)
+ FOREIGN KEY (id_parque) REFERENCES PARQUES(id_parque)

CONSTRAINTS:
+ KEY ultima_parque (id_parque, fecha_hora, nombre_micro)
+ CONSTRAINT medicion_tipo_unidad CHECK ((tipo = 'O3' AND unidad = 'ppb') OR (tipo = 'TEMPERATURA' AND unidad = 'C'))
+ CONSTRAINT medicion_valor CHECK ((tipo = 'O3' AND valor BETWEEN 0 AND 65535 AND valor = FLOOR(valor))
+ OR (tipo = 'TEMPERATURA' AND valor BETWEEN -40 AND 50 AND ABS(valor * 100 - ROUND(valor * 100)) < 0.000001))
+ CONSTRAINT medicion_posicion CHECK (latitud IS NOT NULL AND longitud IS NOT NULL)
+ CHECK (latitud BETWEEN -90 AND 90)
+ CHECK (longitud BETWEEN -180 AND 180)
==========================================================================
```

```text
==========================================================================
TABLE: SESIONES
DESCRIPTION: Huellas de credenciales y vencimiento

COLUMNS:
+ token_hash | CHAR(64) | NOT NULL | sin valor explícito
+ id_usuario | INT | NOT NULL | sin valor explícito
+ expira_en | DATETIME(3) | NOT NULL | sin valor explícito

PRIMARY KEY: token_hash

FOREIGN KEYS:
+ FOREIGN KEY (id_usuario) REFERENCES USUARIOS(id_usuario)

CONSTRAINTS:
+ KEY sesiones_usuario (id_usuario)
==========================================================================
```

```text
==========================================================================
TABLE: TOKENS_ACCESO
DESCRIPTION: Enlaces de alta o recuperación de un uso

COLUMNS:
+ token_hash | CHAR(64) | NOT NULL | sin valor explícito
+ id_usuario | INT | NOT NULL | sin valor explícito
+ finalidad | VARCHAR(20) | NOT NULL | sin valor explícito
+ expira_en | DATETIME(3) | NOT NULL | sin valor explícito
+ usado_en | DATETIME(3) | NULL | sin valor explícito

PRIMARY KEY: token_hash

FOREIGN KEYS:
+ FOREIGN KEY (id_usuario) REFERENCES USUARIOS(id_usuario)

CONSTRAINTS:
+ KEY tokens_usuario (id_usuario)
+ CHECK (finalidad IN ('alta', 'recuperacion'))
==========================================================================
```

```text
==========================================================================
TABLE: CORREOS_PENDIENTES
DESCRIPTION: Buzón transaccional con reintentos

COLUMNS:
+ id_correo | INT | NOT NULL | Auto-Increment
+ destinatario | VARCHAR(100) | NOT NULL | sin valor explícito
+ asunto | VARCHAR(150) | NOT NULL | sin valor explícito
+ cuerpo | TEXT | NOT NULL | sin valor explícito
+ enviado_en | DATETIME(3) | NULL | sin valor explícito
+ intentos | INT | NOT NULL | 0
+ creado_en | DATETIME(3) | NOT NULL | CURRENT_TIMESTAMP(3)

PRIMARY KEY: id_correo

FOREIGN KEYS:
+ Ninguna

CONSTRAINTS:
+ Clave primaria y nulabilidad de columnas
==========================================================================
```

```text
==========================================================================
TABLE: LIMITES
DESCRIPTION: Contadores de intentos por ventana

COLUMNS:
+ clave | CHAR(64) | NOT NULL | sin valor explícito
+ ventana | DATETIME | NOT NULL | sin valor explícito
+ intentos | INT | NOT NULL | sin valor explícito

PRIMARY KEY: clave

FOREIGN KEYS:
+ Ninguna

CONSTRAINTS:
+ Clave primaria y nulabilidad de columnas
==========================================================================
```

```text
==========================================================================
TABLE: SOLICITUDES_CONTACTO
DESCRIPTION: Entidad del catálogo SENDA

COLUMNS:
+ id_solicitud | INT | NOT NULL | Auto-Increment
+ nombre | VARCHAR(100) | NOT NULL | sin valor explícito
+ correo | VARCHAR(100) | NOT NULL | sin valor explícito
+ organizacion | VARCHAR(100) | NOT NULL | sin valor explícito
+ mensaje | TEXT | NOT NULL | sin valor explícito
+ recibida_en | DATETIME(3) | NOT NULL | CURRENT_TIMESTAMP(3)

PRIMARY KEY: id_solicitud

FOREIGN KEYS:
+ Ninguna

CONSTRAINTS:
+ Clave primaria y nulabilidad de columnas
==========================================================================
```

DDL completo normativo: [database.sql](../src/database/database.sql). Las restricciones multilínea se conservan íntegramente en él. Migraciones 001..006: aplicar solo las que correspondan a una instalación anterior; no repetirlas sobre el esquema nuevo.

### Diccionario de tipos auxiliares

Dato = Text | Z | R | B | Nulo | [Dato]. Datos = [(clave:Text, valor:Dato)]. Los tipos de dominio detallados en el contrato conservan esa estructura.

- Buzón: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- CORREOS_PENDIENTES: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Catálogo: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Contadores: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Cuentas: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Enlaces: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Entidad: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Huellas: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Inventario: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- LIMITES: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- LLAVEROS: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Lecturas: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- MEDICIONES: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- ORGANIZACIONES: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- PARQUES: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- PRIMARY: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- ROLES: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- SESIONES: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- SOLICITUDES_CONTACTO: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- SQL: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- TOKENS_ACCESO: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- USUARIOS: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.

## Aclaraciones del Diseño

- Se describe el código actual del Sprint 2; el ejemplo del Sprint 0 se usa para notación, no para copiar comportamientos obsoletos.
- N, Z, R, B y Text son naturales, enteros, reales, booleanos y texto. [T] es una colección; Nulo indica ausencia.
- Los tipos de plataforma citados en las fichas se consideran adaptadores opacos; no autorizan dependencias entre capas.
- Las operaciones auxiliares y callbacks completos se trazan en [el inventario](materiales/informes/revision-funciones.md).

## Reglas Generales

- Lenguaje objetivo: SQL MySQL/MariaDB.
- Cada función o método lleva diseño lógico y descripción en un bloque delimitado por líneas discontinuas.
- + representa interfaz pública y - implementación privada; --> muta o produce efectos, <-- consulta, --x operación estática.
- Código legible, responsabilidad única y auxiliares privados mínimos. No inventar getters ni nuevas operaciones públicas.
- Proteger el estado mutable por confinamiento, transacciones o sincronización explícita según el componente.
- Generar y ejecutar pruebas automatizadas de operaciones críticas, errores, límites, aislamiento y concurrencia cuando el entorno lo permita. No acceder a campos privados para afirmar resultados.
- No introducir secretos, autenticación maestra, respuestas ficticias de éxito, commits ni despliegues automáticos.
