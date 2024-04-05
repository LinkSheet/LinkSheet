package fe.linksheet.module.viewmodel


import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import fe.linksheet.R
import fe.linksheet.activity.main.MainActivity
import fe.linksheet.activity.bottomsheet.TapConfig
import fe.linksheet.activity.bottomsheet.column.*
import fe.linksheet.extension.android.canAccessInternet
import fe.linksheet.extension.android.ioAsync
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.resolver.ResolveModule
import fe.linksheet.module.resolver.ResolveModuleStatus
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import kotlinx.coroutines.Deferred
import mozilla.components.support.utils.toSafeIntent
import org.koin.core.component.KoinComponent
import java.io.File
import java.util.*

class BottomSheetViewModel(
    val context: Application,
    val preferenceRepository: AppPreferenceRepository,
    featureFlagRepository: FeatureFlagRepository,
    experimentRepository: ExperimentRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    private val intentResolver: IntentResolver,
    val state: SavedStateHandle,
) : BaseViewModel(preferenceRepository), KoinComponent {
    private val logger by injectLogger<BottomSheetViewModel>()

    var resolveResult by mutableStateOf<BottomSheetResult?>(null)

    val hideAfterCopying = preferenceRepository.asState(AppPreferences.hideAfterCopying)

    val urlCopiedToast = preferenceRepository.asState(AppPreferences.urlCopiedToast)
    val downloadStartedToast = preferenceRepository.asState(AppPreferences.downloadStartedToast)

    val gridLayout = preferenceRepository.asState(AppPreferences.gridLayout)
    private val followRedirects =
        preferenceRepository.asState(AppPreferences.followRedirects)
    private var enableDownloader = preferenceRepository.asState(
        AppPreferences.enableDownloader
    )

    val openingWithAppToast = preferenceRepository.asState(AppPreferences.openingWithAppToast)
    val resolveViaToast = preferenceRepository.asState(AppPreferences.resolveViaToast)
    val resolveViaFailedToast = preferenceRepository.asState(AppPreferences.resolveViaFailedToast)

    val themeV2 = preferenceRepository.asState(AppPreferences.themeV2)
    val themeAmoled = preferenceRepository.asState(AppPreferences.themeAmoled)

    val previewUrl = preferenceRepository.asState(AppPreferences.previewUrl)

    val enableRequestPrivateBrowsingButton = preferenceRepository.asState(
        AppPreferences.enableRequestPrivateBrowsingButton
    )

    val enableAmp2Html = preferenceRepository.asState(AppPreferences.enableAmp2Html)
    val showAsReferrer =
        preferenceRepository.asState(AppPreferences.showLinkSheetAsReferrer)
    val hideBottomSheetChoiceButtons = preferenceRepository.asState(AppPreferences.hideBottomSheetChoiceButtons)


    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val downloadManager = context.getSystemService<DownloadManager>()!!
    private val connectivityManager = context.getSystemService<ConnectivityManager>()!!

    val experimentalUrlBar = experimentRepository.asState(Experiments.experimentalUrlBar)
    val declutterUrl = experimentRepository.asState(Experiments.declutterUrl)
    val switchProfile = experimentRepository.asState(Experiments.switchProfile)

    val newQueryManager = experimentRepository.asState(Experiments.newQueryManager)

    val tapConfigSingle = preferenceRepository.asState(AppPreferences.tapConfigSingle)
    val tapConfigDouble = preferenceRepository.asState(AppPreferences.tapConfigDouble)
    val tapConfigLong = preferenceRepository.asState(AppPreferences.tapConfigLong)
    val expandOnAppSelect = preferenceRepository.asState(AppPreferences.expandOnAppSelect)
    val bottomSheetNativeLabel = preferenceRepository.asState(AppPreferences.bottomSheetNativeLabel)

    var appListSelectedIdx = mutableIntStateOf(-1)

    fun resolveAsync(intent: Intent, referrer: Uri?) = ioAsync {
        val canAccessInternet = kotlin.runCatching {
            connectivityManager.canAccessInternet()
        }.onFailure {
            logger.error(it)
            it.printStackTrace()
        }.getOrDefault(true)

        intentResolver.resolveIfEnabled(intent.toSafeIntent(), referrer, canAccessInternet).apply {
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
        val str = if (error is Throwable) "${error::class.java.simpleName}(${error.message})" else error.toString()
        return context.getString(R.string.resolve_failed, name, str)
    }

    private suspend fun persistSelectedIntent(intent: Intent, always: Boolean) {
        val (packageName, component) = intent.component
            ?.let { it.packageName to it } ?: (intent.`package` to null)

        if (packageName == null && component == null) {
            logger.error("Passed intent does neither have a package, nor a component, can't persist")
            return
        }

        val host = intent.data!!.host!!.lowercase(Locale.getDefault())
        val app = PreferredApp.new(
            host = host,
            pkg = packageName!!,
            cmp = component,
            always = always
        )

        logger.debug(app, HashProcessor.PreferenceAppHashProcessor, { "Inserting $it" }, "AppPreferencePersister")

        kotlin.runCatching { preferredAppRepository.insert(app) }.onFailure { it.printStackTrace() }

        val historyEntry = AppSelectionHistory(
            host = host,
            packageName = packageName,
            lastUsed = System.currentTimeMillis()
        )

        logger.debug(
            historyEntry,
            HashProcessor.AppSelectionHistoryHashProcessor,
            { "Inserting $it" },
            "HistoryEntryPersister"
        )
        appSelectionHistoryRepository.insert(historyEntry)
    }

    fun startDownload(
        resources: Resources,
        uri: Uri?,
        downloadable: Downloader.DownloadCheckResult.Downloadable,
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
        privateBrowsingBrowser: KnownBrowser? = null,
        persist: Boolean = true,
    ) = ioAsync {
        launchApp(
            info = info,
            intent = intent,
            always = always,
            privateBrowsingBrowser = privateBrowsingBrowser,
            persist = persist
        )
    }

    suspend fun launchApp(
        info: DisplayActivityInfo,
        intent: Intent,
        always: Boolean = false,
        privateBrowsingBrowser: KnownBrowser? = null,
        persist: Boolean = true,
    ): Intent {
        val launchIntent = Intent(intent)
            .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)

//        if (newQueryManager()) {
//            launchIntent.`package` = info.packageName
//        } else {
        launchIntent.addCategory(Intent.CATEGORY_BROWSABLE).addCategory(Intent.CATEGORY_DEFAULT)
        launchIntent.component = info.componentName
//        }

        val newIntent = launchIntent.let {
            privateBrowsingBrowser?.requestPrivateBrowsing(it) ?: it
        }

        // Check for intent.data != null to make sure we don't attempt to persist web search intents
        if (!info.fallback && persist && privateBrowsingBrowser == null && intent.data != null) {
            persistSelectedIntent(newIntent, always)
        }

        return newIntent
    }

    private fun ClickType.getPreference(modifier: ClickModifier): TapConfig {
        if (modifier is ClickModifier.Private || modifier == ClickModifier.Always) {
            return TapConfig.OpenApp
        }

        return when (this) {
            ClickType.Single -> tapConfigSingle
            ClickType.Double -> tapConfigDouble
            ClickType.Long -> tapConfigLong
        }.value
    }

    fun handleClick(
        activity: Activity,
        index: Int,
        isExpanded: Boolean,
        requestExpand: () -> Unit,
        result: Intent,
        info: DisplayActivityInfo,
        type: ClickType,
        modifier: ClickModifier,
    ): Deferred<Intent>? {
        val config = type.getPreference(modifier)

        when (config) {
            TapConfig.None -> {}
            TapConfig.OpenApp -> {
                return launchAppAsync(
                    info = info,
                    intent = result,
                    always = modifier is ClickModifier.Always,
                    privateBrowsingBrowser = (modifier as? ClickModifier.Private)?.browser,
                    persist = modifier !is ClickModifier.Private
                )
            }

            TapConfig.OpenSettings -> {
                startPackageInfoActivity(activity, info)
            }

            TapConfig.SelectItem -> {
                appListSelectedIdx.intValue = if (appListSelectedIdx.intValue != index) index else -1
                if (appListSelectedIdx.intValue != -1 && !isExpanded && expandOnAppSelect()) {
                    requestExpand()
                }
            }
        }

        return null
    }

    fun safeStartActivity(activity: Activity, intent: Intent): Boolean {
        try {
            activity.startActivity(intent)
            return true
        } catch (e: ActivityNotFoundException) {
            logger.error(e)
            return false
        }
    }
}
