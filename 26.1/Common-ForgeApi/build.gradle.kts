import fuzs.multiloader.metadata.ModLoaderProvider
import org.gradle.api.internal.tasks.JvmConstants

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-core")
    id("net.neoforged.moddev")
}

project.expectPlatform(ModLoaderProvider.COMMON)

neoForge {
    enable {
        neoFormVersion = versionCatalog.findVersion("neoform").get().requiredVersion
        isDisableRecompilation = true
    }
}

//loom {
//    accessWidenerPath.set(project.commonProject.loom.accessWidenerPath)
//}

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
    compileOnly(versionCatalog.findLibrary("mixin.common").get())
    compileOnly(versionCatalog.findLibrary("mixinextras.common").get())
    compileOnlyApi(libs.nightconfigcore.common)
    compileOnlyApi(libs.nightconfigtoml.common)
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
