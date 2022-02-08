val kotlinVersion: String by extra("1.5.0")
val jvmTargetVersion: String by extra("1.8")
val arrowMetaVersion: String by extra("1.5.0-SNAPSHOT")

group = "com.soarex16.kaliper"
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