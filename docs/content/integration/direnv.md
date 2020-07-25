---
title: direnv
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_direnv
categories:
- integration
tags:
- direnv
---

[direnv](https://direnv.net/) can be used to automatically execute a command once you enter a directory.
Together with `ilo`, you can do the following:

```shell script
[you@hostname ~]$ cd path/to/your/project
[root@container project-dir]#
```

As soon as you enter the directory of your project, `direnv` will call `ilo` which in turn will open your build environment for you.
In order to create a setup like this, first [install direnv](https://direnv.net/#basic-installation) and then place a `.envrc` file in the root of your project which contains `ilo` and its arguments as its contents.

```shell script
[you@hostname project-dir]$ cat .envrc
ilo @build-env
```

`build-env` is an [arguments](ARGUMENTS.md) file.
