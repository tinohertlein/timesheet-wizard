# System Scope and Context

## Technical Context

![Technical context](assets/readme-context-technical.drawio.png "Technical context")

### AWS

#### AWS EventBridge  (external system)

Serves as a scheduler to trigger the workflow at a given point of
time - e.g. sometimes at night, when a working day is completed.

#### Clockify (external system)

The external system that is providing an API to fetch the timesheets for a given timeframe.

#### AWS S3 (external system)

The place where the generated XLSX, PDF and CSV files as well as any intermediate files are stored.

#### Tino (actor)

The user of the Timesheet-Wizard who downloads the timesheets when he needs them.


### Azure

#### Azure Function Timer

Serves as a scheduler to trigger the workflow at a given point of
time - e.g. sometimes at night, when a working day is completed.

#### Clockify (external system)

The external system that is providing an API to fetch the timesheets for a given timeframe.

#### Azure Blob Storage (external system)

The place where the generated XLSX, PDF and CSV files as well as any intermediate files are stored.

#### Tino (actor)

The user of the Timesheet-Wizard who downloads the timesheets when he needs them.


### Google Cloud Platform

#### Cloud Scheduler

Serves as a scheduler to trigger the workflow at a given point of
time - e.g. sometimes at night, when a working day is completed.

#### Clockify (external system)

The external system that is providing an API to fetch the timesheets for a given timeframe.

#### Cloud Storage (external system)

The place where the generated XLSX, PDF and CSV files as well as any intermediate files are stored.

#### Tino (actor)

The user of the Timesheet-Wizard who downloads the timesheets when he needs them.
