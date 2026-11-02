# Biometria-Equipo2

# SENDA — Plataforma de Monitorización Ambiental

**Proyecto de Biometría y Medio Ambiente | Grado en Tecnologías Interactivas — UPV**

Senda es una plataforma tecnológica diseñada para facilitar la monitorización ambiental de parques naturales y espacios forestales mediante sensores portátiles, una aplicación Android y una plataforma web.

Su objetivo es aprovechar los recorridos habituales de los trabajadores forestales para recoger mediciones ambientales geolocalizadas y ofrecer a las administraciones públicas información que facilite la supervisión y gestión de sus espacios naturales.

**Eslogan:** *Conoce el aire. Protege el entorno.*

---

## 1. Descripción del proyecto

Senda propone un sistema de monitorización ambiental formado por cuatro componentes principales:

1. **Sensor portátil BLE:** mide la concentración de ozono (O₃) y transmite los valores mediante Bluetooth Low Energy.
2. **Aplicación Android:** permite al trabajador identificarse, vincular su sensor mediante un código QR, recibir las mediciones y asociarlas a su ubicación GPS.
3. **Servidor REST:** recibe, valida y almacena los datos enviados desde Android.
4. **Plataforma web:** permite gestionar organizaciones, parques, trabajadores y sensores, además de consultar mediciones, estadísticas y mapas de calor.

El sistema está pensado para que diferentes organizaciones puedan utilizar una misma plataforma manteniendo sus datos separados y protegidos.

## 2. Objetivos

### Objetivo principal

Desarrollar una plataforma que facilite la recogida y visualización de datos ambientales en parques naturales mediante dispositivos portátiles y tecnologías inalámbricas.

### Objetivos específicos

- Monitorizar la concentración de ozono en diferentes zonas.
- Aprovechar los recorridos del personal forestal para recoger mediciones.
- Geolocalizar las mediciones mediante GPS.
- Centralizar los datos en una base de datos.
- Visualizar la información mediante mapas de calor orientativos.
- Gestionar diferentes organizaciones y parques naturales.
- Controlar la asignación de sensores y trabajadores.
- Facilitar la consulta de mediciones históricas.

## 3. Producto Mínimo Viable (PMV)

El PMV de Senda estará centrado en la **monitorización de ozono (O₃)**.

Las funcionalidades previstas incluyen:

- Registro de organizaciones clientes.
- Gestión de parques naturales.
- Creación de cuentas con diferentes roles.
- Registro y asignación de sensores BLE.
- Inicio de sesión en web y Android.
- Vinculación del sensor mediante código QR.
- Recepción de mediciones mediante Bluetooth.
- Obtención de coordenadas GPS.
- Envío de mediciones al servidor.
- Consulta del historial de mediciones.
- Visualización de mapas de calor.

En versiones futuras se contempla ampliar la monitorización a otros parámetros ambientales, como CO₂ y temperatura.

Los mapas de calor serán orientativos y dependerán de la calidad, calibración y cobertura espacial de las mediciones.

## 4. Arquitectura del sistema

El funcionamiento previsto sigue este flujo:

**Sensor BLE → Aplicación Android → API REST → Base de datos → Plataforma web**

### Sensor

Dispositivo portátil encargado de obtener y transmitir las mediciones de ozono mediante BLE.

### Aplicación Android

Utilizada por los trabajadores forestales para vincular el sensor, recibir las mediciones y enviarlas junto con la ubicación GPS.

### Servidor y base de datos

La API REST será responsable de validar las peticiones, comprobar permisos y almacenar las mediciones.

La base de datos se organiza inicialmente en cinco entidades principales:

| Entidad | Función |
|---|---|
| Organizaciones | Almacenar los clientes institucionales |
| Parques | Gestionar los espacios naturales |
| Usuarios | Registrar responsables y trabajadores |
| Sensores | Controlar los dispositivos y sus asignaciones |
| Mediciones | Almacenar los valores ambientales y su ubicación |

### Plataforma web

Permitirá a los responsables consultar los datos ambientales y gestionar los recursos correspondientes a su organización o parque.

