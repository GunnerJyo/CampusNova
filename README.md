# CampusNova – Intelligent College Information Assistant

**Your College. One Conversation.** CampusNova is a Spring Boot college assistant whose chatbot, **NOVA**, combines database knowledge retrieval with an optional Amazon Bedrock generation layer. It is deliberately college-focused, grounded, and usable locally without AWS.

## Architecture

```text
Student → Chat API → relevance + conversation context
        → hybrid database retrieval (FAQ / announcements)
        → strong verified match: direct answer
        → contextual match: optional Amazon Bedrock generation
        → grounding validation → response, source labels, history & analytics
```

The FAQ database remains the primary knowledge source. NOVA does not send trivial strong FAQ requests to Bedrock. For less exact questions, it combines token matching, related campus-language families, and recent session context. If enough context is found and Bedrock is enabled, it asks the model to phrase an answer using only that context. If not, NOVA provides a grounded retrieval response or records a **Knowledge Gap**.

## Grounding and scope

- Institutional facts come only from retrieved CampusNova content.
- Missing details produce an explicit “not currently available in the knowledge base” response—never invented fees, dates, contacts, policies, or timings.
- Non-college requests get a polite scope explanation.
- The Bedrock prompt rejects prompt injection, requests for hidden instructions, credentials, and attempts to override NOVA’s role.
- Demo seed information is not official institutional data; verify time-sensitive details with the appropriate office.

## Local setup

Requires Java 17 and Maven 3.9+.

```bash
mvn clean test
mvn spring-boot:run
```

Open `http://localhost:8080`. H2 starts automatically; no database or AWS account is required. By default `BEDROCK_ENABLED=false`, so NOVA uses local hybrid retrieval and safe grounded fallbacks. The demonstration admin account is `admin` / `nova2026`; replace it before any real deployment.

## Optional Amazon Bedrock

Set these only in a secure shell, Render configuration, or secret manager—never source control:

| Variable | Required | Purpose |
|---|---:|---|
| `BEDROCK_ENABLED` | for AI | `true` enables generation |
| `AWS_REGION` | for AI | Region hosting the model |
| `BEDROCK_MODEL_ID` | for AI | Model ID available to the AWS account; configurable without code changes |
| `BEDROCK_KNOWLEDGE_BASE_ID` | no | Reserved for a future managed Knowledge Bases adapter |
| `BEDROCK_GUARDRAIL_ID` / `BEDROCK_GUARDRAIL_VERSION` | no | Optional Bedrock Guardrail |
| `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` | no | MySQL deployment configuration |

Credentials use the AWS SDK v2 default provider chain (for example IAM roles or secure deployment variables). Never commit access keys. The deployment principal needs `bedrock:InvokeModel` for the configured model and Guardrail access where configured; model access must be enabled in the selected region. CampusNova does not create AWS resources or assume a model/Knowledge Base ID exists.

Requests are only sent after retrieval and Bedrock usage can incur AWS charges. Select a model suitable for your budget and monitor AWS usage.

## API and admin

- `POST /api/chat` — `{ "question": "When can I use the library?", "sessionId": "UUID" }`
- `GET /api/faqs`, `GET /api/announcements`, `GET /api/history/{sessionId}`
- `POST /api/admin/login`; use the returned `X-Admin-Token` for admin endpoints.

Chat responses include `responseType` (`DIRECT_FAQ`, `AI_RAG`, `RETRIEVAL_FALLBACK`, `KNOWLEDGE_GAP`, `OUT_OF_DOMAIN`) and safe source labels. Admin analytics includes direct, AI/RAG, unanswered, out-of-domain, and knowledge-gap activity. Add an FAQ for a gap and it immediately becomes retrievable without Java changes.

## Render

The Java 17 multi-stage [Dockerfile](Dockerfile) builds with `mvn clean package -DskipTests`, runs the JAR in a Java 17 image, exposes 8080, and preserves `server.port=${PORT:8080}`. Create a Docker web service and configure database plus optional Bedrock variables in Render. The app remains functional without Bedrock.

## Testing and troubleshooting

`mvn test` is deterministic and makes no live Bedrock calls. It covers direct FAQ routing, natural phrasing with mocked AI, unavailable information, out-of-domain behavior, Bedrock fallback, and conversation context. If Bedrock is misconfigured or unavailable, NOVA logs only an exception class and uses retrieved knowledge when possible; AWS details never reach students.

## OOP design

`KnowledgeSource`, `BedrockService`, and `IntentMatcher` isolate interchangeable implementations. Constructor-injected `CollegeRelevanceService`, `KnowledgeRetrievalService`, `ConversationService`, `PromptService`, `GroundingValidationService`, and `NovaChatService` each have a focused responsibility. Entities encapsulate persistence while controllers remain thin.

## Team

Computer Science and Engineering · B.Tech Computer Science and Business System · Third Semester · PBCSCT304 OOP · 2026–27

- Jyothish Nalinakshan — Project Lead / Backend
- Abdulla Fawas M H — Chatbot Logic
- Devadath E K — Database
- Arun Sourav K — Frontend / UI
- Abhinandh M — Testing & Documentation
