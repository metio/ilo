<!--
SPDX-FileCopyrightText: The ilo Authors
SPDX-License-Identifier: 0BSD
-->

# 2026.6.8

The `compose`, `devcontainer` and `devfile` commands are available again. `ilo
compose` runs build environments described by a [compose](https://compose-spec.io/)
file, `ilo devcontainer` runs a [devcontainer](https://containers.dev/) (image- or
dockerfile-based, or compose-based via its `dockerComposeFile`), and `ilo devfile`
runs a [devfile](https://devfile.io/) environment. This is additive — existing
`ilo shell` usage is unchanged and no action is required. The native binaries grow
slightly because these commands pull in JSON and YAML parsers.

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
