rootProject.name = "Scoremons"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        gradlePluginPortal()
    }
}

// "common" must stay first: both platform projects consume its namedElements/transformProduction* configurations.
listOf(
    "common",
    "neoforge",
    "fabric"
).forEach { include(it) }
