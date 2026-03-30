package fuzs.multiloader.mixin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MixinConfigJson(
    val parent: String?,
    val target: String?,
    val minVersion: String?,
    val requiredFeatures: List<String>?,
    val compatibilityLevel: String?,
    val required: Boolean?,
    val priority: Int?,
    val mixinPriority: Int?,
    @SerialName("package") val mixinPackage: String,
    val mixins: List<String>?,
    val client: List<String>?,
    val server: List<String>?,
    val setSourceFile: Boolean?,
    val refmap: String?,
    val refmapWrapper: String?,
    val verbose: Boolean?,
    val plugin: String?,
    val injectors: InjectorsEntry?,
    val overwrites: OverwritesEntry?,
    val mixinextras: MixinExtrasEntry?
)

@Serializable
data class InjectorsEntry(
    val defaultRequire: Int?,
    val defaultGroup: String?,
    val namespace: String?,
    val injectionPoints: List<String>?,
    val dynamicSelectors: List<String>?,
    val maxShiftBy: Int?
)

@Serializable
data class OverwritesEntry(val conformVisibility: Boolean?, val requireAnnotations: Boolean?)

@Serializable
data class MixinExtrasEntry(val minVersion: String?)
