package fuzs.multiloader.neoforge.toml

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class NeoForgeModsToml(
    val modLoader: String?,
    val loaderVersion: String?,
    val license: String,
    val showAsResourcePack: Boolean?,
    val showAsDataPack: Boolean?,
    val services: List<String>?,
    val properties: Map<String, @Contextual Any>?,
    val issueTrackerURL: String?,
    val mods: List<@Contextual ModEntry>?,
    val features: Map<String, Map<String, @Contextual Any>>?,
    val modproperties: Map<String, Map<String, @Contextual Any>>?,
    val accessTransformers: List<AccessTransformerEntry>?,
    val mixins: List<MixinEntry>?,
    val dependencies: Map<String, List<DependencyEntry>>?,
    @Transient
    val extraProperties: Map<String, Map<String, Any>>? = null,
    @Transient
    val extraArrayProperties: Map<String, List<Map<String, Any>>>? = null
)

@Serializable
data class ModEntry(
    val modId: String,
    val namespace: String?,
    val version: String?,
    val displayName: String?,
    val description: String?,
    val logoFile: String?,
    val logoBlur: Boolean?,
    val updateJSONURL: String?,
    val modUrl: String?,
    val credits: String?,
    val authors: String?,
    val displayURL: String?,
    val enumExtensions: String?,
    val featureFlags: String?
)

@Serializable
data class AccessTransformerEntry(
    val file: String
)

@Serializable
data class MixinEntry(
    val config: String,
    val requiredMods: List<String>?,
    val behaviorVersion: String?
)

@Serializable
data class DependencyEntry(
    val modId: String,
    // for Minecraft Forge compatibility this must be present (and is ignored by NeoForge)
    val mandatory: Boolean,
    val type: NeoForgeModsTomlSpec.DependencySpec.Type?,
    val reason: String?,
    val versionRange: String?,
    val ordering: NeoForgeModsTomlSpec.DependencySpec.Ordering?,
    val side: NeoForgeModsTomlSpec.DependencySpec.Side?,
    val referralUrl: String?
)
