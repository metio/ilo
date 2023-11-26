---
title: Examples
date: 2020-04-13
menu:
  main:
    parent: shell
    identifier: shell_examples
categories:
- shell
tags:
- examples
---

The following examples show how `ilo shell` can be used.

## Cargo Projects

[Cargo](https://doc.rust-lang.org/cargo/) caches all downloaded dependencies in your local `~/.cargo/registry` directory.

In order to re-use already downloaded dependencies inside the container, specify a `--volumne` like this:

```console
# Cargo project that mounts local .cargo folder
$ ilo shell \
    --volume ${HOME}/.cargo/registry:/usr/local/cargo/registry:z \
    rust:latest
```

**Note**: The container path `/usr/local/cargo` is specified in the image used in this example (`rust:latest`). Adjust this value according to the image you are actually using in your project.

## Gradle Projects

[Gradle](https://gradle.org/) caches all downloaded dependencies in your local `~/.gradle` directory.

In order to re-use already downloaded dependencies inside the container, specify a `--volumne` like this:

```console
# Gradle project that mounts local .gradle folder
$ ilo shell \
    --volume ${HOME}/.gradle:/home/gradle/.gradle:z \
    gradle:latest
```

**Note**: The container path `/home/gradle/.gradle` is specified in the image used in this example (`gradle:latest`). Adjust this value according to the image you are actually using in your project.

## Maven Projects

[Maven](https://maven.apache.org/) caches all downloaded dependencies in your local `~/.m2` directory.

In order to re-use already downloaded dependencies inside the container, specify a `--volumne` like this:

```console
# Maven project that mounts local m2 repo
$ ilo shell \
    --volume ${HOME}/.m2:/root/.m2:z \
    maven:latest
```

**Note**: The container path `/root/.m2` is specified in the image used in this example (`maven:latest`). Adjust this value according to the image you are actually using in your project.
