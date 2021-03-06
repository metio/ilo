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

```shell script
# write argument file
$ cat some/folder/your-arguments.txt
shell
node:latest
/bin/bash

# use argument file
$ ilo @some/folder/your-arguments.txt
```

The argument file in the above example specified all commands and options on a new line, however you could write them all in a single line (or a mixture of both) as well:

```shell script
# write argument file
$ cat some/other/your-arguments.txt
shell node:latest /bin/bash

# write argument file
$ cat some/more/your-arguments.txt
shell
node:latest /bin/bash

# use argument file
$ ilo @some/other/your-arguments.txt
$ ilo @some/more/your-arguments.txt
```

**Important**: In case your option contains a whitespace, you have to either put the entire option with its value in single/double quotes or use a whitespace between option and value like this:

```shell script
# quote the entire option
"--run-as=$(id -u):$(id -g)"

# quote the value
--run-as "$(id -u):$(id -g)"

# THIS WON'T WORK
--run-as="$(id -u):$(id -g)"
```

You can use multiple arguments files which are evaluated in-order, e.g like this:

```shell script
$ ilo @first @second
```

You can mix argument files with regular CLI options as well:

```shell script
$ ilo shell @default-shell openjdk:11
```

The argument file used by `ilo` developers can be seen [here](https://github.com/metio/ilo/blob/main/.ilo.rc) and is used by simply calling `ilo` in the root of the project because it's a [run command](../run-commands) file.
