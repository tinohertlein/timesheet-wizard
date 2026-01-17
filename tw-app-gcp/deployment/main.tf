terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "6.8.0"
    }
  }
  backend "gcs" {
    ## variables not allowed here :-(
    bucket = "terraform-state-europe-west3-timesheet-wizard-484610"
  }
}

locals {
  project = "timesheet-wizard-484610"
  region  = "europe-west3"
}

variable "CLOCKIFY_API_KEY" {
  type = string
}

variable "CLOCKIFY_WORKSPACE_ID" {
  type = string
}

provider "google" {
  project = local.project
  region  = local.region
}

data "google_compute_default_service_account" "default" {
}

resource "random_id" "default" {
  byte_length = 8
}

resource "google_storage_bucket" "function-upload" {
  name                        = "gcf-v2-sources-${random_id.default.hex}"
  location                    = local.region
  uniform_bucket_level_access = true
  force_destroy               = true
  public_access_prevention    = "enforced"
}


data "archive_file" "function-zip" {
  type        = "zip"
  output_path = "/tmp/function-source.zip"
  source_dir  = "../build/deployment"
}

resource "google_storage_bucket_object" "function-upload-object" {
  name   = "function-source.zip"
  bucket = google_storage_bucket.function-upload.name
  source = data.archive_file.function-zip.output_path
}

resource "google_secret_manager_secret" "clockify-api-key" {
  secret_id = "clockify-api-key"
  replication {
    user_managed {
      replicas {
        location = local.region
      }
    }
  }
}

resource "google_secret_manager_secret" "clockify-workspace-id" {
  secret_id = "clockify-workspace-id"
  replication {
    user_managed {
      replicas {
        location = local.region
      }
    }
  }
}

resource "google_secret_manager_secret_version" "clockify-api-key" {
  secret = google_secret_manager_secret.clockify-api-key.name

  secret_data = var.CLOCKIFY_API_KEY
  enabled     = true
}

resource "google_secret_manager_secret_version" "clockify-workspace-id" {
  secret = google_secret_manager_secret.clockify-workspace-id.name

  secret_data = var.CLOCKIFY_WORKSPACE_ID
  enabled     = true
}

resource "google_cloudfunctions2_function" "tw-app-gcp" {
  name        = "tw-app-gcp"
  location    = local.region
  description = "A GCP function to import timesheet reports from Clockify and export them to various formats"

  build_config {
    runtime     = "java21"
    entry_point = "io.quarkus.gcp.functions.QuarkusHttpFunction"

    source {
      storage_source {
        bucket = google_storage_bucket.function-upload.name
        object = google_storage_bucket_object.function-upload-object.name
      }
    }
  }

  service_config {
    min_instance_count = 0
    max_instance_count = 1
    available_memory   = "512Mi"
    timeout_seconds    = 300

    environment_variables = {
      GCP_PROJECT_ID  = local.project
      GCP_BUCKET_NAME = google_storage_bucket.sheets.name
    }

    secret_environment_variables {
      key        = "CLOCKIFY_API_KEY"
      secret     = google_secret_manager_secret.clockify-api-key.secret_id
      project_id = local.project
      version    = "latest"
    }
    secret_environment_variables {
      key        = "CLOCKIFY_WORKSPACE_ID"
      secret     = google_secret_manager_secret.clockify-workspace-id.secret_id
      project_id = local.project
      version    = "latest"
    }
  }
}

resource "google_storage_bucket" "sheets" {
  name                        = "tw-sheets"
  location                    = local.region
  storage_class               = "STANDARD"
  force_destroy               = true
  uniform_bucket_level_access = true
}

resource "google_cloud_scheduler_job" "daily" {
  name             = "tw-import-daily"
  description      = "Triggers tw-app-gcp to import timesheets for this month on a daily basis"
  schedule         = "0 17 * * 1-5"
  time_zone        = "Europe/Berlin"
  attempt_deadline = "600s"
  region           = local.region

  retry_config {
    retry_count = 1
  }

  http_target {
    http_method = "POST"
    uri         = google_cloudfunctions2_function.tw-app-gcp.url
    body        = base64encode("{\"customerIds\": [], \"dateRangeType\": \"THIS_MONTH\"}")

    oidc_token {
      service_account_email = data.google_compute_default_service_account.default.email
    }

    headers = {
      "Content-Type" = "application/json"
    }
  }
}

resource "google_cloud_scheduler_job" "monthly" {
  name             = "tw-import-monthly"
  description      = "Triggers tw-app-gcp to import timesheets of last month at the beginning of a new month"
  schedule         = "0 5 1 * *"
  time_zone        = "Europe/Berlin"
  attempt_deadline = "600s"
  region           = local.region

  retry_config {
    retry_count = 1
  }

  http_target {
    http_method = "POST"
    uri         = google_cloudfunctions2_function.tw-app-gcp.url
    body        = base64encode("{\"customerIds\": [], \"dateRangeType\": \"LAST_MONTH\"}")

    oidc_token {
      service_account_email = data.google_compute_default_service_account.default.email
    }

    headers = {
      "Content-Type" = "application/json"
    }
  }
}

