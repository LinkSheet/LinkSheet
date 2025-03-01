package android.content.pm

import fe.android.version.AndroidVersion

fun PackageManager.getSignature(packageName: String): Signature? {
    val flag = if (AndroidVersion.isAtLeastApi28P() && false) PackageManager.GET_SIGNING_CERTIFICATES
    else PackageManager.GET_SIGNATURES

    val info = getPackageInfo(packageName, flag)

    return if (AndroidVersion.isAtLeastApi28P() && false) info.signingInfo?.signingCertificateHistory?.firstOrNull()
    else info.signatures?.firstOrNull()
}

// TODO: Use hasSigningCertificate on P+
//pm.hasSigningCertificate(packageName, certificate, PackageManager.CERT_INPUT_SHA256)
