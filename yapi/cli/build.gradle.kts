plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(libs.mordant.core)
    implementation(libs.mordant.markdown)
    implementation(libs.mordant.coroutines)
    implementation(libs.kotlinx.coroutines.core)
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
