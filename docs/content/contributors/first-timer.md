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

First time contributors have to perform an additional step in order to get their changes merged into the main repository: The [WAIVER](https://github.com/metio/ilo/blob/main/AUTHORS/WAIVER) has to be signed in order to signal that all contributions can be published into the public domain using the [Creative Commons Zero (CC0)](https://creativecommons.org/publicdomain/zero/1.0/) license. [minisign](https://jedisct1.github.io/minisign/) is used to sign and verify contributions (see [building](../building)). The signed waiver **must** be added to your first pull request. The created git commit **must** be [GPG signed](https://git-scm.com/docs/git-commit#Documentation/git-commit.txt--Sltkeyidgt). Note that recent versions of Git allow you to use your SSH key to *gpg-sign* your commits. Additionally, any contributor may add her/his metadata to the project. This is especially useful for `minisign` public keys since you will have to communicate to the project maintainers anyway.

## Sign

In order to sign the waiver, call the following commands from the root of the project:

```shell script
$ minisign -Sm AUTHORS/WAIVER
$ mv AUTHORS/WAIVER.minisig AUTHORS/WAIVER.`id --name --user`.minisig

# or use the Makefile
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

## Metadata

Every contributor may add/remove her/his metadata to the list of contributors at any time.

Metadata is currently used in three places:

1. To generate a list of existing [minisign](https://jedisct1.github.io/minisign/) signatures in the [first timer](../first-timer) documentation.
2. To generate a [humans.txt](https://humanstxt.org/) file of [all contributors](https://ilo.projects.metio.wtf/humans.txt).
3. To generate a [FOAF](https://www.foaf-project.org/) for the [entire project](https://ilo.projects.metio.wtf/foaf.rdf).

## Adding a new entry

Create a new file called `<YOUR_NAME>.yaml` in the [contributors directory](https://github.com/metio/ilo/tree/main/docs/data/contributors). Add the following properties to it:

```yaml
id: '<YOUR_NAME>'                   # should match file name (required)
minisign: '<YOUR_MINISIGN_PUB_KEY>' # used for key verification (required)
title: '<YOUR_TITLE>'               # used by FOAF/humans.txt (optional)
first_name: '<YOUR_FIRST_NAME>'     # used by FOAF/humans.txt (optional)
last_name: '<YOUR_LAST_NAME>'       # used by FOAF/humans.txt (optional)
email: '<YOUR_EMAIL>'               # used by FOAF (optional)
website: '<YOUR_URL>'               # used by FOAF/humans.txt (optional)
```
