---
title: Building
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

- [git](https://git-scm.com/) to fetch the [source code](../git-mirrors) of `ilo`
- [minisign](https://jedisct1.github.io/minisign/) to sign the waiver as a contributor
- [gpg](https://gnupg.org/) or [ssh](https://www.openssh.com/) to sign your the commit which adds the waiver

## ilo Setup

You can use `ilo` to build `ilo`! Make sure your system has the following:

- [ilo](../../usage/install) to open the reproducible build environment for `ilo` itself
- One of the [runtimes](../../shell/runtimes) that `ilo shell` supports.

## Manual Setup

In case you do not have `ilo` installed on your system, install the following manually:

- [Java JDK](https://jdk.java.net/) to compile the code
- [Maven](https://maven.apache.org/) to build the project
- [hugo](https://gohugo.io/) in order to create the website
- [GraalVM](https://www.graalvm.org/) to build a native executable

## Building

### Using ilo

In case you have `ilo` installed, call this:

```shell script
# open a shell with a pre-defined build environment
$ ilo @dev/env

# build the project
$ ilo @dev/build

# build native executable
$ ilo @dev/native

# build website
$ ilo @dev/website

# serve website
$ ilo @dev/serve
```

### Without ilo

In order to build `ilo` without having `ilo` installed call:

```shell script
# build the project
$ mvn verify

# build native executable
$ mvn verify --define skipGraal=false
```

In case you want to build or work on the website do this:

```shell script
# build website
$ hugo --minify --printI18nWarnings --printPathWarnings --printUnusedTemplates --source docs

# serve website
$ hugo server --minify --printI18nWarnings --printPathWarnings --printUnusedTemplates --source docs --watch
```

Take a look at the [Makefile](../makefile) as an easy way to call all these commands.
