---
title: Shells
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_shells
categories:
- integration
tags:
- bash
- zsh
---

In order to get the most out of `ilo` make sure that it is well integrated into your shell.

## bash

In order to integrate the autocomplete into [bash](https://www.gnu.org/software/bash/), follow these steps:

1. Create or edit `.bashrc`.
2. Add the following line
    ```shell script
    source <(ilo generate-completion)
    ```
3. Reload your shell (or create a new one)

## zsh

In order to integrate the autocomplete into [zsh](https://www.zsh.org/), follow these steps:

1. Create or edit `.zshrc`.
2. Add the following line
    ```shell script
    source <(ilo generate-completion)
    ```
3. Reload your shell (or create a new one)

## oh-my-zsh

In order to integrate the autocomplete into [oh my zsh](https://github.com/ohmyzsh/ohmyzsh), follow these steps:

1. Create a new file in `$ZSH_CUSTOM`, e.g. `$ZSH_CUSTOM/ilo.sh`.
2. Use the following template
    ```shell script
    source <(ilo generate-completion)
    ```
3. Reload your shell (or create a new one)
