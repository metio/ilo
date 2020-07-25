---
title: Install
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_install
categories:
- usage
tags:
- install
---

Use the following locations in order to download the binary distribution of `ilo`:

- https://github.com/metio/ilo/releases

None of the widely used package managers have `ilo` in their repository yet.
In case you want to help package `ilo` for your preferred operating system, take a look at the [open packaging issues](https://github.com/metio/ilo/issues/2).

## Package Types

### Java 11 Runtime

This package requires a Java 11 runtime installed on the host machine. It uses the identifier `java11`.

## Installation Methods

### Manual Command Line (Mac/Linux)

```shell script
$ export ILO_VERSION={release-version}
$ export ILO_TYPE=java11 # or linux/mac/windows for native binaries
$ cd /path/to/ilo/installation/folder
$ curl --location https://github.com/metio/ilo/releases/download/${ILO_VERSION}/ilo-${ILO_VERSION}-${ILO_TYPE}.zip --output ilo.zip
$ unzip ilo.zip
$ ln --symbolic --relative ilo-${ILO_VERSION}/ilo ~/.local/bin/ilo
```

### Windows

- Download the Java 11 artifact.
- Extract it into a folder of your choice.
- Add `ilo.bat` to your `$PATH`

## CLI Autocomplete

In order to get autocomplete working in your shell, run the following after installing `ilo`:

```shell script
$ source <(ilo generate-completion)
```

Place the same line in your `.bashrc` (or similar) file in order to make the change permanent.

### Oh-My-Zsh

[OMZ](https://github.com/ohmyzsh/ohmyzsh) uses the `$ZSH_CUSTOM` directory in order to customize your shell.
In order to integrate the autocomplete support, follow these steps:

1. Create a new file in `$ZSH_CUSTOM`, e.g. `$ZSH_CUSTOM/ilo.sh`.
2. Use the following template
    ```shell script
    $ source <(ilo generate-completion)
    ```

3. Reload your shell (or create a new one)

