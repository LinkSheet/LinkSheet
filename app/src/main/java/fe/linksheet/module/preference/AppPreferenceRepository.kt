package fe.linksheet.module.preference

import android.content.Context
import fe.android.preference.helper.BasePreference
import fe.android.preference.helper.compose.ComposePreferenceRepository
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.module.preference.permission.PermissionBoundPreference
import fe.linksheet.module.preference.permission.UsageStatsPermission
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    singleOf(::AppPreferenceRepository)
}

class AppPreferenceRepository(val context: Context) : ComposePreferenceRepository(context) {

    private val followRedirectsExternalService = getBooleanState(AppPreferences.followRedirectsExternalService)
    private val amp2HtmlExternalService = getBooleanState(AppPreferences.amp2HtmlExternalService)

    private val preferencesRequiringPermission by lazy {
        mapOf(AppPreferences.usageStatsSorting to UsageStatsPermission(context))
    }

    init {
        // Ensure backwards compatibility as this feature was previously included in non-pro versions
        if (!LinkSheetAppConfig.isPro()) {
            followRedirectsExternalService(false)
            amp2HtmlExternalService(false)
        }
    }

    fun importPreferences(preferencesToImport: Map<String, String>): List<PermissionBoundPreference> {
        val preferences = AppPreferences.all

        val mappedPreferences = preferencesToImport.mapNotNull {
            val preference = preferences[it.key] ?: return@mapNotNull null
            preference to it.value
        }

        editor {
            mappedPreferences.forEach { (preference, newValue) ->
                setStringValueToPreference(preference, newValue, this)
            }
        }

        // Refresh must be delayed to until after the editor has been closed
        return mappedPreferences.mapNotNull { (preference) ->
            // Forces refresh by reading new value from the preference file; In the future, maybe this should be update
            // newValue to the RepositoryState instance directly, but that would required converting the
            // string value to the appropriate state type
            stateCache.get(preference.key)?.forceRefresh()

            val requiredPermission = preferencesRequiringPermission[preference]
            if (requiredPermission?.check() == false) requiredPermission else null
        }
    }

    fun exportPreferences(exclude: Iterable<BasePreference<*, *>>): Map<String, String?> {
        val preferences = AppPreferences.all.toMutableMap()
        exclude.forEach {
            preferences.remove(it.key)
        }

        return preferences.values.associate { it.key to getAnyAsString(it) }
    }
}
