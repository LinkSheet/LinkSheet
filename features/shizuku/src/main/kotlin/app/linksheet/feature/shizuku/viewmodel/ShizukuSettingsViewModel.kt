package app.linksheet.feature.shizuku.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import app.linksheet.feature.shizuku.preference.ShizukuPreferences
import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.feature.shizuku.ShizukuService
import fe.linksheet.util.extension.android.tryStartActivity

class ShizukuSettingsViewModel(
    val context: Application,
    private val shizukuService: ShizukuService,
    preferenceRepository: AppPreferenceRepository,
    shizukuPreferences: ShizukuPreferences,
) : ViewModel() {
    val enableShizuku = preferenceRepository.asViewModelState(shizukuPreferences.enable)
    val autoDisableLinkHandling = preferenceRepository.asViewModelState(shizukuPreferences.autoDisableLinkHandling)
    val status = shizukuService.statusFlow

    fun requestPermission() {
        shizukuService.requestPermission()
    }

    fun startManager(activity: Activity?) {
        val success = activity?.tryStartActivity(ShizukuService.ManagerIntent)

//        if (!success) {
//            Toast.makeText(activity, R.string.shizuku_manager_start_failed, Toast.LENGTH_LONG).show()
//        }
    }
}

