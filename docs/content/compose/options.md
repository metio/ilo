---
title: Options
date: 2020-04-13
menu:
  main:
    parent: compose
    identifier: compose_options
categories:
- compose
tags:
- options
---

The `ilo compose` command can be configured with the following command line options. From your terminal, use `ilo compose --help` to get a list of all options, and their default values.

## `--runtime`

The `--runtime` option can be used to force the usage of a specific runtime. See [runtimes](../runtimes) for details.

```shell script
# force to use docker-compose
$ ilo compose --runtime docker-compose

# force to use podman-compose
$ ilo compose --runtime podman-compose

# force to use pods-compose
$ ilo compose --runtime pods-compose

# force to use footloose
$ ilo compose --runtime footloose

# force to use vagrant
$ ilo compose --runtime vagrant

# auto select
$ ilo compose
```

In case no `--runtime` is specified, `ilo compose` will automatically select one of the runtimes installed on your local system and prefers `docker-compose` over `podman-compose` over `pods-compose` over `footloose` over `vagrant` at the moment.
