#!/bin/sh

sam local invoke TwFunction -e ./tw-app-aws/requests/public/event.json -n ./tw-app-aws/requests/public/env.json --parameter-overrides Architecture=arm64 MonitoringRecipient=richard.hendricks@example.org