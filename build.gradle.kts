/*
 * This file is part of ilo. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of ilo,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */

//region Plugins
plugins {
    java
    application
    jacoco
    id("org.javamodularity.moduleplugin") version "1.6.0"
    id("org.beryx.jlink") version "2.17.0"
    id("com.palantir.graal") version "0.6.0-67-gaa8ea65"
}
//endregion

//region Repositories
repositories {
    mavenCentral()
}
//endregion

//region CLI switches
val ENABLE_JPACKAGE = "enable-jpackage"
val JPACKAGE_HOME = "jpackage-home"

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
val PROJECT_VERSION = "2.0.0"
val TARGET_JAVA_VERSION = JavaVersion.VERSION_11
val JUNIT5_VERSION = "5.6.0-RC1"
val JUNIT5_PLATFORM_VERSION = "1.5.2"
//endregion

//region Project
version = PROJECT_VERSION
group = "wtf.metio.ilo"
//endregion

//region Application
application {
    mainClassName = "wtf.metio.ilo/wtf.metio.ilo.Ilo"
    applicationName = "ilo"
}
tasks.named<Tar>("distTar") {
    enabled = false
}
distributions {
    main {
        contents {
            from("LICENSE")
            from("README.asciidoc")
            from("CHANGELOG.asciidoc")
        }
    }
}
//endregion

//region Java
java {
    sourceCompatibility = TARGET_JAVA_VERSION
    targetCompatibility = TARGET_JAVA_VERSION
}
//endregion

//region JUnit5
tasks {
    named<Test>("test") {
        useJUnitPlatform()
    }
}
//endregion

dependencies {
    implementation("info.picocli:picocli:4.1.4")
    annotationProcessor("info.picocli:picocli-codegen:4.1.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$JUNIT5_VERSION")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$JUNIT5_VERSION")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$JUNIT5_PLATFORM_VERSION")
}

//region Test Coverage
jacoco {
    toolVersion = "0.8.5"
}
//endregion

//region GraalVM
graal {
    mainClass("wtf.metio.ilo.Ilo")
    outputName("ilo")
    // https://github.com/palantir/gradle-graal/issues/239
    // graalVersion("19.3.1")
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


//region Gradle
tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}
//endregion
