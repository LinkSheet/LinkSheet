package fe.linksheet.module.build

interface SystemProperties {
    fun get(key: String): String?
}

object RealSystemProperties : SystemProperties {
    override fun get(key: String): String? {
        return android.os.SystemProperties.get(key)
    }
}
