import fe.build.dependencies.Grrfe

plugins {
    kotlin("jvm")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation("org.jetbrains:annotations:_")

    api(Grrfe.gsonExt.core)
    api(Grrfe.std.result.core)
    api(Grrfe.std.uri)
    api(Grrfe.signify)

    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation(Grrfe.std.test)
}
