package fuzs.multiloader.discord

import mod
import org.gradle.api.Project
import versionCatalog
import java.io.File
import java.io.FileNotFoundException

val Project.changelogVersion: String
    get() = "v${project.mod.version}-${project.versionCatalog.findVersion("minecraft").get()}"

fun verifyChangelogVersion(changelogFile: File, versionString: String) {
    if (!changelogFile.canRead()) {
        throw FileNotFoundException("Could not read changelog file")
    }

    if (!changelogFile.readText().contains(versionString)) {
        throw IllegalStateException("Missing changelog version: $versionString")
    }
}

fun parseChangelogFields(changelogFile: File): Map<String, String> {
    val changelogText = changelogFile.readText()
    // Extract the most recent section (everything until next "## " or EOF)
    val sectionRegex = Regex("""## \[.*?] - \d{4}-\d{2}-\d{2}\r?\n(?s)(.*?)(?=\r?\n## |$)""")
    val latestSection = sectionRegex.find(changelogText)?.groups?.get(1)?.value?.trim() ?: ""
    val changelogSections = mutableMapOf<String, String>()
    val subsectionRegex = Regex("""### (.*?)\r?\n(?s)(.*?)(?=\r?\n### |$)""")

    for (section in subsectionRegex.findAll(latestSection)) {
        val title = section.groups[1]?.value?.trim() ?: ""
        val body = section.groups[2]?.value?.trim() ?: ""
        val emoji = ChangelogSectionType.emojiByName(title)
        val formattedBody = body.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .fold(mutableListOf<String>()) { accumulator, line ->
                if (line.startsWith("- ")) {
                    accumulator.add(line.replaceFirst("- ", "\u2022 "))
                } else if (accumulator.isNotEmpty()) {
                    accumulator[accumulator.lastIndex] = accumulator.last() + " " + line
                } else {
                    accumulator.add(line)
                }
                accumulator
            }
            .joinToString("\n")
        changelogSections += "$emoji $title" to formattedBody
    }

    return changelogSections
}
