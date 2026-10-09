package fe.linksheet.activity.util

interface UiEventReceiver {
    fun onEvent(event: UiEvent)

    fun <T : DebugState> publishDebugState(state: T)
    fun <T : DebugState> getStateOrNull(key: DebugState.Key<*>): T?
}

sealed interface UiEvent {
    class ShowSnackbar(val text: String) : UiEvent
    class NavigateTo(val route: Any) : UiEvent
}
