import korlibs.korge.gradle.*

plugins {
    alias(libs.plugins.korge)
}

korge {
    id = "com.devex.stratego"
    name = "Stratego"
    androidMinSdk = 18
    androidCompileSdk = 33
    androidTargetSdk = 33

// To enable all targets at once

    //targetAll()

// To enable targets based on properties/environment variables
    //targetDefault()

// To selectively enable targets

    targetJvm()
    targetDesktop()
    serializationJson()
    targetAndroid()
}

tasks.withType<Test>(){
    useJUnitPlatform()
}

dependencies {
    add("commonMainApi", project(":deps"))
    //add("commonMainApi", project(":korge-dragonbones"))
}
