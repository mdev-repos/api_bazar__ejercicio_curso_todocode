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
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-prod%20(pendiente)-336791?logo=postgresql&logoColor=white)
![Status](https://img.shields.io/badge/status-en%20desarrollo-yellow)

**🖥️ Front (GitHub Pages)**: https://mdev-repos.github.io/front_bazar__curso_todocode/
_(el backend todavía corre solo en local — hasta que se complete el deploy, el front
público va a mostrar error de conexión; ver [Next To-Do](#next-to-do))_

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
- [Next To-Do](#next-to-do)
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
| Base de datos | MySQL (desarrollo) · PostgreSQL (preparado para producción, deploy pendiente) |
| Validación | Jakarta Bean Validation |
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
Service            → lógica de negocio, orquesta Mapper + Repository (y otros Services)
   │
   ├─▶ Mapper       → traduce Entity ⇄ DTO
   │
   ▼
Repository (Spring Data JPA) → persistencia, incluidas queries JPQL propias
   │
   ▼
MySQL (dev) / PostgreSQL (prod, pendiente)
```

```
src/main/java/com/mdev/bazar
├── config          # configuración transversal (CORS)
├── controller       # capa REST — solo DTOs y ResponseEntity
├── dto
│   ├── request        # contratos de entrada (records, con validación)
│   └── response         # contratos de salida (records)
├── mapper             # Entity ⇄ DTO
├── model
├── repository         # Spring Data JPA + queries JPQL propias
└── service
    └── impl
```

Un `Service` puede invocar a **otro Service** (nunca al repository de otro dominio
directamente) cuando necesita datos que no le pertenecen — por ejemplo, `SaleService`
le pide productos a `ProductService` y clientes a `ClientService` al armar una venta,
en vez de acceder a sus repositories.

## Modelo de datos

**Product**

| Campo | Tipo | Notas |
|---|---|---|
| `productCode` | `Long` | autogenerado (`SEQUENCE`) |
| `name` | `String` | obligatorio |
| `brand` | `String` | obligatorio |
| `price` | `Double` | ≥ 0 |
| `stock` | `Double` | sin validación de mínimo todavía (ver Next To-Do) |

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
> como borrado lógico (ver [Next To-Do](#next-to-do)), no como `DELETE` físico.

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

## Cómo correrlo en local

Por ahora, sin Docker (pendiente, ver [Next To-Do](#next-to-do)).

**Prerrequisitos**: JDK 25, Maven, MySQL corriendo en `localhost:3306`.

```bash
# 1. Crear la base de datos
mysql -u root -p -e "CREATE DATABASE todocode_bazar;"

# 2. Configurar credenciales (ver sección Configuración)

# 3. Levantar la aplicación
cd bazar
./mvnw spring-boot:run
```

Arranca con el perfil `dev` (MySQL) por defecto — ver la sección siguiente.

## Perfiles y bases de datos

Mismo esquema que Ferretería: **dos motores de base de datos**, según el perfil de
Spring activo (`spring.profiles.active`), sin cambios de código entre uno y otro:

| Perfil | Motor | Uso previsto | `ddl-auto` |
|---|---|---|---|
| `dev` (default) | MySQL | Desarrollo local | `update` |
| `prod` | PostgreSQL | Despliegue (todavía no realizado) | `validate` |

Ambos drivers JDBC (`mysql-connector-j` y `postgresql`) conviven en el `pom.xml` sin
conflicto — Spring Boot resuelve cuál usar según el prefijo de la URL de conexión.

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
- **https://mdev-repos.github.io/front_bazar__curso_todocode/**: apuntaría al backend
  deployado en producción — **todavía no existe** (el backend de este proyecto no fue
  deployado aún), así que por ahora esa URL pública no va a poder cargar datos.

## Next To-Do

- [x] CRUD completo de Producto, Cliente y Venta, con arquitectura en capas
- [x] Entidad `SaleItem` agregada por decisión de modelado (relación muchos-a-muchos con datos propios)
- [x] DTOs con `record` + validación (Jakarta Bean Validation)
- [x] `ResponseEntity` con status codes semánticamente correctos
- [x] CORS configurado por ambiente
- [x] Perfiles `dev` (MySQL) / `prod` (PostgreSQL) preparados
- [x] Consultas JPQL propias (bajo stock, productos por venta, monto por fecha, venta más alta)
- [x] Front de consumo propio (Productos/Clientes/Ventas/Reportes), publicado en GitHub Pages
- [ ] Modificación de la lógica de eliminado — pasar de `DELETE` físico a borrado lógico (estados) en todos los recursos, incluyendo edición/baja de ventas
- [ ] Manejador de excepciones centralizado (`@ControllerAdvice`) con excepciones propias
- [ ] Validación de stock disponible antes de registrar una venta (usando el manejador de excepciones de arriba)
- [ ] Dockerización (`Dockerfile` + `docker-compose.yml`)
- [ ] Deploy del backend (Render + PostgreSQL gestionado) y actualización de la URL real en el front
- [ ] Refactor con programación funcional
- [ ] Spring Security (autenticación/autorización)
- [ ] Tests unitarios e de integración

## Autor

**Matías Mazzitelli**
_Backend Developer — Java / Spring Boot_

[GitHub] https://github.com/mdev-repos · [LinkedIn] https://www.linkedin.com/in/mnm-dev
