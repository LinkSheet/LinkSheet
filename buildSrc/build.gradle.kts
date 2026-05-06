import fe.build.dependencies.Grrfe

plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.squareup:javapoet:_")
    implementation(Square.kotlinPoet)
    implementation("com.google.code.gson:gson:_")
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
