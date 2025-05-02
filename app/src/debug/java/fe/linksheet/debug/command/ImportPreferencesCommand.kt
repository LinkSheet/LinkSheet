package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import fe.gson.extension.json.array.elementsFilterNull
import fe.gson.extension.json.`object`.asArrayOrNull
import fe.gson.extension.json.`object`.asStringOrNull
import fe.gson.util.Json
import fe.kotlin.extension.string.decodeBase64OrNull
import fe.linksheet.debug.DebugBroadcastReceiver
import fe.linksheet.debug.module.debug.MergedPreferenceRepository
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.state.AppStateRepository
import org.koin.core.component.get
import kotlin.io.encoding.ExperimentalEncodingApi

object ImportPreferencesCommand : DebugCommand<ImportPreferencesCommand>(
    DebugBroadcastReceiver.IMPORT_PREFERENCES_BROADCAST, ImportPreferencesCommand::class
) {
    private val repositories by lazy {
        MergedPreferenceRepository(
            logger = logger,
            appPreferenceRepository = get<AppPreferenceRepository>(),
            featureFlagRepository = get<FeatureFlagRepository>(),
            experimentRepository = get<ExperimentRepository>(),
            appStateRepository = get<AppStateRepository>()
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun handle(context: Context, intent: Intent) {
        val extras = requireNotNull(intent.extras) { "Extras must not be null" }
        val preferences = requireNotNull(extras.getString("preferences")) { "Argument 'preferences' is missing" }
        logger.info("Preferences $preferences")

        val decoded = preferences.decodeBase64OrNull() ?: return
        logger.info("Decoded $decoded")
        val json = Json.parseJsonOrNull<JsonElement>(decoded)
        LogPreferences(logger, repositories).import(json)
    }
}

class LogPreferences(
    private val logger: Logger,
    private val repositories: MergedPreferenceRepository
) {
    fun import(element: JsonElement?) {
        if (element !is JsonObject) return
        val preferences = element
            .asArrayOrNull("preferences")
            ?.elementsFilterNull<JsonObject>() ?: return

        for (preference in preferences) {
            val name = preference.asStringOrNull("name") ?: continue
            val value = preference.asStringOrNull("value") ?: continue

            logger.info("Setting '$name' to '$value'")
            repositories.getPreference(name)?.set(name, value)
        }
    }
}
