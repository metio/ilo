---
title: First Timer
date: 2020-04-13
menu:
  main:
    parent: 'Contributors'
categories:
- Contributors
tags:
- help
---

First time contributors have to perform an additional step in order to get their changes merged into the main repository.

The [WAIVER](https://github.com/metio/ilo/blob/main/AUTHORS/WAIVER) has to be signed in order to signal that all contributions can be published into the public domain using the [Creative Commons Zero (CC0)](https://creativecommons.org/publicdomain/zero/1.0/) license.

[minisign](https://jedisct1.github.io/minisign/) is used to sign and verify contributions (see [dev-env](../dev-env)).

The signed waiver **must** be added to your first pull request. The created git commit **must** be [GPG signed](https://git-scm.com/docs/git-commit#Documentation/git-commit.txt--Sltkeyidgt).

Additionally, any contributor may add her/his [metadata](../metadata) to the project. This is especially useful for `minisign` public keys since you will have to communicate to the project maintainers anyway.

## Prerequisite

Make sure at least version `0.8` of `minisign` is installed:

```shell script
$ minisign -v
minisign 0.8
```

## Sign

In order to sign the waiver, call the following commands from the root of the project:

```shell script
$ minisign -Sm AUTHORS/WAIVER
$ mv AUTHORS/WAIVER.minisig AUTHORS/WAIVER.`id --name --user`.minisig
```

In case you are into [Makefiles](../makefile), you can use the shorter version:

```shell script
$ make sign-waiver
```

In both cases, a new file called `AUTHORS/WAIVER.<USER>.minisign` was created. The `Makefile` will commit the signed waiver to your local repo as well. In case you have not used the `Makefile`, run this:

```shell script
$ git add AUTHORS/WAIVER.`id --name --user`.minisig
$ git commit -m 'sign waiver' --gpg-sign
```

## Verify

An existing contributor will verify your signature with:

```shell script
$ minisign -V -x AUTHORS/WAIVER.<YOUR_NAME>.minisig -P <YOUR_PUB_KEY> -m AUTHORS/WAIVER
```

In order to verify existing signatures, do any of the following:

{{< minisignatures >}}
