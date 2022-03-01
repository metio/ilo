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

[Cargo](https://doc.rust-lang.org/cargo/) caches all downloaded dependencies in your local `~/.cargo` directory.

In order to re-use already downloaded dependencies inside the container, specify a `--volumne` like this:

```shell script
# Gradle project that mounts local .gradle folder
$ ilo shell \
    --volume $HOME/.cargo:/root/.cargo:z \
    rust:latest
```

## Gradle Projects

[Gradle](https://gradle.org/) caches all downloaded dependencies in your local `~/.gradle` directory.

In order to re-use already downloaded dependencies inside the container, specify a `--volumne` like this:

```shell script
# Gradle project that mounts local .gradle folder
$ ilo shell \
    --volume $HOME/.gradle:/home/gradle/.gradle:z \
    gradle:latest
```

## Maven Projects

[Maven](https://maven.apache.org/) caches all downloaded dependencies in your local `~/.m2` directory.

In order to re-use already downloaded dependencies inside the container, specify a `--volumne` like this:

```shell script
# Maven project that mounts local m2 repo
$ ilo shell \
    --volume $HOME/.m2:/root/.m2:z \
    maven:latest
```
