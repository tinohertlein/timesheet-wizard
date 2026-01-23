# Timesheet Wizard on AWS

> [!WARNING]
> Deployment is usually done automatically via GitHub Actions. These are just my personal notes in case a manual
> deployment from local should be required. The steps outlined below are neither executed nor tested regularly: they
> might
> be outdated or might work differently in our situation (e.g., different region-settings might be required).

## Getting Started

### Prerequisites

* [Java 21+](https://www.oracle.com/de/java/technologies/downloads/)
* [AWS account](https://aws.amazon.com/)
* [AWS CLI](https://aws.amazon.com/cli/)
* [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)
* [Docker (for tests using testcontainers)](https://www.docker.com/)

### Packaging and running the application locally with connection to AWS S3

1. Package the application into an uber-jar (in the project root directory)
   ```shell script
   ./gradlew :tw-app-aws:build
   ```
   It produces the `tw-app-aws.jar` _uber-jar_ file in the `build/libs` directory.

2. Export the required env variables
   ```shell script
   export CLOCKIFY_API_KEY=<clockfiy-api-key>
   export CLOCKIFY_WORKSPACE_ID=<clockify-workspace-id>
   export MONITORING_RECIPIENT=<email-of-monitoring-recipient>
   ```

3. Start & invoke the application as a local Function
   * Note: PDF-export won't work due to missing FontConfig in the local image.
   ```shell script
   echo '{"customerIds": [],"dateRangeType": "THIS_MONTH"}' > ./tw-app-aws/requests/event.json
   sam local invoke TwFunction -e ./tw-app-aws/requests/event.json --parameter-overrides Architecture=arm64
   rm -f ./tw-app-aws/requests/event.json
   ```

### Deploying the application to AWS

1. Package the application into an uber-jar (in the project root directory)
    ```shell script
    ./gradlew :tw-app-aws:build
    ```

2. Login to AWS
    ```shell script
    aws login
    ```

3. Create the S3 bucket for code upload
   ```shell script
   aws s3api create-bucket --bucket tw-stack --region eu-central-1 --create-bucket-configuration LocationConstraint=eu-central-1
    ```

4. Update bucket and region in [./deployment/samconfig.toml](./deployment/samconfig.toml)

5. Export Clockify Secrets and other variables
   ```shell script
   export CLOCKIFY_API_KEY=<clockfiy-api-key>
   export CLOCKIFY_WORKSPACE_ID=<clockify-workspace-id>
   export MONITORING_RECIPIENT=<email-of-monitoring-recipient>
   ```

6. Build AWS stack using AWS SAM
    ```shell script
   sam build --template-file tw-app-aws/deployment/template.yml
    ```

7. Deploy AWS stack using AWS SAM
    ```shell script
   sam deploy --config-file tw-app-aws/deployment/samconfig.toml --parameter-overrides ClockifyApiKey=$CLOCKIFY_API_KEY ClockifyWorkspaceId=$CLOCKIFY_WORKSPACE_ID MonitoringRecipient=$MONITORING_RECIPIENT 
    ``` 

8. Upload config files to S3. Examples can be found in [../config/public](../config/public).
   ```shell script
   aws s3api put-object --bucket tw-sheets --key config/clockify.json --body ./config/public/clockify.json
   aws s3api put-object --bucket tw-sheets --key config/export.json --body ./config/public/export.json
   aws s3api put-object --bucket tw-sheets --key config/import.json --body ./config/public/import.json
   ```

9. Invoke AWS Lambda using AWS SAM
   ```shell script
   sam remote invoke --stack-name tw TwFunction --event '{"customerIds": [],"dateRangeType": "THIS_MONTH"}'
   ```