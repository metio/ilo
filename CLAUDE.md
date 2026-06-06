<!--
SPDX-FileCopyrightText: The ilo Authors
SPDX-License-Identifier: 0BSD
-->

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What ilo is

`ilo` is a small CLI that manages reproducible build environments by wrapping a
container runtime (Podman, nerdctl, or Docker). `ilo shell` opens an
(interactive) shell inside a container built from your project's image or
`Containerfile`. It is a single-binary tool (picocli + GraalVM native image),
written in Java 21.

This repository is the source of `ilo` itself, and it dogfoods `ilo` to build
itself — see the `dev/` RC files below.

## Build & test

This machine has no JDK/Maven/Hugo installed; `ilo` (a native binary) is on
`PATH` and everything builds inside the `metio/devcontainers-graalvm` container.
The `dev/` files are `ilo` argument files invoked with the `@` prefix:

```console
ilo @dev/env       # interactive bash shell in the GraalVM devcontainer
ilo @dev/build     # mvn verify (compile + all tests, skips native build)
ilo @dev/native    # mvn verify -Dwith the native executable
ilo @dev/serve     # hugo dev server for docs/ at http://localhost:1313
ilo @dev/website   # build the docs/ site
```

The underlying Maven commands (run them inside `ilo @dev/env` to iterate, e.g.
on a single test):

```console
mvn verify                          # full verification, native build skipped
mvn verify -Dtest=ShellRuntimeTest  # one test class
mvn verify --define skipNativeBuild=false   # include the native image
```

`mvn verify` is the canonical gate — it runs JUnit 5 tests, the ArchUnit
architecture tests, and JaCoCo coverage. The coverage gate is a 0.75 bundle
instruction ratio plus `CLASS MISSEDCOUNT = 0` (every class must be touched):
the ratio is below 1.0 on purpose because OS-gated code (`@EnabledOnOs(WINDOWS)`
PowerShell paths, `@EnabledOnOs(LINUX/MAC)` expansion) is uncovered on whichever
single OS the build runs on, so the per-class `MISSEDCOUNT = 0` rule plus PIT is
what actually guards test quality. PIT mutation testing (65% threshold, currently
~87%) runs in a dedicated CI job (`mutation` in `verify.yml`), not in `mvn verify`;
run it locally with `mvn test-compile org.pitest:pitest-maven:mutationCoverage`.

Do **not** add a `.ilo.rc` / `.ilo/ilo.rc` to this repo: `ilo` auto-loads those
as argument files on *every* invocation in this directory, which would inject
unwanted args into the build commands above. Use the explicit `@dev/...` files.

## Architecture

Flow of a command (`ilo shell ...`):

1. **`Ilo.main`** (`wtf.metio.ilo.Ilo`) — picocli root command. Before parsing,
   it asks `RunCommands.locate` for RC/argument files and prepends them to the
   user's args (`@`-prefixed, picocli's native argument-file mechanism).
2. **`cli.RunCommands`** — locates RC files: `$ILO_RC` (comma-separated) if set,
   otherwise `.ilo/ilo.rc` then `.ilo.rc`. `shouldAddRunCommands` suppresses this
   for `--version`, `--help`, `generate-completion`, and `--no-rc`.
3. **`shell.ShellCommand`** — the only real subcommand. Holds `ShellOptions`
   (a picocli `@Mixin`), delegates to a `ShellExecutor`.
4. **`shell.ShellRuntime`** — enum of `PODMAN` / `NERDCTL` / `DOCKER`.
   `autoSelect` picks the forced runtime, else `$ILO_SHELL_RUNTIME`, else the
   first runtime whose executable `exists()` on `PATH`.
5. **`cli.CommandLifecycle.run`** — runs four steps in order: **pull → build →
   run → cleanup**, each producing a command line and an exit code. Stops early
   on the first non-zero code; otherwise returns the max exit code.
6. **`shell.DockerLike`** — shared `pull/build/run/cleanup` argument assembly for
   all three Docker-compatible runtimes (`Podman`/`Nerdctl`/`Docker` mostly just
   supply `name()`). Build args are emitted only when a `--containerfile` is set;
   pull only when `--pull` and no containerfile; cleanup only when `--rm-image`.

### Key packages (`src/main/java/wtf/metio/ilo/`)

- **`model`** — the generic CLI abstraction: `CliTool<OPTIONS>` (defines the four
  lifecycle argument lists + `exists()`), `CliExecutor`, `Options`, `Runtime`.
  New command families plug in by implementing these.
- **`shell`** — the `shell` command and its runtimes/options.
- **`os`** — host-shell parameter expansion. `OSSupport.expand` runs option
  values through the real shell so `${VAR}` / `$(cmd)` are expanded the way the
  user expects: `PosixShell` (bash/zsh/sh) or `PowerShell` (pwsh), falling back
  to `NoOpExpansion`. Applied to nearly every option in `DockerLike`.
- **`cli`** — `Executables` (locate/run binaries on `PATH`), `RunCommands`,
  `EnvironmentVariables`, `CommandLifecycle`.
- **`errors`** — all checked failure modes extend `BusinessException`; `ExitCodes`
  maps them to process exit codes and `PrintingExceptionHandler` renders them.
  Throw a specific `BusinessException` subtype rather than a generic exception
  (the ArchUnit `noGenericExceptions` rule enforces this).
- **`utils`** — `Streams` (the `flatten`/`maybe`/`optional`/`withPrefix`/
  `fromList` helpers used pervasively to build argument lists) and `Strings`.
- **`version`** — `Version.java` lives under `src/main/java-templates/` and is
  filtered by the `templating-maven-plugin` so `${project.version}` is
  substituted at build time. Edit the template, not a generated copy.

### Architecture tests

`src/test/java/.../architecture/` uses ArchUnit and runs as part of `mvn verify`.
`LayerRules` defines the layered architecture (`cli`, `shell`, `model`, `errors`,
`utils`, …) and restricts who may access `model`. If you add a package or move a
dependency, expect these to gate it. Test helpers live in `.../test/` (TCKs like
`CliToolTCK`, `TestCliExecutor`, `TestResources`); acceptance tests in
`.../acceptance/` drive the real `CommandLine`.

## Conventions

- **License headers (REUSE):** the project is **0BSD**. Every file carries an
  `SPDX-FileCopyrightText: The ilo Authors` + `SPDX-License-Identifier: 0BSD`
  pair. New files must too (enforced by the `reuse.yml` workflow).
- **Releases are calendar-versioned.** `.github/workflows/release.yml` runs every
  Monday 03:45 UTC and tags `date +'%Y.%-m.%-d'` — but **only if there are new
  commits touching `src/main/java` or `pom.xml`** since the last release.
  Native binaries are built for Linux/macOS/Windows via the GraalVM matrix.
- **Docs** are a Hugo site under `docs/` (theme in `docs/themes/metio`),
  published by `website.yml`.
