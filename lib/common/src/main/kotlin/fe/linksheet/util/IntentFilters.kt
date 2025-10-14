package fe.linksheet.util

import android.content.Intent

object IntentFilters {
    val packageState by lazy {
        buildIntentFilter {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
    }
}
