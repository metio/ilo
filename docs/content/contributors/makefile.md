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
  sign-waiver                     Sign the WAIVER

example:
  ec-redis-java11                 Example using 'ilo compose' w/ redis & java 11
  es-default                      Example using 'ilo shell' w/ default settings
  es-openjdk11                    Example using 'ilo shell' w/ openjdk11
  es-maven                        Example using 'ilo shell' w/ maven

hacking:
  build                           Build everything
  native-image                    Create a native image using GraalVM
  clean                           Clean build artifacts

other:
  help                            Show this help
```

Run any of the available targets like this:

```shell script
$ make verify
```
