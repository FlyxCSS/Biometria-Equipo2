---
name: revisar-historias
description: Revisa la completitud de las historias del sprint SENDA y la evidencia de cada criterio, incluida la interfaz acordada.
---

# Revisión de historias

- Transcribe cada criterio desde las fichas que entregue el equipo. Comprueba que sea verificable, con actor, condiciones y resultado observable. Registra ambigüedades como preguntas para el equipo.
- Vincula cada criterio a diseño, ruta de uso real, implementación y comprobación con resultado esperado y observado. Señala pruebas históricas dando su fecha y versión. Un contador de tarjeta indica ítems visibles, y una casilla marcada indica estado documental.
- Recorre los casos de éxito y los errores, permisos, pérdida de conexión y estados de interfaz que correspondan a cada criterio. Identifica los componentes implicados y la prueba de extremo a extremo que falta cuando solo se ha comprobado una capa.
- Usa ✔ Cumple con evidencia suficiente, ✖ Incumple con evidencia del fallo y ◯ No verificable si falta un criterio, un diseño, un entorno o una prueba. Cierra una HU como completa cuando se hayan confirmado todos sus criterios. Registra por separado si existe aceptación explícita del equipo o Product Owner.
- Para Hi‑Fi, cuando el equipo facilite un enlace accesible, usa Figma MCP en lectura (`get_metadata` y `get_screenshot`) para recuperar el nodo y compararlo con la interfaz ejecutada. Usa los archivos exportados como alternativa y documenta el estado si faltan ambos. Para comprobar navegación y estados web usa el navegador disponible y una instancia de prueba. Las historias y sus criterios se leen de los archivos de entrada locales.
