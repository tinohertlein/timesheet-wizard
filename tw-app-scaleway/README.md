# Timesheet Wizard on Scaleway

> [!WARNING]
> Deployment is usually done automatically via GitHub Actions. These are just my personal notes in case a manual
deployment from local should be required. The steps outlined below are neither executed nor tested regularly: they might
be outdated or work differently in our situation (e.g., different region-settings might be required).

## Getting Started

### Prerequisites

* [Java 25+](https://www.oracle.com/de/java/technologies/downloads/)
* [Scaleway Account](https://www.scaleway.com/)
* [Scaleway CLI](https://www.scaleway.com/en/docs/scaleway-cli/)
* [Docker (for tests using testcontainers)](https://www.docker.com/)

### Deploying the application to Scaleway

1. Setup project in Scaleway Console
    * Create application
    * Create API Keys for the application
    * Create policies for the application
        * ObjectStorageFullAccess
        * ServerlessJobsFullAccess
        * ContainerRegistryFullAccess

2. Login to Scaleway
    ```shell script
    scw login
    ```

3. Create Object Storage Bucket
    ```shell script
     scw object bucket create tw-sheets region=fr-par
    ```

4. Upload config files to Object Storage Bucket

5. Create Container Registry
    ```shell script
     scw registry namespace create name=timesheet-wizard-cr region=fr-par is-public=false
    ```

6. Package the application (in the project root directory)
    ```shell script
    ./gradlew :tw-app-scaleway:build
    ```

7. Build the Docker image (in the project root directory)
   ```shell script
    docker build tw-app-scaleway -t tw-app-scaleway --platform linux/amd64 
   ```

8. Tag the Docker image
   ```shell script
    docker tag tw-app-scaleway:latest rg.fr-par.scw.cloud/timesheet-wizard-cr/tw-app-scaleway:latest 
   ```

9. Push the Docker image to the Container Registry
   ```shell script
   scw registry login region=fr-par && docker push rg.fr-par.scw.cloud/timesheet-wizard-cr/tw-app-scaleway:latest
   ```
   
10. Create Serverless Job
    ```shell script
    scw jobs definition create name=tw-app-scaleway region=fr-par image-uri=rg.fr-par.scw.cloud/timesheet-wizard-cr/tw-app-scaleway:latest cpu-limit=140 memory-limit=256 job-timeout=5m local-storage-capacity=1000 args.0='{"customerIds": [], "dateRangeType": "LAST_MONTH"}' cron-schedule.schedule="5 4 1 * *" cron-schedule.timezone="Europe/Paris" environment-variables.CLOCKIFY_API_KEY="${CLOCKIFY_API_KEY}" environment-variables.CLOCKIFY_WORKSPACE_ID="${CLOCKIFY_WORKSPACE_ID}" environment-variables.SCW_ACCESS_KEY="${SCW_ACCESS_KEY}" environment-variables.SCW_SECRET_KEY="${SCW_SECRET_KEY}"
    ```

11. Run the Serverless Job
    ```shell script
    scw jobs definition start <id-from-create-command> region=fr-par
    ```
    
12. Wait for the Serverless Job to complete
    ```shell script
    scw jobs run wait <id-from-run-command>
    ```
