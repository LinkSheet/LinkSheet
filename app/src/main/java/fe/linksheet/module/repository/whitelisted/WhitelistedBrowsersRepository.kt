package fe.linksheet.module.repository.whitelisted

import app.linksheet.feature.app.core.ActivityAppInfo
import app.linksheet.feature.app.core.ActivityAppInfoStatus
import kotlinx.coroutines.flow.Flow


interface WhitelistedBrowsersRepository {
    fun getPackageSet(): Flow<WhitelistedBrowserInfo>
    suspend fun migrateState(
        appInfo: ActivityAppInfo,
        enabled: Boolean,
        isSourcePackageNameOnly: Boolean
    )
    suspend fun migrateState(items: List<ActivityAppInfoStatus>)
    suspend fun insertOrDelete(newState: Boolean, appInfo: ActivityAppInfo)
}
