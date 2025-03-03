package fe.linksheet.activity

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import fe.linksheet.activity.util.UiEvent
import fe.linksheet.activity.util.UiEventReceiver

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
    protected var eventState = mutableStateOf<UiEvent?>(null)
        private set

    override fun receive(event: UiEvent) {
        eventState.value = event
    }
}
