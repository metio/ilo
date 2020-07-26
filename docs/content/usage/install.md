---
title: Install
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_install
categories:
- usage
tags:
- install
---

Use the following locations in order to download the binary distribution of `ilo`:

- https://github.com/metio/ilo/releases

None of the widely used package managers have `ilo` in their repository yet.
In case you want to help package `ilo` for your preferred operating system, take a look at the [open packaging issues](https://github.com/metio/ilo/issues/2).

## Package Types

### Java 11 Runtime

This package requires a Java 11 runtime installed on the host machine. It uses the identifier `java11`.

### Native Binary

The native binary package has no external dependencies and can be used without installing any additional software. It is only available for linux at the moment and uses the identifier `linux`.

## Installation Methods

### Manual Command Line

```shell script
$ export ILO_VERSION={release-version}
$ export ILO_TYPE=java11 # or linux for native binary
$ cd /path/to/ilo/installation/folder
$ curl --location https://github.com/metio/ilo/releases/download/${ILO_VERSION}/ilo-${ILO_VERSION}-${ILO_TYPE}.zip --output ilo.zip
$ unzip ilo.zip
$ ln --symbolic --relative ilo-${ILO_VERSION}/ilo ~/.local/bin/ilo
```

### Windows

- Download the Java 11 artifact.
- Extract it into a folder of your choice.
- Add `ilo.bat` to your `$PATH`
