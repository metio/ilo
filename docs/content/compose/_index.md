---
title: ilo compose
date: 2020-04-13
---

The `ilo compose` command allows interacting with complex build environments, e.g. your project requires a database in order to be build. It integrates with widely used [tools](./runtimes) such as [docker-compose](https://docs.docker.com/compose/) in order to re-use your existing setup as much as possible.

```shell script
# use docker-compose.yml in current directory
[you@hostname project-dir]$ ilo compose
[root@container project-dir]#

# use custom location
[you@hostname project-dir]$ ilo compose --file /some/other/file.yml
[root@container project-dir]#

# use specific service as your dev environment
[you@hostname project-dir]$ ilo compose postgres
[root@container project-dir]#

# use custom service and custom command
[you@hostname project-dir]$ ilo compose postgres /bin/bash
[root@container project-dir]#
```

By default `ilo compose` will look for a `docker-compose.yml` file in the current directory and use the `dev` service to launch your new shell. The following `docker-compose.yml` file shows what a simple setup with Redis and OpenJDK 11 looks like:

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

In order to exit the container either use exit or hit Ctrl + d:

```shell script
[root@container project-dir]# exit
[you@hostname project-dir]$
```

In case you want images to be pulled first, specify the `-pull` flag like this:

```shell script
[you@hostname project-dir]$ ilo compose --pull
[root@container project-dir]#
```

If you want to force (re-)building your container images first, use the `--build` flag like this:

```shell script
[you@hostname project-dir]$ ilo compose --build
[root@container project-dir]#
```

Non-interactive CI builds can use `--no-interactive` like this:

```shell script
[you@hostname project-dir]$ ilo compose --no-interactive dev mvn verify
[you@hostname project-dir]$f
```

Take a look at all available [options](./options) or use `ilo compose --help` to get a list of all options, and their default values. In order to simplify handling of long command line options, consider using [argument files](../usage/argument-files).
