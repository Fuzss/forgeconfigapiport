import fuzs.multiloader.metadata.ModLoaderProvider
import org.gradle.api.internal.tasks.JvmConstants

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-core")
}

project.expectPlatform(ModLoaderProvider.COMMON)

loom {
    accessWidenerPath.set(project.commonProject.loom.accessWidenerPath)
}

configurations {
    named("commonJava") {
        isCanBeResolved = true
    }
    named("commonResources") {
        isCanBeResolved = true
    }
}

dependencies {
    compileOnly(project(project.commonProject.path)) { isTransitive = false }
    add("commonJava", project(mapOf("path" to project.commonProject.path, "configuration" to "commonJava")))
    add("commonResources", project(mapOf("path" to project.commonProject.path, "configuration" to "commonResources")))
    loaderLibraries(versionCatalog.findLibrary("mixin.common").get())
    loaderLibraries(versionCatalog.findLibrary("mixinextras.common").get())
    compileOnlyApi(libs.nightconfigcore)
    compileOnlyApi(libs.nightconfigtoml)
}

tasks.named<JavaCompile>(JvmConstants.COMPILE_JAVA_TASK_NAME) {
    dependsOn(configurations.named("commonJava"))
    source(configurations.named("commonJava"))
}

tasks.named<ProcessResources>(JvmConstants.PROCESS_RESOURCES_TASK_NAME) {
    dependsOn(configurations.named("commonResources"))
    from(configurations.named("commonResources"))
    dependsOn(project.commonProject.tasks.named<ProcessResources>("processResources"))
    from(project.commonProject.layout.buildDirectory.dir("generated/resources"))
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(configurations.named("commonJava"))
    from(configurations.named("commonJava"))
    dependsOn(configurations.named("commonResources"))
    from(configurations.named("commonResources"))
}

tasks.named<Javadoc>(JvmConstants.JAVADOC_TASK_NAME) {
    dependsOn(configurations.named("commonJava"))
    source(configurations.named("commonJava"))
}

tasks.named("generateMixinConfig") {
    enabled = false
}
