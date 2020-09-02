---
title: shell
date: 2020-04-13
---

The `ilo shell` command can be used to run a single container either interactively (default) or in non-interactive mode (e.g. for CI builds). It can build an image, mount directories automatically, stop containers, remove images, and [customize](./customize-env) the build environment according to the needs of your project.

```shell script
# open shell for local builds in default image with default image command
[you@hostname project-dir]$ ilo shell
[root@container project-dir]#

# use custom image
[you@hostname project-dir]$ ilo shell maven:latest
[root@container project-dir]#

# use custom command
[you@hostname project-dir]$ ilo shell openjdk:11 jshell
[root@container project-dir]#

# run command non-interactive
[you@hostname project-dir]$ ilo shell --no--interactive openjdk:11 mvn verify
[you@hostname project-dir]$ 
```

`ilo shell` will delegate most of its work to one of the supported [runtimes](./runtimes). In order to override the default command of your image, specify the command you want to execute just after the image, like this:

```shell script
[you@hostname project-dir]$ ilo shell openjdk:11 /bin/bash
[root@container project-dir]#
```

In order to exit the container either use `exit` or hit `Ctrl + d`:

```shell script
[root@container project-dir]# exit
[you@hostname project-dir]$
```

Once you have exited the container, `ilo` will automatically stop and remove it. In order to remove the image as well, specify the `--remove-image` flag:

```shell script
[you@hostname project-dir]$ ilo shell --remove-image openjdk:11
[root@container project-dir]# exit
```

In order to pull an image first before opening a new shell, use the `--pull` flag like this:

```shell script
[you@hostname project-dir]$ ilo shell --pull openjdk:latest
[root@container project-dir]#
```

In case you want to use a local `Dockerfile`, use the `--dockerfile` flag like this:

```shell script
[you@hostname project-dir]$ ilo shell --dockerfile your.dockerfile your.image:latest
[root@container project-dir]#
```

The resulting image name will be `your.image:latest`. Take a look at all available [options](./options) or use `ilo shell --help` to get a list of all options, and their default values. In order to simplify handling of long command line options, consider using [argument files](../usage/argument-files) and/or [run commands](../usage/run-commands).
