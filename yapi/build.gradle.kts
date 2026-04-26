plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

allprojects {
    group = "team.dedinside"
    version = "0.0.1"
}

subprojects {
    repositories {
        mavenCentral()
    }
}
