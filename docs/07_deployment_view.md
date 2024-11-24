# Deployment View

## AWS

The Timesheet-Wizard consists of a single Lambda function which is created via an AWS Cloudformation template.
All other service like Event Scheduler, S3 buckets, Log Groups ... are also created via AWS Cloudformation.

## Azure

The Timesheet-Wizard consists of a single Azure Function App which is created via an Azure Bicep template.
All other service like Resource Groups, Azure Blob Storage, App Insights  ... are also created via Azure Bicep.