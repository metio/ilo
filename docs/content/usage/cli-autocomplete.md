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

The `ilo generate-completion` command generates autocompletion configuration for shells such as [bash](https://www.gnu.org/software/bash/) and [zsh](https://www.zsh.org/). 

In order to get autocomplete working in your shell, execute the following snippet:

```shell script
$ source <(ilo generate-completion)
```

See the integration pages for [bash](../integration/bash) and [zsh](../integration/zsh) for help.
