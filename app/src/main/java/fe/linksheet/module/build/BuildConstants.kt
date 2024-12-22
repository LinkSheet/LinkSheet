package fe.linksheet.module.build

import android.os.Build

interface BuildConstants {
    val manufacturer: String
    val fingerprint: String
    val release: String
    val model: String
}

object RealBuildConstants : BuildConstants {
    override val manufacturer: String = Build.MANUFACTURER
    override val fingerprint: String = Build.FINGERPRINT
    override val release: String = Build.VERSION.RELEASE
    override val model: String = Build.MODEL
}

class SystemPropertiesBuildConstants(
    val properties: SystemProperties,
    override val manufacturer: String = properties.get("ro.product.manufacturer")
        ?: properties.get("ro.product.system.manufacturer")!!,
    override val fingerprint: String = properties.get("ro.build.fingerprint")
        ?: properties.get("ro.product.build.fingerprint")!!,
    override val release: String = properties.get("ro.build.version.release")!!,
    override val model: String = properties.get("ro.product.model") ?: properties.get("ro.product.system.model")!!,
) : BuildConstants {

}
