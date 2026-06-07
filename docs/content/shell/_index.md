---
title: ilo shell
date: 2020-04-13
menu: main
weight: 110
---

The `ilo shell` command opens a shell in a reusable build-environment container, either interactively (default) or in non-interactive mode (e.g. for CI builds). It can build an image, mount directories automatically, keep the container around for fast reuse, remove images, and customize the build environment according to the needs of your project.

```console
# open shell for local builds in default image with default image command
[you@hostname project-dir]$ ilo shell
[root@container project-dir]#

# use custom image
[you@hostname project-dir]$ ilo shell docker.io/library/maven:latest
[root@container project-dir]#

# use custom command
[you@hostname project-dir]$ ilo shell docker.io/library/eclipse-temurin:21 jshell
[root@container project-dir]#

# run command non-interactive
[you@hostname project-dir]$ ilo shell --no-interactive docker.io/library/maven:latest mvn verify
[you@hostname project-dir]$
```

`ilo shell` will delegate most of its work to one of the supported [runtimes](./runtimes). In order to override the default command of your image, specify the command you want to execute just after the image, like this:

```console
[you@hostname project-dir]$ ilo shell docker.io/library/eclipse-temurin:21 /bin/bash
[root@container project-dir]#
```

In order to exit the container either use `exit` or hit `Ctrl + d`:

```console
[root@container project-dir]# exit
[you@hostname project-dir]$
```

Once you have exited the container, `ilo` stops it but keeps it for reuse, so the next `ilo shell` in the same project resumes the same container instantly (any tools you installed and changes you made are still there). A reused container is one built from the exact same definition; changing the image, `Containerfile`, or run options creates a fresh one instead. See [how a session is reused](./options) for details.

To instead discard the container and its image when you exit — restoring a clean slate on the next run — pass the `--remove-image` flag:

```console
[you@hostname project-dir]$ ilo shell --remove-image docker.io/library/eclipse-temurin:21
[root@container project-dir]# exit
```

In order to pull an image first before opening a new shell, use the `--pull` flag like this:

```console
[you@hostname project-dir]$ ilo shell --pull docker.io/library/eclipse-temurin:latest
[root@container project-dir]#
```

In case you want to use a local `Containerfile`/`Dockerfile`, use the `--containerfile`/`--dockerfile` flag like this:

```console
[you@hostname project-dir]$ ilo shell --containerfile your.containerfile your.image:latest
[root@container project-dir]#
```

The resulting image name will be `your.image:latest`. Take a look at all available [options](./options) or use `ilo shell --help` to get a list of all options, and their default values. In order to simplify handling of long command line options, consider using [argument files](../usage/argument-files).
