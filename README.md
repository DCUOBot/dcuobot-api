# dcuobot-api

[![CI](https://github.com/DCUOBot/dcuobot-api/actions/workflows/ci.yml/badge.svg)](https://github.com/DCUOBot/dcuobot-api/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=DCUOBot_dcuobot-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=DCUOBot_dcuobot-api)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Spring Boot backend for **DCUOBot**. It proxies and caches [DC Universe
Online](https://www.dcuniverseonline.com/) game data from Daybreak Games'
Census API, so the bot (and anyone else) gets character/league lookups,
rankings, and server status without hitting Census directly on every request.

## Features

- **Census proxy + caching** — character, league/guild, and game data
  lookups backed by an in-memory Caffeine cache, so repeated queries don't
  each trigger a fresh upstream call.
- **Rate limiting** — a per-client token bucket (60 requests/min) protects
  the API from being spammed by a single caller, with client identification
  aware of running behind Cloudflare/Traefik.
- **Resilience** — Census calls are wrapped in a circuit breaker with
  connect/read timeouts, so a slow or down upstream fails fast instead of
  piling up requests.
- **Health check** — `/actuator/health` for uptime monitoring.
- **Tracked schema migrations** — Flyway-managed, versioned schema changes.

## Tech stack

- Java 25, Spring Boot (Web MVC, Data JPA, Cache, Validation, Actuator)
- MariaDB, Flyway
- OpenFeign + Resilience4j (circuit breaker/timeouts) for the Census client
- Caffeine (response caching, rate limit bucket storage)
- Bucket4j (rate limiting)

## API

All endpoints are versioned under `/v1`. Full interactive docs — every
parameter, response schema, and error case — are served by the app itself at
[`/docs`](https://dcuo.bot/api/docs) (or `http://localhost:8080/docs` when
running locally), with the raw OpenAPI document at `/v3/api-docs`.

| Group          | Path prefix       | Description                                              |
|-----------------|--------------------|------------------------------------------------------------|
| Characters      | `/v1/census/characters` | Character lookup/ranking and paperdoll images        |
| Guilds          | `/v1/census/guilds`     | League (guild) lookup and ranking                    |
| Server Status   | `/v1/census/status`     | DCUO game server status                              |
| Game Data       | `/v1/data`              | Static reference data (alignments, power types, artifacts, allies, etc.) used to resolve the ids returned by the endpoints above |
| Health          | `/actuator/health`      | Liveness/health check                                |

## Getting started

```
git clone https://github.com/DCUOBot/dcuobot-api.git
cp .env.example .env   # fill in DATABASE_* and CENSUS_BASE_URL
mvn spring-boot:run
```

See [CONTRIBUTING.md](CONTRIBUTING.md) for the full dev setup, project
structure, and coding conventions.

### Requirements

- JDK 25
- MariaDB **10.10+** — the baseline schema migration uses a collation
  (`utf8mb4_uca1400_ai_ci`) only available from that version onward

### Configuration

Set via environment variables (see `.env.example`):

| Variable            | Description                                  |
|----------------------|-----------------------------------------------|
| `DATABASE_URL`       | JDBC URL for the MariaDB database             |
| `DATABASE_USER`      | Database username                             |
| `DATABASE_PASSWORD`  | Database password                             |
| `CENSUS_BASE_URL`    | Base URL for the Daybreak Census API (including your service ID) |

On first run against a fresh, empty database, Flyway creates the schema.
Against an already-populated database (e.g. one previously managed by
Hibernate's `ddl-auto`), it baselines instead of re-running the migration —
see the migration files under `src/main/resources/db/migration` for details.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

[MIT](LICENSE)
