import fuzs.multiloader.classtweaker.classTweakerFile
import fuzs.multiloader.classtweaker.generateAccessTransformerFile
import fuzs.multiloader.classtweaker.generatedAccessTransformerFile
import fuzs.multiloader.extension.expectPlatform
import fuzs.multiloader.extension.mod
import fuzs.multiloader.extension.versionCatalog
import fuzs.multiloader.metadata.ModLoaderProvider
import fuzs.multiloader.neoforge.setupModsTomlTask
import fuzs.multiloader.neoforge.toml.NeoForgeModsTomlSpec
import fuzs.multiloader.neoforge.toml.NeoForgeModsTomlTask
import net.fabricmc.loom.LoomGradlePlugin
import org.gradle.api.internal.tasks.JvmConstants
import kotlin.jvm.optionals.getOrNull

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-platform")
    id("dev.architectury.loom-no-remap") version "1.14-SNAPSHOT"
}

project.expectPlatform(ModLoaderProvider.FORGE)
generateAccessTransformerFile(classTweakerFile, generatedAccessTransformerFile)

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            mapOf(
                "Build-Tool-Name" to "Architectury Loom",
                "Build-Tool-Version" to (LoomGradlePlugin::class.java.`package`.implementationVersion ?: "unknown")
            )
        )
    }
}

loom {
//    forge {
//        listOf(project.commonProject, project).forEach {
//            mixinConfig("${it.mod.id}.${it.name.lowercase()}.mixins.json")
//        }
//    }

    decompilers {
        get("vineflower").apply {
            // Shows the method name of lambdas in a comment.
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runs {
        configureEach {
            name(
                "${project.name} ${this.name.replaceFirstChar { it.titlecase() }} ${
                    versionCatalog.findVersion("minecraft").get()
                }"
            )

            runDir("../run")
            ideConfigGenerated(true)
            startFirstThread()
            vmArgs(
                "-Xms1G",
                "-Xmx4G",
                "-Dmixin.debug.export=true",
                "-Dlog4j2.configurationFile=${
                    this@configureEach.javaClass.classLoader.getResource("log4j.xml")
                        ?: throw IllegalStateException("log4j.xml not found in plugin resources")
                }"
            )
        }

        named("client") {
            client()
            programArgs("--username", "Player####")
        }

        named("server") {
            server()
            programArgs("--nogui")
        }
    }
}

repositories {
    maven {
        name = "Minecraft Forge"
        url = uri("https://maven.minecraftforge.net/")
    }
}

dependencies {
    add("commonJava", project(mapOf("path" to ":Common-NeoForgeApi", "configuration" to "commonJava")))
    add("commonResources", project(mapOf("path" to ":Common-NeoForgeApi", "configuration" to "commonResources")))
    // This is only required for the IDE to see the common classes.
    compileOnly(project(":Common-NeoForgeApi")) { isTransitive = false }

    minecraft("com.mojang:minecraft:${versionCatalog.findVersion("game").get()}")
//    "forge"("net.minecraftforge:forge:1.21.11-62.0.3")
}

val generateModsToml = tasks.register<NeoForgeModsTomlTask>("generateModsToml") {
    fun version(alias: String): String? =
        project.versionCatalog.findVersion(alias).getOrNull()?.requiredVersion?.let { "[${it},)" }

    setupModsTomlTask()
    outputFile.set(project.layout.buildDirectory.file("generated/resources/META-INF/mods.toml"))

    toml {
        dependencies.set(
            dependencies.get().filterNot {
                it.properties.get().modId.orNull == "neoforge"
            }
        )

        dependency(project.mod.id) {
            modId.set("forge")
            type.set(NeoForgeModsTomlSpec.DependencySpec.Type.REQUIRED)
            version("minecraftforge.min")?.let { versionRange.set(it) }
        }
    }
}

tasks.named<ProcessResources>(JvmConstants.PROCESS_RESOURCES_TASK_NAME) {
    dependsOn(generateModsToml)
}
