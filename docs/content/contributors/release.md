---
title: Release
date: 2020-04-13
menu:
  main:
    parent: 'Contributors'
---

The release process of `ilo` is highly automated, therefore you only have to:

1. Push changes into the `main` branch.
2. Wait until an [GitHub action](https://github.com/metio/ilo/blob/main/.github/workflows/release.yml) will perform a release automatically. Take a look at the [calendar](https://metio.groups.io/g/ilo/calendar) for the next scheduled release. Note that a release will only be performed if any changes to the source code were detected since the last release.

Each new release will:

- Publish a new GitHub [release](https://github.com/metio/ilo/releases)
- Publish an [email](https://metio.groups.io/g/ilo/topics)
