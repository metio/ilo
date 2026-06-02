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

The `ilo devcontainer` command can be configured with the following command line options. From your terminal, use `ilo devcontainer --help` to get a list of all options, and their default values.

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

The `--remove-image` option causes `ilo` to remove the specified image from your local system after you close your shell, or the non-interactive commands finishes.

```console
# remove image after shell was closed
$ ilo devcontainer --remove-image

# do not remove image
$ ilo devcontainer --no-remove-image
$ ilo devcontainer --remove-image=false
$ ilo devcontainer
```

By default, `--remove-image` is not enabled.

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
