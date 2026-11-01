# API Contract Notes

All application errors use:

```json
{"success":false,"message":"Human-readable message","errorCode":"MACHINE_CODE","timestamp":"2026-01-01T00:00:00Z"}
```

Authenticate bearer-protected endpoints using the token returned by login. OpenAPI is generated at `/v3/api-docs`; Swagger UI is served at `/swagger-ui/index.html`.

AI analysis returns a validated shape containing a summary, category, bounded score, priority, optional deadline, task proposals and reasons. It is a proposal endpoint: it never writes tasks, preserving the human confirmation boundary.
