package fuzs.multiloader

import fuzs.multiloader.classtweaker.*

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
