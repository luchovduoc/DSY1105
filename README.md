# DSY1105 — Gestión Móvil de Oportunidades y Activaciones Corporativas

## Descripción del proyecto

Aplicación móvil Android desarrollada para la asignatura Desarrollo de Aplicaciones Móviles (DSY1105) de Duoc UC. Resuelve el caso de una agencia de marketing directo que necesita centralizar y gestionar oportunidades de participación en eventos corporativos, ferias empresariales y activaciones comerciales.

## Integrantes

- Luis Veas
- Maria Pía Sepulveda

## Tecnologías utilizadas

- Kotlin 2.0.21
- Android Studio Quail 4
- Jetpack Compose + Material Design 3
- Arquitectura MVVM
- Room (persistencia local)
- CameraX + ML Kit (lector QR)
- ZXing (generador QR)
- Coil (carga de imágenes)
- Git / GitHub

## Funcionalidades implementadas

- Registro e inicio de sesión con persistencia en Room
- Gestión completa de oportunidades (crear, editar, ver detalle)
- Foto de evidencia asociada a cada oportunidad
- Lector de QR para identificar fuente de información
- Generación automática de QR por oportunidad
- Gestión de organizaciones (crear y listar)
- Navegación con menú lateral entre módulos

## Pasos para ejecutar

### Requisitos
- Android Studio Quail 4 o superior
- Dispositivo Android o emulador con API 33-35 y Google APIs

### Pasos

1. Clonar el repositorio


git clone https://github.com/luchovduoc/DSY1105.git



2. Abrir Android Studio y seleccionar **Open** → carpeta del proyecto

3. Esperar que Gradle sincronice las dependencias automáticamente

4. **Importante:** Si la app ya estaba instalada previamente en el dispositivo o emulador, desinstalarla antes de ejecutar. Esto evita conflictos con versiones anteriores de la base de datos local.

5. Seleccionar el dispositivo o emulador y ejecutar con **Play ▶**

6. En la pantalla de inicio, crear una cuenta nueva con **"¿No tienes cuenta? Crear cuenta"**

7. Iniciar sesión con las credenciales registradas

### Para probar el lector QR

- Generar un QR con cualquier texto en [qr.io](https://qr.io) pestaña **Text**
- Desde la app, abrir el formulario de nueva oportunidad
- Presionar **"Escanear QR como fuente"**
- Apuntar la cámara al QR generado
- El texto se copiará automáticamente al campo Fuente

### Para probar la cámara

- Se recomienda dispositivo físico Android
- En emulador se requiere webcam USB configurada en Device Manager → Advanced Settings → Front Camera → Webcam0