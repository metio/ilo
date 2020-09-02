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
- direnv
- smartcd
- zsh-autoenv
---

In order to simplify the usage of `ilo`, consider using [run commands](../../usage/run-commands), [argument files](../../usage/argument-files) and a `autoenv`-like tooling. These tools all allow you to just enter a directory and will automatically call `ilo` for you like this:

```shell script
[you@hostname ~]$ cd path/to/your/project
[root@container project-dir]#
```

As soon as you enter the directory of your project, these tools will call `ilo` which in turn will open your build environment for you.

## [autoenv](https://github.com/inishchith/autoenv)

In order to use `autoenv`, first [install it](https://github.com/inishchith/autoenv#install) and then place a `.env` file in the root of your project which contains `ilo` and its arguments as its contents.

```shell script
[you@hostname project-dir]$ cat .env
ilo @build-env
```

## [direnv](https://direnv.net/)

In order to use `direnv`, first [install it](https://direnv.net/#basic-installation) and then place a `.envrc` file in the root of your project which contains `ilo` and its arguments as its contents.

```shell script
[you@hostname project-dir]$ cat .envrc
ilo @build-env
```

## [smartcd](https://github.com/cxreg/smartcd)

In order to use `smartcd`, first [install it](https://github.com/cxreg/smartcd#ok-how-do-i-use-it), enter the root directory of your project and then call `smartcd edit enter` which will open an editor to write a script to execute whenever you enter your project directory. Use something like `ilo @build-env` as the script.

```shell script
[you@hostname project-dir]$ cat ~/.smartcd/scripts/path/to/project-dir/bash_enter
ilo @build-env
```

## [zsh-autoenv](https://github.com/Tarrasch/zsh-autoenv)

In order to use `zsh-autoenv`, first [install it](https://github.com/Tarrasch/zsh-autoenv#installation) and then place a `.autoenv.zsh` file in the root of your project which contains `ilo` and its arguments as its contents.

```shell script
[you@hostname project-dir]$ cat .autoenv.zsh
ilo @build-env
```
