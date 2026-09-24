# Architecture Decisions

This chapter records the decisions with lasting impact on the codebase. Detailed ADRs (context, decision,
consequences) may later move to `docs/adr/` as the [domain-modeling](agents/domain.md) skill creates that structure;
until then they are kept here.

## AD1: Business logic decoupled from cloud/framework via `tw-core` + `tw-spi`

**Decision:** All business logic lives in the cloud-agnostic `tw-core` subproject. Anything cloud- or
framework-specific (storage, Clockify credentials, ...) is expressed as an interface in `tw-spi` and implemented by
each app module (`tw-app-aws`, `tw-app-azure`, `tw-app-gcp`, `tw-app-scaleway`, `tw-app-local`).

**Context:** QG3 (extensibility) requires adding a hyperscaler or swapping a web framework without touching business
logic.

**Consequences:** Five app modules per feature instead of one, but each is a thin wiring layer. `tw-core` has no
dependency on any cloud SDK or web framework. See chapters 4 and 5.

## AD2: `importing` and `exporting` as independent modules, bridged only by `anticorruption`

**Decision:** `tw-core` has exactly three top-level packages — `importing`, `exporting`, `anticorruption` — with
`importing` and `exporting` forbidden from depending on each other directly. `anticorruption` maps domain
model/events between them. `importing` signals a completed import via an event; `exporting` reacts to it.

**Context:** QG3 requires updating or replacing either side (e.g. adding a new export format, or changing how
timesheets are fetched) without the other module being affected.

**Consequences:** An extra indirection layer (events + mapping) for what is otherwise a simple pipeline. Enforced
automatically by `ArchitectureTest.kt` (`:tw-core:archTest`) so the boundary can't erode silently.

## AD3: Ports & Adapters inside `importing` and `exporting`

**Decision:** Both business modules are internally structured as `domain.model` / `domain.service` / `domain.port` /
`domain.adapter`. Only outgoing adapters are decoupled via port interfaces; incoming adapters are not.

**Context:** QG4 (testability) — domain services should be testable without cloud infrastructure or Clockify
reachable; QG3 — outgoing infrastructure (storage, HTTP client) should be swappable per cloud.

**Consequences:** Incoming direction is not abstracted, since there is exactly one caller per module (the
orchestrating service) and no variability to decouple there. Enforced by the same architecture test as AD2.

## AD4: One cloud provider = one deployable app module, provisioned as infrastructure-as-code

**Decision:** Each of AWS, Azure, GCP and Scaleway gets its own Gradle module and its own IaC tool (CloudFormation/SAM,
Bicep, Terraform, Scaleway CLI respectively), rather than one abstraction layer over all four. `tw-app-local` is a
fifth module with no cloud at all, packaged as a shadow jar for local/manual runs.

**Context:** TC1/TC2 (cloud functions, Kotlin) and QG3 — trying different hyperscalers and web frameworks
(Micronaut on Azure, Quarkus on GCP, Spring Boot on Scaleway) is itself a goal of the project (see chapter 1,
Motivation), so a single unifying cloud abstraction would work against the reason the project exists.

**Consequences:** Provisioning logic is duplicated four ways with no shared IaC layer; each cloud's quirks are
handled locally in its own module instead of behind a common abstraction.

## AD5: No infrastructure outside the cloud provider (QG1)

**Decision:** The workflow (scheduler → import → export → storage) runs entirely inside one cloud account. The only
externally reachable action is a manual, authenticated download of a generated file from cloud storage.

**Context:** QG1 (security) — neither the Timesheet-Wizard nor the data it produces should be reachable without
cloud credentials.

**Consequences:** No public API, webhook, or endpoint to trigger or inspect the workflow; access control is
delegated entirely to the cloud provider's IAM/policies rather than implemented in-app.
