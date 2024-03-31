package fe.linksheet.activity

interface UiEventReceiver {
    fun receive(event: UiEvent)
    fun send(event: UiEvent) = receive(event)
}

sealed interface UiEvent {
    data class ShowSnackbar(val text: String) : UiEvent
}
