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