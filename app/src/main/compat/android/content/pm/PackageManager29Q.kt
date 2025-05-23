package android.content.pm

import fe.composekit.core.AndroidVersion

fun PackageManager.getInstallerFor(packageName: String): String? {
    return when {
        AndroidVersion.isAtLeastApi30R() -> getInstallSourceInfo(packageName).initiatingPackageName
        else -> getInstallerPackageName(packageName)
    }
}
