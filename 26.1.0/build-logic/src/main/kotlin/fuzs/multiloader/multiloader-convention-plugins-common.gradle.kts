package fuzs.multiloader

import fuzs.multiloader.extension.expectPlatform
import fuzs.multiloader.extension.versionCatalog
import fuzs.multiloader.metadata.ModLoaderProvider
import org.gradle.api.internal.tasks.JvmConstants
import kotlin.jvm.optionals.getOrNull

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge-like")
}

project.expectPlatform(ModLoaderProvider.COMMON)

neoForge {
    enable {
        neoFormVersion = versionCatalog.findVersion("neoform").get().requiredVersion
        isDisableRecompilation = true
    }
}

dependencies {
    compileOnly(versionCatalog.findLibrary("mixin.common").get())
    compileOnly(versionCatalog.findLibrary("mixinextras.common").get())

    if (!providers.gradleProperty("project.isolated").orNull.toBoolean()) {
        versionCatalog.findLibrary("multiloaderaccesswideners.common")
            .getOrNull()
            ?.let { accessTransformers(it) { isTransitive = false } }
    }
}
