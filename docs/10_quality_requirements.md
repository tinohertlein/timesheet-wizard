# Quality Requirements

## Quality tree

See [chapter 1](01_introduction_and_goals.md#quality-goals) for the quality goals (QG1–QG4) this tree refines.

## Quality scenarios

Each scenario follows source → stimulus → environment → response → response measure.

### QG1 — Security

| Nr | Source | Stimulus | Environment | Response | Response measure |
|----|--------|----------|--------------|----------|-------------------|
| QS1.1 | Unauthenticated third party | Attempts to read or trigger the Timesheet-Wizard or its generated files | Production, any cloud | Request is rejected before reaching application code | 0 successful unauthorized accesses; enforced by cloud IAM/policies, not application code |
| QS1.2 | Tino | Downloads a generated file | Production, any cloud | Cloud console requires an authenticated, authorized login | Access denied without valid cloud credentials |

### QG2 — Cost efficiency

| Nr | Source | Stimulus | Environment | Response | Response measure |
|----|--------|----------|--------------|----------|-------------------|
| QS2.1 | Scheduled trigger | Monthly accumulation of function invocations + storage | Production, any cloud | Stays within the free/low-cost tier of the provider | Monthly spend ≤ 10€ per cloud, tracked via the provider's cost-alerting mechanism |

### QG3 — Extensibility

| Nr | Source | Stimulus | Environment | Response | Response measure |
|----|--------|----------|--------------|----------|-------------------|
| QS3.1 | Developer | Adds a new export target format (e.g. Markdown) | Development | New `domain.adapter` in `exporting`; no change needed in `importing` or any app module beyond wiring | Implemented, tested and deployed to one cloud in < 1 working day, excluding the format-generation logic itself |
| QS3.2 | Developer | Adds a new hyperscaler target | Development | New app module implementing `tw-spi`; `tw-core` untouched | `tw-core` has zero diff; new module compiles against existing `tw-spi` interfaces without changing them |
| QS3.3 | Developer | Swaps the web framework of an existing app module (e.g. Quarkus → something else) | Development | Only that app module changes | `tw-core` and `tw-spi` have zero diff |

### QG4 — Testability

| Nr | Source | Stimulus | Environment | Response | Response measure |
|----|--------|----------|--------------|----------|-------------------|
| QS4.1 | CI pipeline | Push to any branch | CI | `./gradlew check` runs unit, architecture and E2E tests for all 7 subprojects | Pipeline is green before merge; no manual test step required |
| QS4.2 | Developer | Changes code across the `importing`/`exporting` boundary | Local/CI | `:tw-core:archTest` fails if the module boundary or Ports & Adapters layering is violated | Violation caught at build time, not in review |
| QS4.3 | Tino | Reviews a generated file for plausibility before book-keeping | Production | Manual, visual check of one file per month | The only manual test step in the whole pipeline; everything else is automated |
