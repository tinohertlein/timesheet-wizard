# Deployment View

## AWS

The Timesheet-Wizard consists of a single Lambda function which is created via an AWS CloudFormation template.
All other services like Event Scheduler, S3 buckets, Log Groups ... are also created via AWS CloudFormation.

## Azure

The Timesheet-Wizard consists of a single Azure Function App which is created via an Azure Bicep template.
All other services like Resource Groups, Azure Blob Storage, App Insights ... are also created via Azure Bicep.

## Google Cloud Platform

The Timesheet-Wizard consists of a single Google Cloud Function App which is created via Terraform.
All other services like Cloud Scheduler Events, Gloud Storage Buckets ... are also created via Terraform.

## Scaleway

The Timesheet-Wizard consists of a single Scaleway Serverless Job which is provisioned via the Scaleway CLI.
All other services like the cron schedule, the Container Registry namespace and the Object Storage bucket are also
provisioned via the Scaleway CLI.

## Local

`tw-app-local` is not deployed anywhere. It's packaged as a self-contained shadow jar (`./gradlew
:tw-app-local:shadowJar`) and run on demand with `java -jar tw-app-local/build/libs/*.jar`. There is no scheduler,
no IaC, and no cloud storage — files are read from and written to the local filesystem, and the run is triggered
manually by Tino instead of on a schedule.