package fe.buildlogic.extension

import net.nemerosa.versioning.VersionInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class AndroidVersion(
    val name: String,
    val code: Int,
    val commit: String,
    val branch: String,
)

val VersionInfo.safeTag: String?
    get() = tag

fun AndroidVersionStrategy(now: Long): VersionStrategy<AndroidVersion> {
    return func@{ info ->
        val name = runCatching { info.safeTag ?: info.full }.getOrDefault("0.0.0")
        val code = getVersionCode(info, now)
        AndroidVersion(name, code, info.commit, info.branch)
    }
}

private fun getVersionCode(info: VersionInfo, now: Long): Int {
    return info.safeTag?.let { handleNightlyTag(it) ?: info.versionNumber.versionCode } ?: (now / 1000).toInt()
}

val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
val nightlyTagRegex = Regex("^nightly\\/(\\d{4})(\\d{2})(\\d{2})(\\d{2})$")

fun handleNightlyTag(tag: String): Int? {
    val match = nightlyTagRegex.matchEntire(tag)?.groupValues ?: return null

    val (_, year, month, day, buildNum) = match
    val date = LocalDate.of(year.toInt(), month.toInt(), day.toInt())

    return date.format(dtf).toIntOrNull()
}
