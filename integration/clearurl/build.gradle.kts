import fe.build.dependencies.Grrfe
import fe.buildsrc.clearurls.MetadataGeneratorTask
import fe.buildsrc.clearurls.UpdateRulesTask

plugins {
    kotlin("jvm")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

kotlin {
    explicitApi()
}

dependencies {
    api(Grrfe.gsonExt.core)

    api(Grrfe.std.result.core)
    api(Grrfe.std.uri)
    api(Grrfe.signify)

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
}
