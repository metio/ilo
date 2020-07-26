---
title: compose
date: 2020-04-13
---

The `ilo compose` command allows interacting with complex build environments, e.g. your project requires a database in order to be build. It integrates with widely used [tools](./runtimes) such as [docker-compose](https://docs.docker.com/compose/) in order to re-use as much existing infrastructure as possible.

```shell script
[you@hostname project-dir]$ ilo compose
[root@container project-dir]#
```

Take a look at all available [options](./options) or use `ilo compose --help` to get a list of all options, and their default values. In order to simplify handling of long command line options, consider using [argument files](../usage/argument-files).

```shell script
# using argument file
$ ilo @shell
```
