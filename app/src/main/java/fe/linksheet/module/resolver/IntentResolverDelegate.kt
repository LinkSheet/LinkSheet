package fe.linksheet.module.resolver

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow
import mozilla.components.support.utils.SafeIntent

class IntentResolverDelegate(
    private val improvedIntentResolver: IntentResolver,
    private val linkEngineIntentResolver: IntentResolver,
    private val useLinkEngine: () -> Boolean = { false },
) : IntentResolver {
    private val delegate: IntentResolver
        get() = when {
            useLinkEngine() -> linkEngineIntentResolver
            else -> improvedIntentResolver
        }

    override val events: StateFlow<ResolveEvent>
        get() = delegate.events

    override val interactions: StateFlow<ResolverInteraction>
        get() = delegate.interactions

    override suspend fun resolve(
        intent: SafeIntent,
        referrer: Uri?
    ): IntentResolveResult {
        return delegate.resolve(intent, referrer)
    }

    override suspend fun warmup() {
        delegate.warmup()
    }
}
