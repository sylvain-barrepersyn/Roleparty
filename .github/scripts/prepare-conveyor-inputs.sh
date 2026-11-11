#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/../.."

jar=build/tasks/_application-frontend_executableJarJvm/application-frontend-jvm-executable.jar
output=build/conveyor

./kotlin package -m application-frontend -f executable-jar

rm -rf "$output"
mkdir -p "$output"
unzip -q -j "$jar" 'BOOT-INF/lib/*.jar' -d "$output/lib"

skiko=$(find "$output/lib" -name 'skiko-awt-runtime-*.jar')
version=$(basename "$skiko" .jar | sed -E 's/^skiko-awt-runtime-[a-z]+-[a-z0-9]+-//')
rm "$skiko"

echo "SKIKO_VERSION=$version"
