package fe.linksheet.activity.bottomsheet

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.tasomaniac.openwith.data.LinkSheetDatabase
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.BottomSheetResult
import com.tasomaniac.openwith.resolver.ResolveIntents
import fe.fastforwardkt.isTracker
import fe.gson.extensions.string
import fe.httpkt.json.readToJson
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.data.entity.AppSelectionHistory
import fe.linksheet.data.entity.DisableInAppBrowserInSelected
import fe.linksheet.data.entity.ResolvedRedirect
import fe.linksheet.data.entity.WhitelistedBrowser
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.redirectresolver.RedirectResolver
import fe.linksheet.util.io
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.File
import java.util.Locale

class BottomSheetViewModel : ViewModel(), KoinComponent {

    private val database by inject<LinkSheetDatabase>()
    private val preferenceRepository by inject<PreferenceRepository>()
    private val redirectResolver by inject<RedirectResolver>()
    private val downloader by inject<Downloader>()

    var resolveResult by mutableStateOf<BottomSheetResult?>(null)
    val enableCopyButton = preferenceRepository.getBoolean(Preferences.enableCopyButton)
    val hideAfterCopying = preferenceRepository.getBoolean(Preferences.hideAfterCopying)
    val singleTap = preferenceRepository.getBoolean(Preferences.singleTap)
    val enableSendButton = preferenceRepository.getBoolean(Preferences.enableSendButton)
    val alwaysShowPackageName = preferenceRepository.getBoolean(Preferences.alwaysShowPackageName)
    val disableToasts = preferenceRepository.getBoolean(Preferences.disableToasts)
    val gridLayout = preferenceRepository.getBoolean(Preferences.gridLayout)
    val useClearUrls = preferenceRepository.getBoolean(Preferences.useClearUrls)
    var useFastForwardRules = preferenceRepository.getBoolean(Preferences.useFastForwardRules)
    var enableLibRedirect = preferenceRepository.getBoolean(Preferences.enableLibRedirect)
    val followRedirects = preferenceRepository.getBoolean(Preferences.followRedirects)
    val followRedirectsLocalCache = preferenceRepository.getBoolean(Preferences.followRedirectsLocalCache)
    private val followRedirectsExternalService = preferenceRepository.getBoolean(Preferences.followRedirectsExternalService)
    private val followOnlyKnownTrackers = preferenceRepository.getBoolean(Preferences.followOnlyKnownTrackers)
    var enableDownloader = preferenceRepository.getBoolean(Preferences.enableDownloader)
    private var downloaderCheckUrlMimeType = preferenceRepository.getBoolean(Preferences.downloaderCheckUrlMimeType)
    val theme = preferenceRepository.get(Preferences.theme)
    val dontShowFilteredItem = preferenceRepository.getBoolean(Preferences.dontShowFilteredItem)
    val useTextShareCopyButtons = preferenceRepository.getBoolean(Preferences.useTextShareCopyButtons)
    val previewUrl = preferenceRepository.getBoolean(Preferences.previewUrl)

    fun resolveAsync(
        context: Context,
        intent: Intent,
        referrer: Uri?
    ) = viewModelScope.async(Dispatchers.IO) {
        ResolveIntents.resolve(context, intent, referrer, this@BottomSheetViewModel).apply {
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

    suspend fun persistSelectedIntentAsync(intent: Intent, always: Boolean) {
        Timber.tag("PersistingSelectedIntent").d("Component: ${intent.component}")
        return io {
            intent.component?.let { component ->
                val host = intent.data!!.host!!.lowercase(Locale.getDefault())
                val app = PreferredApp(
                    host = host,
                    packageName = component.packageName,
                    component = component.flattenToString(),
                    alwaysPreferred = always
                )

                Timber.tag("PersistingSelectedIntent").d("Inserting $app")
                database.preferredAppDao().insert(app)

                val historyEntry = AppSelectionHistory(
                    host = host,
                    packageName = component.packageName,
                    lastUsed = System.currentTimeMillis()
                )

                database.appSelectionHistoryDao().insert(historyEntry)
                Timber.tag("PersistingSelectedIntent").d("Inserting $historyEntry")
            }
        }
    }

    suspend fun getWhiteListedBrowsers(): List<WhitelistedBrowser> {
        return withContext(Dispatchers.IO) {
            database.whitelistedBrowsersDao().getAll().first()
        }
    }

    suspend fun getDisableInAppBrowserInSelected(): List<DisableInAppBrowserInSelected> {
        return withContext(Dispatchers.IO) {
            database.disableInAppBrowserInSelectedDao().getAll().first()
        }
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

    suspend fun followRedirects(
        uri: Uri,
        localCache: Boolean,
        fastForwardRulesObject: JsonObject
    ): Result<FollowRedirect> {
        if (localCache) {
            val redirect = io {
                database.resolvedRedirectDao().getForShortUrl(uri.toString())
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
                database.resolvedRedirectDao().insert(ResolvedRedirect(
                    uri.toString(),
                    followRedirect.getOrNull()?.resolvedUrl!!
                ))
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

    fun checkIsDownloadable(uri: Uri): Downloader.DownloadCheckResult {
        if (downloaderCheckUrlMimeType) {
            downloader.checkIsNonHtmlFileEnding(uri.toString()).let {
                Timber.tag("CheckIsDownloadable").d("File ending check result $it")
                if (it.isDownloadable()) return it
            }
        }

        return downloader.isNonHtmlContentUri(uri.toString())
    }

    suspend fun getLibRedirectDefault(serviceKey: String)= io {
        database.libRedirectDefaultDao().getByServiceKey(serviceKey)
    }

    suspend fun loadLibRedirectState(serviceKey: String) = io {
        database.libRedirectServiceStateDao().getServiceState(serviceKey)?.enabled
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