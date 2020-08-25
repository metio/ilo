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

## Maven Project

In order to re-use your local m2 directory inside the container, specify another `--volumne` like this:

```shell script
# Maven project that mounts local m2 repo
$ ilo shell \
    --volume $HOME/.m2/repository:/root/.m2/repository:Z \
    maven:3-jdk-11
```
