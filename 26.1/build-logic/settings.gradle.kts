dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/plugins.versions.toml"))
        }
    }
}

rootProject.name = providers.gradleProperty("mod.name").get().replace(Regex("\\W"), "")
