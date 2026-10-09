package app.linksheet.feature.shizuku.viewmodel

import androidx.lifecycle.ViewModel
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.shizuku.preference.ShizukuPreferences
import app.linksheet.feature.shizuku.usecase.ShizukuStatusUseCase

class ShizukuSettingsViewModel(
    preferenceRepository: AppPreferenceRepository,
    shizukuPreferences: ShizukuPreferences,
    val statusUseCase: ShizukuStatusUseCase,
) : ViewModel() {

    val enableShizuku = preferenceRepository.asViewModelState(shizukuPreferences.enable)
    val autoDisableLinkHandling = preferenceRepository.asViewModelState(shizukuPreferences.autoDisableLinkHandling)
}

