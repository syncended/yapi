plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("team.dedinside.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
