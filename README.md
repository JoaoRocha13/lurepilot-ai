<p align="center">
  <img src="frontend/assets/images/brand/logo.png" alt="LurePilot AI logo" width="280" />
</p>

# LurePilot AI

Local AI fishing copilot built with a Spring Boot backend, PostgreSQL, Open-Meteo weather data and a local LLM through LM Studio.

The product direction is a practical fishing copilot, not just a diary. It helps plan sessions, manage spots and lures, generate AI plan A/B/C recommendations, adapt during a live session, record results and learn from historical data.

## Current Status

The backend MVP is stable enough to support the first React Native Web screens, and the frontend shell has started.

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
- Catches with optional photo metadata and optional lure-library association
- Catch gallery endpoint for photo-based history navigation, including the lure name/image when recorded
- Open-Meteo weather snapshots with current, daily and hourly forecast data
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
- Main menu entries: Dashboard, Gallery, Spots, Weather, Plans, Session, Lure Box, Library and Profile.
- Menu uses static UI icons from `frontend/assets/images/ui`.
- The first screen consumes `GET /api/health` and `GET /api/dashboard`, with a sample fallback when the backend is unavailable.
- A working `PT / EN` language switch exists in the sidebar for the initial UI shell.
- The library switches between fish and lure catalogues in a three-column visual grid, with image-based detail views and create, edit and delete flows.
- Fish catalogue entries support freshwater/saltwater classification, an evident environment filter, image selection, strike-zone/common-zone choices and favorite lures from the catalogue.
- Lure catalogue entries support image selection, difficulty/effectiveness comboboxes with visual score bars, and one-to-one action icon/large action image associations.
- The gallery supports creating, editing and deleting captures from the React Native Web interface. Species are selected from the fish library, lures from the lure library, and each capture remains linked to its fishing session.
- Gallery detail prioritizes the catch image and shows only size, optional weight, session, spot and the selected lure; there is no video section.
- Spots use a zoomable and draggable OpenStreetMap picker, Freshwater/Saltwater and fish-library multi-select comboboxes, and image-based location-type selection (reservoir, river, lake, coast or other).
- Spot cards present favorite species as individual image chips, and the create/detail views keep the focus on the location, water type, spot type, coordinates and species instead of showing description or created-at metadata.
- The Spots header follows the visual language of Gallery and Lure Box, with clearer spacing, counters and a primary create action; the Spots screen no longer uses a search bar.
- The Spots tab stays focused on spot discovery and details; weather and solunar data live in the dedicated Weather tab and remain available to the AI Planner context.
- The Plans screen uses date/time selectors, fish-library multi-select targets including "Any species", optional selected lures or the full Lure Box, and a compact A/B/C workspace header.
- The frontend AI Planner calls the local LM Studio backend, displays the validated recommendation and confidence, allows saving the recommendation, and highlights the feature as Powered by AI. Its structured context includes the latest weather snapshot, solunar phase/illumination and major/minor activity windows when the spot has coordinates.
- Plan detail separates trip context, AI situation read, confidence, A/B/C strategy, recommended lures and warnings for easier scanning.

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
- The current Vite frontend is being used as the React Native Web host, with `react-native` aliased to `react-native-web`.

Media direction:

- Static catalogue images can live under `frontend/assets/images`, for example lures, fish, spots and UI images.
- Static image filenames should use `lowercase-kebab-case`, for example `black-bass.png`, `sea-bass.png`, `clear-sky.png` and `app-icon.png`.
- Current frontend asset folders are `brand`, `fish/freshwater`, `fish/saltwater`, `lures`, `lure-actions`, `lure-action-icons`, `placeholders`, `spots`, `ui` and `weather`.
- UI navigation icons live in `frontend/assets/images/ui` and are imported directly by the React Native Web shell.
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

Run the frontend:

```bash
cd frontend
npm install
npm run dev
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

- `/api/spots` (including `spotType`: `RESERVOIR`, `RIVER`, `LAKE`, `ESTUARY`, `COAST` or `HARBOR`)
- `favoriteSpecies` remains a comma-separated backend field for compatibility; the frontend exposes it as a fish-library multi-select and renders each species as an individual chip with its image.
- The Spots screen uses a draggable OpenStreetMap picker with zoom from `Z4` to `Z18`.
- The Weather tab shows current and apparent temperature, humidity, daily minimum/maximum temperature, rain probability, precipitation, pressure, cloud cover, wind speed/direction/gusts, sunrise/sunset, hourly forecast data, forecast date and forecast coordinates. Its visual icon dynamically uses the clear-sky, cloudy, rain, fog or wind asset according to the Open-Meteo weather code and available conditions.
- The Dashboard weather panel uses a single district combobox inside the weather card. Selecting a district searches Portuguese forecast locations through `GET /api/weather-locations/search`, and the first matching location loads a fresh snapshot with `POST /api/weather-snapshots/location`.
- Weather snapshots use the selected coordinates directly, so they are not limited to districts or a fixed list of administrative locations. The API also returns the nearest matching forecast date when a requested date is outside the available forecast window.
- `/api/fish`
- `/api/lure-library`
- `/api/lures`
- `/api/lure-box` (personal inventory with library-linked type, optional color/size and item photo)
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
- `POST /api/recommendations/{recommendationId}/save`
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

- `GET /api/weather-locations/search?query=Lisboa&countryCode=PT`
- The Weather menu presents current conditions, daily extremes, hourly forecast, sunrise/sunset and a solunar forecast for a saved spot and selected date.
- Weather icons are selected from the local clear-sky, cloudy, rain, fog and wind assets using the Open-Meteo weather code and available conditions.
- `POST /api/weather-snapshots/plans/{planId}`
- `POST /api/weather-snapshots/sessions/{sessionId}`
- `POST /api/weather-snapshots/coordinates`
- `POST /api/weather-snapshots/location`
- `GET /api/weather-snapshots/plans/{planId}`
- `GET /api/weather-snapshots/sessions/{sessionId}`
- `GET /api/weather-snapshots/plans/{planId}/latest`
- `GET /api/weather-snapshots/sessions/{sessionId}/latest`
- `GET /api/solunar/spots/{spotId}?date=YYYY-MM-DD`
- Solunar responses include moon phase, illumination, moonrise/moonset and major/minor activity windows calculated from the spot coordinates.

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
