---
title: smartcd
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_smartcd
categories:
- integration
tags:
- smartcd
---

[smartcd](https://github.com/cxreg/smartcd) can be used to automatically execute a command once you enter a directory. Together with `ilo`, you can do the following:

```shell script
[you@hostname ~]$ cd path/to/your/project
[root@container project-dir]#
```

As soon as you enter the directory of your project, `smartcd` will call `ilo` which in turn will open your build environment for you.
In order to create a setup like this, first [install smartcd](https://github.com/cxreg/smartcd#ok-how-do-i-use-it), enter the root directory of your project and then call `smartcd edit enter` which will open an editor to write a script to execute whenever you enter your project directory. Use something like `ilo @build-env` as the script.

`build-env` is an [arguments](../../usage/argument-files) file.
