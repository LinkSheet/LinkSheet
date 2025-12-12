package fe.linksheet.module.viewmodel

import android.content.Intent
import app.linksheet.feature.app.core.DomainVerificationAppInfo
import app.linksheet.feature.app.usecase.DomainVerificationUseCase
import fe.composekit.core.AndroidVersion
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.devicecompat.oneui.OneUiCompat
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.intent.StandardIntents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VerifiedLinkHandlerViewModel(
    val packageName: String,
    val preferenceRepository: AppPreferenceRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val service: DomainVerificationUseCase,
    private val intentCompat: OneUiCompat,
) : BaseViewModel(preferenceRepository) {
    fun openSettings(): Intent {
        return when {
            AndroidVersion.isAtLeastApi31S() -> intentCompat.createAppOpenByDefaultSettingsIntent(packageName)
            else -> StandardIntents.createAppSettingsIntent(packageName)
        }
    }

    fun get(packageName: String): DomainVerificationAppInfo? {
        val info = service.createDomainVerificationAppInfo(packageName)

        return info
    }

    fun getPreferredAppsFlow(packageName: String): Flow<List<PreferredApp>> {
        val flow = preferredAppRepository.getByPackageNameFlow(packageName)
        return flow
    }
    suspend fun getPreferredApps(packageName: String): List<PreferredApp> = withContext(Dispatchers.IO) {
        val list = preferredAppRepository.getByPackageName(packageName)

        list
    }
}
