import fuzs.multiloader.metadata.ModLoaderProvider
import fuzs.multiloader.neoforge.setupModsTomlTask
import fuzs.multiloader.neoforge.toml.NeoForgeModsTomlSpec
import fuzs.multiloader.neoforge.toml.NeoForgeModsTomlTask
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.internal.tasks.JvmConstants
import kotlin.jvm.optionals.getOrNull

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-platform")
}

project.expectPlatform(ModLoaderProvider.FORGE)

loom {
    forge {
        convertAccessWideners = true
        extraAccessWideners.add(accessWidenerPath.asFile.get().name)
        listOf(project.commonProject, project).forEach {
            mixinConfig("${it.mod.id}.${it.name.lowercase()}.mixins.json")
        }
    }

    runs {
        named("client") {
            client()
            name("${project.name} Client ${versionCatalog.findVersion("minecraft").get()}")
            programArgs("--username", "Player####")
        }

        named("server") {
            server()
            name("${project.name} Server ${versionCatalog.findVersion("minecraft").get()}")
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
    compileOnly(project(":Common-NeoForgeApi")) { isTransitive = false }
    add("commonJava", project(mapOf("path" to ":Common-NeoForgeApi", "configuration" to "commonJava")))
    add("commonResources", project(mapOf("path" to ":Common-NeoForgeApi", "configuration" to "commonResources")))
    "forge"(versionCatalog.findLibrary("minecraftforge.forge").get())
}

tasks.named<RemapJarTask>("remapJar") {
    atAccessWideners.add(project.commonProject.loom.accessWidenerPath.map { it.asFile.name })
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
