---
title: tmux
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_tmux
categories:
- integration
tags:
- tmux
---

[tmux](https://github.com/tmux/tmux) users can simplify the usage of `ilo` with a tmux session manager. All of them allow you to configure reusable sessions that open project directories and execute commands for you. Consider combining them with [run commands](../../usage/run-commands) and/or [argument files](../../usage/argument-files) to simplify this even further.

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
[you@hostname project-dir]$ cat $XDG_CONFIG_HOME/tmuxp/your-project.yml
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
