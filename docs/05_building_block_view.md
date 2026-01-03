# Building Block View

## Level 1

The Timesheet-Wizard consists of the following Gradle subprojects:

![Static-level-1](assets/static-level-1.drawio.png "Static-level-1")

- **tw-spi**: the service provider interface to be implemented for any cloud specific things. Like e.g. uploading
  timesheets to some cloud storage.
- **tw-core**: the core module that contains the business logic. This subproject is cloud-agnostic.
- **tw-app-aws**: implements the interfaces of tw-spi with AWS specific code and also bundles the cloud-agnostic
  tw-core with AWS specific things to an AWS Lambda function.
- **tw-app-azure**: implements the interfaces of tw-spi with Azure specific code and also bundles the
  cloud-agnostic tw-core with Azure specific things to an Azure function.

## Level 2

The tw-core Gradle subproject consists of two business modules and an anticorruption module.

![Static-level-2](assets/static-level-2.drawio.png "Static-level-2")

- **importing**: the timesheet, that can be fetched via the Clockify-API in json-format, is modelled in a very
  generic way and does not fit my use-cases. Therefor, this module is responsible to fetch the json from Clockify,
  transform it to the domain model of the Timesheet-Wizard and signal a successful import.
- **exporting**: this module is responsible for transforming the model created by `importing` by generating an
  XLSX, PDF or CSV file from that data. The XLSX, PDF and CSV files then are stored in some cloud storage, where they
  are available for a manual download.
- **anticorruption**: this module contains code that maps domain model classes of module `importing` to domain model
  classes
  of module `exporting`.

## Level 3

Both business modules follow a domain-centric Ports & Adapters architecture. As the architecture of both modules is
very similar, only the packages in general are documented here. The subtle differences between the modules will be
visible in the code immediately on the package level.

![Static-level-3](assets/static-level-3.drawio.png "Static-level-3")

## Level 4

- The package `domain.model` is in the centre of the architecture without any dependencies to other parts of the system.
- The entities in the domain-logic are used by the application services ImportService & ExportService in packages
  `importing.domain.service` & `exporting.domain.service` that are responsible for orchestrating the workflow.
- In package `importing.domain.port` & `exporting.domain.port` there are also `port`-interfaces, which are implemented in
  package
  `adapter` to invert dependencies.
- Only `outgoing`-ports are decoupled via an interface, having one corresponding adapter.

![Static-level-4](assets/static-level-4.drawio.png "Static-level-4")
