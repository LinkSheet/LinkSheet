package fe.linksheet.module.resolver

import android.content.Intent
import fe.linksheet.module.resolver.util.CustomTabHandler
import fe.linksheet.util.intent.StandardIntents

class SelfIntentUseCase(
    private val selfPackage: String
) {
    fun buildOpenExternallyIntent(intent: Intent): Intent {
        val customTabExtras = CustomTabHandler.filterExtras(intent.extras, true)
        for (extra in customTabExtras) {
            intent.removeExtra(extra)
        }
        val selfIntent = StandardIntents.createSelfIntent(intent, selfPackage = selfPackage)
        return selfIntent
    }
}
