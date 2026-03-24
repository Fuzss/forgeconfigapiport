package fuzs.multiloader.plugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import java.net.URI

class SettingsConventionPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) = with(settings) {
        val modName: String = providers.gradleProperty("mod.name").get()
        val projectLibs: String = providers.gradleProperty("project.libs").get()
        val projectPlatforms: String? = providers.gradleProperty("project.platforms").orNull

        rootProject.name = modName.replace(Regex("\\W"), "") + "-" + projectLibs.substringBeforeLast('-')
        val platformsList = projectPlatforms?.split(",")?.map { it.trim() }?.distinct() ?: emptyList()
        platformsList.forEach { include(it) }
        settings.plugins.apply("org.gradle.toolchains.foojay-resolver-convention")

        dependencyResolutionManagement {
            repositories {
                maven {
                    name = "Fuzs Mod Resources"
                    url = URI("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
                }
            }

            versionCatalogs {
                create("libs") {
                    from("fuzs.sharedcatalogs:sharedcatalogs:$projectLibs")
                    overrideKeys("project.libs", settings)
                }
            }
        }
    }
}

private fun VersionCatalogBuilder.overrideKeys(
    prefix: String,
    settings: Settings
) {
    for ((key, value) in settings.extensions.extraProperties.properties) {
        when {
            key.startsWith("$prefix.versions.") -> {
                val name = key.removePrefix("$prefix.versions.").replace(".", "-")
                version(name, value.toString())
            }

            key.startsWith("$prefix.libraries.") -> {
                val name = key.removePrefix("$prefix.libraries.").replace(".", "-")
                library(name, value.toString())
            }
        }
    }
}
