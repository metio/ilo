---
title: bash
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_bash
categories:
- integration
tags:
- bash
---

[bash](https://www.gnu.org/software/bash/) has several options to customize its shell. In order to integrate the autocomplete support, follow these steps:

1. Create or edit `.bashrc`.
2. Add the following line
    ```shell script
    source <(ilo generate-completion)
    ```
3. Reload your shell (or create a new one)
