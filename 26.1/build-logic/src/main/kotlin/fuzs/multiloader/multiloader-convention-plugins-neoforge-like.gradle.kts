package fuzs.multiloader

import fuzs.multiloader.classtweaker.*
import net.neoforged.moddevgradle.boot.ModDevPlugin

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-core")
    id("net.neoforged.moddev")
}

generateAccessTransformerFile(classTweakerFile, generatedAccessTransformerFile)
generateAccessTransformerFile(
    classTweakerFile, generatedTransitiveAccessTransformerFile,
    TRANSITIVE_CLASS_TWEAKER_ACCESS_LEVELS
)

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            mapOf(
                "Build-Tool-Name" to "ModDevGradle",
                "Build-Tool-Version" to (ModDevPlugin::class.java.`package`.implementationVersion ?: "unknown")
            )
        )
    }
}

configurations {
    named("modApi") {
        extendsFrom(this@configurations.named("accessTransformers").get())
    }

    named("modImplementation") {
        extendsFrom(this@configurations.named("accessTransformers").get())
    }

    named("modCompileOnly") {
        extendsFrom(this@configurations.named("accessTransformers").get())
    }

    named("modCompileOnlyApi") {
        extendsFrom(this@configurations.named("accessTransformers").get())
    }
}

neoForge {
    validateAccessTransformers = true
    accessTransformers {
        files.setFrom(generatedAccessTransformerFile)
        publish(generatedTransitiveAccessTransformerFile)
    }
}
