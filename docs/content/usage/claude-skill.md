---
title: Claude Skill
date: 2026-06-07
menu:
  main:
    parent: usage
    identifier: usage_claude_skill
    weight: 107
categories:
- usage
tags:
- claude
- skill
- plugin
- agent
---

`ilo` ships a [Claude Code](https://claude.com/claude-code) skill that teaches the
assistant how to run builds, tests, and other tooling **inside** your `ilo`-managed
build environment instead of on the host. Once installed, Claude recognizes that a
project uses `ilo` — from a `.ilo.rc`, `dev/` argument files, a `Containerfile`, a
`devcontainer.json`, a `devfile.yaml`, or a compose file — and reaches for `ilo`
automatically when a command needs a toolchain the host does not have.

The skill is distributed as a plugin hosted in the `ilo` repository, so installing
it needs nothing more than `git` and a Claude Code client.

## Requirements

- [Claude Code](https://docs.claude.com/en/docs/claude-code) (the CLI, desktop, or
  IDE client). Skills are a Claude Code feature; the claude.ai web app has no skill
  installation flow.
- The `ilo` binary on your `PATH` — see [Install](../install).

## Install

Add the marketplace once, then install the plugin:

```console
# add the metio marketplace (hosted in the ilo repository)
/plugin marketplace add metio/ilo

# install the ilo skill from it
/plugin install ilo@metio
```

`metio` is the marketplace and `ilo` is the plugin, so the install reference is
`ilo@metio`. After installing, the skill activates on its own whenever it is
relevant — you do not type a command to invoke it.

## What it does

With the skill installed, Claude will:

- run build/test/tooling commands through `ilo` (for example `ilo @dev/build`, or an
  ad-hoc `ilo go test ./...` when a project has an `.rc` file) instead of on the
  host;
- prefer non-interactive runs suited to automation;
- consult `ilo --help` / `ilo <subcommand> --help` as the source of truth for the
  exact flags your installed version supports.

`Bash(ilo *)` is pre-approved by the skill, so `ilo` invocations run without a
permission prompt. Everything else still goes through Claude Code's normal
permission flow.

## Updating

Refresh the marketplace cache, then reinstall to move an already-installed plugin
to the latest version:

```console
# pull the latest marketplace contents
/plugin marketplace update metio

# reinstall to pick up the new version
/plugin install ilo@metio
```

Alternatively, enable auto-update for the `metio` marketplace in the `/plugin` UI
(or set `"autoUpdate": true` for it in your settings) and Claude Code keeps the
plugin current for you.

Because the skill is distributed separately from the `ilo` binary, it documents the
stable command surface and defers to `ilo --help` for version-specific details — so
it keeps working even when your installed `ilo` is a little ahead of or behind the
skill.
