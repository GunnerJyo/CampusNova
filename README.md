# CampusNova – Intelligent College Information Assistant

**Your College. One Conversation.** CampusNova is an AI-powered student information assistant for **L B S College of Engineering, Kasaragod, Kerala**. It uses [lbscek.ac.in](https://lbscek.ac.in/) as its primary institutional source; it is not represented as an official college service.

LBSCEK is a Government of Kerala undertaking affiliated to APJ Abdul Kalam Technological University (KTU). Address: Povval, Muliyar Post Office, Kasaragod, Kerala – 671542.

## Architecture

```text
Student → Chat API → relevance + conversation context
        → hybrid database retrieval (FAQ / announcements)
        → strong verified match: direct answer
        → contextual match: optional Amazon Bedrock generation
        → grounding validation → response, source labels, history & analytics
```

Official LBSCEK records are stored separately with their URL, title, page type, category, import time, active/verified flags, and checksum. An administrator imports the curated core pages through `POST /api/admin/official-knowledge/import-core`; chat messages never crawl the website. The source hierarchy is: current official LBSCEK content, verified admin content, then legacy/internal material. Weak documents are rejected by a relevance gate before Bedrock can see them.

## Grounding and scope

- Institutional facts come only from relevant official LBSCEK content.
- Missing details produce an explicit “not currently available in the knowledge base” response—never invented fees, dates, contacts, policies, or timings.
- Non-college requests get a polite scope explanation.
- The Bedrock prompt rejects prompt injection, requests for hidden instructions, credentials, and attempts to override NOVA’s role.
- Response labels distinguish `VERIFIED_OFFICIAL`, `AI_GROUNDED`, `GENERAL_GUIDANCE`, and `OUT_OF_SCOPE`. General guidance carries no misleading citation and is logged as a knowledge gap.

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
| `COLLEGE_OFFICIAL_DOMAIN` | no | Official LBSCEK import allowlist; defaults to `https://lbscek.ac.in/` |
| `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` | no | MySQL deployment configuration |

Credentials use the AWS SDK v2 default provider chain (for example IAM roles or secure deployment variables). Never commit access keys. The deployment principal needs `bedrock:InvokeModel` for the configured model and Guardrail access where configured; model access must be enabled in the selected region. CampusNova does not create AWS resources or assume a model/Knowledge Base ID exists.

Requests are only sent after retrieval and Bedrock usage can incur AWS charges. Select a model suitable for your budget and monitor AWS usage.

## API and admin

- `POST /api/chat` — `{ "question": "When can I use the library?", "sessionId": "UUID" }`
- `GET /api/faqs`, `GET /api/announcements`, `GET /api/history/{sessionId}`
- `POST /api/admin/login`; use the returned `X-Admin-Token` for admin endpoints.

Chat responses include the accurate response type and only sources actually used. The protected official-import endpoints accept only URLs on the configured official domain. Use `POST /api/admin/official-knowledge/import-core` to refresh the curated official pages, or `POST /api/admin/official-knowledge/import?url=...` for one approved LBSCEK page. Imported HTML is reduced to text and treated as untrusted reference content, never as instructions.

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
