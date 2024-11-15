# What is the Timesheet-Wizard?

[![Master branch](https://github.com/tinohertlein/timesheet-wizard/actions/workflows/master.yml/badge.svg)](https://github.com/tinohertlein/timesheet-wizard/actions/workflows/master.yml)

The Timesheet-Wizard is a personal pet project to fetch timesheets from [Clockify](https://clockify.me/de/), transform
them to various formats and export them again into other tools.

As of now, the only target formats that are supported are XLSX, PDF and CSV, resulting in the
following main features of Timesheet-Wizard:

- Fetch timesheets from Clockify
- Generate & store XLSX files from these timesheets
- Generate & store PDF files from these timesheets
- Generate & store CSV files from these timesheets

## Motivation

As a freelance Software-Engineer & -Architect doing mostly time & material contracting, tracking my working hours is
quite essential. To ease this, I'm using [Clockify](https://clockify.me/). It's an awesome tool with a nice UI that
allows me to track and also categorize my working hours. Besides supporting some Excel-reports out of the box, it also
provides an API to export reports in json-format as well.

In order to have the freedom to customize the reports as much as I like and to transfer these reports automatically to
other tools, I decided to create my own little application allowing me to do that: the Timesheet-Wizard.

In addition to the business motivation mentioned above, this is also a perfect opportunity to play around with
technology in the [function-as-a-service](https://en.wikipedia.org/wiki/Function_as_a_service) territory.
And to be honest: this was the main reason for creating the Timesheet-Wizard.

## Documentation

A more verbose documentation of the architecture following [arc42](https://arc42.org/) can be found in
the [doc-folder](docs/README.md).

### TL;DR

The Timesheet-Wizard consists of three Gradle subprojects:

- **tw-cloud-spi**: the service provider interface to be implemented for any cloud specific things. Like e.g. uploading
  timesheets to some cloud storage.
- **tw-core**: the code module that contains the business logic. This subproject is cloud-agnostic.
- **tw-app-aws**: implements the interfaces defined in tw-cloud-spi with AWS specific code and also bundles the
  cloud-agnostic
  tw-core with AWS specific things to an AWS Lambda function.

![Building blocks](docs/assets/readme-static.drawio.png "Building blocks")

The tw-core Gradle subproject consists of two independent modules (realized as Kotlin packages) with the following
responsibilities:

**import**

- importing timesheets from Clockify
- transforming them into the domain model

**export**

- generating XLSX, PDF & CSV files from the domain model
- storing the XLSX, PDF & CSV files on S3

Timesheet-Wizard is

- a Spring Boot application
- written in Kotlin
- built with Gradle
- deployed continuously
  using [AWS SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) and
  GitHub Actions
- running as AWS Lambda function
- following the infrastructure-as-code-approach with provisioning
  via [AWS Cloudformation](https://aws.amazon.com/cloudformation/?nc1=h_ls)
- triggered by AWS EventBridge

![Technical context](docs/assets/readme-context-technical.drawio.png "Technical context")

## Getting started

### Prerequisites

- Java 21+
- Gradle
- Docker (for tests using testcontainers)
- [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html) (
  for building & invoking as Lambda on local machine)

### Build & test

- Build with `gradle build`
- Test with `gradle test`

### Run

#### Preparation

- Replace placeholders in [application.yml](tw-app-aws/src/main/resources/application.yml) with real Clockify credentials. Or
  override them in a 'confidential' profile not commited to version control.
    - timesheet-wizard.import.clockify.api-key
    - timesheet-wizard.import.clockify.workspace-id
- Create & upload configuration file to cloud storage. An example file is
  shown [here](tw-core/src/testFixtures/resources/e2e/config/configuration.json).
    - Use [Minio](http://localhost:9001/) for local AWS emulation
    - S3 for remote AWS calls


#### Run as AWS Lambda with AWS SAM CLI

- Set import params in [tw-app-aws/requests/public/event.json](tw-app-aws/requests/public/event.json)
- Build: `gradle build && ./tw-app-aws/requests/public/build.sh`
- Invoke locally: `./tw-app-aws/requests/public/invoke-local.sh`
- Invoke remotely in AWS: `./tw-app-aws/requests/public/invoke-remote.sh`