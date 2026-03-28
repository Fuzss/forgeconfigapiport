package fuzs.multiloader

import fuzs.multiloader.classtweaker.*
import org.gradle.api.internal.tasks.JvmConstants

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-core")
    id("net.neoforged.moddev")
}

generateAccessTransformerFile(classTweakerFile, generatedAccessTransformerFile.get().asFile)

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

tasks.named<ProcessResources>(JvmConstants.PROCESS_RESOURCES_TASK_NAME) {
    exclude("**/*.classtweaker")
}

val generateTransitiveAccessTransformerFile =
    tasks.register<GenerateAccessTransformerTask>("generateTransitiveAccessTransformerFile") {
        inputFile.set(classTweakerFile)
        outputFile.set(generatedTransitiveAccessTransformerFile)
        accessLevels.set(TRANSITIVE_CLASS_TWEAKER_ACCESS_LEVELS)
    }

tasks.named("copyAccessTransformersPublications") {
    dependsOn(generateTransitiveAccessTransformerFile)
}
