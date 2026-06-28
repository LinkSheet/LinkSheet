package app.linksheet.feature.shizuku.usecase

import android.app.Activity
import android.content.Intent
import androidx.core.net.toUri
import app.linksheet.api.preference.RemoteConfigRepository
import app.linksheet.feature.shizuku.service.ShizukuService
import fe.android.preference.helper.Preference
import fe.composekit.preference.asFlow
import fe.linksheet.extension.android.tryStartActivity

class ShizukuStatusUseCase(
    remoteConfigRepository: RemoteConfigRepository,
    linkAssetsPreference: Preference.Mapped<Map<String, String>, String>,
    private val shizukuService: ShizukuService,
) {
    companion object {
        private const val LINK_KEY = "web.shizuku.download"
        private val LinkFallback = "https://shizuku.rikka.app/download".toUri()
    }

    private val linkAssets = remoteConfigRepository.asFlow(linkAssetsPreference)

    val status = shizukuService.statusFlow
    fun requestPermission() {
        shizukuService.requestPermission()
    }

    fun openShizukuWeb(activity: Activity?) {
        val uri = linkAssets.value[LINK_KEY]?.toUri() ?: LinkFallback
        activity?.tryStartActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun startManager(activity: Activity?) {
        val success = activity?.tryStartActivity(ShizukuService.ManagerIntent)

//        if (!success) {
//            Toast.makeText(activity, R.string.shizuku_manager_start_failed, Toast.LENGTH_LONG).show()
//        }
    }
}
