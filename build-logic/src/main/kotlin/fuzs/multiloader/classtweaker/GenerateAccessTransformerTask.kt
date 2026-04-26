package fuzs.multiloader.classtweaker

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateAccessTransformerTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @get:Input
    abstract val accessLevels: MapProperty<String, String>

    init {
        accessLevels.convention(CLASS_TWEAKER_ACCESS_LEVELS)
    }

    @TaskAction
    fun run() {
        generateAccessTransformerFile(
            inputFile.get().asFile,
            outputFile.get().asFile,
            accessLevels.get()
        )
    }
}
