package fe.linksheet.activity.bottomsheet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
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
import com.tasomaniac.openwith.resolver.IntentResolverResult
import com.tasomaniac.openwith.resolver.ResolveIntents
import fe.fastforwardkt.isTracker
import fe.gson.extensions.string
import fe.httpkt.Request
import fe.httpkt.json.readToJson
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.data.entity.AppSelectionHistory
import fe.linksheet.data.entity.ResolvedRedirect
import fe.linksheet.data.entity.WhitelistedBrowser
import fe.linksheet.extension.startActivityWithConfirmation
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.redirectresolver.RedirectResolver
import fe.linksheet.ui.theme.Theme
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class BottomSheetViewModel : ViewModel(),
    KoinComponent {
    private val database by inject<LinkSheetDatabase>()
    private val preferenceRepository by inject<PreferenceRepository>()
    private val redirectResolver by inject<RedirectResolver>()

    var result by mutableStateOf<IntentResolverResult?>(null)
    val enableCopyButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableCopyButton) ?: false
    )

    val hideAfterCopying by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.hideAfterCopying) ?: false
    )

    val singleTap by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.singleTap) ?: false
    )

    val enableSendButton by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.enableSendButton) ?: false
    )

    val alwaysShowPackageName by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.alwaysShowPackageName) ?: false
    )

    val disableToasts by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.disableToasts) ?: false
    )

    val gridLayout by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.gridLayout) ?: false
    )

    val useClearUrls by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.useClearUrls) ?: false
    )

    var useFastForwardRules by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.useFastForwardRules) ?: false
    )

    val followRedirects by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.followRedirects) ?: false
    )

    val followRedirectsLocalCache by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.followRedirectsLocalCache) ?: false
    )

    val followRedirectsExternalService by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.followRedirectsExternalService)
            ?: false
    )

    val followOnlyKnownTrackers by mutableStateOf(
        preferenceRepository.getBoolean(PreferenceRepository.followOnlyKnownTrackers) ?: false
    )

    val theme by mutableStateOf(
        preferenceRepository.getInt(
            PreferenceRepository.theme,
            Theme.persister,
            Theme.reader
        ) ?: Theme.System
    )


    fun resolveAsync(context: Context, intent: Intent): Deferred<IntentResolverResult?> {
        return viewModelScope.async(Dispatchers.IO) {
            result = ResolveIntents.resolve(context, intent, this@BottomSheetViewModel)

            result
        }
    }

    fun startMainActivity(context: Context): Boolean {
        return context.startActivityWithConfirmation(Intent(context, MainActivity::class.java))
    }

    fun startPackageInfoActivity(context: Context, info: DisplayActivityInfo): Boolean {
        return context.startActivityWithConfirmation(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            this.data = Uri.parse("package:${info.packageName}")
        })
    }

    suspend fun persistSelectedIntentAsync(intent: Intent, always: Boolean) {
        Log.d("PersistingSelectedIntent", "Component: ${intent.component}")
        return withContext(Dispatchers.IO) {
            intent.component?.let { component ->
                val host = intent.data!!.host!!.lowercase(Locale.getDefault())
                val app = PreferredApp(
                    host = host,
                    packageName = component.packageName,
                    component = component.flattenToString(),
                    alwaysPreferred = always
                )

                Log.d("PersistingSelectedIntent", "Inserting $app")
                database.preferredAppDao().insert(app)

                val historyEntry = AppSelectionHistory(
                    host = host,
                    packageName = component.packageName,
                    lastUsed = System.currentTimeMillis()
                )

                database.appSelectionHistoryDao().insert(historyEntry)
                Log.d("PersistingSelectedIntent", "Inserting $historyEntry")
            }
        }
    }

    suspend fun getWhiteListedBrowsers(): List<WhitelistedBrowser> {
        return withContext(Dispatchers.IO) {
            database.whitelistedBrowsersDao().getWhitelistedBrowsers()
        }
    }

    enum class FollowRedirectResolveType(@StringRes val stringId: Int) {
        Cache(R.string.redirect_resolve_type_cache),
        Remote(R.string.redirect_resolve_type_remote),
        Local(R.string.redirect_resolve_type_local),
        NotResolved(R.string.redirect_resolve_type_not_resolved);
    }

    data class FollowRedirect(
        val resolvedUrl: String,
        val resolveType: FollowRedirectResolveType
    )

    suspend fun followRedirects(
        uri: Uri,
        request: Request,
        localCache: Boolean,
        fastForwardRulesObject: JsonObject
    ): Result<FollowRedirect> {
        if (localCache) {
            val redirect = withContext(Dispatchers.IO) {
                database.resolvedRedirectDao().getResolvedRedirectForShortUrl(uri.toString())
            }

            if (redirect != null) {
                Log.d("FollowRedirect", "From local cache: $redirect")
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
            withContext(Dispatchers.IO) {
                database.resolvedRedirectDao().insert(
                    ResolvedRedirect(
                        uri.toString(),
                        followRedirect.getOrNull()?.resolvedUrl!!
                    )
                )
            }
        }

        return followRedirect
    }

    private fun followRedirectsImpl(
        uri: Uri,
        fastForwardRulesObject: JsonObject
    ): Result<FollowRedirect> {
        Log.d("FollowRedirects", "Following redirects for $uri")

        val followUri = uri.toString()
        if (!followOnlyKnownTrackers || isTracker(followUri, fastForwardRulesObject)) {
            if (followRedirectsExternalService) {
                Log.d("FollowRedirects", "Using external service for $followUri")

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

            Log.d("FollowRedirects", "Using local service for $followUri")
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
        Log.d("FollowRedirects", "Returned json $obj")

        return obj.string("resolvedUrl")?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Something went wrong while reading response"))
    }
}