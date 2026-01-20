# Timesheet Wizard on Microsoft Azure

> [!WARNING]
> Deployment is usually done automatically via GitHub Actions. These are just my personal notes in case a manual deployment from local should be required. The steps outlined below are neither executed nor tested regularly: they might be outdated or work differently in our situation (e.g., different region-settings might be required).

## Getting Started

### Prerequisites

* [Java 21+](https://www.oracle.com/de/java/technologies/downloads/)
* [Microsoft Azure Account](https://azure.microsoft.com)
* [Azure CLI](https://github.com/Azure/azure-cli)
* [Azure Core Tools](https://learn.microsoft.com/en-us/azure/azure-functions/functions-run-local)
* [Docker (for tests using testcontainers)](https://www.docker.com/)

### Packaging and running the application locally with emulated Azure Blob Storage (Azurite)

1. Package the application (in the project root directory)
    ```shell script
    ./gradlew :tw-app-azure:build
    ```

2. Emulate Azure Blob Storage with [Azurite](https://learn.microsoft.com/en-us/azure/storage/common/storage-use-Azurite)
   in [docker-compose.yml](../docker-compose.yml)
    ```shell script
    docker-compose up
    ```

3. Upload config files to Azurite Storage. Examples can be found in [../config/public](../config/public).
   ```shell script
   # well known account key for Azurite
   az storage blob upload --blob-endpoint http://127.0.0.1:10000/devstoreaccount1 --account-key Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw== --container-name tw-sheets --name config/clockify.json --file ../../config/public/clockify.json
   az storage blob upload --blob-endpoint http://127.0.0.1:10000/devstoreaccount1 --account-key Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw== --container-name tw-sheets --name config/import.json --file ../../config/public/import.json
   az storage blob upload --blob-endpoint http://127.0.0.1:10000/devstoreaccount1 --account-key Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw== --container-name tw-sheets --name config/export.json --file ../../config/public/export.json
   ```

4. Export env variables
   ```shell script
   export CLOCKIFY_API_KEY=<clockfiy-api-key>
   export CLOCKIFY_WORKSPACE_ID=<clockify-workspace-id>
   export AzureWebJobsStorage=dummy # has to be populated with something
   export SPRING_CLOUD_AZURE_STORAGE_ACCOUNT_NAME=devstoreaccount1
   export SPRING_CLOUD_AZURE_STORAGE_ACCOUNT_KEY=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw== # well known account ky for Azurite
   export SPRING_CLOUD_AZURE_STORAGE_ENDPOINT=http://127.0.0.1:10000/devstoreaccount1
   ```

5. Run the Azure Function
   ```shell script
   ./gradlew :tw-app-azure:azureFunctionsRun
   ```

6. Invoke the Azure Function
    ```shell script
   curl -X POST --location "http://127.0.0.1:7071/api/import" \
    -d '{"customerIds": ["1000"], "dateRangeType": "CUSTOM_YEAR", "dateRange": "2022"}'
    ```

### Deploying the application to Microsoft Azure

1. Package the application (in the project root directory)
    ```shell script
    ./gradlew :tw-app-azure:build
    ```

2. Login to Azure
    ```shell script
    az login
    ```

3. Create infrastructure with Azure Bicep
    ```shell script
     az deployment sub create --template-file ./tw-app-azure/deployment/main.bicep --location=germanywestcentral --parameters location=germanywestcentral name=timesheetwizard functionAppName=tw-app-azure
    ```

4. Export Clockify Secrets to make them available for Gradle function upload
   ```shell script
   export CLOCKIFY_API_KEY=<clockfiy-api-key>
   export CLOCKIFY_WORKSPACE_ID=<clockify-workspace-id>
   ```

5. Upload function
    ```shell script
    ./gradlew :tw-app-azure:azureFunctionsDeploy
    ```

6. Upload config files to Storage. Examples can be found in [../config/public](../config/public).
   ```shell script
   az storage blob upload --account-key <account-key> --account-name timesheetwizard --container-name tw-sheets --name config/clockify.json --file ./config/public/clockify.json
   az storage blob upload --account-key <account-key> --account-name timesheetwizard --container-name tw-sheets --name config/import.json --file ./config/public/import.json
   az storage blob upload --account-key <account-key> --account-name timesheetwizard --container-name tw-sheets --name config/export.json --file ./config/public/export.json
   ```

7. Test step 1: trigger the daily Import Job using Azure app key
     ```shell script
     curl -X POST --location "https://tw-app-azure.azurewebsites.net/admin/functions/importDaily" \
         -H "x-functions-key: <app-key>" \
         -H "Content-Type: application/json" \
         -d '{}'
     ```

8. Test step 2: check the function log for errors
   ```shell script
   func azure functionapp logstream tw-app-azure
   ```