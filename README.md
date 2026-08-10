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
- Local image upload endpoint for catch photos, served from backend-managed `/uploads/**` paths
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
- End-to-end AI planning flow verified with LM Studio, including plan generation, session adjustment, session review and recommendation execution feedback

Initial frontend area:

- React Native Web app shell with main menu, dashboard preview and backend status.
- Main menu entries: Dashboard, Gallery, Spots, Weather, Plans, Session, Lure Box and Library. The MVP is intentionally single-user, so there is no profile area or authentication flow yet.
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
- The Dashboard also loads the practical insights endpoints and presents compact cards for top lures, best spots, best conditions and recommendation performance, with a clear empty state when history is still limited.
- Sessions have a dedicated workflow for creating/editing outings, starting and finishing them, recording results and rating the session.
- Sessions linked to a plan inherit spot, planned date/time, target species, water clarity and water level into one context block instead of repeating the plan fields; the session detail focuses on execution, result and catches, with an option to adjust the context when reality differs.
- A plan can start the session workflow directly, with its context preselected in the new session form. Session details also provide a direct link back to the associated plan.
- A session detail shows its catches and supports creating, editing and deleting captures without leaving the session.
- Catch photos are previewed in the browser and uploaded as multipart files only when the capture is saved; the database stores the returned URL instead of a base64 image. The complete Plan -> Fish -> Register -> Learn path has been exercised locally with real backend data.

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
- User-generated photos, such as fish photos taken during a session, are handled through backend-managed upload paths/URLs instead of being committed as frontend assets.
- For local development, uploads use the `uploads` directory. For deployment, set `LUREPILOT_UPLOADS_DIRECTORY` to a mounted persistent volume and set `LUREPILOT_UPLOADS_PUBLIC_BASE_URL` when a reverse proxy or CDN serves the files from another public path.
- Uploads are written with generated names, restricted to JPEG/PNG/WEBP/GIF and limited to 10 MB. The database stores the returned public URL, while the image bytes remain in the configured persistent storage.
- The backend exposes a catch gallery endpoint. Each gallery item includes `catchId` and `sessionId`, so the Gallery menu entry opens the history and each photo can navigate back to its fishing session.

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

Frontend checks:

```bash
npm run lint
npm run build
```

The local verification flow also uses a real LM Studio server with the configured `qwen2.5-7b-instruct` model. The backend test suite passes through Maven; on Windows, use the repository Maven wrapper or the bundled Maven executable if the wrapper script cannot resolve its distribution path.

Health check:

```bash
curl http://localhost:8080/api/health
```

Upload a catch photo manually:

```bash
curl -X POST http://localhost:8080/api/uploads/images -F "file=@C:/path/to/catch.jpg"
```

The response contains a relative `url`, for example `/uploads/uuid.jpg`. Use that URL as `photoUrl` and `photoThumbnailUrl` when testing `POST /api/sessions/{sessionId}/catches` in Postman.

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
- The Dashboard latest-catch panel gives the catch photo visual priority and keeps the complete image visible inside its wider image area.
- Weather snapshots use the selected coordinates directly, so they are not limited to districts or a fixed list of administrative locations. The API also returns the nearest matching forecast date when a requested date is outside the available forecast window.
- `/api/fish`
- `/api/lure-library`
- `/api/lures`
- `/api/lure-box` (personal inventory with library-linked type, optional color/size and item photo)
- `/api/plans`
- `/api/plans/{planId}/lures`
- `/api/plans/{id}/context`
- `/api/sessions`
- `/api/sessions/{id}/start` and `/api/sessions/{id}/finish`
- `/api/sessions/{sessionId}/events`
- `/api/sessions/{sessionId}/lures`
- `/api/sessions/{sessionId}/catches`
- `GET /api/gallery/catches`
- `POST /api/uploads/images` (multipart field `file`, image files up to 10 MB)
- `GET /uploads/{fileName}` (served uploaded image)

Photo storage configuration:

```text
LUREPILOT_UPLOADS_DIRECTORY=/persistent/lurepilot/uploads
LUREPILOT_UPLOADS_PUBLIC_BASE_URL=/uploads
```

The deployment must mount the directory configured by `LUREPILOT_UPLOADS_DIRECTORY`; otherwise uploaded photos are local to the container or host and can be lost when it is recreated.

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
- `GET /api/solunar/coordinates?latitude=38.7223&longitude=-9.1393&locationName=Lisboa&date=YYYY-MM-DD`
- Solunar responses include moon phase, illumination, moonrise/moonset and major/minor activity windows calculated from the spot coordinates.
- The Dashboard keeps the relevant weather and solunar forecast synchronized with the selected forecast location and uses the latest catch image when available.

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

## Local Operation and VPN Access

The supported near-term setup is local operation through a private VPN. The computer remains the host for the Vite frontend, Spring Boot backend, PostgreSQL and LM Studio; the iPhone only opens the frontend URL.

Local PostgreSQL settings can be overridden through a root `.env` file copied from `.env.example`. Backend and frontend URL/AI/upload/log settings have their own examples in `backend/.env.example` and `frontend/.env.example`.

Start the complete local stack from PowerShell:

```powershell
.\scripts\start-local.ps1
```

For one-click startup, install a Desktop shortcut once:

```powershell
.\scripts\install-desktop-shortcut.ps1
```

After that, double-click `LurePilot AI` on the Desktop. It opens Docker Desktop when necessary, waits for PostgreSQL and the backend health check, then opens the app in the browser. The shortcut can be removed with `.\scripts\install-desktop-shortcut.ps1 -Remove`.

The script starts PostgreSQL with Docker Compose, starts the backend and frontend when their ports are free, waits for the backend health endpoint, and writes backend/frontend/Docker logs under `logs/`. It prints the local URL and the URL pattern to use from the iPhone:

```text
http://<VPN-IP-OF-COMPUTER>:5173
```

The helper does not replace a process already using port `8080` or `5173`. If an older backend was started with a different `LUREPILOT_AI_BASE_URL`, stop that process and run the helper again so Windows-direct mode uses `http://localhost:1234/v1`.

If the Maven wrapper is blocked by a local Windows policy, pass an installed Maven executable explicitly, for example `.\scripts\start-local.ps1 -MavenCommand 'C:\Program Files\Apache Maven\bin\mvn.cmd'`.

### iPhone access through Tailscale

The recommended VPN for the current single-user setup is [Tailscale](https://tailscale.com/download/). Install it on Windows and iPhone, sign in with the same account, and keep both devices connected. Tailscale gives the computer a private VPN address, normally in the `100.x.x.x` range. The iPhone does not need direct access to PostgreSQL, Spring Boot or LM Studio: it only connects to the Vite frontend, which proxies `/api` and `/uploads` to the local backend.

Allow the frontend port through Windows Firewall. Run PowerShell as Administrator and limit the rule to the iPhone's Tailscale IP when possible:

```powershell
.\scripts\allow-firewall.ps1 -RemoteAddress <IPHONE-TAILSCALE-IP>
```

Start the app with the Desktop shortcut or with `.\scripts\start-local.ps1`. The script starts Docker/PostgreSQL, backend and frontend, then prints the available computer addresses. On the iPhone, with Tailscale connected, open the computer's Tailscale address:

```text
http://<COMPUTER-TAILSCALE-IP>:5173
```

Example:

```text
http://100.124.231.72:5173
```

The daily workflow is: start LM Studio, load `qwen2.5-7b-instruct` and start its local server; confirm Tailscale is connected; double-click the `LurePilot AI` Desktop shortcut; then open the saved Tailscale URL in Safari. Docker is started by the helper when necessary. The computer must remain powered on and connected to the internet while the iPhone uses the app. LM Studio is not started or loaded automatically by the shortcut.

The backend and LM Studio do not need to be exposed to the iPhone. Vite proxies `/api` and `/uploads` to the local backend, and the backend keeps LM Studio on `localhost:1234`. Do not expose ports `8080`, `1234` or `5432` to the public internet.

Stop processes started by the helper with:

```powershell
.\scripts\stop-local.ps1
```

The app supports importing photos and opening the iPhone photo/camera chooser through the browser file input. For Safari, use the file chooser first. If a future camera/PWA flow uses `getUserMedia` or installation requirements that reject HTTP, create a trusted local certificate with `mkcert`, include the computer VPN IP, and start with HTTPS:

```powershell
.\scripts\create-local-cert.ps1 -HostNames @('localhost', '127.0.0.1', '<VPN-IP-OF-COMPUTER>')
.\scripts\start-local.ps1 -Https
```

Safari must trust the `mkcert` local certificate authority on the iPhone. The current HTTP setup is enough to validate normal navigation and file import; camera APIs that require a secure context need the HTTPS setup. Do not expose the backend or LM Studio directly to the public internet.

## Backups and Recovery

The database and user photos are local state. Create a PostgreSQL dump and an uploads archive with:

```powershell
.\scripts\backup-local.ps1
```

Backups are written to `backups/<timestamp>/`. Keep the `lurepilot.dump` file and `uploads.zip` together. The script keeps the seven most recent backup folders by default. The PostgreSQL container must be running.

Operational failure behavior is intentional:

- if LM Studio is stopped or has no loaded model, AI endpoints return a clear upstream error and the frontend shows a retryable message;
- if Open-Meteo is unavailable, weather and Solunar panels show an unavailable state without deleting saved data;
- if the backend or database is unavailable, dashboard/list screens show loading/error states and retry controls;
- uploaded images are stored under `backend/uploads` and are excluded from Git, so they can be backed up independently.

## End-to-End Verification Checklist

Use the running local stack to verify the real flow:

1. Create a plan with a spot, target species and selected lures.
2. Refresh the plan weather snapshot and open the Solunar forecast.
3. Create and start a fishing session from the plan.
4. Generate the AI recommendation and try a session adjustment.
5. Register a catch with an imported or camera photo.
6. Finish the session with success or failure and its duration.
7. Generate/save/evaluate the recommendation and review the session.
8. Open Gallery and Analytics to confirm the catch, session result and history are present.

For resilience, repeat the AI step with LM Studio stopped, repeat weather with internet unavailable, and restart the local stack after running `backup-local.ps1` to confirm that PostgreSQL data and uploaded images remain available.
