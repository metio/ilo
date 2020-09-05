---
title: Customize Environment
date: 2020-04-13
menu:
  main:
    parent: compose
    identifier: compose_customize
categories:
- compose
tags:
- customize
---

The `ilo compose` command looks for a `docker-compose.yml` file by default and uses the `dev` service to open your new shell. Specify any option, additional volumes, custom images and commands in that file in order to customize your build environment according to your needs. Depending on your preferred [runtime](../runtimes) you probably have to change the location of the file to use with the `--file` flag, e.g. [pods-compose](https://github.com/abalage/pods-compose) will look for a `pods-compose.ini` instead.

## docker-compose

`docker-compose` is the default runtime for `ilo compose`. Take a look at the [reference documentation](https://docs.docker.com/compose/compose-file/) for possible configuration values.

```yaml
version: "3.8"
services:
  redis:
    image: redis:latest
  dev:
    image: openjdk:11
    command: /bin/bash         # custom command
    volumes:
      - .:/some/where          # mount project directory
    working_dir: /some/where   # set working directory inside container
    depends_on:
      - redis                  # auto-start build dependencies
```
