package fe.buildsrc.extension

import java.util.*

fun Properties?.getOrSystemEnv(key: String, default: String? = null): String? {
    val value = if (this == null || !containsKey(key)) System.getenv(key)
    else getProperty(key)

    return value ?: default
}
