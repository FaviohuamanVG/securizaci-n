# Documentación de Securización - Backend vg-ms-user

## Resumen

Este microservicio implementa un sistema de securización robusto basado en **Keycloak** como proveedor de identidad y **OAuth 2.0/OpenID Connect** como protocolo de autenticación. La implementación utiliza **Spring Security WebFlux** para manejar la seguridad de forma reactiva y proporciona control granular de acceso basado en roles (RBAC).

---

## Arquitectura de Seguridad

### Componentes Principales

1. **Keycloak Server**: Servidor de identidad externo que maneja autenticación y autorización
2. **OAuth 2.0 Resource Server**: El microservicio actúa como servidor de recursos
3. **JWT Token Validation**: Validación automática de tokens JWT
4. **Role-Based Access Control**: Control de acceso basado en roles extraídos del JWT

### Flujo de Autenticación

```
Cliente → Keycloak (Auth) → JWT Token → Microservicio (Validation) → Recurso Protegido
```

---

## Configuración de Seguridad

### 1. Configuración Principal (application.yml)

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:9090/realms/eduassist
```

**Descripción:**
- **issuer-uri**: URL del realm de Keycloak que emite los tokens JWT
- **eduassist**: Nombre del realm configurado en Keycloak

### 2. SecurityConfig.java

La clase principal de configuración de seguridad implementa:

#### Configuración de Filtros de Seguridad
```java
@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http
        .csrf(csrf -> csrf.disable()) // CSRF deshabilitado para APIs REST
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(...)) // Configuración JWT
        .build();
}
```

#### Control de Acceso por Endpoints

| Endpoint Pattern | Método HTTP | Roles Requeridos | Descripción |
|------------------|-------------|------------------|-------------|
| `/actuator/**` | Todos | Público | Endpoints de monitoreo |
| `/v3/api-docs/**` | Todos | Público | Documentación API |
| `/api/v1/health` | GET | Público | Health check |
| `/api/v1/users/**` | DELETE | ADMIN | Eliminar usuarios |
| `/api/v1/users/**` | PUT | ADMIN, USER | Actualizar usuarios |
| `/api/v1/users/**` | POST | ADMIN | Crear usuarios |
| `/api/v1/users/**` | GET | ADMIN, USER | Consultar usuarios |
| `/api/v1/permissions/**` | Todos | ADMIN | Gestión de permisos |
| `/api/v1/teachers/**` | GET | ADMIN, USER | Consultar profesores |
| `/api/v1/teachers/**` | POST/PUT/DELETE | ADMIN | Gestionar profesores |
| `/api/v1/user-sedes/**` | GET | ADMIN, USER | Consultar asignaciones |
| `/api/v1/user-sedes/**` | POST/PUT/DELETE | ADMIN | Gestionar asignaciones |

### 3. JWT Authentication Converter

#### Extracción de Autoridades desde JWT
```java
private Flux<GrantedAuthority> extractAuthorities(Jwt jwt) {
    // Extraer roles de realm_access (roles del realm)
    // Extraer roles de resource_access (roles del cliente)
    // Mapear roles específicos del sistema
}
```

**Mapeo de Roles:**
- `admin` → `ROLE_ADMIN`
- `user` → `ROLE_USER`
- `eduassist-*` → `ROLE_*` (roles con prefijo específico)

### 4. JWT User Extractor

Componente utilitario para extraer información del usuario autenticado:

```java
@Component
public class JwtUserExtractor {
    public Mono<String> getCurrentUserId()     // Obtiene 'sub' claim
    public Mono<String> getCurrentUsername()   // Obtiene 'preferred_username'
    public Mono<String> getCurrentUserEmail()  // Obtiene 'email' claim
    public Mono<Jwt> getCurrentJwt()          // Obtiene el JWT completo
}
```

---

## Sistema de Roles y Permisos

### Roles Implementados

#### 1. ROLE_ADMIN (Administrador)
**Permisos:**
- ✅ Crear, editar, eliminar usuarios
- ✅ Gestionar todos los profesores
- ✅ Gestionar asignaciones usuario-sede
- ✅ Administrar permisos del sistema
- ✅ Acceso completo a todas las funcionalidades

#### 2. ROLE_USER (Usuario)
**Permisos:**
- ✅ Consultar usuarios (lectura)
- ✅ Actualizar su propio perfil
- ✅ Consultar profesores (lectura)
- ✅ Consultar asignaciones usuario-sede (lectura)
- ❌ No puede crear, eliminar o gestionar otros usuarios

### Permisos Granulares en Dominio

Además de los roles de Spring Security, el sistema implementa permisos específicos del dominio:

```java
// Permisos por defecto según rol
ROLE_DIRECTOR → [CREAR, EDITAR, VER, ELIMINAR, RESTAURAR]
ROLE_PROFESOR → [VER, EDITAR]
ROLE_AUXILIAR → [VER, EDITAR]
DEFAULT → [VER]
```

---

## Implementación de Seguridad Reactiva

### WebFlux Security
- **Filtros Reactivos**: Utiliza `SecurityWebFilterChain` para manejo asíncrono
- **Context Holder**: `ReactiveSecurityContextHolder` para acceso al contexto de seguridad
- **Mono/Flux**: Integración completa con programación reactiva

### Ventajas de la Implementación Reactiva
- **Performance**: Mejor manejo de concurrencia
- **Escalabilidad**: Threads no bloqueantes
- **Integración**: Compatibilidad completa con Spring WebFlux

---

## Configuración de CORS

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        // Configuración permisiva para desarrollo
        // Permite todos los orígenes, métodos y headers
        // Expone header 'Authorization'
    }
}
```

**Nota de Seguridad:** La configuración actual es permisiva para desarrollo. En producción se debe restringir a dominios específicos.

---

## Flujo de Validación JWT

### 1. Recepción de Request
```
Cliente → Request con Header: Authorization: Bearer <JWT_TOKEN>
```

### 2. Validación del Token
```java
// Spring Security automáticamente:
1. Extrae el token del header Authorization
2. Valida la firma con la clave pública de Keycloak
3. Verifica expiración y claims obligatorios
4. Extrae roles y permisos
```

### 3. Autorización
```java
// Para cada endpoint protegido:
1. Verifica si el usuario tiene el rol requerido
2. Evalúa permisos específicos del método HTTP
3. Permite o deniega el acceso
```

---

## Configuración de Keycloak

### Realm: eduassist
- **Clients**: Frontend y backend clients configurados
- **Users**: Usuarios con roles asignados
- **Roles**: admin, user, eduassist-* roles
- **Token Settings**: JWT firmado con RS256

### Claims Esperados en JWT
```json
{
  "sub": "usuario-id-keycloak",
  "preferred_username": "nombreusuario",
  "email": "usuario@ejemplo.com",
  "realm_access": {
    "roles": ["admin", "user", "eduassist-director"]
  },
  "resource_access": {
    "eduassist": {
      "roles": ["specific-role"]
    }
  }
}
```

---

## Endpoints de Seguridad

### Endpoints Públicos (Sin Autenticación)
- `GET /actuator/**` - Monitoreo de aplicación
- `GET /v3/api-docs/**` - Documentación OpenAPI
- `GET /swagger-ui/**` - Interfaz Swagger
- `GET /api/v1/health` - Health check

### Endpoints Protegidos
**Requieren token JWT válido y roles específicos según la tabla de control de acceso.**

---

## Manejo de Errores de Seguridad

### Códigos de Estado HTTP
- **401 Unauthorized**: Token inválido o ausente
- **403 Forbidden**: Token válido pero sin permisos suficientes
- **404 Not Found**: Recurso no encontrado (puede enmascarar 403)

### Logs de Seguridad
Spring Security registra automáticamente:
- Intentos de autenticación fallidos
- Accesos denegados por falta de autorización
- Tokens JWT inválidos

---

## Mejores Prácticas Implementadas

### ✅ Seguridad
- **Principio de Menor Privilegio**: Roles mínimos necesarios
- **Separación de Responsabilidades**: Autenticación vs Autorización
- **Validación Centralizada**: Un punto de configuración de seguridad
- **CSRF Protection**: Deshabilitado correctamente para APIs REST

### ✅ Performance
- **Validación Asíncrona**: No bloquea threads
- **Cache de Claves**: Spring Security cachea claves públicas de Keycloak
- **Lazy Loading**: Roles se cargan solo cuando es necesario

### ✅ Mantenibilidad
- **Configuración Declarativa**: Roles definidos en anotaciones
- **Separación de Concerns**: Configuración separada por responsabilidad
- **Reutilización**: JwtUserExtractor para casos comunes

---

## Configuración de Desarrollo vs Producción

### Desarrollo
```yaml
# application-dev.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:9090/realms/eduassist
```

### Producción
```yaml
# application-prod.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://keycloak.empresa.com/realms/eduassist
```

**Consideraciones Adicionales para Producción:**
- Configurar CORS restrictivo
- Habilitar HTTPS obligatorio
- Configurar rate limiting
- Implementar logging de auditoría detallado

---

## Testing de Seguridad

### Unit Tests
```java
@Test
@WithMockJwt(roles = "ADMIN")
void shouldAllowAdminAccess() {
    // Test con rol ADMIN
}

@Test
@WithMockJwt(roles = "USER")
void shouldDenyUserAccess() {
    // Test con rol USER para endpoint de admin
}
```

### Integration Tests
- Tests con tokens JWT reales de Keycloak de prueba
- Validación de flujos completos de autenticación
- Verificación de autorización por endpoints

---

## Monitoreo y Auditoría

### Métricas de Seguridad
- Número de tokens JWT validados
- Intentos de acceso denegados por rol
- Tiempo de respuesta de validación JWT

### Logs de Auditoría
```
INFO  - Usuario 'admin' accedió a GET /api/v1/users
WARN  - Acceso denegado: Usuario 'user' intentó DELETE /api/v1/users/123
ERROR - Token JWT inválido recibido desde IP: 192.168.1.100
```

---

## Troubleshooting Común

### Problema: 401 Unauthorized
**Causas posibles:**
- Token JWT ausente en header Authorization
- Token expirado
- Keycloak no disponible
- Configuración de issuer-uri incorrecta

### Problema: 403 Forbidden
**Causas posibles:**
- Usuario autenticado pero sin rol requerido
- Roles mal configurados en Keycloak
- Mapeo de roles incorrecto en extractAuthorities()

### Problema: CORS Error
**Solución:**
- Verificar configuración en CorsConfig
- Asegurar que el frontend envía headers correctos
- Validar que el dominio esté en allowedOriginPatterns

---

*Esta documentación describe la implementación completa de securización del microservicio vg-ms-user utilizando Keycloak y Spring Security WebFlux.*
