#!/bin/sh

gradle clean :tw-app-aws:build && sam build --template-file ./tw-app-aws/deployment/template.yml