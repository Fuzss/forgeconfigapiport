package fuzs.multiloader.architectury

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArchitecturyCommonJson(
    val accessWidener: String?,
    @SerialName("injected_interfaces") val injectedInterfaces: Map<String, List<String>>?
)
