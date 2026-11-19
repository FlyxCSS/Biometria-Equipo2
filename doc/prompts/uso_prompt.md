# Cómo usar los prompts SDD de SENDA

El diseño versionado es la fuente de verdad; los prompts lo convierten en una tarea verificable. Se busca equivalencia funcional con SENDA, no reproducir byte a byte un resultado no determinista de IA.

1. Abre un único chat de implementación con acceso al repositorio. Entrega los diseños, las referencias del profesor, el contrato de la API y los recursos visuales que ya contiene el proyecto. No entregues local.php, contraseñas, sesiones ni datos personales.
2. Lee y aprueba los diseños antes de generar código. Las tres fases /specify, /plan y /tasks deben dejar criterios y decisiones visibles. No son comandos mágicos: son apartados de trabajo del prompt.
3. Envía un prompt completo cada vez en este orden: database → config → business_logic → communication → frontend_business_logic → web → microprocesador → android → tools → tests. Cada prompt define un solo componente y consulta los contratos de los anteriores. Android y firmware comparten el protocolo BLE; contrastarlos antes de dar por cerrada su integración.
4. Revisa el plan. Una vez aceptado, pide completar /implement con código, pruebas y documentación. No aceptar funciones públicas nuevas o cambios de esquema sin volver a la especificación.
5. Ejecuta la validación indicada. Si falta hardware o un servicio de pruebas, conservar el caso como pendiente y continuar con las comprobaciones independientes. Un APK compilado no prueba BLE, biometría o GPS.
6. Para una modificación, actualiza primero el diseño afectado y sus criterios. Reenvía su prompt indicando el cambio de alcance; luego ejecuta las pruebas de los consumidores del contrato.
7. Revisa el diff y realiza manualmente los commits del equipo. Estos prompts no autorizan publicaciones, cambios de producción, flasheo ni instalación en teléfonos.

## Estructura esperada

Un xxx_prompt.md por cada carpeta de primer nivel de src y un xxx_design.md del mismo nombre en doc. agents permanece reservado para los agentes futuros. La documentación anterior y las fuentes docentes se conservan en doc/materiales, separadas de la especificación vigente.

## Decisiones propias del Sprint 2

Se usa AUTHORS.md por indicación del equipo aunque el agente docente mencione author.md. Los datos ficticios solo se seleccionan explícitamente en el firmware. No hay un acceso demo en producción. La simulación de datos del sensor no equivale a una simulación de las respuestas del servidor.
