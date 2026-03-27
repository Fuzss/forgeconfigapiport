plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

dependencies {
    add("commonJava", project(mapOf("path" to ":Common-ForgeApi", "configuration" to "commonJava")))
    add("commonResources", project(mapOf("path" to ":Common-ForgeApi", "configuration" to "commonResources")))
    // This is only required for the IDE to see the common classes.
    compileOnly(project(":Common-ForgeApi")) { isTransitive = false }
}
