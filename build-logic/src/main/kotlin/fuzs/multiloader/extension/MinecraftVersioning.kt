package fuzs.multiloader.extension

import org.gradle.api.Project

fun baseVersion(version: String, skipPatchComponent: Boolean = false): MutableList<String> {
    val parts = version.split(".").toMutableList()
    require(parts.size >= 2) { "Version must have at least MAJOR.MINOR" }

    parts.add("0")
    parts.subList(if (skipPatchComponent) 2 else 3, parts.size).clear()
    return parts
}

fun Project.artifactVersion(version: String): String {
    if (strictVersioning(version)) {
        return version
    } else {
        val parts = baseVersion(version, true)
        parts.add("x")
        return parts.joinToString(".")
    }
}

fun supportedVersions(version: String): List<String> {
    val parts: List<String> = baseVersion(version)
    val versions = mutableListOf<MutableList<String>>()
    for (patch in 0..parts.last().toInt()) {
        versions.add(parts.toMutableList().also { parts -> parts[parts.lastIndex] = patch.toString() })
    }

    return versions.map { parts ->
        if (parts.last() == "0") parts.removeLast()
        parts.joinToString(".")
    }
}

fun Project.lowerBoundVersion(version: String): String {
    if (strictVersioning(version)) {
        return version
    } else {
        val parts = baseVersion(version, true)
        return parts.joinToString(".")
    }
}

fun Project.upperBoundVersion(version: String): String {
    val parts = baseVersion(version, !strictVersioning(version))
    parts[parts.lastIndex] = (parts.last().toInt() + 1).toString()
    return parts.joinToString(".")
}
