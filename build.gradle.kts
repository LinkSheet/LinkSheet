
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.TestExtension
import com.gitlab.grrfe.gradlebuild.android.extension.configurePickFirsts
import fe.build.dependencies.Grrfe
import fe.build.dependencies.LinkSheet
import fe.build.dependencies._1fexd

plugins {
    kotlin("plugin.compose") apply false
    kotlin("plugin.serialization") apply false
    id("com.android.application") apply false
    id("androidx.room3") apply false
    id("com.google.devtools.ksp") apply false
    id("com.gitlab.grrfe.android-build-plugin") apply false
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.android.application")) {
            extensions.findByType<ApplicationExtension>()?.packaging?.configurePickFirsts()
        }
        if (plugins.hasPlugin("com.android.library")) {
            extensions.findByType<LibraryExtension>()?.packaging?.configurePickFirsts()
        }
        if (plugins.hasPlugin("com.android.test")) {
            extensions.findByType<TestExtension>()?.packaging?.configurePickFirsts()
        }

        // Via https://github.com/tyejaedon/TigerPlayer/pull/114:

        // Robolectric's FileDescriptorInterceptor reflectively reaches into the JDK-internal
        // jdk.internal.access.SharedSecrets class while shimming android.os.SharedMemory on JDK 17+.
        // That package isn't exported to unnamed modules by default, so every Robolectric test fails
        // during environment setup with "Failed to interact with raw FileDescriptor internals" unless
        // the test JVM explicitly opens it up. See issue #113 and robolectric/robolectric#11434.
        tasks.withType<Test>().configureEach {
            jvmArgs(
                "--add-exports=java.base/jdk.internal.access=ALL-UNNAMED",
                "--add-opens=java.base/java.io=ALL-UNNAMED",
                "--add-opens=java.base/java.lang=ALL-UNNAMED",
            )
        }

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
