#!/bin/sh

export TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY=[secret]
export TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID=[secret]
## well known account key for azure storage emulation with azureite
export SPRING_CLOUD_AZURE_STORAGE_ACCOUNT_NAME=devstoreaccount1
export SPRING_CLOUD_AZURE_STORAGE_ACCOUNT_KEY=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==
export SPRING_CLOUD_AZURE_STORAGE_ENDPOINT=http://127.0.0.1:10000/devstoreaccount1

gradle clean :tw-app-azure:build -x test :tw-app-azure:azureFunctionsRun

unset TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY
unset TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID
unset SPRING_CLOUD_AZURE_STORAGE_ACCOUNT_NAME
unset SPRING_CLOUD_AZURE_STORAGE_ACCOUNT_KEY
unset SPRING_CLOUD_AZURE_STORAGE_ENDPOINT