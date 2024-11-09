package fe.buildlogic.extension


data class AndroidVersion(
    val name: String,
    val code: Int,
    val commit: String,
    val branch: String,
)

fun AndroidVersionStrategy(now: Long): VersionStrategy<AndroidVersion> {
    return { info ->
        val name = runCatching { info.tag ?: info.full }.getOrDefault("0.0.0")
        val code = info.tag?.let { info.versionNumber.versionCode } ?: (now / 1000).toInt()

        AndroidVersion(name, code, info.commit, info.branch)
    }
}
