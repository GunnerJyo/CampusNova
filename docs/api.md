# API
- `POST /api/chat` body: `{"question":"library hours","sessionId":"..."}`
- `GET /api/faqs?q=library`, `GET /api/announcements`, `GET /api/history/{sessionId}`
- `POST /api/admin/login` body: `{"username":"admin","password":"nova2026"}` returns a token.
- Send that value as `X-Admin-Token` to `POST /api/admin/faqs`, `PUT /api/admin/faqs/{id}`, `DELETE /api/admin/faqs/{id}`, `GET /api/admin/analytics`.

FAQ request: `{"category":"Library","question":"...","answer":"...","keywords":"..."}`. Admin FAQ changes are stored in the database and automatically become available to NOVA.
