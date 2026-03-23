plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

dependencies {
    compileOnly(project(":Common-ForgeApi")) { isTransitive = false }
    add("commonJava", project(mapOf("path" to ":Common-ForgeApi", "configuration" to "commonJava")))
    add("commonResources", project(mapOf("path" to ":Common-ForgeApi", "configuration" to "commonResources")))
}
