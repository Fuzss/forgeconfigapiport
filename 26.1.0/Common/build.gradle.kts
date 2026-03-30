plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    compileOnlyApi(sharedLibs.nightconfigcore.common)
    compileOnlyApi(sharedLibs.nightconfigtoml.common)
}
