import fe.build.dependencies.Grrfe

plugins {
    kotlin("jvm")
    id("com.gitlab.grrfe.new-build-logic-plugin")
}

kotlin {
    explicitApi()
}

dependencies {
    api("org.jsoup:jsoup:_")

    testImplementation(Grrfe.httpkt.core)
    testImplementation(kotlin("test"))
    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation(Grrfe.std.test)
}

