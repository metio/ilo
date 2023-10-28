---
title: Argument Files
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_arg_files
categories:
- usage
tags:
- argument files
---

In order to share the same options/commands across your team, `ilo` supports argument files which contain the options for your project, e.g. which image you are using. Argument files are just plain text files and both name and location can be chosen at will. In order to use an argument file, you have to add **@** in front of the file name: `ilo @file-name`.

```console
# write argument file
$ cat some/folder/your-arguments
shell
node:latest
/bin/bash

# use argument file
$ ilo @some/folder/your-arguments
```

The argument file in the above example specified all commands and options on a new line, however you could write them all in a single line (or a mixture of both) as well:

```console
# write argument file
$ cat some/other/your-arguments
shell node:latest /bin/bash

# write argument file
$ cat some/more/of/your-arguments
shell
node:latest /bin/bash

# use argument file
$ ilo @some/other/your-arguments
$ ilo @some/more/of/your-arguments
```

**Important**: In case your option contains a whitespace, you have to either put the entire option with its value in single/double quotes or use a whitespace between option and value like this:

```console
# quote the entire option
"--run-as=$(id -u):$(id -g)"

# quote the value
--run-as "$(id -u):$(id -g)"

# THIS WON'T WORK
--run-as="$(id -u):$(id -g)"
```

You can use multiple arguments files which are evaluated in-order, e.g like this:

```console
$ ilo @first @second
```

You can mix argument files with regular CLI options as well:

```console
$ ilo shell @default-shell openjdk:11
```

The argument file used by `ilo` developers can be seen [here](https://github.com/metio/ilo/blob/main/dev/env) and is used by calling `ilo @dev/env`.

## RC Files

In order to simplify/automate its usage, `ilo` will look for [run command](https://en.wikipedia.org/wiki/Run_commands) files in the following locations:

1. `.ilo/ilo.rc`
2. `.ilo.rc`

**Each** file found will be added in-order as an argument file to your invocation of `ilo` **before** any other options you specify in your terminal. You can change the locations to check by specifying the `ILO_RC` environment variable. Multiple locations can be given by separating them with a comma like this:

```console
$ export ILO_RC=some-file.rc,another-one.rc
$ ilo ...
```

In order to disable loading `.rc` files entirely, specify `--no-rc` in the command line before the actual `ilo` subcommand, like this:

```console
# do not load .rc files
$ ilo --no-rc shell ...
$ ilo --no-rc @some-argument-file ...
```
