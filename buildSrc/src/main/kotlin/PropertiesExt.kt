import java.util.*

fun Properties?.getOrSystemEnv(key: String, default: String? = null): String? {
    return if (this == null || !containsKey(key)) {
        System.getenv(key)
    } else getProperty(key)
}
