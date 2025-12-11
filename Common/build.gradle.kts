plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    compileOnlyApi(libs.nightconfigcore.common)
    compileOnlyApi(libs.nightconfigtoml.common)
}
