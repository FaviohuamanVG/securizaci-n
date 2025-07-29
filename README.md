# vg-ms-user

Este microservicio es responsable de gestionar datos relacionados con usuarios, incluyendo cuentas de usuario, información de profesores, asignaciones usuario-sede y permisos dentro del sistema Valle Grande. Proporciona una API RESTful para diversas operaciones sobre estas entidades.

## Stack Tecnológico

Este microservicio está construido utilizando el siguiente stack tecnológico:

-   **Lenguaje de Programación**: Java
-   **Framework**: Spring Boot
-   **Base de Datos**: MongoDB (utilizando Spring Data MongoDB Reactive)
-   **Gestor de Dependencias**: Maven
-   **Contenerización**: Docker
-   **API**: RESTful
-   **Manejo de Asincronía**: Project Reactor (evidenciado por el uso de `Mono` y `Flux` en los repositorios reactivos)

## Estructura del Proyecto

El proyecto sigue un patrón de arquitectura en capas (similar a Puertos y Adaptadores o Hexagonal), organizado en paquetes distintos:

-   **`application/service`**: Contiene la lógica de negocio principal y los casos de uso de la aplicación. Las clases aquí orquestan las operaciones sobre los modelos de dominio e interactúan con la capa de infraestructura (repositorios).
-   **`domain/model`**: Define las entidades centrales del dominio (objetos de negocio) como `User`, `Teacher`, `UserSede` y `Permission`. Son objetos Java simples que representan la estructura de los datos.
-   **`infrastructure`**: Maneja las preocupaciones externas y los detalles técnicos, incluyendo:
    -   **`config`**: Clases de configuración para la aplicación.
    -   **`exception`**: Clases de excepción personalizadas.
    -   **`repository`**: Interfaces para el acceso a datos (usando Spring Data MongoDB). Spring Data proporciona las implementaciones automáticamente.
    -   **`rest`**: Controladores REST que exponen los endpoints de la API y manejan las solicitudes y respuestas HTTP.
    -   **`service`**: (Nota: Aunque existe un paquete 'service' directamente bajo infrastructure, los servicios de lógica de negocio principales están bajo `application/service`. Este paquete de servicio de infraestructura podría contener servicios técnicos o adaptadores).

```
src/main/java/pe/edu/vallegrande/vg_ms_user/
├── domain/
│   ├── model/
│   │   └── Permission.java                 // Entidad que representa una nota
│   │   └── Teacher.java                 // Entidad que representa una nota
│   │   └── User.java                 // Entidad que representa una nota
│   │   └── UserSede.java      // Entidad para la secuencia de IDs
│   ├── enums/
│   │   └── ... (otros enums del dominio, no utilizado en mi proyecto)
│   └── repository/
│       └── GradeRepository.java       // Interfaz para la persistencia de notas
├── application/
│   ├── service/
│   │   ├── PermissionService.java      // Interfaz para la gestión de permisos
│   │   ├── TeacherService.java         // Servicio para gestionar docentes
│   │   ├── UserService.java            // Servicio para la gestión de usuarios y generación de IDs
│   │   └── UserSedeService.java        // Servicio para la asignación de usuarios a sedes y roles
│   └── impl/
│       ├── no utilizado para mi proyecto
├── infrastructure/
│   ├── client/
│   │   ├── HeadquarterClient.java     // Cliente para consumir servicios de la sede (infraestructura)
│   │   └── InstitutionClient.java     // Cliente para consumir servicios de la institución (infraestructura)
│   ├── config/
│   │   ├── MongoConfig.java         // Configuración de la conexión a MongoDB
│   │   └── WebClientConfig.java     // Configuración del WebClient para llamadas HTTP externas
│   ├── dto/
│   │   └── HeadquarterResponseDTO.java
│   │   └── InstitutionResponseDTO.java
│   ├── exception/
│   │   └── GradeMapper.java           // Mapper entre Grade y GradeDocument
│   ├── repository/
│   │   ├── PermissionRepository.java   // Repositorio Spring Data MongoDB para la entidad Permission
│   │   ├── TeacherRepository.java      // Repositorio Spring Data MongoDB para la entidad Teacher
│   │   ├── UserRepository.java         // Repositorio Spring Data MongoDB para la entidad User
│   │   └── UserSedeRepository.java     // Repositorio Spring Data MongoDB para la entidad UserSede
│   ├── rest/
│   │   ├── PermissionRest.java     // Controlador REST para la gestión de permisos
│   │   ├── TeacherRest.java        // Controlador REST para la gestión de docentes
│   │   ├── UserRest.java           // Controlador REST para la gestión de usuarios
│   │   └── UserSedesRest.java      // Controlador REST para la asignación de usuarios a sedes y roles
└── VgMsGradeManagementApplication.java  // Clase principal de la aplicación
```

## API Endpoints

A continuación se listan los endpoints de la API REST disponibles proporcionados por este microservicio:

### Endpoints de Usuario (`/users`)

-   `POST /users`: Crea un nuevo usuario (individual).
    -   Cuerpo de la Solicitud: Objeto `User`.
    -   Ejemplo de JSON para crear un usuario:
        ```json
        {
          "institutionId": "someInstitutionId",
          "firstName": "Juan",
          "lastName": "Perez",
          "documentType": "DNI",
          "documentNumber": "12345678Z",
          "email": "juan.perez@example.com",
          "phone": "987654321",
          "password": "securepassword",
          "userName": "jperez",
          "role": "PROFESOR",
          "view": "teacherDashboard",
          "permissions": []
        }
        ```
    -   Respuesta: Objeto `User` creado.
-   `POST /users/batch`: Crea múltiples usuarios (lote).
    -   Cuerpo de la Solicitud: Array de objetos `User` (`List<User>`).
    -   Ejemplo de JSON para crear usuarios en lote:
        ```json
        [
          {
            "institutionId": "inst001",
            "firstName": "Ana",
            "lastName": "Gomez",
            "documentType": "DNI",
            "documentNumber": "11223344A",
            "email": "ana.gomez@example.com",
            "phone": "912345678",
            "password": "password123",
            "userName": "agomez",
            "role": "AUXILIAR",
            "view": "assistantPanel"
          },
          {
            "institutionId": "inst002",
            "firstName": "Luis",
            "lastName": "Martinez",
            "documentType": "PASSPORT",
            "documentNumber": "P98765432",
            "email": "luis.martinez@example.com",
            "phone": "987654321",
            "password": "password456",
            "userName": "lmartinez",
            "role": "DIRECTOR",
            "view": "directorView"
          }
        ]
        ```
    -   Respuesta: Array de objetos `User` creados.
-   `GET /users`: Obtiene todos los usuarios. Opcionalmente filtra por rol y estado.
    -   Parámetros de Consulta:
        -   `role` (opcional): Filtra por rol de usuario (ej. `PROFESOR`, `AUXILIAR`).
        -   `status` (opcional): Filtra por estado (`active` o `inactive`).
    -   Respuesta: Array de objetos `User`.
-   `GET /users/{id}`: Obtiene un usuario por ID.
    -   Variable de Ruta: `id` (ID del usuario).
    -   Respuesta: Objeto `User`.
-   `PUT /users/{id}`: Actualiza un usuario por ID.
    -   Variable de Ruta: `id` (ID del usuario).
    -   Cuerpo de la Solicitud: Objeto `User` actualizado.
    -   Ejemplo de JSON para actualizar un usuario:
        ```json
        {
          "institutionId": "updatedInstId",
          "firstName": "Juan Carlos",
          "lastName": "Perez Lopez",
          "documentType": "DNI",
          "documentNumber": "12345678Z",
          "email": "juan.carlos.perez@example.com",
          "phone": "999888777",
          "password": "newsecurepassword",
          "userName": "jcperez",
          "role": "PROFESOR",
          "view": "teacherDashboard",
          "status": "ACTIVE",
          "permissions": ["VER", "EDITAR"]
        }
        ```
    -   Respuesta: Objeto `User` actualizado.
-   `PATCH /users/{id}/deactivate`: Desactiva lógicamente (eliminado suave) un usuario por ID.
    -   Variable de Ruta: `id` (ID del usuario).
    -   Respuesta: Sin contenido (204) en caso de éxito.
-   `PATCH /users/{id}/activate`: Activa (restaura) un usuario que fue desactivado lógicamente por ID.
    -   Variable de Ruta: `id` (ID del usuario).
    -   Respuesta: Objeto `User` activado.
-   `POST /users/{userId}/permissions/{permission}`: Añade un permiso específico a un usuario.
    -   Variables de Ruta: `userId`, `permission`.
    -   Respuesta: Objeto `User` actualizado.
-   `POST /users/{userId}/permissions/batch`: Añade múltiples permisos a un usuario.
    -   Variable de Ruta: `userId`.
    -   Cuerpo de la Solicitud: Conjunto de cadenas de permisos (`Set<String>`).
    -   Ejemplo de JSON para añadir permisos en lote:
        ```json
        [
          "CREAR",
          "ELIMINAR"
        ]
        ```
    -   Respuesta: Objeto `User` actualizado.
-   `DELETE /users/{userId}/permissions/{permission}`: Elimina un permiso específico de un usuario.
    -   Variables de Ruta: `userId`, `permission`.
    -   Respuesta: Objeto `User` actualizado.
-   `PUT /users/{userId}/permissions`: Establece el conjunto exacto de permisos para un usuario, reemplazando los existentes.
    -   Variable de Ruta: `userId`.
    -   Cuerpo de la Solicitud: Conjunto de cadenas de permisos (`Set<String>`).
    -   Ejemplo de JSON para establecer permisos:
        ```json
        [
          "VER",
          "EDITAR",
          "RESTAURAR"
        ]
        ```
    -   Respuesta: Objeto `User` actualizado.
-   `POST /users/migrate-permissions`: Endpoint para migrar usuarios con permisos por defecto (probablemente una operación única).
    -   Respuesta: Flux de objetos `User` actualizados.

### Endpoints de Profesor (`/teachers`)

-   `POST /teachers`: Crea un nuevo profesor.
    -   Cuerpo de la Solicitud: Objeto `Teacher`.
    -   Ejemplo de JSON para crear un profesor:
        ```json
        {
          "userId": "existingUserId123",
          "specialty": "Matemáticas",
          "bio": "Profesor con 10 años de experiencia en matemáticas avanzadas.",
          "status": "ACTIVE"
        }
        ```
    -   Respuesta: Objeto `Teacher` creado.
-   `GET /teachers`: Obtiene todos los profesores. Opcionalmente filtra por estado.
    -   Parámetro de Consulta: `status` (opcional): Filtra por estado (`active` o `inactive`).
    -   Respuesta: Array de objetos `Teacher`.
-   `GET /teachers/{id}`: Obtiene un profesor por ID.
    -   Variable de Ruta: `id` (ID del profesor).
    -   Respuesta: Objeto `Teacher`.
-   `PUT /teachers/{id}`: Actualiza un profesor por ID.
    -   Variable de Ruta: `id` (ID del profesor).
    -   Cuerpo de la Solicitud: Objeto `Teacher` actualizado.
    -   Ejemplo de JSON para actualizar un profesor:
        ```json
        {
          "userId": "existingUserId123",
          "specialty": "Física y Matemáticas",
          "bio": "Profesor con amplia experiencia en ciencias exactas.",
          "status": "ACTIVE"
        }
        ```
    -   Respuesta: Objeto `Teacher` actualizado.
-   `PATCH /teachers/{id}/deactivate`: Desactiva lógicamente (eliminado suave) un profesor por ID.
    -   Variable de Ruta: `id` (ID del profesor).
    -   Respuesta: Objeto `Teacher` activado.
-   `PATCH /teachers/{id}/activate`: Activa (restaura) un profesor que fue desactivado lógicamente por ID.
    -   Variable de Ruta: `id` (ID del profesor).
    -   Respuesta: Objeto `Teacher` activado.

### Endpoints de Usuario-Sede (`/user-sedes`)

-   `POST /user-sedes`: Crea una nueva asignación usuario-sede.
    -   Cuerpo de la Solicitud: Objeto `UserSede`.
    -   Ejemplo de JSON para crear una asignación UserSede:
        ```json
        {
        "userId": "68223e570a57974d86158212",           // ID del usuario al que se asigna
        "assignmentReason": "apoyo",                   // Motivo de la asignación
        "observations": "usuario asignado 5",          // Observaciones adicionales
        "status": "Activo",                            // Estado inicial ("Activo" o "Inactivo")
        "details": [                                   // Lista de asignaciones a sedes
            {
            "sedeId": "s2",                            // ID de la sede
            "sortOrder": 1,                            // Orden de prioridad
            "role": "DIRECTOR",                        // Rol asignado en esa sede
            "schedule": "mañana/tarde",                // Horario de trabajo
            "assignedAt": "2025-06-08T00:00:00.000Z",  // Fecha de inicio de asignación
            "activeUntil": "2025-07-12T00:00:00.000Z", // Fecha de fin de asignación
            "responsibilities": ["asignacion"]         // Lista de responsabilidades
            },
            {
            "sedeId": "s3",
            "sortOrder": 2,
            "role": "COORDINADOR",
            "schedule": "tarde",
            "assignedAt": "2025-06-10T00:00:00.000Z",
            "activeUntil": "2025-08-01T00:00:00.000Z",
            "responsibilities": ["planificación"]
            }
          ]
        }
        ```
    -   Respuesta: Objeto `UserSede` creado.
-   `GET /user-sedes`: Obtiene todas las asignaciones usuario-sede. Opcionalmente filtra por estado.
    -   Parámetro de Consulta: `status` (opcional): Filtra por estado (`Activo` o `Inactivo`).
    -   Respuesta: Array de objetos `UserSede`.
-   `GET /user-sedes/{id}`: Obtiene una asignación usuario-sede por ID (solo si el estado es "Activo").
    -   Variable de Ruta: `id` (ID de la asignación usuario-sede).
    -   Respuesta: Objeto `UserSede`.
-   `PUT /user-sedes/{id}`: Actualiza una asignación usuario-sede por ID.
    -   Variable de Ruta: `id` (ID de la asignación usuario-sede).
    -   Cuerpo de la Solicitud: Objeto `UserSede` actualizado.
    -   Ejemplo de JSON para actualizar una asignación UserSede:
        ```json
        {
            "userId": "68223e570a57974d86158212",           // ID del usuario al que se asigna
            "assignmentReason": "apoyo",                   // Motivo de la asignación
            "observations": "usuario asignado 5",          // Observaciones adicionales
            "status": "Activo",                            // Estado inicial ("Activo" o "Inactivo")
            "details": [                                   // Lista de asignaciones a sedes
                {
                "sedeId": "s2",                            // ID de la sede
                "sortOrder": 1,                            // Orden de prioridad
                "role": "DIRECTOR",                        // Rol asignado en esa sede
                "schedule": "mañana/tarde",                // Horario de trabajo
                "assignedAt": "2025-06-08T00:00:00.000Z",  // Fecha de inicio de asignación
                "activeUntil": "2025-07-12T00:00:00.000Z", // Fecha de fin de asignación
                "responsibilities": ["asignacion"]         // Lista de responsabilidades
                },
                {
                "sedeId": "s3",
                "sortOrder": 2,
                "role": "COORDINADOR",
                "schedule": "tarde",
                "assignedAt": "2025-06-10T00:00:00.000Z",
                "activeUntil": "2025-08-01T00:00:00.000Z",
                "responsibilities": ["planificación"]
                }
            ]
        }
        ```
    -   Respuesta: Objeto `UserSede` actualizado.
-   `PATCH /user-sedes/{id}/deactivate`: Desactiva lógicamente (eliminado suave) una asignación usuario-sede por ID.
    -   Variable de Ruta: `id` (ID de la asignación usuario-sede).
    -   Respuesta: Sin contenido (204) en caso de éxito.
-   `PATCH /user-sedes/{id}/activate`: Activa (restaura) una asignación usuario-sede que fue desactivada lógicamente por ID.
    -   Variable de Ruta: `id` (ID de la asignación usuario-sede).
    -   Respuesta: Objeto `UserSede` activado.

### Endpoints de Permiso (`/permissions`)

*(Nota: Basado en la estructura de tu proyecto, podría existir un controlador `PermissionRest`. Los siguientes endpoints son inferidos basados en los endpoints de `User` relacionados con permisos. Podrías necesitar verificar/añadir endpoints reales si existe un `PermissionRest` dedicado)*

-   *(Potenciales endpoints en un PermissionRest dedicado, si existe:)*
    -   `GET /permissions`: Obtiene todos los permisos disponibles.
    -   `POST /permissions`: Crea un nuevo permiso.
    -   `DELETE /permissions/{id}`: Elimina un permiso.

## Construcción y Ejecución con Docker

El proyecto incluye un `Dockerfile` para construir una imagen Docker para el microservicio.

1.  **Construir la Imagen Docker:**
    ```bash
    docker build -t vg-ms-user .
    ```
    (Este comando utiliza el wrapper `mvnw` dentro del contenedor para construir el JAR de la aplicación, como se define en el Dockerfile).

2.  **Ejecutar el Contenedor Docker:**
    ```bash
    docker run -p 8080:8080 vg-ms-user
    ```
    (Este comando mapea el puerto 8080 de tu máquina host al puerto 8080 dentro del contenedor, donde se ejecuta la aplicación).

**Nota:** El Dockerfile usa `target/your-application.jar`. Asegúrate de que esto coincida con el nombre real del archivo JAR generado por Maven después de la construcción (`./mvnw clean package`).
