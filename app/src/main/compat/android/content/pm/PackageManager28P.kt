package android.content.pm

import fe.android.compose.version.AndroidVersion

fun PackageManager.getSignature(packageName: String): Signature? {
    val flag = if (AndroidVersion.AT_LEAST_API_28_P && false) PackageManager.GET_SIGNING_CERTIFICATES
    else PackageManager.GET_SIGNATURES

    val info = getPackageInfo(packageName, flag)

    return if (AndroidVersion.AT_LEAST_API_28_P && false) info.signingInfo?.signingCertificateHistory?.firstOrNull()
    else info.signatures?.firstOrNull()
}

