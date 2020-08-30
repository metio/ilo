---
title: Run Commands
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_rc_files
categories:
- usage
tags:
- run commands
---

In order to simplify/automate its usage, `ilo` will look for [run command](https://en.wikipedia.org/wiki/Run_commands) files in the following locations:

1. `.ilo/ilo.rc`
2. `.ilo.rc`

**Each** file found will be added in-order as an [argument file](../argument-files) to your invocation of `ilo` **before** any other options you specify in your terminal.
