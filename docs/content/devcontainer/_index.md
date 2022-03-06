---
title: ilo devcontainer
date: 2020-04-13
---

The `devcontainer` command allows interacting with [devcontainers](https://code.visualstudio.com/docs/remote/containers) as used by [Visual Studio Code](https://code.visualstudio.com/) and [GitHub Codespaces](https://docs.github.com/en/codespaces/overview).

Make sure to specify `your.image.here:some-tag` as the image in your `devcontainer.json` file. Take a look at the [reference documentation](https://code.visualstudio.com/docs/remote/devcontainerjson-reference) for all available options for that JSON file.

```shell script
# open shell in devcontainer
[you@hostname project-dir]$ ilo devcontainer
[root@container project-dir]#
```

`ilo` will automatically try the following locations for your `devcontainer.json` file:

1. `.devcontainer/devcontainer.json`
2. `.devcontainer.json`

