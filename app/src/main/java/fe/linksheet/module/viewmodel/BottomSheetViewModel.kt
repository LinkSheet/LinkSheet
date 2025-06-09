package fe.linksheet.module.viewmodel


import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.mutableIntStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.activity.bottomsheet.*
import fe.linksheet.extension.android.getSystemServiceOrThrow
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
import fe.linksheet.module.resolver.util.IntentLauncher
import fe.linksheet.module.resolver.util.LaunchIntent
import fe.linksheet.module.resolver.util.LaunchMainIntent
import fe.linksheet.module.resolver.workaround.GithubWorkaround
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.intent.StandardIntents
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
    val imageLoader: ImageLoader,
    val intentLauncher: IntentLauncher,
    val state: SavedStateHandle,
) : BaseViewModel(preferenceRepository), KoinComponent {
    private val logger by injectLogger<BottomSheetViewModel>()
    val clipboardManager = context.getSystemServiceOrThrow<ClipboardManager>()
    val downloadManager = context.getSystemServiceOrThrow<DownloadManager>()

    val themeAmoled = preferenceRepository.asViewModelState(AppPreferences.themeAmoled)
    val hideAfterCopying = preferenceRepository.asViewModelState(AppPreferences.hideAfterCopying)
    val urlCopiedToast = preferenceRepository.asViewModelState(AppPreferences.urlCopiedToast)
    val downloadStartedToast = preferenceRepository.asViewModelState(AppPreferences.downloadStartedToast)
    val gridLayout = preferenceRepository.asViewModelState(AppPreferences.gridLayout)
    val resolveViaToast = preferenceRepository.asViewModelState(AppPreferences.resolveViaToast)
    val resolveViaFailedToast = preferenceRepository.asViewModelState(AppPreferences.resolveViaFailedToast)
    val previewUrl = preferenceRepository.asViewModelState(AppPreferences.previewUrl)
    val enableRequestPrivateBrowsingButton = preferenceRepository.asViewModelState(AppPreferences.enableRequestPrivateBrowsingButton)
    val showAsReferrer = preferenceRepository.asViewModelState(AppPreferences.showLinkSheetAsReferrer)
    val hideBottomSheetChoiceButtons = preferenceRepository.asViewModelState(AppPreferences.hideBottomSheetChoiceButtons)
    val enableIgnoreLibRedirectButton = preferenceRepository.asViewModelState(AppPreferences.enableIgnoreLibRedirectButton)
    val bottomSheetProfileSwitcher = preferenceRepository.asViewModelState(AppPreferences.bottomSheetProfileSwitcher)
    val tapConfigSingle = preferenceRepository.asViewModelState(AppPreferences.tapConfigSingle)
    val tapConfigDouble = preferenceRepository.asViewModelState(AppPreferences.tapConfigDouble)
    val tapConfigLong = preferenceRepository.asViewModelState(AppPreferences.tapConfigLong)
    val expandOnAppSelect = preferenceRepository.asViewModelState(AppPreferences.expandOnAppSelect)
    val bottomSheetNativeLabel = preferenceRepository.asViewModelState(AppPreferences.bottomSheetNativeLabel)
    val improvedBottomSheetExpandFully = experimentRepository.asViewModelState(Experiments.improvedBottomSheetExpandFully)
    val improvedBottomSheetUrlDoubleTap = experimentRepository.asViewModelState(Experiments.improvedBottomSheetUrlDoubleTap)
    val interceptAccidentalTaps = experimentRepository.asViewModelState(Experiments.interceptAccidentalTaps)
    val manualFollowRedirects = experimentRepository.asViewModelState(Experiments.manualFollowRedirects)
    val noBottomSheetStateSave = experimentRepository.asViewModelState(Experiments.noBottomSheetStateSave)
    val expressiveLoadingSheet = experimentRepository.asViewModelState(Experiments.expressiveLoadingSheet)
    val appListSelectedIdx = mutableIntStateOf(-1)
    val events = intentResolver.events
    val interactions = intentResolver.interactions

    private val _resolveResultFlow = MutableStateFlow<IntentResolveResult>(IntentResolveResult.Pending)
    val resolveResultFlow = _resolveResultFlow.asStateFlow()

    fun warmupAsync() = viewModelScope.launch {
        intentResolver.warmup()
    }

    fun resolveAsync(intent: SafeIntent, uri: Uri?) = viewModelScope.launch(Dispatchers.IO) {
        val resolveResult = intentResolver.resolve(intent, uri)
        _resolveResultFlow.emit(resolveResult)
    }

    fun startPackageInfoActivity(context: Activity, info: ActivityAppInfo): Boolean {
        val intent = StandardIntents.createAppSettingsIntent(info.packageName)
        return context.startActivityWithConfirmation(intent)
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

    fun startDownload(resources: Resources, uri: String, downloadable: DownloadCheckResult.Downloadable) {
        val path = "${resources.getString(R.string.app_name)}${File.separator}${downloadable.toFileName()}"

        val request = DownloadManager.Request(uri.toUri())
            .setTitle(resources.getString(R.string.linksheet_download))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                path
            )

        downloadManager.enqueue(request)
    }

    suspend fun makeOpenAppIntent(
        info: ActivityAppInfo,
        intent: Intent,
        referrer: Uri?,
        always: Boolean,
        privateBrowsingBrowser: KnownBrowser?,
        persist: Boolean,
    ): LaunchIntent {
        // (Hopefully) temporary workaround since Github Mobile doesn't support the releases/latest route (https://github.com/orgs/community/discussions/136120)
        GithubWorkaround.tryFixUri(info.componentName, intent.data)?.let { fixedUri ->
            intent.data = fixedUri
        }

        val launchIntent = intentLauncher.launch(info, intent, referrer, privateBrowsingBrowser)
        if (launchIntent is LaunchMainIntent) {
            return launchIntent
        }

        // Check for intent.data != null to make sure we don't attempt to persist web search intents
        if (persist && privateBrowsingBrowser == null && intent.data != null) {
            persistSelectedIntent(launchIntent.intent, always)
        }

        return launchIntent
    }

    fun ClickType.pickPreference(
        modifier: ClickModifier,
    ): TapConfig {
        if (modifier is ClickModifier.Private || modifier is ClickModifier.Always) {
            return TapConfig.OpenApp
        }

        return when (this) {
            ClickType.Single -> tapConfigSingle
            ClickType.Double -> tapConfigDouble
            ClickType.Long -> tapConfigLong
        }.value
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
    ): LaunchIntent? {
        val config = type.pickPreference(modifier)
        if (config is TapConfig.OpenApp) {
            return makeOpenAppIntent(
                info = info,
                intent = result,
                referrer = activity.referrer,
                always = modifier is ClickModifier.Always,
                privateBrowsingBrowser = (modifier as? ClickModifier.Private)?.browser,
                persist = modifier !is ClickModifier.Private
            )
        }

        if (config is TapConfig.OpenSettings) {
            startPackageInfoActivity(activity, info)
            return null
        }

        if (config is TapConfig.SelectItem) {
            appListSelectedIdx.intValue = if (appListSelectedIdx.intValue != index) index else -1
            if (appListSelectedIdx.intValue != -1 && !isExpanded && expandOnAppSelect.value) {
                requestExpand()
            }

            return null
        }

        return null
    }

    suspend fun handle(
        activity: BottomSheetActivity,
        result: IntentResolveResult.Default,
        interaction: Interaction,
    ): LaunchIntent? {
        if (interaction is AppClickInteraction) {
            return handleClick(
                activity = activity,
                index = interaction.index,
                isExpanded = false,
//                    isExpanded = sheetState.isExpanded(),
                requestExpand = { },
                result = result.intent,
                info = interaction.info,
                type = interaction.type,
                modifier = interaction.modifier
            )
        }

        if (interaction is ChoiceButtonInteraction || interaction is PreferredAppChoiceButtonInteraction) {
            val modifier = interaction.modifier
            return makeOpenAppIntent(
                info = interaction.info,
                intent = result.intent,
                referrer = activity.referrer,
                always = modifier is ClickModifier.Always,
                privateBrowsingBrowser = (modifier as? ClickModifier.Private)?.browser,
                persist = modifier !is ClickModifier.Private
            )
        }

        return null
    }
}
