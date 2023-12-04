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

`ilo` is an open source product released under the [0BSD license](https://spdx.org/licenses/0BSD.html). In order to make sure that each contribution is correctly attributed and licensed, the Developer Certificate of Origin (DCO) **MUST** be signed by each contributor with their first commit. In order to do so, simply add a `Signed-off-by` statement at the end of your commit yourself or use `git commit -s` to do that automatically. The DCO can be seen below or at https://developercertificate.org/

```
Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

(a) The contribution was created in whole or in part by me and I
    have the right to submit it under the open source license
    indicated in the file; or

(b) The contribution is based upon previous work that, to the
    best of my knowledge, is covered under an appropriate open
    source license and I have the right under that license to
    submit that work with modifications, whether created in whole
    or in part by me, under the same open source license (unless
    I am permitted to submit under a different license), as
    Indicated in the file; or

(c) The contribution was provided directly to me by some other
    person who certified (a), (b) or (c) and I have not modified
    it.

(d) I understand and agree that this project and the contribution
    are public and that a record of the contribution (including
    all personal information I submit with it, including my
    sign-off) is maintained indefinitely and may be redistributed
    consistent with this project or the open source license(s)
    involved.
```

## Metadata

Every contributor **MAY** add/remove their metadata to the list of contributors at any time. Simply add a file called `<YOUR_NAME>.yaml` in the [contributors directory](https://github.com/metio/ilo/tree/main/docs/data/contributors) with the following properties:

```yaml
id: '<YOUR_NAME>'                   # should match file name (required)
title: '<YOUR_TITLE>'               # used by FOAF/humans.txt (optional)
first_name: '<YOUR_FIRST_NAME>'     # used by FOAF/humans.txt (optional)
last_name: '<YOUR_LAST_NAME>'       # used by FOAF/humans.txt (optional)
email: '<YOUR_EMAIL>'               # used by FOAF (optional)
website: '<YOUR_URL>'               # used by FOAF/humans.txt (optional)
```

Metadata is currently used in three places:

1. To generate a [humans.txt](https://humanstxt.org/) file of [all contributors](https://ilo.projects.metio.wtf/humans.txt).
2. To generate a [FOAF](http://xmlns.com/foaf/spec/) for the [entire project](https://ilo.projects.metio.wtf/foaf.rdf).
