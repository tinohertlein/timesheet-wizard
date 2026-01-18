# What is the Timesheet-Wizard?

[![Master branch](https://github.com/tinohertlein/timesheet-wizard/actions/workflows/build_deploy_release_master.yml/badge.svg)](https://github.com/tinohertlein/timesheet-wizard/actions/workflows/build_deploy_release_master.yml)

The Timesheet-Wizard is a personal pet project to fetch timesheets from [Clockify](https://clockify.me/de/), transform
them to various formats and export them again into other tools.

As of now, the only target formats that are supported are XLSX, PDF and CSV, resulting in the
following main features of Timesheet-Wizard:

- Fetch timesheets from Clockify
- Generate XLSX files from imported timesheets & store them
- Generate PDF files from imported timesheets & store them
- Generate CSV files from imported timesheets & store them

## Motivation

As a freelance Software-Engineer & -Architect doing mostly time & material contracting, tracking my working hours is
quite essential. To ease this, I'm using [Clockify](https://clockify.me/). It's an awesome tool with a nice UI that
allows me to track and also categorize my working hours. Besides supporting some Excel reports out of the box, it also
provides an API to export reports in JSON format as well.

To have the freedom to customize the reports as much as I like and to transfer these reports automatically to
other tools, I decided to create my own little application allowing me to do that: the Timesheet-Wizard.

In addition to the business motivation mentioned above, this is also a perfect opportunity to play around with
technology in the [function-as-a-service](https://en.wikipedia.org/wiki/Function_as_a_service) territory. That's the
reason why the Timesheet Wizard is bundled and deployed to multiple hyperscalers – at the moment it's
[AWS Lambda](https://aws.amazon.com/de/lambda), [Azure Functions](https://learn.microsoft.com/en-us/azure/azure-functions/functions-overview?pivots=programming-language-java)
and [Google Cloud Functions](https://cloud.google.com/functions).

## Documentation

More verbose documentation of the architecture following [arc42](https://arc42.org/) can be found in
the [doc-folder](docs/README.md).

### TL;DR

#### The Timesheet-Wizard is

- written in Kotlin
- built with Gradle
- deployed continuously to the cloud using [GitHub Actions](https://github.com/features/actions)
- running as **AWS Lambda** without any Web framework
- running as **Azure Function** with [Spring Boot Web Framework](https://spring.io/projects/spring-boot)
- running as **Google Cloud Function** with [Quarkus Web Framework](https://quarkus.io/)
- following the infrastructure-as-code-approach with provisioning
    - via [AWS Cloudformation](https://aws.amazon.com/cloudformation/?nc1=h_ls) for AWS
    - via [Azure Bicep](https://learn.microsoft.com/en-us/azure/azure-resource-manager/bicep/overview?tabs=bicep) for
      Azure
    - via [Terraform](https://www.terraform.io/) for Google Cloud Platform
- triggered by AWS EventBridge and Azure Function triggers

![Technical context](docs/assets/readme-context-technical.drawio.png "Technical context")
*Technical context*

#### The Timesheet-Wizard consists of five Gradle subprojects:

- **tw-spi**: the service provider interface to be implemented for any cloud specific things. Like e.g. uploading
  timesheets to some cloud storage.
- **tw-core**: the code module that contains the business logic. This subproject is cloud-agnostic to switch cloud
  vendors (e.g. AWS, Azure, GCP, ...) easily. This subproject is also
  framework-agnostic to switch web frameworks (e.g. Spring Boot, Quarkus,...) easily.
- **tw-app-aws**: implements the interfaces defined in `tw-spi` with AWS specific code and also bundles the `tw-core`
  with AWS specific things to an AWS Lambda function.
- **tw-app-azure**: implements the interfaces defined in `tw-spi` with Azure specific code and also bundles the
  `tw-core` with Azure specific things to a Spring Boot Azure Function.
- **tw-app-gcp**: implements the interfaces defined in `tw-spi` with Google Cloud specific code and also bundles the
  `tw-core` with Google Cloud specific things to a Quarkus Google Cloud Function.

The `tw-core` Gradle subproject contains two Kotlin packages without any dependencies on each other, having the
following responsibilities:

**importing**

- importing timesheets from Clockify
- transforming them into the domain model

**exporting**

- generating XLSX, PDF & CSV files from the domain model
- storing the XLSX, PDF & CSV files in the cloud

There is a third package '**anticorruption**', which is building the bridge between the other two packages by observing
and sending application events.

![Building blocks](docs/assets/readme-static.drawio.png "Building blocks")
*Building blocks*

## Getting started

### Prerequisites

* [Java 21+](https://www.oracle.com/de/java/technologies/downloads/)
* [Gradle](https://gradle.org/)
* [Docker (for tests using testcontainers)](https://www.docker.com/)

### Build & test

- Build with `gradle build`

### Package, run and deploy to a Cloud

* [tw-app-gcp on Google Cloud Platform](./tw-app-gcp/README.md)
* [tw-app-aws on AWS](./tw-app-aws/README.md) - work in progress
* [tw-app-azure on Microsoft Azure](./tw-app-azure/README.md) - work in progress