package fe.linksheet.module.resolver

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow
import mozilla.components.support.utils.SafeIntent

interface IntentResolver {
    val events: StateFlow<ResolveEvent>
    val interactions: StateFlow<ResolverInteraction>

    suspend fun resolve(intent: SafeIntent, referrer: Uri?): IntentResolveResult
}
