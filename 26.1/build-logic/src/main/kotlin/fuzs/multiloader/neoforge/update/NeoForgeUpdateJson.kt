package fuzs.multiloader.neoforge.update

import kotlinx.serialization.Serializable

@Serializable
data class NeoForgeUpdateJson(
    val homepage: String,
    val promos: Map<String, String>
)
