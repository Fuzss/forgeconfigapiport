package fuzs.multiloader.neoforge

import fuzs.multiloader.extension.*
import fuzs.multiloader.metadata.LinkProvider
import fuzs.multiloader.metadata.ModLoaderProvider
import fuzs.multiloader.neoforge.toml.NeoForgeModsTomlSpec
import fuzs.multiloader.neoforge.toml.NeoForgeModsTomlTask
import kotlin.jvm.optionals.getOrNull

fun NeoForgeModsTomlTask.setupModsTomlTask() {
    val multiLoaderExtension = project.extensions.getByType(MultiLoaderExtension::class.java)
    outputFile.set(project.layout.buildDirectory.file("generated/resources/META-INF/neoforge.mods.toml"))

    toml {
        license.set(project.mod.license)
        project.metadata.links
            .firstOrNull { it.name == LinkProvider.GITHUB }
            ?.url()
            ?.let { issueTrackerURL.set("$it/issues") }

        mod {
            modId.set(project.mod.id)
            displayName.set(project.mod.name)
            description.set(project.mod.description)
            version.set(project.mod.version)
            authors.set(project.mod.authors.joinToString(", "))
            logoFile.set("pack.png")

            project.metadata.links
                .minByOrNull { it.name.index }
                ?.url()
                ?.let { displayURL.set(it) }

            project.metadata.links
                .sortedBy { it.name.index }
                .map { it.updateUrl(project.mod.id) }
                .firstOrNull()
                ?.let { updateJSONURL.set(it) }

            multiLoaderExtension.modFile.orNull?.enumExtensions?.orNull?.let { enumExtensions.set(it) }
        }

        listOf(project.commonProject, project).forEach {
            mixin("${it.mod.id}.${it.name.lowercase()}.mixins.json")
        }

        addDependencies()
        multiLoaderExtension.modFile.orNull?.toml?.orNull?.execute(this)
    }
}

private fun NeoForgeModsTomlTask.addDependencies() {
    fun incrementPatch(version: String): String {
        val parts = version.split(".").toMutableList()
        when {
            parts.size < 2 -> throw IllegalArgumentException("Version must have at least MAJOR.MINOR")
            parts.size == 2 -> parts.add("0")
        }

        parts[parts.lastIndex] = (parts.last().toInt() + 1).toString()
        return parts.joinToString(".")
    }

    fun version(alias: String): String? =
        project.versionCatalog.findVersion(alias).getOrNull()?.requiredVersion?.let { "[${it},)" }

    toml {
        dependency(project.mod.id) {
            modId.set("minecraft")
            type.set(NeoForgeModsTomlSpec.DependencySpec.Type.REQUIRED)
            versionRange.set(
                project.versionCatalog.findVersion("minecraft").get().requiredVersion
                    .let { "[${it},${incrementPatch(it)})" }
            )
        }

        dependency(project.mod.id) {
            modId.set("neoforge")
            type.set(NeoForgeModsTomlSpec.DependencySpec.Type.REQUIRED)
            version("neoforge.min")?.let { versionRange.set(it) }
        }

        for (entry in project.metadata.dependencies) {
            if (entry.platforms.any { it.matches(ModLoaderProvider.NEOFORGE) }) {
                val modId = project.rootProject.externalMods.mods[entry.name]?.mod?.id ?: entry.name
                when (entry.name) {
                    "puzzleslib" -> dependency(project.mod.id) {
                        this.modId.set(modId)
                        type.set(NeoForgeModsTomlSpec.DependencySpec.Type.REQUIRED)
                        version("puzzleslib.min")?.let { versionRange.set(it) }
                    }

                    else -> dependency(project.mod.id) {
                        this.modId.set(modId)
                        when {
                            entry.type.required -> type.set(NeoForgeModsTomlSpec.DependencySpec.Type.REQUIRED)
                            entry.type.optional -> type.set(NeoForgeModsTomlSpec.DependencySpec.Type.OPTIONAL)
                            entry.type.unsupported -> type.set(NeoForgeModsTomlSpec.DependencySpec.Type.INCOMPATIBLE)
                        }
                    }
                }
            }
        }
    }
}
