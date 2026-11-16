---
name: revisar-integracion
description: Audita diseño por capas, lógica fake, comunicación REST, persistencia y condiciones de carrera de SENDA.
---

# Arquitectura, comunicación y concurrencia

1. Sigue el flujo GUI → lógica cliente → transporte → lógica de negocio → datos. Comprueba que el dominio PHP desconoce HTTP y que la GUI consume el proxy. Contrasta la firma lógica de las operaciones del proxy con sus contrapartes de dominio. Verifica método, ruta, carga, autenticación, respuestas, timeout, errores y reintento contra el contrato REST.
2. Verifica el papel de LogicaFake.js y sus consumidores reales. En la referencia arquitectónica, Fake designa un proxy de la GUI que puede comunicarse realmente por debajo. Para dobles, fixtures, datos ilustrativos o firmware con lectura ficticia, identifica activación, origen, persistencia, aislamiento de producción y evidencia que aportan. Un valor de sensor simulado puede probar el flujo real de transporte si el resto del recorrido se ejecuta contra el servidor de pruebas.
3. Haz inventario del estado mutable compartido, hilos, callbacks y eventos asíncronos, incluso en JavaScript. Identifica dueño, lectores y escritores. Evalúa la atomicidad de comprobar y actuar, visibilidad de cambios, orden de eventos y gestión del ciclo de vida. Revisa también cancelación, reintentos, bloqueos y accesos a entidades compartidas entre peticiones o procesos.
4. Comprueba las invariantes aplicables con pruebas que fuercen el orden o solapamiento (barreras, respuestas demoradas, conexiones independientes o repositorios controlados). Para cada riesgo, registra intercalado, resultado esperado y observado:
   - Android y firmware: BLE/GPS frente a finalizar o cambiar de recorrido, sincronizaciones simultáneas, cambio de usuario durante una respuesta anterior, uso del hilo principal, datos compartidos con callbacks del llavero.
   - Web: doble clic y peticiones fuera de orden al cambiar de pantalla o cerrar sesión.
   - REST y BD: altas concurrentes con el mismo correo, consumo simultáneo del token de recuperación, cambio de asignación durante el ingreso de una medida y reenvío si el servidor confirma en BD pero el cliente pierde la respuesta. Contrasta transacciones, restricciones e idempotencia con el contrato y con el estado final.
5. Lanza las pruebas existentes que correspondan, usando las instrucciones del README y de la guía del backend. Combina pruebas de dominio, integración HTTP y evidencia en Android/Web. Para medir concurrencia de servidor usa un entorno de pruebas capaz de tramitar peticiones solapadas. Si la revisión es estática o falta una prueba concurrente relevante, anota el límite y la prueba concreta pendiente. Un caso secuencial por sí solo deja pendiente la evidencia de concurrencia.
