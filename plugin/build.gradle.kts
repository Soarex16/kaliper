import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val kotlinVersion: String by rootProject.extra
val jvmTargetVersion: String by rootProject.extra
val arrowMetaVersion: String by rootProject.extra

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("io.arrow-kt:arrow-meta:${arrowMetaVersion}")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:${kotlinVersion}")
    compileOnly("io.arrow-kt:arrow-meta:${arrowMetaVersion}")
}

tasks.getByName<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.compileOnly.get())
    dependencies {
        exclude("org.jetbrains.kotlin:kotlin-stdlib")
        exclude("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    }
    archiveBaseName.set("kaliper")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}