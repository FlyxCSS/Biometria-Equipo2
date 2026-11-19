# Prompt SDD · web

## Objetivo y alcance

Implementa de forma completa `src/web/` a partir de `doc/web_design.md`. Responsabilidad: Interfaz pública OZORA y panel SENDA. Las pantallas invocan exclusivamente el proxy de negocio, representan datos y validan formularios; no implementan reglas de autorización ni acceso a la base.. Este prompt se envía en el mismo chat que los otros módulos, pero cada contrato debe poder verificarse en archivos.

## Entradas normativas

- Lee `doc/web_design.md` y `doc/materiales/referencias/plantilla-sdd.md`.
- Consulta `doc/frontend_business_logic_design.md` para sus contratos y límites.

## /specify · Criterios de aceptación

Separar markup/estilos/eventos y proxy. Mantener OZORA pública, acceso SENDA, recuperación, primer cambio, panel y alta de trabajadores. No añadir una pantalla web de temperatura que no esté diseñada ni alterar las unidades de la lectura O3.

## /plan · Estrategia

Lee el código existente y el inventario de funciones. Expón dependencias, invariantes, archivos afectados y pruebas antes de editar. Si falta una especificación, registra la carencia y solicita la decisión mínima necesaria; no la sustituyas por un supuesto oculto. Para una carpeta vacía, crea toda su estructura conforme al diseño y a los recursos suministrados.

## /tasks · Secuencia de trabajo

1. Contrastar tipos y firmas con módulos ya generados; registrar la adaptación de contexto autenticado entre cliente y servidor.
2. Preparar clases/modelos y dependencias internas; no mover reglas de negocio a vistas ni a transporte.
3. Implementar flujos normales y errores; preservar restricciones y compatibilidad de la implementación previa.
4. Preparar pruebas de comportamiento público, límites, aislamiento y sincronización.
5. Revisar correspondencia diseño/código y referencias entre carpetas.

## /implement · Validación y entrega

node --check sobre scripts propios, pruebas del proxy y comprobación local de acceso, recuperación, panel y formulario. Verificar consola y navegación; no tratar datos ilustrativos como lecturas reales.

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

- `src/web/js/app.js`
- `src/web/js/publica.js`
