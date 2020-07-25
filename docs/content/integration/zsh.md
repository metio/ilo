---
title: zsh
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_zsh
categories:
- integration
tags:
- zsh
---

[zsh](https://www.zsh.org/) has several options to customize its shell. In order to integrate the autocomplete support, follow these steps:

1. Create or edit `.zshrc`.
2. Add the following line
    ```shell script
    source <(ilo generate-completion)
    ```
3. Reload your shell (or create a new one)

In case you are using [oh my zsh](https://github.com/ohmyzsh/ohmyzsh), take a look at the help for [that](./oh-my-zsh).
