# Diseño de frontend_business_logic

## Diseño del Componente

LogicaFake encapsula las invocaciones del negocio usadas por la GUI web. Cada función conserva el nombre y la proyección lógica de su operación de negocio; transporte y serialización quedan privados.

### Interfaz compartida con negocio

Las operaciones de esta tabla se interpretan en ambos componentes con los mismos tipos lógicos; la implementación cliente recibe la credencial textual de Contexto y el servidor resuelve su Identidad. Esta resolución no autoriza a cambiar el nombre ni el contenido de los datos.

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

Contexto es la prueba opaca de una identidad; Sesion agrega identidad y caducidad; Datos es una agregación de campos del contrato; Confirmacion agrega el resultado de la operación. Los nombres y significados son compartidos con business_logic_design.md. La paridad es lógica, no identidad literal entre tipos PHP, JSON y JavaScript. La GUI no conoce rutas ni añade cabeceras.

### LogicaFake

Implementación: [src/frontend_business_logic/LogicaFake.js](../src/frontend_business_logic/LogicaFake.js).

```text
                 -------- LogicaFake --------
                 | - base: Text
                 |
                 |
                 |
rutaBase: Dato --> constructor() -->
      Dato <--
                 |
                 |
datos: Credenciales --> iniciarSesion() -->
      Sesion <--
                 |
                 |
contexto: Contexto --> consultarSesion() -->
      Sesion <--
                 |
                 |
contexto: Contexto --> cerrarSesion() -->
      Confirmacion <--
                 |
                 |
contexto: Contexto, parque: N --> ultimaMedicion() -->
      UltimaMedicion <--
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
datos: Datos --> restablecerContrasena() -->
      Confirmacion <--
                 |
                 |
datos: Datos --> registrarSolicitud() -->
      Confirmacion <--
                 |
                 |
                 | - pedir: transporte auxiliar privado
                 |
                 --------------------------------------
```

### Diccionario de tipos auxiliares

Dato = Text | Z | R | B | Nulo | [Dato]. Datos = [(clave:Text, valor:Dato)]. Los tipos de dominio detallados en el contrato conservan esa estructura.

- JavaScript: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.

## Aclaraciones del Diseño

- Se describe el código actual del Sprint 2; el ejemplo del Sprint 0 se usa para notación, no para copiar comportamientos obsoletos.
- N, Z, R, B y Text son naturales, enteros, reales, booleanos y texto. [T] es una colección; Nulo indica ausencia.
- Los tipos de plataforma citados en las fichas se consideran adaptadores opacos; no autorizan dependencias entre capas.
- Las operaciones auxiliares y callbacks completos se trazan en [el inventario](materiales/informes/revision-funciones.md).

## Reglas Generales

- Lenguaje objetivo: JavaScript ES2022.
- Cada función o método lleva diseño lógico y descripción en un bloque delimitado por líneas discontinuas.
- + representa interfaz pública y - implementación privada; --> muta o produce efectos, <-- consulta, --x operación estática.
- Código legible, responsabilidad única y auxiliares privados mínimos. No inventar getters ni nuevas operaciones públicas.
- Proteger el estado mutable por confinamiento, transacciones o sincronización explícita según el componente.
- Generar y ejecutar pruebas automatizadas de operaciones críticas, errores, límites, aislamiento y concurrencia cuando el entorno lo permita. No acceder a campos privados para afirmar resultados.
- No introducir secretos, autenticación maestra, respuestas ficticias de éxito, commits ni despliegues automáticos.
