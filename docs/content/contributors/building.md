---
title: Building ilo
date: 2020-04-13
menu:
  main:
    parent: 'Contributors'
categories:
- Contributors
tags:
- build
- environment
---

`ilo` requires a certain set of software installed on your system in order to be built.

## Prerequisites

- [git](https://git-scm.com/) for version control
- [minisign](https://jedisct1.github.io/minisign/) to sign the waiver as a contributor
- [gpg](https://gnupg.org/) to sign your the commit which adds the waiver

## Manual Setup

- [Java JDK](https://jdk.java.net/) to compile the code
- [Maven](https://maven.apache.org/) to build the project
- [hugo](https://gohugo.io/) in order to create the website
- [GraalVM](https://www.graalvm.org/) to build a native executable

## ilo Setup

- [ilo](../../usage/install) to open the reproducible build environment for `ilo` itself
- One of the [runtimes](../../shell/runtimes) that `ilo shell` supports.

## Building

In case you have Java and Maven locally installed call:

```shell script
# run all tests
$ mvn verify
```

In case you have `ilo` installed, call this:

```shell script
# build the project
$ ilo @build/once

# open a shell with a pre-defined build environment
$ ilo
```

In case you want to build the website do this:

```shell script
# build website
$ hugo --minify --i18n-warnings --path-warnings --source docs

# serve website
$ hugo server --minify --i18n-warnings --path-warnings --source docs --watch
```

Take a look at the [Makefile](../makefile) as an easy way to call all these commands.
