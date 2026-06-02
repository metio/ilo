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
"--runtime-option=some option here"

# quote the value
--runtime-option "some option here"

# THIS WON'T WORK
--runtime-option="some option here"
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

### Trusting RC files

A `.rc` file can set any option, and option values are expanded by a host shell —
so command substitution like `$(...)` inside a `.rc` file runs on your machine
when `ilo` loads it. Because `.ilo/ilo.rc` and `.ilo.rc` are discovered
automatically from the current directory, `ilo` only loads such a file after you
have trusted it. The first time `ilo` finds an untrusted file it asks:

```console
$ ilo shell
ilo found an untrusted run command file: /path/to/project/.ilo.rc
Loading it lets it run arbitrary commands on your machine.
Trust and load this file from now on? [y/N]
```

Answering yes records the file in the trust store and loads it; from then on it
loads without asking. Trust is bound to the file's absolute path and a hash of
its content, so moving the file or changing its content asks again. When a file
you trusted before has changed, the prompt says so, so you know why you are being
asked again:

```console
$ ilo shell
ilo: the run command file /path/to/project/.ilo.rc has changed since you trusted it.
Re-trusting it lets the new content run arbitrary commands on your machine.
Trust and load this file from now on? [y/N]
```

In a non-interactive session (for example CI) an untrusted file is **not** loaded.

The trust store lives at `$XDG_CONFIG_HOME/ilo/trusted-rc` (or
`~/.config/ilo/trusted-rc`); set the `ILO_TRUST_FILE` environment variable to use
a different location. Files you point to explicitly with `ILO_RC` are treated as
your own choice and are loaded without a trust prompt.
