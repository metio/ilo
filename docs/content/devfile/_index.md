---
title: ilo devfile
date: 2020-04-13
menu: main
weight: 140
---

The `devfile` command allows interacting with [devfile](https://devfile.io/) as used by [odo](https://odo.dev/) and others.

```console
# open shell using devfile
[you@hostname project-dir]$ ilo devfile
[root@container project-dir]#
```

`ilo` will automatically try the following locations for your `devfile.yaml` file relative to your current directory:

1. `devfile.yaml`
2. `.devfile.yaml`

In case you want to load/use a different location for your `devfile.yaml` file, specify one or more locations like this:

```console
# use custom locations
[you@hostname project-dir]$ ilo devfile some-where-local.yaml /an/absolute/path/appears.yaml
```

The first location that actually exists and can be read by the current user will be used.

Take a look at all available [options](./options) or use `ilo devfile --help` to get a list of all options, and their default values. In order to simplify handling of long command line options, consider using [argument files](../usage/argument-files).
