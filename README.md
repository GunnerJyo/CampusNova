# CampusNova – Intelligent College Information Assistant

> **Your College. One Conversation.** CampusNova is a polished Java/Spring Boot prototype where **NOVA** answers campus questions through a local, explainable intent engine—not an external AI API.

## Run locally

Prerequisites: Java 8+ and Maven 3.8+.

```bash
mvn spring-boot:run
```

Open `http://localhost:8080`. The app starts with an embedded H2 demo database, requiring no installation. Demo admin credentials are `admin` / `nova2026` (change these before any real deployment).

### MySQL setup

Create a database named `campusnova`, then set `DATABASE_URL=jdbc:mysql://localhost:3306/campusnova`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` before launching. Hibernate creates the tables from the entities. An illustrative normalized schema is in [docs/database.md](docs/database.md).

## Product capabilities

- Premium responsive NOVA conversation interface, suggestions, loading state, history persistence and graceful fallback
- Local keyword/token similarity scoring over database-managed questions, categories and responses
- Admin login, protected FAQ create/update/delete APIs, announcements and analytics
- Seeded, explicitly marked **demo information** for admissions, fees, exams, library, placements, facilities and contacts
- REST APIs: `POST /api/chat`, `GET /api/faqs`, `GET /api/announcements`, `GET /api/history/{session}`, and `/api/admin/*`

## Architecture

```text
Browser UI → REST Controller → Service → Repository → JPA Database
                         ↘ ChatbotService → IntentMatcher (KeywordMatcher) → FAQ knowledge base
```

See [docs/architecture.md](docs/architecture.md), [docs/chatbot-logic.md](docs/chatbot-logic.md), [docs/api.md](docs/api.md), and [docs/oop-concepts.md](docs/oop-concepts.md).

## OOP demonstration

| Concept | CampusNova example |
|---|---|
| Classes & objects | `Faq`, `Category`, `ChatHistory`, DTO objects |
| Encapsulation | Private fields plus public getters/setters in entities |
| Abstraction | `IntentMatcher` isolates matching behaviour |
| Polymorphism | `ChatbotService` calls the `IntentMatcher` interface; new strategies can substitute `KeywordMatcher` |
| Interfaces | Spring Data repository interfaces and `IntentMatcher` |
| Collections | Sets for token matching, lists/maps for responses and analytics |
| Exceptions | `ApiExceptionHandler` provides safe error payloads |
| Database | JPA repositories; MySQL-ready configuration |

## Team

Computer Science and Engineering · B.Tech Computer Science and Business System · Third Semester · PBCSCT304 Object Oriented Programming · 2026–27

- Jyothish Nalinakshan — Roll 32, KSD25CSBS032 — Project Lead / Backend
- Abdulla Fawas M H — Roll 1, KSD25CSBS001 — Chatbot Logic
- Devadath E K — Roll 18, KSD25CSBS018 — Database
- Arun Sourav K — Roll 13, KSD25CSBS013 — Frontend / UI
- Abhinandh M — Roll 3, KSD25CSBS003 — Testing & Documentation

## Testing and limitations

Run `mvn test`. The included matcher test establishes deterministic local intent matching. Demo login verification uses BCrypt; a production rollout should use database-backed users, durable token/session storage, HTTPS, a secret manager, and an institutional data approval workflow. Screenshot placeholders: home/chat, knowledge base, admin analytics.
