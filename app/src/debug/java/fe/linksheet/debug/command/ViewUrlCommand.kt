package fe.linksheet.debug.command

import android.content.Context
import android.content.Intent
import android.net.Uri
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.debug.DebugBroadcastReceiver
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.preference.flags.FeatureFlagRepository
import fe.linksheet.module.preference.flags.FeatureFlags
import org.koin.core.component.get

object ViewUrlCommand : DebugCommand<ViewUrlCommand>(
    DebugBroadcastReceiver.VIEW_URL_BROADCAST, ViewUrlCommand::class
) {
    override fun handle(context: Context, intent: Intent) {
        val extras = requireNotNull(intent.extras) { "Extras must not be null" }
        val url = requireNotNull(extras.getString("url")) { "Argument 'url' is missing" }

        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url), context, BottomSheetActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }
}
