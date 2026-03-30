package fuzs.multiloader.mixin

import kotlinx.serialization.json.Json
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

abstract class MixinConfigJsonTask : DefaultTask() {
    @get:Inject
    abstract val objects: ObjectFactory

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Nested
    abstract val json: Property<MixinConfigJsonSpec>

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    init {
        json.convention(objects.newInstance(MixinConfigJsonSpec::class.java))
    }

    @TaskAction
    fun generateJson() {
        workerExecutor.noIsolation().submit(GenerateJsonWorkAction::class.java) {
            jsonProvider.set(json)
            outputFileProvider.set(outputFile)
        }
    }

    fun json(action: Action<MixinConfigJsonSpec>) {
        action.execute(json.get())
    }
}

abstract class GenerateJsonWorkAction : WorkAction<GenerateJsonParameters> {
    override fun execute() {
        val json = generate(parameters.jsonProvider.get())
        parameters.outputFileProvider.get().asFile.writeText(json)
    }

    private fun generate(spec: MixinConfigJsonSpec): String {
        val input = MixinConfigJson(
            parent = spec.parent.orNull,
            target = spec.target.orNull,
            minVersion = spec.minVersion.orNull,
            requiredFeatures = spec.requiredFeatures.orNull?.takeIf { it.isNotEmpty() },
            compatibilityLevel = spec.compatibilityLevel.orNull,
            required = spec.required.orNull,
            priority = spec.priority.orNull,
            mixinPriority = spec.mixinPriority.orNull,
            mixinPackage = spec.mixinPackage.get(),
            mixins = spec.mixins.orNull?.takeIf { it.isNotEmpty() }?.sorted(),
            client = spec.client.orNull?.takeIf { it.isNotEmpty() }?.sorted(),
            server = spec.server.orNull?.takeIf { it.isNotEmpty() }?.sorted(),
            setSourceFile = spec.setSourceFile.orNull,
            refmap = spec.refmap.orNull,
            refmapWrapper = spec.refmapWrapper.orNull,
            verbose = spec.verbose.orNull,
            plugin = spec.plugin.orNull,
            injectors = spec.injectors.orNull?.let {
                InjectorsEntry(
                    defaultRequire = it.defaultRequire.orNull,
                    defaultGroup = it.defaultGroup.orNull,
                    namespace = it.namespace.orNull,
                    injectionPoints = it.injectionPoints.orNull?.takeIf { it.isNotEmpty() },
                    dynamicSelectors = it.dynamicSelectors.orNull?.takeIf { it.isNotEmpty() },
                    maxShiftBy = it.maxShiftBy.orNull
                )
            },
            overwrites = spec.overwrites.orNull?.let {
                OverwritesEntry(
                    conformVisibility = it.conformVisibility.orNull,
                    requireAnnotations = it.requireAnnotations.orNull
                )
            },
            mixinextras = spec.mixinExtras.orNull?.let {
                MixinExtrasEntry(
                    minVersion = it.minVersion.orNull
                )
            }
        )

        val json = Json {
            prettyPrint = true
            explicitNulls = false
        }

        return json.encodeToString(input)
    }
}

interface GenerateJsonParameters : WorkParameters {
    val jsonProvider: Property<MixinConfigJsonSpec>
    val outputFileProvider: RegularFileProperty
}
