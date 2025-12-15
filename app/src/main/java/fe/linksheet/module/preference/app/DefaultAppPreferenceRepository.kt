package fe.linksheet.module.preference.app

import android.content.Context
import fe.android.preference.helper.Preference
import fe.linksheet.module.preference.permission.PermissionBoundPreference
import fe.linksheet.module.preference.permission.UsageStatsPermission
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mozilla.components.support.base.log.logger.Logger
import org.koin.core.component.KoinComponent

class DefaultAppPreferenceRepository(val context: Context) : AppPreferenceRepository(context), KoinComponent {
    private val logger = Logger("DefaultAppPreferenceRepository")

    private val preferencesRequiringPermission by lazy {
        mapOf(AppPreferences.bottomSheet.usageStatsSorting to UsageStatsPermission(context))
    }

    suspend fun init(dispatcher: CoroutineDispatcher = Dispatchers.IO) = withContext(dispatcher) {
        // Ensure backwards compatibility as this feature was previously included in non-pro versions
        if (!LinkSheetAppConfig.isPro()) {
            put(AppPreferences.followRedirects.externalService, false)
            put(AppPreferences.amp2Html.externalService, false)
        }

        AppPreferences.runMigrations(this@DefaultAppPreferenceRepository)
    }

    fun importPreferences(preferencesToImport: Map<String, String>): List<PermissionBoundPreference> {
        val preferences = AppPreferences.all

        val mappedPreferences = preferencesToImport.mapNotNull {
            val preference = preferences[it.key] ?: return@mapNotNull null
            preference to it.value
        }

        edit {
            mappedPreferences.forEach { (preference, newValue) ->
                runCatching {
                    setStringValueToPreference(preference, newValue)
                }.onFailure { logger.error("Failed to import preference '${preference.key}'", it) }
            }
        }

        AppPreferences.runMigrations(this)

        // Refresh must be delayed to until after the editor has been closed
        return mappedPreferences.mapNotNull { (preference) ->
            // Forces refresh by reading new value from the preference file; In the future, maybe this should be updating
            // newValue to the RepositoryState instance directly, but that would require converting the
            // string value to the appropriate state type

            cache.get(preference.key)?.reload()

            val requiredPermission = preferencesRequiringPermission[preference]
            if (requiredPermission?.check() == false) requiredPermission else null
        }
    }

    fun exportPreferences(exclude: Iterable<Preference<*, *>>): Map<String, String?> {
        val preferences = AppPreferences.all.toMutableMap()
        exclude.forEach {
            preferences.remove(it.key)
        }

        return preferences.values.associate { it.key to getAnyAsString(it) }
    }
}
