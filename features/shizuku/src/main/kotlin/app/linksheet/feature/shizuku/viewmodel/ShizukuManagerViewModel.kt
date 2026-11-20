package app.linksheet.feature.shizuku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.app.applist.AppListModel
import app.linksheet.feature.app.core.IAppInfo
import app.linksheet.feature.shizuku.preference.ShizukuPreferences
import app.linksheet.feature.shizuku.usecase.ShizukuManagerUseCase
import kotlinx.coroutines.Dispatchers

class ShizukuManagerViewModel internal constructor(
    preferenceRepository: AppPreferenceRepository,
    shizukuPreferences: ShizukuPreferences,
    private val useCase: ShizukuManagerUseCase,
) : ViewModel() {

    val appListModel by lazy {
        AppListModel(queryApps = useCase::queryManagers, scope = viewModelScope)
    }

    val selectedPackage = preferenceRepository.asViewModelState(shizukuPreferences.managerPackage)
    private val selectedCert = preferenceRepository.asViewModelState(shizukuPreferences.managerCertSha256)

    /** Pins the package together with its current signing cert. Does nothing if the cert cannot be read. */
    fun select(app: IAppInfo) {
        val cert = useCase.certSha256(app.packageName) ?: return
        selectedCert(cert)
        selectedPackage(app.packageName)
    }
}
