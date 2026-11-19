# Diseño de business_logic

## Diseño del Componente

Aplica las reglas de SENDA sobre las entidades del catálogo de datos: cuentas, roles, llaveros, lecturas, solicitudes y recuperación. Solo conoce datos de dominio, configuración y persistencia.

### Tipos del dominio y catálogo de datos

Credenciales=(correo:Text,contrasena:Text). Identidad=(id_usuario:N,rol:Text,id_parque:N|Nulo,id_organizacion:N|Nulo). Contexto es una prueba de identidad autenticada; contiene una credencial antes de resolverla y una identidad al entrar al negocio. Datos es una agregación de los campos de la operación, no una petición de transporte.
Medicion=(nombre_micro:Text,tipo:{O3,TEMPERATURA},valor:R,unidad:{ppb,C},fecha_hora:Text,latitud:R,longitud:R,simulada:B). Confirmacion=(guardada:B,duplicada:B). ErrorDominio=(codigo:Text,mensaje:Text,campos:[Text]). Los tipos del catálogo están definidos en database_design.md.

### Invariantes

- Cuentas activas y organización activa; credenciales derivadas con hash y sesiones de duración limitada.
- Vinculación serializada por usuario y llavero, mismo parque y organización, propietario único. Desvincular conserva dispositivo e histórico.
- guardarMedicion admite O3 entero de 0..65535 ppb o temperatura de -40..50 C con dos decimales; coordenadas obligatorias, rangos geográficos y fecha UTC válida.
- Identidad de lectura=(fecha_hora,id_llavero,tipo). Repetir los mismos datos confirma duplicada; cambiar su contenido es conflicto.
- Las consultas preparadas y las transacciones corresponden a las claves y relaciones de database_design.md. Recuperación y cambio de contraseña invalidan credenciales; los enlaces son de un solo uso.
- ErrorDominio contiene únicamente causa y datos de dominio. La traducción de esa causa al protocolo corresponde al componente communication.
- Cada instancia posee una conexión de persistencia por unidad de trabajo; no se comparte entre hilos. La integridad entre peticiones se protege con bloqueos y restricciones de la base.

### Paridad de operaciones de cliente

| Operación | Entrada lógica | Salida lógica |
|---|---|---|
| iniciarSesion | datos:Credenciales | Sesion |
| consultarSesion | contexto:Contexto | Sesion |
| cerrarSesion | contexto:Contexto | Confirmacion |
| ultimaMedicion | contexto:Contexto, parque:N | UltimaMedicion |
| registrarTrabajador | contexto:Contexto, datos:Datos | Trabajador |
| cambiarContrasena | contexto:Contexto, datos:Datos | Confirmacion |
| solicitarRecuperacion | datos:Datos | Confirmacion |
| restablecerContrasena | datos:Datos | Confirmacion |
| registrarSolicitud | datos:Datos | Confirmacion |

Contexto es la prueba opaca de una identidad; Sesion agrega identidad y caducidad; Datos es una agregación de campos del contrato; Confirmacion agrega el resultado de la operación. Los nombres y significados son compartidos con business_logic_design.md. La paridad es lógica, no identidad literal entre tipos PHP, JSON y JavaScript.

### ErrorDominio

Implementación: [src/business_logic/Apoyo.php](../src/business_logic/Apoyo.php).

```text
                 -------- ErrorDominio --------
                 |
                 |
                 |
codigo: Text, mensaje: Text, campos: [Dato] --> __construct() -->
      Dato <--
                 |
                 --------------------------------------
```

### Apoyo

Implementación: [src/business_logic/Apoyo.php](../src/business_logic/Apoyo.php).

```text
                 -------- Apoyo --------
                 |
                 |
                 |
                 configuracion() -->
      [Dato] <--
                 |
                 |
config: [Dato] --> conectar() -->
      Deposito <--
                 |
                 |
datos: [Dato], campos: [Dato] --> exigirCampos() -->
                 |
                 |
valor: Dato --> validarCorreo() -->
      Text <--
                 |
                 |
valor: Dato --> validarContrasena() -->
      Text <--
                 |
                 |
fecha: Text --> fechaJson() -->
      Text <--
                 |
                 |
datos: [Dato] --> validarMedicion() -->
      [Dato] <--
                 |
                 --------------------------------------
```

### Correo

Implementación: [src/business_logic/Correo.php](../src/business_logic/Correo.php).

```text
                 -------- Correo --------
                 |
                 |
                 |
db: Deposito, config: [Dato] --> entregarCorreos() -->
      Z <--
                 |
                 --------------------------------------
```

### LogicaNegocio

Implementación: [src/business_logic/LogicaNegocio.php](../src/business_logic/LogicaNegocio.php).

```text
                 -------- LogicaNegocio --------
                 | - deposito: Deposito
                 | - configuracion: Datos
                 |
                 |
                 |
db: Deposito, config: [Dato] --> __construct() -->
      Dato <--
                 | - consulta: operación auxiliar privada
                 | - usuarioPublico: operación auxiliar privada
                 |
                 |
operacion: Text, ip: Text, correo: Text --> limitar() -->
                 |
                 |
datos: Credenciales --> iniciarSesion() -->
      Sesion <--
                 |
                 |
token: Text --> autenticar() -->
      [Dato] <--
                 |
                 |
contexto: Contexto --> consultarSesion() -->
      Sesion <--
                 |
                 |
contexto: Contexto --> cerrarSesion() -->
      Confirmacion <--
                 | - exigirRol: operación auxiliar privada
                 |
                 |
usuario: [Dato], datos: [Dato] --> actualizarFotoPerfil() -->
      [Dato] <--
                 |
                 |
usuario: [Dato], datos: [Dato] --> vincularLlavero() -->
      [Dato] <--
                 |
                 |
usuario: [Dato] --> desvincularLlavero() -->
      [Dato] <--
                 |
                 |
usuario: [Dato], datos: [Dato] --> guardarMedicion() -->
      [Dato] <--
                 |
                 |
usuario: [Dato], cursor: Dato --> historialMediciones() -->
      [Dato] <--
                 |
                 |
contexto: Contexto, parque: N --> ultimaMedicion() -->
      UltimaMedicion <--
                 | - prepararEnlace: operación auxiliar privada
                 |
                 |
contexto: Contexto, datos: Datos --> registrarTrabajador() -->
      Trabajador <--
                 |
                 |
contexto: Contexto, datos: Datos --> cambiarContrasena() -->
      Confirmacion <--
                 |
                 |
datos: Datos --> solicitarRecuperacion() -->
      Confirmacion <--
                 |
                 |
datos: Datos --> registrarSolicitud() -->
      Confirmacion <--
                 |
                 |
datos: Datos --> restablecerContrasena() -->
      Confirmacion <--
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

- Lenguaje objetivo: PHP 8.2 o superior con PDO.
- Cada función o método lleva diseño lógico y descripción en un bloque delimitado por líneas discontinuas.
- + representa interfaz pública y - implementación privada; --> muta o produce efectos, <-- consulta, --x operación estática.
- Código legible, responsabilidad única y auxiliares privados mínimos. No inventar getters ni nuevas operaciones públicas.
- Proteger el estado mutable por confinamiento, transacciones o sincronización explícita según el componente.
- Generar y ejecutar pruebas automatizadas de operaciones críticas, errores, límites, aislamiento y concurrencia cuando el entorno lo permita. No acceder a campos privados para afirmar resultados.
- No introducir secretos, autenticación maestra, respuestas ficticias de éxito, commits ni despliegues automáticos.
