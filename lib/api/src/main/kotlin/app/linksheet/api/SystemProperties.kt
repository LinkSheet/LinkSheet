package app.linksheet.api

interface SystemProperties {
    fun get(key: String): String?
    fun getAllProperties(): Map<String, String>

    fun firstNotNullOrNull(vararg keys: String): String? {
        for (key in keys) {
            val value = get(key) ?: continue
            return value
        }

        return null
    }
}
