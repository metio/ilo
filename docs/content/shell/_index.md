---
title: shell
date: 2020-04-13
---

The `ilo shell` command can be used to run a single container either interactively (default) or in non-interactive mode (e.g. for CI builds). It automatically mounts the current working directory (e.g. your project directory) and stops/removes the container once you exit the shell.

```shell script
# open shell for local builds
[you@hostname project-dir]$ ilo shell --image openjdk:11
[root@container project-dir]#

# run command
[you@hostname project-dir]$ ilo shell --no--interactive --image openjdk:11 mvn verify
[you@hostname project-dir]$ 
```

`ilo shell` will delegate most of its work to one of the supported [runtimes](./runtimes). The first example above will produce something like this:

```shell script
$ docker run --rm \
    --volume $(pwd):$(pwd):Z\
    --workdir $(pwd) \
    --tty --interactive \
    openjdk:11
```

The `--pull` flag will cause the image to be pulled first before opening a new shell:

```shell script
# using ilo
$ ilo shell --image openjdk:11 --pull

# using docker
$ docker pull openjdk:11
$ docker run --rm \
    --volume $(pwd):$(pwd):Z\
    --workdir $(pwd) \
    --tty --interactive \
    openjdk:11
```

The `--remove-image` flag causes the image to be removed after the shell was closed:

```shell script
# using ilo
$ ilo shell --image openjdk:11 --pull --remove-image

# using docker
$ docker pull openjdk:11
$ docker run --rm \
    --volume $(pwd):$(pwd):Z\
    --workdir $(pwd) \
    --tty --interactive \
    openjdk:11
$ docker rmi openjdk:11
```

Take a look at all available [options](./options) or use `ilo shell --help` to get a list of all options, and their default values. In order to simplify handling of long command line options, consider using [argument files](../usage/argument-files).
