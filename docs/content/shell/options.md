---
title: Options
date: 2020-04-13
menu:
  main:
    parent: shell
    identifier: shell_options
categories:
- shell
tags:
- options
---

The `ilo shell` command can be configured with the following command line options. From your terminal, use `ilo shell --help` to get a list of all options, and their default values.

## How a session is reused

`ilo shell` keeps your container between runs. The first time you run it in a project, `ilo` builds (or pulls) the image and **creates** a long-lived container. When you exit, the container is **stopped but kept**. The next time you run `ilo shell` in the same project with the same image, `ilo` simply **starts** that container again and attaches to it — so it comes up instantly and anything you installed inside it is still there.

The container is named `ilo-<project>-<hash>` (for example `ilo-my-project-1a2b3c4d5e6f`). The hash covers everything that defines the container — the project path, the image, the build inputs **including your `Containerfile`'s contents**, and the run options (`--env`, `--volume`, `--publish`, `--working-dir`, runtime options, …). So if you edit the `Containerfile`, change the image, or add an `--env`, `ilo` builds a **fresh** container automatically; leave the definition unchanged and it reuses the existing one. Only the current container is kept — `ilo` removes this project's earlier, stopped containers so they do not pile up. Containers are labelled `ilo.managed=true` and `ilo.project=<dir>`, so you can also find them yourself with `docker ps --all --filter label=ilo.managed`.

Use [`--fresh`](#--fresh) when you want a clean slate even though nothing changed — to re-pull a `latest` image, or to reset a container whose state has drifted — or [`--remove-image`](#--remove-image) to remove the container and its image entirely when you exit.

You can attach several terminals to the same running container. By default the container is stopped when you exit, which would interrupt the others — use [`--keep-running`](#--keep-running) in the additional terminals (or all of them) to leave it running.

## `--context`

The `--context` option allows to specify the context when building an image with `--containerfile`/`--dockerfile`.

```console
# specify custom context
$ ilo shell --context ./local/folder

# do not specify context
$ ilo shell
```

By default, `--context` is set to `.` (the current directory).

## `--containerfile`/`--dockerfile`

The `--containerfile`/`--dockerfile` option can be used to specify a local `Containerfile`/`Dockerfile`. Specifying such a file will cause `ilo` to build your image first, and then open a shell into a container based on that image.

```console
# specify custom Containerfile
$ ilo shell --containerfile some/where/your.containerfile

# do not specify Containerfile
$ ilo shell
```

By default, `--containerfile`/`--dockerfile` is not set to any value.

## `--debug`

The `--debug` option toggles whether `ilo` should print the runtime commands into your terminal before executing them. This can be useful in case you want to move away from `ilo` and just use your preferred runtime instead.

```console
# print runtime commands
$ ilo shell --debug

# do not print runtime commands
$ ilo shell --debug=false
$ ilo shell
```

By default, `--debug` is not enabled.

## `--env`

The `--env` option can be used to specify environment variables for your container.

```console
# specify env variable
$ ilo shell --env key=value

# do not specify env variable
$ ilo shell
```

By default, `--env` does not set any environment variables.

## `--fresh`

The `--fresh` option discards the reused container and starts from a clean slate. `ilo` removes any existing container for this project and image, then rebuilds and recreates it from scratch. Use it when you want to start over — for example after the container's state has drifted, or to force a rebuild against an updated `Containerfile`.

```console
# discard the reused container and recreate it
$ ilo shell --fresh

# reuse the existing container (default)
$ ilo shell
```

By default, `--fresh` is not enabled.

## `--keep-running`

The `--keep-running` option leaves the container running after you exit, instead of stopping it. This is useful when you attach to the same container from several terminals: by default the first terminal to exit stops the container and ends the others' sessions, which `--keep-running` avoids.

```console
# leave the container running after exit
$ ilo shell --keep-running

# stop the container on exit (default)
$ ilo shell
```

By default, `--keep-running` is not enabled. The container is still reused on the next run either way; this only controls whether it keeps running in the meantime.

## `--hostname`

The `--hostname` option can be used to set the hostname of your container.

```console
# set hostname
$ ilo shell --hostname you.home.arpa

# do not set hostname
$ ilo shell
```

By default, `--hostname` is not set to any value.

## `--interactive`

The `--interactive` option can be used to control whether you want an interactive session (default) or just want to execute a single command (e.g. during CI builds).

```console
# run interactively
$ ilo shell --interactive

# run non-interactive
$ ilo shell --no-interactive
$ ilo shell --interactive=false

# run interactively
$ ilo shell
```

By default, `--interactive` is enabled.

## `--missing-volumes`

The `--missing-volumes` option controls how `ilo` should deal with non-existing local directories that you want to mount into the container. The default behavior is `CREATE` which creates the directory on the host machine before creating the container and mounting the new empty directory into it. Changing this option to `WARN` causes will result in a warning that a local directory cannot be mounted. `ilo` will still create the container, just not with that missing directory mounted. The `ERROR` option causes `ilo` to fail in case you specify a local directory to mount that does not exist. All three options are case-insensitive, e.g. use `create`, `CREATE`, or `CreATE`.

```console
# create missing local directories
$ ilo shell ----missing-volumes=CREATE

# warn on missing local directories
$ ilo shell ----missing-volumes=WARN

# error on missing local directories
$ ilo shell ----missing-volumes=ERROR
```

## `--mount-project-dir`

The `--mount-project-dir` option can be used to toggle whether the current directory should be mounted into the container. The location inside the container is specified with `--working-dir`.

```console
# mount current directory into container
$ ilo shell --mount-project-dir

# do not mount directory
$ ilo shell --no-mount-project-dir
$ ilo shell --mount-project-dir=false

# mount directory
$ ilo shell
```

By default, `--mount-project-dir` is enabled.

## `--publish`

The `--publish` option can be used to publish ports of your container to your local system.

```console
# binds your local 8080 port to port 80 of the container
$ ilo shell --publish 8080:80

# do not expose any ports
$ ilo shell
```

By default, `--publish` does not exposes any ports.

## `--pull`

The `--pull` option can be used to pull the specified image before opening a new shell. This is especially useful for teams using a `latest` tag for their image. The image will only be pulled in case the registry contains a newer image than locally available. Because a reused container would never see the freshly pulled image, `--pull` also recreates the container (like [`--fresh`](#--fresh)) so the new image takes effect.

```console
# pull image before opening shell
$ ilo shell --pull

# do not pull image before opening shell
$ ilo shell --no-pull
$ ilo shell --pull=false

# do not pull image before opening shell
$ ilo shell
```

By default, `--pull` is not enabled.

## `--remove-image`

The `--remove-image` option opts out of container reuse: after you close your shell, or the non-interactive command finishes, `ilo` removes the container **and** its image from your local system instead of keeping them. This restores the clean-slate-every-run behavior and is especially useful in combination with `--containerfile`/`--dockerfile`.

```console
# remove the container and its image after the shell is closed
$ ilo shell --remove-image

# keep the container and image for reuse (default)
$ ilo shell --no-remove-image
$ ilo shell --remove-image=false
$ ilo shell
```

By default, `--remove-image` is not enabled, so the container and image are kept for reuse.

## `--runtime`

The `--runtime` option can be used to force the usage of a specific runtime. See [runtimes](../runtimes) for details.

```console
# force to use podman
$ ilo shell --runtime podman

# force to use docker
$ ilo shell --runtime docker

# force to use lxd
$ ilo shell --runtime lxd

# auto select
$ ilo shell
```

## `--runtime-option`

The `--runtime-option` option can be specified multiple times and contains options for the underlying runtime.

```console
# specify custom option(s)
$ ilo shell --runtime-option=--remote
$ ilo shell --runtime-option=--remote --runtime-option=--syslog

# do not specify custom option
$ ilo shell
```

By default, `--runtime-option` is set to an empty array.

## `--runtime-pull-option`

The `--runtime-pull-option` can be specified multiple times and contains options for the runtimes' `pull` command.

```console
# specify custom option(s)
$ ilo shell --runtime-pull-option=--all-tags
$ ilo shell --runtime-pull-option=--all-tags --runtime-pull-option=--quiet

# do not specify custom option
$ ilo shell
```

By default, `--runtime-pull-option` is set to an empty array.

## `--runtime-build-option`

The `--runtime-build-option` can be specified multiple times and contains options for the runtimes' `build` command.

```console
# specify custom option(s)
$ ilo shell --runtime-build-option=--squash
$ ilo shell --runtime-build-option=--squash --runtime-build-option=--no-cache

# do not specify custom option
$ ilo shell
```

By default, `--runtime-build-option` is set to an empty array.

## `--runtime-run-option`

The `--runtime-run-option` can be specified multiple times and contains options for the runtimes' `run` command.

```console
# specify custom option(s)
$ ilo shell --runtime-run-option=--privileged
$ ilo shell --runtime-run-option=--privileged --runtime-run-option=--replace

# do not specify custom option
$ ilo shell
```

By default, `--runtime-run-option` is set to an empty array.

## `--runtime-cleanup-option`

The `--runtime-cleanup-option` can be specified multiple times and contains options for the runtimes' `rmi` command.

```console
# specify custom option(s)
$ ilo shell --runtime-cleanup-option=--force
$ ilo shell --runtime-cleanup-option=--force --runtime-cleanup-option=--all

# do not specify custom option
$ ilo shell
```

By default, `--runtime-cleanup-option` is set to an empty array.

## `--shell`

The `--shell` option sets the shell that `ilo` runs when you attach to the container interactively without giving an explicit command. Because the container is reused and entered with `exec`, `ilo` has to name a shell to start; pick one your image actually ships.

```console
# attach with bash
$ ilo shell --shell /bin/bash

# attach with the default POSIX shell
$ ilo shell
```

By default, `--shell` is `/bin/sh`, which exists on practically every image. When you pass a command to run instead of an interactive session (for example `ilo shell my-image ls -la`), that command is used and `--shell` is ignored.

## `--volume`

The `--volume` option can be used to mount additional volumes into your container.

```console
# mount extra volume
$ ilo shell --volume $HOME/.m2/repository:/root/.m2/repository:z

# do not mount extra volume
$ ilo shell
```

By default, `--volume` does not mount any extra volumes.

## `--working-dir`

The `--working-dir` option can be used to set the working directory inside your container.

```console
# specify custom working directory
$ ilo shell --working-dir /project/dir

# use current directory
$ ilo shell
```

By default, `--working-dir` uses the current directory on the host machine in order to have the same paths on the host and in the container.
