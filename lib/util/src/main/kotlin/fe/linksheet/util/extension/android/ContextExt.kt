package fe.linksheet.util.extension.android

import android.content.Context
import androidx.core.content.getSystemService

fun Context.getCurrentLanguageTag(): String {
    return resources.configuration.getLocales()[0].toLanguageTag()
}

inline fun <reified T : Any> Context.getSystemServiceOrThrow(): T {
    return getSystemService<T>() ?: error("System service '${T::class.simpleName}' is not available")
}
