# Diseño de tests

## Diseño del Componente

Pruebas reproducibles del negocio y de los proxies, casos JSON/HTTP y preparación de pruebas Android. Las pruebas de escritura integrales necesitan una base aislada terminada en _test.

### Aceptación y aislamiento

validacion.php comprueba tipos, rangos, unidades y coordenadas; perfil-llavero.php usa SQLite aislado para foto y asignación; historial.php comprueba paginación y cuenta. integracion.php exige entorno local y base terminada en _test: valida el contrato real y persistencia, reintentos y temperatura. logica-fake.test.cjs usa transporte sustituido para validar el proxy web. Las pruebas Java pertenecen al componente android: JVM para reglas y contrato local; instrumentadas para SQLite, ciclo de vida, vistas y AndroidKeyStore. Compilar pruebas instrumentadas no equivale a ejecutarlas en un teléfono.

### integracion

Implementación: [src/tests/integracion.php](../src/tests/integracion.php).

```text
                 -------- integracion --------
                 |
                 |
                 |
condicion: B, descripcion: Text --> comprobar() -->
                 |
                 |
metodo: Text, ruta: Text, datos: Dato, token: Text, esperado: Z, tipo: Text --> pedir() -->
      [Dato] <--
                 |
                 |
consulta: Text, parametros: [Dato] --> sql() -->
      Filas <--
                 |
                 |
correo: Text --> tokenDelCorreo() -->
      Text <--
                 |
                 --------------------------------------
```

### logica-fake.test

Implementación: [src/tests/logica-fake.test.cjs](../src/tests/logica-fake.test.cjs).

```text
                 -------- logica-fake.test --------
                 |
                 |
                 |
responder: Dato --> cliente() -->
      Dato <--
                 |
                 --------------------------------------
```

### BasePrueba

Implementación: [src/tests/perfil-llavero.php](../src/tests/perfil-llavero.php).

```text
                 -------- BasePrueba --------
                 |
                 |
                 |
                 __construct() -->
      Dato <--
                 |
                 |
query: Text, options: [Dato] --> prepare() -->
      Filas | Nulo <--
                 |
                 --------------------------------------
```

### perfil-llavero

Implementación: [src/tests/perfil-llavero.php](../src/tests/perfil-llavero.php).

```text
                 -------- perfil-llavero --------
                 |
                 |
                 |
ok: B --> verificar() -->
                 |
                 |
operacion: Closure, estado: Z --> rechazo() -->
                 |
                 --------------------------------------
```

### validacion

Implementación: [src/tests/validacion.php](../src/tests/validacion.php).

```text
                 -------- validacion --------
                 |
                 |
                 |
condicion: B, descripcion: Text --> comprobar() -->
                 |
                 |
lectura: [Dato] --> esperarRechazo() -->
                 |
                 --------------------------------------
```

### Diccionario de tipos auxiliares

Dato = Text | Z | R | B | Nulo | [Dato]. Datos = [(clave:Text, valor:Dato)]. Los tipos de dominio detallados en el contrato conservan esa estructura.

- Closure: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- Filas: abstracción de estado o recurso de plataforma, encapsulado por el adaptador del componente.
- JVM: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.
- PHP: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.

## Aclaraciones del Diseño

- Se describe el código actual del Sprint 2; el ejemplo del Sprint 0 se usa para notación, no para copiar comportamientos obsoletos.
- N, Z, R, B y Text son naturales, enteros, reales, booleanos y texto. [T] es una colección; Nulo indica ausencia.
- Los tipos de plataforma citados en las fichas se consideran adaptadores opacos; no autorizan dependencias entre capas.
- Las operaciones auxiliares y callbacks completos se trazan en [el inventario](materiales/informes/revision-funciones.md).

## Reglas Generales

- Lenguaje objetivo: PHP, JavaScript y ejemplos JSON/HTTP.
- Cada función o método lleva diseño lógico y descripción en un bloque delimitado por líneas discontinuas.
- + representa interfaz pública y - implementación privada; --> muta o produce efectos, <-- consulta, --x operación estática.
- Código legible, responsabilidad única y auxiliares privados mínimos. No inventar getters ni nuevas operaciones públicas.
- Proteger el estado mutable por confinamiento, transacciones o sincronización explícita según el componente.
- Generar y ejecutar pruebas automatizadas de operaciones críticas, errores, límites, aislamiento y concurrencia cuando el entorno lo permita. No acceder a campos privados para afirmar resultados.
- No introducir secretos, autenticación maestra, respuestas ficticias de éxito, commits ni despliegues automáticos.
