# Glossary

| Term | Definition |
|---|---|
| Timesheet | A record of tracked working hours for a given timeframe, as fetched from Clockify and represented in the Timesheet-Wizard's own domain model (not Clockify's report format). |
| Report | Clockify's own JSON representation of tracked time, fetched via the Clockify API. Input to `importing`; distinct from the `Timesheet` domain model it is transformed into. |
| Import | The process, owned by the `importing` module, of fetching a Report from Clockify and transforming it into the Timesheet domain model. |
| Export | The process, owned by the `exporting` module, of transforming a Timesheet into an XLSX, PDF, CSV or JSON file and storing it. |
| Anticorruption | The `tw-core` package that is the only one allowed to depend on both `importing` and `exporting`; it maps domain model classes and events between them so the two modules stay independent (see [AD2](09_architecture_decisions.md#ad2-importing-and-exporting-as-independent-modules-bridged-only-by-anticorruption)). |
| tw-spi | The Gradle subproject defining the service-provider interfaces (e.g. `Repository`, `ClockifyConfig`) that cloud-specific app modules implement. Has no dependency on `tw-core`. |
| tw-core | The Gradle subproject holding all business logic (`importing`, `exporting`, `anticorruption`). Cloud- and framework-agnostic; depends only on `tw-spi`. |
| App module | One of `tw-app-aws`, `tw-app-azure`, `tw-app-gcp`, `tw-app-scaleway`, `tw-app-local` — implements `tw-spi` for a specific cloud (or the local filesystem) and bundles `tw-core` into a deployable/runnable artifact. |
| Port | An interface in `importing`'s or `exporting`'s `domain.port` package describing an outgoing dependency of the domain (e.g. storing a file). Implemented by an adapter. |
| Adapter | A `domain.adapter` class implementing a port for a specific technology (e.g. an S3-backed storage adapter). Never depended on by domain code — only referenced through its port. |
| Repository | The `tw-spi` interface each app module implements to store generated files and intermediate data in its cloud's storage service (S3, Azure Blob Storage, Google Cloud Storage, Scaleway Object Storage). |
| ClockifyConfig | The `tw-spi` interface each app module implements to supply cloud-specific Clockify credentials/configuration. |
