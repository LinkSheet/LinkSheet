package fe.buildsrc.extension

import java.util.*

private fun getOrSystemEnv(properties: Properties?, key: String, default: String? = null): String? {
    val value = if (properties == null || !properties.containsKey(key)) System.getenv(key)
    else properties.getProperty(key)

    return value ?: default
}

fun Properties?.getOrSystemEnv(key: String): String? {
    return getOrSystemEnv(this, key)
}

fun Properties?.getOrSystemEnvOrDef(key: String, default: String): String {
    return getOrSystemEnv(this, key, default)!!
}
