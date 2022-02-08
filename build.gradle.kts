val groupId: String by project
val kotlinVersion: String by project
val jvmTargetVersion: String by project
val arrowMetaVersion: String by project

group = groupId
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        maven(url ="https://oss.sonatype.org/content/repositories/snapshots/")
    }
}