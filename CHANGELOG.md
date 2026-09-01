# Changelog

## [1.1.0](https://github.com/DCUOBot/dcuobot-api/compare/dcuobot-api-v1.0.1...dcuobot-api-v1.1.0) (2026-09-01)


### Features

* integrate SpringDoc OpenAPI support and configure API metadata ([#59](https://github.com/DCUOBot/dcuobot-api/issues/59)) ([714220c](https://github.com/DCUOBot/dcuobot-api/commit/714220c66d76cdd29ca89563af7dc8ab376b3d87))

## [1.0.1](https://github.com/DCUOBot/dcuobot-api/compare/dcuobot-api-v1.0.0...dcuobot-api-v1.0.1) (2026-09-01)


### Bug Fixes

* correct Dockerfile build (previous commit was mistyped as refactor) ([5047403](https://github.com/DCUOBot/dcuobot-api/commit/5047403821c22190ed07db84d6b3140d224ad557))

## [1.0.0](https://github.com/DCUOBot/dcuobot-api/compare/dcuobot-api-v0.1.0...dcuobot-api-v1.0.0) (2026-09-01)


### Features

* add character images endpoint ([#22](https://github.com/DCUOBot/dcuobot-api/issues/22)) ([ed0ed10](https://github.com/DCUOBot/dcuobot-api/commit/ed0ed1053565a32659de10eecd8becc3fa85b71e))
* add character lookup ([#18](https://github.com/DCUOBot/dcuobot-api/issues/18)) ([e5f2c5a](https://github.com/DCUOBot/dcuobot-api/commit/e5f2c5a20351b3f9f6f06e79c3a04d515dd37e0d))
* add characters ranking endpoint ([#28](https://github.com/DCUOBot/dcuobot-api/issues/28)) ([03f4cbc](https://github.com/DCUOBot/dcuobot-api/commit/03f4cbcbb3852d857a911fbc58fec5a9b6245db6))
* add circuit breaker and fallback mechanism for CensusClient integration ([#40](https://github.com/DCUOBot/dcuobot-api/issues/40)) ([f23268a](https://github.com/DCUOBot/dcuobot-api/commit/f23268a404eb91b4ffc75b529a41fc2f8fe92294))
* add deploy workflow ([#52](https://github.com/DCUOBot/dcuobot-api/issues/52)) ([7ffdf49](https://github.com/DCUOBot/dcuobot-api/commit/7ffdf49e6d52d8a265c612f5ea3f5cbf216dec0a))
* add gamedata endpoints for static reference data ([#14](https://github.com/DCUOBot/dcuobot-api/issues/14)) ([27b883a](https://github.com/DCUOBot/dcuobot-api/commit/27b883a9cbaf4b25c31598c447b2ac5f7ceb2708))
* add global exception handler for consistent API error responses ([#24](https://github.com/DCUOBot/dcuobot-api/issues/24)) ([cf2dfbb](https://github.com/DCUOBot/dcuobot-api/commit/cf2dfbb398767b134a6db5f002a8e204ed3efa27))
* add league lookup endpoint ([#30](https://github.com/DCUOBot/dcuobot-api/issues/30)) ([378cb4b](https://github.com/DCUOBot/dcuobot-api/commit/378cb4b021dcc188788509f25ae41f6454c07682))
* add leagues ranking ([#32](https://github.com/DCUOBot/dcuobot-api/issues/32)) ([58bb7b9](https://github.com/DCUOBot/dcuobot-api/commit/58bb7b9bb4de32c1c17a41a2ee629904a5b56853))
* add server status endpoint ([#34](https://github.com/DCUOBot/dcuobot-api/issues/34)) ([e98f348](https://github.com/DCUOBot/dcuobot-api/commit/e98f348cae1f795770bf4f240f977a9934d10f78))
* configure CORS to allow all origins and methods ([#20](https://github.com/DCUOBot/dcuobot-api/issues/20)) ([69becdf](https://github.com/DCUOBot/dcuobot-api/commit/69becdf979abf9e0690ca88012aa4209618527f4))
* exempt actuator endpoints from rate limiting ([#42](https://github.com/DCUOBot/dcuobot-api/issues/42)) ([e644343](https://github.com/DCUOBot/dcuobot-api/commit/e644343e68deb6e28912705955fbd1225559898a))
* externalize Census API base URL to `.env` configuration ([#43](https://github.com/DCUOBot/dcuobot-api/issues/43)) ([94bae37](https://github.com/DCUOBot/dcuobot-api/commit/94bae3734a638b6be769f8ea6103cf9096ea1b5c))
* handle `GuildNotFoundException` in GlobalExceptionHandler ([378cb4b](https://github.com/DCUOBot/dcuobot-api/commit/378cb4b021dcc188788509f25ae41f6454c07682))
* implement API rate limiting with `RateLimitFilter` ([#38](https://github.com/DCUOBot/dcuobot-api/issues/38)) ([f505217](https://github.com/DCUOBot/dcuobot-api/commit/f505217875574535a14017199c3c31b6e9cc210c))
* implement caching with `@Cacheable` and Caffeine ([#36](https://github.com/DCUOBot/dcuobot-api/issues/36)) ([b7370c7](https://github.com/DCUOBot/dcuobot-api/commit/b7370c761d18353abd34b80a5aabd251f23ae17a))
* initialize baseline database schema and configure Flyway migrations ([#46](https://github.com/DCUOBot/dcuobot-api/issues/46)) ([28055f0](https://github.com/DCUOBot/dcuobot-api/commit/28055f06a629b28555a3d500416757b8e75a6435))


### Bug Fixes

* reorganize and adjust Feign and Census configuration in `application.yml` ([#44](https://github.com/DCUOBot/dcuobot-api/issues/44)) ([4493296](https://github.com/DCUOBot/dcuobot-api/commit/449329606d5640d405cf94865082be37a04109dc))
* restrict allowed CORS methods to `GET` and `OPTIONS` ([#47](https://github.com/DCUOBot/dcuobot-api/issues/47)) ([4f8b477](https://github.com/DCUOBot/dcuobot-api/commit/4f8b477a50cee89ca9c0df9342e0a0bd06002792))


### Documentation

* enhance README with detailed API overview, tech stack, and setup guide ([#50](https://github.com/DCUOBot/dcuobot-api/issues/50)) ([4309ecc](https://github.com/DCUOBot/dcuobot-api/commit/4309ecc6117b84e937d127feeab83fb7fb6a8ca7))


### Miscellaneous Chores

* release 1.0.0 ([#53](https://github.com/DCUOBot/dcuobot-api/issues/53)) ([4abe658](https://github.com/DCUOBot/dcuobot-api/commit/4abe6583b03be9a2e0212d87727568bacaf170b9))

## 0.1.0 (2026-08-29)


### Bug Fixes

* exclude Dependabot from triggering SonarCloud scans ([#9](https://github.com/DCUOBot/dcuobot-api/issues/9)) ([ff47602](https://github.com/DCUOBot/dcuobot-api/commit/ff476027a11baead71a35aeec5ee6f08ecf09dea))


### Miscellaneous Chores

* set initial release version ([#11](https://github.com/DCUOBot/dcuobot-api/issues/11)) ([8833be9](https://github.com/DCUOBot/dcuobot-api/commit/8833be9e69f99814bbdf66c2e651467778c8ffa0))
