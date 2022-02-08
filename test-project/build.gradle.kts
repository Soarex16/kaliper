import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by rootProject.extra
val jvmTargetVersion: String by rootProject.extra
val arrowMetaVersion: String by rootProject.extra

plugins {
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    dependsOn(":plugin:shadowJar")
    kotlinOptions {
        jvmTarget = jvmTargetVersion
        freeCompilerArgs = freeCompilerArgs + listOf("-Xplugin=${rootDir}/plugin/build/libs/plugin-1.0-SNAPSHOT.jar",
            "-P", "plugin:arrow.meta.plugin.compiler:generatedSrcOutputDir=${buildDir}")
    }
}