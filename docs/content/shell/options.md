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

## `--runtime`

The `--runtime` option can be used to force the usage of a specific runtime. See [runtimes](../runtimes) for details.

```shell script
# force to use podman
$ ilo shell --runtime podman

# force to use docker
$ ilo shell --runtime docker

# force to use lxd
$ ilo shell --runtime lxd

# auto select
$ ilo shell
```

In case no `--runtime` is specified, `ilo shell` will automatically select one of the runtimes installed on your local system and prefers `podman` over `docker` over `lxd`.

## `--pull`

The `--pull` option can be used to pull the specified image before opening a new shell. This is especially useful for teams using a `latest` tag for their image. The image will only pulled in case the registry contains a newer image than locally available.

```shell script
# pull image before opening shell
$ ilo shell --pull

# do not pull image before opening shell
$ ilo shell --no-pull
$ ilo shell --pull=false

# do not pull image before opening shell
$ ilo shell 
```

By default, `--pull` is not enabled.

## `--interactive`

The `--interactive` option can be used to control whether you want an interactive session (default) or just want to execute a single command (e.g. during CI builds).

```shell script
# run interactively
$ ilo shell --interactive

# run non-interactive
$ ilo shell --no-interactive
$ ilo shell --interactive=false

# run interactively
$ ilo shell
```

By default, `--interactive` is enabled.

## `--mount-project-dir`

The `--mount-project-dir` option can be used to toggle whether the current directory should be mounted into the container.

```shell script
# mount current directory into container
$ ilo shell --mount-project-dir

# do not mount directory
$ ilo shell --no-mount-project-dir
$ ilo shell --mount-project-dir=false

# mount directory
$ ilo shell
```

By default, `--mount-project-dir` is enabled.

## `--volume`

The `--volume` option can be used to mount additional volumes into your container.

```shell script
# mount extra volume
$ ilo shell --volume $HOME/.m2/repository:/root/.m2/repository:Z

# do not mount extra volume
$ ilo shell
```

By default, `--volume` does not mount any extra volumes.

## `--env`

The `--env` option can be used to specify environment variables for your container.

```shell script
# specify env variable
$ ilo shell --env key=value

# do not specify env variable
$ ilo shell
```

By default, `--env` does not set any environment variables.

## `--publish`

The `--publish` option can be used to publish ports of your container to your local system.

```shell script
# expose container port 80 to local 8080
$ ilo shell --publish 8080:80

# do not expose any ports
$ ilo shell
```

By default, `--publish` does not exposes any ports.

## `--dockerfile`

The `--dockerfile` can be used to specify a local `Dockerfile`. Specifying such a file will cause `ilo` to build your image first, and then open a shell into a container based on that image.

```shell script
# specify custom Dockerfile
$ ilo shell --dockerfile some/where/your.dockerfile

# do not specify Dockerfile
$ ilo shell
```

By default, `--dockerfile` is not set to any value.

## `--remove-image`

The `--remove-image` option causes `ilo` to remove the specified image from your local system after you close your shell or the non-interactive commands finishes. This is especially useful in combination with `--dockerfile`.

```shell script
# remove image after shell was closed
$ ilo shell --remove-image

# do not remove image
$ ilo shell --no-remove-image
$ ilo shell --remove-image=false
$ ilo shell
```

By default, `--remove-image` is not enabled.

## `--runtime-option`

The `--runtime-option` option can be specified multiple times and contains options for the underlying runtime.

```shell script
# specify custom option(s)
$ ilo shell --runtime-option=--remote
$ ilo shell --runtime-option=--remote --runtime-option=--syslog

# do not specify custom option
$ ilo shell
```

By default, `--runtime-option` is set to an empty array.

## `--runtime-pull-option`

The `--runtime-pull-option` can be specified multiple times and contains options for the runtimes' `pull` command.

```shell script
# specify custom option(s)
$ ilo shell --runtime-pull-option=--all-tags
$ ilo shell --runtime-pull-option=--all-tags --runtime-pull-option=--quiet

# do not specify custom option
$ ilo shell
```

By default, `--runtime-pull-option` is set to an empty array.

## `--runtime-build-option`

The `--runtime-build-option` can be specified multiple times and contains options for the runtimes' `build` command.

```shell script
# specify custom option(s)
$ ilo shell --runtime-build-option=--squash
$ ilo shell --runtime-build-option=--squash --runtime-build-option=--no-cache

# do not specify custom option
$ ilo shell
```

By default, `--runtime-build-option` is set to an empty array.

## `--runtime-run-option`

The `--runtime-run-option` can be specified multiple times and contains options for the runtimes' `run` command.

```shell script
# specify custom option(s)
$ ilo shell --runtime-run-option=--privileged
$ ilo shell --runtime-run-option=--privileged --runtime-run-option=--replace

# do not specify custom option
$ ilo shell
```

By default, `--runtime-run-option` is set to an empty array.

## `--runtime-cleanup-option`

The `--runtime-cleanup-option` can be specified multiple times and contains options for the runtimes' `rmi` command.

```shell script
# specify custom option(s)
$ ilo shell --runtime-cleanup-option=--force
$ ilo shell --runtime-cleanup-option=--force --runtime-cleanup-option=--all

# do not specify custom option
$ ilo shell
```

By default, `--runtime-cleanup-option` is set to an empty array.

## `--debug`

The `--debug` option toggles whether `ilo` should print the runtime commands into your terminal before executing them. This can be useful in case you want to move away from `ilo` and just use your preferred runtime instead.

```shell script
# print runtime commands
$ ilo shell --debug

# do not print runtime commands
$ ilo shell --no-debug
$ ilo shell --debug=false
$ ilo shell
```

By default, `--debug` is not enabled.
