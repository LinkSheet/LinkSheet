package fe.linksheet.experiment.improved.resolver

import androidx.compose.runtime.Stable

sealed interface ResolveEvent {
    @JvmInline
    @Stable
    value class Message(val message: String) : ResolveEvent

    companion object {
        val Initialized = Message("Initialized")
    }
}

sealed interface ResolverInteraction {
    data object None : ResolverInteraction
    data class Cancelable(val id: Int, val cancel: () -> Unit) : ResolverInteraction
}
