package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import fe.kotlin.extension.iterable.mapCatching
import fe.kotlin.extension.iterable.onEachFailure
import fe.kotlin.extension.iterable.toSuccess
import fe.linksheet.LinkSheetApp
import fe.linksheet.activity.util.UiEvent
import fe.linksheet.activity.util.UiEventReceiver
import fe.linksheet.debug.DebugBroadcastReceiver
import fe.linksheet.debug.module.debug.MergedPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.state.AppStateRepository
import org.koin.core.component.get
import org.koin.core.component.inject


object UpdatePreferenceCommand : DebugCommand<UpdatePreferenceCommand>(
    DebugBroadcastReceiver.UPDATE_PREF_BROADCAST, UpdatePreferenceCommand::class
) {
    private val app by inject<LinkSheetApp>()
    private val repositories by lazy {
        MergedPreferenceRepository(
            logger = logger,
            appPreferenceRepository = get<AppPreferenceRepository>(),
            featureFlagRepository = get<FeatureFlagRepository>(),
            experimentRepository = get<ExperimentRepository>(),
            appStateRepository = get<AppStateRepository>()
        )
    }

    override fun handle(context: Context, intent: Intent) {
        val extras = requireNotNull(intent.extras) { "Extras must not be null" }
        val keys = requireNotNull(extras.keySet().takeIf { it.isNotEmpty() }) { "Extras must not be empty" }

        val successfulUpdates = keys.mapCatching { it to update(extras, it) }
            .onEachFailure { logger.error("Update failed", it) }
            .toSuccess()
            .toMap()

        for ((key, value) in successfulUpdates) {
            val msg = "Preference '$key' set to '$value'"
            logger.info(msg)

            val activity = app.currentActivity().value
            if (activity != null && activity is UiEventReceiver) {
                activity.send(UiEvent.ShowSnackbar(text = msg))
            } else {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun update(extras: Bundle, key: String): String {
        val value = requireNotNull(extras.getString(key)) { "String value for '$key' is null" }
        val repository = requireNotNull(repositories.getPreference(key)) { "No repository found for '$key'" }

        // TODO: Implement success in pref indicator in pref lib
        repository.set(key, value)
        return value
    }
}
