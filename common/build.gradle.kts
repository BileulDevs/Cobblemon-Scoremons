plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("neoforge", "fabric")
}

loom {
    silentMojangMappingsLicense()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    // Mojang mappings are mandatory here: NeoForge only ships Mojmap, so Yarn cannot be shared with it.
    mappings(loom.officialMojangMappings())

    // Loader-agnostic Cobblemon artifact: only the API surface common to both loaders is visible.
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") { isTransitive = false }

    // Annotations only: the mixins target Cobblemon classes and are applied by each loader at runtime.
    compileOnly("org.spongepowered:mixin:0.8.5")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junit_version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("junit_version")}")
}
