#!/usr/bin/env bash
set -euo pipefail

pattern='[0-9]*.[0-9]*.[0-9]*'
previous=$(git describe --tags --abbrev=0 --match "$pattern" 2>/dev/null || true)
range=${previous:+$previous..}HEAD

scope='(\([^)]*\))?'
bump=none

raise() {
  case "$1:$bump" in
    major:*) bump=major ;;
    minor:none | minor:patch) bump=minor ;;
    patch:none) bump=patch ;;
  esac
}

while IFS= read -r -d '' message; do
  subject=$(sed -n '/[^[:space:]]/{p;q;}' <<<"$message")
  if [[ $subject =~ ^[a-z]+$scope!: || $subject =~ ^break(ing)?$scope: ]] ||
    grep -qE '^BREAKING[ -]CHANGE:' <<<"$message"; then
    raise major
  elif [[ $subject =~ ^feat$scope: ]]; then
    raise minor
  elif [[ $subject =~ ^(fix|refactor|style|perf)$scope: ]]; then
    raise patch
  fi
done < <(git log --format='%B%x00' "$range")

IFS=. read -r major minor patch <<<"${previous:-0.0.0}"
case $bump in
  major) version="$((major + 1)).0.0" ;;
  minor) version="$major.$((minor + 1)).0" ;;
  patch) version="$major.$minor.$((patch + 1))" ;;
  none) version=$previous ;;
esac

echo "previous=$previous"
echo "version=$version"
echo "bump=$bump"
