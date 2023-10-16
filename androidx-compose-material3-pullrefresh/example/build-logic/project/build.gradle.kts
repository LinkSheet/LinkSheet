plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(com.diffplug.spotless)
    implementation(gradmGeneratedJar)
    implementation(me.omico.consensus.api)
    implementation(me.omico.consensus.dsl)
    implementation(me.omico.consensus.git)
    implementation(me.omico.consensus.spotless)
}
