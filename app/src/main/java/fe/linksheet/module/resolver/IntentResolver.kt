package fe.linksheet.module.resolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import com.google.gson.JsonObject
import com.tasomaniac.openwith.preferred.PreferredResolver
import com.tasomaniac.openwith.resolver.BottomSheetResult
import fe.fastforwardkt.FastForwardLoader
import fe.fastforwardkt.isTracker
import fe.gson.extensions.string
import fe.httpkt.json.readToJson
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.data.entity.ResolvedRedirect
import fe.linksheet.extension.componentName
import fe.linksheet.extension.getUri
import fe.linksheet.extension.isSchemeTypicallySupportedByBrowsers
import fe.linksheet.extension.newIntent
import fe.linksheet.extension.queryResolveInfosByIntent
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.redirectresolver.RedirectResolver
import fe.linksheet.module.repository.LibRedirectDefaultRepository
import fe.linksheet.module.repository.LibRedirectStateRepository
import fe.linksheet.module.repository.ResolvedRedirectRepository
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetGrouper
import fe.linksheet.util.io
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class IntentResolver(
    val context: Context,
    val preferenceRepository: PreferenceRepository,
    private val libRedirectDefaultRepository: LibRedirectDefaultRepository,
    private val libRedirectStateRepository: LibRedirectStateRepository,
    private val resolvedRedirectRepository: ResolvedRedirectRepository,
    private val downloader: Downloader,
    private val redirectResolver: RedirectResolver,
    private val browserHandler: BrowserHandler,
    private val inAppBrowserHandler: InAppBrowserHandler,
) {
    private val fastForwardRulesObject by lazy { FastForwardLoader.loadBuiltInFastForwardRules() }
    private val libRedirectServices by lazy { LibRedirectLoader.loadBuiltInServices() }
    private val libRedirectInstances by lazy { LibRedirectLoader.loadBuiltInInstances() }

    private val useClearUrls = preferenceRepository.getBoolean(Preferences.useClearUrls)
    private var useFastForwardRules = preferenceRepository.getBoolean(
        Preferences.useFastForwardRules
    )

    private var enableLibRedirect = preferenceRepository.getBoolean(Preferences.enableLibRedirect)
    private val followRedirects = preferenceRepository.getBoolean(Preferences.followRedirects)
    private val followRedirectsLocalCache = preferenceRepository.getBoolean(
        Preferences.followRedirectsLocalCache
    )

    private val followRedirectsExternalService = preferenceRepository.getBoolean(
        Preferences.followRedirectsExternalService
    )

    private val followOnlyKnownTrackers = preferenceRepository.getBoolean(
        Preferences.followOnlyKnownTrackers
    )

    private var enableDownloader = preferenceRepository.getBoolean(Preferences.enableDownloader)
    private var downloaderCheckUrlMimeType = preferenceRepository.getBoolean(
        Preferences.downloaderCheckUrlMimeType
    )

    val theme = preferenceRepository.get(Preferences.theme)
    private val dontShowFilteredItem = preferenceRepository.getBoolean(
        Preferences.dontShowFilteredItem
    )

    private val inAppBrowserMode = preferenceRepository.get(Preferences.inAppBrowserMode)
    private val browserMode = preferenceRepository.get(Preferences.browserMode)
    private val selectedBrowser = preferenceRepository.getString(Preferences.selectedBrowser)


    suspend fun resolve(intent: Intent, referrer: Uri?): BottomSheetResult {
        Timber.tag("ResolveIntents").d("Intent: $intent")

        var uri = intent.getUri(useClearUrls, useFastForwardRules, fastForwardRulesObject)
        var followRedirect: FollowRedirect? = null

        if (followRedirects && uri != null) {
            followRedirects(
                uri,
                followRedirectsLocalCache,
                fastForwardRulesObject
            ).getOrNull()?.let {
                followRedirect = it
                uri = Uri.parse(it.resolvedUrl)
            }
        }

        if (enableLibRedirect) {
            val service = LibRedirect.findServiceForUrl(uri.toString(), libRedirectServices)
            Timber.tag("ResolveIntents").d("LibRedirect $service")
            if (service != null && libRedirectStateRepository.getServiceState(service.key)
                    .firstOrNull()?.enabled == true
            ) {
                val savedDefault =
                    libRedirectDefaultRepository.getByServiceKey(service.key).firstOrNull()
                val redirected = if (savedDefault != null) {
                    val instanceUrl =
                        if (savedDefault.instanceUrl == SettingsViewModel.libRedirectRandomInstanceKey) {
                            libRedirectInstances.find { it.frontendKey == savedDefault.frontendKey }?.hosts?.random()
                                ?: savedDefault.instanceUrl
                        } else savedDefault.instanceUrl

                    LibRedirect.redirect(
                        uri.toString(),
                        savedDefault.frontendKey,
                        instanceUrl
                    )
                } else {
                    val defaultInstance =
                        LibRedirect.getDefaultInstanceForFrontend(service.defaultFrontend.key)
                    LibRedirect.redirect(
                        uri.toString(),
                        service.defaultFrontend.key,
                        defaultInstance?.first()!!
                    )
                }

                Timber.tag("ResolveIntents").d("LibRedirect $redirected")
                if (redirected != null) {
                    uri = Uri.parse(redirected)
                }
            }
        }

        val downloadable = if (enableDownloader && uri != null) {
            checkIsDownloadable(uri!!)
        } else Downloader.DownloadCheckResult.NonDownloadable

        val preferredApp = uri?.let {
            PreferredResolver.resolve(context, it.host!!)
        }

        Timber.tag("ResolveIntents").d("PreferredApp: $preferredApp")

        val hostHistory = uri?.let {
            PreferredResolver.resolveHostHistory(it.host!!)
        } ?: emptyMap()


        Timber.tag("ResolveIntents").d("HostHistory: $hostHistory")
        Timber.tag("ResolveIntents").d("SourceIntent: $intent ${intent.extras}")

        val isCustomTab = intent.hasExtra(CustomTabsIntent.EXTRA_SESSION)
        val allowCustomTab = inAppBrowserHandler.shouldAllowCustomTab(referrer, inAppBrowserMode)

        val newIntent = intent.newIntent(uri, !isCustomTab || !allowCustomTab)
        if (allowCustomTab) {
            newIntent.extras?.keySet()?.filter { !it.contains("customtabs") }?.forEach { key ->
                Timber.tag("ResolveIntents").d("CustomTab: Remove extra: $key")
                newIntent.removeExtra(key)
            }
        }

        Timber.tag("ResolveIntents").d("NewIntent: $newIntent ${newIntent.extras}")

        val resolvedList = context.packageManager
            .queryResolveInfosByIntent(newIntent, true)
            .toMutableList()

        Timber.tag("ResolveIntents").d("ResolveListPreSort: $resolvedList")

        Timber.tag("ResolveIntents")
            .d("PreferredApp ComponentName: ${preferredApp?.app?.componentName}")

        val browserMode = if (newIntent.isSchemeTypicallySupportedByBrowsers()) {
            browserHandler.handleBrowsers(browserMode, selectedBrowser, resolvedList)
        } else null

        Timber.tag("ResolveIntents").d("BrowserMode: $browserMode")

        val (grouped, filteredItem, showExtended) = BottomSheetGrouper.group(
            context,
            resolvedList,
            hostHistory,
            preferredApp?.app,
            !dontShowFilteredItem
        )

        val selectedBrowserIsSingleOption =
            browserMode?.first == BrowserHandler.BrowserMode.SelectedBrowser
                    && resolvedList.singleOrNull()?.activityInfo?.componentName() == browserMode.second?.activityInfo?.componentName()

        val noBrowsersPresentOnlySingleApp =
            browserMode?.first == BrowserHandler.BrowserMode.None && resolvedList.size == 1


        Timber.tag("ResolveIntents").d(
            "Grouped: $grouped, filteredItem: $filteredItem, showExtended: $showExtended, selectedBrowserIsSingleOption: $selectedBrowserIsSingleOption"
        )

        return BottomSheetResult(
            uri,
            grouped,
            filteredItem,
            showExtended,
            preferredApp?.app?.alwaysPreferred,
            selectedBrowserIsSingleOption || noBrowsersPresentOnlySingleApp,
            followRedirect,
            downloadable
        )
    }

    enum class FollowRedirectResolveType(@StringRes val stringId: Int) {
        Cache(R.string.redirect_resolve_type_cache),
        Remote(R.string.redirect_resolve_type_remote),
        Local(R.string.redirect_resolve_type_local),
        NotResolved(R.string.redirect_resolve_type_not_resolved);

        fun isNotResolved() = this == NotResolved
    }

    data class FollowRedirect(
        val resolvedUrl: String,
        val resolveType: FollowRedirectResolveType
    )

    private suspend fun followRedirects(
        uri: Uri,
        localCache: Boolean,
        fastForwardRulesObject: JsonObject
    ): Result<FollowRedirect> {
        if (localCache) {
            val redirect = io {
                resolvedRedirectRepository.getForShortUrl(uri.toString()).firstOrNull()
            }

            if (redirect != null) {
                Timber.tag("FollowRedirect").d("From local cache: $redirect")
                return Result.success(
                    FollowRedirect(
                        redirect.resolvedUrl,
                        FollowRedirectResolveType.Cache
                    )
                )
            }
        }

        val followRedirect = followRedirectsImpl(uri, fastForwardRulesObject)

        if (localCache && followRedirect.getOrNull()?.resolveType != FollowRedirectResolveType.NotResolved) {
            io {
                resolvedRedirectRepository.insert(
                    ResolvedRedirect(uri.toString(), followRedirect.getOrNull()?.resolvedUrl!!)
                )
            }
        }

        return followRedirect
    }

    private fun followRedirectsImpl(
        uri: Uri,
        fastForwardRulesObject: JsonObject
    ): Result<FollowRedirect> {
        Timber.tag("FollowRedirects").d("Following redirects for $uri")

        val followUri = uri.toString()
        if (!followOnlyKnownTrackers || isTracker(followUri, fastForwardRulesObject)) {
            if (followRedirectsExternalService) {
                Timber.tag("FollowRedirects").d("Using external service for $followUri")

                val response = followRedirectsExternal(followUri)
                if (response.isSuccess) {
                    return Result.success(
                        FollowRedirect(
                            response.getOrNull()!!,
                            FollowRedirectResolveType.Remote
                        )
                    )
                }
            }

            Timber.tag("FollowRedirects").d("Using local service for $followUri")
            return Result.success(
                FollowRedirect(
                    followRedirectsLocal(followUri),
                    FollowRedirectResolveType.Local
                )
            )
        }

        return Result.success(FollowRedirect(followUri, FollowRedirectResolveType.NotResolved))
    }

    private fun followRedirectsLocal(uri: String): String {
        return redirectResolver.resolveLocal(uri).url.toString()
    }

    private fun followRedirectsExternal(uri: String): Result<String> {
        val con = redirectResolver.resolveRemote(uri)
        if (con.responseCode != 200) {
            return Result.failure(Exception("Something went wrong while resolving redirect"))
        }

        val obj = con.readToJson().asJsonObject
        Timber.tag("FollowRedirects").d("Returned json $obj")

        return obj.string("resolvedUrl")?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Something went wrong while reading response"))
    }

    private fun checkIsDownloadable(uri: Uri): Downloader.DownloadCheckResult {
        if (downloaderCheckUrlMimeType) {
            downloader.checkIsNonHtmlFileEnding(uri.toString()).let {
                Timber.tag("CheckIsDownloadable").d("File ending check result $it")
                if (it.isDownloadable()) return it
            }
        }

        return downloader.isNonHtmlContentUri(uri.toString())
    }


}