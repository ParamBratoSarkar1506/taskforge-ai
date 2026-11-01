# Infrastructure

Docker Compose uses PostgreSQL 16, Redis 7, Chroma and the two application containers. The backend owns relational schema evolution through Hibernate for the current foundation. Before production, replace `ddl-auto: update` with Flyway migrations, rotate the JWT secret, use a managed PostgreSQL backup policy, and configure TLS at an ingress.
