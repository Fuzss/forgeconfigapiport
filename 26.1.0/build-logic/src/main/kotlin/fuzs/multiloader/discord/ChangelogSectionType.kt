package fuzs.multiloader.discord

enum class ChangelogSectionType(val emoji: String) {
    ADDED("✨"),
    CHANGED("🔧"),
    DEPRECATED("📉"),
    REMOVED("🗑️"),
    FIXED("🐞"),
    SECURITY("🔒");

    companion object {
        private const val DEFAULT_EMOJI = "📌"

        fun emojiByName(name: String): String =
            runCatching { ChangelogSectionType.valueOf(name.uppercase()) }.getOrNull()?.emoji
                ?: DEFAULT_EMOJI
    }
}
