# 🧺 Bazar API

API REST para la gestión de ventas de un bazar — productos, clientes y ventas, con
consultas específicas de negocio (bajo stock, productos por venta, monto por fecha,
venta más alta). Construida con **Spring Boot** siguiendo una arquitectura en capas
(Controller → Service → Repository) con separación estricta entre entidades de
persistencia y contrato de API mediante **DTOs**.

Proyecto ancla del roadmap: da un salto de complejidad respecto a
[Ferretería](../api_ferreteria__prueba_tecnica_todocode) (una sola entidad) — acá son
**tres entidades relacionadas entre sí** (Producto, Cliente, Venta), más una cuarta
(`SaleItem`) agregada por decisión propia de modelado para representar correctamente
la relación muchos-a-muchos con datos propios (cantidad, precio unitario, subtotal).

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-build-C71A36?logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-prod-336791?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![Error Handling](https://img.shields.io/badge/Error%20Handling-RFC%207807-blueviolet)
![Status](https://img.shields.io/badge/status-deployado-brightgreen)

**🚀 API en vivo**: https://api-bazar-ejercicio-curso-todocode.onrender.com
_(plan gratuito de Render — la primera petición tras un rato sin tráfico puede tardar ~30-50s en responder mientras la instancia "despierta")_

**🖥️ Front (GitHub Pages)**: https://mdev-repos.github.io/front_bazar__curso_todocode/

---

## Índice

- [Contexto del ejercicio](#contexto-del-ejercicio)
- [Stack técnico](#stack-técnico)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Endpoints](#endpoints)
- [Cómo correrlo en local](#cómo-correrlo-en-local)
- [Perfiles y bases de datos](#perfiles-y-bases-de-datos)
- [Configuración](#configuración)
- [Proyecto relacionado](#proyecto-relacionado)
- [Roadmap](#roadmap)
- [Autor](#autor)

---

## Contexto del ejercicio

> TP Integrador Final — TodoCode Academy

Un bazar necesita una API REST que le permita registrar sus ventas y administrar el
stock de sus productos, para ser consumida tanto desde una futura app web como desde
una futura app mobile. El modelado requiere tres clases relacionadas (`Producto`,
`Cliente`, `Venta`, donde cada venta tiene una lista de productos y un único cliente
asociado), un CRUD completo para cada una, y cuatro consultas específicas de negocio
(bajo stock, productos de una venta puntual, monto total vendido en una fecha, y datos
de la venta con el monto más alto).

## Stack técnico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4 (Spring Web, Spring Data JPA) |
| ORM | Hibernate |
| Base de datos | MySQL (desarrollo) · PostgreSQL (producción) |
| Validación | Jakarta Bean Validation |
| Manejo de errores | `ProblemDetail` (RFC 7807) + `@RestControllerAdvice` centralizado |
| Build | Maven |
| Reducción de boilerplate | Lombok |

## Arquitectura

Arquitectura en capas, con **DTOs (`record`) + Mapper** como frontera entre el
contrato público de la API y el modelo de persistencia — las entidades JPA nunca se
exponen directamente en request ni response.

```
Cliente (JSON)
   │
   ▼
Controller        → recibe/devuelve DTOs, gestiona status codes (ResponseEntity)
   │
   ▼
Service            → lógica de negocio (Streams), orquesta Mapper + Repository (y otros Services)
   │
   ├─▶ Mapper       → traduce Entity ⇄ DTO (con Streams para las colecciones)
   │
   ▼
Repository (Spring Data JPA) → persistencia, incluidas queries JPQL propias
   │
   ▼
MySQL (dev) / PostgreSQL (prod)
```

Cualquier excepción lanzada en Service o Repository es interceptada por un
`GlobalExceptionHandler` (`@RestControllerAdvice`) que la traduce a una respuesta
`ProblemDetail` (RFC 7807) con el status code correspondiente — el Controller
nunca maneja errores directamente.

```
src/main/java/com/mdev/bazar
├── config          # configuración transversal (CORS)
├── controller       # capa REST — solo DTOs y ResponseEntity
├── dto
│   ├── request        # contratos de entrada (records, con validación)
│   └── response         # contratos de salida (records)
├── exception          # excepciones de negocio + GlobalExceptionHandler
├── mapper             # Entity ⇄ DTO
├── model
├── repository         # Spring Data JPA + queries JPQL propias
└── service
    └── impl
```

Un `Service` puede invocar a **otro Service** (nunca al repository de otro dominio
directamente) cuando necesita datos que no le pertenecen — por ejemplo, `SaleService`
le pide productos a `ProductService` y clientes a `ClientService` al armar una venta,
en vez de acceder a sus repositories. `SaleService.createSale` valida cliente y stock
por completo **antes** de persistir nada (fail-fast) y arma toda la venta en memoria
para guardarla en una única operación (`cascade = CascadeType.ALL` en `Sale.saleItems`),
todo dentro de una transacción (`@Transactional`) que garantiza rollback total si algo
falla a mitad de camino.

## Modelo de datos

**Product**

| Campo | Tipo | Notas |
|---|---|---|
| `productCode` | `Long` | autogenerado (`SEQUENCE`) |
| `name` | `String` | obligatorio |
| `brand` | `String` | obligatorio |
| `price` | `Double` | ≥ 0 |
| `stock` | `Double` | validado contra el pedido de venta con `InsufficientStockException` |

**Client**

| Campo | Tipo | Notas |
|---|---|---|
| `clientId` | `Long` | autogenerado (`SEQUENCE`) |
| `name` | `String` | obligatorio |
| `lastName` | `String` | obligatorio |
| `dni` | `String` | obligatorio |

**Sale**

| Campo | Tipo | Notas |
|---|---|---|
| `saleId` | `Long` | autogenerado (`SEQUENCE`) |
| `saleDate` | `LocalDate` | asignada por el servidor al crear |
| `amount` | `Double` | calculado como la suma de los subtotales de sus `SaleItem` |
| `saleItems` | `List<SaleItem>` | `@OneToMany`, cascada completa |
| `client` | `Client` | `@ManyToOne` — muchas ventas pueden pertenecer al mismo cliente |

**SaleItem** _(entidad agregada, no pedida explícitamente por la consigna)_

Representa cada línea de una venta — necesaria porque la relación Venta↔Producto no es
un simple muchos-a-muchos: cada línea tiene sus propios datos (cantidad, precio al
momento de la venta, subtotal), que no le pertenecen ni al producto ni a la venta en sí.

| Campo | Tipo | Notas |
|---|---|---|
| `saleItemId` | `Long` | autogenerado (`SEQUENCE`) |
| `sale` | `Sale` | `@ManyToOne` |
| `product` | `Product` | `@ManyToOne` |
| `quantity` | `Double` | |
| `unitPrice` | `Double` | |
| `subtotal` | `Double` | |

## Endpoints

**Productos** — base path `/products`

| Método | Ruta | Descripción | Éxito | Errores |
|---|---|---|---|---|
| `POST` | `/create` | Crea un producto | `201 Created` | `400 Bad Request` |
| `GET` | `/{id}` | Obtiene un producto por código | `200 OK` | `404 Not Found` |
| `GET` | `` | Lista todos los productos | `200 OK` | — |
| `PATCH` | `/update/{id}` | Actualiza parcialmente un producto | `200 OK` | `400`, `404` |
| `DELETE` | `/delete/{id}` | Elimina un producto | `204 No Content` | — |
| `GET` | `/low_stock` | Productos con stock por debajo del umbral | `200 OK` | — |

**Clientes** — base path `/clients`

| Método | Ruta | Descripción | Éxito | Errores |
|---|---|---|---|---|
| `POST` | `/create` | Crea un cliente | `201 Created` | `400 Bad Request` |
| `GET` | `/{id}` | Obtiene un cliente por id | `200 OK` | `404 Not Found` |
| `GET` | `` | Lista todos los clientes | `200 OK` | — |
| `PATCH` | `/update/{id}` | Actualiza parcialmente un cliente | `200 OK` | `400`, `404` |
| `DELETE` | `/delete/{id}` | Elimina un cliente | `204 No Content` | — |

**Ventas** — base path `/sales`

| Método | Ruta | Descripción | Éxito | Errores |
|---|---|---|---|---|
| `POST` | `/create` | Registra una venta (con sus líneas de producto) | `201 Created` | `400 Bad Request` |
| `GET` | `/{id}` | Obtiene una venta por código | `200 OK` | `404 Not Found` |
| `GET` | `` | Lista todas las ventas | `200 OK` | — |
| `GET` | `/products/{saleId}` | Productos que componen una venta puntual | `200 OK` | — |
| `GET` | `/amount/{saleDate}` | Monto total vendido en una fecha | `200 OK` | — |
| `GET` | `/highest` | Datos de la venta con el monto más alto | `200 OK` | — |

> Todavía no hay edición ni eliminación de ventas — se van a implementar directamente
> como borrado lógico (ver [Roadmap](#roadmap)), no como `DELETE` físico.

<details>
<summary><strong>Ejemplo — registrar una venta</strong></summary>

`POST /sales/create`

```json
{
  "clientId": 1,
  "saleItems": [
    { "productCode": 1, "quantity": 2, "unitPrice": 500.0, "subtotal": 1000.0 }
  ]
}
```

`201 Created`

```json
{
  "saleId": 4,
  "saleDate": "2026-09-17",
  "amount": 1000.0,
  "saleItems": [
    { "saleItemId": 7, "saleId": 4, "productId": 1, "quantity": 2.0, "unitPrice": 500.0, "subtotal": 1000.0 }
  ],
  "clientId": 1
}
```

</details>

<details>
<summary><strong>Ejemplo — venta con el monto más alto</strong></summary>

`GET /sales/highest`

```json
{
  "saleId": 3,
  "total": 17599.56,
  "productQuantity": 1,
  "clientName": "Cliente1",
  "clientLastName": "Prueba1"
}
```

</details>

<details>
<summary><strong>Ejemplo — stock insuficiente</strong></summary>

`POST /sales/create`

```json
{
  "clientId": 1,
  "saleItems": [
    { "productCode": 1, "quantity": 999999, "unitPrice": 500.0, "subtotal": 999999.0 }
  ]
}
```

`409 Conflict`

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "No hay suficiente Stock del producto con el codigo 1 (Producto de prueba) para la venta solicitada.",
  "instance": "/sales/create"
}
```

Todos los errores de la API (validación, recurso no encontrado, stock insuficiente,
JSON malformado, etc.) siguen el formato estándar
[`ProblemDetail`](https://www.rfc-editor.org/rfc/rfc7807) — mismo contrato de error en
toda la aplicación. La validación de stock se hace **antes** de persistir la venta: si
falla, no queda ningún registro a medio guardar en la base.

Las rutas inexistentes devuelven un `404 Not Found` real (no un `500`): el catch-all
del `GlobalExceptionHandler` detecta cuando la excepción ya trae su propio status code
(interfaz `ErrorResponse` de Spring, como `NoResourceFoundException`) y lo respeta, en
vez de tratar cualquier excepción como un error interno genérico.

</details>

## Cómo correrlo en local

### Opción A — Docker Compose (recomendado)

**Prerrequisitos**: Docker.

```bash
cd bazar
DB_PASSWORD=tu_password docker compose up -d --build
```

Levanta la API **y** su base de datos (MySQL) juntas, con las variables de entorno ya
resueltas. La API queda disponible en `http://localhost:8080`.

### Opción B — manual, sin Docker

**Prerrequisitos**: JDK 25, Maven, MySQL corriendo en `localhost:3306`.

```bash
# 1. Crear la base de datos
mysql -u root -p -e "CREATE DATABASE todocode_bazar;"

# 2. Configurar credenciales (ver sección Configuración)

# 3. Levantar la aplicación
cd bazar
./mvnw spring-boot:run
```

Ambas opciones arrancan con el perfil `dev` (MySQL) por defecto — ver la sección siguiente.

## Perfiles y bases de datos

Mismo esquema que Ferretería: **dos motores de base de datos**, según el perfil de
Spring activo (`spring.profiles.active`), sin cambios de código entre uno y otro:

| Perfil | Motor | Uso previsto | `ddl-auto` |
|---|---|---|---|
| `dev` (default) | MySQL | Desarrollo local, con o sin Docker | `update` |
| `prod` | PostgreSQL | Despliegue | `validate` |

Ambos drivers JDBC (`mysql-connector-j` y `postgresql`) conviven en el `pom.xml` sin
conflicto — Spring Boot resuelve cuál usar según el prefijo de la URL de conexión.

La base de producción corre en **[Neon](https://neon.tech)** (Postgres serverless,
tier gratuito) en vez de la Postgres gestionada de Render — Render permite múltiples
web services gratis por cuenta, pero solo una base Postgres gratuita, y esa expira a
los 30 días si no se pasa a plan pago. Como Ferretería ya usa esa Postgres de Render,
Bazar usa Neon para no competir por el mismo recurso limitado.

La URL de conexión de Neon requiere SSL siempre — el `DB_URL` de producción necesita
el parámetro `?sslmode=require` al final:

```
DB_URL=jdbc:postgresql://<host>.neon.tech/todocode_bazar?sslmode=require
```

## Configuración

`application.properties` no contiene secretos: cada valor sensible se lee primero de
una variable de entorno y cae a un default de desarrollo si no la encuentra
(`${VARIABLE:default}`).

| Propiedad | Variable de entorno | Default (perfil `dev`) |
|---|---|---|
| `spring.profiles.active` | `SPRING_PROFILES_ACTIVE` | `dev` |
| `spring.datasource.url` | `DB_URL` | `jdbc:mysql://localhost:3306/todocode_bazar...` |
| `spring.datasource.username` | `DB_USERNAME` | `root` |
| `spring.datasource.password` | `DB_PASSWORD` | *(vacío)* |
| `app.cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:5500,http://127.0.0.1:5500,https://mdev-repos.github.io` |

El perfil `prod` no define defaults para estas variables a propósito: si falta alguna,
la aplicación falla al arrancar en vez de conectarse silenciosamente a un lugar
equivocado.

## Proyecto relacionado

Frontend de práctica (vanilla HTML/CSS/JS) que consume esta API:
[`front_bazar__curso_todocode`](../front_bazar__curso_todocode).
Gestiona las tres entidades por pestañas (Productos, Clientes, Ventas) más una de
Reportes para las tres consultas específicas, y detecta solo desde dónde se lo sirve
para elegir el backend correspondiente — mismo mecanismo que el front de Ferretería:

- **En local**: clonar el repo del front y levantarlo con un servidor estático propio
  junto con esta API corriendo en la misma máquina → consume el backend local
  automáticamente.
- **https://mdev-repos.github.io/front_bazar__curso_todocode/**: apunta al backend
  ya deployado en producción (Render).

## Roadmap

- [x] CRUD completo de Producto, Cliente y Venta, con arquitectura en capas
- [x] Entidad `SaleItem` agregada por decisión de modelado (relación muchos-a-muchos con datos propios)
- [x] DTOs con `record` + validación (Jakarta Bean Validation)
- [x] `ResponseEntity` con status codes semánticamente correctos
- [x] CORS configurado por ambiente
- [x] Perfiles `dev` (MySQL) / `prod` (PostgreSQL)
- [x] Consultas JPQL propias (bajo stock, productos por venta, monto por fecha, venta más alta)
- [x] Front de consumo propio (Productos/Clientes/Ventas/Reportes), publicado en GitHub Pages
- [x] Manejo centralizado de excepciones (`@ControllerAdvice` + `ProblemDetail`)
- [x] Validación de stock disponible antes de registrar una venta (`InsufficientStockException`)
- [x] Refactor de `SaleService.createSale` — validación fail-fast, transacción atómica (`@Transactional`), persistencia única vía cascade
- [x] Refactor con programación funcional (Streams en mappers y consultas de colecciones)
- [x] Dockerización (`Dockerfile` + `docker-compose.yml`)
- [x] Base de datos de producción en Neon (PostgreSQL serverless)
- [x] Deploy del backend en Render
- [x] Manejo de errores afinado — rutas inexistentes devuelven `404` real, no `500`
- [x] Mensajes de validación en español
- [ ] Modificación de la lógica de eliminado — pasar de `DELETE` físico a borrado lógico (estados) en todos los recursos, incluyendo edición/baja de ventas
- [ ] Tests unitarios e de integración

## Autor

**Matías Mazzitelli**
_Backend Developer — Java / Spring Boot_

[GitHub] https://github.com/mdev-repos · [LinkedIn] https://www.linkedin.com/in/mnm-dev
