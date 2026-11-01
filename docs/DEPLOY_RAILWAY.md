# Deploying TaskForge AI to Railway

This project is locally orchestrated with Docker Compose. On Railway, create separate services rather than uploading the Compose file directly:

1. Push this repository to GitHub.
2. Create a Railway project.
3. Add managed **PostgreSQL** and **Redis** services.
4. Add a backend service from this repository:
   - Root directory: `backend`
   - Dockerfile: `Dockerfile`
   - Generate a public domain only if API documentation must be publicly available.
   - Set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` from the managed PostgreSQL reference variables.
   - Set a unique `TASKFORGE_JWT_SECRET` and `CORS_ORIGIN` to the frontend's final URL.
5. Add a frontend service from this repository:
   - Root directory: `frontend`
   - Dockerfile: `Dockerfile`
   - Generate its public domain.
   - Set `BACKEND_UPSTREAM=backend.railway.internal:8080`, replacing `backend` if you choose a different backend service name.
6. Set `TASKFORGE_AI_PROVIDER=mock` for a no-cost public demo, or configure `OPENAI_API_KEY` plus `TASKFORGE_AI_PROVIDER=openai` for live LLM analysis.
7. Open `/actuator/health` on the backend and the generated frontend domain to verify the release.

Railway’s managed Postgres/Redis services should be used instead of running database containers in production. Chroma is currently not required by the deterministic embedding provider; add it only when enabling a Chroma-backed embedding adapter.

Before a commercial/long-lived deployment, replace Hibernate `ddl-auto: update` with reviewed Flyway migrations, rotate demo credentials, set backups, and configure a custom domain.
