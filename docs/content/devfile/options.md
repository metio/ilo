---
title: Options
date: 2020-04-13
menu:
  main:
    parent: devfile
    identifier: devfile_options
categories:
- devfile
tags:
- options
---

The `ilo devfile` command can be configured with the following command line options. From your terminal, use `ilo devfile --help` to get a list of all options, and their default values.

## `--component`

The `--component` option can be used to tell `ilo` which devfile component to use. At the moment only `container` and `image` components are supported.

```console
# use specified component
$ ilo devfile --component some-name

# auto-select component
$ ilo devfile
```

By default, `--debug` is not enabled.

## `--debug`

The `--debug` option toggles whether `ilo` should print the runtime commands into your terminal before executing them. This can be useful in case you want to move away from `ilo` and just use your preferred runtime instead.

```console
# print runtime commands
$ ilo devfile --debug

# do not print runtime commands
$ ilo devfile --debug=false
$ ilo devfile
```

By default, `--debug` is not enabled.

## `--pull`

The `--pull` option can be used to pull the specified image before opening a new shell. This is especially useful for teams using a `latest` tag for their image. The image will only pulled in case the registry contains a newer image than locally available.

```console
# pull image before opening shell
$ ilo devfile --pull

# do not pull image before opening shell
$ ilo devfile --no-pull
$ ilo devfile --pull=false

# do not pull image before opening shell
$ ilo devfile
```

By default, `--pull` is not enabled.

## `--remove-image`

The `--remove-image` option causes `ilo` to remove the specified image from your local system after you close your shell, or the non-interactive commands finishes.

```console
# remove image after shell was closed
$ ilo devfile --remove-image

# do not remove image
$ ilo devfile --no-remove-image
$ ilo devfile --remove-image=false
$ ilo devfile
```

By default, `--remove-image` is not enabled.

## `--runtime`

The `--runtime` option can be used to force the usage of a specific [shell runtime](../../shell/runtimes).

```console
# force to use podman
$ ilo devfile --runtime podman

# force to use docker
$ ilo devfile --runtime docker

# force to use nerdctl
$ ilo devfile --runtime nerdctl

# auto select
$ ilo devfile
```

## `--runtime-option`

The `--runtime-option` option can be specified multiple times and contains options for the underlying runtime.

```console
# specify custom option(s)
$ ilo devfile --runtime-option=--remote
$ ilo devfile --runtime-option=--remote --runtime-option=--syslog

# do not specify custom option
$ ilo devfile
```

By default, `--runtime-option` is set to an empty array.

## `--runtime-pull-option`

The `--runtime-pull-option` can be specified multiple times and contains options for the runtimes' `pull` command.

```console
# specify custom option(s)
$ ilo devfile --runtime-pull-option=--all-tags
$ ilo devfile --runtime-pull-option=--all-tags --runtime-pull-option=--quiet

# do not specify custom option
$ ilo devfile
```

By default, `--runtime-pull-option` is set to an empty array.

## `--runtime-build-option`

The `--runtime-build-option` can be specified multiple times and contains options for the runtimes' `build` command.

```console
# specify custom option(s)
$ ilo devfile --runtime-build-option=--squash
$ ilo devfile --runtime-build-option=--squash --runtime-build-option=--no-cache

# do not specify custom option
$ ilo devfile
```

By default, `--runtime-build-option` is set to an empty array.

## `--runtime-run-option`

The `--runtime-run-option` can be specified multiple times and contains options for the runtimes' `run` command.

```console
# specify custom option(s)
$ ilo devfile --runtime-run-option=--privileged
$ ilo devfile --runtime-run-option=--privileged --runtime-run-option=--replace

# do not specify custom option
$ ilo devfile
```

By default, `--runtime-run-option` is set to an empty array.

## `--runtime-cleanup-option`

The `--runtime-cleanup-option` can be specified multiple times and contains options for the runtimes' `rmi` command.

```console
# specify custom option(s)
$ ilo devfile --runtime-cleanup-option=--force
$ ilo devfile --runtime-cleanup-option=--force --runtime-cleanup-option=--all

# do not specify custom option
$ ilo devfile
```

By default, `--runtime-cleanup-option` is set to an empty array.
