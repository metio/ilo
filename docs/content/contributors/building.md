---
title: Building ilo
date: 2020-04-13
menu:
  main:
    parent: 'Contributors'
categories:
- Contributors
tags:
- build
- environment
---

`ilo` requires a certain set of software installed on your system in order to be built.

## Prerequisites

- [git](https://git-scm.com/) for version control
- [minisign](https://jedisct1.github.io/minisign/) to sign the waiver as a contributor
- [gpg](https://gnupg.org/) to sign your the commit which adds the waiver

## Manual Setup

- [Java JDK](https://jdk.java.net/) to compile the code
- [Maven](https://maven.apache.org/) to build the project
- [hugo](https://gohugo.io/) in order to create the website

## ilo Setup

- [ilo](../usage/install) to open the reproducible build environment for `ilo` itself
- Anyone of the [runtimes](../shell) that `ilo shell` supports.

## GitHub Packages

Some of `ilo`'s dependencies are only available in GitHub packages which requires authentication. Since `ilo` is using Maven to build itself, you will have to create/change your local `~/.m2/settings.xml` file. Use the following template as reference and take a look at the [settings used by ilo](https://github.com/metio/ilo/blob/master/build/settings.xml) itself:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>YOUR_NAME</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>YOUR_NAME</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases>
	          <enabled>true</enabled>
	        </releases>
          <snapshots>
	          <enabled>false</enabled>
	        </snapshots>
        </repository>
        <repository>
          <id>maven-build-process</id>
          <name>GitHub maven-build-process Apache Maven Packages</name>
          <url>https://maven.pkg.github.com/metio/maven-build-process</url>
          <releases>
	          <enabled>true</enabled>
	        </releases>
          <snapshots>
	          <enabled>true</enabled>
	        </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>maven-build-process</id>
      <username>YOUR_NAME</username>
      <password>YOUR_TOKEN</password>
    </server>
  </servers>
</settings>
```

Replace `YOUR_NAME` and `YOUR_TOKEN` with your GitHub username and [personal access token](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line).

## Building

In case you have Java and Maven locally installed call:

```shell script
# run all tests
$ mvn verify
```

In case you have `ilo` installed, call this:

```shell script
# build the project
$ ilo @build/once

# open a shell with a pre-defined build environment
$ ilo
```

In case you want to build the website do this:

```shell script
# build website
$ hugo --minify --i18n-warnings --path-warnings --source docs

# serve website
$ hugo server --minify --i18n-warnings --path-warnings --source docs --watch
```

Take a look at the [Makefile](../makefile) as an easy way to call all these commands.
