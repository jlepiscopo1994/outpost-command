# Outpost Command — Tactical Roster & Intelligence Service

> A production-grade backend service built with Spring Boot 3 and PostgreSQL (pgvector) to manage combat units and tactical intelligence for *Goddess of Victory: Nikke* and external crossover IPs. Designed as Phase 1 of an eventual event-driven microservices architecture featuring RAG-powered AI agents.

---

## System Architecture (Phase 1 Monolith)

The system follows a clean layered monolithic architecture that cleanly isolates client interfaces, business logic, data access, and storage.

![Phase 1 Architecture](./docs/images/Phase-1.png)

### Architectural Layers Breakdown

1. **Client Tier**
   * Communicates with the service via standard HTTP REST APIs returning standard JSON payloads on port `:8080`.
   * Designed for easy consumption by modern web/mobile frontends, Postman, or Bruno API clients.

2. **Spring Boot 3.x Application Tier**
   * **Controller Layer (`UnitController`, `TacticalLogController`):** Manages inbound HTTP routing, request payload validation (`hibernate-validator`), and HTTP status mapping.
   * **Service & DTO Layer (`Records / Services`):** Encapsulates core business logic, entity-to-DTO conversion using modern **Java Records**, transaction management (`@Transactional`), and validation rules.
   * **Repository Layer (`UnitRepository`, `TacticalLogRepository`):** Abstraction layer over data operations using **Spring Data JPA / Hibernate**.

3. **Database Tier (Dockerized PostgreSQL + pgvector)**
   * Hosted inside a Docker container (`pgvector/pgvector:pg16`) running on port `:5432`.
   * **`units` Table:** Stores unit stats, weapon types, elements, burst types, and crossover metadata (`origin_ip`).
   * **`tactical_logs` Table:** Stores unstructured text logs, strategy guides, and skill notes linked via a `1-to-Many` foreign key relationship to a unit.
   * **AI-Ready (`pgvector`):** Utilizes PostgreSQL with `pgvector` pre-installed to avoid database migrations when converting tactical logs into vector embeddings in Phase 3.

---

## Phase 1 Tech Stack

* **Language:** Java 17+ or Java 21
* **Framework:** Spring Boot 3.x (Spring Web, Spring Data JPA, Validation)
* **Database:** PostgreSQL (`pgvector/pgvector:pg16` Docker Image)
* **ORM:** Hibernate / JPA
* **Build Tool:** Gradle / Maven

---

## Quick Start Guide

### 1. Prerequisites
* **JDK 17** or higher installed.
* **Docker** & **Docker Compose** installed.

### 2. Run Database Container
Spin up the `pgvector` container in the background:
```bash
docker compose up -d
