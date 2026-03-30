package fuzs.multiloader.discord

import kotlinx.serialization.json.Json
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.inject.Inject

abstract class DiscordWebhookTask : DefaultTask() {
    @get:Inject
    abstract val objects: ObjectFactory

    @get:Nested
    abstract val payload: Property<DiscordWebhookPayloadSpec>

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    init {
        payload.convention(objects.newInstance(DiscordWebhookPayloadSpec::class.java))
    }

    @TaskAction
    fun sendWebhook() {
        workerExecutor.noIsolation().submit(SendWebhookWorkAction::class.java) {
            payloadProvider.set(payload)
        }
    }

    fun payload(action: Action<DiscordWebhookPayloadSpec>) {
        action.execute(payload.get())
    }
}

abstract class SendWebhookWorkAction : WorkAction<SendWebhookParameters> {
    override fun execute() {
        val spec = parameters.payloadProvider.get()
        val payload = generate(spec)

        if (payload.content == null && payload.embeds == null && payload.flags == null) {
            throw GradleException("Discord webhook payload is empty")
        }

        if (spec.debug.orNull == true) {
            val debugJson = Json {
                prettyPrint = true
                explicitNulls = false
            }

            println("Discord payload (debug mode):")
            println(debugJson.encodeToString<DiscordWebhookPayload>(payload))
            return
        }

        val json = Json { explicitNulls = false }
        val jsonString = json.encodeToString(payload)
        val url = "https://discord.com/api/webhooks/${spec.channel.get()}/${spec.token.get()}"

        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonString))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        println("Discord response: ${response.statusCode()}")

        if (response.statusCode() >= 400) {
            println(response.body())
        }
    }

    private fun generate(spec: DiscordWebhookPayloadSpec): DiscordWebhookPayload {
        return DiscordWebhookPayload(
            content = spec.content.orNull?.takeIf { it.isNotBlank() },
            embeds = spec.embeds.orNull?.takeIf { it.isNotEmpty() }?.map { embed ->
                EmbedEntry(
                    title = embed.title.orNull,
                    description = embed.description.orNull,
                    url = embed.url.orNull,
                    timestamp = embed.timestamp.orNull,
                    color = embed.color.orNull,
                    footer = embed.footer.orNull?.let {
                        FooterEntry(
                            text = it.text.get(),
                            iconUrl = it.iconUrl.orNull,
                            proxyIconUrl = it.proxyIconUrl.orNull
                        )
                    },
                    image = embed.image.orNull?.let {
                        ImageEntry(
                            url = it.url.get(),
                            proxyUrl = it.proxyUrl.orNull,
                            height = it.height.orNull,
                            width = it.width.orNull
                        )
                    },
                    thumbnail = embed.thumbnail.orNull?.let {
                        ThumbnailEntry(
                            url = it.url.get(),
                            proxyUrl = it.proxyUrl.orNull,
                            height = it.height.orNull,
                            width = it.width.orNull
                        )
                    },
                    video = embed.video.orNull?.let {
                        VideoEntry(
                            url = it.url.orNull,
                            proxyUrl = it.proxyUrl.orNull,
                            height = it.height.orNull,
                            width = it.width.orNull
                        )
                    },
                    provider = embed.provider.orNull?.let {
                        ProviderEntry(
                            name = it.name.orNull,
                            url = it.url.get()
                        )
                    },
                    author = embed.author.orNull?.let {
                        AuthorEntry(
                            name = it.name.get(),
                            url = it.url.orNull,
                            iconUrl = it.iconUrl.orNull,
                            proxyIconUrl = it.proxyIconUrl.orNull
                        )
                    },
                    fields = embed.fields.orNull?.map { field ->
                        FieldEntry(
                            name = field.name.get(),
                            value = field.value.get(),
                            inline = field.inline.orNull
                        )
                    }
                )
            },
            flags = spec.flags.orNull?.takeIf { it != 0 }
        )
    }
}

interface SendWebhookParameters : WorkParameters {
    val payloadProvider: Property<DiscordWebhookPayloadSpec>
}
