---
title: shell
date: 2020-04-13
---

The `ilo shell` command can be used to run a single container either interactively (default) or in non-interactive mode (e.g. for CI builds). It automatically mounts the current working directory (e.g. your project directory) and stops/removes the container once you exit the shell.

```shell script
# open shell for local builds
[you@hostname project-dir]$ ilo shell @build
[root@container project-dir]#

# run command
[you@hostname project-dir]$ ilo shell --no--interactive @build mvn verify
[you@hostname project-dir]$ 
```

CLI arguments starting with **@** are so called [argument files](../usage/argument-files). Take a look at all available [options](./options) or use `ilo shell --help` to get a list of all options, and their default values. `ilo shell` supports multiple [runtimes](./runtimes) using the `--runtime` flag.
