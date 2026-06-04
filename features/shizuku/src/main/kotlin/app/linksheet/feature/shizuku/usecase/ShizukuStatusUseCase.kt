package app.linksheet.feature.shizuku.usecase

import android.app.Activity
import android.content.Intent
import androidx.core.net.toUri
import app.linksheet.feature.shizuku.ShizukuDownload
import app.linksheet.feature.shizuku.service.ShizukuService
import fe.linksheet.extension.android.tryStartActivity

class ShizukuStatusUseCase(
    private val shizukuService: ShizukuService,
) {
    val status = shizukuService.statusFlow
    fun requestPermission() {
        shizukuService.requestPermission()
    }

    fun openShizukuWeb(activity: Activity?) {
        activity?.tryStartActivity(Intent(Intent.ACTION_VIEW, ShizukuDownload.toUri()))
    }

    fun startManager(activity: Activity?) {
        val success = activity?.tryStartActivity(ShizukuService.ManagerIntent)

//        if (!success) {
//            Toast.makeText(activity, R.string.shizuku_manager_start_failed, Toast.LENGTH_LONG).show()
//        }
    }
}
