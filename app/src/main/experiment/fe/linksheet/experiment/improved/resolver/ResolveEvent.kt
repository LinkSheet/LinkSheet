package fe.linksheet.experiment.improved.resolver

import androidx.compose.runtime.Stable

sealed interface ResolveEvent {
    @JvmInline
    @Stable
    value class Message(val message: String) : ResolveEvent

    companion object{
        val Initialized = Message("Initialized")
    }
}
