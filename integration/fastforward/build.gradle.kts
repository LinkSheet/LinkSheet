import fe.build.dependencies.Grrfe

plugins {
    kotlin("jvm")
    id("com.gitlab.grrfe.android-build-plugin")
}

dependencies {
    api(Grrfe.std.result.core)
    api(Grrfe.std.uri)

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation(Grrfe.std.test)
}
