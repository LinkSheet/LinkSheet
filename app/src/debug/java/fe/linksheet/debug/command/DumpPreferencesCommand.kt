package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import app.linksheet.api.preference.AppPreferenceRepository
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.debug.DebugBroadcastReceiver
import fe.linksheet.debug.module.preference.DebugPreferenceRepository
import fe.linksheet.debug.module.preference.DebugPreferences
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.flags.FeatureFlags
import fe.linksheet.module.preference.state.AppStatePreferences
import fe.linksheet.module.preference.state.DefaultAppStateRepository
import org.koin.core.component.get

object DumpPreferencesCommand : DebugCommand<DumpPreferencesCommand>(
    DebugBroadcastReceiver.DUMP_PREFERENCES_BROADCAST, DumpPreferencesCommand::class
) {
    private val allPrefs = listOf(
        PreferenceHolder(
            title = "Preferences",
            preferences = AppPreferences.all,
            repository = get<AppPreferenceRepository>()
        ),
        PreferenceHolder(
            title = "Experiments",
            preferences = Experiments.all,
            repository = get<ExperimentRepository>()
        ),
        PreferenceHolder(
            title = "Feature flags",
            preferences = FeatureFlags.all,
            repository = get<FeatureFlagRepository>()
        ),
        PreferenceHolder(
            title = "App state",
            preferences = AppStatePreferences.all,
            repository = get<DefaultAppStateRepository>()
        ),
        PreferenceHolder(
            title = "Debug preferences",
            preferences = DebugPreferences.all,
            repository = get<DebugPreferenceRepository>()
        ),
    )

    override fun handle(context: Context, intent: Intent) {
        val names = intent.extras?.getString("names")?.split(",")
        val sort = intent.extras?.getString("sort")?.toBooleanStrictOrNull() == true

        for ((title, preferences, repo) in allPrefs) {
            val actualPreferences = applyOptions(preferences, names, sort)
            if(actualPreferences.isEmpty()) continue
            logger.info("Dumping $title:")

            for ((key, preference) in actualPreferences) {
                val value = repo.getAnyAsString(preference)
                logger.info("\t$key=$value (default=${preference.default})")
            }
        }
    }

    private fun applyOptions(
        preferences: Map<String, Preference<*, *>>,
        names: List<String>?,
        sort: Boolean
    ): Map<String, Preference<*, *>> {
        var mutPreferences: Map<String, Preference<*, *>> = if(names != null) preferences.filterKeys { it in names } else preferences
        mutPreferences = if (sort) mutPreferences.toSortedMap() else mutPreferences

        return mutPreferences
    }
}

data class PreferenceHolder(
    val title: String,
    val preferences: Map<String, Preference<*, *>>,
    val repository: PreferenceRepository
)
