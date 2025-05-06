#!/bin/bash

PROJECT="$(echo ${ENV0_PROJECT_NAME// /_} | awk '{print tolower($0)}')"
ENVIRONMENT="$(echo ${ENV0_ENVIRONMENT_NAME// /_} | awk '{print tolower($0)}')"

while read line; do
  echo $line >> $ENV0_ENV
done < <(vault kv get -format json env0/azure/credentials/sb | \
jq -r '.data.data | to_entries | map("\(.key)=\"\(.value|tostring)\"")|.[]')

while read line; do
  echo $line >> $ENV0_ENV
done < <(vault kv get -format json env0/azure/$PROJECT/$ENVIRONMENT | \
jq -r '.data.data | to_entries | map("\(.key)=\"\(.value|tostring)\"")|.[]')
