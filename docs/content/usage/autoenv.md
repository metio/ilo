---
title: Automate
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_autoenv
categories:
- usage
tags:
- autoenv
- direnv
- smartcd
- zsh-autoenv
---

In order to simplify the usage of `ilo`, consider using `autoenv`-like tooling. These tools all allow you to just enter a directory and will automatically call `ilo` for you like this:

```shell script
[you@hostname ~]$ cd path/to/your/project
[root@container project-dir]#
```

As soon as you enter the directory of your project, these tools will call `ilo` which in turn will open your build environment for you.

## autoenv

In order to use [autoenv](https://github.com/inishchith/autoenv), first [install it](https://github.com/inishchith/autoenv#install) and then place a `.env` file in the root of your project which contains `ilo` and its arguments as its contents.

```shell script
[you@hostname project-dir]$ cat .env
ilo @build-env
```

## direnv

In order to use [direnv](https://direnv.net/), first [install it](https://direnv.net/#basic-installation) and then place a `.envrc` file in the root of your project which contains `ilo` and its arguments as its contents.

```shell script
[you@hostname project-dir]$ cat .envrc
ilo @build-env
```

## envrc-rs

In order to use [envrc-rs](https://github.com/roxma/envrc-rs), first [install it](https://github.com/roxma/envrc-rs#install) and then place a `.envrc` file in the root of your project which contains `ilo` and its arguments as its contents.

```shell script
[you@hostname project-dir]$ cat .envrc
ilo @build-env
```

## smartcd

In order to use [smartcd](https://github.com/cxreg/smartcd), first [install it](https://github.com/cxreg/smartcd#ok-how-do-i-use-it), enter the root directory of your project and then call `smartcd edit enter` which will open an editor to write a script to execute whenever you enter your project directory. Use something like `ilo @build-env` as the script.

```shell script
[you@hostname project-dir]$ cat ~/.smartcd/scripts/path/to/project-dir/bash_enter
ilo @build-env
```

## zsh-autoenv

In order to use [zsh-autoenv](https://github.com/Tarrasch/zsh-autoenv), first [install it](https://github.com/Tarrasch/zsh-autoenv#installation) and then place a `.autoenv.zsh` file in the root of your project which contains `ilo` and its arguments as its contents.

```shell script
[you@hostname project-dir]$ cat .autoenv.zsh
ilo @build-env
```

## tmuxp

In order to use [tmuxp](https://github.com/tmux-python/tmuxp), first [install it](https://github.com/tmux-python/tmuxp#installation) and then create a new configuration in `$XDG_CONFIG_HOME/tmuxp`:

```shell script
[you@hostname project-dir]$ cat $XDG_CONFIG_HOME/tmuxp/your-project.yml
session_name: your-project
windows:
- window_name: dev window
  shell_command_before:
    - cd ~/path/to/your/project
  panes:
    - shell_command:
        - ilo @build-env
```

## teamocil

In order to use [teamocil](https://github.com/remi/teamocil), first [install it](https://github.com/remi/teamocil#installation) and then create a new configuration in `~/.teamocil`:

```shell script
[you@hostname project-dir]$ cat ~/.teamocil/your-project.yml
windows:
  - name: your-project
    root: ~/path/to/your/project
    panes:
      - ilo @build-env
```

## tmuxifier

In order to use [tmuxifier](https://github.com/jimeh/tmuxifier), first [install it](https://github.com/jimeh/tmuxifier#installation) and then create a new configuration in `$TMUXIFIER_LAYOUT_PATH`:

```shell script
[you@hostname project-dir]$ cat $TMUXIFIER_LAYOUT_PATH/your-project.sh
window_root "~/path/to/your/project"
new_window "Your Project"
split_v 20
run_cmd "ilo @build-env"
split_h 60
select_pane 0
```

## tmuxinator

In order to use [tmuxinator](https://github.com/tmuxinator/tmuxinator), first [install it](https://github.com/tmuxinator/tmuxinator#installation) and then create a new configuration in `~/.config/tmuxinator`:

```shell script
[you@hostname project-dir]$ cat ~/.config/tmuxinator/your-project.yml
name: your-project
root: ~/path/to/your/project
windows:
  - your_project:
      panes:
        - ilo @build-env
```

## dmux

In order to use [dmux](https://github.com/zdcthomas/dmux), first [install it](https://github.com/zdcthomas/dmux#installation) and then create your configuration in `$XDG_CONFIG_HOME/dmux/dmux.conf.{file_type}`:

```shell script
[you@hostname project-dir]$ cat $XDG_CONFIG_HOME/dmux/dmux.conf.toml
[your-project]
number_of_panes = 2
session_name = "Your Project"
commands = ["ilo @build-env"]
```
