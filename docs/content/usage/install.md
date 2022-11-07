---
title: Install
date: 2020-04-13
menu:
  main:
    parent: usage
    identifier: usage_install
    weight: 100
categories:
- usage
tags:
- install
---

Prebuilt binaries of `ilo` are available for each published release at:

- https://github.com/metio/ilo/releases

Almost none of the widely used package managers have `ilo` in their official repositories yet. In case you want to help package `ilo` for your preferred operating system, take a look at the [open packaging issues](https://github.com/metio/ilo/issues?q=is%3Aopen+label%3A%22package%3A+windows%22%2C%22package%3A+alpine%22%2C%22package%3A+debian%22%2C%22package%3A+ubuntu%22%2C%22package%3A+arch%22%2C%22package%3A+fedora%22%2C%22package%3A+gentoo%22%2C%22package%3A+nix%22%2C%22package%3A+osx%22%2C%22package%3A+suse%22).

## Huber

In case you are using [huber](https://github.com/innobead/huber), execute the following commands to install `ilo` on Linux/Mac/Windows:

```console
$ huber repo add remote-repo --url https://github.com/metio/ilo
$ huber install ilo
```

## Linux

### Manual

The [Linux](https://www.kernel.org/) distribution of `ilo` is a native executable and does not require any additional software to be installed.

{{< linuxinstall >}}

### Fedora

[Fedora](https://getfedora.org/) users can install `ilo` from a [COPR](https://copr.fedorainfracloud.org/coprs/sebhoss/ilo/) repository like this:

```console
$ sudo dnf copr enable sebhoss/ilo
$ sudo dnf install ilo
```

### Fedora Silverblue

[Fedora Silverblue](https://silverblue.fedoraproject.org/) users can install `ilo` from the same [COPR](https://copr.fedorainfracloud.org/coprs/sebhoss/ilo/) by following these steps:

```console
$ curl --location https://copr.fedorainfracloud.org/coprs/sebhoss/ilo/repo/fedora-$(rpm -E %fedora)/sebhoss-ilo-fedora-$(rpm -E %fedora).repo --output ilo.repo
$ sudo mv ilo.repo /etc/yum.repos.d/ilo.repo
$ rpm-ostree install ilo
```

### NixOS

[NixOS](https://nixos.org/) users can use the following flake that always references the latest release:

```console
$ nix run github:metio/ilo/main?dir=build/nixos -- --help
```

## MacOS

### Manual

The [MacOS](https://www.apple.com/macos/) distribution of `ilo` is a native executable and does not require any additional software to be installed.

{{< macinstall >}}

### Homebrew

[Homebrew](https://brew.sh/) users can install `ilo` from our tap like this:

```console
$ brew tap metio/ilo https://github.com/metio/ilo
$ brew install metio/ilo/ilo
```

## Windows

The [Windows](https://www.microsoft.com/en-us/windows) distribution of `ilo` is a native executable and does not require any additional software to be installed.

{{< windowsinstall >}}

## Other

For all other platforms that support at least [Java 17](https://www.oracle.com/javadownload), run something like this:

{{< otherinstall >}}
