plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-fabric")
}

dependencies {
    listOf(project(":Common-ForgeApi"), project(":Common-NeoForgeApi")).forEach {
        add("commonJava", project(mapOf("path" to it.path, "configuration" to "commonJava")))
        add("commonResources", project(mapOf("path" to it.path, "configuration" to "commonResources")))
        // This is only required for the IDE to see the common classes.
        compileOnly(project(it.path)) { isTransitive = false }
    }

    modApi(sharedLibs.fabricapi.fabric)
    api(sharedLibs.nightconfigcore.common)
    include(sharedLibs.nightconfigcore.common)
    api(sharedLibs.nightconfigtoml.common)
    include(sharedLibs.nightconfigtoml.common)
    modCompileOnly(sharedLibs.modmenu.fabric) { isTransitive = false }
    modLocalRuntime(sharedLibs.modmenu.fabric) { isTransitive = false }
}

tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE-FORGE.md"))
    from(rootProject.file("LICENSE-NIGHT-CONFIG.md"))
    from(rootProject.file("LICENSING.md"))
}

multiloader {
    modFile {
        packagePrefix.set("impl")
        library.set(true)
        json {
            entrypoint(
                "modmenu",
                "${project.group}.${project.name.lowercase()}.impl.integration.modmenu.ModMenuApiImpl"
            )
        }
    }
}
