---
title: CI Servers
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_ci
categories:
- integration
tags:
- jenkins
---

## Jenkins

[Jenkins](https://jenkins.io/) knows multiple ways to define jobs. In order to integrate with `ilo` make sure the `ilo` is [installed](../../usage/install) on your slaves.

### Declarative Pipeline

```groovy
pipeline {
  stages {
    stage('Build with ilo') {
      sh 'ilo @build-things'
    }
  }
}
```

`build-things` is an [arguments](../../usage/argument-files) file.
