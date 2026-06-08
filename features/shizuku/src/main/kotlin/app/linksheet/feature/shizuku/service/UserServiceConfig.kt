package app.linksheet.feature.shizuku.service

data class UserServiceConfig(
    val packageName: String,
    val versionCode: Int,
    val debuggable: Boolean,
    val tag: String,
)
