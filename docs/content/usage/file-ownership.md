---
title: File Ownership
date: 2026-06-04
menu:
  main:
    parent: usage
    identifier: usage_file_ownership
    weight: 106
categories:
- usage
tags:
- permissions
- volume
---

`ilo` mounts your project directory into the container read-write, so the build tools inside the container (compilers, package managers, …) work on your real source and the artifacts they produce land back in your working directory. The natural question is: when something inside the container creates or edits a file, **who owns it on the host?** Many build images run as `root`, and you do not want to end up with root-owned files in your checkout that you then need `sudo` to delete.

The good news is that with `ilo`'s default runtime this just works — and where it does not, it is a property of the *container runtime*, not of `ilo`.

## Rootless Podman (recommended)

On Linux `ilo` selects [Podman](https://podman.io/) first, and rootless Podman runs the container inside a **user namespace** that maps the container's `root` (UID 0) to *your* host user.

**Images that run as `root` (most build images — Maven, Node, GCC, …): there is nothing to do.** The container is `root` *inside* — so it can write the image's root-owned caches and tools, e.g. Maven's `/root/.m2` — while those writes map to *your* UID *outside*, so files in your project are owned by **you**. This "root inside, you outside" mapping is exactly what makes a container-based build environment pleasant, and it is the default.

**Images that run as a fixed non-root user** are the exception. Without help, their writes land on the host under a high "phantom" sub-UID (from your `/etc/subuid` range) that you cannot easily edit or delete. `ilo` handles this for you: [`--update-remote-user-uid`](../../shell/options#--update-remote-user-uid) is **on by default** and maps that user back to yourself with a [`--userns=keep-id`](https://docs.podman.io/en/latest/markdown/podman-run.1.html#userns-mode) user namespace pinned to that user's own UID/GID — so it works even when your host UID is not the same as the image's. It reads the image's user automatically, or you can name it with `--remote-user`:

```console
$ ilo shell my-nonroot-image
```

You *can* instead set `userns = "keep-id"` globally in `containers.conf`, but be aware that makes **every** container run as your non-root user — which removes the "root inside" benefit above and can break images that genuinely need `root`. Prefer the default, which only maps the user when the image actually needs it.

## What `:z` does (and does not) do

When it mounts your project directory, `ilo` appends `:z` to the volume. That is purely an **SELinux** relabel so the container is *allowed* to access the directory on systems like Fedora and RHEL — it has nothing to do with UID ownership. The two concerns are independent: `:z` grants access, the user namespace decides ownership.

## Docker

The Docker daemon runs as real `root` and does **not** remap UIDs for bind mounts by default, so a container running as `root` writes host files owned by `root`.

The clean fix is to **run Docker in [rootless mode](https://docs.docker.com/engine/security/rootless/)**, which gives you the same user-namespace mapping as rootless Podman — the problem then disappears the same way, with no per-command flags.

If you cannot use rootless Docker, `ilo` aligns ownership for you: [`--update-remote-user-uid`](../../shell/options#--update-remote-user-uid) is **on by default**. Because `ilo` runs your work by `exec`ing into a long-lived container, the mapping applies to **both** the container *and* every `exec` (a raw `--runtime-run-option=--user=…` would only cover the container's main process, not the shell you actually work in). For an image with a non-root user, `ilo` builds a small derived image that remaps that user's UID/GID to yours, so it keeps its name, home and shell while writing files you own. For an image that runs as `root`, `ilo` instead runs it as your host `--user <uid>:<gid>`; the trade-off there is that the process is no longer `root`, so paths the image keeps under `/root` may not be writable — point such tools at a writable location (your mounted working directory, a `$HOME` you own, or a dedicated cache volume), or use rootless Docker, which keeps `root` working while still mapping ownership. Pass `--no-update-remote-user-uid` to opt out entirely.

## Not mounting at all

If a particular run should not touch your working directory, pass [`--no-mount-project-dir`](../../shell/options#--mount-project-dir) to skip the mount entirely.
