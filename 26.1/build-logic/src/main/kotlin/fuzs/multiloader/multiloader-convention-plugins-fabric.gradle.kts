package fuzs.multiloader

import commonProject
import expectPlatform
import fuzs.multiloader.fabric.setupModJsonTask
import fuzs.multiloader.metadata.ModLoaderProvider
import mod
import net.fabricmc.loom.task.FabricModJsonV1Task
import org.gradle.api.internal.tasks.JvmConstants
import org.gradle.internal.extensions.stdlib.capitalized
import versionCatalog

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-platform")
    id("net.fabricmc.fabric-loom")
}

project.expectPlatform(ModLoaderProvider.FABRIC)

configurations {
    create("modLocalRuntime") {
        this@configurations.named("localRuntime") {
            extendsFrom(this@create)
        }
    }
}

loom {
    accessWidenerPath.set(project.commonProject.file("src/main/resources/${mod.id}.accesswidener"))

    decompilers {
        get("vineflower").apply {
            // Shows the method name of lambdas in a comment.
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runs {
        configureEach {
            name("${project.name} ${this.name.capitalized()} ${versionCatalog.findVersion("minecraft").get()}")
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
                }",
                "-Dpuzzleslib.isDevelopmentEnvironment=true",
                "-D${mod.id}.isDevelopmentEnvironment=true"
            )
        }

        named("client") {
            client()
            vmArgs(
                "-Dfabric-tag-conventions-v2.missingTagTranslationWarning=silenced",
                "-Dfabric-tag-conventions-v1.legacyTagWarning=silenced"
            )
        }

        named("server") {
            server()
            vmArgs(
                "-Dfabric-tag-conventions-v2.missingTagTranslationWarning=silenced",
                "-Dfabric-tag-conventions-v1.legacyTagWarning=silenced"
            )
        }
    }
}

repositories {
    maven {
        name = "Modmuss"
        url = uri("https://maven.modmuss50.me/")
    }
    maven {
        name = "Ladysnake Libs"
        url = uri("https://maven.ladysnake.org/releases/")
    }
    maven {
        name = "jamieswhiteshirt"
        url = uri("https://maven.jamieswhiteshirt.com/libs-release/")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${versionCatalog.findVersion("game").get()}")
    api(versionCatalog.findLibrary("fabricloader.fabric").get())

    if (!providers.gradleProperty("project.isolated").orNull.toBoolean()) {
        versionCatalog.findLibrary("modmenu.fabric")
            .orElse(null)
            ?.let { localRuntime(it) { isTransitive = false } }
    }
}

val generateModJson = tasks.register<FabricModJsonV1Task>("generateModJson") {
    setupModJsonTask()
}

tasks.named<ProcessResources>(JvmConstants.PROCESS_RESOURCES_TASK_NAME) {
    dependsOn(generateModJson)
}
