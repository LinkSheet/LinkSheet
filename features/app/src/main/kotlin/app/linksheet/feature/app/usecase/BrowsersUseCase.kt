package app.linksheet.feature.app.usecase

import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.app.core.AppInfoCreator
import app.linksheet.feature.app.core.PackageIntentHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BrowsersUseCase(
    private val creator: AppInfoCreator,
    private val packageIntentHandler: PackageIntentHandler,
) {
    fun queryBrowsersFlow(): Flow<List<ActivityAppInfo>> = flow {
        val browsers = queryBrowsers()
        emit(browsers)
    }

    fun queryBrowser(packageName: String): ActivityAppInfo? {
        val browser = packageIntentHandler.findHttpBrowsable(packageName).firstOrNull() ?: return null
        return creator.toActivityAppInfo(browser, null)
    }

    fun queryBrowsers(): List<ActivityAppInfo> {
        return packageIntentHandler.findHttpBrowsable(null).map {
            creator.toActivityAppInfo(
                it,
                null
            )
        }
    }
}
