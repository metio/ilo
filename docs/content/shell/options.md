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

The `--mount-project-dir` option can be used to toggle whether the current directory should be mounted into the container.

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

The `--pull` option can be used to pull the specified image before opening a new shell. This is especially useful for teams using a `latest` tag for their image. The image will only be pulled in case the registry contains a newer image than locally available.

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

The `--remove-image` option causes `ilo` to remove the specified image from your local system after you close your shell, or the non-interactive commands finishes. This is especially useful in combination with `--containerfile`/`--dockerfile`.

```console
# remove image after shell was closed
$ ilo shell --remove-image

# do not remove image
$ ilo shell --no-remove-image
$ ilo shell --remove-image=false
$ ilo shell
```

By default, `--remove-image` is not enabled.

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

## `--volume`

The `--volume` option can be used to mount additional volumes into your container.

```console
# mount extra volume
$ ilo shell --volume $HOME/.m2/repository:/root/.m2/repository:z

# do not mount extra volume
$ ilo shell
```

By default, `--volume` does not mount any extra volumes.
