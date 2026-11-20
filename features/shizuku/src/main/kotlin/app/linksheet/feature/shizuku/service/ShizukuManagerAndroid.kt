package app.linksheet.feature.shizuku.service

import android.content.pm.PackageManager
import android.content.pm.getSignature
import fe.std.result.getOrNull
import fe.std.result.tryCatch

// getSignature returns the oldest signer of the signing lineage, not the current one
internal fun PackageManager.getCertSha256(packageName: String): String? {
    return tryCatch { getSignature(packageName) }.getOrNull()?.toByteArray()?.let(::sha256Hex)
}
