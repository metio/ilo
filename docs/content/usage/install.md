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

None of the widely used package managers have `ilo` in their repository yet. In case you want to help package `ilo` for your preferred operating system, take a look at the open packaging issues for [Linux](https://github.com/metio/ilo/issues/2), [MacOS](https://github.com/metio/ilo/issues/12), and [Windows](https://github.com/metio/ilo/issues/11).

## Linux

The Linux distribution of `ilo` is a native executable and does not require any additional software to be installed.

{{< linuxinstall >}}

## MacOS

The MacOS distribution of `ilo` is a native executable and does not require any additional software to be installed.

{{< macinstall >}}

## Windows

The Windows distribution of `ilo` requires at least [Java 11](https://www.oracle.com/javadownload) to be installed.

**THIS IS UNTESTED** - [help us](https://github.com/metio/ilo/issues/46) to get this working.

{{< windowsinstall >}}

## Other

For all other platforms that support at least [Java 11](https://www.oracle.com/javadownload), run something like this:

{{< otherinstall >}}
