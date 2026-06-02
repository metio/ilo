<!--
SPDX-FileCopyrightText: The ilo Authors
SPDX-License-Identifier: 0BSD
-->

# 2026.6.8

`ilo shell`, `ilo compose` and `ilo devcontainer` now keep their container between
runs instead of discarding it. The first run in a project builds and creates the
container; later runs reuse it — starting the existing container and attaching to it
— so start-up is faster and whatever you install inside the container is still there
next time. A devcontainer's `onCreateCommand`/`postCreateCommand` therefore run only
once, when the container is created, while `postStartCommand` runs on every start and
`postAttachCommand` on every attach, as the [devcontainer specification](https://containers.dev/implementors/spec/#lifecycle)
intends.

Previously every run started from a fresh, throwaway container (`run --rm`). To get
that clean slate again, pass `--fresh`: it removes the reused container and recreates
it (re-running the creation lifecycle). `--remove-image` now removes the container and
its image when you exit, opting out of reuse entirely.

The container's identity covers its whole definition — the project path, image, build inputs
(including the `Containerfile`'s contents) and run options — so editing any of them makes `ilo`
build a fresh container automatically rather than reuse a stale one. Only the current container
is kept: `ilo` removes a project's earlier, stopped containers so they do not accumulate.

What you may need to do: nothing for a normal upgrade. Be aware that `ilo` now leaves one
stopped container behind per project (named `ilo-<project>-<hash>` and labelled `ilo.managed=true`;
compose manages its own containers per the compose file). Find them with
`docker ps --all --filter label=ilo.managed` and reclaim one with `--fresh` on the next run, with
`--remove-image`, or with your container runtime's `rm`. If a reused container has drifted into a
bad state, `--fresh` is the reset button; `--pull` likewise recreates it so a freshly pulled
`latest` image takes effect. If you attach from several terminals at once, pass `--keep-running`
so exiting one does not stop the container under the others.

---

The `compose`, `devcontainer` and `devfile` commands are available again. `ilo
compose` runs build environments described by a [compose](https://compose-spec.io/)
file, `ilo devcontainer` runs a [devcontainer](https://containers.dev/) (image- or
dockerfile-based, or compose-based via its `dockerComposeFile`), and `ilo devfile`
runs a [devfile](https://devfile.io/) environment. These commands simply become
available again, so no action is required; the native binaries grow slightly because
they pull in JSON and YAML parsers.

---

Run command files discovered automatically in your working directory
(`.ilo/ilo.rc` and `.ilo.rc`) are no longer loaded without your confirmation. A
run command file can set any option, and `ilo` expands option values through a
host shell — so command substitution such as `$(...)` inside an `.ilo.rc` placed
in a directory you did not write could run arbitrary commands on your machine.
`ilo` now loads an auto-discovered file only after you have trusted it.

The first time `ilo` finds such a file it prompts you; answer yes to trust and
load it. The decision is remembered in `$XDG_CONFIG_HOME/ilo/trusted-rc`
(override with `ILO_TRUST_FILE`) and is bound to the file's path and content, so
editing or moving the file prompts again.

What you may need to do:

- **Interactive use:** run `ilo` once in each project and accept the prompt — no
  other change is needed.
- **CI and other non-interactive runs:** an untrusted file is now skipped instead
  of loaded, so a build that relied on auto-loading one will run without it. Point
  `ilo` at the file explicitly with `ILO_RC=.ilo.rc` (files named via `ILO_RC` are
  loaded without a prompt), or pre-populate the trust store on the runner.

Files loaded with an explicit `@argument-file`, files named via `ILO_RC`, and the
`--no-rc` flag are unaffected.
