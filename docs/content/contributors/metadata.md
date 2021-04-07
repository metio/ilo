---
title: Metadata
date: 2020-04-13
menu:
  main:
    parent: 'Contributors'
categories:
- Contributors
tags:
- metadata
---

Every contributor may add/remove her/his metadata to the [list of contributors](https://github.com/metio/ilo/tree/main/docs/data/contributors) at any time.

Metadata is currently used in three places:

1. To generate a list of existing [minisign](https://jedisct1.github.io/minisign/) signatures in the [first timer](../first-timer) documentation.
2. To generate a [humans.txt](http://humanstxt.org/) file of [all contributors](https://ilo.projects.metio.wtf/humans.txt).
3. To generate a [FOAF](http://www.foaf-project.org/) for the [entire project](https://ilo.projects.metio.wtf/foaf.rdf).

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
