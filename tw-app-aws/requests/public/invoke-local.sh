#!/bin/sh

sam local invoke TwFunction -e ./requests/public/event.json -n ./requests/public/env.json --parameter-overrides Architecture=arm64 MonitoringRecipient=richard.hendricks@example.org