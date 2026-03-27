package fuzs.multiloader.classtweaker

import fuzs.multiloader.extension.commonProject
import fuzs.multiloader.extension.mod
import org.gradle.api.Project
import java.io.File

val TRANSITIVE_CLASS_TWEAKER_ACCESS_LEVELS: Map<String, String> = mapOf(
    "transitive-accessible" to "public",
    "transitive-extendable" to "public-f",
    "transitive-mutable" to "public-f"
)
val CLASS_TWEAKER_ACCESS_LEVELS: Map<String, String> = mapOf(
    "accessible" to "public",
    "extendable" to "public-f",
    "mutable" to "public-f"
) + TRANSITIVE_CLASS_TWEAKER_ACCESS_LEVELS
private const val COLUMN_SEPARATOR: String = " "
private val CLASS_TWEAKER_HEADER: String = listOf("classTweaker", "v1", "official").joinToString(COLUMN_SEPARATOR)

val Project.classTweakerFile: File
    get() = project.commonProject.file("src/main/resources/${mod.id}.classtweaker")

val Project.generatedClassTweakerFile: File
    get() = project.layout.buildDirectory.file("generated/resources/${mod.id}.classtweaker").get().asFile

val Project.generatedAccessTransformerFile: File
    get() = project.layout.buildDirectory.file("generated/resources/META-INF/accesstransformer.cfg").get().asFile

val Project.generatedTransitiveAccessTransformerFile: File
    get() = project.layout.buildDirectory.file("generated/resources/META-INF/transitive-accesstransformer.cfg").get().asFile

fun generateClassTweakerFile(inputFile: File, outputFile: File) {
    val lines = inputFile.readLines()
        .asSequence()
        .map { line ->
            val index = line.indexOf('#')
            if (index != -1) line.substring(0, index) else line
        }
        .map(String::trim)
        .filter(String::isNotEmpty)
        .filter { line ->
            CLASS_TWEAKER_ACCESS_LEVELS.keys.any(line::startsWith)
        }
        .map { line ->
            line.replace(Regex("\\s+"), COLUMN_SEPARATOR)
        }
        .toMutableList().also { lines ->
            lines.addFirst(CLASS_TWEAKER_HEADER)
        }

    outputFile.parentFile.mkdirs()
    outputFile.writeText(lines.joinToString("\n"))
}

fun generateAccessTransformerFile(
    inputFile: File,
    outputFile: File,
    accessLevels: Map<String, String> = CLASS_TWEAKER_ACCESS_LEVELS
) {
    val lines = inputFile.readLines()
        .asSequence()
        .map { line ->
            val index = line.indexOf('#')
            if (index != -1) line.substring(0, index) else line
        }
        .map(String::trim)
        .filter(String::isNotEmpty)
        .filter { line ->
            accessLevels.keys.any(line::startsWith)
        }
        .map { line ->
            val entry = line.split(Regex("\\s+"))

            if (entry.size < 3) error("Invalid entry: $line")

            val access = entry[0]
            val type = entry[1]
            val owner = entry[2].replace('/', '.')

            val modifier = accessLevels[access] ?: error("Invalid entry: $line")

            when (type) {
                "class" -> {
                    check(entry.size == 3) { "Invalid entry: $line" }
                    listOf(modifier, owner)
                }

                "field" -> {
                    check(entry.size == 5) { "Invalid entry: $line" }
                    val name = entry[3]
                    listOf(modifier, owner, name)
                }

                "method" -> {
                    check(entry.size == 5) { "Invalid entry: $line" }
                    val name = entry[3]
                    val desc = entry[4]
                    listOf(modifier, owner, "$name$desc")
                }

                else -> {
                    error("Invalid entry: $line")
                }
            }
        }
        .map { lines ->
            lines.joinToString(COLUMN_SEPARATOR)
        }
        .toList()

    outputFile.parentFile.mkdirs()
    outputFile.writeText(lines.joinToString("\n"))
}
