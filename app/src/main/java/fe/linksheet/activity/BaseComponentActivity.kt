package fe.linksheet.activity

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import fe.linksheet.activity.util.DebugState
import fe.linksheet.activity.util.UiEvent
import fe.linksheet.activity.util.UiEventReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseComponentActivity : ComponentActivity() {
    var edgeToEdge: Boolean = false
        private set

    fun initPadding(): BaseComponentActivity {
        enableEdgeToEdge()
        edgeToEdge = true
        return this
    }

    fun setContent(edgeToEdge: Boolean, content: @Composable () -> Unit) {
        if (edgeToEdge) initPadding()
        return setContent(content = content)
    }
}

open class UiEventReceiverBaseComponentActivity : BaseComponentActivity(), UiEventReceiver {
    private val map: MutableMap<DebugState.Key<*>, DebugState> = mutableMapOf()
    private val _events = MutableStateFlow<UiEvent?>(null)
    val events = _events.asStateFlow()

    override fun onEvent(event: UiEvent) {
        _events.tryEmit(event)
    }

    override fun <T : DebugState> publishDebugState(state: T) {
        map[state.key] = state
    }

    override fun <T : DebugState> getStateOrNull(key: DebugState.Key<*>): T? {
        return map[key] as T?
    }
}
