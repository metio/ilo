---
title: Options
date: 2020-04-13
menu:
  main:
    parent: devcontainer
    identifier: devcontainer_options
categories:
- devcontainer
tags:
- options
---

The `ilo devcontainer` command reads a `devcontainer.json` (it looks for `.devcontainer/devcontainer.json` and `.devcontainer.json`, in that order) and opens a shell in the environment it describes. It can be configured with the following command line options. From your terminal, use `ilo devcontainer --help` to get a list of all options, and their default values.

## How a session works

`ilo devcontainer` keeps your container between runs, just like [`ilo shell`](../../shell/options#how-a-session-is-reused):

1. **First run:** `ilo` builds (or pulls) the image described by `image`/`build`, **creates** a long-lived container, and runs the creation lifecycle commands once.
2. **Later runs:** `ilo` **starts** the same container again and attaches to it. The creation lifecycle commands are *not* run again — only the start- and attach-time ones.
3. **On exit:** the container is **stopped but kept**, so the next run resumes it.

Editing your `devcontainer.json` — the image, build inputs (including a referenced `Containerfile`), the run configuration, or a lifecycle command such as `postCreateCommand` — makes `ilo` build a fresh container automatically and re-run the creation lifecycle, because the file's contents are part of the container's identity. So your edits are picked up on the next run without any extra flag. Use [`--fresh`](#--fresh) when you want that even though nothing changed (for example to re-pull a `latest` image or reset a container whose state has drifted), or [`--remove-image`](#--remove-image) to remove the container and image entirely when you exit.

If the `devcontainer.json` uses `dockerComposeFile`, `ilo` delegates to [`ilo compose`](../../compose/options) instead and brings the services up the same way.

## Lifecycle commands

`ilo` runs the [lifecycle scripts](https://containers.dev/implementors/spec/#lifecycle) from your `devcontainer.json` around the session:

| Property | When it runs | Where |
| --- | --- | --- |
| `initializeCommand` | before the container is created | on your **host** |
| `onCreateCommand` | once, when the container is created | in the container |
| `updateContentCommand` | once, when the container is created | in the container |
| `postCreateCommand` | once, when the container is created | in the container |
| `postStartCommand` | every time the container is started | in the container |
| `postAttachCommand` | every time you attach | in the container |

Each command may be a string (run through the container's shell), an array (run directly), or an object of named commands. Object entries run one after another. Every lifecycle command can be turned off with its [`--execute-…-command`](#--execute-initialize-command) flag.

## Supported and unsupported

`ilo` is a small wrapper around your container runtime, so it supports the parts of the specification that map onto running a container, and deliberately ignores the parts that belong to a full IDE integration.

| Supported | Not supported |
| --- | --- |
| `image`, `build` (`dockerfile`, `context`, `args`, `target`, `cacheFrom`) | `features` |
| `dockerComposeFile`, `service` | `customizations` (e.g. VS Code extensions/settings) |
| `workspaceFolder`, `containerEnv`, `mounts`, `forwardPorts`, `appPort` | `secrets`, `hostRequirements` |
| `containerUser`/`remoteUser`, `runArgs`, `init`, `privileged`, `capAdd`, `securityOpt` | `waitFor`, `userEnvProbe` |
| `initializeCommand`, `onCreateCommand`, `updateContentCommand`, `postCreateCommand`, `postStartCommand`, `postAttachCommand` | |
| `overrideCommand` (set `false` to keep the image's own long-running process) | |

If you rely on `features` or editor `customizations`, use the [official `@devcontainers/cli`](https://github.com/devcontainers/cli) for those parts — `ilo` will not error on them, it simply does not act on them.

## `--debug`

The `--debug` option toggles whether `ilo` should print the runtime commands into your terminal before executing them. This can be useful in case you want to move away from `ilo` and just use your preferred runtime instead.

```console
# print runtime commands
$ ilo devcontainer --debug

# do not print runtime commands
$ ilo devcontainer --debug=false
$ ilo devcontainer
```

By default, `--debug` is not enabled.

## `--mount-project-dir`

The `--mount-project-dir` option can be used to toggle whether the current directory should be mounted into the container.

```console
# mount current directory into container
$ ilo devcontainer --mount-project-dir

# do not mount directory
$ ilo devcontainer --no-mount-project-dir
$ ilo devcontainer --mount-project-dir=false

# mount directory
$ ilo devcontainer
```

By default, `--mount-project-dir` is enabled.

## `--pull`

The `--pull` option can be used to pull the specified image before opening a new shell. This is especially useful for teams using a `latest` tag for their image. The image will only pulled in case the registry contains a newer image than locally available.

```console
# pull image before opening shell
$ ilo devcontainer --pull

# do not pull image before opening shell
$ ilo devcontainer --no-pull
$ ilo devcontainer --pull=false

# do not pull image before opening shell
$ ilo devcontainer
```

By default, `--pull` is not enabled.

## `--remove-image`

The `--remove-image` option opts out of container reuse: after you close your shell, `ilo` removes the container **and** its image instead of keeping them. The next run then rebuilds from scratch and re-runs the creation lifecycle.

```console
# remove the container and its image after the shell is closed
$ ilo devcontainer --remove-image

# keep the container and image for reuse (default)
$ ilo devcontainer --no-remove-image
$ ilo devcontainer --remove-image=false
$ ilo devcontainer
```

By default, `--remove-image` is not enabled, so the container and image are kept for reuse.

## `--fresh`

The `--fresh` option discards the reused container and starts from a clean slate: `ilo` removes the existing container, rebuilds and recreates it, and re-runs the creation lifecycle (`onCreateCommand`, `updateContentCommand`, `postCreateCommand`). Use it to start over after the container's state has drifted, or to pick up changes to your `devcontainer.json`.

```console
# discard the reused container and recreate it
$ ilo devcontainer --fresh

# reuse the existing container (default)
$ ilo devcontainer
```

By default, `--fresh` is not enabled.

## `--shell`

The `--shell` option sets the shell that `ilo` runs when you attach to the container. Because the container is reused and entered with `exec`, `ilo` has to name a shell to start.

```console
# attach with sh
$ ilo devcontainer --shell /bin/sh

# attach with bash (default)
$ ilo devcontainer
```

By default, `--shell` is `/bin/bash`, since devcontainer images conventionally ship bash. Point it at another shell if your image does not.

## `--execute-initialize-command`

The `--execute-…-command` flags toggle each [lifecycle command](#lifecycle-commands) individually. There is one for every stage:

- `--execute-initialize-command` — run `initializeCommand` on the host before the container is created.
- `--execute-on-create-command` — run `onCreateCommand` when the container is created.
- `--execute-update-content-command` — run `updateContentCommand` when the container is created.
- `--execute-post-create-command` — run `postCreateCommand` when the container is created.
- `--execute-post-start-command` — run `postStartCommand` every time the container is started.
- `--execute-post-attach-command` — run `postAttachCommand` every time you attach.

```console
# skip the postCreateCommand (e.g. to attach faster while debugging)
$ ilo devcontainer --no-execute-post-create-command

# skip the initializeCommand
$ ilo devcontainer --execute-initialize-command=false

# run every lifecycle command (default)
$ ilo devcontainer
```

By default, every `--execute-…-command` flag is enabled, so all lifecycle commands present in your `devcontainer.json` run.

## `--compose-runtime` / `-C`

The `--compose-runtime` option can be used to force the usage of a specific [compose runtime](../../compose/runtimes).

```console
# force to use podman-compose
$ ilo devcontainer --compose-runtime podman-compose

# force to use docker-compose
$ ilo devcontainer --compose-runtime docker-compose

# auto select
$ ilo devcontainer
```

In case no `--compose-runtime` is specified, `ilo devcontainer` will automatically select one of the runtimes installed on your local system and prefers `docker-compose` over `podman-compose`.

## `--shell-runtime` / `-S`

The `--shell-runtime` option can be used to force the usage of a specific [shell runtime](../../shell/runtimes).

```console
# force to use podman
$ ilo devcontainer --shell-runtime podman

# force to use docker
$ ilo devcontainer --shell-runtime docker

# force to use nerdctl
$ ilo devcontainer --shell-runtime nerdctl

# auto select
$ ilo devcontainer
```
