---
name: ilo
description: >-
  Run builds, tests, and other tooling inside a reproducible, containerized dev
  environment via the `ilo` CLI instead of on the host. Use this whenever a
  project relies on ilo, devcontainers, or container-based build environments —
  it has a `.ilo.rc` / `.ilo/ilo.rc`, `dev/` argument files, a
  `Containerfile`/`Dockerfile`, a `devcontainer.json`, a `devfile.yaml`, or a
  compose file — or when a command needs a toolchain the host is missing (no
  local JDK, Node, Go, Rust, Maven, etc.) and the work should happen in a
  container dev environment. Includes ready-to-use dependency-cache recipes for
  many languages and build tools, and how to keep files written in the container
  owned by you on the host (the `--remote-user` flag) when an image runs as root.
allowed-tools: Bash(ilo *)
---

# Using ilo

`ilo` manages reproducible build environments by wrapping a container runtime
(Podman, nerdctl, Docker, or Apple's `container`). Instead of installing a
project's toolchain on the host, you run commands **inside a container** built
from the project's image or `Containerfile`. The container is persistent and
reused across runs, the project directory is mounted read-write, and with the
default rootless Podman runtime files written inside the container stay owned by
you on the host.

The core idea for an agent: **don't run build/test commands on the host — run
them through `ilo` so the right toolchain is present and the build is
reproducible.**

## `--help` is the source of truth

This skill ships separately from the `ilo` binary, so the installed version may be
newer or older than what is described here. Treat the installed binary as
authoritative: run `ilo --help` and `ilo <subcommand> --help` to confirm the
available subcommands, flags, and defaults before relying on anything below. The
patterns in this skill describe the stable command surface; if a flag named here
is rejected, check `--help` rather than assuming the command is wrong.

## When this applies

Treat ilo as the way to run tooling when any of these are true:

- `ilo` is on `PATH`, AND
- the project has one of: `.ilo.rc`, `.ilo/ilo.rc`, an `ILO_RC` env var,
  `dev/` argument files (plain-text files of ilo args), a `Containerfile` /
  `Dockerfile`, a `devcontainer.json` (`.devcontainer/devcontainer.json` or
  `.devcontainer.json`), a `devfile.yaml`, or a compose file; OR
- a command you need to run requires a toolchain that is **not installed on the
  host**.

If the host already has the toolchain and the project shows no sign of ilo,
prefer running commands directly.

## Setting up a project

When a project could use ilo but isn't wired up yet, **offer** to set it up — show
what you'll add and ask first, don't do it silently. Before creating an rc file,
check the repo's `CLAUDE.md` / docs: some repos deliberately forbid an auto-loaded
rc file (ilo's own does) and use explicit `@dev/...` argument files instead.
Respect that.

### 1. Offer a Containerfile

If no image already carries the project's toolchain, offer to create a
`Containerfile` tailored to it. Detect the stack from its build files (`pom.xml`,
`package.json`, `go.mod`, `Cargo.toml`, …), start from an official base image for
that toolchain, and add only what the build actually needs. Put it at
`dev/Containerfile` (ilo's own convention) and build from it with `--containerfile`.
Show the file and get sign-off before writing it.

### 2. Detect the runtime and its mode

How files written in the container end up owned on the host depends on **which
runtime ilo uses and whether it runs rootless or rootful** — so determine this
before writing the rc file, and use it both to construct the rc and to interpret
the ownership check in step 4.

- **Which runtime** ilo will use: it honors `--runtime` / `$ILO_SHELL_RUNTIME`,
  else auto-selects the first installed of podman → nerdctl → docker → container.
  Check what's on `PATH` (`command -v podman nerdctl docker`).
- **Which mode**:
  - Podman / nerdctl: `podman info --format '{{.Host.Security.Rootless}}'`
    (`true` = rootless).
  - Docker: `docker info --format '{{.SecurityOptions}}'` — contains `rootless`
    when in rootless mode; Docker Desktop maps ownership like rootless via its VM.

ilo's [`--update-remote-user-uid`](https://ilo.projects.metio.wtf/shell/options/#--update-remote-user-uid)
is **on by default** and does the right thing per runtime — keep it on. The
behavior to expect:

| Runtime / mode | Root image | Non-root image (most current images) |
| --- | --- | --- |
| rootless Podman / nerdctl | owned by you — nothing to do | `--userns=keep-id` maps the user to you (auto; name it with `--remote-user` if not detected) |
| rootless Docker / Docker Desktop | owned by you | owned by you |
| rootful Docker | runs as your host UID, so `/root/...` paths may be unwritable — redirect caches to a mounted path | a derived image remaps the user to you |

The cache-redirect pattern (step 3) is doubly useful: it also sidesteps the
rootful-Docker case where a root image can't write `/root`.

### 3. Create an `.ilo.rc` to simplify invocation

An rc file removes the `shell <options> <image>` boilerplate so every call becomes
just `ilo <command>`. Create `.ilo.rc` in the project root with the `shell`
subcommand, the image (or `--containerfile` + an image name to build), any cache
mounts, and any ownership flag step 2 calls for — one argument per line:

```
shell
--containerfile dev/Containerfile
--volume ${XDG_CACHE_HOME:-$HOME/.cache}/maven:/cache:z
--env MAVEN_ARGS=-Dmaven.repo.local=/cache
dev/my-project:latest
```

The cache mount above points Maven's local repository at `/cache` (a host
directory) so dependencies survive between runs — see
[references/commands.md](references/commands.md) for the equivalent for Gradle,
Cargo, Go, npm, and others. Redirecting the tool's cache to a fixed path you mount
is more robust than mounting onto the image's default location, which varies by
image (and is often unwritable, since most current images run as a non-root user).
Add `--remote-user <name>` (or `--runtime <name>`) here only if step 2 / step 4
shows you need it — ilo's defaults usually suffice.

With that in place, ilo prepends those args automatically, so the trailing command
runs inside the container:

```console
ilo mvn verify                                   # runs `mvn verify` in the container
ilo go test ./...
ilo bash -c 'cd web && npm ci && npm run build'  # complex / multi-step cases
```

`ilo bash -c '...'` is the go-to for anything with pipes, `&&`, `cd`, or quoting;
it needs `bash` in the image — use `sh -c '...'` for minimal images.

**An `.ilo.rc` is host-specific** (local cache paths, host-dependent ownership
settings), so it must not be shared. After writing it, if the project is a git
repository and the file isn't already ignored, add it to `.gitignore`
automatically:

```console
# only if inside a work tree and not already ignored
git rev-parse --is-inside-work-tree >/dev/null 2>&1 \
  && ! git check-ignore -q .ilo.rc \
  && printf '\n# ilo per-developer config (host-specific)\n.ilo.rc\n.ilo/\n' >> .gitignore
```

If there is no git repository, skip this step — do not create one.

Two things to know about rc files: ilo loads `.ilo.rc` on **every** invocation in
that directory and expands its values with your host shell, so it asks you to trust
the file the first time. In a **non-interactive** session an untrusted file is
silently skipped — so right after creating one, the next automated `ilo <command>`
may run without the image/options and fail confusingly. Have the user run `ilo`
once interactively to trust it, or fall back to an explicit `@dev/run` argument file
(which needs no trust prompt).

### 4. Verify read/write and file ownership

Before generating any build artifacts, confirm that files written **inside** the
container land on the host owned by *you*, not by `root` (or a phantom sub-UID).
Run the check through the rc file you just created, so it exercises the real
runtime, image, and flags:

```console
ilo bash -c 'echo ok > .ilo-write-test'
ls -l .ilo-write-test    # must be owned by your host user, not root
rm .ilo-write-test
```

If it comes back owned by `root`/another UID or won't delete without `sudo`, stop
and apply the remedy for your runtime and mode from step 2 — typically
`--remote-user <name>` on rootless Podman/nerdctl, or switching to rootless Docker
(or redirecting the tool's cache to a mounted path) on rootful Docker — then add
that flag to `.ilo.rc` and re-check. Full details:
https://ilo.projects.metio.wtf/usage/file-ownership/.

### Alternative: a committed `devcontainer.json`

When the environment should be **shared with the team and committed** — and usable
by editors that read [devcontainers](https://containers.dev/), not just ilo —
describe it in a `devcontainer.json` and point the rc file at `ilo devcontainer`
instead of `ilo shell`. Split by what is portable:

- **`devcontainer.json`** is committed, so it must contain **no host paths or other
  host-specific settings**. Put the portable configuration here — ilo reads `image` /
  `build.dockerfile`, `containerEnv` (cache-redirect vars such as `CARGO_HOME=/cache`
  are portable), `remoteUser` (the ownership fix for root images — portable, use it
  instead of `--remote-user`), `remoteEnv`, `forwardPorts`, `workspaceFolder`, and the
  lifecycle commands (`postCreateCommand`, …). Persist caches with a **named volume**,
  which has no host path, survives recreation, and side-steps host file ownership
  entirely:

  ```json
  {
    "image": "docker.io/library/rust:latest",
    "containerEnv": { "CARGO_HOME": "/cache" },
    "mounts": ["source=myproject-cache,target=/cache,type=volume"]
  }
  ```

  Do **not** put a host bind-mount (`source=/home/...`) here — it would break for
  everyone else. Use a named volume, or keep host-path caches in the `ilo shell` setup.

- **`.ilo.rc`** stays host-specific and git-ignored (step 3), but now just selects the
  devcontainer workflow plus any host-only toggle (e.g. a forced `--shell-runtime`):

  ```
  devcontainer
  ```

Then `ilo` opens the shared environment, applying the file's lifecycle. Two
differences from the `ilo shell` setup:

- `ilo devcontainer` always opens an **interactive** shell and does **not** accept a
  trailing command — positional arguments are `devcontainer.json` locations, so the
  `ilo mvn verify` / `ilo bash -c '...'` shortcut does **not** apply. For
  non-interactive one-offs (CI, automation), keep an `ilo shell` rc; drive automated
  setup through `postCreateCommand` instead.
- ilo searches `.devcontainer/devcontainer.json` then `.devcontainer.json`.

### Alternative: a committed `devfile.yaml`

For teams standardizing on [devfiles](https://devfile.io/) (odo, Eclipse Che), point
the rc file at `ilo devfile`. ilo opens the first component that declares an `image`
(or a local Dockerfile), mapping its `image`, container `env` (cache-redirect vars are
portable), and `mountSources` / `sourceMapping` (the project mount):

```yaml
schemaVersion: 2.2.0
metadata:
  name: my-project
components:
  - name: dev
    container:
      image: docker.io/library/rust:latest
      mountSources: true
      sourceMapping: /workspace
      env:
        - name: CARGO_HOME
          value: /cache
```

```
# .ilo.rc (host-specific, git-ignored)
devfile
```

Like `ilo devcontainer`, this is **interactive-only** (positional arguments are
`devfile.yaml` locations, not a command). ilo maps only the component's image, env,
and project mount — it does **not** bind extra volumes from the devfile, so a cache
redirected via `env` lives in the reused container (and is cleared by `--fresh`),
with no named-volume option here. Pick a component with `--component <name>`; ilo
searches `devfile.yaml` then `.devfile.yaml`.

### When the project needs companion services: `ilo compose`

ilo's core job is a complete local dev environment — so when building or testing needs
more than a toolchain (a database, a message broker, a cache server, …), describe the
whole stack in a compose file and switch the setup to `ilo compose`. It opens a shell
in one service (`dev` by default) with the others running alongside, which is the right
choice whenever a project can't be built or tested in isolation:

```yaml
# docker-compose.yml (committed; relative paths + named volumes keep it portable)
services:
  db:
    image: docker.io/library/postgres:latest
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
      - db                  # the database comes up with the dev shell
volumes:
  cache:
```

```
# .ilo.rc (host-specific, git-ignored)
compose
```

Then `ilo` opens the `dev` shell with `db` running (reachable from `dev` at the
hostname `db`). Run a one-off non-interactively with
`ilo compose --no-interactive dev mvn verify`, force a rebuild with `--build`, or
attach to another service with `ilo compose db`. The service is the first positional
(default `dev`); to keep the `ilo <command>` shortcut, put `compose` and `dev` on
separate lines in `.ilo.rc` so `ilo mvn verify` runs in the `dev` service. As with
`devcontainer.json`, keep host paths out of the committed compose file — use relative
mounts and named volumes, or compose's `${VAR}` interpolation for anything truly
host-specific. `ilo compose` needs a compose runtime (Docker Compose, podman-compose,
or nerdctl) on `PATH`.

## How to run commands

Pick the first pattern that fits. Always run **non-interactively** for
automation — never open a bare interactive shell you can't exit.

**1. Use the project's argument files (preferred).** ilo argument files are
plain-text files of ilo args, invoked with an `@` prefix. Projects commonly keep
them under `dev/`. They usually already set `--no-interactive` and the command to
run:

```console
ilo @dev/build      # e.g. runs `mvn verify` in the project's container
ilo @dev/test
```

List candidates first (e.g. `ls dev/`) and read one to see what it does before
running it.

**2. Project has an rc file — run an ad-hoc command.** When `.ilo.rc` or
`.ilo/ilo.rc` exists, ilo auto-loads it (it supplies `shell`, the options, and
the image), so trailing arguments become the command run inside the container:

```console
ilo go build ./...
ilo go test ./...
```

**3. No rc/arg file — run a one-off explicitly.** Name the subcommand, the
image, and the command, and pass `--no-interactive`:

```console
ilo shell --no-interactive docker.io/library/maven:latest mvn -q verify
ilo shell --no-interactive --volume ${XDG_CACHE_HOME:-$HOME/.cache}/maven:/cache:z --env MAVEN_ARGS=-Dmaven.repo.local=/cache docker.io/library/maven:latest mvn -q verify
```

## Subcommands

- `ilo shell` — single container from an image or `Containerfile`. The workhorse.
- `ilo compose` — environment backed by a compose file (use when the build needs
  companion services like a database). Defaults to the `dev` service.
- `ilo devcontainer` — reads a `devcontainer.json` and injects its lifecycle.
- `ilo devfile` — reads a `devfile.yaml` and injects its lifecycle.

All four take the same shell-style options below and run a command the same way
(`ilo compose --no-interactive dev mvn verify`).

## Common, stable flags

These are the flags you reach for most; they are part of the stable surface. For
the complete, version-accurate list and defaults, use `ilo <subcommand> --help`.

- `--no-interactive` — run one command and exit (do this for automation).
- `--containerfile` / `--dockerfile <file>` — build the image from a file first.
- `--volume host:container:z` — mount a host directory (e.g. a dependency cache)
  into the container. Pair it with the tool's own cache-dir env var (see
  `references/commands.md`) so downloads survive between runs.
- `--env KEY=value` — set an environment variable.
- `--remote-user <name>` — run as (and align to your host user) the named container
  user. Use it when an image runs as `root` but writes files you need to own on the
  host: running as the image's non-root user (e.g. `--remote-user gradle`) keeps
  caches and outputs owned by you instead of an unmanageable sub-UID.
- `--publish host:container` — expose a port.
- `--pull` — re-pull the image (and recreate the container) before running.
- `--fresh` — discard the reused container and recreate from scratch.
- `--remove-image` — remove the container and its image on exit (clean slate).
- `--runtime podman|docker|nerdctl|container` — force a runtime; otherwise ilo
  auto-selects podman → nerdctl → docker → container.
- `--no-rc` — run without loading any rc files (goes *before* the subcommand:
  `ilo --no-rc shell ...`).

Run `ilo <subcommand> --help` for the full list, or read `references/commands.md`
for a fuller cheatsheet.

## Notes & cautions

- **rc files run host shell expansion.** `.ilo.rc` / `.ilo/ilo.rc` are discovered
  automatically and their values are expanded by your host shell, so loading one
  can run arbitrary commands. The first time ilo sees an untrusted rc file it
  prompts to trust it; in a non-interactive session an untrusted file is **not**
  loaded. If a command silently runs without the expected environment, an
  untrusted rc file may have been skipped — run it interactively once to trust it,
  or use an explicit `@dev/...` argument file instead.
- **Don't add an rc file to a repo that forbids it.** Some repos (ilo's own, for
  example) explicitly tell you not to add `.ilo.rc` / `.ilo/ilo.rc` because ilo
  auto-loads them on *every* invocation. Use explicit `@dev/...` argument files
  there. Check the repo's `CLAUDE.md` / docs before creating an rc file.
- **Always use long-form image references** — include the registry everywhere you
  name an image (`ilo` commands, `.ilo.rc` / argument files, `Containerfile` `FROM`
  lines, compose files): `docker.io/library/golang:latest`, not `golang:latest`;
  `ghcr.io/...` / `mcr.microsoft.com/...` keep their host. Short names rely on
  registry-search resolution that differs by runtime and can fail or pull the wrong
  image under Podman/Fedora; the long form works identically everywhere.
- **First run is slow** (pull/build); later runs reuse the container and are fast.
- **Apple `container` runtime** is macOS-only and single-terminal; it needs
  `container system start` once per boot.

For full docs see https://ilo.projects.metio.wtf/.
