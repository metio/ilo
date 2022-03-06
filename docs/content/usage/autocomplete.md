---
title: Autocomplete
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

Once enabled you can use the `<TAB>` key to autocomplete ilo commands and their options:

```shell script
# autocomplete commands
$ ilo s<TAB>
$ ilo shell

# autocomplete options
$ ilo shell --re<TAB>
$ ilo shell --remove-image
```

## bash

In order to integrate autocompletion into [bash](https://www.gnu.org/software/bash/), follow these steps:

1. Create or edit `~/.bashrc`.
2. Add the following line
    ```shell script
    source <(ilo generate-completion)
    ```
3. Reload your shell (or create a new one)

## zsh

In order to integrate autocompletion into [zsh](https://www.zsh.org/), follow these steps:

1. Create or edit `$ZDOTDIR/.zshrc`.
2. Add the following line
    ```shell script
    source <(ilo generate-completion)
    ```
3. Reload your shell (or create a new one)
