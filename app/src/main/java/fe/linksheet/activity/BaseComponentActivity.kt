package fe.linksheet.activity

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

open class BaseComponentActivity : ComponentActivity() {
    fun initPadding(): BaseComponentActivity {
        enableEdgeToEdge()
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
