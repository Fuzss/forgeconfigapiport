package fuzs.multiloader.extension

import fuzs.multiloader.metadata.*
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByType

// Expose our shared version catalog
val Project.versionCatalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("sharedLibs")

// Load external mods once per project
val Project.externalMods: ExternalMods
    get() {
        if (project == rootProject) {
            return extensions.findByType(ExternalMods::class.java)
                ?: ExternalMods(ExternalMods.BY_ID.toMutableMap()).also {
                    extensions.add(ExternalMods::class.java, "externalMods", it)
                }
        } else {
            throw GradleException("External mod metadata is only available for the root project")
        }
    }

// Load metadata once per project
val Project.metadata: ModMetadata
    get() = extensions.findByType(ModMetadata::class.java) ?: loadMetadata().also {
        extensions.add(ModMetadata::class.java, "metadata", it)
    }

// Expose the mod entry from metadata
val Project.mod: ModEntry
    get() = metadata.mod

// Load the loom.platform Architectury Loom property
val Project.projectPlatform: ModLoaderProvider
    get() = (if (extra.has("loom.platform")) extra["loom.platform"] as? String else null)?.uppercase()
        ?.let { ModLoaderProvider.valueOf(it) } ?: ModLoaderProvider.COMMON

fun Project.expectPlatform(platform: ModLoaderProvider) {
    if (platform != project.projectPlatform) {
        throw GradleException("Mismatched platform: ${project.projectPlatform} != ${platform}, define via loom.platform=${platform} in gradle.properties")
    }
}

// Get the Common project from the Loom property
val Project.commonProject: Project
    get() = rootProject.subprojects.first { !it.projectPlatform.platform }

// Get the platform projects from the Loom property
val Project.platformProjects: List<Project>
    get() = rootProject.subprojects.filter { it.projectPlatform.platform }
