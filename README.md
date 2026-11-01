# TaskForge AI

**From Information to Action.** TaskForge AI converts unstructured enterprise requests into explainable, trackable work. It is a portfolio-grade Spring Boot + React monorepo built around a secure API boundary, role-aware work management, and an LLM provider port.

## What is implemented

- JWT login and registration; passwords are BCrypt hashed and roles are enforced by Spring Security.
- Seeded enterprise workspace with admin, manager, and employee users; projects, priorities, task lifecycle and mandatory dependency completion guard.
- REST dashboard summary and responsive React dashboard/task views.
- Inbox Simulator with real backend analysis: `POST /api/ai/analyze` uses a deterministic development provider or an OpenAI Responses API structured-JSON provider.
- PDF, DOCX and TXT upload with server-side text extraction and file-size/type validation.
- Persisted document chunking, deterministic local embeddings, cosine retrieval, and source-cited grounded knowledge answers.
- Manager-only workflow definition, human approval decisions, in-app notification records, and admin-only audit-log API.
- Controlled assistant endpoint that selects permission-aware task/workload/knowledge tools; workflow instances can start and advance through approval gates.
- Consistent API error envelope, CORS configuration, Swagger UI, Docker Compose topology, and a CI workflow.

## Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for boundaries and rationale, and [IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md) for phased delivery.

```text
React / Vite -> Spring Boot REST -> services -> JPA / PostgreSQL
                               -> LLM provider (OpenAI | deterministic mock)
                               -> Redis / Chroma service boundaries
```

## Live deployment

`localhost:5173` is for local development only. The repository includes a production-aware frontend proxy configuration, a health endpoint at `/actuator/health`, and a [Railway deployment guide](docs/DEPLOY_RAILWAY.md). Public deployment requires a GitHub repository, a hosting account, production secrets, and a generated domain; do not publish development passwords or `.env` files.

## Local setup

Prerequisites: Docker Desktop, or Java 21+ and Node 22+ with Maven and npm.

```bash
cp .env.example .env
docker compose up --build
```

Open `http://localhost:5173`; API Swagger is at `http://localhost:8080/swagger-ui/index.html`.

To run without Docker:

```bash
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

## Demo credentials

| Role | Email | Password |
|---|---|---|
| Admin | `admin@taskforge.demo` | `DemoPass123!` |
| Manager | `manager@taskforge.demo` | `DemoPass123!` |
| Employee | `sam@taskforge.demo` | `DemoPass123!` |

These are synthetic local seed accounts only.

## AI configuration

The default `TASKFORGE_AI_PROVIDER=mock` is deterministic and makes a useful demo without credentials. It uses request text rather than a hard-coded example, returns typed structured data, and exposes its priority reasoning.

Set `TASKFORGE_AI_PROVIDER=openai` and `OPENAI_API_KEY` to use the real OpenAI Responses API. The adapter requests strict JSON Schema output and rejects missing/invalid fields before an analysis reaches application code. Never put the API key in the frontend.

## API highlights

- `POST /api/auth/login`, `POST /api/auth/register`
- `GET|POST /api/tasks`, `PUT /api/tasks/{id}`
- `POST /api/tasks/{id}/dependencies/{dependencyId}`
- `GET /api/dashboard/summary`
- `POST /api/documents` (multipart), `GET /api/documents`
- `POST /api/ai/analyze`
- `POST /api/knowledge/ask`, `GET /api/knowledge/search`
- `GET|POST /api/workflows`, `GET|POST /api/approvals`, `POST /api/approvals/{id}/approve`
- `GET /api/notifications`, `GET /api/audit-logs` (admin only)
- `POST /api/assistant/chat`, `POST /api/workflows/{id}/instances`, `POST /api/workflows/instances/{id}/advance`

## Testing

```bash
cd backend && mvn test
cd frontend && npm run test && npm run build
```

The mock-provider unit tests cover action extraction, priority bounds, urgency scoring and deadlines. CI builds backend, frontend and Docker images on push/PR.

## Remaining roadmap

The current increment includes workflow execution, approvals, notifications, audit persistence, an AI assistant, document library, and local RAG. Release blockers remaining are production Chroma/OpenAI embedding adapters, database migrations, expanded tests, analytics/audit UI, rate limiting, and final GitHub/deployment polish. See the implementation plan for sequence.

## License

MIT. See [LICENSE](LICENSE).
