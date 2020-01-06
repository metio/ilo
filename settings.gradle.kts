/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

rootProject.name = "ilo"

//region Exec
include(":modules:exec:exec-api")
include(":modules:exec:process-builder")
//endregion

//region OS
include(":modules:os:generic")
// include(":modules:os:linux")
// include(":modules:os:mac")
// include(":modules:os:windows")
//endregion

//region Formats
include(":modules:formats:formats-api")
// include(":modules:formats:devcontainer")
// include(":modules:formats:docker-compose")
include(":modules:formats:ilo-rc-format")
//endregion

//region Tools
include(":modules:tools:tools-api")
include(":modules:tools:podman")
include(":modules:tools:podman-impl")
include(":modules:tools:podman-compose")
include(":modules:tools:podman-compose-impl")
include(":modules:tools:buildah")
include(":modules:tools:buildah-impl")
include(":modules:tools:kubectl")
include(":modules:tools:kubectl-impl")
include(":modules:tools:docker")
include(":modules:tools:docker-impl")
include(":modules:tools:docker-compose")
include(":modules:tools:docker-compose-impl")
//endregion

//region App
include(":modules:app:cli")
//endregion
