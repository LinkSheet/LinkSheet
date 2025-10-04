package fe.linksheet.activity.bottomsheet

import app.linksheet.feature.browser.Browser
import fe.linksheet.module.resolver.BrowserResolver


sealed interface ClickType {
    data object Single : ClickType
    data object Double : ClickType
    data object Long : ClickType
}

sealed interface ClickModifier {
    data class Private(val browser: Browser) : ClickModifier
    data object Always : ClickModifier
    data object None : ClickModifier
}
