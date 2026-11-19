# Diseño de tools

## Diseño del Componente

Operaciones administrativas y herramientas locales: instalación, migraciones, datos de prueba, correo y visor local restringido. No forman parte del acceso público ordinario.

### Ejecución administrativa

gestionar.php es solo CLI. instalar exige una base vacía; actualizar-contacto, actualizar-contrasenas, actualizar-roles y actualizar-perfil son operaciones explícitas de evolución. demo requiere environment=local. correo procesa la cola transaccional. limpiar solo elimina registros técnicos caducados. El visor bbdd es de consulta y restringe dirección local, nombre de host y ruta; no debe desplegarse públicamente. No ejecutar utilidades de escritura contra producción para probarlas.

### enviar-demo

Implementación: [src/tools/enviar-demo.php](../src/tools/enviar-demo.php).

```text
                 -------- enviar-demo --------
                 |
                 |
                 |
ruta: Text, datos: [Dato], token: Text --> enviarDemo() -->
      [Dato] <--
                 |
                 --------------------------------------
```

### gestionar

Implementación: [src/tools/gestionar.php](../src/tools/gestionar.php).

```text
                 -------- gestionar --------
                 |
                 |
                 |
db: Deposito --> instalarEsquema() -->
                 |
                 |
db: Deposito --> actualizarContacto() -->
                 |
                 |
db: Deposito --> actualizarContrasenas() -->
                 |
                 |
db: Deposito --> actualizarRoles() -->
                 |
                 |
db: Deposito, config: [Dato] --> crearDemo() -->
                 |
                 --------------------------------------
```

### index

Implementación: [src/tools/bbdd/index.php](../src/tools/bbdd/index.php).

```text
                 -------- index --------
                 |
                 |
                 |
valor: Dato --> escapar() -->
      Text <--
                 |
                 --------------------------------------
```

### Diccionario de tipos auxiliares

Dato = Text | Z | R | B | Nulo | [Dato]. Datos = [(clave:Text, valor:Dato)]. Los tipos de dominio detallados en el contrato conservan esa estructura.

- Deposito: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- PHP: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.

## Aclaraciones del Diseño

- Se describe el código actual del Sprint 2; el ejemplo del Sprint 0 se usa para notación, no para copiar comportamientos obsoletos.
- N, Z, R, B y Text son naturales, enteros, reales, booleanos y texto. [T] es una colección; Nulo indica ausencia.
- Los tipos de plataforma citados en las fichas se consideran adaptadores opacos; no autorizan dependencias entre capas.
- Las operaciones auxiliares y callbacks completos se trazan en [el inventario](materiales/informes/revision-funciones.md).

## Reglas Generales

- Lenguaje objetivo: PHP 8.2 o superior y PowerShell.
- Cada función o método lleva diseño lógico y descripción en un bloque delimitado por líneas discontinuas.
- + representa interfaz pública y - implementación privada; --> muta o produce efectos, <-- consulta, --x operación estática.
- Código legible, responsabilidad única y auxiliares privados mínimos. No inventar getters ni nuevas operaciones públicas.
- Proteger el estado mutable por confinamiento, transacciones o sincronización explícita según el componente.
- Generar y ejecutar pruebas automatizadas de operaciones críticas, errores, límites, aislamiento y concurrencia cuando el entorno lo permita. No acceder a campos privados para afirmar resultados.
- No introducir secretos, autenticación maestra, respuestas ficticias de éxito, commits ni despliegues automáticos.
