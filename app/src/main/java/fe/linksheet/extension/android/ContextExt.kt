package fe.linksheet.extension.android

import android.content.Context
import java.util.*

fun Context.getCurrentLocale(): Locale {
    return resources.configuration.getLocales()[0]
}
