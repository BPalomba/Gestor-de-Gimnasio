# GymSaaS

Sistema de gestión para gimnasios independientes construido como plataforma multi-tenant. Un solo deployment sirve a múltiples gimnasios con aislamiento completo de datos por tenant.

---

## Stack

**Backend**
- Java 21 con Virtual Threads
- Spring Boot 3.3.5 · Spring Security 6 · Spring Data JPA
- PostgreSQL 16 · Hibernate ORM
- JWT stateless (JJWT 0.12.5)
- Lombok · springdoc-openapi

**Frontend**
- React 18 · Vite
- TanStack Query · Zustand · React Router v6
- Tailwind CSS · shadcn/ui
- React Hook Form · Zod

**Infraestructura**
- Docker · Docker Compose
- GitHub Actions CI/CD
- JUnit 5 · Mockito · H2 (tests)

---

## Arquitectura

Arquitectura de tres capas sobre un modelo cliente-servidor REST con aislamiento multi-tenant por JWT. El `gymId` viaja en el payload del token y se extrae en cada request mediante un `ThreadLocal`, garantizando que ningún tenant pueda acceder a datos de otro sin depender del cliente.

```
React (Vite)  →  REST API (Spring Boot)  →  PostgreSQL
                      ↕
               JWT + GymContextHolder
               (aislamiento multi-tenant)
```

**Organización del código:** package by feature. Cada módulo es autocontenido con su entidad, repositorio, servicio, mapper, DTOs y controller.

```
modules/
├── auth/
├── branch/
├── member/
├── membership/
├── payment/
├── plan/
├── role/
├── user/
└── dashboard/
```

---

## Funcionalidades

**Seguridad**
- Autenticación JWT con access token (15 min) y refresh token (7 días)
- Roles dinámicos por gimnasio con 27 permisos granulares asignables
- Endpoints protegidos con `@PreAuthorize` por permiso específico
- Aislamiento multi-tenant en todas las queries del repositorio

**Gestión operativa**
- CRUD completo de socios con búsqueda paginada y validación de DNI único por gimnasio
- Membresías con cálculo automático de vencimiento, snapshot de precio y congelamiento acumulativo
- Planes de membresía con visibilidad pública configurable
- Pagos manuales con cálculo automático de revenue share

**Automatización**
- Cron job nocturno que detecta membresías vencidas, las marca como `EXPIRED` y suspende al socio
- Arquitectura preparada para integración con Mercado Pago (webhook + QR)
- Entidades `PaymentLink` y `MemberNotification` modeladas para notificaciones automáticas

**Reportes**
- Dashboard con métricas en tiempo real: ingresos del período, comparativa mensual, socios activos, membresías por vencer y top planes

---

## Decisiones técnicas

**`FetchType.LAZY` en todas las relaciones `@ManyToOne`**
Evita el problema N+1 y reduce la carga en queries de listado donde no se necesita el objeto relacionado completo.

**`gymId` siempre desde el JWT, nunca del request body**
Protección contra IDOR. Un usuario no puede acceder a datos de otro gimnasio aunque manipule el cuerpo del request. El `GymContextHolder` (ThreadLocal) hace disponible el `gymId` en toda la cadena de ejecución.

**Snapshot de precio en `Membership.pricePaid`**
Los cambios futuros en el precio de un plan no afectan membresías históricas. Cada membresía guarda el precio vigente al momento de su creación.

**Estados de socios como entidad separada (`MemberStatus`)**
Permite agregar nuevos estados sin recompilar. Sigue el principio Open/Closed: el sistema puede extenderse sin modificar las entidades existentes.

**`PasswordEncoderConfig` en clase separada**
Evita la instanciación dual de `BCryptPasswordEncoder` que ocurre cuando el bean se define dentro de `SecurityConfig`, causando que `passwordEncoder.matches()` siempre retorne `false`.

---

## Levantar con Docker

```bash
# Clonar el repositorio
git clone https://github.com/tuusuario/gymsaas.git
cd gymsaas

# Compilar
mvn clean package -DskipTests

# Levantar backend + base de datos
docker compose up --build
```

El sistema queda disponible en:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

**Frontend**

```bash
cd gymsaas-frontend
npm install
npm run dev
```

Panel disponible en `http://localhost:5173`

---

## Tests y CI

```bash
# Ejecutar tests unitarios
mvn test -Dspring.profiles.active=test
```

El pipeline de GitHub Actions se ejecuta en cada push a `main` o `develop`:

1. Configura Java 21 (Temurin)
2. Ejecuta los tests con H2 en memoria
3. Publica los resultados en la pestaña Actions
4. Si el branch es `main`, genera y sube el JAR como artefacto

---

## Endpoints principales

```
POST   /api/auth/login
GET    /api/dashboard
GET    /api/members
POST   /api/members
GET    /api/members/{id}
POST   /api/memberships
PATCH  /api/memberships/{id}/freeze
PATCH  /api/memberships/{id}/unfreeze
GET    /api/plans
POST   /api/payments
GET    /api/payments/summary
GET    /api/roles
POST   /api/roles
```

Documentación completa disponible en Swagger UI una vez levantado el proyecto.

---

## Estructura del proyecto

```
gymsaas/
├── src/
│   ├── main/
│   │   ├── java/com/gymsaas/
│   │   │   ├── config/          # Security, JWT, CORS, OpenAPI
│   │   │   ├── shared/          # BaseEntity, excepciones, GymContextHolder
│   │   │   └── modules/         # Módulos de negocio
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   └── test/
│       ├── java/                # Tests unitarios con Mockito
│       └── resources/
│           └── application-test.properties
├── frontend/                    # Frontend React
├── docker/
├── docker-compose.yml
├── Dockerfile
└── .github/workflows/ci.yml
```

---

## Variables de entorno

| Variable | Descripción | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexión a PostgreSQL | `jdbc:postgresql://localhost:5432/gymsaas` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base | `postgres` |
| `APP_JWT_SECRET` | Secret para firmar los tokens JWT | — |
| `APP_JWT_EXPIRATION_MS` | Duración del access token en ms | `900000` (15 min) |
| `APP_JWT_REFRESH_EXPIRATION_MS` | Duración del refresh token en ms | `604800000` (7 días) |

---

## Autor

Bruno Palombarini — [LinkedIn](https://www.linkedin.com/in/bruno-palombarini-b43850285/) · [GitHub](https://github.com/BPalomba)
