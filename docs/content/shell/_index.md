---
title: shell
date: 2020-04-13
---

The `ilo shell` command can be used to run a single container either interactively (default) or in non-interactive mode (e.g. for CI builds). It can build an image, mount directories automatically, stop containers, remove images, and [customize](./customize-env) the build environment according to the needs of your project.

```shell script
# open shell for local builds
[you@hostname project-dir]$ ilo shell
[root@container project-dir]#

# run command
[you@hostname project-dir]$ ilo shell --no--interactive openjdk:11 mvn verify
[you@hostname project-dir]$ 
```

`ilo shell` will delegate most of its work to one of the supported [runtimes](./runtimes).

The `--pull` flag will cause the image to be pulled first before opening a new shell:

```shell script
$ ilo shell --pull openjdk:11
```

The `--remove-image` flag causes the image to be removed after the shell is closed:

```shell script
$ ilo shell --pull --remove-image openjdk:11
```

Take a look at all available [options](./options) or use `ilo shell --help` to get a list of all options, and their default values. In order to simplify handling of long command line options, consider using [argument files](../usage/argument-files).

```shell script
$ ilo @shell
```
