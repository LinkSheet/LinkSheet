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
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getState
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.extension.android.ioAsync
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.log.PackageProcessor
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.PrivateBrowsingBrowser
import org.koin.core.component.KoinComponent
import java.io.File
import java.util.Locale

class BottomSheetViewModel(
    val context: Application,
    loggerFactory: LoggerFactory,
    val preferenceRepository: AppPreferenceRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val intentResolver: IntentResolver,
) : BaseViewModel(preferenceRepository), KoinComponent {
    private val logger = loggerFactory.createLogger(BottomSheetViewModel::class)

    var resolveResult by mutableStateOf<BottomSheetResult?>(null)

    val enableCopyButton = preferenceRepository.getBooleanState(AppPreferences.enableCopyButton)
    val hideAfterCopying = preferenceRepository.getBooleanState(AppPreferences.hideAfterCopying)
    val singleTap = preferenceRepository.getBooleanState(AppPreferences.singleTap)
    val enableSendButton = preferenceRepository.getBooleanState(AppPreferences.enableSendButton)
    val enableIgnoreLibRedirectButton = preferenceRepository.getBooleanState(
        AppPreferences.enableIgnoreLibRedirectButton
    )

    val disableToasts = preferenceRepository.getBooleanState(AppPreferences.disableToasts)
    val gridLayout = preferenceRepository.getBooleanState(AppPreferences.gridLayout)
    private val followRedirects =
        preferenceRepository.getBooleanState(AppPreferences.followRedirects)
    private var enableDownloader = preferenceRepository.getBooleanState(
        AppPreferences.enableDownloader
    )

    val theme = preferenceRepository.getState(AppPreferences.theme)
    val useTextShareCopyButtons = preferenceRepository.getBooleanState(
        AppPreferences.useTextShareCopyButtons
    )
    val previewUrl = preferenceRepository.getBooleanState(AppPreferences.previewUrl)

    val enableRequestPrivateBrowsingButton = preferenceRepository.getBooleanState(
        AppPreferences.enableRequestPrivateBrowsingButton
    )

    val enableAmp2Html = preferenceRepository.getBooleanState(AppPreferences.enableAmp2Html)
    val showAsReferrer =
        preferenceRepository.getBooleanState(AppPreferences.showLinkSheetAsReferrer)

    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val downloadManager = context.getSystemService<DownloadManager>()!!

    fun resolveAsync(intent: Intent, referrer: Uri?) = ioAsync {
        intentResolver.resolve(intent, referrer).apply {
            resolveResult = this
        }
    }

    fun showLoadingBottomSheet() = followRedirects.value || enableAmp2Html.value
            || enableDownloader.value

    fun startMainActivity(context: Activity): Boolean {
        return context.startActivityWithConfirmation(Intent(context, MainActivity::class.java))
    }

    fun startPackageInfoActivity(context: Activity, info: DisplayActivityInfo): Boolean {
        return context.startActivityWithConfirmation(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            this.data = Uri.parse("package:${info.packageName}")
        })
    }

    private suspend fun persistSelectedIntent(intent: Intent, always: Boolean) {
        if (intent.component != null) {
            logger.debug(
                { "Component=${it}" },
                intent.component!!,
                HashProcessor.ComponentProcessor
            )

        }

        intent.component?.let { component ->
            val host = intent.data!!.host!!.lowercase(Locale.getDefault())
            val app = PreferredApp(
                host = host,
                packageName = component.packageName,
                component = component.flattenToString(),
                alwaysPreferred = always
            )

            logger.debug({ "Inserting $it" }, app, HashProcessor.PreferenceAppHashProcessor)

            preferredAppRepository.insert(app)

            val historyEntry = AppSelectionHistory(
                host = host,
                packageName = component.packageName,
                lastUsed = System.currentTimeMillis()
            )

            logger.debug(
                { "Inserting=$it" },
                historyEntry,
                HashProcessor.AppSelectionHistoryHashProcessor
            )
            appSelectionHistoryRepository.insert(historyEntry)
        }
    }

    fun startDownload(
        resources: Resources,
        uri: Uri?,
        downloadable: Downloader.DownloadCheckResult.Downloadable
    ) {
        val path =
            "${resources.getString(R.string.app_name)}${File.separator}${downloadable.toFileName()}"

        val request = DownloadManager.Request(uri)
            .setTitle(resources.getString(R.string.linksheet_download))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                path
            )

        downloadManager.enqueue(request)
    }

    fun launchAppAsync(
        info: DisplayActivityInfo,
        intent: Intent,
        always: Boolean = false,
        privateBrowsingBrowser: PrivateBrowsingBrowser? = null,
        persist: Boolean = true,
    ) = ioAsync {
        val newIntent = info.intentFrom(intent).let {
            privateBrowsingBrowser?.requestPrivateBrowsing(it) ?: it
        }

        if (persist && privateBrowsingBrowser == null) {
            persistSelectedIntent(newIntent, always)
        }

        newIntent
    }
}