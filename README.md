DSY1105 — Gestión Móvil de Oportunidades y Activaciones Corporativas
Descripción del proyecto

Aplicación móvil Android desarrollada para la asignatura Desarrollo de Aplicaciones Móviles (DSY1105) de Duoc UC. Resuelve el caso de una agencia de marketing directo que necesita centralizar y gestionar oportunidades de participación en eventos corporativos, ferias empresariales y activaciones comerciales.

Integrantes
Luis Veas
Maria Pía Sepulveda
Tecnologías utilizadas
Kotlin
Android Studio Quail 4
Jetpack Compose
Material Design 3
Arquitectura MVVM
Room (persistencia local)
CameraX + ML Kit (recursos nativos)
ZXing (generación de QR)
Coil (carga de imágenes)
Git / GitHub
Funcionalidades implementadas

Autenticación

Registro de usuario con validaciones
Login verificado contra base de datos local Room
Cierre de sesión

Módulo de Oportunidades

Listado de oportunidades almacenadas localmente
Crear nueva oportunidad con validaciones por campo
Editar oportunidad existente
Ver detalle de oportunidad
Persistencia real con Room — los datos sobreviven al reinicio

Recursos nativos

Cámara para captura de foto de evidencia asociada a la oportunidad
Lector de QR con CameraX y ML Kit para identificar fuente de información
Generación automática de código QR por oportunidad para acceso directo

Navegación

Menú lateral con acceso a todos los módulos
Navegación fluida entre pantallas con botón de regreso
Arquitectura

El proyecto sigue la arquitectura MVVM con separación clara de responsabilidades:

ui/screens/     → pantallas Composables
viewmodel/      → lógica de negocio y estado
repository/     → intermediario entre ViewModel y datos
database/       → entidades Room, DAOs y base de datos
model/          → data classes del dominio
navigation/     → rutas de navegación
utils/          → helpers de cámara y QR
Pasos para ejecutar
Clonar el repositorio
git clone https://github.com/luchovduoc/DSY1105.git
Abrir con Android Studio Quail 4
Esperar que Gradle sincronice las dependencias
Ejecutar en emulador API 33-35 con Google APIs o dispositivo Android físico
Crear una cuenta desde la pantalla de registro
Iniciar sesión con las credenciales registradas
Notas
Todos los datos son ficticios y de prueba
La base de datos es local — no requiere conexión a internet
Para probar la cámara se recomienda dispositivo físico o emulador con webcam configurada