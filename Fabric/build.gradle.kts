plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-fabric")
}

dependencies {
    listOf(project(":Common-ForgeApi"), project(":Common-NeoForgeApi")).forEach {
        compileOnly(project(it.path)) { isTransitive = false }
        add("commonJava", project(mapOf("path" to it.path, "configuration" to "commonJava")))
        add("commonResources", project(mapOf("path" to it.path, "configuration" to "commonResources")))
    }

    modApi(libs.fabricapi.fabric)
    api(libs.nightconfigcore)
    include(libs.nightconfigcore)
    api(libs.nightconfigtoml)
    include(libs.nightconfigtoml)
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
