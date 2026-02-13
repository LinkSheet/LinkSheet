import fe.build.dependencies.Grrfe
import fe.buildsrc.mimetypes.MimeType
import fe.buildsrc.mimetypes.UpdateMimeTypesTask

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

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation(Grrfe.std.test)
}

val generatedSrcDir: File = layout.buildDirectory.dir("generated/sources/apache/main/kotlin").get().asFile

val main by sourceSets
main.java.srcDir(generatedSrcDir)

val generateMimeTypes = tasks.register<UpdateMimeTypesTask>("generateMimeTypes") {
    group = "build"
    packageName = "fe.linksheet.mimetype"
    baseDir = generatedSrcDir
    customMimeTypes.set(
        listOf(
            MimeType(
                "application/x-gtar", listOf(
                    "tar.gz", "tgz", "tar.Z", "tar.bz2", "tbz2", "tar.lz", "tlz", "tar.xz", "txz", "tar.zst"
                )
            )
        )
    )
}

val assemble by tasks
assemble.dependsOn(generateMimeTypes)
