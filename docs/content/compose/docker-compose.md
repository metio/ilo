---
title: docker-compose
date: 2020-04-13
menu:
  main:
    parent: compose
    identifier: compose_docker_compose
categories:
- compose
tags:
- runtime
- docker-compose
---

[docker-compose](https://docs.docker.com/compose/) is one of the available `ilo compose` runtimes. Force `ilo` to use `docker-compose` like this:

```shell script
$ ilo compose --runtime docker-compose

# alias
$ ilo compose --runtime dc
```
