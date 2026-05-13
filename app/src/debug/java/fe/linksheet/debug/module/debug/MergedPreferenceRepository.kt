package fe.linksheet.debug.module.debug

import app.linksheet.api.preference.AppPreferenceRepository
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.StatePreferenceRepository
import fe.composekit.preference.FlowPreferenceRepository
import fe.linksheet.debug.module.preference.DebugPreferenceRepository
import fe.linksheet.debug.module.preference.DebugPreferences
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.flags.FeatureFlags
import fe.linksheet.module.preference.state.AppStatePreferences
import fe.linksheet.module.preference.state.DefaultAppStateRepository

data class Repository(val definition: PreferenceDefinition, val preferenceRepository: PreferenceRepository) {
    val allPreferences by lazy { definition.all.map { it.key } }

    fun set(key: String, value: String): Boolean {
        val pref = requireNotNull(definition.all[key]) { "'$key' is not defined in '$definition'" }
        if(!preferenceRepository.setStringValueToPreference(pref, value)) {
            return false
        }

        when (preferenceRepository) {
            is StatePreferenceRepository -> preferenceRepository.stateCache.get(key)?.forceRefresh()
            is FlowPreferenceRepository -> preferenceRepository.cache.get(key)?.reload()
        }
        return true
    }
}

class MergedPreferenceRepository(
    val logger: Logger,
    val appPreferenceRepository: AppPreferenceRepository,
    val featureFlagRepository: FeatureFlagRepository,
    val experimentRepository: ExperimentRepository,
    val appStateRepository: DefaultAppStateRepository,
    val debugRepository: DebugPreferenceRepository,
) {
    private val repositories = setOf(
        Repository(AppPreferences, appPreferenceRepository),
        Repository(FeatureFlags, featureFlagRepository),
        Repository(Experiments, experimentRepository),
        Repository(AppStatePreferences, appStateRepository),
        Repository(DebugPreferences, debugRepository)
    )

    private fun createPreferences(): MutableMap<String, Repository> {
        val merged = mutableMapOf<String, Repository>()

        for (repository in repositories) {
            for (key in repository.allPreferences) {
                val existing = merged[key]
                val putKey = if (existing != null) {
                    val renamed = "${repository.preferenceRepository::class.java.simpleName}_$key"
//                    logger.error("The key '$key' is present in multiple preference definitions, renaming to $renamed")

                    renamed
                } else key

                merged[putKey] = repository
            }
        }

        return merged
    }

    private val preferences by lazy { createPreferences() }

    fun getPreference(key: String): Repository? {
        return preferences[key]
    }
}
