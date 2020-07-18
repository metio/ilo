# Contributor Guide

Thank you so much for improving `ilo`! Without a healthy community, no open source project can survive.

## How to become a contributor

In order to become a contributor for `ilo`, you have to complete these steps:

- Fork the project on github/codeberg/gitlab/bitbucket
- Sign the waiver
- Create a pull-/merge-request
- Celebrate \o/

In order to sign the waiver follow these steps. They assume, that you have cloned your fork locally (or added another remote) to an existing clone.

```shell script
$ git checkout -b contributor/$(id -u -n)
$ gpg2 --no-version --armor --sign AUTHORS/WAIVER
$ mv AUTHORS/WAIVER.asc AUTHORS/WAIVER-signed-by-$(id -u -n)-$(date "+%Y-%m-%d").asc
$ git add AUTHORS/WAIVER-signed-by-$(id -u -n)-$(date "+%Y-%m-%d").asc
$ git commit -s -S -m 'sign CLA'
$ git push -u origin contributor/$(id -u -n)
```

## Git Branching Model

The `master` branch always contains the latest public stable release. New features can be developed in feature branches.

## Building the Project

`ilo` requires **Java 11** to build. Earlier versions cannot be used to build `ilo`.

```shell script
[you@hostname ~]$ git clone <upstream> # see mirrors at bottom
[you@hostname ~]$ cd ilo
[you@hostname ~/ilo]$ ./mvnw verify
```

The binary distribution will be located in `target/`.

### GraalVM native-image

In case you want to build a native executable of `ilo`, make sure Graal 19.3 is installed and the `native-image` command is available.
Since 19.3 Graal no longer ships `native-image` in their base distribution, thus you have to install it manually using `gu install native-image`.
Take a look at the [reference documentation](https://www.graalvm.org/docs/reference-manual/native-image/) for more information.

### Use `ilo` to build `ilo`

In case you already have `ilo` installed on your system, do the following:

```shell script
[you@hostname ~]$ git clone <upstream> # see mirrors at bottom
[you@hostname ~]$ cd ilo
[you@hostname ~/ilo]$ ilo @build-once
```

## SSH Setup

Use the following example config as reference and adapt according to your needs:

```
Host github
        HostName github.com
        User git
        IdentityFile ~/.ssh/github

Host gitlab
        HostName gitlab.com
        User git
        IdentityFile ~/.ssh/gitlab

Host bitbucket
        HostName bitbucket.org
        User git
        IdentityFile ~/.ssh/bitbucket

Host codeberg
        HostName codeberg.org
        User git
        IdentityFile ~/.ssh/codeberg
```

## Git Mirrors

In order to have all mirrors in sync, do the following:

```shell script
$ git remote add mirrors DISABLED
$ git remote set-url --add --push mirrors github:metio/ilo.git
$ git remote set-url --add --push mirrors gitlab:metio.wtf/ilo.git
$ git remote set-url --add --push mirrors bitbucket:metio-wtf/ilo.git
```

The end result should look like this:

```shell script
$ git remote -v
mirrors DISABLED (fetch)
mirrors github:metio/ilo.git (push)
mirrors gitlab:metio.wtf/ilo.git (push)
mirrors bitbucket:metio-wtf/ilo.git (push)
origin  codeberg:metio.wtf/ilo.git (fetch)
origin  codeberg:metio.wtf/ilo.git (push)
```

Use `git push mirrors` in order to keep the mirrors up to date.
