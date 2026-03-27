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

    modApi(libs.fabricapi.fabric)
    api(libs.nightconfigcore.common)
    include(libs.nightconfigcore.common)
    api(libs.nightconfigtoml.common)
    include(libs.nightconfigtoml.common)
    modCompileOnly(libs.modmenu.fabric) { isTransitive = false }
    modLocalRuntime(libs.modmenu.fabric) { isTransitive = false }
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
