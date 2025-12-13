package app.linksheet.feature.browser.usecase

import android.content.ComponentName
import app.linksheet.feature.browser.core.Browser
import app.linksheet.feature.browser.core.PrivateBrowsingService
import app.linksheet.feature.browser.database.repository.PrivateBrowsingBrowserRepository

class PrivateBrowserUseCase internal constructor(
    private val repository: PrivateBrowsingBrowserRepository,
    private val privateBrowsingService: PrivateBrowsingService,
) {
    suspend fun isAllowedKnownBrowser(componentName: ComponentName, privateOnly: Boolean): Browser? {
        if (!repository.exists(componentName.flattenToString())) {
            return null
        }
        return privateBrowsingService.isKnownBrowser(componentName.packageName, privateOnly)
    }
}
