#!/usr/bin/env bash
# Deploys tw-app-scaleway to Scaleway as a Serverless Job.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

REGION="fr-par"
PROJECT_ID="a2e443b7-71ee-42ff-8d4f-925001a467f0"
REGISTRY_NAMESPACE="timesheet-wizard-cr"
REGISTRY_ENDPOINT="rg.${REGION}.scw.cloud"
IMAGE_NAME="tw-app-scaleway"
IMAGE_TAG=$(date +%s)
FULL_IMAGE="${REGISTRY_ENDPOINT}/${REGISTRY_NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}"
BUCKET_NAME="tw-sheets"
JOB_NAME="tw-last-month"

: "${CLOCKIFY_API_KEY:?CLOCKIFY_API_KEY must be set}"
: "${CLOCKIFY_WORKSPACE_ID:?CLOCKIFY_WORKSPACE_ID must be set}"
: "${SCW_DEFAULT_ORGANIZATION_ID:?SCW_DEFAULT_ORGANIZATION_ID must be set}"
: "${SCW_ACCESS_KEY:?SCW_ACCESS_KEY must be set}"
: "${SCW_SECRET_KEY:?SCW_SECRET_KEY must be set}"

for bin in scw docker jq; do
  command -v "${bin}" >/dev/null 2>&1 || { echo "Error: '${bin}' is required but not found on PATH." >&2; exit 1; }
done

echo "==> Checking Container Registry namespace '${REGISTRY_NAMESPACE}'"
if [[ "$(scw registry namespace list name="${REGISTRY_NAMESPACE}" project-id="${PROJECT_ID}" region="${REGION}" -o json | jq 'length')" == "0" ]]; then
  echo "Namespace not found, creating..."
  scw registry namespace create name="${REGISTRY_NAMESPACE}" project-id="${PROJECT_ID}" region="${REGION}" is-public=false
else
  echo "Namespace already exists."
fi

echo "==> Checking Object Storage bucket '${BUCKET_NAME}'"
if [[ "$(scw object bucket list region="${REGION}" -o json | jq --arg name "${BUCKET_NAME}" '[.[] | select(.Name == $name)] | length')" == "0" ]]; then
  echo "Bucket not found, creating..."
  scw object bucket create "${BUCKET_NAME}" region="${REGION}"
else
  echo "Bucket already exists."
fi

echo "==> Building application with Gradle"
(cd "${REPO_ROOT}" && ./gradlew :tw-app-scaleway:build -x test)

echo "==> Building Docker image"
docker build -t "${IMAGE_NAME}:${IMAGE_TAG}" "${REPO_ROOT}/tw-app-scaleway" --platform linux/amd64

echo "==> Tagging Docker image as ${FULL_IMAGE}"
docker tag "${IMAGE_NAME}:${IMAGE_TAG}" "${FULL_IMAGE}"

echo "==> Logging in to Container Registry"
scw registry login region="${REGION}"

echo "==> Pushing Docker image"
docker push "${FULL_IMAGE}"

echo "==> Checking Serverless Job definition '${JOB_NAME}'"
JOB_ID="$(scw jobs definition list project-id="${PROJECT_ID}" region="${REGION}" -o json | jq -r --arg name "${JOB_NAME}" '[.[] | select(.name == $name)][0].id // empty')"

if [[ -z "${JOB_ID}" ]]; then
  echo "Job definition not found, creating..."
  JOB_ID="$(scw jobs definition create \
    name="${JOB_NAME}" \
    project-id="${PROJECT_ID}" \
    region="${REGION}" \
    image-uri="${FULL_IMAGE}" \
    cpu-limit=140 \
    memory-limit=256 \
    job-timeout=10m \
    local-storage-capacity=1000 \
    args.0='{"customerIds": [], "dateRangeType": "LAST_MONTH"}' \
    cron-schedule.schedule="5 4 1 * *" \
    cron-schedule.timezone="Europe/Paris" \
    environment-variables.CLOCKIFY_API_KEY="${CLOCKIFY_API_KEY}" \
    environment-variables.CLOCKIFY_WORKSPACE_ID="${CLOCKIFY_WORKSPACE_ID}" \
    environment-variables.SCW_ACCESS_KEY="${SCW_ACCESS_KEY}" \
    environment-variables.SCW_SECRET_KEY="${SCW_SECRET_KEY}" \
    -o json | jq -r '.id')"
else
  echo "Job definition already exists (${JOB_ID}), updating image and configuration..."
  scw jobs definition update "${JOB_ID}" \
    region="${REGION}" \
    image-uri="${FULL_IMAGE}"
fi