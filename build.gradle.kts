val groupId: String by project
val kotlinVersion: String by project
val jvmTargetVersion: String by project
val arrowMetaVersion: String by project

group = groupId
version = "1.0"

plugins {
    kotlin("jvm") version "1.5.0"
}

allprojects {
    repositories {
        mavenCentral()
        maven(url ="https://oss.sonatype.org/content/repositories/snapshots/")
    }
}