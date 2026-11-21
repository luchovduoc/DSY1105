# DSY1105 — Gestión Móvil de Oportunidades y Activaciones Corporativas

## Descripción del proyecto

Aplicación móvil Android desarrollada para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)**.

El proyecto aborda el caso de una agencia de marketing directo que necesita centralizar y gestionar oportunidades de participación en eventos corporativos, ferias empresariales y activaciones comerciales.

La aplicación utiliza una arquitectura **MVVM**, persistencia local mediante **Room** y recursos nativos del dispositivo, manteniendo separadas la interfaz, la lógica de negocio y el acceso a datos.

## Integrantes

- Luis Veas
- María Pía Sepúlveda

## Tecnologías utilizadas

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- Arquitectura MVVM
- StateFlow
- Room
- SQLite
- CameraX / recursos de cámara
- ML Kit para lectura de códigos QR
- ZXing para generación de códigos QR
- Coil para carga de imágenes
- Recursos de ubicación de Android
- Git
- GitHub
- Trello

## Funcionalidades implementadas

### Autenticación

- Registro de usuarios.
- Inicio de sesión utilizando usuarios almacenados en Room.
- Validación individual de campos.
- Validación de formato de correo electrónico.
- Verificación de credenciales.
- Mensajes visuales de error.
- Cierre de sesión.

### Módulo de Oportunidades

- Listado de oportunidades almacenadas localmente.
- Registro de nuevas oportunidades.
- Edición de oportunidades existentes.
- Consulta del detalle de una oportunidad.
- Selección de prioridad.
- Selección de potencial comercial.
- Actualización del estado de gestión.
- Registro de observaciones.
- Validaciones por campo desde el ViewModel.
- Persistencia local mediante Room.

### Módulo de Organizaciones

- Registro de organizaciones.
- Listado de organizaciones almacenadas localmente.
- Registro de nombre, tipo de organización y rubro.
- Selección de región.
- Selección de comuna para la Región Metropolitana.
- Registro de observaciones.
- Validaciones visuales por campo.
- Lógica de validación centralizada en el ViewModel.
- Persistencia local mediante Room.

### Perfiles y permisos

La aplicación tiene tres perfiles, tomados de la lámina del profesor: **Ejecutivo Comercial**, **Supervisor Comercial** y
**Gestión de Activaciones** (el perfil Marketing del diseño anterior quedó en pausa).

- Toda cuenta nueva se crea como Ejecutivo. Solo un supervisor cambia perfiles y activa o desactiva cuentas.
- Cada perfil ve y puede hacer cosas distintas sobre las mismas pantallas; los permisos se validan en la
  pantalla y también en los repositorios.
- Detalle de permisos en [CONVENCIONES.md](CONVENCIONES.md).

### Activaciones, resultados y catálogo de eventos

- **Activaciones:** las crea Gestión de Activaciones sobre oportunidades confirmadas y asigna el responsable.
- **Resultados:** Gestión de Activaciones registra el resultado de cada activación; el supervisor los consulta.
- **Catálogo de eventos:** el ejecutivo importa un evento de la API como oportunidad Pendiente.

### Módulo de Eventos

- Registro de eventos con tipo, fechas (con calendario), horario, comuna, región, organización responsable,
  público esperado y condiciones de participación.
- Una oportunidad se asocia a un evento y, por el evento, a su organización.

### Módulo de Contactos

- Contactos ficticios asociados a una organización, con cargo, área, medio de contacto preferente y estado.
- La fecha del último contacto se actualiza con los seguimientos.

### Seguimiento e historial

- Registro de acciones sobre una oportunidad (tipo de acción, resultado, próxima acción y fecha).
- Historial de gestiones en el detalle de la oportunidad, con registros automáticos de la creación y de
  cada cambio de estado.

### Alertas

- Avisos internos: seguimientos atrasados y próximos, eventos próximos, propuestas sin respuesta,
  oportunidades sin contacto, información incompleta y oportunidades sin responsable.

### Búsqueda y filtros

- Oportunidades: texto, estado, tipo, prioridad, región y responsable.
- Eventos: texto, tipo, región y fecha. Contactos: texto, área y estado. Organizaciones: texto.

## Recursos nativos del dispositivo

### Cámara

La aplicación permite capturar una fotografía como evidencia asociada a una oportunidad.

El flujo incluye:

- Solicitud del permiso de cámara en tiempo de ejecución.
- Captura de fotografía.
- Asociación de la imagen con la oportunidad.
- Visualización de la fotografía almacenada.

### Ubicación

La aplicación permite registrar de forma puntual la ubicación asociada a una oportunidad.

El flujo incluye:

- Solicitud de permisos de ubicación en tiempo de ejecución.
- Obtención de latitud y longitud.
- Uso de ubicación de red o GPS según disponibilidad.
- Persistencia de las coordenadas en Room.
- Recuperación de la ubicación al volver a editar una oportunidad.

La aplicación no realiza seguimiento permanente del dispositivo.

## Códigos QR

- Lectura de códigos QR utilizando la cámara.
- Uso del contenido de un QR como fuente de información de una oportunidad.
- Generación de código QR asociado a oportunidades.

## Animaciones

La aplicación incorpora animaciones funcionales para mejorar la retroalimentación visual, entre ellas:

- Animación de interacción en el botón flotante de creación de oportunidades.
- Aparición y desaparición animada de fotografías mediante `AnimatedVisibility`.

## Persistencia local

La aplicación utiliza **Room** para almacenar los datos localmente.

Actualmente se almacenan, entre otros:

- Usuarios.
- Oportunidades.
- Estado de gestión.
- Prioridad y potencial comercial.
- Fotografías asociadas.
- Coordenadas de ubicación.
- Organizaciones.

- Activaciones y resultados de las activaciones.

La base de datos se rehízo desde cero (versión 1, sin migraciones) mientras la aplicación no se publique. Si cambia el
esquema, hay que borrar los datos de la app en el emulador.

## Arquitectura

El proyecto utiliza el patrón **MVVM**, separando las responsabilidades de la siguiente manera:

```text
ui/screens/     → Pantallas desarrolladas con Jetpack Compose
viewmodel/      → Estado y lógica de negocio
repository/     → Comunicación entre ViewModel y fuentes de datos
database/       → Entidades Room, DAO y base de datos
model/          → Modelos y estados de interfaz
navigation/     → Navegación entre pantallas
utils/          → Funciones auxiliares
```

Los formularios utilizan `StateFlow` para mantener un estado reactivo y las validaciones principales se realizan desde los ViewModel, evitando acoplar la lógica de negocio a los Composable.

## Navegación

La aplicación cuenta con navegación entre sus principales módulos, incluyendo:

- Inicio de sesión.
- Registro.
- Menú principal.
- Oportunidades.
- Formulario de oportunidades.
- Detalle de oportunidades.
- Organizaciones.
- Formulario de organizaciones.
- Lector QR.

Se utilizan componentes de navegación de Jetpack Compose y controles para regresar entre pantallas.

## Trabajo colaborativo

El desarrollo se gestiona mediante:

- Git para control de versiones.
- Repositorio compartido en GitHub.
- Ramas de trabajo por integrante.
- Commits técnicos asociados a funcionalidades concretas.
- Trello para planificación y distribución de tareas.

### Tablero Trello

[Ver tablero de planificación en Trello](https://trello.com/b/NSmDSIpI/desarrollo-de-aplicaciones-moviles003v)

## Pasos para ejecutar

1. Clonar el repositorio:

```bash
git clone https://github.com/luchovduoc/DSY1105.git
```

2. Abrir el proyecto con Android Studio.

3. Esperar que Gradle sincronice las dependencias.

4. Ejecutar la aplicación en un emulador compatible o en un dispositivo Android físico.

5. Crear una cuenta desde la pantalla de registro.

6. Iniciar sesión con las credenciales registradas.

7. Utilizar los módulos disponibles desde el menú principal.

## Recomendaciones para las pruebas

Para comprobar correctamente los recursos nativos se recomienda utilizar un dispositivo Android físico.

Para probar la cámara:

- Otorgar el permiso de cámara cuando la aplicación lo solicite.

Para probar la ubicación:

- Activar la ubicación del dispositivo.
- Otorgar los permisos solicitados.
- Mantener disponible Wi-Fi, datos móviles o GPS.

## Consideraciones

- Todos los datos utilizados durante las pruebas deben ser ficticios.
- La aplicación utiliza persistencia local mediante Room.
- No se requiere una base de datos remota para usar la app. El catálogo de eventos usa una API REST (Retrofit) del
  backend en `backend/`; si el servidor está apagado, la pantalla muestra un aviso y el resto de la app funciona igual.
- La ubicación se obtiene únicamente cuando el usuario solicita registrarla.
- No se realiza seguimiento permanente de la ubicación del usuario.
