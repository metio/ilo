# ilo [![Chat](https://img.shields.io/badge/matrix-%23ilo:matrix.org-brightgreen.svg?style=social&label=Matrix)](https://matrix.to/#/#ilo:matrix.org) [![Mailing List](https://img.shields.io/badge/email-ilo%40metio.groups.io%20-brightgreen.svg?style=social&label=Mail)](https://metio.groups.io/g/ilo/topics)

Manage reproducible build environments. Take a look at the [website](https://ilo.projects.metio.wtf/) for detailed information.

# Usage

## Customize Build Environment

In most cases `fedora:latest` will not be enough to compile/test/package/run your software.
While you can install additional packages inside the container, those changes will be lost once you remove the container.
Instead `ilo` allows you to define your build environment either in a [Dockerfile](https://docs.docker.com/engine/reference/builder/) or any other [OCI Image](https://github.com/opencontainers/image-spec/blob/master/spec.md) compliant way.
Make sure your image can be accessed by everyone in your team and use `ilo shell --image your.image.here:latest` to open a new instance of your build environment.
If you are using `ilo compose`, make sure to specify `your.image.here:latest` as the image in your docker-compose.yml file.


## License

```
To the extent possible under law, the author(s) have dedicated all copyright
and related and neighboring rights to this software to the public domain
worldwide. This software is distributed without any warranty.

You should have received a copy of the CC0 Public Domain Dedication along with
this software. If not, see http://creativecommons.org/publicdomain/zero/1.0/.
```
