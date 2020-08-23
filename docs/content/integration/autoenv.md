---
title: autoenv
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_autoenv
categories:
- integration
tags:
- autoenv
---

[autoenv](https://github.com/inishchith/autoenv) can be used to automatically execute a command once you enter a directory. Together with `ilo`, you can do the following:

```shell script
[you@hostname ~]$ cd path/to/your/project
[root@container project-dir]#
```

As soon as you enter the directory of your project, `autoenv` will call `ilo` which in turn will open your build environment for you.
In order to create a setup like this, first [install autoenv](https://github.com/inishchith/autoenv#install) and then place a `.env` file in the root of your project which contains `ilo` and its arguments as its contents.

```shell script
[you@hostname project-dir]$ cat .env
ilo @build-env
```

`build-env` is an [arguments](../../usage/argument-files) file.
