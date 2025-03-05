package fe.linksheet.module.systeminfo

import android.os.Build

interface BuildConstants {
    val manufacturer: String
    val fingerprint: String
    val release: String
    val model: String
    val sdk: Int
}

object StaticBuildConstants : BuildConstants {
    override val manufacturer: String = Build.MANUFACTURER
    override val fingerprint: String = Build.FINGERPRINT
    override val release: String = Build.VERSION.RELEASE
    override val model: String = Build.MODEL
    override val sdk: Int = Build.VERSION.SDK_INT
}

class InjectedBuildConstants(
    val properties: SystemProperties,
    override val manufacturer: String = properties.firstNotNullOrNull(
        "ro.product.manufacturer",
        "ro.product.system.manufacturer",
    )!!,
    override val fingerprint: String = properties.firstNotNullOrNull(
        "ro.build.fingerprint",
        "ro.product.build.fingerprint",
        "ro.system.build.fingerprint",
    )!!,
    override val release: String = properties.get("ro.build.version.release")!!,
    override val model: String = properties.firstNotNullOrNull("ro.product.model", "ro.product.system.model")!!,
    override val sdk: Int = properties.firstNotNullOrNull(
        "ro.build.sdk",
        "ro.build.version.sdk",
        "ro.product.build.version.sdk",
        "ro.system.build.version.sdk"
    )?.toInt() ?: 0
) : BuildConstants
