package app.linksheet.feature.shizuku.service

import java.security.MessageDigest
import rikka.shizuku.ShizukuProvider

internal const val ORIGINAL_MANAGER_PACKAGE = ShizukuProvider.MANAGER_APPLICATION_ID
internal const val MANAGER_ACTIVITY_CLASS = "moe.shizuku.manager.MainActivity"

/** A manager picked by the user: its package and the SHA-256 (lowercase hex) of its signing certificate. */
data class ManagerSelection(val packageName: String, val certSha256: String)

internal sealed interface ManagerCheck {
    /** Nothing stored, original package installed. */
    data object Default : ManagerCheck
    /** Stored package installed and its certificate matches the stored fingerprint. */
    data class Verified(val packageName: String) : ManagerCheck
    data object NotInstalled : ManagerCheck
    data object CertMismatch : ManagerCheck
}

internal val ManagerCheck.isUsable: Boolean
    get() = this is ManagerCheck.Default || this is ManagerCheck.Verified

/** Blank package -> no selection. Package without fingerprint -> selection with an empty fingerprint (fails closed). */
internal fun readManagerSelection(packageName: String?, certSha256: String?): ManagerSelection? {
    if (packageName.isNullOrBlank()) return null
    return ManagerSelection(packageName, certSha256.orEmpty())
}

internal fun checkManager(
    selection: ManagerSelection?,
    isInstalled: (String) -> Boolean,
    currentCertSha256: (String) -> String?,
): ManagerCheck {
    val packageName = selection?.packageName ?: ORIGINAL_MANAGER_PACKAGE
    if (!isInstalled(packageName)) return ManagerCheck.NotInstalled
    if (selection == null) return ManagerCheck.Default

    val current = currentCertSha256(packageName)
    return if (selection.certSha256.isNotEmpty() && current != null && current == selection.certSha256) {
        ManagerCheck.Verified(packageName)
    } else {
        ManagerCheck.CertMismatch
    }
}

internal fun sha256Hex(bytes: ByteArray): String {
    return MessageDigest.getInstance("SHA-256").digest(bytes)
        .joinToString("") { "%02x".format(it.toInt() and 0xFF) }
}
