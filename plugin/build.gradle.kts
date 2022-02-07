import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}

group = "com.soarex16.kaliper"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("io.arrow-kt:arrow-meta:1.5.0-SNAPSHOT")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    compileOnly("io.arrow-kt:arrow-meta:1.5.0-SNAPSHOT")
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

tasks.getByName<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(configurations.compileClasspath.get().map { if (it.isDirectory()) it else zipTree(it) })
}