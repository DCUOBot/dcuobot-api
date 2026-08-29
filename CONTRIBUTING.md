# Contributing to dcuobot-api

Thanks for wanting to contribute! This covers setup and conventions specific
to this repo. For the general workflow (branching, PR process, code of
conduct), see the [org-wide CONTRIBUTING.md](https://github.com/DCUOBot/.github/blob/main/CONTRIBUTING.md).

## Prerequisites

- JDK (see `pom.xml` for the required version)
- Maven
- IntelliJ IDEA recommended (not required)

## Setup

1. Clone the repo:
   ```
   git clone https://github.com/DCUOBot/dcuobot-api.git
   ```
2. Copy `.env.example` to `.env` and fill in the required values.
3. Build the project:
   ```
   mvn clean install
   ```
4. Run it locally:
   ```
   mvn spring-boot:run
   ```

## Project structure

This project is organized **package-by-feature**, not package-by-layer. Each
feature owns its own controller, service, and DTOs:

```
src/main/java/com/dcuobot/api/
├── character/
│   ├── api/
│   │   └── CharacterApi.java
│   ├── control/
│   │   └── CharacterService.java
│   └── dto/
├── league/
│   ├── api/
│   │   └── LeagueApi.java
│   ├── control/
│   │   └── LeagueService.java
│   └── dto/
```

Within each feature package:
- **`api/`** — controllers, named with an `Api` suffix (e.g. `CharacterApi`,
  `LeagueApi`)
- **`control/`** — services (business logic)
- **`dto/`** — request/response DTOs

When adding a new feature, create a new package for it rather than dropping
files into shared `controller/`, `service/`, etc. folders.

## Coding conventions

- **DTOs, not maps.** Request and response bodies use proper DTO classes —
  never generic `Map<String, Object>` payloads.
- **Lombok** is used throughout to reduce boilerplate (`@Getter`, `@Setter`,
  `@Builder`, etc. as appropriate). Prefer it over hand-written
  getters/setters/constructors.
- Keep `Api` classes thin — request handling and validation only. Business
  logic belongs in the `control` (service) layer.
- Follow existing naming and package conventions in the feature you're
  touching rather than introducing a new pattern.

## Testing

- Add or update tests for any new/changed behavior.
- Run the full test suite before opening a PR:
  ```
  mvn test
  ```

## API testing

We use Postman for manually exercising endpoints during development. If you
add new endpoints, consider including example requests in your PR
description (a Postman collection export or just example curl/HTTP calls)
to help reviewers verify behavior.

## Opening a PR

Please fill out the PR template. Make sure:

- [ ] `mvn clean install` succeeds
- [ ] `mvn test` passes
- [ ] New/changed endpoints have corresponding DTOs (not raw maps)
- [ ] New code follows the package-by-feature structure above

## Questions

Open a discussion or issue if anything here is unclear.
