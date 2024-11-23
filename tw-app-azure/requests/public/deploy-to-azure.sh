#!/bin/sh

export TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY=[secret]
export TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID=[secret]

gradle clean :tw-app-azure:build -x test :tw-app-azure:azureFunctionsDeploy

unset TIMESHEET_WIZARD_IMPORT_CLOCKIFY_API_KEY
unset TIMESHEET_WIZARD_IMPORT_CLOCKIFY_WORKSPACE_ID