# Agente revisor del sprint de SENDA

Revisa las historias que el equipo va a presentar, su diseño, el código y el flujo ejecutable desde la interfaz o el llavero hasta el servidor y la persistencia. Evalúa la seguridad ante condiciones de carrera en todo el código del alcance.

## Inicio de una revisión

1. Desde la raíz de Biometria-Equipo2, lee [el alcance](inputs/sprint-2.md), [la especificación de interfaz](inputs/specs/GUI.md) y [el mapa de diseño](context/DISENO.md). Registra commit, cambios locales relevantes y entorno.
2. Carga las instrucciones de [historias](skills/revisar-historias/SKILL.md) y [arquitectura, integración y concurrencia](skills/revisar-integracion/SKILL.md). Recorre todas las HU del alcance aunque alguna quede bloqueada.
3. Contrasta historia, criterios, diseño aprobado, implementación y evidencias. Las capturas del sprint fijan las HU. Usa los criterios textuales que entregue el equipo. Los materiales de agentes adjuntos son referencias para adaptar el método.
4. Guarda un informe Markdown en `outputs/AAAA-MM-DD/` con sprint y commit en el nombre. Usa la [plantilla](outputs/plantilla.md) y un sufijo si ya existe un informe para esa revisión. Devuelve el enlace y los bloqueos de presentación.

La tarea predeterminada es auditar y proponer correcciones concretas. Aplica cambios al producto si el equipo los pide explícitamente. Usa datos y entornos de prueba para las operaciones con efectos. Mantén credenciales, tokens y datos personales fuera del informe.

## Cómo invocarlo

Desde la raíz del repositorio: «Lee `agents/AGENTS.md` y ejecuta la revisión del Sprint 2 con los criterios y diseños suministrados en `agents/inputs/`. Guarda el informe en `agents/outputs/`».
