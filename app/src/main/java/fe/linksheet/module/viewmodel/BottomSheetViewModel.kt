package fe.linksheet.module.viewmodel

import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getState
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.extension.IntentExt.newIntent
import fe.linksheet.extension.ioAsync
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import org.koin.core.component.KoinComponent
import java.io.File
import java.util.Locale

class BottomSheetViewModel(
    val context: Application,
    loggerFactory: LoggerFactory,
    val preferenceRepository: PreferenceRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val intentResolver: IntentResolver,
) : BaseViewModel(preferenceRepository), KoinComponent {
    private val logger = loggerFactory.createLogger(BottomSheetViewModel::class)

    var resolveResult by mutableStateOf<BottomSheetResult?>(null)

    val enableCopyButton = preferenceRepository.getBooleanState(Preferences.enableCopyButton)
    val hideAfterCopying = preferenceRepository.getBooleanState(Preferences.hideAfterCopying)
    val singleTap = preferenceRepository.getBooleanState(Preferences.singleTap)
    val enableSendButton = preferenceRepository.getBooleanState(Preferences.enableSendButton)
    val enableIgnoreLibRedirectButton = preferenceRepository.getBooleanState(
        Preferences.enableIgnoreLibRedirectButton
    )

    val disableToasts = preferenceRepository.getBooleanState(Preferences.disableToasts)
    val gridLayout = preferenceRepository.getBooleanState(Preferences.gridLayout)
    private val followRedirects = preferenceRepository.getBooleanState(Preferences.followRedirects)
    private var enableDownloader =
        preferenceRepository.getBooleanState(Preferences.enableDownloader)
    val theme = preferenceRepository.getState(Preferences.theme)
    val useTextShareCopyButtons = preferenceRepository.getBooleanState(
        Preferences.useTextShareCopyButtons
    )
    val previewUrl = preferenceRepository.getBooleanState(Preferences.previewUrl)

    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val downloadManager = context.getSystemService<DownloadManager>()!!

    fun resolveAsync(intent: Intent, referrer: Uri?) = ioAsync {
        intentResolver.resolve(intent, referrer).apply {
            resolveResult = this
        }
    }

    fun showLoadingBottomSheet() = followRedirects.value || enableDownloader.value

    fun startMainActivity(context: Activity): Boolean {
        return context.startActivityWithConfirmation(Intent(context, MainActivity::class.java))
    }

    fun startPackageInfoActivity(context: Activity, info: DisplayActivityInfo): Boolean {
        return context.startActivityWithConfirmation(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            this.data = Uri.parse("package:${info.packageName}")
        })
    }

    suspend fun persistSelectedIntent(intent: Intent, always: Boolean) {
        logger.debug("Component=%s", intent.component)

        intent.component?.let { component ->
            val host = intent.data!!.host!!.lowercase(Locale.getDefault())
            val app = PreferredApp(
                host = host,
                packageName = component.packageName,
                component = component.flattenToString(),
                alwaysPreferred = always
            )

            logger.debug("Inserting=%s", app)
            preferredAppRepository.insert(app)

            val historyEntry = AppSelectionHistory(
                host = host,
                packageName = component.packageName,
                lastUsed = System.currentTimeMillis()
            )

            appSelectionHistoryRepository.insert(historyEntry)
            logger.debug("Inserting=%s", historyEntry)
        }
    }

    fun startDownload(
        resources: Resources,
        uri: Uri?,
        downloadable: Downloader.DownloadCheckResult.Downloadable
    ) {
        val path = "${resources.getString(R.string.app_name)}${File.separator}${downloadable.toFileName()}"

        val request = DownloadManager.Request(uri)
            .setTitle(resources.getString(R.string.linksheet_download))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                path
            )

        downloadManager.enqueue(request)
    }

    fun launchApp(
        info: DisplayActivityInfo,
        intent: Intent,
        uri: Uri?,
        always: Boolean = false
    ) = ioAsync {
        info.intentFrom(intent.newIntent(uri)).also {
            persistSelectedIntent(it, always)
        }
    }
}