# Android · Biometría, QR y recuperación

Referencias: las cuatro pantallas de PBIO PROYECTO (7).zip.

- Activación biométrica: pantalla con fondo ilustrado, huella y consentimiento
  explícito. Disponible desde Ajustes y tras un inicio con contraseña. Ahora no
  conserva el acceso con contraseña. Si Android no tiene biometría configurada,
  se explica y se ofrece abrir sus ajustes; nunca se simula una activación.
- Vincular llavero: pantalla propia con marco de escaneo y entrada manual. La
  cámara real se abre en Google Code Scanner, cuya interfaz pertenece a Google.
  No se presenta la fotografía de bosque de la referencia como cámara en vivo.
  Se conservan el modo solo GPS y la comprobación de los códigos S-001.
- Recuperación: solicitud por correo, formulario del enlace recibido y nueva
  contraseña, y confirmación de éxito. Las peticiones conservan su estado al
  girar la pantalla. El token y las contraseñas no se guardan en el estado de la
  actividad ni se registran; la ventana evita capturas de información sensible.
- El backend usa tokens hexadecimales de 64 caracteres en enlaces HTTPS
  /acceso#token=..., con 30 minutos de vigencia y un solo uso. Se mantiene ese
  contrato en lugar de reducirlo a las cuatro cifras del diseño. La app permite
  pegar el enlace o el token completo. Solo confirma el cambio cuando responde
  POST /recuperaciones/confirmacion. La solicitud inicial conserva el mensaje
  genérico para no revelar si existe una cuenta.
- Los errores de conexión y validación permiten reintentar, sin mostrar un
  resultado exitoso ficticio. La vigencia y el uso del enlace los valida el servidor.

## Comprobaciones

Pruebas JVM: RecoveryTokenTest y la batería existente.
Pruebas de interfaz: validación de correo, recuperación y enlace, conservación
del paso al recrear la pantalla, biometría sin configurar y selección manual QR.
Se ejecutan solo en el emulador, con datos locales de prueba; no se envían
correos reales ni se cambian contraseñas de producción.

Resultado del 18/11/2026: APK Debug compilado, 25 pruebas JVM y 4 pruebas de interfaz superadas en el emulador Android 16. Capturas revisadas de biometría, vinculación y recuperación.
