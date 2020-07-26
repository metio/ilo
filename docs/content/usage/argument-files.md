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

In order to share the same options/commands across your team, `ilo` supports argument files which contain the options for your project, e.g. which image you are using. Argument files are just plain text files and both name and location can be chosen at will.

```shell script
# write argument file
$ cat some/folder/your-arguments.txt
shell
--image node:latest
/bin/bash

# use argument file
$ ilo @some/folder/your-arguments.txt
```

The argument file in the above example specified all commands and options on a new line, however you could write them all in a single line (or a mixture of both) as well:

```shell script
# write argument file
$ cat some/other/your-arguments.txt
shell --image node:latest /bin/bash

# write argument file
$ cat some/more/your-arguments.txt
shell --image
node:latest /bin/bash

# use argument file
$ ilo @some/other/your-arguments.txt
$ ilo @some/more/your-arguments.txt
```

In order to use an argument file, you have to add **@** in front of the file name: `ilo @file-name`.

You can use multiple arguments files which are evaluated in-order, e.g like this:

```shell script
$ ilo @first @second
```

You can mix argument files with regular CLI options as well:

```shell script
$ ilo shell --image openjdk:11 @default-shell
```

The argument file used by `ilo` developers can be seen [here](https://github.com/metio/ilo/blob/master/build/shell) and is used like this:

```shell script
$ ilo @build/shell
```
