package fuzs.multiloader.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.util.*

@DisableCachingByDefault(
    because = "Output depends on previous file state and increments on each execution"
)
abstract class IncrementBuildNumber : DefaultTask() {
    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun run() {
        val file = inputFile.asFile.orNull
        val properties = Properties().apply { if (file?.exists() == true) load(file.inputStream()) }
        val uniqueBuildNumber = properties.getProperty("project.build")?.toIntOrNull() ?: 1
        properties.setProperty("project.build", (uniqueBuildNumber + 1).toString())
        outputFile.asFile.get().outputStream().use { properties.store(it, null) }
    }
}
