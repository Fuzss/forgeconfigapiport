package fuzs.multiloader.discord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordWebhookPayload(
    val content: String?,
    val embeds: List<EmbedEntry>?,
    val flags: Int?
)

@Serializable
data class EmbedEntry(
    val title: String?,
    val description: String?,
    val url: String?,
    val timestamp: String?,
    val color: Int?,
    val footer: FooterEntry?,
    val image: ImageEntry?,
    val thumbnail: ThumbnailEntry?,
    val video: VideoEntry?,
    val provider: ProviderEntry?,
    val author: AuthorEntry?,
    val fields: List<FieldEntry>?
)

@Serializable
data class ThumbnailEntry(
    val url: String,
    @SerialName("proxy_url") val proxyUrl: String?, val height: Int?, val width: Int?
)

@Serializable
data class VideoEntry(
    val url: String?,
    @SerialName("proxy_url") val proxyUrl: String?, val height: Int?, val width: Int?
)

@Serializable
data class ImageEntry(
    val url: String,
    @SerialName("proxy_url") val proxyUrl: String?, val height: Int?, val width: Int?
)

@Serializable
data class ProviderEntry(
    val name: String?,
    val url: String
)

@Serializable
data class AuthorEntry(
    val name: String,
    val url: String?,
    @SerialName("icon_url") val iconUrl: String?,
    @SerialName("proxy_icon_url") val proxyIconUrl: String?
)

@Serializable
data class FooterEntry(
    val text: String,
    @SerialName("icon_url") val iconUrl: String?,
    @SerialName("proxy_icon_url") val proxyIconUrl: String?
)

@Serializable
data class FieldEntry(val name: String, val value: String, val inline: Boolean?)

/**
 * See Also: [Discord Message Flags](https://discord.com/developers/docs/resources/message#message-object-message-flags)
 */
object MessageFlags {
    const val CROSSPOSTED: Int = 1 shl 0
    const val IS_CROSSPOST: Int = 1 shl 1
    const val SUPPRESS_EMBEDS: Int = 1 shl 2
    const val SOURCE_MESSAGE_DELETED: Int = 1 shl 3
    const val URGENT: Int = 1 shl 4
    const val HAS_THREAD: Int = 1 shl 5
    const val EPHEMERAL: Int = 1 shl 6
    const val LOADING: Int = 1 shl 7
    const val FAILED_TO_MENTION_SOME_ROLES_IN_THREAD: Int = 1 shl 8
    const val SUPPRESS_NOTIFICATIONS: Int = 1 shl 12
    const val IS_VOICE_MESSAGE: Int = 1 shl 13
    const val HAS_SNAPSHOT: Int = 1 shl 14
    const val IS_COMPONENTS_V2: Int = 1 shl 15
}
