package fe.linksheet.module.viewmodel


import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.compose.runtime.mutableIntStateOf
import androidx.core.content.getSystemService
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import fe.linksheet.activity.bottomsheet.TapConfig
import fe.linksheet.extension.android.ioAsync
import fe.linksheet.extension.android.startActivityWithConfirmation
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.profile.ProfileSwitcher
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.repository.AppSelectionHistoryRepository
import fe.linksheet.module.repository.PreferredAppRepository
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.resolver.IntentResolver
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.resolver.ResolveModule
import fe.linksheet.module.resolver.urlresolver.ResolveResultType
import fe.linksheet.module.viewmodel.base.BaseViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.support.utils.SafeIntent
import org.koin.core.component.KoinComponent
import java.io.File
import java.util.*

class BottomSheetViewModel(
    val context: Application,
    val preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val preferredAppRepository: PreferredAppRepository,
    private val appSelectionHistoryRepository: AppSelectionHistoryRepository,
    val profileSwitcher: ProfileSwitcher,
    val intentResolver: IntentResolver,
    val state: SavedStateHandle,
) : BaseViewModel(preferenceRepository), KoinComponent {
    private val logger by injectLogger<BottomSheetViewModel>()

    val hideAfterCopying = preferenceRepository.asState(AppPreferences.hideAfterCopying)

    val urlCopiedToast = preferenceRepository.asState(AppPreferences.urlCopiedToast)
    val downloadStartedToast = preferenceRepository.asState(AppPreferences.downloadStartedToast)

    val gridLayout = preferenceRepository.asState(AppPreferences.gridLayout)
    val resolveViaToast = preferenceRepository.asState(AppPreferences.resolveViaToast)
    val resolveViaFailedToast = preferenceRepository.asState(AppPreferences.resolveViaFailedToast)
    val previewUrl = preferenceRepository.asState(AppPreferences.previewUrl)
    val enableRequestPrivateBrowsingButton = preferenceRepository.asState(
        AppPreferences.enableRequestPrivateBrowsingButton
    )

    val showAsReferrer =
        preferenceRepository.asState(AppPreferences.showLinkSheetAsReferrer)
    val hideBottomSheetChoiceButtons = preferenceRepository.asState(AppPreferences.hideBottomSheetChoiceButtons)
    val enableIgnoreLibRedirectButton = preferenceRepository.asState(AppPreferences.enableIgnoreLibRedirectButton)

    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val downloadManager = context.getSystemService<DownloadManager>()!!

    val bottomSheetProfileSwitcher = preferenceRepository.asState(AppPreferences.bottomSheetProfileSwitcher)

    val tapConfigSingle = preferenceRepository.asState(AppPreferences.tapConfigSingle)
    val tapConfigDouble = preferenceRepository.asState(AppPreferences.tapConfigDouble)
    val tapConfigLong = preferenceRepository.asState(AppPreferences.tapConfigLong)
    val expandOnAppSelect = preferenceRepository.asState(AppPreferences.expandOnAppSelect)
    val bottomSheetNativeLabel = preferenceRepository.asState(AppPreferences.bottomSheetNativeLabel)

    val improvedBottomSheetExpandFully = experimentRepository.asState(Experiments.improvedBottomSheetExpandFully)
    val improvedBottomSheetUrlDoubleTap = experimentRepository.asState(Experiments.improvedBottomSheetUrlDoubleTap)
    val interceptAccidentalTaps = experimentRepository.asState(Experiments.interceptAccidentalTaps)

    val manualFollowRedirects = experimentRepository.asState(Experiments.manualFollowRedirects)
    val noBottomSheetStateSave = experimentRepository.asState(Experiments.noBottomSheetStateSave)

    val appListSelectedIdx = mutableIntStateOf(-1)

    val events = intentResolver.events
    val interactions = intentResolver.interactions

    private val _resolveResultFlow = MutableStateFlow<IntentResolveResult>(IntentResolveResult.Pending)
    val resolveResultFlow = _resolveResultFlow.asStateFlow()

    fun resolveAsync(intent: SafeIntent, uri: Uri?) = viewModelScope.launch(Dispatchers.IO) {
        val resolveResult = intentResolver.resolve(intent, uri)
        _resolveResultFlow.emit(resolveResult)
    }

    fun startPackageInfoActivity(context: Activity, info: ActivityAppInfo): Boolean {
        return context.startActivityWithConfirmation(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            this.data = Uri.parse("package:${info.packageName}")
            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
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

    private val Intent.pkgCmp: Pair<String?, ComponentName?>
        get() = component?.let { it.packageName to it } ?: (`package` to null)

    private suspend fun persistSelectedIntent(intent: Intent, always: Boolean) {
        val (packageName, component) = intent.pkgCmp

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

        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                preferredAppRepository.insert(app)
            }
        }.onFailure { it.printStackTrace() }

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

        withContext(Dispatchers.IO) {
            appSelectionHistoryRepository.insert(historyEntry)
        }
    }

    fun startDownload(resources: Resources, uri: String?, downloadable: DownloadCheckResult.Downloadable) {
        val path =
            "${resources.getString(R.string.app_name)}${File.separator}${downloadable.toFileName()}"

        val request = DownloadManager.Request(Uri.parse(uri))
            .setTitle(resources.getString(R.string.linksheet_download))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                path
            )

        downloadManager.enqueue(request)
    }

    object GithubWorkaround {
        private val latestReleasesRegex = Regex(
            "^((?:https?|github)://(?:.+\\.)?github\\.com/.+/.+/releases)/latest/?$"
        )

        private val `package` = ComponentName(
            "com.github.android", "com.github.android.activities.DeepLinkActivity"
        )

        fun tryFixUri(componentName: ComponentName, uri: Uri?): Uri? {
            if (componentName != `package`) return null

            return uri
                ?.let { latestReleasesRegex.matchEntire(it.toString()) }?.groupValues
                ?.let { (_, releasesPath) -> Uri.parse(releasesPath) }
        }
    }

    suspend fun makeOpenAppIntent(
        info: ActivityAppInfo,
        intent: Intent,
        always: Boolean = false,
        privateBrowsingBrowser: KnownBrowser? = null,
        persist: Boolean = true,
    ): Intent {
        // (Hopefully) temporary workaround since Github Mobile doesn't support the releases/latest route (https://github.com/orgs/community/discussions/136120)
        GithubWorkaround.tryFixUri(info.componentName, intent.data)?.let { fixedUri ->
            intent.data = fixedUri
        }

        val viewIntent = Intent(Intent.ACTION_VIEW, intent.data)
            .addCategory(Intent.CATEGORY_BROWSABLE).let { privateBrowsingBrowser?.requestPrivateBrowsing(it) ?: it }

        val componentEnabled = context.packageManager.getComponentEnabledSetting(info.componentName)
        if (componentEnabled == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            return Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).apply {
                selector = viewIntent.addCategory(Intent.CATEGORY_BROWSABLE).setPackage(info.packageName)
            }
        }

        viewIntent.component = info.componentName

        // Check for intent.data != null to make sure we don't attempt to persist web search intents
        if (persist && privateBrowsingBrowser == null && intent.data != null) {
            persistSelectedIntent(viewIntent, always)
        }

        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return viewIntent
    }

    suspend fun makeOpenAppIntent(info: ActivityAppInfo, result: Intent, modifier: ClickModifier): Intent {
        return makeOpenAppIntent(
            info,
            result,
            modifier is ClickModifier.Always,
            (modifier as? ClickModifier.Private)?.browser,
            modifier !is ClickModifier.Private
        )
    }

    private fun ClickType.getPreference(modifier: ClickModifier): TapConfig {
        if (modifier is ClickModifier.Private || modifier is ClickModifier.Always) {
            return TapConfig.OpenApp
        }

        return when (this) {
            ClickType.Single -> tapConfigSingle
            ClickType.Double -> tapConfigDouble
            ClickType.Long -> tapConfigLong
        }.value
    }

    fun handleClickAsync(
        activity: Activity,
        index: Int,
        isExpanded: Boolean,
        requestExpand: () -> Unit,
        result: Intent,
        info: ActivityAppInfo,
        type: ClickType,
        modifier: ClickModifier,
    ): Deferred<Intent?> = ioAsync {
        handleClick(activity, index, isExpanded, requestExpand, result, info, type, modifier)
    }

    suspend fun handleClick(
        activity: Activity,
        index: Int,
        isExpanded: Boolean,
        requestExpand: () -> Unit,
        result: Intent,
        info: ActivityAppInfo,
        type: ClickType,
        modifier: ClickModifier,
    ): Intent? {
        val config = type.getPreference(modifier)

        when (config) {
            TapConfig.None -> {}
            TapConfig.OpenApp -> {
                return makeOpenAppIntent(info, result, modifier)
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
