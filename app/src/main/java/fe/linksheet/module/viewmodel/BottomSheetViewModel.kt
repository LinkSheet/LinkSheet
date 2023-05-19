package fe.linksheet.module.viewmodel

import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.BottomSheetResult
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.data.entity.AppSelectionHistory
import fe.linksheet.data.entity.DisableInAppBrowserInSelected
import fe.linksheet.data.entity.WhitelistedBrowser
import fe.linksheet.extension.ioAsync
import fe.linksheet.extension.ioLaunch
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.redirectresolver.RedirectResolver
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.repository.ResolvedRedirectRepository
import fe.linksheet.module.repository.WhitelistedBrowserRepository
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.io
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.io.File
import java.util.Locale

class BottomSheetViewModel(
    val context: Application,
    val preferenceRepository: PreferenceRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val intentResolver: IntentResolver,
) : BaseViewModel(preferenceRepository), KoinComponent {
    var resolveResult by mutableStateOf<BottomSheetResult?>(null)
    val enableCopyButton = preferenceRepository.getBoolean(Preferences.enableCopyButton)
    val hideAfterCopying = preferenceRepository.getBoolean(Preferences.hideAfterCopying)
    val singleTap = preferenceRepository.getBoolean(Preferences.singleTap)
    val enableSendButton = preferenceRepository.getBoolean(Preferences.enableSendButton)
    val disableToasts = preferenceRepository.getBoolean(Preferences.disableToasts)
    val gridLayout = preferenceRepository.getBoolean(Preferences.gridLayout)
    private val followRedirects = preferenceRepository.getBoolean(Preferences.followRedirects)
    private var enableDownloader = preferenceRepository.getBoolean(Preferences.enableDownloader)
    val theme = preferenceRepository.get(Preferences.theme)
    val useTextShareCopyButtons =
        preferenceRepository.getBoolean(Preferences.useTextShareCopyButtons)
    val previewUrl = preferenceRepository.getBoolean(Preferences.previewUrl)

    fun resolveAsync(intent: Intent, referrer: Uri?) = ioAsync {
        intentResolver.resolve(intent, referrer).apply {
            resolveResult = this
        }
    }

    fun showLoadingBottomSheet() = followRedirects || enableDownloader

    fun startMainActivity(context: Activity): Boolean {
        return context.startActivityWithConfirmation(Intent(context, MainActivity::class.java))
    }

    fun startPackageInfoActivity(context: Activity, info: DisplayActivityInfo): Boolean {
        return context.startActivityWithConfirmation(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            this.data = Uri.parse("package:${info.packageName}")
        })
    }

    fun persistSelectedIntent(intent: Intent, always: Boolean) = ioLaunch {
        Timber.tag("PersistingSelectedIntent").d("Component: ${intent.component}")
        intent.component?.let { component ->
            val host = intent.data!!.host!!.lowercase(Locale.getDefault())
            val app = PreferredApp(
                host = host,
                packageName = component.packageName,
                component = component.flattenToString(),
                alwaysPreferred = always
            )

            Timber.tag("PersistingSelectedIntent").d("Inserting $app")
            preferredAppRepository.insert(app)

            val historyEntry = AppSelectionHistory(
                host = host,
                packageName = component.packageName,
                lastUsed = System.currentTimeMillis()
            )

            appSelectionHistoryRepository.insert(historyEntry)
            Timber.tag("PersistingSelectedIntent").d("Inserting $historyEntry")
        }
    }

    fun startDownload(
        resources: Resources,
        downloadManager: DownloadManager,
        uri: Uri?,
        downloadable: Downloader.DownloadCheckResult.Downloadable
    ) {
        val path =
            "${resources.getString(R.string.app_name)}${File.separator}${downloadable.toFileName()}"
        Timber.tag("startDownload").d(path)

        val request = DownloadManager.Request(uri)
            .setTitle(resources.getString(R.string.linksheet_download))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                path
            )

        downloadManager.enqueue(request)
    }
}