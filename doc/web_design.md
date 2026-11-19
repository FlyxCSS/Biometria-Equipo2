# Diseño de web

## Diseño del Componente

Interfaz pública OZORA y panel SENDA. Las pantallas invocan exclusivamente el proxy de negocio, representan datos y validan formularios; no implementan reglas de autorización ni acceso a la base.

### Pantallas y contrato visual

Portada pública OZORA con información del proyecto y formulario de contacto. Acceso por correo/contraseña; recuperación por enlace de un solo uso y cambio de contraseña inicial. Panel de responsable con última lectura O3 del parque y alta de trabajadores. La temperatura se persiste pero el indicador existente de última lectura mantiene O3 y ppb.

Los controles invocan LogicaFake; no realizan fetch directo. La sesión web reside en memoria y recargar exige volver a entrar. Formularios presentan errores públicos sin revelar existencia de cuentas en recuperación. El controlador index.php sirve únicamente recursos permitidos desde la copia privada.

### app

Implementación: [src/web/js/app.js](../src/web/js/app.js).

```text
                 -------- app --------
                 |
                 |
                 |
id: Dato --> elemento() -->
      Dato <--
                 |
                 |
texto: Text, error: Dato, destino: Dato --> mensaje() -->
      Dato <--
                 |
                 |
seccion: Dato --> mostrar() -->
      Dato <--
                 |
                 |
contenedor: Dato --> ocultarContrasenas() -->
      Dato <--
                 |
                 |
operacion: Dato --> conSesion() -->
      Dato <--
                 |
                 |
evento: Dato, operacion: Dato --> ejecutarFormulario() -->
      Dato <--
                 |
                 |
texto: Text, clase: Dato --> parrafo() -->
      Dato <--
                 |
                 |
                 actualizarLectura() -->
      Dato <--
                 |
                 |
evento: Dato --> iniciarSesion() -->
      Dato <--
                 |
                 |
evento: Dato --> registrarTrabajador() -->
      Dato <--
                 |
                 |
evento: Dato --> solicitarRecuperacion() -->
      Dato <--
                 |
                 |
evento: Dato --> restablecerContrasena() -->
      Dato <--
                 |
                 |
evento: Dato --> cambiarContrasenaInicial() -->
      Dato <--
                 |
                 |
                 cerrarSesion() -->
      Dato <--
                 |
                 |
                 abrirRecuperacion() -->
      Dato <--
                 |
                 |
                 volverAcceso() -->
      Dato <--
                 |
                 |
evento: Dato --> alternarContrasena() -->
      Dato <--
                 |
                 |
                 prepararVisibilidadContrasenas() -->
      Dato <--
                 |
                 |
                 prepararAvisosFormularios() -->
      Dato <--
                 |
                 --------------------------------------
```

### publica

Implementación: [src/web/js/publica.js](../src/web/js/publica.js).

```text
                 -------- publica --------
                 |
                 |
                 |
evento: Dato --> solicitarInformacion() -->
      Dato <--
                 |
                 --------------------------------------
```

### Diccionario de tipos auxiliares

Dato = Text | Z | R | B | Nulo | [Dato]. Datos = [(clave:Text, valor:Dato)]. Los tipos de dominio detallados en el contrato conservan esa estructura.

- HTML5: abstracción de la entidad o resultado homónimo; su estado y operaciones se detallan en la ficha correspondiente.

## Aclaraciones del Diseño

- Se describe el código actual del Sprint 2; el ejemplo del Sprint 0 se usa para notación, no para copiar comportamientos obsoletos.
- N, Z, R, B y Text son naturales, enteros, reales, booleanos y texto. [T] es una colección; Nulo indica ausencia.
- Los tipos de plataforma citados en las fichas se consideran adaptadores opacos; no autorizan dependencias entre capas.
- Las operaciones auxiliares y callbacks completos se trazan en [el inventario](materiales/informes/revision-funciones.md).

## Reglas Generales

- Lenguaje objetivo: HTML5, CSS, JavaScript y PHP.
- Cada función o método lleva diseño lógico y descripción en un bloque delimitado por líneas discontinuas.
- + representa interfaz pública y - implementación privada; --> muta o produce efectos, <-- consulta, --x operación estática.
- Código legible, responsabilidad única y auxiliares privados mínimos. No inventar getters ni nuevas operaciones públicas.
- Proteger el estado mutable por confinamiento, transacciones o sincronización explícita según el componente.
- Generar y ejecutar pruebas automatizadas de operaciones críticas, errores, límites, aislamiento y concurrencia cuando el entorno lo permita. No acceder a campos privados para afirmar resultados.
- No introducir secretos, autenticación maestra, respuestas ficticias de éxito, commits ni despliegues automáticos.
