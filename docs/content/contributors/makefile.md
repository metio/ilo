---
title: Makefile
date: 2020-04-13
menu:
  main:
    parent: 'Contributors'
categories:
- Contributors
tags:
- makefile
---

The [Makefile](https://github.com/metio/ilo/blob/master/Makefile) commonly used task by maintainers of this project. In order to use it, you have to install a variant of [make](https://en.wikipedia.org/wiki/Make_(software)) on your system. Just calling `make` in the root directory will print the help/usage guide:

```shell script
$ make
usage: make [target]

contributing:
  sign-waiver         Sign the WAIVER

hacking:
  install             Install all artifacts into local repository
  verify              Verify all modules
  site                Build website
  site-watch          Build and watch website
  build-env           Open a new shell in a predefined build environment
  build-once          Build the entire project once in a predefined build environment

other:
  help                Show this help
```

Run any of the available targets like this:

```shell script
$ make verify
```
