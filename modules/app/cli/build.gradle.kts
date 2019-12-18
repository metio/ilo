//region Plugins
plugins {
    application
    id("org.beryx.jlink")
}
//endregion

//region Properties
val enablePodman: Boolean by rootProject.extra
val enablePodmanCompose: Boolean by rootProject.extra
val enableBuildah: Boolean by rootProject.extra
val enableKubectl: Boolean by rootProject.extra
val enableDocker: Boolean by rootProject.extra
val enableDockerCompose: Boolean by rootProject.extra
val enableJpackage: Boolean by rootProject.extra
val jpackageLocation: String by rootProject.extra
//endregion

//region Application
application {
    mainClassName = "wtf.metio.ilo.cli/wtf.metio.ilo.cli.Ilo"
    applicationName = "ilo"
}
tasks.named<Tar>("distTar") {
    enabled = false
}
distributions {
    main {
        contents {
            from("../../../LICENSE")
            from("../../../README.asciidoc")
            from("../../../CHANGELOG.asciidoc")
        }
    }
}
//endregion

//region JPackage
jlink {
    launcher {
        name = "ilo"
    }
    jpackage {
        outputDir = "jpackage"
        installerType = "app-image"
        jpackageHome = jpackageLocation
    }
}

if (enableJpackage) {
    distributions {
        create("jpackage") {
            distributionBaseName.set("jpackage")
            contents {
                from("build/jpackage/ilo")
            }
        }
    }

    tasks.named("assembleDist") {
        dependsOn("assembleJpackageDist")
    }
    tasks.named("jpackageDistZip") {
        dependsOn("jpackageImage")
    }
    tasks.named<Tar>("jpackageDistTar") {
        enabled = false
    }
}

//endregion

//region Dependencies
dependencies {
    //region App
    implementation(project(":modules:app:user-input"))
    implementation(project(":modules:app:orchestration"))
    implementation(project(":modules:app:usage-help"))
    //endregion

    //region Exec
    implementation(project(":modules:exec:exec-api"))
    implementation(project(":modules:exec:process-builder"))
    //endregion

    //region Formats
    implementation(project(":modules:formats:formats-api"))
    implementation(project(":modules:formats:ilo-rc-format"))
    //endregion

    //region Tools
    implementation(project(":modules:tools:tools-api"))
    implementation(project(":modules:tools:podman"))
    implementation(project(":modules:tools:podman-compose"))
    implementation(project(":modules:tools:buildah"))
    implementation(project(":modules:tools:kubectl"))
    implementation(project(":modules:tools:docker"))
    implementation(project(":modules:tools:docker-compose"))
    //endregion

    //region feature flags
    if (enablePodman) implementation(project(":modules:tools:podman-impl"))
    if (enablePodmanCompose) implementation(project(":modules:tools:podman-compose-impl"))
    if (enableBuildah) implementation(project(":modules:tools:buildah-impl"))
    if (enableKubectl) implementation(project(":modules:tools:kubectl-impl"))
    if (enableDocker) implementation(project(":modules:tools:docker-impl"))
    if (enableDockerCompose) implementation(project(":modules:tools:docker-compose-impl"))
    //endregion

    //region externals
    implementation("org.tinylog:tinylog-api:2.0.1")
    implementation("org.tinylog:tinylog-impl:2.0.1")
    //endregion
}
//endregion
