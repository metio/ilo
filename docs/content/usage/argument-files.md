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

In order to share the same options/commands across your team, `ilo` supports argument files which contain the options (like which image to use) for your project.
Create an empty text file and put all options in there with their corresponding values.
You can specify all options in a single line, separated by whitespace or use a new line for every option.
Use that argument file by calling `ilo @file-name`.
You can mix argument files with regular CLI options as well, e.g. `ilo shell --image=openjdk:latest @some-file`.
The argument file used by `ilo` developers can be seen [here](../../build/build-env) and is used like this `ilo @build-env`
