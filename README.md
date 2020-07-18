# ilo

ilo is a [toolbox](https://github.com/containers/toolbox) inspired tool to create/manage [reproducible build environments](https://reproducible-builds.org/) based on [OCI](https://www.opencontainers.org/) container images.

# Usage

Make sure that `ilo` is installed in your path or otherwise accessible from your terminal.
Two commands are available: `shell` to run a single container and `compose` to run multiple containers defined in a [docker-compose.yml](https://docs.docker.com/compose/compose-file/) file.

### shell

The `ilo shell` command can be used to run a single container either in interactive mode (default) or non-interactive mode (e.g. for CI builds).
It defaults to use the `fedora:latest` image and executes `/bin/bash` inside the running container to get a running shell.
It automatically mounts the current working directory (e.g. your project directory) and stops/removes the container once you exit the shell. `ilo shell` can be used with either [podman](https://podman.io/) (default) or [docker](https://www.docker.com/products/container-runtime) by using the `--runtime` switch. If no runtime is specified `ilo` will auto-detect available runtimes and prefer `podman` over `docker`.
Use `ilo shell --help` to get a list of all options, and their default values.

```shell script
[you@hostname project-dir]$ ilo shell
[root@container project-dir]#
```

### compose

The `ilo compose` command can be used for more complex build environments based on docker-compose.yml files.
Use this command in case your project requires e.g. a database in order to be build. `ilo compose` does not mount any directories by default, nor does it automatically execute a specific command.
It defaults to run with podman-compose but can be used with docker-compose as well by using the `--runtime` switch.
Use `ilo compose --help` to get a list of all options, and their default values.

```shell script
[you@hostname project-dir]$ ilo compose
[root@container project-dir]#
```

## Customize Build Environment

In most cases `fedora:latest` will not be enough to compile/test/package/run your software.
While you can install additional packages inside the container, those changes will be lost once you remove the container.
Instead `ilo` allows you to define your build environment either in a [Dockerfile](https://docs.docker.com/engine/reference/builder/) or any other [OCI Image](https://github.com/opencontainers/image-spec/blob/master/spec.md) compliant way.
Make sure your image can be accessed by everyone in your team and use `ilo shell --image your.image.here:latest` to open a new instance of your build environment.
If you are using `ilo compose`, make sure to specify `your.image.here:latest` as the image in your docker-compose.yml file.

One easy way to share build environments for open source projects, is to use the [automated build system](https://docs.docker.com/docker-hub/builds/) from Docker Hub.
Make sure Docker Hub rebuilds your build environment on every change to master (or any other branch) and have your contributors pull the resulting images to their machines.

## Installation

See [INSTALL](./docs/topics/INSTALL.md) for installation instructions.

## Integration

Take a look at the [integration guide](./docs/topics/INTEGRATION.md) if you want to integrate `ilo` into your workflow.

## User Support

In case you need help, don't panic - we've all been there!
Try the [following resources](./docs/topics/SUPPORT.md) in order to get help.

## Alternatives

In case `ilo` does not offer what you are looking for, take a look at the [following tools](./docs/topics/ALTERNATIVES.md).

## License

```
To the extent possible under law, the author(s) have dedicated all copyright
and related and neighboring rights to this software to the public domain
worldwide. This software is distributed without any warranty.

You should have received a copy of the CC0 Public Domain Dedication along with
this software. If not, see http://creativecommons.org/publicdomain/zero/1.0/.
```

## Mirrors

* https://github.com/metio.wtf/ilo
* https://gitlab.com/metio.wtf/ilo
* https://codeberg.org/metio.wtf/ilo
* https://bitbucket.org/metio-wtf/ilo
