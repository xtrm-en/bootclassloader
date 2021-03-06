private const val kotlinVersion = "1.6.10"

object Plugins {
    const val KOTLIN = kotlinVersion
    const val GRGIT = "4.1.1" // old version for jgit to work on Java 8
    const val BLOSSOM = "1.3.0"
    const val SHADOW = "7.1.2"
    const val KTLINT = "10.2.1"
    const val DOKKA = kotlinVersion
    const val NEXUS_PUBLISH = "1.0.0"
    const val GRADLE_RUST = "3.1.1"
}

object Dependencies {
    const val KOTLIN = kotlinVersion
    const val YANL = "0.7.1"
    const val DEFACE = "0.2.0"
    const val UNSAFE = "1.7.3"

    val kotlinModules = arrayOf("stdlib")
}

object Repositories {
    val mavenUrls = arrayOf(
        "https://jitpack.io/",
    )
}
