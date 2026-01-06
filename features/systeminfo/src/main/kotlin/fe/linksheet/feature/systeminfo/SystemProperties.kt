package fe.linksheet.feature.systeminfo

import app.linksheet.api.RefineWrapper
import app.linksheet.api.SystemProperties
import fe.kotlin.extension.string.substringOrNull
import fe.std.process.android.AndroidStartConfig
import fe.std.process.launchProcess

class RealSystemProperties(
    private val refineWrapper: RefineWrapper
) : SystemProperties {

    override fun get(key: String): String? {
        return refineWrapper.getSystemProperty(key)
    }

    override fun getAllProperties(): Map<String, String> {
        fun String.unwrap(): String? {
            return trim().run { substringOrNull(1, length - 1) }
        }

        fun String.parseLine(): Pair<String, String>? {
            val (wrappedKey, wrappedValue) = split(":").takeIf { it.size >= 2 } ?: return null

            val key = wrappedKey.unwrap() ?: return null
            val value = wrappedValue.unwrap() ?: return null

            return key to value
        }

        return buildMap {
            launchProcess("getprop", invokeOnEmpty = false, config = AndroidStartConfig) {
                it.parseLine()?.let { (key, value) -> put(key, value) }
            }
        }
    }
}
