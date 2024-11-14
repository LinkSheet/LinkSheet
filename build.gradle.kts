plugins {
    kotlin("android") apply false
    kotlin("plugin.compose") apply false
    kotlin("plugin.serialization") apply false
    id("com.android.application") apply false
    id("net.nemerosa.versioning") apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
}
