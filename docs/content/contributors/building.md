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

## breto Setup

[breto](https://github.com/metio/breto) installs the JDK, Maven and hugo at the versions named in `.breto.edn`.

## Manual Setup

Without breto, install the following manually:

- [Java JDK](https://jdk.java.net/) to compile the code
- [Maven](https://maven.apache.org/) to build the project
- [hugo](https://gohugo.io/) in order to create the website
- [GraalVM](https://www.graalvm.org/) to build a native executable

## Building

### Using breto

```console
# put the build tools on PATH
$ eval "$(breto env)"

# build website
$ breto run website

# serve website
$ breto run serve
```

### Using Maven

Build the project with Maven:

```console
# build the project
$ mvn verify

# build native executable
$ mvn verify --define skipNativeBuild=false
```

In case you want to build or work on the website do this:

```console
# build website
$ hugo --minify --printI18nWarnings --printPathWarnings --printUnusedTemplates --source docs

# serve website
$ hugo server --minify --printI18nWarnings --printPathWarnings --printUnusedTemplates --source docs --watch
```
