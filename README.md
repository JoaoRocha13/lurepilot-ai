<p align="center">
  <img src="frontend/assets/images/brand/logo.png" alt="LurePilot AI logo" width="280" />
</p>

# LurePilot AI

Local AI fishing copilot built with a Spring Boot backend, PostgreSQL, IPMA weather data and a local LLM through LM Studio.

The product direction is a practical fishing copilot, not just a diary. It helps plan sessions, manage spots and lures, generate AI plan A/B/C recommendations, adapt during a live session, record results and learn from historical data.

## Current Status

The backend MVP is the active focus and is ready to support the first React Native Web screens.

Implemented backend areas:

- Health check
- Fishing spots
- Fish library
- General lure library
- Personal lure box
- Fishing plans
- Fishing plan selected lures
- Fishing sessions with planned, active and finished states
- Session events
- Session lures
- Catches with optional photo metadata
- Catch gallery endpoint for photo-based history navigation
- IPMA weather snapshots
- AI plan recommendations through LM Studio
- AI session adjustment
- AI session review
- Recommendation execution tracking
- Dashboard summary
- Analytics summary
- Practical insights
- Frontend options for dropdowns/comboboxes
- Global API error payloads with stable error codes
- Flyway database migrations
- Postman collection

Initial frontend area:

- React Native Web app shell with main menu, dashboard preview and backend status.

## Stack

Backend:

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven

AI:

- LM Studio running locally
- Model currently used in development: `qwen2.5-7b-instruct`
- When the backend runs directly on Windows, LM Studio should be reachable at `http://localhost:1234/v1`
- When the backend runs in Docker, LM Studio should be reachable at `http://host.docker.internal:1234/v1`

Frontend direction:

- React Native Web first
- iOS/Android later from the same React Native direction
- The old Vite frontend folder exists from early setup, but future frontend development should follow the project specification.

Media direction:

- Static catalogue images can live under `frontend/assets/images`, for example lures, fish, spots and UI images.
- Static image filenames should use `lowercase-kebab-case`, for example `black-bass.png`, `sea-bass.png`, `clear-sky.png` and `app-icon.png`.
- Current frontend asset folders are `brand`, `fish/freshwater`, `fish/saltwater`, `lures`, `lure-actions`, `lure-action-icons`, `placeholders`, `spots`, `ui` and `weather`.
- User-generated photos, such as fish photos taken during a session, should be handled as uploads later and stored through backend-managed paths/URLs instead of being committed as frontend assets.
- The backend now exposes a catch gallery endpoint. Each gallery item includes `catchId` and `sessionId`, so the future frontend menu icon can open the gallery and each photo can navigate back to its fishing session.

## Architecture

The backend follows a layered MVC architecture:

- Controllers handle HTTP requests and responses only.
- Services contain business logic.
- Repositories handle persistence.
- Models/entities represent database tables.
- DTOs are used for request and response payloads.
- Controllers must not access repositories directly.
- Entities must not be exposed directly in API responses.

## Running Locally

Start PostgreSQL:

```bash
docker compose up -d
```

Run backend tests:

```bash
cd backend
./mvnw test
```

Run the backend:

```bash
cd backend
./mvnw spring-boot:run
```

Health check:

```bash
curl http://localhost:8080/api/health
```

## Main API Areas

Base URL:

```text
http://localhost:8080
```

Core:

- `GET /api/health`
- `GET /api/dashboard`
- `GET /api/options`

Planning and fishing data:

- `/api/spots`
- `/api/fish`
- `/api/lure-library`
- `/api/lures`
- `/api/lure-box`
- `/api/plans`
- `/api/plans/{planId}/lures`
- `/api/plans/{id}/context`
- `/api/sessions`
- `/api/sessions/{sessionId}/events`
- `/api/sessions/{sessionId}/lures`
- `/api/sessions/{sessionId}/catches`
- `GET /api/gallery/catches`

AI:

- `POST /api/recommendations/plan`
- `POST /api/recommendations/session-adjustment`
- `POST /api/recommendations/session-review`
- `GET /api/recommendations/plans/{planId}`
- `GET /api/recommendations/plans/{planId}/latest`
- `GET /api/recommendations/sessions/{sessionId}/adjustments`
- `GET /api/recommendations/sessions/{sessionId}/adjustments/latest`
- `GET /api/recommendations/sessions/{sessionId}/reviews`
- `GET /api/recommendations/sessions/{sessionId}/reviews/latest`
- `GET /api/recommendations/{id}/debug`

Tracking:

- `POST /api/recommendations/{recommendationId}/executions`
- `GET /api/recommendations/{recommendationId}/executions`
- `GET /api/plans/{planId}/recommendation-executions`
- `GET /api/sessions/{sessionId}/recommendation-executions`

Weather:

- `GET /api/weather-locations/ipma`
- `GET /api/weather-locations/ipma/search`
- `POST /api/weather-snapshots/plans/{planId}/ipma`
- `POST /api/weather-snapshots/sessions/{sessionId}/ipma`
- `POST /api/weather-snapshots/ipma/coordinates`
- `POST /api/weather-snapshots/ipma/location`
- `GET /api/weather-snapshots/plans/{planId}`
- `GET /api/weather-snapshots/sessions/{sessionId}`
- `GET /api/weather-snapshots/plans/{planId}/latest`
- `GET /api/weather-snapshots/sessions/{sessionId}/latest`

Analytics and insights:

- `GET /api/analytics/summary`
- `GET /api/insights/top-lures`
- `GET /api/insights/best-spots`
- `GET /api/insights/best-conditions`
- `GET /api/insights/recommendation-performance`

## Postman

Import this collection into Postman:

```text
docs/LurePilot_AI.postman_collection.json
```

The collection includes grouped requests for the backend MVP and uses `{{baseUrl}}`, defaulting to:

```text
http://localhost:8080
```

## Demo Data

Sample data is synthetic and used only to demonstrate the dashboard, fishing library, lure library, planning, sessions, insights and analytics flow.

## Project Specification

The living product specification is:

```text
docs/FishLog_AI_Project_Specification.docx
```

Treat it as the source of truth for product direction and roadmap decisions.
