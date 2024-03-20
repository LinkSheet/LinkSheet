package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.StatePreferenceRepository
import fe.kotlin.extension.iterable.mapCatching
import fe.kotlin.extension.iterable.onEachFailure
import fe.kotlin.extension.iterable.toSuccess
import fe.linksheet.LinkSheetApp
import fe.linksheet.debug.DebugBroadcastReceiver
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.flags.FeatureFlags
import org.koin.core.component.get


object UpdatePreferenceCommand : DebugCommand<UpdatePreferenceCommand>(
    DebugBroadcastReceiver.UPDATE_PREF_BROADCAST, UpdatePreferenceCommand::class
) {

    override fun handle(context: Context, intent: Intent) {
        val extras = requireNotNull(intent.extras) { "Extras must not be null" }
        val keys = requireNotNull(extras.keySet().takeIf { it.isNotEmpty() }) { "Extras must not be empty" }

        val successfulUpdates = keys.mapCatching { it to update(extras, it) }.onEachFailure { logger.error(it) }.toSuccess().toMap()

        for ((key, value) in successfulUpdates) {
            val msg = "Preference '$key' set to '$value'"
            logger.info(msg)

            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun update(extras: Bundle, key: String): String {
        val value = requireNotNull(extras.getString(key)) { "String value for '$key' is null" }
        val repository = requireNotNull(repositories[key]) { "No repository found for '$key'" }

        // TODO: Implement success in pref indicator in pref lib
        repository.set(key, value)
        return value
    }

    data class Repository(val definition: PreferenceDefinition, val preferenceRepository: PreferenceRepository) {
        val allPreferences by lazy { definition.all.map { it.key } }

        fun set(key: String, value: String) {
            val pref = requireNotNull(definition.all[key]) { "'$key' is not defined in '$definition'" }
            preferenceRepository.setStringValueToPreference(pref, value)

            if (preferenceRepository is StatePreferenceRepository) {
                preferenceRepository.stateCache.get(key)?.forceRefresh()
            }
        }
    }

    private val repositories by lazy {
        val repositories = setOf(
            Repository(AppPreferences, get<AppPreferenceRepository>()),
            Repository(FeatureFlags, get<FeatureFlagRepository>()),
            Repository(Experiments, get<ExperimentRepository>())
        )

        val merged = mutableMapOf<String, Repository>()

        for (repository in repositories) {
            for (key in repository.allPreferences) {
                val existing = merged[key]
                val putKey = if (existing != null) {
                    val renamed = "${repository.preferenceRepository::class.java.simpleName}_$key"
                    logger.error("The key '$key' is present in multiple preference definitions, renaming to $renamed")

                    renamed
                } else key

                merged[putKey] = repository
            }
        }

        merged
    }
}
