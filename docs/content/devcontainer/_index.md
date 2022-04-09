---
title: ilo devcontainer
date: 2020-04-13
menu: main
weight: 130
---

The `devcontainer` command allows interacting with [devcontainers](https://code.visualstudio.com/docs/remote/containers) as used by [Visual Studio Code](https://code.visualstudio.com/) and [GitHub Codespaces](https://docs.github.com/en/codespaces/overview).

Make sure to specify `your.image.here:some-tag` as the image in your `devcontainer.json` file. Take a look at the [reference documentation](https://code.visualstudio.com/docs/remote/devcontainerjson-reference) for all available options for that JSON file.

```console
# open shell in devcontainer
[you@hostname project-dir]$ ilo devcontainer
[root@container project-dir]#
```

`ilo` will automatically try the following locations for your `devcontainer.json` file relative to your current directory:

1. `.devcontainer/devcontainer.json`
2. `.devcontainer.json`

In case you want to load/use a different location for your `devconatiner.json` file, specify one or more locations like this:

```console
# use custom locations
[you@hostname project-dir]$ ilo devcontainer some-where-local.json /an/absolute/path/appears.json
```

The first location that actually exists and can be read by the current user will be used.

Take a look at all available [options](./options) or use `ilo devcontainer --help` to get a list of all options, and their default values. In order to simplify handling of long command line options, consider using [argument files](../usage/argument-files).
