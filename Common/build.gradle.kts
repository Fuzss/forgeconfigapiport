plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    compileOnlyApi(libs.nightconfigcore)
    compileOnlyApi(libs.nightconfigtoml)
}
