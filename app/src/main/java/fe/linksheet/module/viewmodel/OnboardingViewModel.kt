package fe.linksheet.module.viewmodel

import android.app.Application
import fe.linksheet.module.preference.app.AppPreferenceRepository


import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.viewmodel.base.BaseViewModel


class OnboardingViewModel(
    val context: Application,
    val preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {

    val firstRun = preferenceRepository.asState(AppPreferences.firstRun)
}
