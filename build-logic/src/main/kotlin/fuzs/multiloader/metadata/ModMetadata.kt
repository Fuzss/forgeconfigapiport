package fuzs.multiloader.metadata

import fuzs.multiloader.extension.platformProjects
import fuzs.multiloader.extension.projectPlatform
import fuzs.multiloader.extension.versionCatalog
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.api.Project

@Serializable
data class ModMetadata(
    val minecraft: String,
    val mod: ModEntry,
    val dependencies: List<DependencyEntry>,
    val links: List<DistributionEntry>,
    val environments: EnvironmentsEntry,
    val platforms: List<ModLoaderProvider>
)

@Serializable
data class ModEntry(
    val id: String,
    val group: String,
    val name: String,
    val version: String,
    val authors: List<String>,
    val description: String,
    val license: String
)

@Serializable
data class DependencyEntry(
    val name: String,
    val type: DependencyType,
    val platforms: List<ModLoaderProvider>
)

@Serializable
data class DistributionEntry(
    val name: LinkProvider,
    val slug: String,
    val id: String? = null
) {
    fun url(): String = name.url(slug)

    fun updateUrl(modId: String): String? = id?.let { name.updateUrl(it, modId) }
}

@Serializable
data class EnvironmentsEntry(
    val client: DependencyType,
    val server: DependencyType
) {
    fun forClient() = !client.unsupported
    fun forServer() = !server.unsupported
    fun forBoth() = forClient() && forServer()
    fun clientOnly() = forClient() && server.unsupported
    fun serverOnly() = client.unsupported && forServer()
}

enum class ModLoaderProvider(val platform: Boolean = true) {
    @SerialName("common")
    COMMON(false) {
        override fun matches(modLoader: ModLoaderProvider): Boolean = true
    },

    @SerialName("fabric")
    FABRIC,

    @SerialName("forge")
    FORGE,

    @SerialName("neoforge")
    NEOFORGE,

    @SerialName("quilt")
    QUILT;

    open fun matches(modLoader: ModLoaderProvider): Boolean = this == modLoader
}

enum class DependencyType(
    val required: Boolean = false,
    val optional: Boolean = false,
    val unsupported: Boolean = false,
    val mandatory: Boolean = false
) {
    @SerialName("embedded")
    EMBEDDED(required = true),

    @SerialName("optional")
    OPTIONAL(optional = true),

    @SerialName("required")
    REQUIRED(required = true, mandatory = true),

    @SerialName("unsupported")
    UNSUPPORTED(unsupported = true)
}

enum class LinkProvider(val index: Int, val baseUrl: String) {
    @SerialName("curseforge")
    CURSEFORGE(1, "https://www.curseforge.com/minecraft/mc-mods/") {
        override fun updateUrl(projectId: String, modId: String): String {
            return "https://curseupdate.com/${projectId}/${modId}?ml=neoforge"
        }
    },

    @SerialName("github")
    GITHUB(2, "https://github.com/Fuzss/"),

    @SerialName("modrinth")
    MODRINTH(0, "https://modrinth.com/mod/") {
        override fun updateUrl(projectId: String, modId: String): String {
            return "https://api.modrinth.com/updates/${projectId}/forge_updates.json?neoforge=only"
        }
    };

    fun url(projectSlug: String): String = "${baseUrl}${projectSlug}"

    open fun updateUrl(projectId: String, modId: String): String? = null
}

fun Project.loadMetadata(): ModMetadata {
    val properties: Map<String, String> =
        (rootProject.properties + project.properties).mapValues { it.value.toString() }

    fun collect(prefix: String): Map<String, String> =
        properties
            .filterKeys { it.startsWith(prefix) }
            .mapKeys { it.key.removePrefix(prefix) }

    val dependencyProperties = collect("dependencies.")
    val distributionProperties = collect("distributions.")
    val environmentProperties = collect("environments.")

    val minecraftVersion = versionCatalog.findVersion("minecraft").get().requiredVersion
    val platforms = project.platformProjects.map { it.projectPlatform }.distinct().sortedBy { it.name }

    val dependencies = dependencyProperties.entries
        .flatMap { (key, value) ->
            val name = key.substringAfter('.')
            val type = DependencyType.valueOf(value.uppercase())
            val platform = ModLoaderProvider.valueOf(key.substringBefore('.').uppercase())
            val platforms =
                if (!platform.platform) platforms else if (platform in platforms) listOf(platform) else emptyList()
            platforms.map { it to (name to type) }
        }
        .groupBy({ it.second }, { it.first })
        .map { (key, platforms) ->
            DependencyEntry(key.first, key.second, platforms.distinct().sortedBy { it.name })
        }
        .sortedWith(compareBy({ it.type }, { it.name }))

    val distributions = distributionProperties
        .entries
        .groupBy { it.key.substringBefore('.') }
        .map { (name, entries) ->
            val map = entries.associate { it.key.substringAfter('.') to it.value }
            DistributionEntry(
                LinkProvider.valueOf(name.uppercase()),
                map["slug"]!!,
                map["id"]
            )
        }
        .sortedBy { it.name }

    val environments =
        EnvironmentsEntry(
            environmentProperties["client"]?.uppercase()?.let { DependencyType.valueOf(it) }
                ?: DependencyType.REQUIRED,
            environmentProperties["server"]?.uppercase()?.let { DependencyType.valueOf(it) }
                ?: DependencyType.REQUIRED
        ).also {
            require(!it.client.unsupported || !it.server.unsupported) { "No environments defined" }
        }

    val authorList = properties["mod.authors"]
        ?.split(',')
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: error("No authors defined")

    return ModMetadata(
        minecraftVersion,
        ModEntry(
            properties["mod.id"]!!,
            properties["mod.group"]!!,
            properties["mod.name"]!!,
            properties["mod.version"]!!,
            authorList,
            properties["mod.description"]!!,
            properties["mod.license"]!!
        ),
        dependencies,
        distributions,
        environments,
        platforms
    )
}
