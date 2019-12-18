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
include(":modules:app:orchestration")
include(":modules:app:usage-help")
include(":modules:app:user-input")
include(":modules:app:cli")
//endregion
