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
