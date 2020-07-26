---
title: compose
date: 2020-04-13
---

The `compose` command allows interacting with complex build environments.

`ilo compose` does not mount any directories by default, nor does it automatically execute a specific command.

`ilo compose` can be used with either [podman-compose](./podman-compose), [docker-compose](./docker), [pods-compose](./pods-compose), [footloose](./footloose), or [vagrant](./vagrant) by using the `--runtime` switch.

Use `ilo compose --help` to get a list of all options, and their default values.
