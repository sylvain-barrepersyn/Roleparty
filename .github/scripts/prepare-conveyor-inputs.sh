#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/../.."

output=build/conveyor

./kotlin package -m application-frontend -f executable-jar

rm -rf "$output"
mkdir -p "$output/lib"

python3 - "$output/lib" build/incremental.state/_application-frontend_executableJarJvm-* <<'PYTHON'
import json, pathlib, re, shutil, sys

target, state = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
jars = [pathlib.Path(file) for file in json.loads(state.read_text())["inputFiles"] if file.endswith(".jar")]
skiko = None

for jar in jars:
    runtime = re.fullmatch(r"skiko-awt-runtime-[a-z]+-[a-z0-9]+-(.+)\.jar", jar.name)
    if runtime:
        skiko = runtime.group(1)
        continue
    destination = target / jar.name
    if destination.exists():
        destination = target / f"{jar.parents[2].name}-{jar.parents[3].name}-{jar.name}"
    if destination.exists():
        sys.exit(f"Two dependencies would be packaged as {destination.name}")
    shutil.copyfile(jar, destination)

if skiko is None:
    sys.exit("The Skiko runtime is missing from the classpath")
print(f"SKIKO_VERSION={skiko}")
PYTHON
