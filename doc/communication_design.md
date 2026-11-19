# Diseño de communication

## Diseño del Componente

Adapta el contrato REST a las operaciones del negocio. Verifica ruta, método, tamaño, contenido, autenticación y formato; traduce los errores de dominio a estados de respuesta.

### Operaciones externas

La API cuelga de /api/v1 (prefijo de instalación opcional /senda). POST /sesiones; GET/DELETE /sesiones/actual; POST /usuarios/actual/contrasena; POST /usuarios/actual/foto; POST/DELETE /llaveros/actual; POST /mediciones; GET /usuarios/actual/mediciones; GET /parques/{parque}/mediciones/ultima; POST /trabajadores; POST /recuperaciones; POST /recuperaciones/confirmacion; POST /solicitudes.

Solo aquí se adaptan estados 400/401/403/404/405/409/413/415/422/429/503. ContextoWeb contiene el mapa de errores y la resolución de rutas. ServidorRest comprueba JSON de hasta 16384 bytes, o 150000 para fotografía. La credencial opaca se resuelve a la identidad autorizada antes de invocar negocio. La respuesta pública preserva ok/data o ok/error; no devuelve mensajes PDO ni secretos.

### ContextoWeb

Implementación: [src/communication/ContextoWeb.php](../src/communication/ContextoWeb.php).

```text
                 -------- ContextoWeb --------
                 |
                 |
                 |
                 rutaBaseAplicacion() -->
      Text <--
                 |
                 |
                 rutaPeticionAplicacion() -->
      Text <--
                 |
                 |
error: ErrorDominio --> estadoErrorDominio() -->
      Z <--
                 |
                 --------------------------------------
```

### ErrorApi

Implementación: [src/communication/ContextoWeb.php](../src/communication/ContextoWeb.php).

```text
                 -------- ErrorApi --------
                 |
                 |
                 |
estado: Z, codigo: Text, mensaje: Text, campos: [Dato] --> __construct() -->
      Dato <--
                 |
                 --------------------------------------
```

### ServidorRest

Implementación: [src/communication/ServidorRest.php](../src/communication/ServidorRest.php).

```text
                 -------- ServidorRest --------
                 |
                 |
                 |
estado: Z, contenido: [Dato] --> responderJson() -->
                 |
                 |
maximo: Z --> leerJson() -->
      [Dato] <--
                 |
                 |
                 atenderApi() -->
                 |
                 --------------------------------------
```

### Diccionario de tipos auxiliares

Dato = Text | Z | R | B | Nulo | [Dato]. Datos = [(clave:Text, valor:Dato)]. Los tipos de dominio detallados en el contrato conservan esa estructura.

- PHP: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.

## Aclaraciones del Diseño

- Se describe el código actual del Sprint 2; el ejemplo del Sprint 0 se usa para notación, no para copiar comportamientos obsoletos.
- N, Z, R, B y Text son naturales, enteros, reales, booleanos y texto. [T] es una colección; Nulo indica ausencia.
- Los tipos de plataforma citados en las fichas se consideran adaptadores opacos; no autorizan dependencias entre capas.
- Las operaciones auxiliares y callbacks completos se trazan en [el inventario](materiales/informes/revision-funciones.md).

## Reglas Generales

- Lenguaje objetivo: PHP 8.2 o superior.
- Cada función o método lleva diseño lógico y descripción en un bloque delimitado por líneas discontinuas.
- + representa interfaz pública y - implementación privada; --> muta o produce efectos, <-- consulta, --x operación estática.
- Código legible, responsabilidad única y auxiliares privados mínimos. No inventar getters ni nuevas operaciones públicas.
- Proteger el estado mutable por confinamiento, transacciones o sincronización explícita según el componente.
- Generar y ejecutar pruebas automatizadas de operaciones críticas, errores, límites, aislamiento y concurrencia cuando el entorno lo permita. No acceder a campos privados para afirmar resultados.
- No introducir secretos, autenticación maestra, respuestas ficticias de éxito, commits ni despliegues automáticos.
