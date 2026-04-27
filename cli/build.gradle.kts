plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    application
}

dependencies {
    implementation(libs.clikt)
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
    mainClass = "team.dedinside.yapi.application.MainKt"
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
