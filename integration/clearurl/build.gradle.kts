import fe.build.dependencies.Grrfe
import fe.buildsrc.clearurls.MetadataGeneratorTask
import fe.buildsrc.clearurls.UpdateRulesTask

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.power-assert") version "2.4.21"
    id("com.gitlab.grrfe.android-build-plugin")
    id("de.infix.testBalloon") version "1.1.0"
    id("io.github.yairm210.purity-plugin") version "1.11.2"
}

kotlin {
    explicitApi()
}

dependencies {
    api(Grrfe.gsonExt.core)

    api(Grrfe.std.result.core)
    api(Grrfe.std.uri)
    api(Grrfe.std.extension.kotlinxSerialization)
    api(Grrfe.signify)
    implementation(KotlinX.serialization.core)
    implementation(KotlinX.serialization.json)
    implementation(KotlinX.serialization.protobuf)
    implementation(KotlinX.serialization.cbor)
    implementation("io.github.yairm210:purity-annotations:1.11.2")

    testImplementation(Grrfe.std.reflect)
    testImplementation("de.infix.testBalloon:testBalloon-framework-core:_")
    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation(Grrfe.std.test)
}


val generatedSrcDir: File = layout.buildDirectory.dir("generated/sources/metadata/main/java").get().asFile

val main by sourceSets
main.java.srcDir(generatedSrcDir)

val generateMetadata = tasks.register<MetadataGeneratorTask>("generateMetadata") {
    group = "build"
    dir = generatedSrcDir
}

val assemble by tasks
assemble.dependsOn(generateMetadata)

val updateRules = tasks.register<UpdateRulesTask>("updateRules") {
    file = "src/main/resources/fe/clearurlskt/clearurls.json"
    rawUrl.set("https://raw.githubusercontent.com/LinkSheet/ClearURLs-Rules/master/data.min.json")
}
