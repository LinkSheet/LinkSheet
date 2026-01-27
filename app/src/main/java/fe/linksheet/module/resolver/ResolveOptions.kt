package fe.linksheet.module.resolver

import android.net.Uri
import android.os.Bundle
import app.linksheet.feature.app.core.MetaDataHandler

data class ResolveOptions(
    val referrer: Uri?,
    val targetMetaData: Bundle?
) {
    val forwardProfile by lazy {
        targetMetaData?.getBoolean(MetaDataHandler.METADATA_KEY_FORWARD_PROFILE) == true
    }
}
