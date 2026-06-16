#!/bin/bash
set -a
source "$(dirname "$0")/.env.local"
set +a

./gradlew bootRun --args='--spring.profiles.active=local'
