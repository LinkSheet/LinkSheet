package app.linksheet.feature.shizuku.viewmodel

import androidx.lifecycle.ViewModel
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.shizuku.preference.ShizukuPreferences
import app.linksheet.feature.shizuku.service.ShizukuService
import app.linksheet.feature.shizuku.usecase.ShizukuStatusUseCase

class ShizukuSettingsViewModel(
    private val shizukuService: ShizukuService,
    preferenceRepository: AppPreferenceRepository,
    shizukuPreferences: ShizukuPreferences,
) : ViewModel() {
    val statusUseCase = ShizukuStatusUseCase(shizukuService = shizukuService)

    val enableShizuku = preferenceRepository.asViewModelState(shizukuPreferences.enable)
    val autoDisableLinkHandling = preferenceRepository.asViewModelState(shizukuPreferences.autoDisableLinkHandling)
}

