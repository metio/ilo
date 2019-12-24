/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 *
 */

//region Build
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.javamodularity:moduleplugin:1.6.0")
    }
}
//endregion

//region Plugins
plugins {
    id("org.beryx.jlink") version "2.17.0" apply false
}
//endregion

//region CLI switches
val ENABLE_PODMAN = "enable-podman"
val ENABLE_PODMAN_COMPOSE = "enable-podman-compose"
val ENABLE_BUILDAH = "enable-buildah"
val ENABLE_KUBECTL = "enable-kubectl"
val ENABLE_DOCKER = "enable-docker"
val ENABLE_DOCKER_COMPOSE = "enable-docker-compose"
val ENABLE_JPACKAGE = "enable-jpackage"
val JPACKAGE_HOME = "jpackage-home"
//endregion

//region Properties
val enablePodman by extra(
        if (project.hasProperty(ENABLE_PODMAN))
            "true" == project.property(ENABLE_PODMAN)
        else true)
val enablePodmanCompose by extra(
        if (project.hasProperty(ENABLE_PODMAN_COMPOSE))
            "true" == project.property(ENABLE_PODMAN_COMPOSE)
        else true)
val enableBuildah by extra(
        if (project.hasProperty(ENABLE_BUILDAH))
            "true" == project.property(ENABLE_BUILDAH)
        else true)
val enableKubectl by extra(
        if (project.hasProperty(ENABLE_KUBECTL))
            "true" == project.property(ENABLE_KUBECTL)
        else true)
val enableDocker by extra(
        if (project.hasProperty(ENABLE_DOCKER))
            "true" == project.property(ENABLE_DOCKER)
        else true)
val enableDockerCompose by extra(
        if (project.hasProperty(ENABLE_DOCKER_COMPOSE))
            "true" == project.property(ENABLE_DOCKER_COMPOSE)
        else true)
val enableJpackage by extra(
        if (project.hasProperty(ENABLE_JPACKAGE))
            "true" == project.property(ENABLE_JPACKAGE)
        else false)
val jpackageLocation: String by extra(
        if (project.hasProperty(JPACKAGE_HOME))
            project.property(JPACKAGE_HOME) as String
        else file(System.getProperty("user.home")).toPath()
                .resolve(".sdkman/candidates/java/14.ea.27-open/")
                .toAbsolutePath().toString())
//endregion

//region Versions
val PROJECT_VERSION = "1.0.0"
val TARGET_JAVA_VERSION = JavaVersion.VERSION_13
val JUNIT5_VERSION = "5.6.0-M1"
val JUNIT5_PLATFORM_VERSION = "1.5.2"
//endregion

//region Subprojects
configure(subprojects) {
    //region Plugins
    apply(plugin = "java")
    apply(plugin = "org.javamodularity.moduleplugin")
    apply(plugin = "jacoco")
    //endregion

    //region Metadata
    version = PROJECT_VERSION
    group = "wtf.metio.ilo"
    //endregion

    //region Repositories
    repositories {
        mavenCentral()
    }
    //endregion

    //region Java
    configure<JavaPluginConvention> {
        sourceCompatibility = TARGET_JAVA_VERSION
        targetCompatibility = TARGET_JAVA_VERSION
    }
    //endregion

    //region JUnit 5
    tasks {
        named<Test>("test") {
            useJUnitPlatform()
        }
    }

    dependencies {
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:$JUNIT5_VERSION")
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:$JUNIT5_VERSION")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher:$JUNIT5_PLATFORM_VERSION")
    }
    //endregion

    //region Test Coverage
    configure<JacocoPluginExtension> {
        toolVersion = "0.8.5"
    }
    //endregion
}
//endregion

//region Gradle
tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
//endregion
