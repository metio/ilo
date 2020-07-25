---
title: oh-my-zsh
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_omz
categories:
- integration
tags:
- oh my zsh
- zsh
---

[oh my zsh](https://github.com/ohmyzsh/ohmyzsh) uses the `$ZSH_CUSTOM` directory in order to customize your shell.
In order to integrate the autocomplete support, follow these steps:

1. Create a new file in `$ZSH_CUSTOM`, e.g. `$ZSH_CUSTOM/ilo.sh`.
2. Use the following template
    ```shell script
    $ source <(ilo generate-completion)
    ```
3. Reload your shell (or create a new one)

Just using [zsh](https://www.zsh.org/), take a look at the help for [that](./zsh).
