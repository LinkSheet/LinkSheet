package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import fe.linksheet.debug.DebugBroadcastReceiver
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.flags.FeatureFlags
import org.koin.core.component.get

object DumpPreferencesCommand : DebugCommand<DumpPreferencesCommand>(
    DebugBroadcastReceiver.DUMP_PREFERENCES_BROADCAST, DumpPreferencesCommand::class
) {
    private val allPrefs = listOf(
        Triple("Preferences", AppPreferences.all, get<AppPreferenceRepository>()),
        Triple("Experiments", Experiments.all, get<ExperimentRepository>()),
        Triple("Feature flags", FeatureFlags.all, get<FeatureFlagRepository>())
    )

    override fun handle(context: Context, intent: Intent) {
        allPrefs.forEach { (pref, all, repo) ->
            logger.info("Dumping $pref:")
            all.forEach { (key, pref) ->
                val value = repo.getAnyAsString(pref)
                logger.info("\t$key=$value (default=${pref.default})")
            }
        }
    }
}
