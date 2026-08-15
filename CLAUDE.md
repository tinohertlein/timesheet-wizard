# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Key principles

Keep your replies extremely concise and focus on everything conveying the key information. No unnecessary fluff, no long code snippets.

Whenever working with any third-party library or something similar, you MUST look up the official documentation to ensure that you're working with up-to-date information. Use the DocsExplorer subagent for efficient documentation lookup.


## What this is

Timesheet-Wizard fetches timesheets from Clockify and exports them as XLSX, PDF, CSV and JSON. It's written in
Kotlin/Gradle and deployed as a serverless function to three different clouds, plus a local CLI variant — a
deliberate exercise in keeping business logic decoupled from any specific cloud or web framework.

## Commands

Requires Java 21 (`.sdkmanrc` pins `21.0.10-amzn`) and Docker (Testcontainers-based tests).

- `./gradlew build` — full build (all modules)
- `./gradlew check` — what CI runs on every push (compile + test + archTest for all modules); use this to verify
  changes before considering a task done
- `./gradlew :tw-core:test` — unit tests for one module
- `./gradlew :tw-core:test --tests "dev.hertlein.timesheetwizard.core.importing.SomeClassTest"` — a single test class
- `./gradlew :tw-core:archTest` — architecture rules only (see below)
- `./gradlew :tw-app-local:shadowJar` then `java -jar tw-app-local/build/libs/*.jar` — run the local CLI variant

Versioning is automatic via `com.github.jmongard.git-semver-plugin` (git tags), not hand-edited.

## Architecture

Six Gradle subprojects (`settings.gradle.kts`): `tw-spi`, `tw-core`, and four deployable apps —
`tw-app-aws`, `tw-app-azure`, `tw-app-gcp`, `tw-app-local`.

```
tw-app-aws / tw-app-azure / tw-app-gcp / tw-app-local   (cloud/framework-specific glue + entry point)
              ↓ implements                    ↓ depends on
           tw-spi                          tw-core
   (service-provider interfaces,      (all business logic;
    e.g. Repository, ClockifyConfig)   cloud- and framework-agnostic)
```

- **tw-spi**: interfaces the apps implement for cloud-specific concerns (e.g. `Repository` for storage,
  `ClockifyConfig`). Has no dependency on `tw-core`.
- **tw-core**: all business logic. Depends only on `tw-spi`. This is where nearly all code changes happen.
- **tw-app-aws**: AWS Lambda, plain SDK, packaged as a zip (`packageJar` task), provisioned with CloudFormation/SAM.
- **tw-app-azure**: Spring Boot + Spring Cloud Function on Azure Functions, provisioned with Azure Bicep.
- **tw-app-gcp**: Quarkus on Google Cloud Functions, provisioned with Terraform.
- **tw-app-local**: no cloud — a Clikt CLI packaged as a shadow jar (`java -jar`), for running everything locally.

### tw-core package structure (enforced by ArchUnit)

`tw-core/src/main/kotlin/.../core` has exactly three top-level packages, checked by
`tw-core/src/archTest/kotlin/.../core/ArchitectureTest.kt`:

- **importing**: fetches the Clockify JSON report and transforms it into the domain model.
- **exporting**: turns the domain model into XLSX/PDF/CSV/JSON and stores the files.
- **anticorruption**: the only package allowed to depend on both `importing` and `exporting`; maps between their
  domain models/events so the two stay independent of each other.

`importing` and `exporting` must never depend on each other directly — only `anticorruption` may bridge them
(they communicate via events). Both `importing` and `exporting` internally follow Ports & Adapters:
`domain.model` / `domain.service` / `domain.port` / `domain.adapter`, where adapters implement outgoing ports and
are never depended on by domain code. `ArchitectureTest.kt` enforces both the module boundary and the per-module
layering — a violation fails `./gradlew check`, not just review. When adding code, put it in the matching layer
rather than reaching across, and run `:tw-core:archTest` after cross-package or cross-layer changes.

### Testing setup in tw-core

- Regular unit tests: `src/test`.
- `src/testFixtures` holds shared E2E test infrastructure (`AbstractApplicationE2ETest`, `AbstractE2ESteps`,
  fixture JSON under `resources/e2e`) that the app modules' own tests consume via
  `testImplementation(testFixtures(project(":tw-core")))`.
- `src/test/kotlin/features` contains Cucumber step definitions/features exercising import→export end-to-end.
- Testcontainers (mockserver, localstack, azure, gcp emulators depending on module) back integration-style tests —
  Docker must be running.

## Docs

Full arc42 architecture docs live in `docs/` (`docs/README.md` is the index). `docs/04_solution_strategy.md` and
`docs/05_building_block_view.md` explain the module-boundary rationale in more depth than this file.
