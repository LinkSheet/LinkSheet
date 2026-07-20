package app.linksheet.feature.profile.service

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import app.linksheet.feature.app.core.MetaDataHandler
import app.linksheet.feature.app.core.setForwardProfileActivities
import fe.android.lifecycle.LifecycleAwareService
import fe.composekit.mozilla.components.support.base.log.logger.Logger
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileService(
    private val metaDataHandler: MetaDataHandler,
    private val sendTarget: StateFlow<Boolean>,
) : LifecycleAwareService {
    private val logger = Logger("ProfileService")

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            sendTarget.collect {
                logger.debug("Updating forward profile state $it")
                metaDataHandler.setForwardProfileActivities(it)
            }
        }
    }
}
