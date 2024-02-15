package fe.linksheet.module.viewmodel

import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getState
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.extension.android.canAccessInternet
import fe.linksheet.extension.android.ioAsync
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.log.hasher.HashProcessor
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.preference.AppPreferences
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.ResolveModule
import fe.linksheet.module.resolver.ResolveModuleStatus
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.util.PrivateBrowsingBrowser
import org.koin.core.component.KoinComponent
import java.io.File
import java.net.UnknownHostException
import java.util.Locale

class BottomSheetViewModel(
    val context: Application,
    loggerFactory: LoggerFactory,
    val preferenceRepository: AppPreferenceRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val intentResolver: IntentResolver,
    val state: SavedStateHandle,
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

    val privateBrowser: MutableState<PrivateBrowsingBrowser?> = mutableStateOf(null)
    val appInfo: MutableState<DisplayActivityInfo?> = mutableStateOf(null)

    val urlCopiedToast = preferenceRepository.getBooleanState(AppPreferences.urlCopiedToast)
    val downloadStartedToast = preferenceRepository.getBooleanState(AppPreferences.downloadStartedToast)

    val gridLayout = preferenceRepository.getBooleanState(AppPreferences.gridLayout)
    private val followRedirects =
        preferenceRepository.getBooleanState(AppPreferences.followRedirects)
    private var enableDownloader = preferenceRepository.getBooleanState(
        AppPreferences.enableDownloader
    )

    val openingWithAppToast = preferenceRepository.getBooleanState(AppPreferences.openingWithAppToast)
    val resolveViaToast = preferenceRepository.getBooleanState(AppPreferences.resolveViaToast)
    val resolveViaFailedToast = preferenceRepository.getBooleanState(AppPreferences.resolveViaFailedToast)

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
    val hideBottomSheetChoiceButtons = preferenceRepository.getBooleanState(AppPreferences.hideBottomSheetChoiceButtons)


    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val downloadManager = context.getSystemService<DownloadManager>()!!
    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!



    fun resolveAsync(intent: Intent, referrer: Uri?) = ioAsync {
        val canAccessInternet = kotlin.runCatching {
            connectivityManager.canAccessInternet()
        }.onFailure {
            it.printStackTrace()
        }.getOrDefault(true)

        intentResolver.resolveIfEnabled(intent, referrer, canAccessInternet).apply {
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

    fun getResolveToastTexts(resolveModuleStatus: ResolveModuleStatus): List<String> {
        return resolveModuleStatus.globalFailure?.getString(context)?.let {
            listOf(resolveFailureString(context.getString(R.string.online_services), it))

        } ?: resolveModuleStatus.resolved.mapNotNull {
            getResolveToastText(it.key, it.value)
        }
    }

    private fun getResolveToastText(resolveModule: ResolveModule, result: Result<ResolveResultType>?): String? {
        if (result == null) return null
        val resolveResult = result.getOrNull()

        val moduleName = resolveModule.getString(context)
        if (resolveResult is ResolveResultType.Resolved && resolveViaToast.value) {
            return context.getString(R.string.resolved_via, moduleName, resolveResult.getString(context))
        }

        if (resolveResult == null && resolveViaFailedToast.value) {
            return resolveFailureString(moduleName, result.exceptionOrNull())
        }

        return null
    }

    private fun resolveFailureString(name: String, error: Any?): String {
        return context.getString(R.string.resolve_failed, name, error)
    }

    private suspend fun persistSelectedIntent(intent: Intent, always: Boolean) {
        if (intent.component != null) {
            logger.debug(
                { "Component $it" },
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

            logger.debug({ "Inserting $it" }, app, HashProcessor.PreferenceAppHashProcessor, "AppPreferencePersister")

            preferredAppRepository.insert(app)

            val historyEntry = AppSelectionHistory(
                host = host,
                packageName = component.packageName,
                lastUsed = System.currentTimeMillis()
            )

            logger.debug(
                { "Inserting $it" },
                historyEntry,
                HashProcessor.AppSelectionHistoryHashProcessor,
                "HistoryEntryPersister"
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

        // Check for intent.data != null to make sure we don't attempt to persist web search intents
        if (persist && privateBrowsingBrowser == null && intent.data != null) {
            persistSelectedIntent(newIntent, always)
        }

        newIntent
    }
}
