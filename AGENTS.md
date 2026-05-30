\# LurePilot AI - Codex Instructions



\## Project goal



Build LurePilot AI, a local AI-powered fishing copilot.



The application helps the user:

\- plan fishing sessions;

\- manage fishing spots;

\- manage their lure box;

\- generate a practical plan A/B/C using a local LLM;

\- adapt strategy during a fishing session;

\- register session results;

\- build historical data for future analytics and ML.



This is not just a fishing diary. The main product direction is a fishing copilot/planner.



\## Stack



Backend:

\- Java 21

\- Spring Boot

\- Spring Web

\- Spring Data JPA

\- PostgreSQL

\- Maven



Frontend:

\- React

\- Vite

\- Axios

\- React Router



Infrastructure:

\- Docker Compose

\- PostgreSQL container

\- LM Studio running on the host machine



Local LLM:

\- LM Studio

\- Model: qwen2.5-7b-instruct

\- API base URL when backend runs in Docker: http://host.docker.internal:1234/v1

\- API base URL when backend runs directly on Windows: http://localhost:1234/v1



\## MVP rules



Work incrementally. Do not build the entire project at once.



First priority:

1\. Create backend Spring Boot project.

2\. Create frontend React + Vite project.

3\. Add docker-compose.yml with PostgreSQL.

4\. Add backend health check endpoint.

5\. Add basic connection to PostgreSQL.

6\. Keep everything runnable locally.



Do not implement authentication in the first step.

Do not implement Python/ML yet.

Do not implement web search or RAG yet.

Do not over-engineer.



\## Backend package structure



Use this structure:



\- controller

\- service

\- repository

\- model

\- dto

\- config

\- client



Use English for code, classes, methods, variables and database fields.



\## Core domain entities planned



Initial entities:

\- FishingSpot

\- Lure

\- FishingPlan

\- FishingSession

\- SessionEvent

\- Catch

\- AiRecommendation

\- WeatherSnapshot



User/auth can be added later.



\## AI behavior



The LLM should not be treated as the source of truth.



The backend should prepare structured context and send it to LM Studio.



The LLM should:

\- answer in Portuguese from Portugal;

\- give practical recommendations;

\- create a plan A/B/C;

\- rank available lures;

\- explain reasoning briefly;

\- avoid illegal, dangerous or unrealistic recommendations;

\- avoid inventing data.



\## Commands



Backend:

\- ./mvnw test

\- ./mvnw spring-boot:run



Frontend:

\- npm install

\- npm run dev

\- npm run build



Docker:

\- docker compose up --build



\## Development expectations



Keep the project simple and clean.

Prefer working software over complex abstractions.

After each major change, explain what changed and how to test it.

