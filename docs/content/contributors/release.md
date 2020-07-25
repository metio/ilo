---
title: Release
date: 2020-04-13
menu:
  main:
    parent: 'Contributors'
---

The release process of `ilo` is highly automated, therefore you only have to:

1. Push changes into the `master` branch
2. Wait until an [GitHub action](https://github.com/metio/ilo/blob/master/.github/workflows/release.yml) will perform a release automatically

Each new release will:

- Publish a new GitHub [release](https://github.com/metio/ilo/releases)
- Publish an [email](https://metio.groups.io/g/ilo/topics)
- Publish a [toot](https://fosstodon.org/@metiobuild)
- Publish a post to [reddit](https://www.reddit.com/r/metiowtf/)