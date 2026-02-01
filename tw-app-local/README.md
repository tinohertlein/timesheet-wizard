# Timesheet Wizard on a local machine

> [!NOTE]
> tw-app-local does not use any cloud fluff, just plain old Java jar stuff.

## Getting Started

### Prerequisites

* [Java 21+](https://www.oracle.com/de/java/technologies/downloads/)
* [Docker (for tests using testcontainers)](https://www.docker.com/)

### Packaging and running the application locally

1. Package the application into an uber-jar (execute in the project root directory)
   ```shell script
   ./gradlew :tw-app-local:build
   ```
   It produces the `tw-app-local-all.jar` _uber-jar_ file in the `build/libs` directory.

2. Export the required env variables
   ```shell script
   export CLOCKIFY_API_KEY=<clockfiy-api-key>
   export CLOCKIFY_WORKSPACE_ID=<clockify-workspace-id>
   ```

3. Copy config files to the data directory. Examples can be found in [../config/public](../config/public).
   ```shell script
   cp ./config/public/clockify.json ./tw-app-local/data/config/clockify.json
   cp ./config/public/export.json ./tw-app-local/data/config/export.json
   cp ./config/public/import.json ./tw-app-local/data/config/import.json
   ```

4. Start & invoke the application as a jar file
   ```shell script
   echo '{"customerIds": [],"dateRangeType": "THIS_MONTH"}' > ./tw-app-local/requests/event.json
   java -jar ./tw-app-local/build/libs/tw-app-local-all.jar ./tw-app-local/data ./tw-app-local/requests/lastmonth.json
   rm -f ./tw-app-local/requests/event.json
   ```

5. Check the generated timesheets in the `tw-app-local/data/customers` directory