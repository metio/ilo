plugins {
    `java-library`
}

dependencies {
    implementation(project(":modules:exec:exec-api"))
    implementation(project(":modules:tools:tools-api"))
    implementation(project(":modules:app:usage-help"))
    implementation(project(":modules:app:user-input"))

    implementation("org.tinylog:tinylog-api:2.0.1")
}
