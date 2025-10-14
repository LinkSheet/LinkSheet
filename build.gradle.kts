import fe.build.dependencies.Grrfe
import fe.build.dependencies.LinkSheet
import fe.build.dependencies._1fexd

plugins {
    kotlin("android") apply false
    kotlin("plugin.compose") apply false
    kotlin("plugin.serialization") apply false
    id("com.android.application") apply false
    id("net.nemerosa.versioning") apply false
    id("androidx.room") apply false
    id("com.google.devtools.ksp") apply false
    id("com.gitlab.grrfe.new-build-logic-plugin") apply false
}

subprojects {
    afterEvaluate {
        dependencies {
            configurations.findByName("implementation")?.let { implementation ->
                implementation(platform(LinkSheet.flavors.bom))
                implementation(platform(Grrfe.std.bom))
                implementation(platform(Grrfe.httpkt.bom))
                implementation(platform(Grrfe.gsonExt.bom))
                implementation(platform(_1fexd.composeKit.bom))
            }
        }
    }
}
