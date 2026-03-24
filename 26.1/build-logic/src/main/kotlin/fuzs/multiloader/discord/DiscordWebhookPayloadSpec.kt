package fuzs.multiloader.discord

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import javax.inject.Inject

abstract class DiscordWebhookPayloadSpec {
    @get:Inject
    abstract val objects: ObjectFactory

    @get:Input
    abstract val channel: Property<String>

    @get:Input
    abstract val token: Property<String>

    @get:Input
    @get:Optional
    abstract val content: Property<String>

    @get:Input
    @get:Optional
    abstract val flags: Property<Int>

    @get:Input
    @get:Optional
    abstract val debug: Property<Boolean>

    @get:Nested
    @get:Optional
    abstract val embeds: ListProperty<EmbedSpec>

    fun embed(action: Action<EmbedSpec>) {
        val entry = objects.newInstance(EmbedSpec::class.java).also { action.execute(it) }
        embeds.add(entry)
    }

    abstract class EmbedSpec {
        @get:Inject
        abstract val objects: ObjectFactory

        @get:Input
        @get:Optional
        abstract val title: Property<String>

        @get:Input
        @get:Optional
        abstract val description: Property<String>

        @get:Input
        @get:Optional
        abstract val url: Property<String>

        @get:Input
        @get:Optional
        abstract val timestamp: Property<String>

        @get:Input
        @get:Optional
        abstract val color: Property<Int>

        @get:Nested
        @get:Optional
        abstract val footer: Property<EmbedFooterSpec>

        @get:Nested
        @get:Optional
        abstract val image: Property<EmbedImageSpec>

        @get:Nested
        @get:Optional
        abstract val thumbnail: Property<EmbedThumbnailSpec>

        @get:Nested
        @get:Optional
        abstract val video: Property<EmbedVideoSpec>

        @get:Nested
        @get:Optional
        abstract val provider: Property<EmbedProviderSpec>

        @get:Nested
        @get:Optional
        abstract val author: Property<EmbedAuthorSpec>

        @get:Nested
        @get:Optional
        abstract val fields: ListProperty<EmbedFieldSpec>

        fun footer(text: String) {
            footer(text) {
                // NO-OP
            }
        }

        fun footer(text: String, action: Action<EmbedFooterSpec>) {
            footer {
                this.text.set(text)
                action.execute(this)
            }
        }

        fun footer(action: Action<EmbedFooterSpec>) {
            val entry = objects.newInstance(EmbedFooterSpec::class.java).also { action.execute(it) }
            footer.set(entry)
        }

        fun image(url: String) {
            image(url) {
                // NO-OP
            }
        }

        fun image(url: String, action: Action<EmbedImageSpec>) {
            image {
                this.url.set(url)
                action.execute(this)
            }
        }

        fun image(action: Action<EmbedImageSpec>) {
            val entry = objects.newInstance(EmbedImageSpec::class.java).also { action.execute(it) }
            image.set(entry)
        }

        fun thumbnail(url: String) {
            thumbnail(url) {
                // NO-OP
            }
        }

        fun thumbnail(url: String, action: Action<EmbedThumbnailSpec>) {
            thumbnail {
                this.url.set(url)
                action.execute(this)
            }
        }

        fun thumbnail(action: Action<EmbedThumbnailSpec>) {
            val entry = objects.newInstance(EmbedThumbnailSpec::class.java).also { action.execute(it) }
            thumbnail.set(entry)
        }

        fun video(action: Action<EmbedVideoSpec>) {
            val entry = objects.newInstance(EmbedVideoSpec::class.java).also { action.execute(it) }
            video.set(entry)
        }

        fun provider(url: String) {
            provider(url) {
                // NO-OP
            }
        }

        fun provider(url: String, action: Action<EmbedProviderSpec>) {
            provider {
                this.url.set(url)
                action.execute(this)
            }
        }

        fun provider(action: Action<EmbedProviderSpec>) {
            val entry = objects.newInstance(EmbedProviderSpec::class.java).also { action.execute(it) }
            provider.set(entry)
        }

        fun author(name: String) {
            author(name) {
                // NO-OP
            }
        }

        fun author(name: String, action: Action<EmbedAuthorSpec>) {
            author {
                this.name.set(name)
                action.execute(this)
            }
        }

        fun author(action: Action<EmbedAuthorSpec>) {
            val entry = objects.newInstance(EmbedAuthorSpec::class.java).also { action.execute(it) }
            author.set(entry)
        }

        fun field(name: String, value: String) {
            field(name, value) {
                // NO-OP
            }
        }

        fun field(name: String, value: String, action: Action<EmbedFieldSpec>) {
            field {
                this.name.set(name)
                this.value.set(value)
                action.execute(this)
            }
        }

        fun field(action: Action<EmbedFieldSpec>) {
            val entry = objects.newInstance(EmbedFieldSpec::class.java).also { action.execute(it) }
            fields.add(entry)
        }
    }

    abstract class EmbedThumbnailSpec {
        @get:Input
        abstract val url: Property<String>

        @get:Input
        @get:Optional
        abstract val proxyUrl: Property<String>

        @get:Input
        @get:Optional
        abstract val height: Property<Int>

        @get:Input
        @get:Optional
        abstract val width: Property<Int>
    }

    abstract class EmbedVideoSpec {
        @get:Input
        @get:Optional
        abstract val url: Property<String>

        @get:Input
        @get:Optional
        abstract val proxyUrl: Property<String>

        @get:Input
        @get:Optional
        abstract val height: Property<Int>

        @get:Input
        @get:Optional
        abstract val width: Property<Int>
    }

    abstract class EmbedImageSpec {
        @get:Input
        abstract val url: Property<String>

        @get:Input
        @get:Optional
        abstract val proxyUrl: Property<String>

        @get:Input
        @get:Optional
        abstract val height: Property<Int>

        @get:Input
        @get:Optional
        abstract val width: Property<Int>
    }

    abstract class EmbedProviderSpec {
        @get:Input
        @get:Optional
        abstract val name: Property<String>

        @get:Input
        abstract val url: Property<String>
    }

    abstract class EmbedAuthorSpec {
        @get:Input
        abstract val name: Property<String>

        @get:Input
        @get:Optional
        abstract val url: Property<String>

        @get:Input
        @get:Optional
        abstract val iconUrl: Property<String>

        @get:Input
        @get:Optional
        abstract val proxyIconUrl: Property<String>
    }

    abstract class EmbedFooterSpec {
        @get:Input
        abstract val text: Property<String>

        @get:Input
        @get:Optional
        abstract val iconUrl: Property<String>

        @get:Input
        @get:Optional
        abstract val proxyIconUrl: Property<String>
    }

    abstract class EmbedFieldSpec {
        @get:Input
        abstract val name: Property<String>

        @get:Input
        abstract val value: Property<String>

        @get:Input
        @get:Optional
        abstract val inline: Property<Boolean>
    }
}
