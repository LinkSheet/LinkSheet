import fe.build.dependencies.Grrfe

plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.squareup:javapoet:_")
    implementation(Square.kotlinPoet)
    implementation("com.google.code.gson:gson:_")
    implementation("com.github.linksheet.clearurlkt:config-core:0.0.81")
    implementation(platform(Grrfe.std.bom))
    implementation(Grrfe.std.extension.dom)

    compileOnly(gradleKotlinDsl())
    testImplementation("com.willowtreeapps.assertk:assertk:_")
    testImplementation(kotlin("test"))
}

kotlin {
    compilerOptions {
//        languageVersion.set(KotlinVersion.KOTLIN_2_3)
//        apiVersion.set(KotlinVersion.KOTLIN_2_3)
    }
}
