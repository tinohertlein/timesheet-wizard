#!/bin/sh

cd ../
gradle :tw-app-local:build

java -jar ./tw-app-local/build/libs/tw-app-local-all.jar ./tw-app-local/data ./tw-app-local/requests/thisweek.json
