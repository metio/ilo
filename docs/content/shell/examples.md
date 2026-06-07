---
title: Examples
date: 2020-04-13
menu:
  main:
    parent: shell
    identifier: shell_examples
categories:
- shell
tags:
- examples
---

The following examples show how `ilo shell` can be used to cache a build tool's
downloaded dependencies across runs, so they are not re-downloaded every time.

## Caching dependencies

Persisting a tool's download cache means mounting a host directory into the
container. The **robust, image-agnostic** way is to mount one host directory at a
fixed container path (`/cache` below) and point the tool's cache there with its own
environment variable — rather than mounting onto the image's default cache location.
Default locations vary between images and, because most current official images run
as a **non-root** user (e.g. `docker.io/library/maven:latest` runs as `ubuntu`, not `root`, with its
repo under `/home/ubuntu`), mounting onto `/root/...` is usually both the wrong path
and unwritable.

`ilo` keeps the cached files owned by **you** on the host
(`--update-remote-user-uid`, on by default), so the cache stays usable from the host
too. See [File Ownership](../../usage/file-ownership).

All rows use the same mount; pick the image that carries your toolchain — **always
in long-form** (`docker.io/library/golang:latest`, not `golang:latest`; short names
resolve differently under Podman/Fedora, long-form works everywhere) — and add the
tool's cache environment variable:

```console
--volume ${XDG_CACHE_HOME:-$HOME/.cache}/<tool>:/cache:z
```

| Tool / ecosystem | Image (long-form) | Add this to point the cache at `/cache` |
| --- | --- | --- |
| Maven | `docker.io/library/maven:latest` | `--env MAVEN_ARGS=-Dmaven.repo.local=/cache` |
| Gradle | `docker.io/library/gradle:latest` | `--env GRADLE_USER_HOME=/cache` |
| Cargo (Rust) | `docker.io/library/rust:latest` | `--env CARGO_HOME=/cache` |
| Go | `docker.io/library/golang:latest` | `--env GOMODCACHE=/cache/mod --env GOCACHE=/cache/build` |
| npm | `docker.io/library/node:latest` | `--env npm_config_cache=/cache` |
| pip | `docker.io/library/python:latest` | `--env PIP_CACHE_DIR=/cache` |
| uv (Python) | `ghcr.io/astral-sh/uv:python3.13-bookworm-slim` | `--env UV_CACHE_DIR=/cache` |
| Composer (PHP) | `docker.io/library/composer:latest` | `--env COMPOSER_CACHE_DIR=/cache` |
| NuGet (.NET) | `mcr.microsoft.com/dotnet/sdk:latest` | `--env NUGET_PACKAGES=/cache` |
| Deno | `docker.io/denoland/deno:latest` | `--env DENO_DIR=/cache` |
| Bundler (Ruby) | `docker.io/library/ruby:latest` | `--env GEM_HOME=/cache` |
| Bun (JS/TS) | `docker.io/oven/bun:latest` | `--env BUN_INSTALL_CACHE_DIR=/cache` |
| Dart | `docker.io/library/dart:latest` | `--env PUB_CACHE=/cache` |
| Elixir (Mix/Hex) | `docker.io/library/elixir:latest` | `--env MIX_HOME=/cache --env HEX_HOME=/cache` |
| Haskell (Cabal) | `docker.io/library/haskell:latest` | `--env CABAL_DIR=/cache` |
| Julia | `docker.io/library/julia:latest` | `--env JULIA_DEPOT_PATH=/cache` |

Tools not bundled in a base image follow the same pattern once installed: enable
Yarn/pnpm with `corepack enable`, or install Poetry in your `Containerfile`, then
point that tool's own cache-dir environment variable (`YARN_CACHE_FOLDER`,
`POETRY_CACHE_DIR`, …) at `/cache`. An image that runs as `root` (e.g.
`docker.io/library/gradle:latest`) is the exception on a host whose rootless runtime
defaults to a `keep-id` user namespace: its cache is written under a sub-UID, so the
container reuses it fine but it is not owned by your user and you cannot manage it
from the host. The fix is to run the image as its non-root user with
`--remote-user <name>`, which maps to you, so the cache is owned by you. See
[File Ownership](../../usage/file-ownership).

## Full examples

```console
# Maven
$ ilo shell \
    --volume ${XDG_CACHE_HOME:-$HOME/.cache}/maven:/cache:z \
    --env MAVEN_ARGS=-Dmaven.repo.local=/cache \
    docker.io/library/maven:latest mvn verify

# Go
$ ilo shell \
    --volume ${XDG_CACHE_HOME:-$HOME/.cache}/go:/cache:z \
    --env GOMODCACHE=/cache/mod --env GOCACHE=/cache/build \
    docker.io/library/golang:latest go test ./...
```

Or bake the mount and environment variable into an
[argument file](../../usage/argument-files) (or `.ilo.rc`), one argument per line,
so every call is just `ilo <command>`:

```console
shell
--volume ${XDG_CACHE_HOME:-$HOME/.cache}/cargo:/cache:z
--env CARGO_HOME=/cache
docker.io/library/rust:latest
```

If you prefer to mount onto the image's native cache path instead, first confirm
where that image keeps it (`ilo shell <image> sh -c 'echo $HOME; id'`) — it is
image-specific.

## Caching tools without a dedicated cache variable

Some toolchains keep their caches under `$HOME`, or in several places with no single
cache-dir variable. These need a full invocation rather than a table row.

**Swift** keeps its package cache under `$HOME` (`~/.cache/org.swift.swiftpm`,
`~/.swiftpm`), so redirect `HOME` to the mount:

```console
$ ilo shell \
    --volume ${XDG_CACHE_HOME:-$HOME/.cache}/swift:/cache:z --env HOME=/cache \
    docker.io/library/swift:latest swift build
```

**Clojure**'s git dependencies honor `GITLIBS`, but its Maven local repository has
no environment variable (the JVM derives it from the passwd home, not `HOME`), so
set it on the command line with `-Sdeps`:

```console
$ ilo shell \
    --volume ${XDG_CACHE_HOME:-$HOME/.cache}/clojure:/cache:z --env GITLIBS=/cache/gitlibs \
    docker.io/library/clojure:latest \
    clojure -Sdeps '{:mvn/local-repo "/cache/m2"}' -P
```

Maven deps land in `/cache/m2`, git deps in `/cache/gitlibs`.
