# Timesheet Wizard on Google Cloud Platform

> [!WARNING]
> Deployment is usually done automatically via GitHub Actions. These are just my personal notes in case a manual deployment from local should be required. The steps outlined below are neither executed nor tested regularly: they might be outdated or might work differently in our situation (e.g., different location settings might be required).

## Getting Started

### Prerequisites

* [Java 21+](https://www.oracle.com/de/java/technologies/downloads/)
* [Google Cloud account](https://cloud.google.com/)
* [Google Cloud CLI](https://cloud.google.com/cli)
* [Terraform](https://developer.hashicorp.com/terraform)
* [Docker (for tests using testcontainers)](https://www.docker.com/)

### Packaging and running the application locally with connection to Google Cloud Storage

1. Package the application into an uber-jar (in the project root directory)
   ```shell script
   ./gradlew :tw-app-gcp:build
   ```
   It produces the `tw-app-gcp-unspecified-runner.jar` _uber-jar_ file in the `build/` directory.

2. Export the required env variables
   ```shell script
   export GCP_PROJECT_ID=timesheet-wizard
   export GCP_BUCKET_NAME=tw-sheets
   export CLOCKIFY_API_KEY=<clockfiy-api-key>
   export CLOCKIFY_WORKSPACE_ID=<clockify-workspace-id>
   ```

3. Start the application as a local Function
   with [java-function-invoker-1.4.1.jar](./requests/java-function-invoker-1.4.1.jar)
   ```shell script
   java -jar java-function-invoker-1.4.1.jar --classpath ../build/tw-app-gcp-unspecified-runner.jar --target io.quarkus.gcp.functions.QuarkusHttpFunction
   ```

4. Curl the Function
   ```shell script
   curl -X POST --location "http://localhost:8080/import" -d '{"customerIds": [], "dateRangeType": "LAST_MONTH"}'
   ````    

### Deploying the application to Google Cloud Platform

1. Package the application into an uber-jar (in the project root directory)
    ```shell script
    ./gradlew :tw-app-gcp:build
    ```

2. Login to Google Cloud
    ```shell script
    gcloud auth login
    ```

3. Create Google Cloud Project
   ```shell script
    gcloud projects create timesheet-wizard --name=timesheet-wizard
    ```

4. Set default Project Id for Google Cloud CLI
    ```shell script
   gcloud config set project timesheet-wizard
    ```

5. List Billing Accounts and choose one for the following step
   ```shell script
   gcloud billing accounts list
   ```

6. Link the Google Cloud Project to the Billing Account
   ```shell script
   gcloud billing projects link timesheet-wizard --billing-account=<billing-account-id>
   ```

7. Enable required APIs
   ```shell script
   gcloud services enable compute.googleapis.com
   gcloud services enable iam.googleapis.com
   gcloud services enable secretmanager.googleapis.com
   gcloud services enable cloudfunctions.googleapis.com
   gcloud services enable cloudbuild.googleapis.com
   gcloud services enable run.googleapis.com
   gcloud services enable cloudscheduler.googleapis.com
   ```

8. Add roles to Project Owner principal
   ```shell script
   gcloud projects add-iam-policy-binding timesheet-wizard --member=user:<owner-email> --role=roles/iam.serviceAccountTokenCreator
   ```

9. List Service Accounts and choose one for the following steps
   ```shell script
   gcloud iam service-accounts list 
   ```

10. Add roles to Service Account
   ```shell script
   gcloud iam service-accounts add-iam-policy-binding <service-account-id> --member=serviceAccount:<service-account-id> --role=roles/iam.serviceAccountUser
   gcloud projects add-iam-policy-binding timesheet-wizard --member=serviceAccount:<service-account-id> --role=roles/cloudbuild.builds.builder
   gcloud projects add-iam-policy-binding timesheet-wizard --member=serviceAccount:<service-account-id> --role=roles/cloudfunctions.admin
   gcloud projects add-iam-policy-binding timesheet-wizard --member=serviceAccount:<service-account-id> --role=roles/cloudscheduler.admin
   gcloud projects add-iam-policy-binding timesheet-wizard --member=serviceAccount:<service-account-id> --role=roles/compute.instanceAdmin
   gcloud projects add-iam-policy-binding timesheet-wizard --member=serviceAccount:<service-account-id> --role=roles/run.admin
   gcloud projects add-iam-policy-binding timesheet-wizard --member=serviceAccount:<service-account-id> --role=roles/secretmanager.admin
   gcloud projects add-iam-policy-binding timesheet-wizard --member=serviceAccount:<service-account-id> --role=roles/storage.admin
   gcloud projects add-iam-policy-binding timesheet-wizard --member=serviceAccount:<service-account-id> --role=roles/storage.objectAdmin
   ```

11. Impersonate Service Account
   ```shell script
   gcloud auth application-default login --impersonate-service-account <service-account-id>
   ```

12. Create Google Cloud Storage Bucket for Terraform Backend
   ```shell script
   gcloud storage buckets create gs://<terraform-bucket-name> --location=europe-west3 --project=timesheet-wizard
   ```

13. Export Clockify Secrets to make them available to Terraform
   ```shell script
   export TF_VAR_CLOCKIFY_API_KEY=<clockfiy-api-key>
   export TF_VAR_CLOCKIFY_WORKSPACE_ID=<clockify-workspace-id>
   ```

14. Adjust Terraform settings in [tw-app-gcp/deployment/main.tf](./deployment/main.tf)
    - `locals.project` - Google Cloud Project Id
    - `bucket` - Google Cloud Storage Bucket for Terraform Backend

15. Initialize Terraform (in the tw-app-gcp/deployment directory)
   ```shell script
   terraform init
   ```

16. Check the Terraform plan (in the tw-app-gcp/deployment directory)
   ```shell script
   terraform plan
   ```

17. Apply the Terraform plan (in the tw-app-gcp/deployment directory)
   ```shell script
   terraform apply
   ```

18. Upload config files to Bucket. Examples can be found in [../config/public](../config/public). Use Bucket name from
    Terraform output
   ```shell script
   gcloud storage folders create --recursive gs://<sheets-bucket>/config
   gcloud storage cp ../../config/public/clockify.json gs://<sheets-bucket>/config/
   gcloud storage cp ../../config/public/export.json gs://<sheets-bucket>/config/
   gcloud storage cp ../../config/public/import.json gs://<sheets-bucket>/config/
   ```

19. Test step 1: trigger the daily Import Job. Use job name from Terraform output
   ```shell script
   gcloud scheduler jobs run <daily-job> --location=europe-west3
   ```

20. Test step 2: check the Function log for errors
   ```shell script
  gcloud functions logs read tw-app-gcp --region=europe-west3
   ```
