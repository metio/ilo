plugins {
    `java-library`
}

dependencies {
    implementation(project(":modules:exec:exec-api"))
    implementation(project(":modules:os:generic"))
    implementation(project(":modules:tools:tools-api"))
    implementation(project(":modules:tools:docker"))
}
