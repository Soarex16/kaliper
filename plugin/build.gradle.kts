import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val groupId: String by project
val jvmTargetVersion: String by project
val arrowMetaVersion: String by project

plugins {
    kotlin("jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

group = groupId
version = "1.0-SNAPSHOT"

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

    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    compileOnly("io.arrow-kt:arrow-meta:${arrowMetaVersion}")
}

tasks.getByName<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.compileOnly.get())
    dependencies {
        exclude("org.jetbrains.kotlin:kotlin-stdlib")
        exclude("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}