package fe.linksheet.activity.util

interface UiEventReceiver {
    fun receive(event: UiEvent)
    fun send(event: UiEvent) = receive(event)
}

sealed interface UiEvent {
    class ShowSnackbar(val text: String) : UiEvent
    class NavigateTo(val route: String) : UiEvent
}
