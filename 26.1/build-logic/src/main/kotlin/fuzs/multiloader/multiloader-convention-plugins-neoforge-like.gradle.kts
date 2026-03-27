package fuzs.multiloader

import fuzs.multiloader.classtweaker.TRANSITIVE_CLASS_TWEAKER_ACCESS_LEVELS
import fuzs.multiloader.classtweaker.classTweakerFile
import fuzs.multiloader.classtweaker.generateAccessTransformerFile
import fuzs.multiloader.classtweaker.generatedAccessTransformerFile
import fuzs.multiloader.classtweaker.generatedTransitiveAccessTransformerFile
import fuzs.multiloader.extension.versionCatalog
import gradle.kotlin.dsl.accessors._67ac5cfd97f494774d312633a4de8939.neoForge
import org.gradle.kotlin.dsl.assign

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-core")
    id("net.neoforged.moddev")
}

generateAccessTransformerFile(classTweakerFile, generatedAccessTransformerFile)
generateAccessTransformerFile(
    classTweakerFile, generatedTransitiveAccessTransformerFile,
    TRANSITIVE_CLASS_TWEAKER_ACCESS_LEVELS
)

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
