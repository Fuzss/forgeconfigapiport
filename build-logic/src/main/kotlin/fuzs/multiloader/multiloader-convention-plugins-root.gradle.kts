package fuzs.multiloader

import fuzs.multiloader.discord.*
import fuzs.multiloader.extension.*
import fuzs.multiloader.metadata.LinkProvider
import fuzs.multiloader.metadata.loadMetadata
import fuzs.multiloader.task.IncrementBuildNumber
import kotlinx.serialization.json.Json
import java.time.Instant

afterEvaluate {
    project.layout.buildDirectory.dir("run/server").get().asFile.mkdirs()
    val metadata = loadMetadata()
    val json = Json { prettyPrint = true }
    val output = project.layout.projectDirectory.file("metadata.json").asFile
    output.writeText(json.encodeToString(metadata))
}

tasks.register<IncrementBuildNumber>("incrementBuildNumber") {
    val propertiesFile = layout.buildDirectory.file("build.properties")
    // Check file existence here, as the property requires it when set.
    if (propertiesFile.orNull?.asFile?.exists() == true) {
        inputFile.set(propertiesFile)
    }

    outputFile.set(propertiesFile)
}

tasks.withType<DiscordWebhookTask>().configureEach {
    val versionString = project.changelogVersion
    doFirst {
        verifyChangelogVersion(project.file("CHANGELOG.md"), versionString)
    }
}

tasks.register<DiscordWebhookTask>("sendDiscordWebhook") {
    val discordChannel = providers.gradleProperty("fuzs.multiloader.remote.discord.channel")
    val discordToken = providers.gradleProperty("fuzs.multiloader.remote.discord.token")
    onlyIf { discordChannel.isPresent && discordToken.isPresent }

    val changelogFile = file("CHANGELOG.md")

    payload {
        channel.set(discordChannel.get())
        token.set(discordToken.get())
        val epochSeconds = System.currentTimeMillis() / 1000
        content.set("<t:$epochSeconds:R>")
        flags.set(MessageFlags.SUPPRESS_NOTIFICATIONS)
        debug.set(debugRemoteUploads)

        embed {
            title.set("[$minecraftVersion] ${mod.name} v${mod.version}")
            description.set(mod.description)
            metadata.links.firstOrNull { it.name == LinkProvider.MODRINTH }
                ?.url()
                ?.let { url.set(it) }
            timestamp.set(Instant.now().toString())
            color.set(5814783)

            val footerValues = listOf(mod.name, "v${mod.version}", minecraftVersion)
            footer(footerValues.joinToString(" \u2022 "))
            image("https://raw.githubusercontent.com/Fuzss/modresources/main/pages/data/${mod.id}/banner.png")
            thumbnail("https://raw.githubusercontent.com/Fuzss/modresources/main/pages/data/${mod.id}/logo.png")

            author("Fuzs") {
                url.set("https://modrinth.com/user/Fuzs")
                iconUrl.set("https://raw.githubusercontent.com/Fuzss/modresources/main/pages/commons/avatar.png")
            }

            parseChangelogFields(changelogFile).forEach { field(it.key, it.value) }

            val downloadLinks = metadata.links.mapNotNull {
                when (it.name) {
                    LinkProvider.CURSEFORGE -> "<:CurseForge:893088361634471948> [CurseForge](${it.url()})"
                    LinkProvider.MODRINTH -> "<:modrinth:1176378033578459206> [Modrinth](${it.url()})"
                    else -> null
                }
            }

            if (downloadLinks.isNotEmpty()) {
                field("\uD83D\uDCE5 Downloads", downloadLinks.joinToString("\n")) {
                    inline.set(true)
                }
            }

            metadata.links.firstOrNull { it.name == LinkProvider.GITHUB }
                ?.url()
                ?.let {
                    field(
                        "<:github:1422695832951455814> GitHub",
                        listOf(
                            "\uD83D\uDC68\u200D\uD83D\uDCBB [Source]($it)",
                            "\u26A0\uFE0F [Issues]($it/issues)"
                        ).joinToString("\n")
                    ) {
                        inline.set(true)
                    }
                }

            field("\uD83D\uDCAC Support", "<:Fuzs:993195872131235881> <#917550806922846299>") {
                inline.set(true)
            }
        }
    }
}

if (project.platformProjects.isNotEmpty()) {
    tasks.register("all-build") {
        group = "multiloader/build"
        dependsOn(project.platformProjects.map { it.tasks.named("build") })
    }
}

if (project.subprojects.isNotEmpty()) {
    tasks.register("all-clean") {
        group = "multiloader/build"
        dependsOn(project.subprojects.map { it.tasks.named("clean") })
    }
}

if (project.subprojects.isNotEmpty()) {
    tasks.register("all-publish") {
        group = "multiloader/publish"
        dependsOn(project.subprojects.map { it.tasks.named("publishMavenJavaPublicationToFuzsModResourcesRepository") })
    }
}

if (metadata.links.firstOrNull { it.name == LinkProvider.CURSEFORGE } != null && project.platformProjects.isNotEmpty()) {
    tasks.register("all-curseforge") {
        group = "multiloader/remote"
        dependsOn(project.platformProjects.map { it.tasks.named("publishCurseforge") })
    }
}

if (metadata.links.firstOrNull { it.name == LinkProvider.GITHUB } != null && project.platformProjects.isNotEmpty()) {
    tasks.register("all-github") {
        group = "multiloader/remote"
        dependsOn(project.platformProjects.map { it.tasks.named("publishGithub") })
    }
}

if (metadata.links.firstOrNull { it.name == LinkProvider.MODRINTH } != null && project.platformProjects.isNotEmpty()) {
    tasks.register("all-modrinth") {
        group = "multiloader/remote"
        dependsOn(project.platformProjects.map { it.tasks.named("publishModrinth") })
    }
}

if (metadata.links.isNotEmpty()) {
    tasks.register("all-discord") {
        group = "multiloader/remote"
        dependsOn(tasks.named("sendDiscordWebhook"))
    }
}

if (metadata.links.isNotEmpty()) {
    project.platformProjects.forEach {
        project.tasks.register("${it.name.lowercase()}-all") {
            group = "multiloader/remote"
            dependsOn(it.tasks.named("publishMods"))
        }
    }
}

if (metadata.links.isNotEmpty() && project.platformProjects.isNotEmpty()) {
    tasks.register("all-all") {
        group = "multiloader/remote"
        dependsOn(project.platformProjects.map { it.tasks.named("publishMods") })
        dependsOn(tasks.named("sendDiscordWebhook"))
    }
}

tasks.register("all-java-apply") {
    group = "multiloader/spotless"
    dependsOn(project.subprojects.map { it.tasks.named("spotlessJavaApply") })
}

tasks.register("all-java-check") {
    group = "multiloader/spotless"
    dependsOn(project.subprojects.map { it.tasks.named("spotlessJavaCheck") })
}

tasks.register("all-tinytakeover-apply") {
    group = "multiloader/spotless"
    dependsOn(project.subprojects.map { it.tasks.named("spotlessTinyTakeoverApply") })
}

tasks.register("all-tinytakeover-check") {
    group = "multiloader/spotless"
    dependsOn(project.subprojects.map { it.tasks.named("spotlessTinyTakeoverCheck") })
}

tasks.register("all-mountsofmayhem-apply") {
    group = "multiloader/spotless"
    dependsOn(project.subprojects.map { it.tasks.named("spotlessMountsOfMayhemApply") })
}

tasks.register("all-mountsofmayhem-check") {
    group = "multiloader/spotless"
    dependsOn(project.subprojects.map { it.tasks.named("spotlessMountsOfMayhemCheck") })
}

tasks.register("all-thecopperage-apply") {
    group = "multiloader/spotless"
    dependsOn(project.subprojects.map { it.tasks.named("spotlessTheCopperAgeApply") })
}

tasks.register("all-thecopperage-check") {
    group = "multiloader/spotless"
    dependsOn(project.subprojects.map { it.tasks.named("spotlessTheCopperAgeCheck") })
}
