import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val groupId: String by project
val jvmTargetVersion: String by project
val arrowMetaVersion: String by project

plugins {
    kotlin("jvm")
}

group = "com.soarex16"
version = "1.0"

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