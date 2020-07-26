---
title: shell
date: 2020-04-13
---

The `shell` command opens a shell in a predefined build environment.

It defaults to use the `fedora:latest` image and executes the default entrypoint/command inside the running container to get a running shell.

It automatically mounts the current working directory (e.g. your project directory) and stops/removes the container once you exit the shell.

`ilo shell` can be used with either [podman](./podman), [docker](./docker), or [lxd](./lxd) by using the `--runtime` switch.

Use `ilo shell --help` to get a list of all options, and their default values.
