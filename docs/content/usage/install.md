---
title: Install
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_install
    weight: 100
categories:
- usage
tags:
- install
---

Prebuilt binaries of `ilo` are available for each published release at:

- https://github.com/metio/ilo/releases

Download the package for your operating system and put the `ilo` binary in your `$PATH`. Use the JVM variant in case your operating system is not directly supported.

## Huber

In case you are using [huber](https://github.com/innobead/huber), execute the following commands to install `ilo` on Linux/Mac/Windows:

```console
$ huber repo add remote-repo --url https://github.com/metio/ilo
$ huber install ilo
```
