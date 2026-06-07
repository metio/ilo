# ilo command reference

A fuller cheatsheet than `SKILL.md`. **`ilo <subcommand> --help` is the source of
truth** — this file ships separately from the binary, so confirm flags and
defaults against the installed version before relying on them. Full docs:
https://ilo.projects.metio.wtf/.

## Subcommands

| Command                   | Purpose                                                                 |
|---------------------------|-------------------------------------------------------------------------|
| `ilo shell`               | Open/run a single reusable container from an image or `Containerfile`.  |
| `ilo compose`             | Same, backed by a compose file; defaults to the `dev` service.          |
| `ilo devcontainer`        | Read a `devcontainer.json` and run a shell with its lifecycle injected. |
| `ilo devfile`             | Read a `devfile.yaml` and run a shell with its lifecycle injected.      |
| `ilo generate-completion` | Emit bash/zsh completion (`source <(ilo generate-completion)`).         |

Positional arguments after the subcommand: `ilo shell [IMAGE] [COMMAND...]`.
The first positional is the image; everything after it is the command to run
inside the container. For `compose`, the first positional is the service name:
`ilo compose [SERVICE] [COMMAND...]`.

## Argument files and rc files

- Argument file: a plain-text file of ilo args, used with `@`: `ilo @dev/build`.
  One arg per line or several per line. Quote values containing whitespace
  (`"--runtime-option=some value"` or `--runtime-option "some value"`; the form
  `--opt="a b"` does **not** work). Multiple are allowed and evaluated in order:
  `ilo @first @second`, and they mix with CLI args: `ilo shell @defaults docker.io/library/node:latest`.
- rc files: auto-loaded before your args. Default locations, both loaded if
  present: `.ilo/ilo.rc` then `.ilo.rc`. Override with `ILO_RC` (comma-separated
  list). Disable with `--no-rc` (placed before the subcommand). rc values are
  expanded by the host shell and are trusted on first use (see SKILL.md).

## Common shell/compose options

| Option                                                     | Effect                                                                       |
|------------------------------------------------------------|------------------------------------------------------------------------------|
| `--interactive` / `--no-interactive`                       | Interactive session (default) vs. run one command and exit.                  |
| `--containerfile` / `--dockerfile <file>`                  | Build the image from this file before running.                               |
| `--context <dir>`                                          | Build context for `--containerfile` (default `.`).                           |
| `--volume host:container[:z]`                              | Mount an extra volume (caches, secrets, …).                                  |
| `--env KEY=value`                                          | Set an env var baked onto the container.                                     |
| `--remote-env KEY=value`                                   | Set an env var only for the exec'd shell/command (no recreate).              |
| `--publish host:container`                                 | Publish a port to the host.                                                  |
| `--working-dir <path>`                                     | Working directory inside the container (default: mirrors host cwd).          |
| `--mount-project-dir` / `--no-mount-project-dir`           | Mount the current dir (default on).                                          |
| `--workspace-mount <spec>`                                 | Replace the default project bind-mount with an explicit mount.               |
| `--shell <path>`                                           | Interactive shell to start (default `/bin/sh`).                              |
| `--shell-arg <arg>`                                        | Extra arg for the interactive shell (repeatable; e.g. `-l`, `-i`).           |
| `--hostname <name>`                                        | Set the container hostname.                                                  |
| `--pull`                                                   | Re-pull the image (and recreate the container) before running.               |
| `--fresh`                                                  | Discard the reused container and recreate from scratch.                      |
| `--keep-running`                                           | Leave the container running after the last session exits.                    |
| `--remove-image`                                           | Remove the container and its image on exit (clean slate).                    |
| `--override-command` / `--no-override-command`             | Inject a keepalive (default) vs. rely on the image's own long-lived process. |
| `--update-remote-user-uid` / `--no-update-remote-user-uid` | Align the container user's UID/GID with the host user (default on).          |
| `--remote-user <name>`                                     | Container user to run as / align.                                            |
| `--missing-volumes CREATE\|WARN\|ERROR`                    | How to handle a missing host dir to mount (default CREATE).                  |
| `--debug`                                                  | Print the runtime commands before executing them.                            |

### Runtime control

| Option                                         | Effect                                                     |
|------------------------------------------------|------------------------------------------------------------|
| `--runtime podman\|docker\|nerdctl\|container` | Force a runtime (aliases `p`/`d`/`n`/`c`).                 |
| `--runtime-option <opt>`                       | Extra option for the runtime itself (repeatable).          |
| `--runtime-pull-option <opt>`                  | Extra option for the runtime's `pull` (repeatable).        |
| `--runtime-build-option <opt>`                 | Extra option for the runtime's `build` (repeatable).       |
| `--runtime-run-option <opt>`                   | Extra option for the runtime's `run` (repeatable).         |
| `--runtime-cleanup-option <opt>`               | Extra option for the runtime's image removal (repeatable). |

Auto-selection order when `--runtime` / `ILO_SHELL_RUNTIME` is unset:
podman → nerdctl → docker → container (macOS only).

## compose specifics

- Defaults to a `docker-compose.yml` in the current directory and the `dev` service.
- `--file <path>` selects a different compose file.
- `--build` forces (re-)building images first; `--pull` pulls first.
- `ilo compose --no-interactive dev mvn verify` runs a one-off in the `dev` service.

## devcontainer / devfile specifics

- `ilo devcontainer` searches `.devcontainer/devcontainer.json` then `.devcontainer.json`.
- `ilo devfile` searches `devfile.yaml` then `.devfile.yaml`.
- Pass explicit paths to override: `ilo devcontainer ./custom.json /abs/path.json`
  (first readable one wins). The image in the file must be a real image reference.

## Cache-mount examples

Persisting a tool's download cache across runs means mounting a host directory into
the container. The **robust, image-agnostic** way is to mount one host directory at
a fixed container path (`/cache` below) and point the tool's cache there with its
own environment variable — rather than mounting onto the image's default cache
location. Default locations vary between images and, because most current official
images run as a **non-root** user (e.g. `docker.io/library/maven:latest` runs as `ubuntu`, not
`root`, with its repo under `/home/ubuntu`), mounting onto `/root/...` is usually
both the wrong path and unwritable.

ilo keeps the cached files owned by **you** on the host (`--update-remote-user-uid`,
on by default), so the cache stays usable from the host too. See
https://ilo.projects.metio.wtf/usage/file-ownership/.

All rows use the same mount; pick the image that carries your toolchain — **always
in long-form** (`docker.io/library/golang:latest`, not `golang:latest`; short names
resolve differently under Podman/Fedora, long-form works everywhere) — and add the
tool's cache env var:

```
--volume ${XDG_CACHE_HOME:-$HOME/.cache}/<tool>:/cache:z
```

| Tool / ecosystem | Image (long-form) | Add this to point the cache at `/cache` |
|------------------|-------------------|-----------------------------------------|
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
point that tool's own cache-dir env var (`YARN_CACHE_FOLDER`, `POETRY_CACHE_DIR`, …)
at `/cache`. An image that runs as `root` (e.g. `docker.io/library/gradle:latest`)
is the exception on a host whose rootless runtime defaults to a `keep-id` user
namespace: its cache is written under a sub-UID, so the container reuses it fine but
it is not owned by your user and you cannot manage it from the host. The fix is to
run the image as its non-root user with `--remote-user <name>`, which maps to you,
so the cache is owned by you. See
[file ownership](https://ilo.projects.metio.wtf/usage/file-ownership/).

Full one-off invocations:

```console
# Maven
ilo shell --no-interactive \
  --volume ${XDG_CACHE_HOME:-$HOME/.cache}/maven:/cache:z --env MAVEN_ARGS=-Dmaven.repo.local=/cache \
  docker.io/library/maven:latest mvn -q verify

# Go
ilo shell --no-interactive \
  --volume ${XDG_CACHE_HOME:-$HOME/.cache}/go:/cache:z --env GOMODCACHE=/cache/mod --env GOCACHE=/cache/build \
  docker.io/library/golang:latest go test ./...
```

Or bake the mount + env into an `.ilo.rc` (one argument per line) so every call is
just `ilo <command>`:

```
shell
--volume ${XDG_CACHE_HOME:-$HOME/.cache}/cargo:/cache:z
--env CARGO_HOME=/cache
docker.io/library/rust:latest
```

If you prefer to mount onto the image's native cache path instead, first confirm
where that image keeps it (`ilo shell <image> sh -c 'echo $HOME; id'`) — it is
image-specific.

### Caching tools without a dedicated cache variable

Some toolchains keep their caches under `$HOME`, or in several places with no single
cache-dir variable. These need a full invocation rather than a table row.

**Swift** keeps its package cache under `$HOME` (`~/.cache/org.swift.swiftpm`,
`~/.swiftpm`), so redirect `HOME` to the mount:

```console
ilo shell --no-interactive \
  --volume ${XDG_CACHE_HOME:-$HOME/.cache}/swift:/cache:z --env HOME=/cache \
  docker.io/library/swift:latest swift build
```

**Clojure**'s git dependencies honor `GITLIBS`, but its Maven local repository has
no environment variable (the JVM derives it from the passwd home, not `HOME`), so
set it on the command line with `-Sdeps`:

```console
ilo shell --no-interactive \
  --volume ${XDG_CACHE_HOME:-$HOME/.cache}/clojure:/cache:z --env GITLIBS=/cache/gitlibs \
  docker.io/library/clojure:latest \
  clojure -Sdeps '{:mvn/local-repo "/cache/m2"}' -P
```

Maven deps land in `/cache/m2`, git deps in `/cache/gitlibs`.
