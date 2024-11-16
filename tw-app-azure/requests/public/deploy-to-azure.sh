#!/bin/sh

export AzureWebJobsStorage=[secret]
export TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY=[secret]
export TW_IMPORT_CLOCKIFY_WORKSPACE_ID=[secret]
export TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID=[secret]

gradle clean :tw-app-azure:build -x test :tw-app-azure:azureFunctionsDeploy

unset AzureWebJobsStorage
unset TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY
unset TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID
unset SPRING_CLOUD_AZURE_STORAGE_CONNECTION_STRING