#!/usr/bin/env bash
# SPDX-FileCopyrightText: The ilo Authors
# SPDX-License-Identifier: 0BSD
#
# Verifies the documented 'ilo compose' multi-service setup: 'ilo compose' brings up a companion
# service (a database) alongside the 'dev' service, runs a command in 'dev', and 'dev' can reach the
# companion over the compose network — i.e. a project that cannot be built/tested in isolation works.
#
# ilo compose auto-selects 'docker compose' (v2), which is present on GitHub's ubuntu-latest.
# Local use (needs a compose runtime): ILO=./target/ilo bash .github/scripts/verify-compose-example.sh
set -uo pipefail

ILO="${ILO:-ilo}"
# This script cd's into a temp work dir below, so resolve a relative ILO path (e.g. target/ilo) to an
# absolute one now while the current directory is still the repo. A bare command on PATH is left as-is.
case "$ILO" in */*) ILO="$(readlink -f "$ILO")" ;; esac
work="$(mktemp -d)"
cleanup() {
  ( cd "$work" 2>/dev/null && docker compose down --volumes --remove-orphans >/dev/null 2>&1 )
  rm -rf "$work"
}
trap cleanup EXIT

cat > "$work/docker-compose.yml" <<'YAML'
services:
  db:
    image: docker.io/library/postgres:16-alpine
    environment:
      POSTGRES_PASSWORD: dev
  dev:
    image: docker.io/library/maven:latest
    working_dir: /workspace
    volumes:
      - .:/workspace        # project directory (relative path — portable)
      - cache:/cache        # named volume for the dependency cache
    environment:
      MAVEN_ARGS: -Dmaven.repo.local=/cache
    depends_on:
      - db
volumes:
  cache:
YAML

# The check runs from a file mounted into the dev service (at /workspace), not inline: ilo expands
# command arguments through the host shell, so a '$(...)'/'/dev/tcp' one-liner would be mangled.
cat > "$work/check.sh" <<'SH'
mvn -v >/dev/null 2>&1 && echo MVN-OK
# depends_on only waits for the db container to start, not to accept connections, so retry.
for _ in $(seq 1 20); do
  (exec 3<>/dev/tcp/db/5432) 2>/dev/null && { echo DB-REACHABLE; exec 3>&-; break; }
  sleep 1
done
SH

cd "$work"
echo "Running 'ilo compose' (brings up db + dev, then execs a command in dev) ..."
out="$("$ILO" compose --no-interactive dev bash /workspace/check.sh 2>&1)"
echo "$out"

fail=0
echo "$out" | grep -q MVN-OK       || { echo "FAIL: the dev service's toolchain (mvn) is not invocable"; fail=1; }
echo "$out" | grep -q DB-REACHABLE || { echo "FAIL: the companion db service did not come up / is unreachable from dev"; fail=1; }

echo
[ "$fail" = 0 ] && echo "Compose example verified." || { echo "Compose example FAILED."; exit 1; }
