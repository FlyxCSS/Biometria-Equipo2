# Prompt SDD · config

## Objetivo y alcance

Implementa de forma completa `src/config/` a partir de `doc/config_design.md`. Responsabilidad: Proporciona una plantilla para datos de entorno y permite cargar la configuración privada sin incorporarla al repositorio.. Este prompt se envía en el mismo chat que los otros módulos, pero cada contrato debe poder verificarse en archivos.

## Entradas normativas

- Lee `doc/config_design.md` y `doc/materiales/referencias/plantilla-sdd.md`.

## /specify · Criterios de aceptación

Crear solo example.php con placeholders explícitos. La configuración efectiva se aporta externamente y se excluye de Git; no generar contraseñas utilizables.

## /plan · Estrategia

Lee el código existente y el inventario de funciones. Expón dependencias, invariantes, archivos afectados y pruebas antes de editar. Si falta una especificación, registra la carencia y solicita la decisión mínima necesaria; no la sustituyas por un supuesto oculto. Para una carpeta vacía, crea toda su estructura conforme al diseño y a los recursos suministrados.

## /tasks · Secuencia de trabajo

1. Contrastar tipos y firmas con módulos ya generados; registrar la adaptación de contexto autenticado entre cliente y servidor.
2. Preparar clases/modelos y dependencias internas; no mover reglas de negocio a vistas ni a transporte.
3. Implementar flujos normales y errores; preservar restricciones y compatibilidad de la implementación previa.
4. Preparar pruebas de comportamiento público, límites, aislamiento y sincronización.
5. Revisar correspondencia diseño/código y referencias entre carpetas.

## /implement · Validación y entrega

php -l src/config/example.php. Comprobar que local.php no está versionado y que las rutas públicas no pueden servir configuración ni correo privado.

Entrega los archivos completos, una lista concreta de cambios, comandos ejecutados y sus resultados. Distingue pruebas pasadas, omitidas y bloqueadas. No certifiques comportamiento de hardware o servidor que no hayas observado.

## Reglas compartidas

- Fuente de verdad: el diseño del módulo, el catálogo de datos y las referencias del profesor en doc/materiales/referencias. No depender de recuerdos de este chat; releer los contratos antes de cada módulo.
- Mantener doc/, src/, agents/, AUTHORS.md, README.md y .gitignore. No modificar agents. Una carpeta de primer nivel de src corresponde a un xxx_design.md y un xxx_prompt.md.
- Diseñar primero, implementar después. No inventar interfaces ni añadir métodos públicos ausentes del diseño. Helpers privados mínimos y responsabilidades separadas.
- N, Z, R, B, Text, agregaciones y colecciones para contratos lógicos; + público, - privado; --> efectos, <-- consulta, --x estático. No exponer estado privado en pruebas.
- Cabecera de archivo: Nombre, Descripción, Copyright (c) 2026 Equipo 2 - SENDA / OZORA, Fecha, Autores (Fabián Andrés Useche Rivera; Élia Garcia Pons; Judith Civico Aporta; Julia Valen de Oliveira; Soufiane Belfquih El Khalfi), Aportación. Conservar fechas y atribuciones históricas. Base original Jordi Bataller i Mascarell en el firmware.
- Cada función con nombre debe tener contrato de entradas/salida y descripción breve entre líneas discontinuas. No introducir comentarios en JSON ni alterar formatos o código de terceros para forzar una cabecera.
- Estado mutable: confinarlo al hilo del componente o usar mecanismos explícitos. Persistencia concurrente: transacciones, bloqueo y claves de integridad. No prometer thread-safety sin comprobarla.
- No incluir credenciales, datos personales reales, contraseñas maestras ni éxitos simulados. No ejecutar pruebas de escritura en producción.
- No iniciar emuladores, subir firmware, instalar en teléfonos, desplegar, hacer commit, push, merge ni crear copias de seguridad automáticamente.

## Inventario de partida

- El esquema y los recursos descritos en el diseño del módulo.
