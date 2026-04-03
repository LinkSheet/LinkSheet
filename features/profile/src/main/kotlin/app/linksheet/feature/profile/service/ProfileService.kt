package app.linksheet.feature.profile.service

import androidx.lifecycle.LifecycleOwner
import app.linksheet.feature.app.core.MetaDataHandler
import app.linksheet.feature.app.core.setForwardProfileActivities
import fe.android.lifecycle.LifecycleAwareService

class ProfileService(
    private val metaDataHandler: MetaDataHandler,
    private val sendTarget: () -> Boolean,
) : LifecycleAwareService {
    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        metaDataHandler.setForwardProfileActivities(sendTarget())
    }
}
