package fe.linksheet.activity

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import fe.linksheet.activity.util.UiEvent
import fe.linksheet.activity.util.UiEventReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BaseComponentActivity : AppCompatActivity() {
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
    private val _events = MutableStateFlow<UiEvent?>(null)
    val events = _events.asStateFlow()

    override fun receive(event: UiEvent) {
        _events.tryEmit(event)
    }
}
