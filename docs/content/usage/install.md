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

## Linux

The Linux distribution of `ilo` is a native executable and does not require any additional software to be installed.

```shell script
$ export ILO_VERSION={{ getenv "ILO_RELEASE" }}
$ cd /path/to/ilo/installation/folder
$ curl --location https://github.com/metio/ilo/releases/download/${ILO_VERSION}/ilo-${ILO_VERSION}-linux.zip --output ilo.zip
$ unzip ilo.zip
$ chmod +x ilo-${ILO_VERSION}/ilo
$ ln --symbolic --relative ilo-${ILO_VERSION}/ilo ~/.local/bin/ilo
```

## MacOS

The MacOS distribution of `ilo` is a native executable and does not require any additional software to be installed.

**THIS IS UNTESTED** - [help us](https://github.com/metio/ilo/issues/47) to get this working.

```shell script
$ export ILO_VERSION={{ getenv "ILO_RELEASE" }}
$ cd /path/to/ilo/installation/folder
$ curl --location https://github.com/metio/ilo/releases/download/${ILO_VERSION}/ilo-${ILO_VERSION}-mac.zip --output ilo.zip
$ unzip ilo.zip
$ chmod +x ilo-${ILO_VERSION}/ilo
$ ln --symbolic --relative ilo-${ILO_VERSION}/ilo /usr/local/bin/ilo
```

## Windows

The Windows distribution of `ilo` requires at least [Java 11](https://www.oracle.com/javadownload) to be installed.

**THIS IS UNTESTED** - [help us](https://github.com/metio/ilo/issues/46) to get this working.

```shell script
# this has to executed only once per user account
$ MD %USERPROFILE%\bin
$ control sysdm.cpl # manually add '%USERPROFILE%\bin' to the PATH of your user account
$ export ILO_VERSION={{ getenv "ILO_RELEASE" }}
$ cd /path/to/ilo/installation/folder
$ curl --location https://github.com/metio/ilo/releases/download/${ILO_VERSION}/ilo-${ILO_VERSION}-java11.zip --output ilo.zip
$ unzip ilo.zip
$ chmod +x ilo-${ILO_VERSION}/ilo.bat
$ ln --symbolic --relative ilo-${ILO_VERSION}/ilo %USERPROFILE%\bin\ilo.bat
```

## Other

For all other platforms that support at least [Java 11](https://www.oracle.com/javadownload), run something like this:

```shell script
$ export ILO_VERSION={{ getenv "ILO_RELEASE" }}
$ cd /path/to/ilo/installation/folder
$ curl --location https://github.com/metio/ilo/releases/download/${ILO_VERSION}/ilo-${ILO_VERSION}-java11.zip --output ilo.zip
$ unzip ilo.zip
$ chmod +x ilo-${ILO_VERSION}/ilo
# move 'ilo' start script to your preferred location
```
