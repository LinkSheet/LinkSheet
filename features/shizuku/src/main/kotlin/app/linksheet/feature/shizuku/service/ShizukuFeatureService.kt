package app.linksheet.feature.shizuku.service

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import app.linksheet.api.eventbus.BroadcastEventBus
import app.linksheet.api.eventbus.IntentEventHandler
import app.linksheet.feature.shizuku.ShizukuDomainVerification
import fe.android.lifecycle.LifecycleAwareService
import fe.composekit.core.AndroidVersion
import fe.composekit.log.createLogger
import fe.linksheet.util.IntentFilters
import fe.std.result.tryCatch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ShizukuFeatureService(
    private val eventBus: BroadcastEventBus,
    private val enabled: StateFlow<Boolean>,
    private val autoDisableLinkHandlers: StateFlow<Boolean>
) : LifecycleAwareService {

    val logger = createLogger<ShizukuFeatureService>()

    val handler = object : IntentEventHandler {
        override val filter = IntentFilters.packageState

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != Intent.ACTION_PACKAGE_ADDED) return
            val packageName = intent.data!!.schemeSpecificPart

            if (!AndroidVersion.isAtLeastApi31S()) return
            logger.debug("Disabling link handling for $packageName")
            // TODO: Handle dead binder etc.
            tryCatch { ShizukuDomainVerification.setDomainVerificationLinkHandlingAllowed(packageName, false) }

//                Toast.makeText(
//                    context,
//                    "Disabled link handling for $packageName",
//                    Toast.LENGTH_SHORT
//                ).show()
        }
    }

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        if (!AndroidVersion.isAtLeastApi31S()) return
        owner.lifecycleScope.launch {
            enabled.combine(autoDisableLinkHandlers) { enabled, autoDisable -> enabled to autoDisable }
                .collect { (enabled, autoDisable) ->
                    if (enabled && autoDisable) {
                        eventBus.register(handler)
                    } else {
                        eventBus.unregister(handler)
                    }
                }
        }
    }
}
