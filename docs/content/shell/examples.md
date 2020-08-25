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

## Maven Projects

[Maven](https://maven.apache.org/) caches all downloaded dependencies in your local `~/.m2/repository` directory.

In order to re-use already downloaded dependencies inside the container, specify a `--volumne` like this:

```shell script
# Maven project that mounts local m2 repo
$ ilo shell \
    --volume $HOME/.m2/repository:/root/.m2/repository:Z \
    maven:latest
```

## Gradle Projects

[Gradle](https://gradle.org/) caches all downloaded dependencies in your local `~/.gradle` directory.

In order to re-use already downloaded dependencies inside the container, specify a `--volumne` like this:

```shell script
# Gradle project that mounts local .gradle folder
$ ilo shell \
    --volume $HOME/.gradle:/home/gradle/.gradle:Z \
    gradle:latest
```
