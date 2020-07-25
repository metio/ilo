---
title: CLI Autocomplete
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_autocomplete
categories:
- usage
tags:
- autocomplete
---

The `ilo generate-completion` command generates autocompletion configuration for shells such as [bash](https://www.gnu.org/software/bash/) and [zsh](https://en.wikipedia.org/wiki/Z_shell). 

In order to get autocomplete working in your shell, execute the following snippet:

```shell script
$ source <(ilo generate-completion)
```

Place the same line in your `.bashrc` (or similar) file in order to make the change permanent.
