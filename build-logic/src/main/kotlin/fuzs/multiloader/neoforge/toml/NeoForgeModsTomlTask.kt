package fuzs.multiloader.neoforge.toml

import com.akuleshov7.ktoml.Toml
import kotlinx.serialization.encodeToString
import kotlinx.serialization.modules.SerializersModule
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

@CacheableTask
abstract class NeoForgeModsTomlTask : DefaultTask() {
    @get:Inject
    abstract val objects: ObjectFactory

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Nested
    abstract val toml: Property<NeoForgeModsTomlSpec>

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    init {
        toml.convention(objects.newInstance(NeoForgeModsTomlSpec::class.java))
    }

    @TaskAction
    fun generateToml() {
        workerExecutor.noIsolation().submit(GenerateTomlWorkAction::class.java) {
            tomlProvider.set(toml)
            outputFileProvider.set(outputFile)
        }
    }

    fun toml(action: Action<NeoForgeModsTomlSpec>) {
        action.execute(toml.get())
    }
}

abstract class GenerateTomlWorkAction : WorkAction<GenerateTomlParameters> {
    override fun execute() {
        val toml = generate(parameters.tomlProvider.get())
        parameters.outputFileProvider.get().asFile.writeText(toml)
    }

    private fun generate(spec: NeoForgeModsTomlSpec): String {
        val input = NeoForgeModsToml(
            spec.modLoader.orNull,
            spec.loaderVersion.orNull,
            spec.license.get(),
            spec.showAsResourcePack.orNull,
            spec.showAsDataPack.orNull,
            spec.services.orNull?.takeIf { it.isNotEmpty() },
            spec.properties.orNull?.takeIf { it.isNotEmpty() },
            spec.issueTrackerURL.orNull,
            spec.mods.orNull?.map {
                ModEntry(
                    it.modId.get(),
                    it.namespace.orNull,
                    it.version.orNull,
                    it.displayName.orNull,
                    it.description.orNull,
                    it.logoFile.orNull,
                    it.logoBlur.orNull,
                    it.updateJSONURL.orNull,
                    it.modUrl.orNull,
                    it.credits.orNull,
                    it.authors.orNull,
                    it.displayURL.orNull,
                    it.enumExtensions.orNull,
                    it.featureFlags.orNull
                )
            },
            spec.features.orNull?.takeIf { it.isNotEmpty() }
                ?.map { it.modId.get() to it.properties.get() }
                ?.fold(mutableMapOf<String, MutableMap<String, Any>>()) { accumulator, (key, value) ->
                    accumulator.merge(key, value.toMutableMap()) { old, new ->
                        old.apply { putAll(new) }
                    }
                    accumulator
                },
            spec.modProperties.orNull?.takeIf { it.isNotEmpty() }
                ?.map { it.modId.get() to it.properties.get() }
                ?.fold(mutableMapOf<String, MutableMap<String, Any>>()) { accumulator, (key, value) ->
                    accumulator.merge(key, value.toMutableMap()) { old, new ->
                        old.apply { putAll(new) }
                    }
                    accumulator
                },
            spec.accessTransformers.orNull?.takeIf { it.isNotEmpty() }?.map { AccessTransformerEntry(it.file.get()) },
            spec.mixins.orNull?.takeIf { it.isNotEmpty() }
                ?.map {
                    MixinEntry(
                        it.config.get(),
                        it.requiredMods.orNull?.takeIf { it.isNotEmpty() },
                        it.behaviorVersion.orNull
                    )
                },
            spec.dependencies.orNull?.takeIf { it.isNotEmpty() }?.map {
                val properties = it.properties.get()
                it.modId.get() to DependencyEntry(
                    properties.modId.get(),
                    properties.type.orNull?.let { it == NeoForgeModsTomlSpec.DependencySpec.Type.REQUIRED } ?: true,
                    properties.type.orNull,
                    properties.reason.orNull,
                    properties.versionRange.orNull,
                    properties.ordering.orNull,
                    properties.side.orNull,
                    properties.referralUrl.orNull
                )
            }
                ?.groupBy({ it.first }, { it.second }),
            spec.extraProperties.orNull?.takeIf { it.isNotEmpty() }
                ?.map { it.property.get() to it.properties.get() }
                ?.fold(mutableMapOf<String, MutableMap<String, Any>>()) { accumulator, (key, value) ->
                    accumulator.merge(key, value.toMutableMap()) { old, new ->
                        old.apply { putAll(new) }
                    }
                    accumulator
                },
            spec.extraArrayProperties.orNull?.takeIf { it.isNotEmpty() }
                ?.map { it.property.get() to it.properties.get() }
                ?.groupBy({ it.first }, { it.second })
        )

        val toml = Toml(serializersModule = SerializersModule {
            contextual(Any::class, AnyPrimitiveSerializer)
        })

        return buildString {
            appendLine(
                toml.encodeToString(
                    NeoForgeModsToml.serializer(),
                    input
                )
            )
            input.extraProperties?.let {
                appendLine()
                appendLine(toml.encodeToString(it))
            }
            input.extraArrayProperties?.let {
                appendLine()
                appendLine(toml.encodeToString(it))
            }
        }
    }
}

interface GenerateTomlParameters : WorkParameters {
    val tomlProvider: Property<NeoForgeModsTomlSpec>
    val outputFileProvider: RegularFileProperty
}
