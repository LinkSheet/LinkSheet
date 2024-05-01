package fe.linksheet.extension.android

import android.content.Context

fun Context.getCurrentLanguageTag(): String {
    return resources.configuration.getLocales()[0].toLanguageTag()
}
