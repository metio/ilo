#!/usr/bin/env bash
# SPDX-FileCopyrightText: The ilo Authors
# SPDX-License-Identifier: 0BSD
#
# Verifies every row of the cache-mount table in docs/content/shell/examples.md
# (kept identical in skills/ilo/references/commands.md): for each documented
# image + cache env var, the tool is invocable, honors the env var (its cache dir
# resolves under /cache), and can read/write the mounted /cache directory.
#
# The image and env var are read from the table, so a new row is tested
# automatically. The per-tool version/probe commands live in the map below; a row
# whose tool has no entry fails loudly, forcing the map to stay in sync.
#
# Local use:  ILO=./target/ilo bash .github/scripts/verify-cache-examples.sh
set -uo pipefail

ILO="${ILO:-ilo}"
TABLE="${TABLE:-docs/content/shell/examples.md}"
CACHE_ROOT="$(mktemp -d)"
PROJECT_DIR="$(pwd -P)"   # resolved path: matches the ilo.project label even when cwd has a symlink
fail=0

# Per-tool checks keyed by a token from the table's first column.
#   version: command that must exit 0 (proves the tool is invocable)
#   probe:   command whose output must contain "/cache" (proves the env var is honored); "" to skip
version_cmd() {
  case "$1" in
    maven) echo 'mvn -v' ;;
    gradle) echo 'gradle --version' ;;
    cargo) echo 'cargo --version' ;;
    go) echo 'go version' ;;
    npm) echo 'npm --version' ;;
    pip) echo 'pip --version' ;;
    uv) echo 'uv --version' ;;
    composer) echo 'composer --version' ;;
    nuget) echo 'dotnet --version' ;;
    deno) echo 'deno --version' ;;
    bundler) echo 'gem --version' ;;
    bun) echo 'bun --version' ;;
    dart) echo 'dart --version' ;;
    elixir) echo 'elixir --version' ;;
    haskell) echo 'cabal --version' ;;
    julia) echo 'julia --version' ;;
    *) echo '' ;;
  esac
}
probe_cmd() {
  case "$1" in
    go) echo 'go env GOMODCACHE' ;;
    npm) echo 'npm config get cache' ;;
    pip) echo 'pip cache dir' ;;
    uv) echo 'uv cache dir' ;;
    composer) echo 'composer config -g cache-dir' ;;
    nuget) echo 'dotnet nuget locals global-packages -l' ;;
    bundler) echo 'gem env home' ;;
    julia) echo "julia -e 'print(DEPOT_PATH[1])'" ;;
    *) echo '' ;;   # bun/dart/elixir/haskell/maven/gradle/cargo/deno: no probe usable outside a project; rely on read/write
  esac
}
key_for() {
  case "$(echo "$1" | tr 'A-Z' 'a-z')" in
    maven*) echo maven ;; gradle*) echo gradle ;; cargo*) echo cargo ;; go) echo go ;;
    npm) echo npm ;; pip) echo pip ;; uv*) echo uv ;; composer*) echo composer ;;
    nuget*) echo nuget ;; deno) echo deno ;; bundler*) echo bundler ;;
    bun*) echo bun ;; dart) echo dart ;; elixir*) echo elixir ;;
    haskell*) echo haskell ;; julia) echo julia ;;
    *) echo '' ;;
  esac
}

cleanup() {
  podman ps -a --filter "label=ilo.project=${PROJECT_DIR}" --format '{{.Names}}' 2>/dev/null \
    | xargs -r podman rm -f >/dev/null 2>&1
  # On the CI runner (standard rootless Podman) every image's writes map to the runner,
  # so this removes everything. A root-running image on a keep-id host can leave a few
  # sub-UID-owned files behind; they sit in a mktemp dir under /tmp and are harmless.
  # The user-facing fix for that is --remote-user (see the table note), not cleanup here.
  rm -rf "$CACHE_ROOT" 2>/dev/null
}
trap cleanup EXIT

# Run one example: tool is invocable (vcmd exits 0), env var honored (pcmd output has
# /cache; "" to skip), and /cache is read-write. Records failures in $CACHE_ROOT/.fail.
verify_one() { # label image envargs vcmd pcmd
  local label="$1" image="$2" env="$3" vcmd="$4" pcmd="$5"
  local cache; cache="$(mktemp -d "$CACHE_ROOT/cacheXXXXXX")"
  # Run in /tmp so a tool's stray telemetry/config (e.g. dart's .dart_tool) never dirties the checkout.
  local inner="cd /tmp; v=0; ${vcmd} >/dev/null 2>&1 && v=1; d=skip; "
  [ -n "$pcmd" ] && inner="${inner}if ${pcmd} 2>/dev/null | grep -q /cache; then d=1; else d=0; fi; "
  inner="${inner}rw=0; if echo hi >/cache/.rw 2>/dev/null && [ \"\$(cat /cache/.rw 2>/dev/null)\" = hi ]; then rw=1; fi; echo \"RESULT v=\$v d=\$d rw=\$rw\""
  local res; res="$("$ILO" shell --no-interactive --volume "$cache":/cache:z $env "$image" sh -c "$inner" 2>/dev/null | grep '^RESULT ' | tail -1)"
  local v d rw; v="${res#*v=}"; v="${v%% *}"; d="${res#*d=}"; d="${d%% *}"; rw="${res##*rw=}"
  local inv=PASS envc=PASS rwc=PASS
  [ "$v" = 1 ] || { inv=FAIL; echo fail >>"$CACHE_ROOT/.fail"; }
  case "$d" in 1|skip) ;; *) envc=FAIL; echo fail >>"$CACHE_ROOT/.fail" ;; esac
  [ "$rw" = 1 ] || { rwc=FAIL; echo fail >>"$CACHE_ROOT/.fail"; }
  printf '%-18s %-50s %-8s %-6s %-6s\n' "$label" "$image" "$inv" "$envc" "$rwc"
}

printf '%-18s %-50s %-8s %-6s %-6s\n' TOOL IMAGE INVOKE ENVVAR RW
printf '%.0s-' {1..92}; echo

# Match table rows whose second cell is a backticked registry image (host with a dot, then /).
grep -E '^\|[^|]*\| *`[a-z0-9.-]+\.[a-z]+/[^`]+` *\|' "$TABLE" | while IFS='|' read -r _ c1 c2 c3 _; do
  tool="$(echo "$c1" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')"
  image="$(echo "$c2" | tr -d '`' | sed 's/[[:space:]]//g')"
  env="$(echo "$c3" | tr -d '`' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')"
  key="$(key_for "$tool")"
  if [ -z "$key" ]; then
    printf '%-18s %-50s %s\n' "$tool" "$image" "ERROR: no smoke test mapped — add one to verify-cache-examples.sh"
    echo "fail" >>"$CACHE_ROOT/.fail"; continue
  fi
  verify_one "$tool" "$image" "$env" "$(version_cmd "$key")" "$(probe_cmd "$key")"
done

# Tools documented separately because they have no single cache-dir variable (see the
# "Caching tools without a dedicated cache variable" section in the docs).
verify_one "Swift"   docker.io/library/swift:latest   "--env HOME=/cache"            "swift --version"   ""
verify_one "Clojure" docker.io/library/clojure:latest "--env GITLIBS=/cache/gitlibs" "clojure --version" ""

echo
if [ -f "$CACHE_ROOT/.fail" ]; then
  echo "FAILED: one or more cache examples did not work."; exit 1
fi
echo "All cache examples verified."
