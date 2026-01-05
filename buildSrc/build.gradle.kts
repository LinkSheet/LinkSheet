import fe.build.dependencies.Grrfe

plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("net.nemerosa.versioning:net.nemerosa.versioning.gradle.plugin:3.1.0")
    implementation(platform(Grrfe.std.bom))
    implementation(Grrfe.std.extension.dom)

    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation(kotlin("test"))
}

kotlin {
    compilerOptions {
//        languageVersion.set(KotlinVersion.KOTLIN_2_3)
//        apiVersion.set(KotlinVersion.KOTLIN_2_3)
    }
}
