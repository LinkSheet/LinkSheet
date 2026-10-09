package fe.linksheet.module.viewmodel


import android.app.Application
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.shizuku.service.ShizukuService
import app.linksheet.feature.shizuku.usecase.ShizukuStatusUseCase
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel


class OnboardingViewModel(
    val context: Application,
    val preferenceRepository: AppPreferenceRepository,
    val shizukuStatusUseCase: ShizukuStatusUseCase
) : BaseViewModel(preferenceRepository) {

    val firstRun = preferenceRepository.asViewModelState(AppPreferences.firstRun)
}
