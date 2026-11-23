# DSY1105 – Proyecto Semestral

## Gestión móvil de oportunidades y activaciones corporativas

Aplicación móvil desarrollada para la asignatura **DSY1105 – Aplicaciones Móviles**.

El proyecto permite gestionar oportunidades comerciales, organizaciones, contactos, activaciones y resultados desde una aplicación Android desarrollada con **Kotlin, Jetpack Compose, Material 3 y arquitectura MVVM**.

Además, incorpora persistencia local mediante **Room**, consumo de un microservicio propio mediante **Retrofit + Spring Boot + MongoDB Atlas** y consumo de una **API externa de feriados de Chile**.

---

## Integrantes

- **María Pía Sepúlveda**
- **Luis Veas**

---

# Tecnologías utilizadas

## Aplicación Android

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- StateFlow
- Room
- Retrofit
- Coroutines
- Cámara del dispositivo
- FileProvider
- Lectura y generación de códigos QR

## Backend

- Kotlin
- Spring Boot 3
- Maven
- REST API
- MongoDB Atlas

## Pruebas

- Kotest
- MockK
- Kotlin Coroutines Test
- Kover

---

# Arquitectura

La aplicación utiliza arquitectura **MVVM (Model – View – ViewModel)**.

Flujo principal de la aplicación:

```text
Pantalla Compose
      ↓
ViewModel / UiState
      ↓
Repository
      ↓
DAO / Room
```

Para el catálogo remoto de eventos:

```text
Android
   ↓
Retrofit
   ↓
Spring Boot
   ↓
MongoDB Atlas
```

Para la API externa:

```text
Android
   ↓
Retrofit
   ↓
Nager.Date API
```

---

# Funcionalidades principales

## Autenticación

La aplicación permite:

- iniciar sesión;
- registrar usuarios;
- validar credenciales;
- mantener usuarios almacenados localmente;
- controlar usuarios activos e inactivos;
- trabajar con distintos perfiles.

Perfiles disponibles:

- Ejecutivo Comercial
- Supervisor Comercial
- Gestión de Activaciones

---

# Gestión de oportunidades

El módulo de oportunidades permite:

- crear oportunidades;
- visualizar oportunidades;
- editar oportunidades;
- eliminar oportunidades;
- modificar su estado;
- seleccionar prioridad;
- seleccionar potencial;
- asociar organizaciones;
- asociar contactos;
- registrar evidencia fotográfica;
- utilizar la cámara del dispositivo;
- mantener persistencia mediante Room.

Estados principales:

- Pendiente
- En gestión
- Confirmada
- Descartada

---

# Organizaciones

El módulo de organizaciones permite administrar empresas u organizaciones relacionadas con las oportunidades comerciales.

Incluye:

- creación;
- listado;
- edición;
- eliminación;
- validaciones;
- persistencia mediante Room.

---

# Contactos

Permite registrar contactos asociados a organizaciones.

Incluye:

- creación;
- edición;
- eliminación;
- filtros;
- validación de correo;
- validación de teléfono;
- asociación con organizaciones.

---

# Seguimientos

Permite registrar acciones realizadas sobre una oportunidad.

Entre ellas:

- llamadas;
- reuniones;
- correos;
- contacto relacionado;
- resultado de la acción;
- próxima acción;
- próxima fecha de seguimiento;
- observaciones.

El sistema valida que una próxima fecha de seguimiento no sea anterior a la fecha de la acción realizada.

---

# Activaciones

Las oportunidades confirmadas pueden utilizarse para generar activaciones.

El módulo permite:

- seleccionar oportunidades confirmadas;
- asignar responsables;
- registrar información de la activación;
- actualizar activaciones;
- eliminar registros;
- controlar permisos según perfil.

---

# Resultados

Permite registrar el resultado obtenido después de una activación.

Se pueden almacenar:

- activación asociada;
- resultado;
- cantidad de contactos generados;
- evidencia;
- observaciones.

---

# Catálogo remoto de eventos

La aplicación incorpora un catálogo de eventos almacenado en **MongoDB Atlas**.

La comunicación se realiza mediante un microservicio desarrollado con **Spring Boot**.

Arquitectura:

```text
Aplicación Android
       ↓
    Retrofit
       ↓
Spring Boot REST API
       ↓
 MongoDB Atlas
```

Desde la aplicación es posible realizar operaciones CRUD remotas.

## Operaciones implementadas

### Consultar eventos

```http
GET /eventos
```

### Consultar evento por ID

```http
GET /eventos/{id}
```

### Crear evento

```http
POST /eventos
```

### Actualizar evento

```http
PUT /eventos/{id}
```

### Eliminar evento

```http
DELETE /eventos/{id}
```

Algunas operaciones utilizan los encabezados:

```text
X-Perfil
X-Usuario-Id
```

para identificar al usuario que realiza la operación.

---

# Permisos del catálogo remoto

## Ejecutivo Comercial

Puede:

- consultar;
- crear;
- editar.

## Supervisor Comercial

Puede:

- consultar;
- editar;
- eliminar.

## Gestión de Activaciones

Puede:

- consultar.

---

# MongoDB Atlas

El microservicio utiliza MongoDB Atlas como base de datos remota.

Base utilizada:

```text
dsy1105_equipo1
```

Colección principal:

```text
eventos
```

La URI de conexión se mantiene fuera del repositorio público mediante configuración local.

---

# API externa

Además del microservicio propio, la aplicación consume una API pública externa.

## Nager.Date

Servicio utilizado:

```text
Nager.Date Public Holidays API
```

La aplicación consulta los feriados oficiales de Chile.

Ejemplo conceptual:

```http
GET /api/v3/PublicHolidays/2026/CL
```

Esta información se utiliza como apoyo para la planificación de eventos y oportunidades comerciales.

La API externa se consume directamente desde Android mediante Retrofit.

---

# Diferencia entre microservicio propio y API externa

## Microservicio propio

```text
Android → Retrofit → Spring Boot → MongoDB Atlas
```

El equipo controla:

- backend;
- endpoints;
- reglas de negocio;
- base de datos.

## API externa

```text
Android → Retrofit → Nager.Date
```

Es un servicio externo utilizado únicamente como fuente de información.

---

# Recursos nativos

## Cámara

La aplicación utiliza la cámara del dispositivo para registrar fotografías como evidencia.

Tecnologías utilizadas:

- permisos Android;
- FileProvider;
- Activity Result API;
- TakePicture.

---

# Código QR

La aplicación incorpora funcionalidades relacionadas con códigos QR.

Permite utilizar QR como recurso complementario dentro del sistema móvil.

---

# Persistencia local

La aplicación utiliza **Room** como base de datos local.

Principales componentes:

```text
Entity
DAO
Repository
ViewModel
Screen
```

La información almacenada localmente permanece disponible después de cerrar y volver a abrir la aplicación.

---

# Pruebas unitarias

Las pruebas unitarias de la aplicación utilizan:

- Kotest;
- MockK;
- Coroutines Test;
- Kover.

Se prueban principalmente las capas:

```text
ViewModel
Repository
```

Entre los componentes cubiertos se encuentran:

- LoginViewModel
- OportunidadViewModel
- ContactoViewModel
- OrganizacionViewModel
- ActivacionViewModel
- ResultadoViewModel
- SeguimientoViewModel
- FeriadoViewModel
- UsuariosViewModel

También se validan:

- formularios;
- reglas de negocio;
- validaciones;
- errores de Repository;
- operaciones CRUD;
- estados exitosos;
- estados de error.

## Cobertura

La cobertura se mide mediante **Kover**.

Estado durante la etapa final de QA:

```text
Cobertura en actualización.
```

Este valor debe reemplazarse por el porcentaje final obtenido antes de cerrar la entrega.

Para ejecutar la cobertura:

```powershell
.\gradlew :app:koverLogDebug --no-daemon
```

Para generar el informe HTML:

```powershell
.\gradlew :app:koverHtmlReportDebug --no-daemon
```

Informe:

```text
app/build/reports/kover/htmlDebug/index.html
```

---

# Ejecutar pruebas Android

Desde la raíz del proyecto:

```powershell
.\gradlew :app:testDebugUnitTest --no-daemon
```

---

# Pruebas del backend

El backend cuenta con pruebas automatizadas.

También se incluye un script para facilitar su ejecución:

```text
EJECUTAR_TESTS_BACKEND.bat
```

Resultado validado durante QA:

```text
Tests run: 5
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

---

# Ejecutar backend

Existe un archivo de apoyo:

```text
INICIAR_BACKEND.bat
```

También puede iniciarse manualmente desde la carpeta del backend utilizando Maven Wrapper.

El backend necesita el archivo local:

```text
backend/src/main/resources/application-local.properties
```

con la configuración privada de MongoDB Atlas.

Las credenciales y URI privadas no se almacenan en GitHub.

Cuando el backend se inicia correctamente debe aparecer:

```text
Started BackendApplicationKt
```

El catálogo puede verificarse desde:

```text
http://localhost:8080/eventos
```

---

# Ejecución con teléfono físico

Para las pruebas se utilizó un dispositivo Android físico.

La aplicación se conecta al backend local mediante:

```bash
adb reverse tcp:8080 tcp:8080
```

También se incluye:

```text
CONECTAR_TELEFONO.bat
```

para simplificar esta configuración.

La aplicación Android utiliza como dirección local del backend:

```text
http://127.0.0.1:8080/
```

Gracias a `adb reverse`, esta dirección permite que el teléfono acceda al Spring Boot que se está ejecutando en el computador.

---

# APK Release firmado

El proyecto genera una versión Release firmada de la aplicación.

APK generado:

```text
app/build/outputs/apk/release/app-release.apk
```

La firma fue realizada mediante un archivo:

```text
.jks
```

El archivo de firma y su configuración forman parte de la preparación de entrega.

Las contraseñas privadas de firma **no se publican en el repositorio**.

## Verificación

La firma del APK fue comprobada mediante `apksigner`.

El APK utiliza firma Android v2.

SHA-256 del APK validado durante QA:

```text
5599CC2FF456EF11C0BECFA2961367E28D92EB4D3B9371A275981967FED596DD
```

---

# Usuarios de demostración

Para facilitar la revisión se encuentran disponibles usuarios de prueba.

## Supervisor Comercial

```text
supervisor@agencia.cl
12345
```

## Ejecutivo Comercial

```text
ejecutivo@agencia.cl
12345
```

## Gestión de Activaciones

```text
activaciones@agencia.cl
12345
```

Estas cuentas corresponden únicamente a datos de demostración académica.

---

# GitHub

Repositorio del proyecto:

```text
https://github.com/luchovduoc/DSY1105
```

El desarrollo se realizó mediante control de versiones Git y GitHub.

El historial permite identificar los cambios realizados durante el desarrollo del proyecto.

---

# Trello

El equipo utilizó Trello para organizar:

- funcionalidades;
- desarrollo;
- pruebas;
- tareas pendientes;
- actividades terminadas;
- responsabilidades de los integrantes.

El tablero sirve como evidencia del proceso de planificación y trabajo colaborativo.

---

# Flujo de demostración recomendado

Durante la revisión del proyecto puede demostrarse el siguiente flujo:

```text
1. Iniciar Spring Boot
2. Verificar GET /eventos
3. Conectar teléfono mediante ADB
4. Iniciar la aplicación
5. Iniciar sesión
6. Mostrar funcionalidades locales
7. Abrir catálogo remoto
8. Crear evento
9. Editar evento
10. Verificar cambio en backend/MongoDB
11. Eliminar evento
12. Abrir feriados de Chile
13. Mostrar consumo de API externa
14. Ejecutar pruebas unitarias
15. Mostrar cobertura Kover
16. Mostrar APK firmado
17. Mostrar GitHub y Trello
```

---

# Seguridad

El proyecto evita almacenar en el repositorio público:

- contraseñas privadas;
- URI de MongoDB Atlas;
- passwords del JKS;
- información sensible de configuración.

Los valores privados se mantienen en archivos locales excluidos del control de versiones.

---

# Estado del proyecto

Proyecto preparado para la entrega final de **DSY1105 – Aplicaciones Móviles**.

Incluye:

- aplicación Android funcional;
- arquitectura MVVM;
- persistencia Room;
- recursos nativos;
- CRUD local;
- microservicio Spring Boot;
- MongoDB Atlas;
- CRUD remoto;
- API externa;
- Retrofit;
- pruebas unitarias;
- cobertura Kover;
- APK Release firmado;
- GitHub;
- Trello.