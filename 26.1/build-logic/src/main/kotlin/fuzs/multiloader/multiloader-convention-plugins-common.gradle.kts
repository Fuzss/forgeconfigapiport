package fuzs.multiloader

import expectPlatform
import fuzs.multiloader.metadata.ModLoaderProvider
import versionCatalog

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

    validateAccessTransformers = true
    // TODO add access transformer
}

dependencies {
    compileOnly(versionCatalog.findLibrary("mixin.common").get())
    compileOnly(versionCatalog.findLibrary("mixinextras.common").get())

    if (!providers.gradleProperty("project.isolated").orNull.toBoolean()) {
        versionCatalog.findLibrary("multiloaderaccesswideners.common")
            .orElse(null)
            ?.let { compileOnly(it) { isTransitive = false } }
    }
}
