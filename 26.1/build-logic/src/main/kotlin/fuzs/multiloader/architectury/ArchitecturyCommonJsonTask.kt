package fuzs.multiloader.architectury

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

abstract class ArchitecturyCommonJsonTask : DefaultTask() {
    @get:Inject
    abstract val objects: ObjectFactory

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Nested
    abstract val json: Property<ArchitecturyCommonJsonSpec>

    @get:Inject
    abstract val workerExecutor: WorkerExecutor

    init {
        json.convention(objects.newInstance(ArchitecturyCommonJsonSpec::class.java))
    }

    @TaskAction
    fun generateJson() {
        workerExecutor.noIsolation().submit(GenerateJsonWorkAction::class.java) {
            jsonProvider.set(json)
            outputFileProvider.set(outputFile)
        }
    }

    fun json(action: Action<ArchitecturyCommonJsonSpec>) {
        action.execute(json.get())
    }
}

abstract class GenerateJsonWorkAction : WorkAction<GenerateJsonParameters> {
    override fun execute() {
        val json = generate(parameters.jsonProvider.get())
        parameters.outputFileProvider.get().asFile.writeText(json)
    }

    private fun generate(spec: ArchitecturyCommonJsonSpec): String {
        val input = ArchitecturyCommonJson(
            spec.accessWidener.orNull, spec.injectedInterfaces.orNull?.takeIf { it.isNotEmpty() }
                ?.fold(mutableMapOf<String, MutableList<String>>()) { accumulator, item ->
                    accumulator.merge(item.clazz.get(), item.injectedInterfaces.get().toMutableList()) { old, new ->
                        old.apply { addAll(new) }
                    }
                    accumulator
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
    val jsonProvider: Property<ArchitecturyCommonJsonSpec>
    val outputFileProvider: RegularFileProperty
}
