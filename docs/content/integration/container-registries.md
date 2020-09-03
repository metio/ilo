---
title: Container Registries
date: 2020-04-13
menu:
  main:
    parent: integration
    identifier: integration_container_registry
categories:
- integration
tags:
- docker hub
- github container registry
---

In order to share build environments within a team, using a [container registry](https://github.com/opencontainers/distribution-spec) is highly recommended.

## Docker Hub

[Docker Hub](https://hub.docker.com/) has an [automated build system](https://docs.docker.com/docker-hub/builds/) that can be used to automatically create a build environment which in turn can be used by team members of your project.

Make sure Docker Hub rebuilds your build environment on every change to master (or any other branch) and have your contributors pull the resulting images to their machines.

## GitHub Container Registry

The [GitHub container registry](https://github.blog/2020-09-01-introducing-github-container-registry/) can be used together with [GitHub actions](https://github.com/features/actions) to publish OCI images for your team. Simply follow their getting started [guide](https://docs.github.com/en/packages/getting-started-with-github-container-registry/migrating-to-github-container-registry-for-docker-images) to publish your image(s).
