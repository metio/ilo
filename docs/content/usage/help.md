---
title: Help
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_help
categories:
- usage
tags:
- help
---

In order to see the help information of `ilo`, run `ilo --help` or `ilo -h`. Its output looks something like this:

```shell script
$ ilo --help
Usage: ilo [-hV] [@<filename>...] COMMAND

Manage reproducible build environments

      [@<filename>...]   One or more argument files containing options.

Options:
  -h, --help             Show this help message and exit.
  -V, --version          Print version information and exit.

Commands:
  shell                Opens an (interactive) shell for your build environment
  compose              Open an (interactive) shell using podman-/docker-compose
  devcontainer         Open an (interactive) shell using devcontainer
  generate-completion  Generate bash/zsh completion script for ilo.
```

Note that you can use show help infos for each command as well, e.g. like this:

```shell script
$ ilo shell --help
Usage: ilo shell [-hV] [--debug] [--[no-]interactive] [--[no-]mount-project-dir] [--pull] [--remove-image] [--dockerfile=<dockerfile>] [--runtime=<runtime>] [--env=<variables>]... [--publish=<ports>]...
                 [--runtime-build-option=<runtimeBuildOptions>]... [--runtime-cleanup-option=<runtimeCleanupOptions>]... [--runtime-option=<runtimeOptions>]... [--runtime-pull-option=<runtimePullOptions>]...
                 [--runtime-run-option=<runtimeRunOptions>]... [--volume=<volumes>]... <image> [<commands>...]

Opens an (interactive) shell for your build environment

      <image>               The OCI image to use. In case --dockerfile is given as well, this defines the name of the resulting image.
                              Default: fedora:latest
      [<commands>...]
      --debug               Show additional debug information.
      --dockerfile=<dockerfile>
                            The Dockerfile to use.
      --env=<variables>     Specify a environment variable for the container.
  -h, --help                Show this help message and exit.
      --[no-]interactive    Open interactive shell or just run a single command.
      --[no-]mount-project-dir
                            Mount the project directory into the running container.
      --publish=<ports>     Publish container ports to the host system.
      --pull                Pull image before opening shell.
      --remove-image        Remove image after closing the shell.
      --runtime=<runtime>   Specify the runtime to use. If none is specified, use auto-selection.
      --runtime-build-option=<runtimeBuildOptions>
                            Options for the build command of the selected runtime.
      --runtime-cleanup-option=<runtimeCleanupOptions>
                            Options for the cleanup command of the selected runtime.
      --runtime-option=<runtimeOptions>
                            Options for the selected runtime itself.
      --runtime-pull-option=<runtimePullOptions>
                            Options for the pull command of the selected runtime.
      --runtime-run-option=<runtimeRunOptions>
                            Options for the run command of the selected runtime.
  -V, --version             Print version information and exit.
      --volume=<volumes>    Mount a volume into the container.
```

