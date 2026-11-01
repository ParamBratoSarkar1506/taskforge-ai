# Implementation Plan

## Phase 1 — Foundation
- [x] Monorepo, Docker topology, PostgreSQL/Redis/Chroma contracts
- [x] Spring Boot API, JWT authentication, seed users, React shell

## Phase 2 — Work management
- [x] Projects, tasks, lifecycle validation, assignment, dependency guards, dashboard summary

## Phase 3 — Intelligence
- [x] Upload and server-side text extraction, provider abstraction, schema-validated AI proposals, priority explanation

## Phase 4 — Knowledge
- [x] Server-side document chunking and persisted chunk metadata
- [x] Deterministic local embedding provider, cosine retrieval, grounded answer endpoint and source citations
- [ ] Chroma HTTP adapter and OpenAI embedding provider for production vector storage

## Phase 5 — Automation
- [x] Manager-only workflow definition API with ordered steps and SLA/approval configuration
- [x] Approval request/decision workflow, in-app notifications and human-in-the-loop approval UI
- [x] Persisted audit events with admin-only audit-log API
- [x] Workflow instance start/advance endpoints with approval gates and assignment notifications
- [ ] Asynchronous deadline notifications

## Phase 6 — Hardening
- [x] Permission-aware controlled AI assistant with task and knowledge tools
- [ ] Full audit UI, analytics, Redis rate limiter, testcontainers/security test matrix

## Phase 7 — Release
- [x] README, CI definition, Dockerfiles
- [ ] Run complete Docker acceptance test on a machine with Docker and Maven installed

Each completed phase has a build/test checkpoint. The present environment has Java but does not have Maven, npm, or Docker available, so their commands are documented but cannot be truthfully run here.
