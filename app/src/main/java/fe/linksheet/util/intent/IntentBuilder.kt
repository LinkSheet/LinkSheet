package fe.linksheet.util.intent

import android.content.ComponentName
import android.content.Intent
import android.net.Uri

inline fun buildIntent(
    action: String? = null,
    uri: Uri? = null,
    componentName: ComponentName? = null,
    block: Intent.() -> Unit,
): Intent {
    val intent = Intent(action, uri)
    if (componentName != null) {
        intent.component = componentName
    }

    block(intent)
    return intent
}

