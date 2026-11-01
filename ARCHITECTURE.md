# TaskForge AI Architecture

TaskForge AI is a modular monolith: a React/Vite client talks to a stateless Spring Boot API. PostgreSQL is the source of truth; Redis is used for rate limiting and short-lived AI-job state; Chroma stores document vectors. This preserves a straightforward local deployment while keeping external seams explicit.

## Key decisions

- **Java 21 / Spring Boot modular monolith.** Controllers are thin; services own use-cases and repositories own persistence.
- **JWT + server-side authorization.** `@PreAuthorize` and ownership/team checks protect every mutating resource. The SPA's role-aware navigation is only a convenience.
- **LLM port.** `LlmProvider` has OpenAI and deterministic mock implementations. Provider selection is configuration-based and every response is validated before use.
- **Human confirmation.** AI analysis creates proposals only. Users explicitly confirm before tasks are persisted.
- **RAG grounding.** Documents are extracted, chunked, indexed through a vector-store port, then answers cite retrieved chunks. When retrieval is empty the assistant says so.

## Modules

`auth`, `task`, `document`, `intelligence`, `workflow`, `approval`, `notification`, `audit`, and `dashboard` are package-level modules. Persistence entities are never returned from HTTP endpoints.

## Data and request flow

```text
React SPA -> REST API -> application services -> JPA/PostgreSQL
                                |-> Redis (rate limits/jobs)
                                |-> LLM provider (OpenAI or mock)
                                `-> Chroma vector-store port
```

The initial release is deliberately a vertical slice. The schema already models teams, projects, dependencies, documents, workflows, approvals, notifications, audit events, and AI jobs; feature expansion builds on those constraints rather than reworking the data model.
