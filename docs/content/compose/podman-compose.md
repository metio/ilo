---
title: podman-compose
date: 2020-04-13
menu:
  main:
    parent: compose
    identifier: compose_podman_compose
categories:
- compose
tags:
- runtime
- podman-compose
---

[podman-compose](https://github.com/containers/podman-compose) is one of the available `ilo compose` runtimes. Force `ilo` to use `podman-compose` like this:

```shell script
$ ilo compose --runtime podman-compose

# alias
$ ilo compose --runtime pc
```
