package fe.linksheet.module.preference

import android.content.Context
import fe.android.preference.helper.BasePreference
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getBooleanState
import fe.android.preference.helper.compose.getGlobalCachedState
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.module.preference.permission.PermissionBoundPreference
import fe.linksheet.module.preference.permission.UsageStatsPermission
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    singleOf(::AppPreferenceRepository)
}

class AppPreferenceRepository(val context: Context) : PreferenceRepository(context) {

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
            getGlobalCachedState(preference.key)?.forceRefresh()

            val requiredPermission = preferencesRequiringPermission[preference]
            if (requiredPermission?.check() == false) requiredPermission else null
        }
    }

    fun dumpPreferences(excludePreferences: List<BasePreference<*, *>>): Map<String, String?> {
        return AppPreferences.all.toMutableMap().apply {
            excludePreferences.forEach { remove(it.key) }
        }.map { (_, pkg) -> pkg }.associate { preference ->
            preference.key to getAnyAsString(preference)
        }
    }
}
