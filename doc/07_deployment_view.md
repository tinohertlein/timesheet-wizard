# Deployment View

The Timesheet-Wizard consists of two decoupled modules:

- import-from-clockify
- generate-excel

![Deployment](assets/deployment.drawio.png "Deployment")

Both are built with Gradle and deployed as an AWS CloudFormation stack via AWS-SAM and GitHub Actions.

'generate-excel' has a dependency on the [Apache POI](https://poi.apache.org/) jar-library to read and write
Excel-files. Unfortunately this library does not work packaged into a GraalVM native image, so 'generate-excel' is
deployed as Quarkus application on
a ['normal' Java 11 AWS lambda runtime](https://docs.aws.amazon.com/lambda/latest/dg/lambda-java.html). (--> Risk #R1)

'import-from-clockify', on the contrary, functions very well as a GraalVM native image and is therefor deployed as a
Micronaut application with
a [custom AWS Lambda runtime](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html). 
