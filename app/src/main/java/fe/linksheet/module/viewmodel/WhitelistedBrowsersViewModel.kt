package fe.linksheet.module.viewmodel

import androidx.lifecycle.viewModelScope
import app.linksheet.feature.app.ActivityAppInfo
import app.linksheet.feature.app.usecase.BrowsersUseCase
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedBrowserInfo
import fe.linksheet.module.repository.whitelisted.WhitelistedInAppBrowsersRepository
import fe.linksheet.module.repository.whitelisted.WhitelistedNormalBrowsersRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.common.applist.AppListCommon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WhitelistedBrowsersViewModel(
    val type: PreferredBrowserViewModel.BrowserType,
    val useCase: BrowsersUseCase,
    normalBrowsersRepository: WhitelistedNormalBrowsersRepository,
    inAppBrowsersRepository: WhitelistedInAppBrowsersRepository,
    preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {
    val list by lazy { AppListCommon(apps = useCase.queryBrowsersFlow(), scope = viewModelScope) }

    val browsersRepository = when(type) {
        PreferredBrowserViewModel.BrowserType.Normal -> normalBrowsersRepository
        PreferredBrowserViewModel.BrowserType.InApp -> inAppBrowsersRepository
    }

    fun getAll(): Flow<WhitelistedBrowserInfo> {
        return browsersRepository.getPackageSet()
    }

    fun save(
        item: ActivityAppInfo,
        enabled: Boolean,
        isSourcePackageNameOnly: Boolean
    ) = viewModelScope.launch(Dispatchers.IO) {
        browsersRepository.migrateState(item, enabled, isSourcePackageNameOnly)
        browsersRepository.insertOrDelete(enabled, item)
    }
}
